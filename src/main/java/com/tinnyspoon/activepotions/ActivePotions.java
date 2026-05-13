package com.tinnyspoon.activepotions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.tinnyspoon.activepotions.commands.GivePotionCmd;
import com.tinnyspoon.activepotions.commands.ListPotionsCmd;
import com.tinnyspoon.activepotions.ingredients.Ingredient;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class ActivePotions extends JavaPlugin {
    public static final String keyNamespace = "activepotions";

    public static List<InternalPotion> potions;
    public static boolean advancedLore;

    @Override
    public void onEnable() {
        // List<InternalPotion> potions = new ArrayList<>();

        this.saveDefaultConfig();


        ActivePotions.advancedLore = this.getConfig().getBoolean("advanced-lore", false);

        ActivePotions.potions = this.getConfig().getConfigurationSection("potions").getKeys(false)
            .stream()
            .map(potionName -> InternalPotion.fromSection(potionName, this.getConfig().getConfigurationSection("potions." + potionName)))
            .filter(potion -> potion != null)
            .toList();



        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("givepotion", new GivePotionCmd());
            commands.registrar().register("listpotions", new ListPotionsCmd());
        });


        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
        // this.registerCommand("givepotion", new GivePotionCmd());
    }
}

package com.tinnyspoon.activepotions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.activepotions.ingredients.Ingredient;

public class ActivePotions extends JavaPlugin {
    public static final String keyNamespace = "activepotions";

    @Override
    public void onEnable() {
        // List<InternalPotion> potions = new ArrayList<>();

        this.saveDefaultConfig();

        List<InternalPotion> potions = this.getConfig().getKeys(false)
            .stream()
            .map(potionName -> InternalPotion.fromSection(potionName, this.getConfig().getConfigurationSection(potionName)))
            .filter(potion -> potion != null)
            .toList();
    }


    // Looks much cleaner :)
    private void registerCommand(@NotNull String name, @NotNull CommandExecutor exexutor) {
        PluginCommand cmd = this.getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(exexutor);
        } else {
            this.getLogger().severe("Failed to register command [" + name + "]");
        }
    }
}

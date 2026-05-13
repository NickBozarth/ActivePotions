package com.tinnyspoon.activepotions.commands;

import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.activepotions.ActivePotions;
import com.tinnyspoon.activepotions.InternalPotion;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class GivePotionCmd implements BasicCommand {

    @Override
    public void execute(CommandSourceStack arg0, String[] args) {
        CommandSender sender = arg0.getSender();

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players");
            return;
        }
    
        String potionName = String.join(" ", args);
        Optional<InternalPotion> pot = ActivePotions.potions.stream()
            .filter(potion -> potion.name().equalsIgnoreCase(potionName))
            .findFirst();
    
        if (pot.isPresent()) {
            player.getInventory().addItem(pot.get().asItem());
            player.sendMessage("You got a " + pot.get().name());
        } else {
            player.sendMessage("No potion found for [" + potionName + "]");
        }
    
    
        return;
    }

    // @Override
    // public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    // }
}

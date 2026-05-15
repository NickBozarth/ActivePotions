package com.tinnyspoon.activepotions.commands;

import org.bukkit.entity.Player;

import com.tinnyspoon.activepotions.gui.PotionBrewingGui;
import com.tinnyspoon.activepotions.gui.PotionBrewingGui.BrewingLevel;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class BrewCmd implements BasicCommand {
    @Override
    public void execute(CommandSourceStack sourceStack, String[] args) {
        if (!(sourceStack.getSender() instanceof Player player)) return;
        PotionBrewingGui.openInv(player, BrewingLevel.THREE);
    }
}

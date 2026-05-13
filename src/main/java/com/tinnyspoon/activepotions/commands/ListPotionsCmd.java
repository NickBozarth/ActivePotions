package com.tinnyspoon.activepotions.commands;

import org.bukkit.command.CommandSender;

import com.tinnyspoon.activepotions.ActivePotions;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class ListPotionsCmd implements BasicCommand {
    @Override
    public void execute(CommandSourceStack sourceStack, String[] args) {
        CommandSender sender = sourceStack.getSender();
        sender.sendMessage("There are currently " + ActivePotions.potions.size() + " potions");
        ActivePotions.potions.forEach(potion -> sender.sendMessage("- [" + potion.name() + "]"));
    }
}

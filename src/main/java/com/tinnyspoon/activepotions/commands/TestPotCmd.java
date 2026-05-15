package com.tinnyspoon.activepotions.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.tinnyspoon.activepotions.ActivePotions;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class TestPotCmd implements BasicCommand {
    @Override
    public void execute(CommandSourceStack sourceStack, String[] args) {
        if (!(sourceStack.getSender() instanceof Player player)) return;

        player.sendMessage("DEBUG trying to create potions");
        ItemStack slot0 = player.getInventory().getItem(0);
        ItemStack slot1 = player.getInventory().getItem(1);
        ItemStack slot2 = player.getInventory().getItem(2);
        ItemStack slot3 = player.getInventory().getItem(3);
        String slot0String = (slot0 == null) ? "null" : slot0.getType().name();
        String slot1String = (slot1 == null) ? "null" : slot1.getType().name();
        String slot2String = (slot2 == null) ? "null" : slot2.getType().name();
        String slot3String = (slot3 == null) ? "null" : slot3.getType().name();

        player.sendMessage("Items here [" + slot0String + "] + " + slot1String + ", " + slot2String + ", " + slot3String);
        ActivePotions.potions.forEach(potion -> {
            boolean ret = potion.matchesIngredients(slot0, slot1, slot2, slot3);
            player.sendMessage("Ret for [" + potion.name() + "] is " + ret);
        });
    }
}

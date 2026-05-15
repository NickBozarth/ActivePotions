package com.tinnyspoon.activepotions.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

public class PotionBrewingGui {
    public enum BrewingLevel {
        ONE(1),
        TWO(2),
        THREE(3);

        public final int val;

        BrewingLevel(int level) {
            this.val = level;
        }

        public static BrewingLevel fromLevel(int level) throws IllegalArgumentException {
            for (BrewingLevel levelVariant : BrewingLevel.values()) {
                if (levelVariant.val == level) return levelVariant;
            }
            throw new IllegalArgumentException("BrewingLevel provided is invalid");
        }
    }


    static final int SLOTS_PER_ROW  = 9;
    static final int ROWS_PER_LEVEL = 2;
    public static void openInv(Player player, BrewingLevel level) throws IllegalArgumentException {
        int invSlots = level.val * SLOTS_PER_ROW * ROWS_PER_LEVEL;
        Inventory inv = Bukkit.createInventory(player, invSlots, Component.text("Brewing"));

        for (int i = 0; i < invSlots; i++) {
            int mod18 = i % 18;
            if (mod18 == 0 || mod18 == 2 || mod18 == 6 || mod18 == 8) inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            if (mod18 >= 10 && mod18 <=16) inv.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE));
            if (mod18 == 9) inv.setItem(i, new ItemStack(Material.CLOCK));
            if (mod18 == 17) inv.setItem(i, new ItemStack(Material.LIME_DYE));
        }

        player.openInventory(inv);
    }
}

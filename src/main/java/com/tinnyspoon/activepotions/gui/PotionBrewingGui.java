package com.tinnyspoon.activepotions.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.tinnyspoon.activepotions.ActivePotions;
import com.tinnyspoon.activepotions.utility.ItemStackCreator;

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

        ItemStack bgItem = ItemStackCreator.of(Material.BLACK_STAINED_GLASS_PANE)
            .makeMinimal()
            .cancelEvent()
            .collect();
        ItemStack redProgBarItem = ItemStackCreator.of(Material.RED_STAINED_GLASS_PANE)
            .makeMinimal()
            .cancelEvent()
            .collect();
        ItemStack timeLeftItem = ItemStackCreator.of(Material.CLOCK)
            .withDisplayName("No Recipe in Progress")
            .cancelEvent()
            .collect();
        ItemStack goItem = ItemStackCreator.of(Material.LIME_DYE)
            .withDisplayName("Start Current Recipe")
            .withLore(List.of(
                Component.text("Base Ingredient:"),
                Component.text("- None"),
                Component.text("Ingredients:"),
                Component.text("- None"),
                Component.text("- None"),
                Component.text("- None"),
                Component.text("Result:"),
                Component.text("- No Matching Recipe")
            ))
            .cancelEvent()
            .collect();

        for (int i = 0; i < invSlots; i++) {
            int mod18 = i % 18;
            if (mod18 == 0 || mod18 == 2 || mod18 == 6 || mod18 == 8) inv.setItem(i, bgItem);
            if (mod18 >= 10 && mod18 <=16) inv.setItem(i, redProgBarItem);
            if (mod18 == 9) inv.setItem(i, timeLeftItem);
            if (mod18 == 17) {
                ItemMeta goMeta = goItem.getItemMeta();
                goMeta.getPersistentDataContainer().set(new NamespacedKey(ActivePotions.keyNamespace, "brew-confirm"), PersistentDataType.INTEGER, ( i / 18 ) + 1);
                goItem.setItemMeta(goMeta);
                inv.setItem(i, goItem);
            }
        }

        player.openInventory(inv);
    }
}

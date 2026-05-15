package com.tinnyspoon.activepotions.gui;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.tinnyspoon.activepotions.ActivePotions;
import com.tinnyspoon.activepotions.InternalPotion;
import com.tinnyspoon.activepotions.utility.ItemStackCreator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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


    private static final int _SLOTS_PER_ROW  = 9;
    private static final int _ROWS_PER_LEVEL = 2;
    private static final String _GUI_TITLE_STRING = "Brewing";
    private static final Component _GUI_TITLE = Component.text(_GUI_TITLE_STRING);
    public static void openInv(Player player, BrewingLevel level) throws IllegalArgumentException {
        int invSlots = level.val * _SLOTS_PER_ROW * _ROWS_PER_LEVEL;
        Inventory inv = Bukkit.createInventory(player, invSlots, _GUI_TITLE);
        _initializeInv(inv, invSlots);
        

        player.openInventory(inv);
    }



    private static void _initializeInv(Inventory inv, int invSlots) {
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
                goMeta.getPersistentDataContainer().set(new NamespacedKey(ActivePotions.keyNamespace, "brew-level"), PersistentDataType.INTEGER, ( i / 18 ) + 1);
                goItem.setItemMeta(goMeta);
                inv.setItem(i, goItem);
            }
        }

        bgItem.editMeta(meta -> 
            meta.getPersistentDataContainer().set(
                new NamespacedKey(ActivePotions.keyNamespace, "brew-level"),
                PersistentDataType.INTEGER, 
                invSlots / 18
            )
        );
        inv.setItem(0, bgItem);
    }

    public static boolean shouldHandle(InventoryClickEvent event) {
        String invTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        return invTitle.equals(_GUI_TITLE_STRING);
    }

    public static void onClick(InventoryClickEvent event) {
        // if (event.getCurrentItem().getType() == Material.LIME_DYE) _handleConfirm();
        if (event.isCancelled()) return;

        Inventory inv = event.getInventory();
        ItemStack firstItem = inv.getItem(0);
        Integer brewingLevel = firstItem.getPersistentDataContainer().get(new NamespacedKey(ActivePotions.keyNamespace, "brew-level"), PersistentDataType.INTEGER);
        if (brewingLevel == null) brewingLevel = 0;

        for (int i = 0; i < brewingLevel; i++) {
            int rowNum = i * 2;
            ItemStack baseIngredient = inv.getItem(rowNum * 9 + 1);
            ItemStack ingredient1    = inv.getItem(rowNum * 9 + 3);
            ItemStack ingredient2    = inv.getItem(rowNum * 9 + 4);
            ItemStack ingredient3    = inv.getItem(rowNum * 9 + 5);
            int outputSlotNum        = rowNum * 9 + 7;
            ItemStack confirmItem    = inv.getItem(rowNum * 9 + 17);



            ItemStack slot0 = baseIngredient;
            ItemStack slot1 = ingredient1;
            ItemStack slot2 = ingredient2;
            ItemStack slot3 = ingredient3;
            String slot0String = (slot0 == null) ? "null" : slot0.getType().name();
            String slot1String = (slot1 == null) ? "null" : slot1.getType().name();
            String slot2String = (slot2 == null) ? "null" : slot2.getType().name();
            String slot3String = (slot3 == null) ? "null" : slot3.getType().name();

            Bukkit.broadcastMessage("Items here [" + slot0String + "] + " + slot1String + ", " + slot2String + ", " + slot3String);
            Optional<InternalPotion> matchingRecipe = ActivePotions.potions
                .stream()
                .filter(potion -> {
                    boolean ret = potion.matchesIngredients(slot0, slot1, slot2, slot3);
                    Bukkit.broadcastMessage("Ret for [" + potion.name() + "] is " + ret);
                    return ret;
                })
                .findFirst();

            confirmItem.editMeta(meta -> {
                List<Component> lore = meta.lore();
                if (baseIngredient != null) lore.set(1, Component.text("- ").append(baseIngredient.displayName()));
                else lore.set(1, Component.text("- None"));
                if (ingredient1 != null) lore.set(3, Component.text("- ").append(ingredient1.displayName()));
                else lore.set(3, Component.text("- None"));
                if (ingredient2 != null) lore.set(4, Component.text("- ").append(ingredient2.displayName()));
                else lore.set(4, Component.text("- None"));
                if (ingredient3 != null) lore.set(5, Component.text("- ").append(ingredient3.displayName()));
                else lore.set(5, Component.text("- None"));

                if (matchingRecipe.isEmpty()) {
                    lore.set(7, Component.text("- No Matching Recipe"));
                } else {
                    lore.set(7, Component.text("- " + matchingRecipe.get().name()));
                }
                meta.lore(lore);
            });
        }
    }
}

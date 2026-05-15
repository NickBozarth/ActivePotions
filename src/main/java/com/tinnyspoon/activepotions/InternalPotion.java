package com.tinnyspoon.activepotions;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.activepotions.ingredients.Ingredient;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public record InternalPotion (
    String name,
    String description,
    List<String> keys,
    List<String> playerCommands,
    List<String> consoleCommands,
    String permission,
    Color color,
    int uses,
    Ingredient baseIngredient,
    List<Ingredient> ingredients,
    NamespacedKey potionKey
) {
    public static @Nullable InternalPotion fromSection(String potionName, @NotNull ConfigurationSection sec) {
        String description           = sec.getString("description", "");
        List<String> keys            = sec.getStringList("keys");
        List<String> playerCommands  = sec.getStringList("player-commands");
        List<String> consoleCommands = sec.getStringList("console-commands");
        String permission = sec.getString("permission");
        String hexColorString = sec.getString("color");
        Color color;
        try { 
            int colorInt = Long.decode(hexColorString).intValue();
            color = Color.fromRGB(colorInt);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Potion [" + potionName + "] failed to parse color with error [" + e.getMessage() + "]");
            color = Color.WHITE;
        }

        int uses = sec.getInt("uses", -1);
        List<Map<?, ?>> ingredientListMap = sec.getMapList("ingredients");
        if (ingredientListMap.size() == 0) {
            Bukkit.getLogger().warning("Potion [" + potionName + "] failed to load: Ingredient list is empty");
            return null;
        }
        if (ingredientListMap.size() > 3) {
            Bukkit.getLogger().warning("Potion [" + potionName + "] failed to load: Ingredient list is must be less than 3 ingredients");
            return null;
        }

        Ingredient baseIngredient;
        try {
            Map<String, Object> baseIngredientMap = sec.getConfigurationSection("base-ingredient").getValues(false);
            baseIngredient = Ingredient.parseMap(baseIngredientMap);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Failed to load base-ingredient [" + potionName + "] for reason [" + e.getMessage() + "]");
            return null;
        }


        List<Ingredient> ingredients = new ArrayList<>();
        for (Map<?, ?> ingredientMap : ingredientListMap) {
            try {
                ingredients.add(Ingredient.parseMap(ingredientMap));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Failed to load recipe [" + potionName + "] for reason [" + e.getMessage() + "]");
                return null;
            }
        }

        NamespacedKey potionKey = new NamespacedKey(ActivePotions.keyNamespace, potionName.replace(' ', '_').toLowerCase());


        return new InternalPotion(potionName, description, keys, playerCommands, consoleCommands, permission, color, uses, baseIngredient, ingredients, potionKey);
    }

    public @NotNull ItemStack asItem() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta)potion.getItemMeta();

        potionMeta.setColor(this.color());
        int rgbColor = this.color().asRGB();
        potionMeta.displayName(Component.text(this.name(), TextColor.color(rgbColor), TextDecoration.BOLD));
        PersistentDataContainer pdt = potionMeta.getPersistentDataContainer();
        pdt.set(new NamespacedKey(ActivePotions.keyNamespace, "uses"), PersistentDataType.INTEGER, this.uses());

        List<Component> lore = potionMeta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(Component.text(this.description()));

        if (uses != -1) {
            lore.add(Component.text(""));
            lore.add(Component.text("Uses Remaining: " + this.uses()));
        }

        potionMeta.lore(lore);

        this.keys().stream().forEach(key -> pdt.set(this.potionKey, PersistentDataType.BOOLEAN, true));

        potion.setItemMeta(potionMeta);

        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
        builder.addHiddenComponents(DataComponentTypes.POTION_CONTENTS);
        potion.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());


        return potion;
    }

    public void consume(Player player) {
        if (permission != null && !player.hasPermission(this.permission)) {
            player.sendMessage("You do not have the permission to use this item");
            return;
        }

        this.consoleCommands.forEach(command -> {
            command = command.replaceAll("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
        this.playerCommands.forEach(command -> {
            command = command.replaceAll("<player>", player.getName());
            player.performCommand(command);
        });
    }

    public boolean matchesIngredients(@Nullable ItemStack baseIng, @Nullable ItemStack ing0, @Nullable ItemStack ing1, @Nullable ItemStack ing2) {
        if (baseIng == null) return false;
        if (ing0 == null && ing1 == null && ing2 == null) return false;

        if (!this.baseIngredient.matchesItem(baseIng)) return false;

        Boolean itemUsed[] = new Boolean[3];
        itemUsed[0] = ing0 == null;
        itemUsed[1] = ing1 == null;
        itemUsed[2] = ing2 == null;

        // check that all ingredients are matched to an input item
        this.ingredients.stream().allMatch(ingredient -> {
            List<Pair<Integer, ItemStack>> matchingItems = new ArrayList<>();
            if (!itemUsed[0] && ingredient.matchesItem(ing0)) matchingItems.add(Pair.of(0, ing0));
            if (!itemUsed[1] && ingredient.matchesItem(ing1)) matchingItems.add(Pair.of(1, ing1));
            if (!itemUsed[2] && ingredient.matchesItem(ing2)) matchingItems.add(Pair.of(2, ing2));

            if (matchingItems.isEmpty()) return false;

            // why 😭😭
            // find the stack with the smallest amount in it and use that stack for current ingredient
            var smallestMatchingItemIndex = matchingItems.stream().min((i, j) -> Integer.compare(i.getRight().getAmount(), j.getRight().getAmount())).get().getLeft();
            itemUsed[smallestMatchingItemIndex] = true;

            return true;
        });
        
        // check if all items were used
        return Arrays.stream(itemUsed).allMatch(wasUsed -> wasUsed);
    }
}

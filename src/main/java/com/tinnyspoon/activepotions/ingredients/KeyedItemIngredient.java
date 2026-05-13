package com.tinnyspoon.activepotions.ingredients;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.activepotions.ActivePotions;

public class KeyedItemIngredient extends Ingredient {
    private NamespacedKey key;

    protected KeyedItemIngredient(@NotNull Map<?, ?> ingredientMap) throws IllegalArgumentException {
        this.amount = (Integer)ingredientMap.get("amount");
        
        if (!ingredientMap.containsKey("key")) throw new IllegalArgumentException("ItemIngredient does not contain a key");
        Object keyObj = ingredientMap.get("key");
        if (!(keyObj instanceof String keyString)) throw new IllegalArgumentException("ItemIngredient key is not a String");
        this.key = NamespacedKey.fromString(keyString);
    }

    @Override
    protected boolean _matchesItem(ItemStack item) {
        return item.getItemMeta() != null &&
               item.getItemMeta().getPersistentDataContainer().has(this.key);
    }
}

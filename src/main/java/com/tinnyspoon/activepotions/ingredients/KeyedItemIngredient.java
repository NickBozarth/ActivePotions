package com.tinnyspoon.activepotions.ingredients;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KeyedItemIngredient extends Ingredient {
    private String key;

    protected KeyedItemIngredient(@NotNull Map<?, ?> ingredientMap) throws IllegalArgumentException {
        this.amount = (Integer)ingredientMap.get("amount");
        
        if (!ingredientMap.containsKey("key")) throw new IllegalArgumentException("ItemIngredient does not contain a key");
        Object keyObj = ingredientMap.get("key");
        if (!(keyObj instanceof String keyString)) throw new IllegalArgumentException("ItemIngredient key is not a String");
        this.key = keyString;
    }
}

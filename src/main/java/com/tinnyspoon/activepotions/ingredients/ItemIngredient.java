package com.tinnyspoon.activepotions.ingredients;

import java.util.Map;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ItemIngredient extends Ingredient {
    private Material mat;

    protected ItemIngredient(@NotNull Map<?, ?> ingredientMap) throws IllegalArgumentException {
        this.amount = (Integer)ingredientMap.get("amount");
        
        if (!ingredientMap.containsKey("material")) throw new IllegalArgumentException("ItemIngredient does not contain a material");
        Object matObj = ingredientMap.get("material");
        if (!(matObj instanceof String matString)) throw new IllegalArgumentException("ItemIngredient material is not a String");
        this.mat = Material.matchMaterial(matString);
        if (this.mat == null) throw new IllegalArgumentException("ItemIngredient Material." + matString + " is not a valid Material");
    }
}

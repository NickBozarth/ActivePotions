package com.tinnyspoon.activepotions.ingredients;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public abstract class Ingredient {
    int amount = 0;

    public static @NotNull Ingredient parseMap(@NotNull Map<?, ?> ingredientMap) throws IllegalArgumentException {
        // ensure type and amount exist and are valid
        if (!ingredientMap.containsKey("type")) throw new IllegalArgumentException("Ingredient does not contain a type");
        Object typeObj = ingredientMap.get("type");
        if (!(typeObj instanceof String typeString)) throw new IllegalArgumentException("Ingredient type is not a String");
        if (!ingredientMap.containsKey("amount")) throw new IllegalArgumentException("Ingredient does not contain an amount");
        Object amountObj = ingredientMap.get("amount");
        if (!(amountObj instanceof Integer amount)) throw new IllegalArgumentException("Ingredient amount is not an Integer");        

        
        // construct and return item
        if (typeString.equals("Item")) return new ItemIngredient(ingredientMap);
        if (typeString.equals("KeyedItem")) return new KeyedItemIngredient(ingredientMap);

        // default to exception
        throw new IllegalArgumentException("Ingredient no ingredient type defined for " + typeString);
    }
}
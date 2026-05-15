package com.tinnyspoon.activepotions.ingredients;

import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class PotionIngredient extends Ingredient {

    private final PotionType type;

    protected PotionIngredient(@NotNull Map<?, ?> ingredientMap) throws IllegalArgumentException {

        if (!ingredientMap.containsKey("potion-type")) throw new IllegalArgumentException("PotionIngredient does not contain a potion-type field");
        if (!(ingredientMap.get("potion-type") instanceof String potionTypeString)) throw new IllegalArgumentException("PotionIngredient potion-type is not a string");

        try {
            type = PotionType.valueOf(potionTypeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("PotionIngredient failed for reason: " + e.getMessage());
        }
    }


    @Override
    protected boolean _matchesItem(ItemStack item) {
        return item.hasItemMeta() &&
               item.getItemMeta() instanceof PotionMeta potionMeta &&
               potionMeta.getBasePotionType() == this.type;
    }
}

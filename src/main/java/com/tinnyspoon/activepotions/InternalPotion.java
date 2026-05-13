package com.tinnyspoon.activepotions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.activepotions.ingredients.Ingredient;

public record InternalPotion (
    String name,
    List<String> keys,
    List<String> playerCommands,
    List<String> consoleCommands,
    String permission,
    Color color,
    int uses,
    List<Ingredient> ingredients
) {
    public static @Nullable InternalPotion fromSection(String potionName, @NotNull ConfigurationSection sec) {
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

        int uses = sec.getInt("uses", 1);
        List<Map<?, ?>> ingredientListMap = sec.getMapList("ingredients");
        if (ingredientListMap.size() == 0) {
            Bukkit.getLogger().warning("Potion [" + potionName + "] failed to load: Ingredient list is empty");
            return null;
        }
        if (ingredientListMap.size() > 3) {
            Bukkit.getLogger().warning("Potion [" + potionName + "] failed to load: Ingredient list is must be less than 3 ingredients");
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


        return new InternalPotion(potionName, keys, playerCommands, consoleCommands, permission, color, uses, ingredients);
    }
}

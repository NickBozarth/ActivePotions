package com.tinnyspoon.activepotions;


public record InternalPotion (
    List<String> playerCommands,
    List<String> consoleCommands,
    String permission,
    int hexColor,
    int uses,
    // List<Ingredient> ingredients,
) {}

package com.tinnyspoon.activepotions;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class Listeners implements Listener {
    @EventHandler
    public void onPotion(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) { Bukkit.broadcastMessage("Not a potion"); return; }

        Optional<InternalPotion> potionOpt = ActivePotions.potions
            .stream()
            .filter(potion -> event.getItem().getPersistentDataContainer().has(potion.potionKey()))
            .findFirst();
        if (potionOpt.isEmpty()) { Bukkit.broadcastMessage("No potion found"); return; }
        potionOpt.get().consume(event.getPlayer());
    }
}

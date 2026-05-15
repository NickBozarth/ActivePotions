package com.tinnyspoon.activepotions;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.tinnyspoon.activepotions.gui.PotionBrewingGui;

public class Listeners implements Listener {
    
    private <E> void _runOneTickLater(Consumer<E> eventHandler, E event) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(ActivePotions.class), () -> {
            eventHandler.accept(event);
        });
    }


    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) { Bukkit.broadcastMessage("Not a potion"); return; }

        Optional<InternalPotion> potionOpt = ActivePotions.potions
            .stream()
            .filter(potion -> event.getItem().getPersistentDataContainer().has(potion.potionKey()))
            .findFirst();
        if (potionOpt.isEmpty()) { Bukkit.broadcastMessage("No potion found"); return; }
        potionOpt.get().consume(event.getPlayer());
    }

    @EventHandler
    public void invClick(InventoryClickEvent event) {
        if (PotionBrewingGui.shouldHandle(event)) _runOneTickLater(_event -> PotionBrewingGui.onClick(_event), event);
    }
}

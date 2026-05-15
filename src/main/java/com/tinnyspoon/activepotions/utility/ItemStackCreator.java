package com.tinnyspoon.activepotions.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.activepotions.ActivePotions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ItemStackCreator {
    private ItemStack stack;
    

    public ItemStackCreator() {
        stack = new ItemStack(Material.AIR);
    }    

    public @NotNull ItemStack collect() {
        return this.stack;
    }


    public @NotNull ItemStackCreator withMaterial(@Nullable Material material) {
        if (material == null) material = Material.BARRIER;
        this.stack.setType(material);
        return this;
    }

    public @NotNull ItemStackCreator withAmount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }    

    public @NotNull ItemStackCreator withDisplayName(@Nullable Component displayName) {
        this.stack.editMeta(meta -> {
            meta.displayName(displayName);
        });
        return this;
    }
    public @NotNull ItemStackCreator withDisplayName(@NotNull String displayName) {
        return this.withDisplayName(Component.text(displayName, NamedTextColor.GRAY));
    }

    public @NotNull ItemStackCreator addLore(@Nullable Component lore) {
        if (lore == null) return this;

        this.stack.editMeta(meta -> {
            List<Component> loreList = meta.lore();

            if (loreList == null) {
                loreList = new ArrayList<>();
            }

            loreList.add(lore);
            meta.lore(loreList);
        });

        return this;
    }
    public @NotNull ItemStackCreator addLore(@NotNull String loreString) {
        return this.addLore(Component.text(loreString, NamedTextColor.GRAY));
    }

    public @NotNull ItemStackCreator withLore(List<@NotNull Component> lore) {
        this.stack.editMeta(meta -> {
            meta.lore(lore);
        });
        return this;
    }

    
    public @NotNull ItemStackCreator cancelEvent() {
        return withKey("event-cancelled", PersistentDataType.BOOLEAN, true);
    }
    
    public @NotNull ItemStackCreator editMeta(@NotNull Consumer<? super ItemMeta> consumer) {
        this.stack.editMeta(consumer);
        return this;
    }
    
    public <Z> @NotNull ItemStackCreator withKey(@NotNull String keyString, @NotNull PersistentDataType<?, Z> dataType, @NotNull Z value) {
        NamespacedKey key = new NamespacedKey(ActivePotions.keyNamespace, keyString);
        this.stack.editMeta(meta -> {
            meta.getPersistentDataContainer().set(key, dataType, value);
        });

        return this;
    }

    public static <T, Z> void setKey(@NotNull ItemStack stack, @NotNull String keyString, @NotNull PersistentDataType<?, Z> dataType, @NotNull Z value) {
        NamespacedKey key = new NamespacedKey(ActivePotions.keyNamespace, keyString);
        stack.editMeta(meta -> {
            meta.getPersistentDataContainer().set(key, dataType, value);
        });
    }

    public static @Nullable <T, Z> Z getPersistent(@Nullable ItemStack stack, @NotNull String keyString, @NotNull PersistentDataType<T, Z> dataType) {
        if (stack == null) return null;
        NamespacedKey key = new NamespacedKey(ActivePotions.keyNamespace, keyString);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(key, dataType);
    }

    @Contract("_, !null -> !null")
    public static <T, Z> Z getPersistent(@NotNull ItemStack stack, @NotNull String keyString, @NotNull PersistentDataType<T, Z> dataType, @Nullable Z def) {
        Z ret = ItemStackCreator.getPersistent(stack, keyString, dataType);
        if (ret == null) return def;
        return ret;
    }
}

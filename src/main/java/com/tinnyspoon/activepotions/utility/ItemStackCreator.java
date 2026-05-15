package com.tinnyspoon.activepotions.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.activepotions.ActivePotions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ItemStackCreator {
    private ItemStack stack;
    

    private ItemStackCreator(Material mat) {
        stack = new ItemStack(mat);
    }    

    public static ItemStackCreator of(Material mat) {
        return new ItemStackCreator(mat);
    }

    @Contract("_ -> this")
    public @NotNull ItemStack collect() {
        return this.stack;
    }


    @Contract("_ -> this")
    public @NotNull ItemStackCreator withMaterial(@Nullable Material material) {
        if (material == null) material = Material.BARRIER;
        // this.stack.setData(Valued<Material>., material);
        this.stack = this.stack.withType(material);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ItemStackCreator withAmount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }    

    @Contract("_ -> this")
    public @NotNull ItemStackCreator withDisplayName(@Nullable Component displayName) {
        this.stack.editMeta(meta -> {
            meta.displayName(displayName);
        });
        return this;
    }
    @Contract("_ -> this")
    public @NotNull ItemStackCreator withDisplayName(@NotNull String displayName) {
        return this.withDisplayName(Component.text(displayName, NamedTextColor.GRAY));
    }

    @Contract("_ -> this")
    public @NotNull ItemStackCreator makeMinimal() {
        this.withDisplayName("");
        this.withLore(List.of());
        return this;
    }

    @Contract("_ -> this")
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
    @Contract("_ -> this")
    public @NotNull ItemStackCreator addLore(@NotNull String loreString) {
        return this.addLore(Component.text(loreString, NamedTextColor.GRAY));
    }

    @Contract("_ -> this")
    public @NotNull ItemStackCreator withLore(List<@NotNull Component> lore) {
        this.stack.editMeta(meta -> {
            meta.lore(lore);
        });
        return this;
    }

    
    @Contract("_ -> this")
    public @NotNull ItemStackCreator cancelEvent() {
        return withPersistent("event-cancelled", PersistentDataType.BOOLEAN, true);
    }
    
    @Contract("_ -> this")
    public @NotNull ItemStackCreator editMeta(@NotNull Consumer<? super ItemMeta> consumer) {
        this.stack.editMeta(consumer);
        return this;
    }
    
    @Contract("_ -> this")
    public <Z> @NotNull ItemStackCreator withPersistent(@NotNull String keyString, @NotNull PersistentDataType<?, Z> dataType, @NotNull Z value) {
        NamespacedKey key = new NamespacedKey(ActivePotions.keyNamespace, keyString);
        this.stack.editMeta(meta -> {
            meta.getPersistentDataContainer().set(key, dataType, value);
        });

        return this;
    }

    @Contract("_ -> this")
    public static <T, Z> void setKey(@NotNull ItemStack stack, @NotNull String keyString, @NotNull PersistentDataType<?, Z> dataType, @NotNull Z value) {
        NamespacedKey key = new NamespacedKey(ActivePotions.keyNamespace, keyString);
        stack.editMeta(meta -> {
            meta.getPersistentDataContainer().set(key, dataType, value);
        });
    }

    @Contract("_ -> this")
    public static @Nullable <T, Z> Z getPersistent(@Nullable ItemStack stack, @NotNull String keyString, @NotNull PersistentDataType<T, Z> dataType) {
        if (stack == null) return null;
        NamespacedKey key = new NamespacedKey(ActivePotions.keyNamespace, keyString);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(key, dataType);
    }

    @Contract("_ -> this; _, !null -> !null")
    public static <T, Z> Z getPersistent(@NotNull ItemStack stack, @NotNull String keyString, @NotNull PersistentDataType<T, Z> dataType, @Nullable Z def) {
        Z ret = ItemStackCreator.getPersistent(stack, keyString, dataType);
        if (ret == null) return def;
        return ret;
    }
}

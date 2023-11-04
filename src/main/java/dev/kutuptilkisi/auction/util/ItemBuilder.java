package dev.kutuptilkisi.auction.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemBuilder {

    public static class EnchantBuilder {
        private final ItemBuilder itemBuilder;
        private final HashMap<Enchantment, Integer> safeEnchants;
        private final HashMap<Enchantment, Integer> unsafeEnchants;

        private EnchantBuilder(ItemBuilder itemBuilder){
            this.itemBuilder = itemBuilder;
            this.safeEnchants = new HashMap<>();
            this.unsafeEnchants = new HashMap<>();
        }

        public EnchantBuilder addEnchant(Enchantment enchantment, int level){
            safeEnchants.put(enchantment, level);
            return this;
        }

        public EnchantBuilder addUnsafeEnchant(Enchantment enchantment, int level){
            unsafeEnchants.put(enchantment, level);
            return this;
        }

        public ItemBuilder build(){
            for(Map.Entry<Enchantment, Integer> mapElement : safeEnchants.entrySet()){
                itemBuilder.itemStack.addEnchantment(mapElement.getKey(), mapElement.getValue());
            }
            for(Map.Entry<Enchantment, Integer> mapElement : unsafeEnchants.entrySet()){
                itemBuilder.itemStack.addUnsafeEnchantment(mapElement.getKey(), mapElement.getValue());
            }
            return itemBuilder;
        }
    }

    public static class MetaBuilder {
        private final ItemBuilder itemBuilder;
        private final ItemMeta itemMeta;

        private String displayName;
        private Integer modalId;
        private boolean unbreakable;
        private final List<ItemFlag> itemFlags;
        private final List<String> lore;

        private MetaBuilder(ItemBuilder itemBuilder){
            this.itemBuilder = itemBuilder;
            this.itemMeta = itemBuilder.itemStack.getItemMeta();
            this.itemFlags = new ArrayList<>();
            this.lore = new ArrayList<>();
        }

        public MetaBuilder displayName(String displayName){
            this.displayName = displayName;
            return this;
        }

        public MetaBuilder customModalData(int id){
            this.modalId = id;
            return this;
        }

        public MetaBuilder unbreakable(boolean v){
            this.unbreakable = v;
            return this;
        }

        public MetaBuilder addFlag(ItemFlag... itemFlag){
            this.itemFlags.addAll(Arrays.asList(itemFlag));
            return this;
        }

        public MetaBuilder addLore(String text){
            lore.add(text);
            return this;
        }

        public ItemBuilder build(){
            if(this.displayName != null) this.itemMeta.setDisplayName(displayName);
            if(this.modalId != null) this.itemMeta.setCustomModelData(this.modalId);
            if(this.unbreakable) this.itemMeta.setUnbreakable(true);
            if(this.itemFlags.size() != 0) this.itemFlags.forEach(this.itemMeta::addItemFlags);
            if(this.lore.size() != 0) this.itemMeta.setLore(this.lore);

            this.itemBuilder.itemStack.setItemMeta(this.itemMeta);
            return this.itemBuilder;
        }
    }

    private final ItemStack itemStack;
    private final EnchantBuilder enchantBuilder;
    private final MetaBuilder metaBuilder;
    private ItemBuilder(ItemStack itemStack){
        this.itemStack = itemStack;
        this.enchantBuilder = new EnchantBuilder(this);
        this.metaBuilder = new MetaBuilder(this);
    }

    public EnchantBuilder getEnchantBuilder(){
        return this.enchantBuilder;
    }

    public MetaBuilder getMetaBuilder(){
        return this.metaBuilder;
    }

    /*
        BUILD METHODS
     */
    public static ItemBuilder builder(ItemStack itemStack){
        return new ItemBuilder(itemStack);
    }

    public ItemStack build(){
        return itemStack;
    }
}

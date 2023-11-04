package dev.kutuptilkisi.auction.instance;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionItem {
    private final int id;
    private final UUID uuid;
    private final int price;
    private final ItemStack itemStack;

    public AuctionItem(int id, UUID uuid, int price, ItemStack itemStack){
        this.id = id;
        this.uuid = uuid;
        this.price = price;
        this.itemStack = itemStack;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPrice() {
        return price;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}

package dev.kutuptilkisi.auction.gui;

import dev.kutuptilkisi.auction.Auction;
import dev.kutuptilkisi.auction.instance.AuctionItem;
import dev.kutuptilkisi.auction.util.ChatUtil;
import dev.kutuptilkisi.auction.util.InventoryUtil;
import dev.kutuptilkisi.auction.util.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AuctionItemPage implements InventoryProvider {
    private static final ItemStack GO_BACK = ItemBuilder.builder(new ItemStack(Material.ARROW))
            .getMetaBuilder()
                .displayName(ChatUtil.color("&aGo Back"))
                .addLore(ChatUtil.color("&cClick to go back to view your items on AH Menu"))
                .build()
            .build();

    private static final ItemStack REMOVE_ITEM = ItemBuilder.builder(new ItemStack(Material.BARRIER))
            .getMetaBuilder()
                .displayName(ChatUtil.color("&cRemove item from AH"))
                .addLore(ChatUtil.color("&cRemoves item from AH"))
                .addLore("")
                .addLore("&dOnce you remove item, it comes back to your inventory")
                .build()
            .build();

    private final AuctionItem auctionItem;
    public AuctionItemPage(AuctionItem auctionItem){
        this.auctionItem = auctionItem;
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.set(0, 2, ClickableItem.of(REMOVE_ITEM, event -> {
            HashMap<Integer, ItemStack> remain = event.getWhoClicked().getInventory().addItem(auctionItem.getItemStack());
            if(remain.size() != 0){
                event.getWhoClicked().sendMessage(ChatUtil.color("&cYou can't remove this item as you don't have space in your inventory!"));
                return;
            }
            Auction.getInstance().getDatabase().removeItem(auctionItem.getId());
            event.getWhoClicked().sendMessage(ChatUtil.color("&aYou removed this item from AH"));
            InventoryUtil.openPlayerItemsInventory(player);
        }));
        inventoryContents.set(0, 4, ClickableItem.empty(auctionItem.getItemStack()));
        inventoryContents.set(0, 6, ClickableItem.of(GO_BACK, event -> {
            InventoryUtil.openPlayerItemsInventory(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}

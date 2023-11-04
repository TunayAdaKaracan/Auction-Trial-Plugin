package dev.kutuptilkisi.auction.util;

import dev.kutuptilkisi.auction.Auction;
import dev.kutuptilkisi.auction.gui.AuctionItemPage;
import dev.kutuptilkisi.auction.gui.ItemsPage;
import dev.kutuptilkisi.auction.gui.PlayerItemsPage;
import dev.kutuptilkisi.auction.instance.AuctionItem;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

public class InventoryUtil {

    public static void openItemsInventory(Player player, int page){
        SmartInventory smartInventory = SmartInventory.builder()
                .id(player.getDisplayName())
                .manager(Auction.getInstance().getInventoryManager())
                .provider(new ItemsPage())
                .size(6, 9)
                .title(ChatUtil.color("&dAuction Items"))
                .build();
        smartInventory.open(player, page);
    }
    public static void openItemsInventory(Player player){
        openItemsInventory(player, 0);
    }

    public static void openPlayerItemsInventory(Player player, int page){
        SmartInventory smartInventory = SmartInventory.builder()
                .id(player.getDisplayName())
                .manager(Auction.getInstance().getInventoryManager())
                .provider(new PlayerItemsPage())
                .size(6, 9)
                .title(ChatUtil.color("&dYour Auction Items"))
                .build();
        smartInventory.open(player, page);
    }

    public static void openPlayerItemsInventory(Player player){
        openPlayerItemsInventory(player, 0);
    }

    public static void openAuctionItemPage(Player player, AuctionItem auctionItem){
        SmartInventory smartInventory = SmartInventory.builder()
                .id(player.getDisplayName())
                .manager(Auction.getInstance().getInventoryManager())
                .provider(new AuctionItemPage(auctionItem))
                .size(1, 9)
                .title(ChatUtil.color("&dItem"))
                .build();
        smartInventory.open(player);
    }
}

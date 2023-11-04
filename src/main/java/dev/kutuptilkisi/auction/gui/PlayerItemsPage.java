package dev.kutuptilkisi.auction.gui;

import dev.kutuptilkisi.auction.Auction;
import dev.kutuptilkisi.auction.instance.AuctionItem;
import dev.kutuptilkisi.auction.util.ChatUtil;
import dev.kutuptilkisi.auction.util.InventoryUtil;
import dev.kutuptilkisi.auction.util.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PlayerItemsPage implements InventoryProvider {
    private static final ItemStack CLOSE = ItemBuilder.builder(new ItemStack(Material.BARRIER))
            .getMetaBuilder()
                .displayName(ChatUtil.color("&c&bClose This Menu"))
                .build()
            .build();

    private static final ItemStack PREVIOUS = ItemBuilder.builder(new ItemStack(Material.SPECTRAL_ARROW))
            .getMetaBuilder()
                .displayName(ChatUtil.color("&eGo Back To Previous Page"))
                .build()
            .build();

    private static final ItemStack NEXT = ItemBuilder.builder(new ItemStack(Material.SPECTRAL_ARROW))
            .getMetaBuilder()
                .displayName(ChatUtil.color("&eGo To Next Page"))
                .build()
            .build();

    private List<AuctionItem> auctionItems;

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Pagination pagination = inventoryContents.pagination();
        int itemCount = Auction.getInstance().getDatabase().getItemCount();

        ClickableItem[] items = new ClickableItem[itemCount];
        auctionItems = Auction.getInstance().getDatabase().getItems(player.getUniqueId());
        for(int i=0; i<auctionItems.size(); i++){
            ItemStack itemStack = auctionItems.get(i).getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Auction.getInstance(), "ah_id"), PersistentDataType.INTEGER, auctionItems.get(i).getId());
            List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()){
                lore.addAll(itemMeta.getLore());
                lore.add("");
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&d&bPrice: "+auctionItems.get(i).getPrice()+"$"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            items[i] = ClickableItem.of(itemStack, event -> {
                openItemPage(event, inventoryContents);
            });
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(45);
        int slotX = 0;
        int slotY = 0;
        for(ClickableItem clickableItem : pagination.getPageItems()){
            inventoryContents.set(slotY, slotX, clickableItem);
            slotX += 1;
            if(slotX % 9 == 0){
                slotY += 1;
                slotX = 0;
            }
        }

        if(!pagination.isFirst()){
            inventoryContents.set(5, 2, ClickableItem.of(PREVIOUS, event -> {
                InventoryUtil.openPlayerItemsInventory((Player) event.getWhoClicked(), pagination.previous().getPage());
            }));
        }

        inventoryContents.set(5, 4, ClickableItem.of(CLOSE, event -> {event.getWhoClicked().closeInventory();}));

        if(!pagination.isLast()) {
            inventoryContents.set(5, 6, ClickableItem.of(NEXT, event -> {
                InventoryUtil.openPlayerItemsInventory((Player) event.getWhoClicked(), pagination.next().getPage());
            }));
        }
    }

    private AuctionItem findFromItemStack(ItemStack itemStack){
        int id = itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Auction.getInstance(), "ah_id"), PersistentDataType.INTEGER);
        for(AuctionItem auctionItem : auctionItems){
            if(auctionItem.getId() == id) return auctionItem;
        }
        return null;
    }

    public void openItemPage(InventoryClickEvent event, InventoryContents inventoryContents){
        AuctionItem auctionItem = findFromItemStack(event.getCurrentItem());
        if(auctionItem == null) return;
        InventoryUtil.openAuctionItemPage((Player) event.getWhoClicked(), auctionItem);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {}
}

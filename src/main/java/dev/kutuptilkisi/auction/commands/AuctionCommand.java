package dev.kutuptilkisi.auction.commands;

import dev.kutuptilkisi.auction.Auction;
import dev.kutuptilkisi.auction.util.ChatUtil;
import dev.kutuptilkisi.auction.util.InventoryUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionCommand implements CommandExecutor {

    private void sendHelpCommand(Player p){
        p.sendMessage(ChatUtil.color("&dCorrect Usage:"));
        p.sendMessage(ChatUtil.color("&e- /ah"));
        p.sendMessage(ChatUtil.color("&e-/ah myitems"));
        p.sendMessage(ChatUtil.color("&e- /ah sell <price>"));
    }

    private boolean sendGUI(Player player){
        InventoryUtil.openItemsInventory(player);
        return true;
    }

    private boolean sellItem(Player player, int price){
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType().isAir()){
            player.sendMessage(ChatUtil.color("&cYou need to hold the item you want to sell!"));
            return false;
        }

        Auction.getInstance().getDatabase().addItem(player.getUniqueId(), price, itemStack);
        player.getInventory().setItemInMainHand(null);
        player.sendMessage(ChatUtil.color("&aYou sold "+(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().replace("_", " "))+" &afor "+price+"$"));
        return true;
    }

    private boolean playerItems(Player player){
        InventoryUtil.openPlayerItemsInventory(player);
        return true;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            if(args.length == 0){
                return sendGUI(p);
            } else if(args.length == 2 && args[0].equals("sell")){
                int price;
                try {
                    price = Integer.parseInt(args[1]);
                } catch (NumberFormatException e){
                    p.sendMessage(ChatUtil.color("&cPlease enter a number!"));
                    return false;
                }
                if(price <= 0){
                    p.sendMessage(ChatUtil.color("&cPrice must be bigger than 0!"));
                    return false;
                }
                return sellItem(p, price);
            } if(args.length == 1 && args[0].equals("myitems")){
                return playerItems(p);
            } else {
                sendHelpCommand(p);
                return false;
            }
        }
        return true;
    }
}

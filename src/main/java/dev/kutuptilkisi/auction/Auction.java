package dev.kutuptilkisi.auction;

import dev.kutuptilkisi.auction.commands.AuctionCommand;
import dev.kutuptilkisi.auction.database.Database;
import dev.kutuptilkisi.auction.util.ItemBuilder;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Auction extends JavaPlugin {

    private static Auction INSTANCE;
    public static Auction getInstance(){
        return INSTANCE;
    }

    private Database database;
    private InventoryManager inventoryManager;

    private Economy econ;

    @Override
    public void onEnable() {
        INSTANCE = this;

        database = new Database(new File(getDataFolder(), "database.db"));

        if(!database.connect()){
            Bukkit.getLogger().severe("Couldn't create/connect to database");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if(!setupEconomy()){
            Bukkit.getLogger().severe("Couldn't get Vault economy");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        database.initialize();
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        getCommand("ah").setExecutor(new AuctionCommand());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void onDisable() {
        database.close();
    }

    public Database getDatabase() {
        return database;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public Economy getEcon() {
        return econ;
    }
}

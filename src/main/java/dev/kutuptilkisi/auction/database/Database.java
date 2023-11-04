package dev.kutuptilkisi.auction.database;

import dev.kutuptilkisi.auction.instance.AuctionItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Database {
    private static final int PAGE_MAX_ELEMENT_AMOUNT = 45;

    private final File file;
    private Connection connection;
    public Database(File file){
        this.file = file;
    }

    public boolean connect(){

        try {
            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            String url = "jdbc:sqlite:"+file.getPath();
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Database Structure
    // id: int | Auto incremented id
    // uuid: text | User who added the item
    // price: int | The price it is selling for
    // item: text | base64 coded itemstack

    public void initialize(){
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS auction(ID INTEGER PRIMARY KEY AUTOINCREMENT, UUID TEXT, PRICE INTEGER, ITEM TEXT);");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int getItemCount(){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(ID) AS count FROM auction");
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) return 0;
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AuctionItem getItem(int id){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM auction WHERE ID=?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()) return null;
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(resultSet.getString(4)));
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            ItemStack itemStack = (ItemStack) bukkitObjectInputStream.readObject();
            bukkitObjectInputStream.close();
            byteArrayInputStream.close();
            return new AuctionItem(resultSet.getInt(1), UUID.fromString(resultSet.getString(2)), resultSet.getInt(3), itemStack);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AuctionItem> getItems(UUID uuid){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM auction" + (uuid != null ? " WHERE UUID=?": ""));
            if(uuid != null) preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<AuctionItem> items = new ArrayList<>();
            while(resultSet.next()){
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(resultSet.getString(4)));
                BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
                ItemStack itemStack = (ItemStack) bukkitObjectInputStream.readObject();
                bukkitObjectInputStream.close();
                byteArrayInputStream.close();
                items.add(new AuctionItem(
                        resultSet.getInt(1),
                        UUID.fromString(resultSet.getString(2)),
                        resultSet.getInt(3),
                        itemStack
                ));
            }
            return items;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AuctionItem> getItems(){
        return getItems(null);
    }

    public void addItem(UUID uuid, int price, ItemStack itemStack){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
            bukkitObjectOutputStream.writeObject(itemStack);
            bukkitObjectOutputStream.flush();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO auction(UUID, PRICE, ITEM) VALUES(?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setInt(2, price);
            statement.setString(3, Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
            statement.executeUpdate();
            bukkitObjectOutputStream.close();
            byteArrayOutputStream.close();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeItem(int id){
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM auction WHERE ID=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

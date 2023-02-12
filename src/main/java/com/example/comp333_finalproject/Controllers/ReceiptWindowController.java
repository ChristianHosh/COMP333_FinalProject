package com.example.comp333_finalproject.Controllers;

import com.example.comp333_finalproject.Classes.DatabaseConnection;
import com.example.comp333_finalproject.Classes.Item;
import com.example.comp333_finalproject.TableClasses.ItemOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReceiptWindowController {

    @FXML
    private Label customerNameLabel;

    @FXML
    private TableView<Item> itemsTable;

    @FXML
    private Label orderIDLabel;

    @FXML
    private Label orderPriceLabel;

    @FXML
    private TableColumn<Item, String> tCol_itemBrand;

    @FXML
    private TableColumn<Item, String> tCol_itemColor;

    @FXML
    private TableColumn<Item, String> tCol_itemName;

    @FXML
    private TableColumn<Item, Double> tCol_itemPrice;

    private static String customerName;
    private static String orderID;
    private static String orderPrice;
    private static ArrayList<Integer> itemIDs;

    public static void setData(String customerName, int orderID, double orderPrice, ArrayList<Integer> itemIDs){
        ReceiptWindowController.customerName = customerName;
        ReceiptWindowController.orderID = String.valueOf(orderID);
        ReceiptWindowController.orderPrice = String.valueOf(orderPrice);
        ReceiptWindowController.itemIDs = itemIDs;
    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        setValueFactoryOrderItems();

        customerNameLabel.setText("Customer Name: " + customerName);
        orderIDLabel.setText("Order ID: " + orderID);
        orderPriceLabel.setText("Order Price: " + orderPrice);

        itemsTable.setItems(getItemsFromID());
    }

    private ObservableList<Item> getItemsFromID() throws SQLException, ClassNotFoundException {
        ObservableList<Item> data = FXCollections.observableArrayList();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection selectConnection = databaseConnection.connectDB();
        String selectQuery = "SELECT IName, Brand, Color, Price FROM ecommerce.items WHERE itemID = ?";
        PreparedStatement selectStatement = selectConnection.prepareStatement(selectQuery);
        for (Integer itemID : itemIDs){
            selectStatement.setInt(1,itemID);
            ResultSet selectResult = selectStatement.executeQuery();
            if (selectResult.next()){
                data.add(new Item(selectResult.getString(1),selectResult.getString(2),selectResult.getString(3),selectResult.getDouble(4)));
            }
        }

        return data;
    }

    private void setValueFactoryOrderItems() {
        tCol_itemBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        tCol_itemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tCol_itemColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        tCol_itemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

}

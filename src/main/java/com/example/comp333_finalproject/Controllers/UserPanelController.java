package com.example.comp333_finalproject.Controllers;

import com.example.comp333_finalproject.Classes.*;
import com.example.comp333_finalproject.Driver;
import com.example.comp333_finalproject.Classes.MyListener;
import com.example.comp333_finalproject.TableClasses.ItemOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserPanelController {

    @FXML
    private GridPane grid;

    @FXML
    private Label label_userFullName;

    @FXML
    private ScrollPane scroll;

    @FXML
    private AnchorPane browserPane;

    @FXML
    private AnchorPane orderPane;

    @FXML
    private Button button_newOrder;

    @FXML
    private TableView<ItemOrder> itemOrderTable;

    @FXML
    private TableColumn<ItemOrder, String> itemOrderTable_itemName;

    @FXML
    private TableColumn<ItemOrder, Double> itemOrderTable_itemPrice;


    private final List<Item> items = new ArrayList<>();

    private static Customer currentUser;

    private Order currentOrder;

    private MyListener myListener;


    // INITIALIZATION
    public static void setCustomer(Customer user){
        currentUser = user;
    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        setValueFactoryOrderItems();

        label_userFullName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        items.addAll(Driver.getItemList());
        myListener = item -> addItemToOrder(item);
        int column = 0;
        int row = 0;
        try {
            for (Item item : items) {
                if (item.getQuantity() == 0)
                    continue;
                FXMLLoader fxmlLoader = new FXMLLoader(Driver.class.getResource("itemCard.fxml"));
                VBox vBox = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setData(item, myListener);
                if (column == 3) {
                    column = 0;
                    row++;
                }
                grid.add(vBox, column++, row);
                GridPane.setMargin(vBox, new Insets(10));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        grid.setMinWidth(695);
        grid.setPrefWidth(695);
        grid.setMaxWidth(695);
    }

    // TOP MENU BUTTONS
    @FXML
    void browseItems(ActionEvent event) {
        browserPane.setVisible(true);
        orderPane.setVisible(false);
    }

    @FXML
    void showCustomerOrders(ActionEvent event) {
        browserPane.setVisible(false);
        orderPane.setVisible(true);
        try{
            updateOrderItem();
        } catch (SQLException | ClassNotFoundException sqlException) {
            sqlException.printStackTrace();
        }

    }

    @FXML
    void removeSelectedItem(ActionEvent event) throws SQLException, ClassNotFoundException {
        ItemOrder selected = itemOrderTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        String deleteQuery = "DELETE FROM item_order WHERE item_order.itemID = ? AND item_order.orderID = ?";
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection deleteConnection = databaseConnection.connectDB();
        PreparedStatement deleteStatement = deleteConnection.prepareStatement(deleteQuery);
        deleteStatement.setInt(1,selected.getItemID());
        deleteStatement.setInt(2,currentOrder.getOrderID());
        deleteStatement.execute();
        deleteConnection.close();

        String updateQuery = "UPDATE ordert SET ordert.totalPrice = ordert.totalPrice - ? WHERE ordert.orderID = ?";
        Connection updateConnection = databaseConnection.connectDB();
        PreparedStatement updateStatement = updateConnection.prepareStatement(updateQuery);
        updateStatement.setDouble(1,selected.getItemPrice());
        updateStatement.setInt(2,currentOrder.getOrderID());
        updateStatement.execute();
        updateConnection.close();

        currentOrder.setOrderTotalPrice(currentOrder.getOrderTotalPrice() - selected.getItemPrice());
        updateOrderItem();
    }

    @FXML
    void finalizeOrder(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        ArrayList<Integer> currentOrderItemIDs = getCurrentOrderItems();
        updateItemQuantities(currentOrderItemIDs);
        String customerName = currentUser.getFirstName() + " " + currentUser.getLastName();
        ReceiptWindowController.setData(customerName,currentOrder.getOrderID(),currentOrder.getOrderTotalPrice(),currentOrderItemIDs);

        Stage receiptStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Driver.class.getResource("reciptWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        receiptStage.setTitle("Receipt");
        receiptStage.setScene(scene);
        receiptStage.setResizable(false);
        receiptStage.show();

        newOrder();
        updateOrderItem();
    }

    private void updateItemQuantities(ArrayList<Integer> currentOrderItemIDs) throws SQLException, ClassNotFoundException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection updateConnection = databaseConnection.connectDB();
        String updateQuery = "UPDATE items SET Quantity = Quantity - 1 WHERE itemID = ?";
        PreparedStatement updateStatement = updateConnection.prepareStatement(updateQuery);
        for (Integer itemID : currentOrderItemIDs){
            updateStatement.setInt(1,itemID);
            updateStatement.execute();
        }
        updateConnection.close();
    }

    private ArrayList<Integer> getCurrentOrderItems() throws SQLException, ClassNotFoundException {
        ArrayList<Integer> orderItemsIDs = new ArrayList<>();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection selectItemIDConnection = databaseConnection.connectDB();
        String selectItemIDQuery = "SELECT itemID FROM ecommerce.item_order WHERE orderID = ?";
        PreparedStatement selectItemIDStatement = selectItemIDConnection.prepareStatement(selectItemIDQuery);
        selectItemIDStatement.setInt(1,currentOrder.getOrderID());
        ResultSet selectItemIDResult = selectItemIDStatement.executeQuery();
        while (selectItemIDResult.next()){
            orderItemsIDs.add(selectItemIDResult.getInt(1));
        }
        selectItemIDConnection.close();
        return orderItemsIDs;
    }

    // SHOW ORDER ITEMS
    void updateOrderItem() throws SQLException, ClassNotFoundException {
        ObservableList<ItemOrder> data = FXCollections.observableArrayList();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection selectConnection = databaseConnection.connectDB();
        String selectQuery = "SELECT item_order.itemID, items.IName, items.Price FROM item_order INNER JOIN items WHERE item_order.itemID = items.itemID AND item_order.orderID = ?";
        PreparedStatement selectStatement = selectConnection.prepareStatement(selectQuery);
        selectStatement.setInt(1,currentOrder.getOrderID());
        ResultSet selectResult = selectStatement.executeQuery();
        while (selectResult.next()){
            data.add(new ItemOrder(selectResult.getInt(1),selectResult.getString(2),selectResult.getDouble(3)));
        }
        itemOrderTable.setItems(data);
    }

    private void setValueFactoryOrderItems() {
        itemOrderTable_itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemOrderTable_itemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
    }


    // NEW ORDERS
    @FXML
    void newOrder(ActionEvent event) throws SQLException, ClassNotFoundException {
        newOrder();
    }

    void newOrder() throws SQLException, ClassNotFoundException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection insertConnection = connectNow.connectDB();
        System.out.println("DB CONNECTION SUCCESSFUL");
        PreparedStatement ps = insertConnection.prepareStatement("INSERT INTO ordert (dateOfOrder, totalPrice, customerID) " +
                "VALUES (?, ?, ?)");
        ps.setDate(1, Date.valueOf(LocalDate.now()));
        ps.setDouble(2,0.0);
        ps.setInt(3,currentUser.getID());
        ps.execute();
        insertConnection.close();

        String selectLatestRecordSQL = "SELECT * FROM ordert ORDER BY orderID DESC LIMIT 1";
        Connection selectConnection = connectNow.connectDB();
        Statement selectStatment = selectConnection.createStatement();
        ResultSet selectResult = selectStatment.executeQuery(selectLatestRecordSQL);
        if (selectResult.next()){
            currentOrder = new Order(selectResult.getInt(1),selectResult.getDate(2),selectResult.getDouble(3),selectResult.getInt(4));
        }
        selectConnection.close();

    }

    private void addItemToOrder(Item item){
        if (currentOrder == null){
            try {
                newOrder();
                System.out.println("NEW ORDER WITH ID:" + currentOrder.getOrderID() + " FOR " + currentOrder.getCustomerID());
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection insertConnection = connectNow.connectDB();
            System.out.println("DB CONNECTION SUCCESSFUL");
            PreparedStatement insertStatement = insertConnection.prepareStatement("INSERT INTO item_order (itemID, orderID) " +
                    "VALUES (?, ?)");
            insertStatement.setInt(1, item.getId());
            insertStatement.setInt(2, currentOrder.getOrderID());
            insertStatement.execute();
            insertConnection.close();

            Connection updateConnection = connectNow.connectDB();
            System.out.println("DB CONNECTION SUCCESSFUL");
            PreparedStatement updateStatement = updateConnection.prepareStatement("UPDATE ordert SET ordert.totalPrice = ? WHERE ordert.orderID = ?");
            double newPrice = currentOrder.getOrderTotalPrice() + item.getPrice();
            currentOrder.setOrderTotalPrice(newPrice);
            updateStatement.setDouble(1,newPrice);
            updateStatement.setInt(2,currentOrder.getOrderID());
            updateStatement.execute();
            updateConnection.close();

            System.out.println("ADDED" + item.getId() + " : " + item.getColor() + " " + item.getName());
        }catch (SQLIntegrityConstraintViolationException duplicate){
            Alert alert = new Alert(Alert.AlertType.WARNING,"ITEM ALREADY IN ORDER", ButtonType.CLOSE);
            alert.show();
        }catch (SQLException | ClassNotFoundException exception){
            exception.printStackTrace();
        }
        currentOrder.setOrderTotalPrice(currentOrder.getOrderTotalPrice() + item.getPrice());

    }

}

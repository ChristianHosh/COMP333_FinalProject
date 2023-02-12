package com.example.comp333_finalproject.Controllers;

import com.example.comp333_finalproject.Classes.Customer;
import com.example.comp333_finalproject.Classes.DatabaseConnection;
import com.example.comp333_finalproject.Classes.Item;
import com.example.comp333_finalproject.Driver;
import com.example.comp333_finalproject.TableClasses.CustomerOrder;
import com.example.comp333_finalproject.TableClasses.ItemOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminController {

    // FXML FX:IDS

    // SIDE MENU BUTTONS FXML
    @FXML
    private Button button_customers;

    @FXML
    private Button button_items;

    @FXML
    private Button button_orders;

    // CUSTOMER TABLE FXML
    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, String> customer_tCol_building;

    @FXML
    private TableColumn<Customer, String> customer_tCol_city;

    @FXML
    private TableColumn<Customer, String> customer_tCol_fName;

    @FXML
    private TableColumn<Customer, String> customer_tCol_id;

    @FXML
    private TableColumn<Customer, String> customer_tCol_lName;

    @FXML
    private TableColumn<Customer, String> customer_tCol_mobile1;

    @FXML
    private TableColumn<Customer, String> customer_tCol_mobile2;

    @FXML
    private TableColumn<Customer, String> customer_tCol_street;

    @FXML
    private TextField textfield_searchAddress;

    @FXML
    private TextField textfield_searchID;

    @FXML
    private TextField textfield_searchName;

    // ITEM TABLE FXML
    @FXML
    private TextField item_textfield_searchBrand;

    @FXML
    private TextField item_textfield_searchColor;

    @FXML
    private TextField item_textfield_searchName;

    @FXML
    private TableView<Item> itemsTable;

    @FXML
    private TableColumn<Item, String> items_tCol_brand;

    @FXML
    private TableColumn<Item, String> items_tCol_color;

    @FXML
    private TableColumn<Item, Integer> items_tCol_id;

    @FXML
    private TableColumn<Item, String> items_tCol_name;

    @FXML
    private TableColumn<Item, Float> items_tCol_price;

    @FXML
    private TableColumn<Item, Integer> items_tCol_quantity;

    @FXML
    private TableColumn<CustomerOrder, Integer> order_tCol_customerID;

    @FXML
    private TableColumn<CustomerOrder, Integer> order_tCol_orderID;

    @FXML
    private TableColumn<CustomerOrder, String> order_tCol_orderName;

    @FXML
    private TableColumn<CustomerOrder, Double> order_tCol_orderPrice;

    @FXML
    private TableView<CustomerOrder> ordersTable;

    @FXML
    private TextField order_textField_searchCustomer;

    @FXML
    private TextField order_textField_searchOrder;

    // ADDING ITEMS FXML
    @FXML
    private TextField textfield_itemBrand;

    @FXML
    private TextField textfield_itemColor;

    @FXML
    private TextField textfield_itemName;

    @FXML
    private TextField textfield_itemPrice;

    @FXML
    private TextField textfield_itemQuantity;

    @FXML
    private TableView<ItemOrder> itemOrderTable;

    @FXML
    private TableColumn<ItemOrder, Integer> itemOrderTable_itemID;

    @FXML
    private TableColumn<ItemOrder, String> itemOrderTable_itemName;

    @FXML
    private TableColumn<ItemOrder, Double> itemOrderTable_itemPrice;

    // PANES FXML
    @FXML
    private AnchorPane pane_customers;

    @FXML
    private AnchorPane pane_items;

    @FXML
    private AnchorPane pane_orders;


    @FXML
    public void initialize() {

        setValueFactoryCustomers();
        setCustomerSearchListeners();

        setValueFactoryItems();
        setItemSearchListeners();

        setValueFactoryCustomerOrders();
        setCustomerOrdersSearchListeners();
        setValueFactoryOrderItems();
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            try {
                setItemOrderTableData(ordersTable.getSelectionModel().getSelectedItem().getOrderID());
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    // ORDER ITEMS TABLE
    private void setValueFactoryOrderItems() {
        itemOrderTable_itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        itemOrderTable_itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemOrderTable_itemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
    }

    private void setItemOrderTableData(int orderID) throws SQLException, ClassNotFoundException {
        ObservableList<ItemOrder> data = FXCollections.observableArrayList();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection selectConnection = databaseConnection.connectDB();
        String selectQuery = "SELECT item_order.itemID, items.IName, items.Price FROM item_order INNER JOIN items WHERE item_order.itemID = items.itemID AND item_order.orderID = ?";
        PreparedStatement selectStatement = selectConnection.prepareStatement(selectQuery);
        selectStatement.setInt(1,orderID);
        ResultSet selectResult = selectStatement.executeQuery();
        while (selectResult.next()){
            data.add(new ItemOrder(selectResult.getInt(1),selectResult.getString(2),selectResult.getDouble(3)));
        }
        itemOrderTable.setItems(data);
    }

    // SETTING SEARCH LISTENERS
    private void setItemSearchListeners() {
        try{
            ObservableList<Item> items = setItemTable();
            item_textfield_searchName.textProperty().addListener((observable, oldValue, newValue) ->
                    itemsTable.setItems(filterListItemName(items, newValue)));
            item_textfield_searchBrand.textProperty().addListener((observable, oldValue, newValue) ->
                    itemsTable.setItems(filterListItemBrand(items, newValue)));
            item_textfield_searchColor.textProperty().addListener((observable, oldValue, newValue) ->
                    itemsTable.setItems(filterListItemColor(items, newValue)));
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void setCustomerSearchListeners() {
        try{
            ObservableList<Customer> customers = setCustomerTable();
            textfield_searchName.textProperty().addListener((observable, oldValue, newValue) ->
                    customersTable.setItems(filterListName(customers, newValue)));
            textfield_searchAddress.textProperty().addListener((observable, oldValue, newValue) ->
                    customersTable.setItems(filterListAddress(customers, newValue)));
            textfield_searchID.textProperty().addListener((observable, oldValue, newValue) ->
                    customersTable.setItems(filterListID(customers, newValue)));
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void setCustomerOrdersSearchListeners(){
        try{
            ObservableList<CustomerOrder> customerOrders = setOrdersTable();
            order_textField_searchCustomer.textProperty().addListener((observable, oldValue, newValue) ->
                    ordersTable.setItems(filterListOrderCustomerID(customerOrders, newValue)));
            order_textField_searchOrder.textProperty().addListener((observable, oldValue, newValue) ->
                    ordersTable.setItems(filterListOrderID(customerOrders, newValue)));
        } catch (SQLException | ClassNotFoundException sqlException) {
            sqlException.printStackTrace();
        }
    }

    // SEARCH ORDERS
    private ObservableList<CustomerOrder> filterListOrderID(ObservableList<CustomerOrder> list, String searchText) {
        List<CustomerOrder> filteredList = new ArrayList<>();
        for (CustomerOrder customer : list){
            if(searchFindOrderID(customer, searchText)) filteredList.add(customer);
        }
        return FXCollections.observableList(filteredList);
    }

    private boolean searchFindOrderID(CustomerOrder customerOrder, String searchText) {
        return (String.valueOf(customerOrder.getOrderID()).contains(searchText.toLowerCase()));
    }

    private ObservableList<CustomerOrder> filterListOrderCustomerID(ObservableList<CustomerOrder> list, String searchText) {
        List<CustomerOrder> filteredList = new ArrayList<>();
        for (CustomerOrder customerOrder : list){
            if(searchFindOrderCustomerID(customerOrder, searchText)) filteredList.add(customerOrder);
        }
        return FXCollections.observableList(filteredList);
    }

    private boolean searchFindOrderCustomerID(CustomerOrder customerOrder, String searchText) {
        return (String.valueOf(customerOrder.getCustomerID()).contains(searchText.toLowerCase()));
    }

    // ORDERS TABLE
    private void setValueFactoryCustomerOrders() {
        order_tCol_customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        order_tCol_orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        order_tCol_orderPrice.setCellValueFactory(new PropertyValueFactory<>("orderPrice"));
        order_tCol_orderName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    }

    private ObservableList<CustomerOrder> setOrdersTable() throws SQLException, ClassNotFoundException{
        ObservableList<CustomerOrder> data = FXCollections.observableArrayList();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.connectDB();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ordert.orderID, ordert.totalPrice, customer.customer_id, customer.customer_first_name, customer.customer_last_name FROM customer INNER JOIN ordert \n" +
                "WHERE ordert.customerID = customer.customer_id;");
        while (resultSet.next()){
            data.add(new CustomerOrder(resultSet.getInt(1),resultSet.getInt(3), resultSet.getDouble(2), resultSet.getString(4), resultSet.getString(5)));
        }
        ordersTable.setItems(data);
        return data;
    }

    // CUSTOMERS TABLE
    private void setValueFactoryCustomers() {
        customer_tCol_id.setCellValueFactory(new PropertyValueFactory<>("ID"));
        customer_tCol_fName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        customer_tCol_lName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        customer_tCol_city.setCellValueFactory(new PropertyValueFactory<>("city"));
        customer_tCol_street.setCellValueFactory(new PropertyValueFactory<>("street"));
        customer_tCol_building.setCellValueFactory(new PropertyValueFactory<>("building"));
        customer_tCol_mobile1.setCellValueFactory(new PropertyValueFactory<>("mobile1"));
        customer_tCol_mobile2.setCellValueFactory(new PropertyValueFactory<>("mobile2"));
    }

    private ObservableList<Customer> setCustomerTable() throws SQLException, ClassNotFoundException {
        ObservableList<Customer> data = FXCollections.observableArrayList(Driver.getCustomerList());
        customersTable.setItems(data);
        return data;
    }

    // ITEMS TABLE
    private void setValueFactoryItems() {
        items_tCol_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        items_tCol_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        items_tCol_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        items_tCol_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        items_tCol_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        items_tCol_color.setCellValueFactory(new PropertyValueFactory<>("color"));
    }

    private ObservableList<Item> setItemTable() throws SQLException, ClassNotFoundException {
        ObservableList<Item> data = FXCollections.observableArrayList(Driver.getItemList()) ;
        itemsTable.setItems(data);
        return data;
    }

    // SEARCH CUSTOMERS
    private boolean searchFindName(Customer customer, String searchText){
        return (customer.getFirstName().toLowerCase().contains(searchText.toLowerCase())) ||
                (customer.getLastName().toLowerCase().contains(searchText.toLowerCase()));
    }
    private ObservableList<Customer> filterListName(List<Customer> list, String searchText){
        List<Customer> filteredList = new ArrayList<>();
        for (Customer customer : list){
            if(searchFindName(customer, searchText)) filteredList.add(customer);
        }
        return FXCollections.observableList(filteredList);
    }

    private boolean searchFindID(Customer customer, String searchText){
        return (String.valueOf(customer.getID()).contains(searchText.toLowerCase()));
    }
    private ObservableList<Customer> filterListID(ObservableList<Customer> list, String searchText) {
        List<Customer> filteredList = new ArrayList<>();
        for (Customer customer : list){
            if(searchFindID(customer, searchText)) filteredList.add(customer);
        }
        return FXCollections.observableList(filteredList);
    }

    private boolean searchFindAddress(Customer customer, String searchText){
        return (customer.getCity().toLowerCase().contains(searchText.toLowerCase())) ||
               (customer.getStreet().toLowerCase().contains(searchText.toLowerCase()) ||
               (customer.getBuilding().toLowerCase().contains(searchText.toLowerCase())));
    }
    private ObservableList<Customer> filterListAddress(ObservableList<Customer> list, String searchText) {
        List<Customer> filteredList = new ArrayList<>();
        for (Customer customer : list){
            if(searchFindAddress(customer, searchText)) filteredList.add(customer);
        }
        return FXCollections.observableList(filteredList);
    }

    // SEARCH ITEMS
    private boolean searchFindItemName(Item item, String searchText){
        return (item.getName().toLowerCase().contains(searchText.toLowerCase()));
    }
    private ObservableList<Item> filterListItemName(ObservableList<Item> list, String searchText) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : list){
            if(searchFindItemName(item, searchText)) filteredList.add(item);
        }
        return FXCollections.observableList(filteredList);
    }

    private boolean searchFindItemBrand(Item item, String searchText){
        return (item.getBrand().toLowerCase().contains(searchText.toLowerCase()));
    }
    private ObservableList<Item> filterListItemBrand(ObservableList<Item> list, String searchText) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : list){
            if(searchFindItemBrand(item, searchText)) filteredList.add(item);
        }
        return FXCollections.observableList(filteredList);
    }

    private boolean searchFindItemColor(Item item, String searchText){
        return (item.getColor().toLowerCase().contains(searchText.toLowerCase()));
    }
    private ObservableList<Item> filterListItemColor(ObservableList<Item> list, String searchText) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : list){
            if(searchFindItemColor(item, searchText)) filteredList.add(item);
        }
        return FXCollections.observableList(filteredList);
    }

    // SWITCHING TABS
    @FXML
    void openCustomersMenu(ActionEvent event) {
        pane_customers.setVisible(true);
        button_customers.setStyle("-fx-border-color: #88B4EF; -fx-border-width: 2; -fx-text-fill: #88B4EF");
        button_items.setStyle("");
        button_orders.setStyle("");
        pane_items.setVisible(false);
        pane_orders.setVisible(false);
    }

    @FXML
    void openOrdersMenu(ActionEvent event){
        pane_orders.setVisible(true);
        button_orders.setStyle("-fx-border-color: #88B4EF; -fx-border-width: 2; -fx-text-fill: #88B4EF");
        button_customers.setStyle("");
        button_items.setStyle("");
        pane_customers.setVisible(false);
        pane_items.setVisible(false);
    }

    @FXML
    void openItemsMenu(ActionEvent event) {
        pane_items.setVisible(true);
        button_items.setStyle("-fx-border-color: #88B4EF; -fx-border-width: 2; -fx-text-fill: #88B4EF");
        button_customers.setStyle("");
        button_orders.setStyle("");
        pane_orders.setVisible(false);
        pane_customers.setVisible(false);
    }

    // ADDING AN ITEM
    String addImageToItem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        Stage thisStage = (Stage) button_items.getScene().getWindow();
        File chosenFile = fileChooser.showOpenDialog(thisStage);
//        return null;
        String path = chosenFile.getAbsolutePath();
//        path = path.replace("\\","\\\\");
        System.out.println("PATH: " + path);
        return path;
    }

    @FXML
    void addItem(ActionEvent event) {
        String itemName = textfield_itemName.getText();
        String itemBrand = textfield_itemBrand.getText();
        String itemColor = textfield_itemColor.getText();
        String imagePath = addImageToItem();
        int itemQuantity = 0;
        double itemPrice = 0.0;
        try{
            itemQuantity = Integer.parseInt(textfield_itemQuantity.getText());
        }catch (NumberFormatException e){
            textfield_itemQuantity.setText("");
            Alert alert = new Alert(Alert.AlertType.WARNING,"QUANTITY SHOULD BE AN INTEGER",ButtonType.CLOSE);
            alert.show();
            return;
        }
        try {
            itemPrice = Double.parseDouble(textfield_itemPrice.getText());
        }catch (NumberFormatException e){
            textfield_itemPrice.setText("");
            Alert alert = new Alert(Alert.AlertType.WARNING,"PRICE SHOULD BE A DOUBLE",ButtonType.CLOSE);
            alert.show();
            return;
        }
        for (String checkString : Arrays.asList(itemName,itemBrand,itemColor)){
            if (checkString.isBlank()){
                Alert alert = new Alert(Alert.AlertType.WARNING,"YOU CAN'T HAVE AN EMPTY FIELD",ButtonType.CLOSE);
                alert.show();
                return;
            }
        }
        try {
            if (AddItemToDatabase(itemName, itemBrand, itemQuantity, itemPrice, itemColor, imagePath)){
                setItemTable();
            }
        }catch (SQLException | ClassNotFoundException e){
            Alert alert = new Alert(Alert.AlertType.WARNING,"DATABASE ERROR",ButtonType.CLOSE);
            alert.show();
        }

    }

    private boolean AddItemToDatabase(String itemName, String itemBrand, int itemQuantity, double itemPrice, String itemColor, String imagePath) throws SQLException, ClassNotFoundException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connection = connectNow.connectDB();
        System.out.println("DB CONNECTION SUCCESSFUL");
        PreparedStatement ps = connection.prepareStatement("INSERT INTO items (IName, Brand, Quantity, Price, Color, ImagePath) " +
                "VALUES (?, ?, ?, ?, ?, ?)");
        ps.setString(1,itemName);
        ps.setString(2,itemBrand);
        ps.setInt(3,itemQuantity);
        ps.setDouble(4,itemPrice);
        ps.setString(5,itemColor);
        ps.setString(6,imagePath);
        try {
            ps.execute();
        }catch (Exception e){
            return false;
        }
        connection.close();
        return true;


    }



}

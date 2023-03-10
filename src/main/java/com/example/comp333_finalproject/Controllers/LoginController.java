package com.example.comp333_finalproject.Controllers;

import com.example.comp333_finalproject.Classes.Customer;
import com.example.comp333_finalproject.Classes.DatabaseConnection;
import com.example.comp333_finalproject.Driver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private Label incorrectInfoLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextfield;

    @FXML
    private TextField usernameTextfield;

    @FXML
    void login(ActionEvent event){
        String username = usernameTextfield.getText();
        String password = passwordTextfield.getText();
        try {
            if (isAdmin(username, password))
                openAdminStage();
            else if (userExists(username, password)) {
                Customer currentCustomer = getCustomer(username);
                if (currentCustomer == null)
                    wrongLoginInfo();
                openUserStage(currentCustomer);
            }else {
                wrongLoginInfo();
            }
        }catch (ClassNotFoundException | IOException | SQLException exception){
            incorrectInfoLabel.setText("HARD ERROR");
            usernameTextfield.setText("");
            passwordTextfield.setText("");
            exception.printStackTrace();
        }


    }

    @FXML
    void register(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Driver.class.getResource("registerationWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Registration");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        Stage thisStage = (Stage) loginButton.getScene().getWindow();
        thisStage.close();
    }

    private void wrongLoginInfo() {
        incorrectInfoLabel.setText("Username or password is incorrect");
        incorrectInfoLabel.setVisible(true);
        usernameTextfield.setText("");
        passwordTextfield.setText("");
    }

    private Customer getCustomer(String username) throws SQLException, ClassNotFoundException {
        for (Customer customer: Driver.getCustomerList()){
            if (customer.get_username().equals(username))
                return customer;
        }
        return null;
    }

    private void openUserStage(Customer currentCustomer) throws IOException {
        UserPanelController.setCustomer(currentCustomer);
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Driver.class.getResource("userMainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Zughayer");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        Stage thisStage = (Stage) loginButton.getScene().getWindow();
        thisStage.close();
    }

    private boolean isAdmin(String username, String password) {
        return (username.equals("admin") && password.equals("admin"));
    }

    private void openAdminStage() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Driver.class.getResource("adminPanelWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("COMP333 Project");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        Stage thisStage = (Stage) loginButton.getScene().getWindow();
        thisStage.close();
    }

    private boolean userExists(String username, String password) throws SQLException, ClassNotFoundException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connection = connectNow.connectDB();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(1) FROM `customer` WHERE `customer_username` = ? AND `customer_password` = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet result = ps.executeQuery();
            if(result.next()){
                return result.getInt(1) == 1;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}

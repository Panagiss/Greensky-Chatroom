package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.panagis.chatroom.db.DBService;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    public JFXPasswordField password = new JFXPasswordField();
    @FXML
    public JFXTextField username = new JFXTextField();
    @FXML
    public JFXButton loginBtn = new JFXButton();
    @FXML
    public JFXButton signUpBtn = new JFXButton();
    @FXML
    public JFXButton closeBtn = new JFXButton();

    public LoginController(){
        //connect to DB
        try {
            DBService.makeDBConnection();
        }catch (SQLException | ClassNotFoundException e){
            System.out.println("DB connection error");
            e.printStackTrace();
            //must print error to users
        }
    }
    public void initialize(){
        RequiredFieldValidator validator =new RequiredFieldValidator();
        validator.setMessage("Input Required");
        password.getValidators().add(validator);
        username.getValidators().add(validator);

        password.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(!t1){
                password.validate();
            }
        } );
        username.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(!t1){
                username.validate();
            }
        } );
        loginBtn.disableProperty().bind(Bindings.isEmpty(password.textProperty()).or(Bindings.isEmpty(username.textProperty()) ) );

    }

    public void login() throws IOException{
        //must validate user
        if(!DBService.getDataFromDB(username.getText(),password.getText())){
            System.out.println("User not found");
            return; //must print error to users
        }

        FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/MyRoom.fxml"));
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        RoomController controller = loader.getController();
        controller.initialize(username.getText());
    }

    public void signUp(ActionEvent actionEvent) throws IOException {
        if(!DBService.insertDataToDB(username.getText(),password.getText())){
            System.out.println("Error occurred, maybe users exists");
            return; //must print error to users
        }

        FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/MyRoom.fxml"));
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        RoomController controller = loader.getController();
        controller.initialize(username.getText());
    }
}

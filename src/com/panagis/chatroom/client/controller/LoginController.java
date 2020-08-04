package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

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

    public LoginController(){}
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

    public void login() throws IOException {
        //connect to DB
        //must validate user

        FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/MyRoom.fxml"));
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        RoomController controller = loader.getController();
        controller.initialize(username.getText());
    }

}

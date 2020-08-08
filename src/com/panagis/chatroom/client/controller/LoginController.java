package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.panagis.chatroom.client.main.MainClient.SERVER_IP;
import static com.panagis.chatroom.client.main.MainClient.SERVER_PORT;

public class LoginController {
    private final Socket socket;
    private final PrintWriter toServer;
    private final BufferedReader fromServer;

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

    public LoginController() throws IOException {
        socket = new Socket(SERVER_IP,SERVER_PORT);
        toServer =new PrintWriter(socket.getOutputStream(),true);
        fromServer =new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        //send user info to server for validation
        toServer.println("1");// 1 is for login
        toServer.println(username.getText());
        toServer.println(password.getText());
        System.out.println("DEBUG info sent");
        if(fromServer.readLine().equals("0")){
            //wrong username or password
            System.out.println("Wrong info, exiting...");
            return;
        }else {
            System.out.println("Login Successful");
        }

        System.out.println("DEBUG 2");
        FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/MyRoom.fxml"));
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        RoomController controller = loader.getController();
        controller.initialize(username.getText(),toServer,fromServer);
    }

    public void signUp(ActionEvent actionEvent) throws IOException {
        //send user info to server for signing up
        toServer.println("0"); // 0 is for sign up
        toServer.println(username.getText());
        toServer.println(password.getText());
        if(fromServer.readLine().equals("0")){
            //error or username exists
            System.out.println("Username already exists or an error occurred, exiting...");
            return;
        }else {
            System.out.println("Sign up Successful");
        }

        FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/MyRoom.fxml"));
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        RoomController controller = loader.getController();
        controller.initialize(username.getText(),toServer,fromServer);
    }
}

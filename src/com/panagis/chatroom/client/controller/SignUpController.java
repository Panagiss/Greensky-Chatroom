package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SignUpController {

    @FXML
    public JFXTextField username;
    @FXML
    public JFXPasswordField password;
    @FXML
    public JFXTextField roomPass;
    @FXML
    public JFXButton signUpBtn;
    @FXML
    public Label errorMsg;
    @FXML
    public JFXButton backBtn;

    boolean b=true;

    Socket serverSocket;
    PrintWriter toServer;
    BufferedReader fromServer;

    public SignUpController(){}

    public void initialize(Socket serverSocket) throws IOException {
        this.serverSocket =serverSocket;
        if(this.serverSocket==null){System.out.println("NULL ERROR sign up 1");}
        toServer = new PrintWriter(this.serverSocket.getOutputStream(),true);
        fromServer = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));

        RequiredFieldValidator validator =new RequiredFieldValidator();
        validator.setMessage("Input Required");
        password.getValidators().add(validator);
        username.getValidators().add(validator);
        roomPass.getValidators().add(validator);

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
        roomPass.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(!t1){
                roomPass.validate();
            }
        } );
        signUpBtn.disableProperty().bind(Bindings.isEmpty(password.textProperty()).or(Bindings.isEmpty(username.textProperty()) ) );
    }


    public void signUp(ActionEvent actionEvent) throws IOException {
        //send user info to server for signing up
        toServer.println("0"); // 0 is for sign up
        toServer.println(username.getText());
        toServer.println(password.getText());
        toServer.println("theboys");
        if(fromServer.readLine().equals("0")){
            //error or username exists
            System.out.println("Username already exists or an error occurred, try again");
            if(!b){
                username.setText(null);
                password.setText(null);
                roomPass.setText(null);
                errorMsg.setText("Invalid info, remember username must be unique");
                b=true;
            }else {
                username.setText(null);
                password.setText(null);
                roomPass.setText(null);
                errorMsg.setText("Invalid info, check Room Pass ID and please try again");
                b=false;
            }
        }else {
            System.out.println("Sign up Successful");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/MyRoom.fxml"));
            Stage window = (Stage) signUpBtn.getScene().getWindow();
            window.getScene().setRoot(loader.load());
            window.show();

            RoomController controller = loader.getController();
            if(serverSocket==null){System.out.println("NULL ERROR sign up");}
            controller.initialize(username.getText(), serverSocket);
        }
    }

    public void goBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/Login.fxml"));
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        LoginController controller = loader.getController();
        controller.initialize(serverSocket);
    }
}

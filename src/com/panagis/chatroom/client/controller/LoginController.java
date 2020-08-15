package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.panagis.chatroom.client.main.MainClient;
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


public class LoginController {
    private Socket serverSocket;
    private PrintWriter toServer;
    private BufferedReader fromServer;

    @FXML
    public JFXPasswordField password = new JFXPasswordField();
    @FXML
    public JFXTextField username = new JFXTextField();
    @FXML
    public JFXButton loginBtn = new JFXButton();
    @FXML
    public JFXButton signUpBtn = new JFXButton();
    @FXML
    public Label errorMsg = new Label();

    public LoginController(){}

    public void initialize(Socket serverSocket) throws IOException {

        this.serverSocket = serverSocket;

        if(this.serverSocket==null){System.out.println("NULL ERROR1"); return;}

        //check if socket is closed
        if(serverSocket.isClosed()){
            System.out.println("Socket was closed\n");
            serverSocket = new Socket(MainClient.SERVER_IP,MainClient.SERVER_PORT);
            this.serverSocket = serverSocket;
        }

        System.out.println("DEBUG Login controller initialized");
        this.toServer =new PrintWriter(serverSocket.getOutputStream(),true);
        this.fromServer =new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

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
            System.out.println("Wrong info, try again");
            username.setText(null);
            password.setText(null);
            errorMsg.setText("Wrong login info, please try again");
        }else {
            System.out.println("Login Successful");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/MyRoom.fxml"));
            Stage window = (Stage) loginBtn.getScene().getWindow();
            window.getScene().setRoot(loader.load());
            window.show();

            RoomController controller = loader.getController();
            controller.initialize(username.getText(),serverSocket);
        }
    }

    public void signUp(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/SignUp.fxml"));
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        SignUpController controller = loader.getController();
        if(serverSocket==null){System.out.println("NULL ERROR");}
        controller.initialize(serverSocket);
    }
}

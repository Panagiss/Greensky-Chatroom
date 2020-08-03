package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.VBox;

public class RoomController {

    @FXML
    public VBox messagesVbox;
    @FXML
    public VBox usersVbox;
    @FXML
    public JFXTextArea textToSend;
    @FXML
    public JFXButton btnSend;

    public RoomController(){}

    public void initialize(){

    }

    public void send(){
        JFXTextField msg =new JFXTextField("Me: "+textToSend.getText());
        //msg.setStyle("-fx-background-color: #e8574d");
        msg.setStyle("-fx-background-color: #052afa;" + " -fx-alignment : baseline-right");
        messagesVbox.setAlignment(Pos.TOP_RIGHT);
        messagesVbox.getChildren().add(msg);
    }
}

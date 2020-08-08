package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static com.panagis.chatroom.client.main.MainClient.SERVER_IP;
import static com.panagis.chatroom.client.main.MainClient.SERVER_PORT;

public class RoomController {
    private PrintWriter toServer;

    @FXML
    public VBox messagesVbox;
    @FXML
    public VBox usersVbox;
    @FXML
    public JFXTextArea textToSend;
    @FXML
    public JFXButton btnSend;

    public RoomController() {}

    public void initialize(String name,PrintWriter toServer,BufferedReader fromServer) {
        this.toServer =toServer;

        //toServer.println(name); //send username to server
        Service<Void> chatThread = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        JSONObject json = new JSONObject();
                        JSONParser jsonParser = new JSONParser();
                        ArrayList<String> list;
                        String res = new String();
                        System.out.println("DEBUG LOL");
                        try {
                            while (true){
                                System.out.println("DEBUG LOL 2");
                                res = fromServer.readLine();
                                System.out.println("DEBUG "+res);
                                json= (JSONObject) jsonParser.parse(res);
                                if(json.containsKey("exit")) break;
                                if(json.containsKey("message")) {
                                    System.out.println(json.get("name") + " said: " + json.get("message"));
                                    String finalRes = json.get("name") + " said: " + json.get("message"); //copying res to an effectively final res variable String
                                    Platform.runLater(() -> {
                                        JFXTextField msg = new JFXTextField(finalRes);
                                        msg.getStyleClass().add("clientMsg");
                                        //messagesVbox.setAlignment(Pos.TOP_RIGHT);
                                        messagesVbox.getChildren().add(msg);
                                    });
                                }else if(json.containsKey("addUser")){
                                    list= (ArrayList<String>) json.get("addUser");
                                    System.out.println("New addition: "+list);
                                    ArrayList<String> finalList = list;
                                    Platform.runLater(() -> {
                                        usersVbox.getChildren().clear();
                                        finalList.forEach(s -> {
                                            if(!s.equals(name) ) {
                                                JFXTextField textField = new JFXTextField(s);
                                                textField.getStyleClass().add("onlineUsers");
                                                textField.setEditable(false);
                                                usersVbox.getChildren().add(textField);
                                            }
                                        });

                                    });
                                }else if(json.containsKey("removeUser")) {
                                    list= (ArrayList<String>) json.get("removeUser");
                                    System.out.println(json.get("name")+" has left the chat");
                                    System.out.println("New list: "+list);
                                    ArrayList<String> finalList = list;
                                    Platform.runLater(() -> {
                                        usersVbox.getChildren().clear();
                                        finalList.forEach(s -> {
                                            if(!s.equals(name) ) {
                                                JFXTextField textField = new JFXTextField(s);
                                                textField.getStyleClass().add("onlineUsers");
                                                textField.setEditable(false);
                                                usersVbox.getChildren().add(textField);
                                            }
                                        });

                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw e;
                        }finally {
                            try {
                                fromServer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw e;
                            }
                        }
                        return null;
                    }
                };
            }
        };


        chatThread.setOnSucceeded(workerStateEvent -> {
            System.out.println("Chat Thread Succeed");
            System.out.println(chatThread.getValue());
        });
        chatThread.setOnFailed(workerStateEvent -> {
            System.out.println("Chat Thread Failed");
            chatThread.getException().printStackTrace(System.out);
        });

        chatThread.start();
    }

    public void send(){
        //send message to server
        System.out.println("DEBUG "+textToSend.getText());
        toServer.println(textToSend.getText());

        JFXTextField msg =new JFXTextField("Me: "+textToSend.getText());
        msg.getStyleClass().add("myMsg");
        //messagesVbox.setAlignment(Pos.TOP_RIGHT);
        messagesVbox.getChildren().add(msg);
        textToSend.setText(null);
    }
}

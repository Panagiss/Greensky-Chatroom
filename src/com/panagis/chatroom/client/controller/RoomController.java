package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.panagis.chatroom.client.main.MessageReceiver;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.panagis.chatroom.client.main.MainClient.SERVER_IP;
import static com.panagis.chatroom.client.main.MainClient.SERVER_PORT;

public class RoomController {
    private final Socket socket;
    private final BufferedReader fromKeyboard;
    private final PrintWriter toServer;
    private final BufferedReader fromServer;

    @FXML
    public VBox messagesVbox;
    @FXML
    public VBox usersVbox;
    @FXML
    public JFXTextArea textToSend;
    @FXML
    public JFXButton btnSend;

    public RoomController() throws IOException {
        socket = new Socket(SERVER_IP,SERVER_PORT);
        fromKeyboard = new BufferedReader(new InputStreamReader(System.in) );
        toServer =new PrintWriter(socket.getOutputStream(),true);
        fromServer =new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void initialize(String name) throws IOException {

        toServer.println(name); //send username to server
        Service<Void> thread = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        String res= null;
                        try {
                            while (true){
                                res = fromServer.readLine();
                                if(res.contains("exit")) break;
                                System.out.println(res);
                                String finalRes = res; //copying res to an effectively final res variable String
                                Platform.runLater(() -> {
                                    JFXTextField msg =new JFXTextField(finalRes);
                                    msg.getStyleClass().add("clientMsg");
                                    //messagesVbox.setAlignment(Pos.TOP_RIGHT);
                                    messagesVbox.getChildren().add(msg);
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                fromServer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                };
            }
        };

        thread.setOnSucceeded(workerStateEvent -> System.out.println("Thread Succeed"));
        thread.setOnFailed(workerStateEvent -> System.out.println("Thread Failed"));

        thread.start();
    }

    public void send(){
        //send message to server
        toServer.println(textToSend.getText());

        JFXTextField msg =new JFXTextField("Me: "+textToSend.getText());
        msg.getStyleClass().add("myMsg");
        //messagesVbox.setAlignment(Pos.TOP_RIGHT);
        messagesVbox.getChildren().add(msg);
        textToSend.setText(null);
    }
}

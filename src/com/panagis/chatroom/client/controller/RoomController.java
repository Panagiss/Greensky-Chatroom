package com.panagis.chatroom.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RoomController {

    private PrintWriter toServer;
    private Socket serverSocket;
    JSONObject jsonToSend;

    @FXML
    public VBox messagesVbox;
    @FXML
    public VBox usersVbox;
    @FXML
    public JFXTextArea textToSend;
    @FXML
    public JFXButton btnSend;
    @FXML
    public JFXButton logoutBtn;

    public RoomController() {}

    public void initialize(String name,Socket serverSocket) throws IOException {
        this.serverSocket=serverSocket;
        this.toServer = new PrintWriter(serverSocket.getOutputStream(),true);
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");


        Stage window = (Stage) btnSend.getScene().getWindow();
        window.setOnCloseRequest(e -> closeProgram());

        Service<Void> chatThread = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        JSONObject json;
                        JSONParser jsonParser = new JSONParser();
                        ArrayList<String> list;
                        String res ;
                        try {
                            while (true){
                                res = fromServer.readLine();
                                //System.out.println("\nDEBUG "+res);
                                json = (JSONObject) jsonParser.parse(res);
                                if(json.containsKey("exit") || json.containsKey("logout")) break;
                                if(json.containsKey("message")) {
                                    System.out.println("\n"+json.get("name") + " said: " + json.get("message"));
                                    String finalRes = json.get("name") + " said: " + json.get("message"); //copying res to an effectively final res variable String
                                    Platform.runLater(() -> {
                                        JFXTextField msg = new JFXTextField(finalRes);
                                        msg.getStyleClass().add("clientMsg");
                                        messagesVbox.getChildren().add(msg);
                                    });
                                }else if(json.containsKey("addUser")){
                                    list= (ArrayList<String>) json.get("addUser");
                                    System.out.println("\nNew addition: "+list);
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
                                    System.out.println("\n"+json.get("name")+" has left the chat");
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
            System.out.println("Chat Thread Succeed "+chatThread.getValue());
        });
        chatThread.setOnFailed(workerStateEvent -> {
            System.out.println("Chat Thread Failed "+formatter.format(new Date()));
            chatThread.getException().printStackTrace(System.out);
        });

        chatThread.start();
    }


    public void send(){
        jsonToSend = new JSONObject();
        jsonToSend.put("message",textToSend.getText());
        toServer.println(jsonToSend.toJSONString());

        JFXTextField msg =new JFXTextField("Me: "+textToSend.getText());
        msg.getStyleClass().add("myMsg");
        //messagesVbox.setAlignment(Pos.TOP_RIGHT);
        messagesVbox.getChildren().add(msg);
        textToSend.setText(null);
    }

    public void logout() throws IOException {
        jsonToSend = new JSONObject();
        jsonToSend.put("logout",null);
        toServer.println(jsonToSend.toJSONString());
        System.out.println("\nLogging out client");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panagis/chatroom/client/fxml/Login.fxml"));
        Stage window = (Stage) logoutBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        LoginController controller = loader.getController();
        controller.initialize(serverSocket);
    }

    private void closeProgram() {
        jsonToSend = new JSONObject();
        jsonToSend.put("exit",null);
        toServer.println(jsonToSend.toJSONString());
        System.out.println("\nExiting...");
    }
}

package com.greensky.chatroom.client.controller;

import com.greensky.chatroom.client.mainGUI.MainClient;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import java.text.SimpleDateFormat;
import java.util.Date;


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
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

    public LoginController(){}

    public void initialize(Socket serverSocket) throws IOException {

        this.serverSocket = serverSocket;

        if(this.serverSocket==null){System.out.println("NULL ERROR1"); return;}

        //check if socket is closed
        if(serverSocket.isClosed()){
            System.out.println("Socket was closed "+formatter.format(new Date())+"\n");
            serverSocket = new Socket(MainClient.SERVER_IP,MainClient.SERVER_PORT);
            this.serverSocket = serverSocket;
        }

        System.out.println("\nDEBUG Login controller initialized "+formatter.format(new Date()));
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
        String usrn=username.getText().toLowerCase().trim();
        String pswd=password.getText().toLowerCase().trim();
        usrn=usrn.replaceAll("\\s+","");
        pswd=pswd.replaceAll("\\s+","");
        if(usrn.length()>10||usrn.length()<3){
            Service<Void> aThread = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Platform.runLater(()->{
                                username.setText(null);
                                password.setText(null);
                                errorMsg.setText("This cannot be your username..");
                            });
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(()->{
                                errorMsg.setText(null);
                            });
                            return null;
                        }
                    };
                }
            };
            aThread.start();
        }else if(pswd.length()>12||pswd.length()<3){
            Service<Void> bThread = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Platform.runLater(()->{
                                username.setText(null);
                                password.setText(null);
                                errorMsg.setText("Password doesn't meet criteria");
                            });
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(()->{
                                errorMsg.setText(null);
                            });
                            return null;
                        }
                    };
                }
            };
            bThread.start();
        }else {
            //send user info to server for validation
            toServer.println("1");// 1 is for login
            toServer.println(usrn);
            toServer.println(pswd);
            System.out.println("\nDEBUG info sent, waiting for confirmation... "+formatter.format(new Date()));
            String res = fromServer.readLine();
            System.out.println("DEBUG2 " + res);
            if (res.equals("0")) {
                //wrong username or password
                System.out.println("Wrong info, try again");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(()->{
                            username.setText(null);
                            password.setText(null);
                            errorMsg.setText("Wrong login info, please try again");
                        });
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(()->{
                            errorMsg.setText(null);
                        });
                    }
                }).start();
            } else if (res.equals("1")) {
                System.out.println("Login Successful "+formatter.format(new Date()));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/greensky/chatroom/client/fxml/MyRoom.fxml"));
                Stage window = (Stage) loginBtn.getScene().getWindow();
                window.getScene().setRoot(loader.load());
                window.show();

                RoomController controller = loader.getController();
                controller.initialize(username.getText(), serverSocket);
            } else {
                System.out.println("WHAT??? "+formatter.format(new Date()));
            }
        }
    }

    public void signUp(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/greensky/chatroom/client/fxml/SignUp.fxml"));
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.getScene().setRoot(loader.load());
        window.show();

        SignUpController controller = loader.getController();
        if(serverSocket==null){System.out.println("NULL ERROR "+formatter.format(new Date()));}
        controller.initialize(serverSocket);
    }
}

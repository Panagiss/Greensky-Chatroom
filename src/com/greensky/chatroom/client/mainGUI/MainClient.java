package com.greensky.chatroom.client.mainGUI;

import com.greensky.chatroom.client.controller.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;


public class MainClient extends Application {

    public static final String SERVER_IP="127.0.0.1";
    public static final int SERVER_PORT=8080;
    public Socket serverSocket =null;

    /**
     * This is the main method. Everything starts from here.
     *
     *
     *
     * @param args Not a really necessary parameter
     */
    public static void main(String[] args) { launch(args);}

    @Override
    public void start(Stage window) {
        //pre-GUI data fetch
        Service<Void> DBRequestThread = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws IOException {
                        serverSocket = new Socket(SERVER_IP,SERVER_PORT);
                        return null;
                    }
                };
            }
        };
        DBRequestThread.setOnSucceeded(workerStateEvent -> {
            System.out.println("Pre-GUI DB Thread finished");

            FXMLLoader loader;
            try {
                loader =new FXMLLoader(getClass().getResource("/com/greensky/chatroom/client/fxml/Login.fxml"));
            }catch (Exception e){
                System.out.println("MainClass error: "+e);
                System.out.println("LOL");
                return;
            }
            window.setTitle("GREEN SKY");
            Scene loginScene= null;
            try {
                loginScene = new Scene(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            window.setScene(loginScene);
            window.setMaximized(true);

            window.show();

            LoginController controller = loader.getController();
            try {
                controller.initialize(serverSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        DBRequestThread.setOnFailed(workerStateEvent -> {System.out.println("Pre-GUI DB Thread failed "+DBRequestThread.getException().toString());
            Platform.exit();
        });

        DBRequestThread.start();


    }
}

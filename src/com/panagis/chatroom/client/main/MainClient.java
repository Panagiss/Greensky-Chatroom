package com.panagis.chatroom.client.main;

import com.panagis.chatroom.db.DBService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MainClient extends Application {

    public static final String SERVER_IP="127.0.0.1";
    public static final int SERVER_PORT=1313;

    /**
     * This is the main method. Everything starts from here.
     *
     * @param args Not a really necessary parameter
     */
    public static void main(String[] args) { launch(args);}

    @Override
    public void start(Stage window)  {
        //pre-GUI data fetch
        Service<Void> DBRequestThread = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        return null;
                    }
                };
            }
        };
        DBRequestThread.setOnSucceeded(workerStateEvent -> System.out.println("Pre-GUI DB Thread finished"));
        DBRequestThread.setOnFailed(workerStateEvent -> {System.out.println("Pre-GUI DB Thread failed");
            Platform.exit();
        });
        DBRequestThread.start();

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/panagis/chatroom/client/fxml/Login.fxml"));
        }catch (Exception e){
            System.out.println("MainClass error: "+e);
            System.out.println("LOL");
            return;
        }
        window.setTitle("GREEN SKY");
        Scene loginScene=new Scene(root);
        window.setScene(loginScene);
        window.setMaximized(true);
        window.show();
    }
}

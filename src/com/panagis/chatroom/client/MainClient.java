package com.panagis.chatroom.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainClient extends Application {

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
        //window.initStyle(StageStyle.TRANSPARENT);
        window.setTitle("GREEN SKY");
        Scene loginScene=new Scene(root);
        //loginScene.setFill(Color.TRANSPARENT);
        window.setScene(loginScene);
        window.setMaximized(true);
        window.show();
    }
}

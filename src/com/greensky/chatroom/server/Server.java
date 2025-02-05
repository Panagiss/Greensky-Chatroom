package com.greensky.chatroom.server;

import com.greensky.chatroom.db.DBService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT =8080;
    private static final ArrayList<ClientHandler> clientList =new ArrayList<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);
    public static LinkedList<String> list = new LinkedList<>();

    public static void main(String [] args) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
        ServerSocket listener = new ServerSocket(PORT);

        //make DB connection
        try {
            DBService.makeDBConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("DB connection failed, exiting.....");
            e.printStackTrace();
            return;
        }
        System.out.println("Server up and running\n\n");


        while(true){
            Socket clientSoc = listener.accept();
            System.out.println("\n\nA client tries to connect -- "+formatter.format(new Date())+"\n");

            ClientHandler clientThread = new ClientHandler(clientSoc, clientList);
            clientList.add(clientThread);

            pool.execute(clientThread);
        }

    }
}

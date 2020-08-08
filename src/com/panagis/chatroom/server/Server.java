package com.panagis.chatroom.server;

import com.panagis.chatroom.db.DBService;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.IllegalCharsetNameException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT =1313;
    private static final ArrayList<ClientHandler> clientList =new ArrayList<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);
    public static LinkedList<String> list = new LinkedList<>();

    public static void main(String [] args) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        ServerSocket listener = new ServerSocket(PORT);

        //make DB connection
        try {
            DBService.makeDBConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("DB connection failed, exiting.....");
            e.printStackTrace();
            throw e;
        }
        System.out.println("Server up and running\n\n");


        while(true){
            Socket clientSoc = listener.accept();
            System.out.println("\nA client tries to connect -- "+formatter.format(new Date())+"\n");

            ClientHandler clientThread = new ClientHandler(clientSoc, clientList);
            clientList.add(clientThread);

            pool.execute(clientThread);
            System.out.println("Thread started");

        }

    }
}

package com.panagis.chatroom.server;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
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

    public static void main(String [] args) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        JSONObject json = new JSONObject();

        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("Server up and running\n\n");

        while(true){
            Socket clientSoc = listener.accept();
            BufferedReader fromClient =new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            String username = fromClient.readLine();
            System.out.println("\n"+username+" connected -- "+formatter.format(new Date())+"\n");
            list.add(username);

            ClientHandler clientThread = new ClientHandler(clientSoc, clientList,username);
            clientList.add(clientThread);

            json.put("addUser",list);
            clientList.forEach(clientHandler -> {
                clientHandler.getToClient().println(json.toJSONString());
            });

            pool.execute(clientThread);
        }

    }
}

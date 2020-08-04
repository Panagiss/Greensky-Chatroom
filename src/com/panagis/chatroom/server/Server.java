package com.panagis.chatroom.server;

import com.panagis.chatroom.server.ClientHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT =1313;
    private static final ArrayList<ClientHandler> clientList =new ArrayList<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String [] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("Server up and running\n\n");
        while(true){
            Socket clientSoc = listener.accept();
            BufferedReader fromClient =new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            String username = fromClient.readLine();
            System.out.println("\n"+username+" connected");

            ClientHandler clientThread = new ClientHandler(clientSoc, clientList,username);
            clientList.add(clientThread);

            pool.execute(clientThread);
        }

    }
}

package com.panagis.chatroom.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private final ArrayList<ClientHandler> clientList;
    private final String username;

    public ClientHandler(Socket clientSocket,ArrayList<ClientHandler> clients, String name) throws IOException {
        this.clientSocket = clientSocket;
        this.clientList = clients;
        fromClient =new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        toClient =new PrintWriter(this.clientSocket.getOutputStream(),true);
        username=name;
    }

    @Override
    public void run() {
        try {
            while (true){
                String res= fromClient.readLine();
                if(res.contains("exit")){
                    toClient.println("exit");
                    System.out.println("Client left");
                    break;
                }
                System.out.println("Client (" +username+ ") said: "+res);
                clientList.forEach(clientHandler -> {
                    if(clientHandler.clientSocket!=this.clientSocket) {
                        clientHandler.toClient.println(username+" said: " + res);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            toClient.close();
            try {
                fromClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public BufferedReader getFromClient() {
        return fromClient;
    }

    public void setFromClient(BufferedReader fromClient) {
        this.fromClient = fromClient;
    }

    public PrintWriter getToClient() {
        return toClient;
    }

    public void setToClient(PrintWriter toClient) {
        this.toClient = toClient;
    }
}

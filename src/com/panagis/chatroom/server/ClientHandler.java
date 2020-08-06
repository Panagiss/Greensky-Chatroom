package com.panagis.chatroom.server;

import netscape.javascript.JSObject;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            while (true){
                JSONObject json = new JSONObject();
                String res= fromClient.readLine();
                if(res.contains("exit")){
                    json.put("name",username);
                    json.put("exit","");
                    toClient.println(json.toJSONString());
                    System.out.println("Client ("+json.get("name")+") left the chat -- "+formatter.format(new Date()));

                    //notify other users that this user left
                    json.remove("exit");
                    Server.list.remove(username);
                    json.put("removeUser",Server.list);
                    clientList.forEach(clientHandler -> {
                        if(clientHandler.getClientSocket()!=this.clientSocket){
                            clientHandler.getToClient().println(json.toJSONString());
                        }
                    });
                    break;
                }
                System.out.println("Client (" +username+ ") said: "+res+" -- "+formatter.format(new Date()));
                json.put("name",username);
                json.put("message",res);
                clientList.forEach(clientHandler -> {
                    if(clientHandler.clientSocket!=this.clientSocket) {
                        clientHandler.toClient.println(json.toJSONString());
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

    public String getUsername() {
        return username;
    }
}

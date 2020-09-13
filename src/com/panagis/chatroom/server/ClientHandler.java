package com.panagis.chatroom.server;

import com.panagis.chatroom.db.DBService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private String username;

    public ClientHandler(Socket clientSocket,ArrayList<ClientHandler> clients) throws IOException {
        this.clientSocket = clientSocket;
        this.clientList = clients;
        fromClient =new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        toClient =new PrintWriter(this.clientSocket.getOutputStream(),true);
        username = null;
    }

    @Override
    public void run() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
        JSONObject json = new JSONObject();
        JSONParser jsonParser=new JSONParser();

        //client validation
        String choice;
        String username = null;
        String password;
        while(true) {
            try {
                choice = fromClient.readLine();
            } catch (IOException | NullPointerException e) {
                //e.printStackTrace();
                System.out.println("Connection wasn't established cause: "+ e.toString()+" -- "+formatter.format(new Date()));
                toClient.close();
                System.out.println("toClient Closed "+formatter.format(new Date()) );
                try {
                    fromClient.close();
                    System.out.println("fromClient Closed "+formatter.format(new Date())+"\n" );
                } catch (IOException es) {
                    es.printStackTrace();
                }
                return;
            }

            try {
                if (choice.equals("0") && fromClient.readLine().equals("theboys")) { //sign up
                    username = fromClient.readLine();
                    password = fromClient.readLine();
                    System.out.println("DEBUG " + choice + ", " + username + ", " + password);
                    if (!DBService.insertDataToDB(username, password)) {
                        System.out.println("Error signing up client " + username + " -- " + formatter.format(new Date()) + "\n");
                        toClient.println("0");
                    } else {
                        System.out.println("\nNew user " + username + " signed up -- " + formatter.format(new Date()) + "\n");
                        toClient.println("1"); //user signed up
                        break;
                    }

                } else if (choice.equals("1")) { //log in
                    username = fromClient.readLine();
                    password = fromClient.readLine();
                    System.out.println("DEBUG " + choice + ", " + username + ", " + password);
                    //validate client
                    if (!DBService.getDataFromDB(username, password)) {
                        System.out.println("Client " + username + " couldn't be found in DB, access blocked -- " + formatter.format(new Date()) + "\n");
                        toClient.println("0");
                    } else {
                        System.out.println("\n" + username + " logged in successfully -- " + formatter.format(new Date()) + "\n");
                        toClient.println("1"); //user found
                        break;
                    }
                }
                else {
                    System.out.println("Option = "+ choice + " Error "+formatter.format(new Date()));
                    toClient.close();
                    System.out.println("toClient Closed "+formatter.format(new Date()) );
                    try {
                        fromClient.close();
                        System.out.println("fromClient Closed "+formatter.format(new Date())+"\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            } catch (IOException | NullPointerException e) {
                //e.printStackTrace();
                System.out.println("Connection wasn't established cause: "+ e.toString()+" -- "+formatter.format(new Date()));
                toClient.close();
                System.out.println("toClient Closed "+formatter.format(new Date()) );
                try {
                    fromClient.close();
                    System.out.println("fromClient Closed "+formatter.format(new Date())+"\n");
                } catch (IOException es) {
                    es.printStackTrace();
                }
                return;
            }
        }
        //System.out.println("\n"+username+" connected -- "+formatter.format(new Date())+"\n");
        Server.list.add(username);
        this.username=username;

        json.put("addUser",Server.list);
        //System.out.println("DEBUG "+json);
        JSONObject finalJson = json;
        clientList.forEach(clientHandler -> {
            if(clientHandler.getUsername()!=null) {
                clientHandler.getToClient().println(finalJson.toJSONString());
            }
        });
        //System.out.println("DEBUG2 New user-addition message sent\n");


        //chatting
        try {
            while (true){
                json.clear();
                String res= fromClient.readLine();
                //System.out.println("DEBUG "+res);
                json = (JSONObject) jsonParser.parse(res);
                if(json.containsKey("logout")){
                    json.clear();
                    json.put("name",username);
                    json.put("logout",null);
                    toClient.println(json.toJSONString());
                    System.out.println("\nClient ("+json.get("name")+") left the chat (logged out) -- "+formatter.format(new Date()));

                    //notify other users that this user left
                    json.remove("logout");
                    Server.list.remove(username);
                    json.put("removeUser", Server.list);
                    sendToClients(clientList,json.toJSONString());
                    break;
                }else if(json.containsKey("message")){
                    String msg= (String) json.get("message");
                    json.clear();
                    System.out.println("Client (" +username+ ") said: "+msg+" -- "+formatter.format(new Date()));
                    json.put("name",username);
                    json.put("message",msg);
                    //System.out.println("DEBUG "+jsonToSend);
                    sendToClients(clientList,json.toJSONString());
                }else if(json.containsKey("exit")){
                    json.clear();
                    json.put("name",username);
                    json.put("exit",null);
                    toClient.println(json.toJSONString());
                    System.out.println("\nClient ("+json.get("name")+") left the chat and exited -- "+formatter.format(new Date()));

                    //notify other users that this user left
                    json.remove("exit");
                    Server.list.remove(username);
                    json.put("removeUser", Server.list);
                    sendToClients(clientList,json.toJSONString());
                    break;
                }

            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Client "+username+" lost connection - cause: "+ e.toString()+" -- "+formatter.format(new Date()));
            json.clear();
            json.put("name",username);
            //notify other users that this user left
            Server.list.remove(username);
            json.put("removeUser", Server.list);
            sendToClients(clientList,json.toJSONString());
        } finally {
            toClient.close();
            System.out.println("toClient Closed "+formatter.format(new Date()) );
            try {
                fromClient.close();
                System.out.println("fromClient Closed "+formatter.format(new Date())+"\n" );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendToClients(ArrayList<ClientHandler> clientList,String msg){
        clientList.forEach(clientHandler -> {
            if(clientHandler.getClientSocket()!=this.clientSocket && clientHandler.getUsername()!=null){
                clientHandler.getToClient().println(msg);
            }
        });    }


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

    public void setUsername(String username) {
        this.username = username;
    }
}

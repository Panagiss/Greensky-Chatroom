package com.panagis.chatroom.server;

import com.panagis.chatroom.db.DBService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public ClientHandler(Socket clientSocket,ArrayList<ClientHandler> clients) throws IOException {
        this.clientSocket = clientSocket;
        this.clientList = clients;
        fromClient =new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        toClient =new PrintWriter(this.clientSocket.getOutputStream(),true);
    }

    @Override
    public void run() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        JSONObject json = new JSONObject();
        JSONParser jsonParser=new JSONParser();

        //client validation
        String choice= null;
        String username = null;
        String password = null;
        while(true) {
            try {
                choice = fromClient.readLine();
                username = fromClient.readLine();
                password = fromClient.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("DEBUG " + choice + ", " + username + ", " + password);
            try {
                if (choice.equals("0") && fromClient.readLine().equals("theboys")) { //sign up
                    if (!DBService.insertDataToDB(username, password)) {
                        System.out.println("Error signing up client " + username + " -- " + formatter.format(new Date()) + "\n");
                        toClient.println("0");
                    } else {
                        System.out.println("\nNew user " + username + " signed up -- " + formatter.format(new Date()) + "\n");
                        toClient.println("1"); //user signed up
                        break;
                    }

                } else if (choice.equals("1")) { //log in
                    //validate client
                    if (!DBService.getDataFromDB(username, password)) {
                        System.out.println("Client " + username + " couldn't be found in DB, access blocked -- " + formatter.format(new Date()) + "\n");
                        toClient.println("0");
                    } else {
                        System.out.println("\n" + username + " logged in successfully -- " + formatter.format(new Date()) + "\n");
                        toClient.println("1"); //user found
                        break;
                    }
                } else {
                    System.out.println("Option Error");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("\n"+username+" connected -- "+formatter.format(new Date())+"\n");
        Server.list.add(username);

        json.put("addUser",Server.list);
        //System.out.println("DEBUG "+json);
        JSONObject finalJson = json;
        clientList.forEach(clientHandler -> {
            clientHandler.getToClient().println(finalJson.toJSONString());
        });
        //System.out.println("DEBUG2 New user-addition message sent\n");


        //chatting
        try {
            while (true){
                json.clear();
                String res= fromClient.readLine();
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
                    JSONObject finalJson1 = json;
                    clientList.forEach(clientHandler -> {
                        if(clientHandler.getClientSocket()!=this.clientSocket){
                            clientHandler.getToClient().println(finalJson1.toJSONString());
                        }
                    });
                    break;
                }else if(json.containsKey("message")){
                    json.clear();
                    System.out.println("Client (" +username+ ") said: "+res+" -- "+formatter.format(new Date()));
                    json.put("name",username);
                    json.put("message",res);
                    //System.out.println("DEBUG "+jsonToSend);
                    JSONObject finalJson2 = json;
                    clientList.forEach(clientHandler -> {
                        if(clientHandler.clientSocket!=this.clientSocket) {
                            clientHandler.toClient.println(finalJson2.toJSONString());
                        }
                    });
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
                    JSONObject finalJson1 = json;
                    clientList.forEach(clientHandler -> {
                        if(clientHandler.getClientSocket()!=this.clientSocket){
                            clientHandler.getToClient().println(finalJson1.toJSONString());
                        }
                    });
                    break;
                }

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
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

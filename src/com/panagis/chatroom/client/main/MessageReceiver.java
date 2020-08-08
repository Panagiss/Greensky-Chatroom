package com.panagis.chatroom.client.main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class MessageReceiver implements Runnable {
    private final BufferedReader fromServer;

    public MessageReceiver(Socket socket) throws IOException {
        fromServer =new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {

        JSONObject json = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        ArrayList<String> list;
        String res= null;
        try {
            while (true){
                res = fromServer.readLine();
                System.out.println("DEBUG"+res);
                json= (JSONObject) jsonParser.parse(res);
                System.out.println("DEBUG2"+json);
                if(json.containsKey("exit")) break;
                if(json.containsKey("message")) System.out.println(json.get("name")+" said: "+json.get("message"));
                if(json.containsKey("addUser")) {
                    System.out.println("DEBUG3");
                    list= (ArrayList<String>) json.get("addUser");
                    System.out.println("New addition: "+list);
                }
                if(json.containsKey("removeUser")) {
                    list= (ArrayList<String>) json.get("removeUser");
                    System.out.println(json.get("name")+" has left the chat");
                    System.out.println("New list: "+list);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }finally {
            try {
                fromServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

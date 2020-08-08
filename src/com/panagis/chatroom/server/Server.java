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
        JSONObject json = new JSONObject();

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
            BufferedReader fromClient =new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            PrintWriter toCient=new PrintWriter(clientSoc.getOutputStream());

            String choice=fromClient.readLine();
            String username = fromClient.readLine();
            String password = fromClient.readLine();
            System.out.println("DEBUG "+choice+", "+username+", "+password);
            if(choice.equals("0")){ //sign up
                if(!DBService.insertDataToDB(username,password)){
                    System.out.println("Error signing up client "+username+" -- "+formatter.format(new Date())+"\n");
                    toCient.println("0");
                    continue;
                }else {
                    System.out.println("sign up success");
                    toCient.println("1"); //user signed up
                }

            }else if(choice.equals("1")){ //log in
                //validate client
                if(!DBService.getDataFromDB(username,password)){
                    System.out.println("Client "+ username+" couldn't be found in DB, access blocked -- "+formatter.format(new Date())+"\n");
                    toCient.println("0");
                    continue;
                }else {
                    System.out.println("login success");
                    toCient.println("1"); //user found
                }
            }else{
                System.out.println("Option Error");
                continue;
            }


            System.out.println("\n"+username+" connected -- "+formatter.format(new Date())+"\n");
            list.add(username);

            ClientHandler clientThread = new ClientHandler(clientSoc, clientList,username);
            clientList.add(clientThread);

            json.put("addUser",list);
            System.out.println("DEBUG "+json);
            System.out.println("DEBUG2 "+json.toJSONString());
            clientList.forEach(clientHandler -> {
                clientHandler.getToClient().println(json.toJSONString());
            });
            System.out.println("DEBUG3");
            pool.execute(clientThread);
            System.out.println("DEBUG4");

        }

    }
}

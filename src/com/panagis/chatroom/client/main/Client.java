package com.panagis.chatroom.client.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP="127.0.0.1";
    private static final int SERVER_PORT=1313;

    public static void main(String [] args) throws IOException {
        Socket socket = new Socket(SERVER_IP,SERVER_PORT);

        MessageReceiver messageReceiver=new MessageReceiver(socket);

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in) );
        PrintWriter toServer =new PrintWriter(socket.getOutputStream(),true);

        Thread t = new Thread(messageReceiver);
        t.start();

        //int c= keyboard.read();
        toServer.println("1");
        toServer.println(args[0]);
        toServer.println(args[1]);



        try {
            while (true) { //ayto prepei na bei sth send() toy RoomController
                String msg = keyboard.readLine();
                toServer.println(msg);
                if (msg.contains("exit")) {
                    System.out.println("Exiting...");
                    break;
                }
            }
        }finally {
            keyboard.close();
            while(true){
                if(!t.isAlive()){
                    toServer.close();
                    socket.close();
                    break;
                }
            }
        }
    }
}

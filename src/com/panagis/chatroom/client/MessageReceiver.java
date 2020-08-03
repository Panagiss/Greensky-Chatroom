package com.panagis.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReceiver implements Runnable {
    private final BufferedReader fromServer;

    public MessageReceiver(Socket socket) throws IOException {
        fromServer =new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {

        String res= null;
        try {
            while (true){
                res = fromServer.readLine();
                if(res.contains("exit")) break;
                System.out.println("Server says: "+res);
            }
        } catch (IOException e) {
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

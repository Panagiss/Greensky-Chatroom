package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReceiver implements Runnable {
    private Socket socket;
    private BufferedReader in;

    public MessageReceiver(Socket socket) throws IOException {
        this.socket = socket;
        in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {

        String res= null;
        try {
            while (true){
                res = in.readLine();
                System.out.println("Server says: "+res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

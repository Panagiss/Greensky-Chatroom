package sample;

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
        PrintWriter out =new PrintWriter(socket.getOutputStream(),true);

        new Thread(messageReceiver).start();

        while(true){
            String write =keyboard.readLine();
            out.println(write);
        }
    }
}

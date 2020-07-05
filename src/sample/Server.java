package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT =1313;
    private static ArrayList<ClientHandler> clientList =new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String [] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("Server up and running");
        while(true){
            Socket clientSoc = listener.accept();
            System.out.println("User connected");

            ClientHandler clientThread = new ClientHandler(clientSoc, clientList);
            clientList.add(clientThread);

            pool.execute(clientThread);
        }

    }
}

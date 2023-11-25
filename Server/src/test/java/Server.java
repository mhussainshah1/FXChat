import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final String[] names = {"Amir", "Ali", "Asim", "Fatima", "Marrieum"};
    private static final String[] adjs = {"the gentle", "the un-gentle", "the overwrought", "the urban"};
    private static final int PORT = 9090;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);
    public static void main(String[] args) throws IOException {
        ServerSocket listner = new ServerSocket(PORT);

        while(true){
            System.out.println("(SERVER) waiting for client connection...");
            Socket socket = listner.accept();
            System.out.println("(SERVER) connected to client!");
            ClientHandler clientThread = new ClientHandler(socket, clients);
            clients.add(clientThread);

            pool.execute(clientThread);
        }
    }

    public static String getRandomName() {
        String name = names[(int) (Math.random() * names.length)];
        String adj = adjs[(int) (Math.random() * adjs.length)];
        return name + " " + adj;
    }
}

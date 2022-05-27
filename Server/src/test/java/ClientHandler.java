import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket client;
    private final List<ClientHandler> clients;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket client, List<ClientHandler> clients) throws IOException {
        this.client = client;
        this.clients = clients;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if (request.contains("name")) {
                    out.println(Server.getRandomName());
                } else if (request.startsWith("say")) {
                    int firstSpace = request.indexOf(" ");
                    if (firstSpace != -1) {
                        outToAll(request.substring(firstSpace + 1));
                    }
                } else {
                    out.println("Type 'tell me a name' to get a random name");
                }
            }
        } catch (IOException e) {
            System.err.println("IO exception in client handler");
            System.err.println(e.getStackTrace());
            e.printStackTrace();
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void outToAll(String msg) {
        for (ClientHandler clientHandler : clients){
            clientHandler.out.println(msg);
        }
    }
}

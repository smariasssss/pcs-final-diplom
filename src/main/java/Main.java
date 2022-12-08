import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 8989;

    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream());){

                    System.out.println("Connected!");
                    String word = in.readLine();

                    Gson gson = new GsonBuilder().create();
                    String jsonList = gson.toJson(engine.search(word));
                    out.println(jsonList);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер!");
            e.printStackTrace();
        }
    }
}

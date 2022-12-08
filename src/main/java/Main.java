import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                )
                {
                    out.println("Connected!" + " Введите слово или слова, которые хотите найти");

                    BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

                    String word = in.readLine();

                    Gson gson = new Gson();

                    String pageEntry = gson.toJson(engine.search(word));

                    out.println(pageEntry);
                    System.out.println(pageEntry);
                }
            }
        } catch (
                IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Client {
    private static final int PORT = 8989;
    private static final String HOST = "localHost";

    public static void main(String[] arg) {
        try (Socket clientSocket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String word = "Бизнес, количество, руководитель";
            out.println(word);
            String json = in.readLine();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setPrettyPrinting().create();
            List<PageEntry> list = gson.fromJson(json,
                    new TypeToken<List<PageEntry>>() {
                    }.getType());
            System.out.println(list);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка!");
        }
    }
}

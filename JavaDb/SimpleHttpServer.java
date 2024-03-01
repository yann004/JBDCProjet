
package JavaDb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {

        Connection connection = Connectdb.getConnection();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        //SensorDataQueue.printQueue(); 

        server.createContext("/sensor-data", new SensorDataHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port " + port);

       // Démarrer le thread de traitement des données

        DataProcessor dataProcessor = new DataProcessor(connection);
        Thread processorThread = new Thread(dataProcessor);
        processorThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            dataProcessor.terminate(); // Demander au thread de s'arrêter
            try {
                processorThread.join(); // Attendre que le thread se termine
                connection.close(); // Fermer la connexion à la base de données
            } catch (InterruptedException | SQLException e) {
                e.printStackTrace();
            }
            server.stop(0);
            System.out.println("Server stopped");
        }));

        
    }

    static class SensorDataHandler implements HttpHandler {
       
        @Override
public void handle(HttpExchange exchange) throws IOException {
    String response = "Data Received";
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> data = parseFormData(body);

        // Détecter s'il s'agit de données de capteur ou d'actuateur
        String type = data.getOrDefault("sensorType", "unknown"); 
        if ("actuateur".equals(type) || "capteur".equals(type)) {
            SensorDataQueue.addToQueue(data); // Ajouter les données à la file
            System.out.println("Received " + type + " data: " + data);
        } else {
            response = "Invalid Data Type";
        }

        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    } else {
        exchange.sendResponseHeaders(405, -1);
        exchange.close();
    }
}


        private Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            for (String pair : formData.split("&")) {
                int idx = pair.indexOf("=");
                map.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
            return map;
        }

        
    }
}


package JavaDb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;



public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {

        Connection connection = Connectdb.getConnection();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);


        server.createContext("/sensor-data", new SensorDataHandler());
        server.createContext("/api/microcontrollers", new MicrocontrollerHandlers.ListMicrocontrollersHandler());
        server.createContext("/api/microcontrollers/add", new MicrocontrollerHandlers.AddMicrocontrollerHandler());
        server.createContext("/api/microcontrollers/update", new MicrocontrollerHandlers.UpdateMicrocontrollerHandler());
        server.createContext("/api/microcontrollers/delete", new MicrocontrollerHandlers.DeleteMicrocontrollerHandler());
 
        server.createContext("/api/Capteur", new CapteurHandlers.ListCapteurHandler());
        server.createContext("/api/Capteur/add", new CapteurHandlers.AddCapteurHandler());
        server.createContext("/api/Capteur/update", new CapteurHandlers.UpdateCapteurHandler());
        server.createContext("/api/Capteur/delete", new CapteurHandlers.DeleteCapteurHandler());

        server.createContext("/api/actuateurs", new ActuateurHandlers.ListActuateursHandler());
        server.createContext("/api/actuateurs/add", new ActuateurHandlers.AddActuateursHandler());
        server.createContext("/api/actuateurs/update", new ActuateurHandlers.UpdateActuateursHandler());
        server.createContext("/api/actuateurs/delete", new ActuateurHandlers.DeleteActuateursHandler());


        server.createContext("/api/mesure", new MesureHandlers.ListmesureHandler());
        server.createContext("/api/mesure/delete", new MesureHandlers.DeletemesureHandler());

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
           // System.out.println("Received " + type + " data: " + data);
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

       class MicrocontrollerHandlers  {

        static class ListMicrocontrollersHandler implements HttpHandler {
            Connection connection = Connectdb.getConnection();
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                // Envoyer une réponse OK pour les requêtes OPTIONS (nécessaire pour le "preflight")
                exchange.sendResponseHeaders(204, -1);
                return ;
            }

            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
                exchange.close();
                return;
            }
            
            JSONArray jsonResponse = new JSONArray();
            String response = "";
            int responseCode = 200;
    
            try {
                String sql = "SELECT * FROM Microcontrolleur";
                try (PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        JSONObject microcontroller = new JSONObject();
                        microcontroller.put("id", resultSet.getInt("microcontrolleur_id"));
                        microcontroller.put("nom", resultSet.getString("nom"));
                        microcontroller.put("details", resultSet.getString("details"));
                        jsonResponse.put(microcontroller);
                    }
                    response = jsonResponse.toString();
                }
            } catch (SQLException e) {
                response = "{\"error\":\"Error while listing microcontrollers: " + e.getMessage() + "\"}";
                responseCode = 500; // Internal Server Error
            }
    
            exchange.sendResponseHeaders(responseCode, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class AddMicrocontrollerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                // Envoyer une réponse OK pour les requêtes OPTIONS (nécessaire pour le "preflight")
                exchange.sendResponseHeaders(204, -1);
                return ;
            }

            
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Parse JSON request body

                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String jsonText = sb.toString();
    
                // Convert JSON text to Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> inputData = objectMapper.readValue(jsonText, new TypeReference<Map<String,String>>(){});
    
                String response = "";
                int responseCode = 200;
                try (Connection connection = Connectdb.getConnection()) {
                    // Insert into database
                    String sql = "INSERT INTO Microcontrolleur (nom, details) VALUES (?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setString(1, inputData.get("nom"));
                        preparedStatement.setString(2, inputData.get("details"));
                        int rowsAffected = preparedStatement.executeUpdate();
                        response = rowsAffected + " microcontrolleur ajouté.";
                    }
                } catch (SQLException e) {
                    response = "Erreur lors de l'ajout du microcontrolleur : " + e.getMessage();
                    responseCode = 500; // Server error
                }
    
                exchange.sendResponseHeaders(responseCode, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();


            } else {
                String response = "Méthode non autorisée.";
                exchange.sendResponseHeaders(405, response.getBytes().length); // 405 Method Not Allowed
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    


static class UpdateMicrocontrollerHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) { // Devrait normalement être PUT, mais les formulaires HTML standard ne supportent que POST et GET
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();

            String response = "";
            int responseCode = 200;
            try (Connection connection = Connectdb.getConnection()) {
                String[] keyValuePairs = query.split("&");
                Map<String, String> inputData = new HashMap<>();
                for (String pair : keyValuePairs) {
                    String[] entry = pair.split("=");
                    inputData.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8.name()), URLDecoder.decode(entry[1], StandardCharsets.UTF_8.name()));
                }
                String sql = "UPDATE Microcontrolleur SET nom = ?, details = ? WHERE microcontrolleur_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, inputData.get("nom"));
                    preparedStatement.setString(2, inputData.get("details"));
                    preparedStatement.setInt(3, Integer.parseInt(inputData.get("microcontrolleur_id")));
                    int rowsAffected = preparedStatement.executeUpdate();
                    response = rowsAffected + " microcontrolleur mis à jour.";
                }
            } catch (SQLException e) {
                response = "Erreur lors de la mise à jour du microcontrolleur : " + e.getMessage();
                responseCode = 500;
            }

            exchange.sendResponseHeaders(responseCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            String response = "Méthode non autorisée.";
            exchange.sendResponseHeaders(405, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

static class DeleteMicrocontrollerHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) { // Devrait être DELETE, mais similaire à ci-dessus
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();

            String response = "";
            int responseCode = 200;
            try (Connection connection = Connectdb.getConnection()) {
                String[] keyValuePairs = query.split("&");
                Map<String, String> inputData = new HashMap<>();
                for (String pair : keyValuePairs) {
                    String[] entry = pair.split("=");
                    inputData.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8.name()), URLDecoder.decode(entry[1], StandardCharsets.UTF_8.name()));
                }
                String sql = "DELETE FROM Microcontrolleur WHERE microcontrolleur_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, Integer.parseInt(inputData.get("microcontrolleur_id")));
                    int rowsAffected = preparedStatement.executeUpdate();
                    response = rowsAffected + " microcontrolleur supprimé.";
                }
            } catch (SQLException e) {
                response = "Erreur lors de la suppression du microcontrolleur : " + e.getMessage();
                responseCode = 500; // Internal Server Error
            }

            exchange.sendResponseHeaders(responseCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            String response = "Méthode non autorisée.";
            exchange.sendResponseHeaders(405, response.getBytes().length); // 405 Method Not Allowed
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}


        public static Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0 && idx < pair.length() - 1) {
                    String key = pair.substring(0, idx);
                    String value = pair.substring(idx + 1);
                    map.put(key, value);
                }
            }
            return map;
        }
    }

    public class CapteurHandlers {

        static class ListCapteurHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            }
        }
    
        static class AddCapteurHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
               
            }
        }
    
        static class UpdateCapteurHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                 
            }
        }
    
        static class DeleteCapteurHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                
            }
        }
    }
    
    public class MesureHandlers {
    
        static class ListmesureHandler implements HttpHandler {
            Connection connection = Connectdb.getConnection();
            
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1); // Pour les requêtes OPTIONS
                    return;
                }
        
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
                    exchange.close();
                    return;
                }
        
                JSONArray jsonResponse = new JSONArray();
                String response = "";
                int responseCode = 200;
        
                try {
                    String sql = "SELECT * FROM Mesures";
                    try (PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            JSONObject mesure = new JSONObject();
                            mesure.put("mesure_id", resultSet.getInt("mesure_id"));
                            mesure.put("nom", resultSet.getString("nom"));
                            mesure.put("capteur_id", resultSet.getInt("capteur_id"));
                            mesure.put("valeur", resultSet.getDouble("valeur"));
                            mesure.put("timestamp", resultSet.getTimestamp("timestamp").toString());
                            mesure.put("actif", resultSet.getBoolean("actif"));
                            jsonResponse.put(mesure);
                        }
                        response = jsonResponse.toString();
                    }
                } catch (SQLException e) {
                    response = "{\"error\":\"Error while listing measures: " + e.getMessage() + "\"}";
                    responseCode = 500; // Internal Server Error
                }
        
                exchange.sendResponseHeaders(responseCode, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
        
    
       
    
        static class DeletemesureHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String response;
        int responseCode;

        if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {

            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);

            String mesureName = params.get("nom"); // Assurez-vous que "nom" est le paramètre passé dans l'URL.

            try (Connection connection = Connectdb.getConnection()) {

                String sql = "UPDATE Mesures SET actif = FALSE WHERE capteur_id IN (SELECT Capteurs.capteur_id FROM Capteurs JOIN Microcontrolleur ON Capteurs.microcontrolleur_id = Microcontrolleur.microcontrolleur_id WHERE Microcontrolleur.nom = ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, mesureName);
                    int rowsAffected = preparedStatement.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        response = "Mesure supprimée avec succès.";
                        responseCode = 200; // OK
                    } else {
                        response = "Aucune mesure trouvée avec le nom fourni.";
                        responseCode = 404; // Not Found
                    }
                }
            } catch (SQLException e) {
                response = "Erreur lors de la suppression de la mesure: " + e.getMessage();
                responseCode = 500; // Internal Server Error
            }
        } else {
            response = "Méthode non autorisée.";
            responseCode = 405; // Method Not Allowed
        }

        exchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
      }
      private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
        }
        return result;
    }
    }
       
    }

    public class ActuateurHandlers {

        static class ListActuateursHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                // Implementation for listing actuateurs
            }
        }
    
        static class AddActuateursHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                // Implementation for adding a new actuateur
            }
        }
    
        static class UpdateActuateursHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                // Implementation for updating an existing actuateur
            }
        }
    
        static class DeleteActuateursHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                //exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                // Implementation for deleting an actuateur
            }
        }
    }
}


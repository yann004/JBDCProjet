package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
/* 
public class DataProcessor implements Runnable {
    
    private volatile boolean running = true;
    private Connection connection; // Assurez-vous d'initialiser cette connexion

    public DataProcessor(Connection connection) {
        this.connection = connection;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            Map<String, String> data = SensorDataQueue.removeFromQueue();
            if (data != null) {
                processSensorData(data);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void processSensorData(Map<String, String> data) {

      /*   String sensorName = data.get("sensorName");
        String sensorValue = data.get("sensorValue");
        String sensorType = data.get("sensorType"); // 'capteur' ou 'actuateur'
        
        try {
            if ("capteur".equals(sensorType)) {
                int capteurId = findCapteurIdByName(sensorName);
                if (capteurId != -1) {
                    updateMesure(capteurId, Double.parseDouble(sensorValue));
                }
            } else if ("actuateur".equals(sensorType)) {
                // Logique de traitement pour les actuateurs
            String actuatorName = data.get("actuatorName");
            String actuatorStatus = data.get("status");
            int actuatorId = findActuateurIdByName(actuatorName);
            if (actuatorId != -1) {
                updateActuateurStatus(actuatorId, actuatorStatus);
            } else {
                System.out.println("Actuateur non trouvé: " + actuatorName);
            }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    private int findCapteurIdByName(String name) throws SQLException {
        String sql = "SELECT capteur_id FROM Capteurs WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capteur_id");
                }
            }
        }
        return -1;
    }

    private void updateMesure(int capteurId, double value) throws SQLException {

           
        String sql = "UPDATE Mesures SET valeur = ? WHERE capteur_id = ? ";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, value);
            stmt.setInt(2, capteurId);
            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0) {
                System.out.println("Mise à jour réussie pour le capteur ID: " + capteurId);
            } else {
                System.out.println("Aucune mesure à mettre à jour pour le capteur ID: " + capteurId);
            }
        }
    }
    

    private int findActuateurIdByName(String name) throws SQLException {
        String sql = "SELECT actuateur_id FROM Actuateurs WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("actuateur_id");
                }
            }
        }
        return -1;
    }

    private void updateActuateurStatus(int actuateurId, String status) throws SQLException {
        String sql = "UPDATE Actuateurs SET fonction = ? WHERE actuateur_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, actuateurId);
            stmt.executeUpdate();
        }
      }
    }
}*/

    
    public class DataProcessor implements Runnable {

        
        private volatile boolean running = true;
        //private Connection connection; // Assurez-vous d'initialiser cette connexion

        Connection connection = Connectdb.getConnection();
    
        public DataProcessor(Connection connection) {
            this.connection = connection;
        }
    
        public void terminate() {
            running = false;
        }
    
        @Override
      
        public void run() {
            while (running) {
                Map<String, String> data = SensorDataQueue.removeFromQueue();
                if (data != null) {
                    System.out.println("Received data: " + data); // Affiche les données reçues
                    processSensorData(data);
                } else {
                    try {
                        Thread.sleep(1000); // Attente pour réduire l'utilisation du CPU lorsque la file d'attente est vide
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break; // Sort de la boucle si le thread est interrompu
                    }
                }
            }
        }
        
    
        private void processSensorData(Map<String, String> data) {


            try {
                String type = data.get("sensorType"); // 'capteur' ou 'actuateur'
                if ("capteur".equals(type)) {
                    String sensorName = data.get("sensorName");
                    double sensorValue = Double.parseDouble(data.get("sensorValue"));
                    insertMesure(sensorName, sensorValue);
                } else if ("actuateur".equals(type)) {
                    String actuatorName = data.get("sensorName");
                    String actuatorStatus = data.get("status");
                    insertActuateurStatus(actuatorName, actuatorStatus);
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
        }
    
        private void insertMesure(String sensorName, double value) throws SQLException {
            int sensorId = findSensorIdByName(sensorName);
            if (sensorId != -1) {
                String sql = "INSERT INTO Mesures (capteur_id, valeur, nom, actif) VALUES (?, ?, ?, TRUE)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, sensorId);
                    pstmt.setDouble(2, value);
                    pstmt.setString(3, sensorName);  // Assurez-vous que 'nom' est un champ valide dans votre table Mesures
                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        System.out.println("mesure ajouté avec succès.");
                        }
                }catch (SQLException e) {
                    System.err.println("Erreur lors de l'ajout de l'actuateur: " + e.getMessage());
                    }
            }
        }
    
        private int findSensorIdByName(String name) throws SQLException {
            String sql = "SELECT capteur_id FROM Capteurs WHERE nom = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("capteur_id");
                    }
                }
            }
            return -1;  // -1 signifie que le capteur n'a pas été trouvé
        }
    
        private void insertActuateurStatus(String actuatorName, String status) throws SQLException {
            int actuatorId = findActuatorIdByName(actuatorName);
            if (actuatorId != -1) {
                String sql = "INSERT INTO Actuateurs (nom, fonction, actif) VALUES (?, ?, TRUE)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, actuatorName);
                    pstmt.setString(2, status);
                    pstmt.executeUpdate();
                }
            }
        }
    
        private int findActuatorIdByName(String name) throws SQLException {
            String sql = "SELECT actuateur_id FROM Actuateurs WHERE nom = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("actuateur_id");
                    }
                }
            }
            return -1;  // -1 signifie que l'actuateur n'a pas été trouvé
        }
    }
    
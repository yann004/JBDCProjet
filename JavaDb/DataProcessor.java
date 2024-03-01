package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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

        String sensorName = data.get("sensorName");
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

        
        // Par exemple, si vous mettez à jour la dernière mesure enregistrée pour ce capteur:
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

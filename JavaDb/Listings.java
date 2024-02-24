package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Listings {
    
    public static void afficherMicrocontrolleur(ResultSet resultSet) throws SQLException {
        int idMicrocontrolleur = resultSet.getInt("microcontrolleur_id");
        String nom = resultSet.getString("nom");
        String details = resultSet.getString("details");
        boolean actif = resultSet.getBoolean("actif");
        System.out.println("ID: " + idMicrocontrolleur + ", Nom: " + nom + ", Détails: " + details + ", Actif: " + actif);
    }

    public static void afficherCapteur(ResultSet resultSet) throws SQLException {
        int idCapteur = resultSet.getInt("capteur_id");
        String nom = resultSet.getString("nom");
        String type = resultSet.getString("type_capteur");
        int microcontrolleurId = resultSet.getInt("microcontrolleur_id");
        boolean actif = resultSet.getBoolean("actif");
        System.out.println("ID: " + idCapteur + ", Nom: " + nom + ", Type: " + type + ", Microcontrolleur ID: " + microcontrolleurId + ", Actif: " + actif);
    }

    public static void afficherActuateur(ResultSet resultSet) throws SQLException {
        int idActuateur = resultSet.getInt("actuateur_id");
        String nom = resultSet.getString("nom");
        String fonction = resultSet.getString("fonction");
        int microcontrolleurId = resultSet.getInt("microcontrolleur_id");
        boolean actif = resultSet.getBoolean("actif");
        System.out.println("ID: " + idActuateur + ", Nom: " + nom + ", Fonction: " + fonction + ", Microcontrolleur ID: " + microcontrolleurId + ", Actif: " + actif);
    }

    public static void afficherMesure(ResultSet resultSet) throws SQLException {
        int idMesure = resultSet.getInt("mesure_id");
        int capteurId = resultSet.getInt("capteur_id");
        float valeur = resultSet.getFloat("valeur");
        String nom = resultSet.getString("nom");
        String timestamp = resultSet.getString("timestamp");
        boolean actif = resultSet.getBoolean("actif");
        System.out.println("ID: " + idMesure + ", Capteur ID: " + capteurId + ", Valeur: " + valeur  +", nom: " + nom + ", Timestamp: " + timestamp + ", Actif: " + actif);
    }

    public static void listerElementsTable(Connection connexion, String nomTable) throws SQLException {
        System.out.println("\nListe des éléments de la table " + nomTable + " :");

        String sql = "SELECT * FROM " + nomTable;
        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                switch (nomTable) {
                    case "Microcontrolleur":
                        afficherMicrocontrolleur(resultSet);
                        break;
                    case "Capteurs":
                        afficherCapteur(resultSet);
                        break;
                    case "Actuateurs":
                        afficherActuateur(resultSet);
                        break;
                    case "Mesures":
                        afficherMesure(resultSet);
                        break;
                    default:
                        System.out.println("Table non reconnue : " + nomTable);
                }
            }

        }
    }
    
    public static void main(String[] args) {
        Connection connection = Connectdb.getConnection(); // Assurez-vous que c'est le bon nom de la méthode de connexion

        try (connection) {
            System.out.println("Connexion à la base de données établie avec succès.");

            listerElementsTable(connection, "Microcontrolleur");
            listerElementsTable(connection, "Capteurs");
            listerElementsTable(connection, "Actuateurs");
            listerElementsTable(connection, "Mesures");

        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }
}

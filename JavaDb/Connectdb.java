package JavaDb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;



/**
 *
 * @author postgresqltutorial.com
 */


public class Connectdb {
     
    private static final String user = "postgres";
    private static final String password = "admin";
    private static final String url = "jdbc:postgresql://localhost:5432/FARM";
    private static final String url1 = "jdbc:postgresql://localhost:5432/objet";

    private static Connection connection;
    
    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */

     

    public boolean databaseExists(String Objet) {

        try ( Connection connection = DriverManager.getConnection(url, user, password);) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();

            while (resultSet.next()) {
                String existingObjet = resultSet.getString("TABLE_CAT");
                if (existingObjet.equalsIgnoreCase(Objet)) {
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    public void createDatabaseIfNotExists(String Objet) {

        if (!databaseExists(Objet)) {
            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/FARM", user, password)) {
                Statement statement = connection.createStatement();
                String createDatabaseQuery = "CREATE DATABASE " + Objet;
                statement.executeUpdate(createDatabaseQuery);
                statement.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la création de la base de données : " + e.getMessage());
            }
        } else {
            System.out.println("La base de données existe déjà.");
        }
    }

 public void CreateTable(String Objet){


    try (Connection connection1 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/objet", user, password)) {

        // Création de la table microcontrolleur
        
        Statement statement1 = connection1.createStatement();
        String sqlCreationTableMicrocontrolleur = "CREATE TABLE IF NOT EXISTS Microcontrolleur (" +
        "microcontrolleur_id SERIAL PRIMARY KEY, " +
        "nom VARCHAR(255), " +
        "details VARCHAR(255), " +
        "actif BOOLEAN DEFAULT TRUE)";
        statement1.executeUpdate(sqlCreationTableMicrocontrolleur);
        System.out.println("Table microcontrolleur créée ou existante.");

        // Création de la table Capteurs

        String sqlCreationTableCapteurs = "CREATE TABLE IF NOT EXISTS Capteurs (" +
        "capteur_id SERIAL PRIMARY KEY, " +
        "nom VARCHAR(255), " +
        "type_capteur VARCHAR(10) CHECK (type_capteur IN ('analogique', 'numerique')), " +
        "microcontrolleur_id INT, " +
        "actif BOOLEAN DEFAULT TRUE, " +
        "FOREIGN KEY (microcontrolleur_id) REFERENCES Microcontrolleur(microcontrolleur_id))";
        statement1.executeUpdate(sqlCreationTableCapteurs);
        System.out.println("Table Capteurs créée ou existante.");

        // Création de la table Mesures

        String sqlCreationTableMesures = "CREATE TABLE IF NOT EXISTS Mesures (" +
        "mesure_id SERIAL PRIMARY KEY, " +
        "nom VARCHAR(255), " +
        "capteur_id INT, " +
        "valeur DECIMAL, " +
        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "actif BOOLEAN DEFAULT TRUE, " +
        "FOREIGN KEY (capteur_id) REFERENCES Capteurs(capteur_id))";
        statement1.executeUpdate(sqlCreationTableMesures);
        System.out.println("Table Mesures créée ou existante.");
        
        System.out.println("Base de données créée avec succès.");

         // Création de la table Actuateurs

         String sqlCreationTableActuateurs = "CREATE TABLE IF NOT EXISTS Actuateurs (" +
         "actuateur_id SERIAL PRIMARY KEY, " +
         "nom VARCHAR(255), " +
         "fonction VARCHAR(6) CHECK (fonction IN ('ouvert', 'ferme')), " +
         "microcontrolleur_id INT, " +
         "actif BOOLEAN DEFAULT TRUE, " +
         "FOREIGN KEY (microcontrolleur_id) REFERENCES Microcontrolleur(microcontrolleur_id))";
         statement1.executeUpdate(sqlCreationTableActuateurs);
         System.out.println("Table Actuateurs créée ou existante.");
         statement1.close();
     
         System.out.println("Base de données créée avec succès.");
    } catch (SQLException e) {
        System.out.println("Erreur lors de la création de la base de données : " + e.getMessage());
    }

}
    public static  Connection getConnection() {

        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url1, user, password);
            } catch (SQLException e) {
                System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            }
        }
        return connection;
    }

    // Méthode pour fermer la connexion à la base de données

    public static void closeConnection() {

        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée avec succès.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
    
  }




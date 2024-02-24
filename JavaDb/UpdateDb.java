package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateDb {
    
    private static void updateMicrocontrolleur(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Microcontrolleur SET details = ? WHERE nom = ?")) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Entrez le nom du microcontrolleur pour la mise à jour : ");
            String nomMicrocontrolleur = scanner.nextLine();

            System.out.print("Nouveaux détails pour le microcontrolleur : ");
            String nouveauxDetails = scanner.nextLine();

            preparedStatement.setString(1, nouveauxDetails);
            preparedStatement.setString(2, nomMicrocontrolleur);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Nombre de lignes affectées dans Microcontrolleur : " + rowsAffected);
        }
    }

    private static void updateCapteurs(Connection connection) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Capteurs SET type_capteur = ? WHERE nom = ?")) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Entrez le nom du capteur pour la mise à jour : ");
            String nomCapteur = scanner.nextLine();

            System.out.print("Nouveau type pour le capteur : ");
            String nouveauType = scanner.nextLine();

            preparedStatement.setString(1, nouveauType);
            preparedStatement.setString(2, nomCapteur);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Nombre de lignes affectées dans Capteurs : " + rowsAffected);
        }
    }

    private static void updateActuateurs(Connection connection) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Actuateurs SET fonction = ? WHERE nom = ?")) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Entrez le nom de l'actuateur pour la mise à jour : ");
            String nomActuateur = scanner.nextLine();

            System.out.print("Nouvelle fonction pour l'actuateur : ");
            String nouvelleFonction = scanner.nextLine();

            preparedStatement.setString(1, nouvelleFonction);
            preparedStatement.setString(2, nomActuateur);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Nombre de lignes affectées dans Actuateurs : " + rowsAffected);
        }
    }

    public static void main(String[] args) {
        Connection connection = Connectdb.getConnection();
    
        try (connection) {
            System.out.println("Connexion à la base de données établie avec succès.");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Menu de mise à jour :");
                System.out.println("1. Mise à jour de la table Microcontrolleur");
                System.out.println("2. Mise à jour de la table Capteurs");
                System.out.println("3. Mise à jour de la table Actuateurs");
                System.out.println("4. Quitter");
                System.out.print("Veuillez choisir une option : ");
    
                int choice = scanner.nextInt();
                scanner.nextLine(); // Nettoyer le buffer d'entrée

                switch (choice) {
                    case 1:
                        updateMicrocontrolleur(connection);
                        break;
                    case 2:
                        updateCapteurs(connection);
                        break;
                    case 3:
                        updateActuateurs(connection);
                        break;
                    case 4:
                        System.out.println("Programme terminé.");
                        return;
                    default:
                        System.out.println("Option non valide. Veuillez réessayer.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
    }
}

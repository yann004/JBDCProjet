package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateDb {
    
    private static void updateMicrocontrolleur(Connection connection) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Entrez le nom actuel du microcontrôleur pour la mise à jour : ");
    String oldNomMicrocontrolleur = scanner.nextLine();
    
    // Vérifier si plus d'un microcontrôleur existe avec ce nom
    try (PreparedStatement checkExists = connection.prepareStatement("SELECT microcontrolleur_id, nom, details FROM Microcontrolleur WHERE nom = ?")) {
        checkExists.setString(1, oldNomMicrocontrolleur);
        ResultSet resultSet = checkExists.executeQuery();
        
        boolean multipleEntries = false;
        int microcontrolleurIdToUpdate = 0;
        while (resultSet.next()) {
            if (!multipleEntries) {
                System.out.println("Existant microcontrôleur(s) avec le nom '" + oldNomMicrocontrolleur + "':");
                System.out.println("Microcontrolleur_ID\tNom\tDétails");
                multipleEntries = true; // Set true when at least one entry exists
            }
            System.out.println(resultSet.getInt("microcontrolleur_id") + "\t" + resultSet.getString("nom") + "\t" + resultSet.getString("details"));
        }

        if (multipleEntries) {
            System.out.print("Plusieurs microcontrôleurs trouvés avec ce nom, veuillez entrer le microcontrolleur_id de celui que vous souhaitez mettre à jour : ");
            microcontrolleurIdToUpdate = scanner.nextInt();
            scanner.nextLine(); // Nettoyer le buffer d'entrée après la lecture de l'entier
        } else {
            System.out.println("Aucun microcontrôleur trouvé avec le nom spécifié.");
            return; // Sortir si aucun microcontrôleur n'est trouvé ou si seulement un est trouvé mais on ne veut pas continuer sans vérification
        }
        
        // Mise à jour des détails du microcontrôleur après avoir obtenu le microcontrolleur_id correct
        System.out.print("Entrez les nouveaux détails pour le microcontrôleur : ");
        String nouveauxDetails = scanner.nextLine();
        System.out.print("Entrez le nouveau nom pour le microcontrôleur : ");
        String newNomMicrocontrolleur = scanner.nextLine();
        
        try (PreparedStatement update = connection.prepareStatement("UPDATE Microcontrolleur SET nom = ?, details = ? WHERE microcontrolleur_id = ?")) {
            update.setString(1, newNomMicrocontrolleur);
            update.setString(2, nouveauxDetails);
            update.setInt(3, microcontrolleurIdToUpdate);
            int rowsAffected = update.executeUpdate();
            System.out.println("Nombre de lignes affectées dans Microcontrolleur : " + rowsAffected);
        }
    }
}

    

private static void updateCapteurs(Connection connection) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Entrez le nom actuel du capteur pour la mise à jour : ");
    String oldNomCapteur = scanner.nextLine();
    
    // Vérifier si plus d'un capteur existe avec ce nom
    try (PreparedStatement checkExists = connection.prepareStatement("SELECT capteur_id, nom, type_capteur FROM Capteurs WHERE nom = ?")) {
        checkExists.setString(1, oldNomCapteur);
        ResultSet resultSet = checkExists.executeQuery();
        
        boolean multipleEntries = false;
        int capteurIdToUpdate = 0;
        while (resultSet.next()) {
            if (!multipleEntries) {
                System.out.println("Existant capteur(s) avec le nom '" + oldNomCapteur + "':");
                System.out.println("Capteur_ID\tNom\tType");
                multipleEntries = true; // Set true when at least one entry exists
            }
            System.out.println(resultSet.getInt("capteur_id") + "\t" + resultSet.getString("nom") + "\t" + resultSet.getString("type_capteur"));
        }

        if (multipleEntries) {
            System.out.print("Plusieurs capteurs trouvés avec ce nom, veuillez entrer le capteur_id de celui que vous souhaitez mettre à jour : ");
            capteurIdToUpdate = scanner.nextInt();
            scanner.nextLine(); // Nettoyer le buffer d'entrée après la lecture de l'entier
        } else {
            System.out.println("Aucun capteur trouvé avec le nom spécifié.");
            return; // Sortir si aucun capteur n'est trouvé ou si seulement un est trouvé mais on ne veut pas continuer sans vérification
        }
        
        // Mise à jour des détails du capteur après avoir obtenu le capteur_id correct
        System.out.print("Entrez le nouveau type pour le capteur : ");
        String nouveauType = scanner.nextLine();
        System.out.print("Entrez le nouveau nom pour le capteur : ");
        String newNomCapteur = scanner.nextLine();
        
        try (PreparedStatement update = connection.prepareStatement("UPDATE Capteurs SET nom = ?, type_capteur = ? WHERE capteur_id = ?")) {
            update.setString(1, newNomCapteur);
            update.setString(2, nouveauType);
            update.setInt(3, capteurIdToUpdate);
            int rowsAffected = update.executeUpdate();
            System.out.println("Nombre de lignes affectées dans Capteurs : " + rowsAffected);
        }
    }
}


private static void updateActuateurs(Connection connection) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Entrez le nom actuel de l'actuateur pour la mise à jour : ");
    String oldNomActuateur = scanner.nextLine();
    
    // Vérifier si plus d'un actuateur existe avec ce nom
    try (PreparedStatement checkExists = connection.prepareStatement("SELECT actuateur_id, nom, fonction FROM Actuateurs WHERE nom = ?")) {
        checkExists.setString(1, oldNomActuateur);
        ResultSet resultSet = checkExists.executeQuery();
        
        boolean multipleEntries = false;
        int actuateurIdToUpdate = 0;
        while (resultSet.next()) {
            if (!multipleEntries) {
                System.out.println("Existant actuateur(s) avec le nom '" + oldNomActuateur + "':");
                System.out.println("Actuateur_ID\tNom\tFonction");
                multipleEntries = true; // Set true when at least one entry exists
            }
            System.out.println(resultSet.getInt("actuateur_id") + "\t" + resultSet.getString("nom") + "\t" + resultSet.getString("fonction"));
        }

        if (multipleEntries) {
            System.out.print("Plusieurs actuateurs trouvés avec ce nom, veuillez entrer l'actuateur_id de celui que vous souhaitez mettre à jour : ");
            actuateurIdToUpdate = scanner.nextInt();
            scanner.nextLine(); // Nettoyer le buffer d'entrée après la lecture de l'entier
        } else {
            System.out.println("Aucun actuateur trouvé avec le nom spécifié.");
            return; // Sortir si aucun actuateur n'est trouvé ou si seulement un est trouvé mais on ne veut pas continuer sans vérification
        }
        
        // Mise à jour des détails de l'actuateur après avoir obtenu l'actuateur_id correct
        System.out.print("Entrez les nouvelles fonction pour l'actuateur : ");
        String nouvelleFonction = scanner.nextLine();
        System.out.print("Entrez le nouveau nom pour l'actuateur : ");
        String newNomActuateur = scanner.nextLine();
        
        try (PreparedStatement update = connection.prepareStatement("UPDATE Actuateurs SET nom = ?, fonction = ? WHERE actuateur_id = ?")) {
            update.setString(1, newNomActuateur);
            update.setString(2, nouvelleFonction);
            update.setInt(3, actuateurIdToUpdate);
            int rowsAffected = update.executeUpdate();
            System.out.println("Nombre de lignes affectées dans Actuateurs : " + rowsAffected);
        }
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

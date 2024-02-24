package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DeleteDb {

    private static void afficherMenuDesactivation() {
        System.out.println("\nMenu de désactivation :");
        System.out.println("1. Désactiver un Microcontrolleur (et tous les Capteurs et Actuateurs associés)");
        System.out.println("2. Désactiver un Capteur (et toutes les Mesures associées)");
        System.out.println("3. Désactiver un Actuateur");
        System.out.println("4. Désactiver une Mesure");
        System.out.println("5. Retour");
        System.out.print("Choisissez une option : ");
    }

    private static void desactiverElementParNom(Connection connexion, Scanner scanner, String nomTable, String nomColonneNom, String nomColonneId) throws SQLException {
        System.out.print("\nEntrez le nom de l'élément à désactiver dans la table " + nomTable + " : ");
        String nomElement = scanner.nextLine();

        String sqlUpdate = "UPDATE " + nomTable + " SET actif = FALSE WHERE " + nomColonneNom + " = ?";
        try (PreparedStatement updateStatement = connexion.prepareStatement(sqlUpdate)) {
            updateStatement.setString(1, nomElement);
            int lignesMiseAJour = updateStatement.executeUpdate();

            if (lignesMiseAJour > 0) {
                System.out.println(nomTable + " désactivé avec succès.");
                desactiverAssocies(connexion, nomTable, nomColonneId, nomElement, nomColonneNom);
            } else {
                System.out.println("Aucun élément correspondant au nom fourni n'a été trouvé dans " + nomTable + ".");
            }
        }
    }

    private static void desactiverAssocies(Connection connexion, String nomTable, String nomColonneForeignKey, String valeurForeignKey, String nomColonneNom) throws SQLException {
        String sqlUpdateAssocies;
        if ("Microcontrolleur".equals(nomTable)) {
            sqlUpdateAssocies = "UPDATE Capteurs SET actif = FALSE WHERE microcontrolleur_id IN (SELECT microcontrolleur_id FROM Microcontrolleur WHERE nom = ?);";
            sqlUpdateAssocies += "UPDATE Actuateurs SET actif = FALSE WHERE microcontrolleur_id IN (SELECT microcontrolleur_id FROM Microcontrolleur WHERE nom = ?);";
        } else if ("Capteurs".equals(nomTable)) {
            sqlUpdateAssocies = "UPDATE Mesures SET actif = FALSE WHERE capteur_id IN (SELECT capteur_id FROM Capteurs WHERE nom = ?);";
        } else {
            return; // Pas de désactivation en cascade pour Actuateurs ou Mesures
        }

        try (PreparedStatement updateStatementAssocies = connexion.prepareStatement(sqlUpdateAssocies)) {
            updateStatementAssocies.setString(1, valeurForeignKey);
            updateStatementAssocies.executeUpdate(); // Exécutez chaque mise à jour si plusieurs
        }
    }

    public static void main(String[] args) {
        Connection connection = Connectdb.getConnection(); // Assurez-vous que c'est le bon nom de la méthode de connexion

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Connexion à la base de données établie avec succès.");

            int choix;
            do {
                afficherMenuDesactivation();
                choix = scanner.nextInt();
                scanner.nextLine(); // Consommer le saut de ligne après un int

                switch (choix) {
                    case 1:
                        desactiverElementParNom(connection, scanner, "Microcontrolleur", "nom", "microcontrolleur_id");
                        break;
                    case 2:
                        desactiverElementParNom(connection, scanner, "Capteurs", "nom", "capteur_id");
                        break;
                    case 3:
                        desactiverElementParNom(connection, scanner, "Actuateurs", "nom", "actuateur_id");
                        break;
                    case 4:
                        desactiverElementParNom(connection, scanner, "Mesures", "nom", "mesure_id");
                        break;
                    case 5:
                        System.out.println("Retour au menu principal.");
                        break;
                    default:
                        System.out.println("Choix invalide. Veuillez choisir à nouveau.");
                }
            } while (choix != 5);

        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
       
        } finally {
            Connectdb.closeConnection(); // Assurez-vous de fermer la connexion correctement
        }
    }
    }
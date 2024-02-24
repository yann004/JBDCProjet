package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RestoreDb {

    private static void afficherMenuRestauration() {
        System.out.println("\nMenu de restauration :");
        System.out.println("1. Restaurer un Microcontrolleur (et tous les Capteurs et Actuateurs associés)");
        System.out.println("2. Restaurer un Capteur (et toutes les Mesures associées)");
        System.out.println("3. Restaurer un Actuateur");
        System.out.println("4. Restaurer une Mesure");
        System.out.println("5. Retour");
        System.out.print("Choisissez une option : ");
    }

    private static void restaurerElementParNom(Connection connexion, Scanner scanner, String nomTable, String nomColonneNom, String nomColonneId) throws SQLException {
        System.out.print("\nEntrez le nom de l'élément à restaurer dans la table " + nomTable + " : ");
        String nomElement = scanner.nextLine();

        // Mise à jour de la colonne 'actif' de 'false' à 'true' pour l'élément principal
        
        String sqlUpdate = "UPDATE " + nomTable + " SET actif = TRUE WHERE " + nomColonneNom + " = ?";
        try (PreparedStatement updateStatement = connexion.prepareStatement(sqlUpdate)) {
            updateStatement.setString(1, nomElement);
            int lignesMiseAJour = updateStatement.executeUpdate();

            if (lignesMiseAJour > 0) {
                System.out.println(nomTable + " restauré avec succès.");

                // Logique de restauration en cascade pour Microcontrolleur
                if ("Microcontrolleur".equals(nomTable)) {
                    restaurerAssocies(connexion, "Capteurs", "microcontrolleur_id", nomElement, "nom");
                    restaurerAssocies(connexion, "Actuateurs", "microcontrolleur_id", nomElement, "nom");
                }

                // Logique de restauration en cascade pour Capteurs
                if ("Capteurs".equals(nomTable)) {
                    restaurerAssocies(connexion, "Mesures", "capteur_id", nomElement, "nom");
                }
            } else {
                System.out.println("Aucun élément correspondant au nom fourni n'a été trouvé dans " + nomTable + ".");
            }
        }
    }

    private static void restaurerAssocies(Connection connexion, String nomTable, String nomColonneForeignKey, String valeurForeignKey, String nomColonneNom) throws SQLException {
        String sqlUpdateAssocies = "UPDATE " + nomTable + " SET actif = TRUE WHERE " + nomColonneForeignKey + " IN (SELECT " + nomColonneForeignKey + " FROM " + nomTable + " JOIN Microcontrolleur ON " + nomTable + "." + nomColonneForeignKey + " = Microcontrolleur.microcontrolleur_id WHERE Microcontrolleur." + nomColonneNom + " = ?)";
        try (PreparedStatement updateStatementAssocies = connexion.prepareStatement(sqlUpdateAssocies)) {
            updateStatementAssocies.setString(1, valeurForeignKey);
            int lignesMiseAJour = updateStatementAssocies.executeUpdate();
            if (lignesMiseAJour > 0) {
                System.out.println(lignesMiseAJour + " éléments associés dans " + nomTable + " restaurés en cascade.");
            }
        }
    }

    public static void main(String[] args) {
        Connection connection = Connectdb.getConnection(); // Assurez-vous que c'est le bon nom de la méthode de connexion

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Connexion à la base de données établie avec succès.");

            int choix;
            do {
                afficherMenuRestauration();
                choix = scanner.nextInt();
                scanner.nextLine(); // Consommer le saut de ligne après un int

                switch (choix) {
                    case 1:
                        restaurerElementParNom(connection, scanner, "Microcontrolleur", "nom", "microcontrolleur_id");
                        break;
                    case 2:
                        restaurerElementParNom(connection, scanner, "Capteurs", "nom", "capteur_id");
                        break;
                    case 3:
                        restaurerElementParNom(connection, scanner, "Actuateurs", "nom", "actuateur_id");
                        break;
                    case 4:
                        restaurerElementParNom(connection, scanner, "Mesures", "nom", "mesure_id");
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
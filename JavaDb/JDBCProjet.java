package JavaDb;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
public class JDBCProjet {

    private static void afficherMenu() {
        System.out.println("\nMenu de gestion de la base de données:");
        System.out.println("1. Lister les éléments");
        System.out.println("2. Ajouter un élément");
        System.out.println("3. Supprimer (désactiver) un élément");
        System.out.println("4. Mettre à jour un élément");
        System.out.println("5. Quitter");
        System.out.print("Choisissez une option: ");
    }

    private static void gestionMenu(int choix, Scanner scanner) {
        switch (choix) {
            case 1:
            Listings listings = new Listings();
            listings.main(new String[]{});
            break;
        case 2:
            AddDb addDb = new AddDb();
            addDb.main(new String[]{});
            break;
        case 3:
            DeleteDb deleteDb = new DeleteDb();
            deleteDb.main(new String[]{});
            break;
        case 4:
            UpdateDb updateDb = new UpdateDb();
            updateDb.main(new String[]{});
            break;
        case 5:
            System.out.println("Programme terminé.");
            return; // sortir de la méthode autocloable() et donc fermer le scanner
        default:
            System.out.println("Choix invalide. Veuillez choisir à nouveau.");
        }
    }

    public static void main(String[] args) {

  
        Connectdb databaseUtil = new Connectdb();
        String dbNameToCreate = "Objet";

        databaseUtil.createDatabaseIfNotExists(dbNameToCreate);
        databaseUtil.CreateTable(dbNameToCreate);

        try (Scanner scanner = new Scanner(System.in)) {
            int choix;
            do {
                afficherMenu();
                while (!scanner.hasNextInt()) {
                    System.out.println("Entrée invalide. Veuillez saisir un nombre.");
                    afficherMenu();
                    scanner.next(); // Consommer l'entrée non valide

                            SimulationCapteur capteurTemperature = new Capteur(1, "Température");
                            SimulationCapteur actuateur = new Actuateur(2);
                    
                            SimulationCapteur simulation = new SimulationCapteur();
                            
                            // Simulation des données en temps réel
                            for (int i = 0; i < 10; i++) { // Simuler 10 fois pour l'exemple
                                simulation.genererDonnees(capteurTemperature);
                                capteurTemperature.simulerDonnees();
                                actuateur.simulerDonnees();
                                
                                try {
                                    Thread.sleep(1000); // Attendre 1 seconde entre chaque génération
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                    
                            // Affichage des données de la pile
                            System.out.println("Données de la pile: " + simulation.getPileDonnees());
                        }
                    
                choix = scanner.nextInt();
                scanner.nextLine(); // Nettoyer le buffer d'entrée
                gestionMenu(choix, scanner);
            } while (choix != 5);
        } finally {
            //Connectdb.closeConnection(); // Assurez-vous que cette méthode ferme la connexion à la base de données
        }
    }
}

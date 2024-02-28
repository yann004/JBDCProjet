package JavaDb;

import java.util.Scanner;

public class JDBCProjet {

    private static void afficherMenu() {
        System.out.println("\nMenu de gestion de la base de données:");
        System.out.println("1. Lister les éléments");
        System.out.println("2. Ajouter un élément");
        System.out.println("3. Supprimer un élément");
        System.out.println("4. Mettre à jour un élément");
        System.out.println("5. Restaurer un element");
        System.out.println("6. Quitter");
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
        RestoreDb RestoreDb = new RestoreDb();
            RestoreDb.main(new String[]{});
            break;    
        case 6:
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
                    
                choix = scanner.nextInt();
                scanner.nextLine(); // Nettoyer le buffer d'entrée
                gestionMenu(choix, scanner);
            } while (choix != 6);
        } finally {
            Connectdb.closeConnection(); // Assurez-vous que cette méthode ferme la connexion à la base de données
        }
    }
}

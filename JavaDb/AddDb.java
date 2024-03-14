package JavaDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDb {
    
    // Méthode pour valider le nom avec l'expression régulière
    private static boolean validerNom(String nom) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\-_ ]*$");  // Espaces ajoutés dans la regex
        Matcher matcher = pattern.matcher(nom);
        return matcher.matches();
    }

    private static void afficherMenu() {
        System.out.println("Menu insertion :");
        System.out.println("1. Ajouter un Microcontrolleur");
        System.out.println("2. Ajouter un Capteur");
        System.out.println("3. Ajouter un Actuateur");
        System.out.println("4. Ajouter une Mesure");
        System.out.println("5. exit");
    }

    public static void addMicrocontroller(Connection connexion, String nom, String details) throws SQLException {
        if (nom.trim().isEmpty() || details.trim().isEmpty()) {
            System.out.println("Le nom et les détails du microcontrolleur ne peuvent pas être vides. Veuillez réessayer.");
            return;
        }

        if (!validerNom(nom) || !validerNom(details)) {
            System.out.println("Le nom ou les détails du microcontrolleur ne correspondent pas au motif spécifié. Veuillez réessayer.");
            return;
        }

        String sql = "INSERT INTO Microcontrolleur (nom, details) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, details);
            preparedStatement.executeUpdate();
            System.out.println("Microcontrolleur ajouté avec succès.");
        }
    }
    

    private static void ajouterMicrocontrolleur(Connection connexion, Scanner scanner) throws SQLException {
        System.out.print("Entrez le nom du microcontrolleur : ");
        String nom = scanner.nextLine();
        System.out.print("Entrez les détails du microcontrolleur : ");
        String details = scanner.nextLine();

        if (nom.trim().isEmpty() || details.trim().isEmpty()) {
            System.out.println("Le nom et les détails du microcontrolleur ne peuvent pas être vides. Veuillez réessayer.");
            return;
        }

        if (!validerNom(nom) || !validerNom(details)) {
            System.out.println("Le nom ou les détails du microcontrolleur ne correspondent pas au motif spécifié. Veuillez réessayer.");
            return;
        }

        String sql = "INSERT INTO Microcontrolleur (nom, details) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, details);
            preparedStatement.executeUpdate();
            System.out.println("Microcontrolleur ajouté avec succès.");
        }
    }

    // Les méthodes ajouterCapteur, ajouterActuateur et ajouterMesure 

    private static void ajouterCapteur(Connection connexion, Scanner scanner) throws SQLException {
        System.out.print("Entrez le nom du capteur : ");
        String nom = scanner.nextLine();
        System.out.print("Entrez le type du capteur (analogique ou numerique) : ");
        String type = scanner.nextLine();
        System.out.print("Entrez le nom du microcontrolleur associé : ");
        String nomMicrocontrolleur = scanner.nextLine();
    
        if (nom.trim().isEmpty() || type.trim().isEmpty() || nomMicrocontrolleur.trim().isEmpty()) {
            System.out.println("Le nom, le type du capteur et le nom du microcontrolleur ne peuvent pas être vides. Veuillez réessayer.");
            return;
        }
    
        if (!validerNom(nom) || !validerNom(nomMicrocontrolleur)) {
            System.out.println("Le nom du capteur ou du microcontrolleur ne correspond pas au motif spécifié. Veuillez réessayer.");
            return;
        }
    
        // Récupérer l'ID du microcontrolleur à partir de son nom
        String sqlFindMicrocontrolleur = "SELECT microcontrolleur_id FROM Microcontrolleur WHERE nom = ?";
        int microcontrolleurId = -1;
        try (PreparedStatement preparedStatement = connexion.prepareStatement(sqlFindMicrocontrolleur)) {
            preparedStatement.setString(1, nomMicrocontrolleur);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    microcontrolleurId = resultSet.getInt("microcontrolleur_id");
                } else {
                    System.out.println("Aucun microcontrolleur correspondant trouvé. Veuillez vérifier le nom et réessayer.");
                    return;
                }
            }
        }
    
        String sql = "INSERT INTO Capteurs (nom, type_capteur, microcontrolleur_id) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, type);
            preparedStatement.setInt(3, microcontrolleurId);
            preparedStatement.executeUpdate();
            System.out.println("Capteur ajouté avec succès.");
        }
    }
    
    
    private static void ajouterActuateur(Connection connexion, Scanner scanner) throws SQLException {
        System.out.print("Entrez le nom de l'actuateur : ");
        String nom = scanner.nextLine();
        System.out.print("Entrez la fonction de l'actuateur (ouvert ou ferme) : ");
        String fonction = scanner.nextLine();
        System.out.print("Entrez le nom du microcontrolleur associé : ");
        String nomMicrocontrolleur = scanner.nextLine();
    
        if (nom.trim().isEmpty() || fonction.trim().isEmpty() || nomMicrocontrolleur.trim().isEmpty()) {
            System.out.println("Le nom, la fonction de l'actuateur et le nom du microcontrolleur ne peuvent pas être vides. Veuillez réessayer.");
            return;
        }
    
        if (!validerNom(nom) || !validerNom(nomMicrocontrolleur)) {
            System.out.println("Le nom de l'actuateur ou du microcontrolleur ne correspond pas au motif spécifié. Veuillez réessayer.");
            return;
        }
    
        // Récupérer l'ID du microcontrolleur à partir de son nom
        String sqlFindMicrocontrolleur = "SELECT microcontrolleur_id FROM Microcontrolleur WHERE nom = ?";
        int microcontrolleurId = -1;
        try (PreparedStatement preparedStatement = connexion.prepareStatement(sqlFindMicrocontrolleur)) {
            preparedStatement.setString(1, nomMicrocontrolleur);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    microcontrolleurId = resultSet.getInt("microcontrolleur_id");
                } else {
                    System.out.println("Aucun microcontrolleur correspondant trouvé. Veuillez vérifier le nom et réessayer.");
                    return;
                }
            }
        }
    
        String sql = "INSERT INTO Actuateurs (nom, fonction, microcontrolleur_id) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, fonction);
            preparedStatement.setInt(3, microcontrolleurId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
            System.out.println("Actuateur ajouté avec succès.");
            }
            } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'actuateur: " + e.getMessage());
            }
            }
    
    
            private static void ajouterMesure(Connection connexion, Scanner scanner) throws SQLException {
                System.out.print("Entrez le nom du capteur associé : ");
                String nomCapteur = scanner.nextLine();
                System.out.print("Entrez le nom de la valeur: ");
                String nom = scanner.nextLine();
                System.out.print("Entrez la valeur de la mesure : ");
                float valeur = scanner.hasNextFloat() ? scanner.nextFloat() : -1;
                scanner.nextLine(); // Nettoyer le buffer d'entrée
            
                if (nomCapteur.trim().isEmpty() || valeur == -1) {
                    System.out.println("Le nom du capteur et la valeur de la mesure ne peuvent pas être vides. Veuillez réessayer.");
                    return;
                }
            
                // Récupérer l'ID du capteur à partir de son nom

                String sqlFindCapteur = "SELECT capteur_id FROM Capteurs WHERE nom = ?";
                int capteurId = -1;
                try (PreparedStatement preparedStatement = connexion.prepareStatement(sqlFindCapteur)) {
                    preparedStatement.setString(1, nomCapteur);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            capteurId = resultSet.getInt("capteur_id");
                        } else {
                            System.out.println("Aucun capteur correspondant trouvé. Veuillez vérifier le nom et réessayer.");
                            return;
                        }
                    }
                }
            
                String sql = "INSERT INTO Mesures (capteur_id, valeur, nom) VALUES (?, ?, ?)";
                
                try (PreparedStatement preparedStatement = connexion.prepareStatement(sql)) {
                    preparedStatement.setInt(1, capteurId);
                    preparedStatement.setFloat(2, valeur);
                    preparedStatement.setString(3, nom);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Mesure ajoutée avec succès.");
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de l'ajout de la mesure: " + e.getMessage());
                }
            }
    
    // Assurez-vous de les mettre à jour pour correspondre aux champs et à la logique de votre base de données

    public static void main(String[] args) {
        Connection connection = Connectdb.getConnection();  // Assurez-vous que c'est le nom correct de votre méthode de connexion

        try (connection) {
            System.out.println("Connexion à la base de données établie avec succès.");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                afficherMenu();
                int choix = scanner.nextInt();
                scanner.nextLine();  // Consommer la ligne restante

                switch (choix) {
                    case 1:
                        ajouterMicrocontrolleur(connection, scanner);
                        break;
                    case 2:
                         ajouterCapteur(connection, scanner);
                        break;
                    case 3:
                         ajouterActuateur(connection, scanner);
                        break;
                    case 4:
                         ajouterMesure(connection, scanner);
                        break;
                    case 5:
                        System.out.println("Fin du programme.");
                        return;
                    default:
                        System.out.println("Choix non valide. Veuillez réessayer.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }
}

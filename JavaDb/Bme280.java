package JavaDb;


import java.util.Random;


class Bme280 {

    public String identifiant;
    public String type;
    public double valeur; // Supposons que la valeur est un double pour simplifier
    public boolean actif;

    public Bme280(String identifiant, String type, double valeur) {
        this.identifiant = identifiant;
        this.type = type;
        this.valeur = valeur;
        this.actif = true; // Par défaut, le capteur est actif
    }

    @Override
    public String toString() {
        return "Capteur{" + "identifiant='" + identifiant + '\'' + ", type='" + type + '\'' + ", valeur=" + valeur + ", actif=" + actif + '}';
    }


public class GenerateurDeDonneesIoT {

    private Random random;

    public GenerateurDeDonneesIoT() {
        this.random = new Random();
    }

    public Bme280 genererDonneeCapteur(String identifiant) {

        // Définir les types de capteurs possibles

        String[] types = {"Température", "Humidité", "Luminosité", "Pression"};

        // Sélectionner un type de capteur aléatoire

        String type = types[random.nextInt(types.length)];

        // Générer une valeur aléatoire pour le capteur

        double valeur;
        switch (type) {
            case "Température":
                valeur = -20 + 60 * random.nextDouble(); // Température de -20 à 40 degrés
                break;
            case "Humidité":
                valeur = 0 + 100 * random.nextDouble(); // Humidité de 0% à 100%
                break;
            case "Luminosité":
                valeur = 0 + 1000 * random.nextDouble(); // Luminosité de 0 à 1000 Lux
                break;
            case "Pression":
                valeur = 950 + 150 * random.nextDouble(); // Pression de 950 à 1100 hPa
                break;
            default:
                valeur = 0; // Valeur par défaut au cas où
        }
        return new Bme280(identifiant, type, valeur);
    }
}
}

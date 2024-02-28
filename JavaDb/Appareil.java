package JavaDb;

public class Appareil {

        protected String identifiant;
        protected boolean actif;
    
        public Appareil(String identifiant) {
            this.identifiant = identifiant;
            this.actif = true; // par défaut le dispositif est actif
        }
    
        public abstract void afficherDetails();
    }
    
    class Microcontroleur extends Appareil {
        public Microcontroleur(String identifiant) {
            super(identifiant);
        }
    
        @Override
        public void afficherDetails() {
            System.out.println("Microcontrôleur ID: " + identifiant + ", Actif: " + actif);
        }
    }
    
    class Capteur extends Appareil {
        private String type;
    
        public Capteur(String identifiant, String type) {
            super(identifiant);
            this.type = type;
        }
    
        @Override
        public void afficherDetails() {
            System.out.println("Capteur ID: " + identifiant + ", Type: " + type + ", Actif: " + actif);
        }
    }
    
    class Actuateur extends Appareil {
        private String fonction;
    
        public Actuateur(String identifiant, String fonction) {
            super(identifiant);
            this.fonction = fonction;
        }
    
        @Override
        public void afficherDetails() {
            System.out.println("Actuateur ID: " + identifiant + ", Fonction: " + fonction + ", Actif: " + actif);
        }
    }
    

#include <Arduino.h>

#include <WiFi.h>

#define RELAY_PIN 5

const char* ssid     = "Wifi Rogers";     
const char* password = "Bathurst2024"; 
const char* serverAddress = "192.168.1.193"; 
const int serverPort = 8080; // Remplacez par le port de votre serveur

WiFiClient client;

void setup() {
  Serial.begin(115200);
  pinMode(RELAY_PIN, OUTPUT);
  
  // Connexion au réseau WiFi

  Serial.print("Connexion à ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connecté.");
}

void loop() {
  // Activer le relais
  digitalWrite(RELAY_PIN, HIGH); 
  sendRelayStatus("ferme"); // Supposons que HIGH signifie "fermé"
  delay(5000); // Attendre 5 secondes

  // Désactiver le relais
  digitalWrite(RELAY_PIN, LOW); 
  sendRelayStatus("ouvert"); // Supposons que LOW signifie "ouvert"
  delay(5000); // Attendre 5 secondes
}

void sendRelayStatus(const char* status) {
  if (client.connect(serverAddress, serverPort)) {
    Serial.println("Connexion au serveur réussie...");

    // Construire le corps de la requête POST
    String postData = "sensorType=actuateur&actuatorName=Relay&status=";
    postData += status;

    // Envoyer la requête POST
    client.println("POST /sensor-data HTTP/1.1");
    client.println("Host: " + String(serverAddress));
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(postData.length());
    client.println();
    client.println(postData);

    // Attendre la réponse du serveur et l'afficher
    while (client.connected()) {
      if (client.available()) {
        String line = client.readStringUntil('\r');
        Serial.print(line);
      }
    }
    client.stop();
  } else {
    Serial.println("Échec de la connexion.");
  }
}

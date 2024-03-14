#include <Arduino.h>

#include <SPI.h>
#include <WiFiNINA.h>

#define LED_PIN 6 

const char* ssid     = "Wifi Rogers";     
const char* password = "Bathurst2024"; 
const char* serverAddress = "192.168.1.193"; 
const int serverPort = 8080; // Remplacez par le port de votre serveur

WiFiClient client;

void sendLedStatus(const char* status) {
  if (client.connect(serverAddress, serverPort)) {
    Serial.println("Connexion au serveur réussie...");

    // Construire le corps de la requête POST
    String postData = "sensorType=actuateur&actuatorName=LED&status=";
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
        Serial.println(line);
      }
    }
    client.stop();
  } else {
    Serial.println("Échec de la connexion.");
  }
}

void setup() {
  
  Serial.begin(9600);
  pinMode(LED_PIN, OUTPUT);
  
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
  // Allumer la LED

  digitalWrite(LED_PIN, HIGH);
  sendLedStatus("ouvert");
  delay(3000); 

  // Éteindre la LED

  digitalWrite(LED_PIN, LOW);
  sendLedStatus("ferme");
  delay(3000); 
}



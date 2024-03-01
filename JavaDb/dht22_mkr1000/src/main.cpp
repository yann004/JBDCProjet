#include <WiFi101.h>
#include <DHT.h>
#include <Adafruit_Sensor.h>
#include <SPI.h>

#define DHTPIN 2            
#define DHTTYPE DHT22       
DHT dht(DHTPIN, DHTTYPE);

const char* ssid = "Wifi Rogers";      
const char* password = "Bathurst2024";  

void setup() {
    Serial.begin(9600);
    dht.begin();
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }
    Serial.println("");
    Serial.println("WiFi connecté");
}

void loop() {
    delay(10000); // Délai entre les lectures

    float h = dht.readHumidity();      // Lire l'humidité
    float t = dht.readTemperature();   // Lire la température

    if (isnan(h) || isnan(t)) {
        Serial.println("Failed to read from DHT sensor!");
        return;
    }

    if (WiFi.status() == WL_CONNECTED) {
        WiFiClient client;
        const int httpPort = 8080;  // Assurez-vous que cela correspond au port de votre serveur
        if (!client.connect("192.168.1.193", httpPort)) {
            Serial.println("Connexion échouée");
            return;
        }

        // Construire le corps de la requête POST
        String postData = "sensorName=dht22&sensorValue=" + String(h) + "&sensorType=capteur"; 
        postData += "&humidity=" + String(t); 

        // Envoyer la requête POST
        client.println("POST /sensor-data HTTP/1.1");
        client.println("Host: 192.168.1.193"); 
        client.println("Content-Type: application/x-www-form-urlencoded");
        client.println("Connection: close");
        client.print("Content-Length: ");
        client.println(postData.length());
        client.println();
        client.println(postData);

        // Attendre la réponse du serveur et l'afficher
        while (client.available()) {
            String line = client.readStringUntil('\r');
            Serial.print(line);
        }
        client.stop(); // Fermer la connexion
    } else {
        Serial.println("WiFi non connecté");
    }
}

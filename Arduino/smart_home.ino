#include <DHT.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// =======================
// Smart Home Control System
// Java Swing ↔ Arduino
// =======================

// ----- DHT11 -----
#define DHTPIN 2
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);

// ----- Components -----
const int LED_PIN = 8;
const int BUZZER_PIN = 9;

// ----- LCD I2C -----
LiquidCrystal_I2C lcd(0x27, 16, 2);

// ----- Variables -----
bool ledState = false;
bool buzzerState = false;

void setup() {

  // Serial Communication
  Serial.begin(9600);

  // Initialize components
  pinMode(LED_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);

  digitalWrite(LED_PIN, LOW);
  digitalWrite(BUZZER_PIN, LOW);

  // Initialize DHT11
  dht.begin();

  // Initialize LCD
  lcd.init();
  lcd.backlight();

  // Welcome Screen
  lcd.setCursor(0, 0);
  lcd.print("Smart Home");

  lcd.setCursor(0, 1);
  lcd.print("System Ready");

  delay(2000);
  lcd.clear();

  // Serial message
  Serial.println("Arduino is ready");
}

void loop() {

  // Read serial commands
  if (Serial.available()) {

    String command = Serial.readStringUntil('\n');
    command.trim();

    // =========================
    // LED CONTROL
    // =========================

    if (command == "LED_ON") {

      digitalWrite(LED_PIN, HIGH);
      ledState = true;

      Serial.println("LED:ON");

      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("LED Turned ON");
    }

    else if (command == "LED_OFF") {

      digitalWrite(LED_PIN, LOW);
      ledState = false;

      Serial.println("LED:OFF");

      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("LED Turned OFF");
    }

    // =========================
    // BUZZER CONTROL
    // =========================

    else if (command == "BUZZER_ON") {

      digitalWrite(BUZZER_PIN, HIGH);
      buzzerState = true;

      Serial.println("BUZZER:ON");

      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Buzzer ON");
    }

    else if (command == "BUZZER_OFF") {

      digitalWrite(BUZZER_PIN, LOW);
      buzzerState = false;

      Serial.println("BUZZER:OFF");

      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Buzzer OFF");
    }

    // =========================
    // SENSOR DATA
    // =========================

    else if (command == "GET_DATA") {

      float temperature = dht.readTemperature();
      float humidity = dht.readHumidity();

      // Check sensor
      if (isnan(temperature) || isnan(humidity)) {

        Serial.println("ERROR:SENSOR");

        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Sensor Error");

        return;
      }

      // Send data to Java
      Serial.print("TEMP:");
      Serial.println(temperature);

      Serial.print("HUM:");
      Serial.println(humidity);

      // Update LCD
      lcd.clear();

      lcd.setCursor(0, 0);
      lcd.print("Temp:");
      lcd.print(temperature);
      lcd.print((char)223);
      lcd.print("C");

      lcd.setCursor(0, 1);
      lcd.print("Hum:");
      lcd.print(humidity);
      lcd.print("%");
    }

    // =========================
    // UNKNOWN COMMAND
    // =========================

    else {

      Serial.println("ERROR:UNKNOWN_COMMAND");
    }
  }
}

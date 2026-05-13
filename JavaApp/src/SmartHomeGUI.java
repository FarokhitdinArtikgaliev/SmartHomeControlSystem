import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class SmartHomeGUI extends JFrame {

    // =========================
    // SERIAL
    // =========================

    private SerialPort serialPort;

    // =========================
    // GUI COMPONENTS
    // =========================

    private JComboBox<String> portComboBox;

    private JButton connectButton;
    private JButton disconnectButton;

    private JButton ledOnButton;
    private JButton ledOffButton;

    private JButton buzzerOnButton;
    private JButton buzzerOffButton;

    private JButton refreshButton;

    private JLabel temperatureLabel;
    private JLabel humidityLabel;
    private JLabel statusLabel;

    private JTextArea logArea;

    // =========================
    // CONSTRUCTOR
    // =========================

    public SmartHomeGUI() {

        setTitle("Smart Home Control System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadCOMPorts();

        setVisible(true);
    }

    // =========================
    // UI DESIGN
    // =========================

    private void initializeUI() {

        Color background = new Color(15, 20, 30);
        Color panelColor = new Color(25, 35, 50);
        Color blue = new Color(0, 170, 255);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(background);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel("SMART HOME CONTROL SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(title, BorderLayout.NORTH);

        // =========================
        // CENTER PANEL
        // =========================

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        centerPanel.setBackground(background);

        // LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(background);

        // RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(background);

        // =========================
        // CONNECTION PANEL
        // =========================

        JPanel connectionPanel = createPanel("Connection", panelColor);

        portComboBox = new JComboBox<>();

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");

        connectButton.addActionListener(e -> connectArduino());
        disconnectButton.addActionListener(e -> disconnectArduino());

        connectionPanel.add(new JLabel("COM Port:"));
        connectionPanel.add(portComboBox);
        connectionPanel.add(Box.createVerticalStrut(10));
        connectionPanel.add(connectButton);
        connectionPanel.add(Box.createVerticalStrut(5));
        connectionPanel.add(disconnectButton);

        // =========================
        // SENSOR PANEL
        // =========================

        JPanel sensorPanel = createPanel("Sensor Data", panelColor);

        temperatureLabel = new JLabel("Temperature: -- °C");
        humidityLabel = new JLabel("Humidity: -- %");

        styleLabel(temperatureLabel);
        styleLabel(humidityLabel);

        refreshButton = new JButton("Refresh Data");

        refreshButton.addActionListener(e -> sendCommand("GET_DATA"));

        sensorPanel.add(temperatureLabel);
        sensorPanel.add(Box.createVerticalStrut(10));
        sensorPanel.add(humidityLabel);
        sensorPanel.add(Box.createVerticalStrut(15));
        sensorPanel.add(refreshButton);

        // =========================
        // CONTROL PANEL
        // =========================

        JPanel controlPanel = createPanel("Device Controls", panelColor);

        ledOnButton = new JButton("LED ON");
        ledOffButton = new JButton("LED OFF");

        buzzerOnButton = new JButton("Buzzer ON");
        buzzerOffButton = new JButton("Buzzer OFF");

        ledOnButton.addActionListener(e -> sendCommand("LED_ON"));
        ledOffButton.addActionListener(e -> sendCommand("LED_OFF"));

        buzzerOnButton.addActionListener(e -> sendCommand("BUZZER_ON"));
        buzzerOffButton.addActionListener(e -> sendCommand("BUZZER_OFF"));

        controlPanel.add(ledOnButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(ledOffButton);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(buzzerOnButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(buzzerOffButton);

        // =========================
        // STATUS PANEL
        // =========================

        JPanel statusPanel = createPanel("System Status", panelColor);

        statusLabel = new JLabel("Disconnected");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));

        statusPanel.add(statusLabel);

        // =========================
        // LOG PANEL
        // =========================

        JPanel logPanel = createPanel("System Logs", panelColor);

        logArea = new JTextArea(15, 40);
        logArea.setEditable(false);

        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(logArea);

        logPanel.add(scrollPane);

        // =========================
        // ADD PANELS
        // =========================

        leftPanel.add(connectionPanel);
        leftPanel.add(Box.createVerticalStrut(15));

        leftPanel.add(sensorPanel);
        leftPanel.add(Box.createVerticalStrut(15));

        leftPanel.add(statusPanel);

        rightPanel.add(controlPanel);
        rightPanel.add(Box.createVerticalStrut(15));

        rightPanel.add(logPanel);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    // =========================
    // CREATE PANEL
    // =========================

    private JPanel createPanel(String title, Color color) {

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBackground(color);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title,
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                Color.WHITE
        ));

        return panel;
    }

    // =========================
    // STYLE LABEL
    // =========================

    private void styleLabel(JLabel label) {

        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
    }

    // =========================
    // LOAD COM PORTS
    // =========================

    private void loadCOMPorts() {

        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {

            portComboBox.addItem(port.getSystemPortName());
        }
    }

    // =========================
    // CONNECT
    // =========================

    private void connectArduino() {

        String selectedPort = (String) portComboBox.getSelectedItem();

        if (selectedPort == null) {

            JOptionPane.showMessageDialog(this, "No COM port selected.");
            return;
        }

        serialPort = SerialPort.getCommPort(selectedPort);

        serialPort.setBaudRate(9600);

        if (serialPort.openPort()) {

            statusLabel.setText("Connected");
            statusLabel.setForeground(Color.GREEN);

            log("Connected to " + selectedPort);

            startReading();
        }

        else {

            JOptionPane.showMessageDialog(this, "Connection failed.");
        }
    }

    // =========================
    // DISCONNECT
    // =========================

    private void disconnectArduino() {

        if (serialPort != null && serialPort.isOpen()) {

            serialPort.closePort();

            statusLabel.setText("Disconnected");
            statusLabel.setForeground(Color.RED);

            log("Disconnected.");
        }
    }

    // =========================
    // SEND COMMAND
    // =========================

    private void sendCommand(String command) {

        try {

            if (serialPort != null && serialPort.isOpen()) {

                OutputStream outputStream = serialPort.getOutputStream();

                outputStream.write((command + "\n").getBytes());

                outputStream.flush();

                log("TX → " + command);
            }

        } catch (Exception e) {

            log("Error sending command.");
        }
    }

    // =========================
    // READ SERIAL DATA
    // =========================

    private void startReading() {

        Thread thread = new Thread(() -> {

            try {

                InputStream inputStream = serialPort.getInputStream();

                Scanner scanner = new Scanner(inputStream);

                while (scanner.hasNextLine()) {

                    String line = scanner.nextLine();

                    log("RX ← " + line);

                    processResponse(line);
                }

            } catch (Exception e) {

                log("Read error.");
            }
        });

        thread.start();
    }

    // =========================
    // PROCESS RESPONSE
    // =========================

    private void processResponse(String response) {

        if (response.startsWith("TEMP:")) {

            String value = response.replace("TEMP:", "");

            temperatureLabel.setText("Temperature: " + value + " °C");
        }

        else if (response.startsWith("HUM:")) {

            String value = response.replace("HUM:", "");

            humidityLabel.setText("Humidity: " + value + " %");
        }
    }

    // =========================
    // LOG METHOD
    // =========================

    private void log(String message) {

        SwingUtilities.invokeLater(() ->
                logArea.append(message + "\n")
        );
    }

    // =========================
    // MAIN METHOD
    // =========================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(SmartHomeGUI::new);
    }
}

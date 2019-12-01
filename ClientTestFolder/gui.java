import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JList;
import javax.swing.border.LineBorder;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.JScrollPane;

public class gui {

    private JFrame frame;
    private JTextField serverHostName;
    private JTextField userName;
    private JTextField portNumber;
    private JTextField hostName;
    private JComboBox speed;

    private JTextField searchKeyWord;
    private JTable table;

    private JTextField command;
    private JTextArea textArea;
    private JTextArea textKeyArea;

    private String message;
    private ArrayList<String> results;

    private String responseFromClient;

    private JScrollPane scroll;
    private JScrollPane scroll1;

    protected Thread listener;

    private int port = 0;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    gui window = new gui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public gui() {
        initialize();
    }

    private void initialize() {
        HostClient host = new HostClient();
        HostServer server = new HostServer();

        frame = new JFrame();
        frame.setBounds(500, 500, 550, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton connectButton = new JButton("Connect");

        frame.getRootPane().setDefaultButton(connectButton);

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // code for connect
                try {

                    String PORT = portNumber.getText();
                    String serverHost = serverHostName.getText();
                    message = "connect " + serverHost + " " + PORT;
                    port = Integer.parseInt(PORT);
                    host.connectToCentralServer(port, serverHost);
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                } catch (Exception e) {

                }
            }
        });

        connectButton.setBounds(380, 11, 170, 23);
        frame.getContentPane().add(connectButton);

        serverHostName = new JTextField();
        serverHostName.setBounds(125, 12, 159, 20);
        frame.getContentPane().add(serverHostName);
        serverHostName.setColumns(10);

        JLabel lblServerHostName = new JLabel("Server Host Name:");
        lblServerHostName.setBounds(8, 15, 133, 14);
        frame.getContentPane().add(lblServerHostName);

        JLabel lblNewLabel = new JLabel("Username:");
        lblNewLabel.setBounds(10, 43, 75, 14);
        frame.getContentPane().add(lblNewLabel);

        userName = new JTextField();
        userName.setBounds(85, 40, 95, 20);
        frame.getContentPane().add(userName);
        userName.setColumns(10);

        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(289, 15, 34, 14);
        frame.getContentPane().add(lblPort);

        portNumber = new JTextField();
        portNumber.setBounds(325, 12, 52, 20);
        frame.getContentPane().add(portNumber);
        portNumber.setColumns(10);

        JLabel lblHostname = new JLabel("Hostname:");
        lblHostname.setBounds(200, 43, 69, 14);
        frame.getContentPane().add(lblHostname);

        hostName = new JTextField();
        hostName.setBounds(280, 40, 110, 20);
        frame.getContentPane().add(hostName);
        hostName.setColumns(10);

        JLabel lblSpeed = new JLabel("Speed:");
        lblSpeed.setBounds(396, 43, 52, 14);
        frame.getContentPane().add(lblSpeed);

        speed = new JComboBox();
        speed.setBounds(442, 40, 105, 20);
        speed.addItem("Ethernet");
        speed.addItem("WiFi");
        speed.addItem("T1");
        speed.addItem("T3");
        frame.getContentPane().add(speed);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // code for register
                // FIRST Register the user on the server
                String clientUserName = userName.getText();
                String clientHostName = hostName.getText();
                String clientConnectionType = speed.getSelectedItem().toString();
                int serverPort = server.getPortNumber();
                try {
                    host.registerToCentralServer(clientUserName, clientHostName, clientConnectionType, serverPort);
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                    
                } catch (Exception e3) {

                }

                try {
                    host.uploadFileListToServer(clientConnectionType, clientHostName);
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                    } catch (Exception e){

                    }
            }
        });

        registerButton.setBounds(10, 68, 137, 23);
        frame.getContentPane().add(registerButton);

        JLabel lblKeyword = new JLabel("Keyword:");
        lblKeyword.setBounds(10, 119, 60, 14);
        frame.getContentPane().add(lblKeyword);

        searchKeyWord = new JTextField();
        searchKeyWord.setBounds(71, 116, 250, 20);
        frame.getContentPane().add(searchKeyWord);
        searchKeyWord.setColumns(10);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                // code for search
                String keywordSearch = searchKeyWord.getText();
                String returnedQuery = "";
                try {
                    returnedQuery = host.queryFileList(keywordSearch);
                    textKeyArea.setText(returnedQuery);
                } catch (Exception e) {

                }
                responseFromClient = host.responseFromClient;
                printResults();
                responseFromClient = "";
            }
        });
        searchButton.setBounds(320, 115, 100, 23);
        frame.getContentPane().add(searchButton);

        textKeyArea = new JTextArea();
        scroll1 = new JScrollPane(textKeyArea);
        scroll1.setBounds(10, 150, 514, 103);
        frame.getContentPane().add(scroll1);

        table = new JTable();
        table.setBorder(new LineBorder(new Color(0, 0, 0), 3));
        table.setBounds(10, 285, 514, -168);

        frame.getContentPane().add(table);

        JLabel lblEnterCommand = new JLabel("Command:");
        lblEnterCommand.setBounds(10, 300, 95, 14);
        frame.getContentPane().add(lblEnterCommand);

        command = new JTextField();

        command.setBounds(80, 300, 310, 20);
        frame.getContentPane().add(command);
        command.setColumns(10);

        JButton goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // go command
                String fileCommand = command.getText();
                int serverToConnectToPort = 0;
                String [] commands = fileCommand.split(" ");
                String retrieveCommand = commands[0];
                String fileName = commands[1];
                String userHostName = commands[2];
                try {
                    serverToConnectToPort = host.getClientPort(userHostName);
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                } catch (Exception e4){
                    
                }
                if(serverToConnectToPort != 0){
                    try{
                        host.establishConnectionAndPullData(serverToConnectToPort, retrieveCommand, fileName);
                        responseFromClient = host.responseFromClient;
                        printResults();
                        responseFromClient = "";
                    } catch (Exception e5){

                    }
                } else {
                    responseFromClient = "Could not connect to server";
                    printResults();
                    responseFromClient = "";
                }
            }
        });
        goButton.setBounds(390, 300, 83, 23);
        frame.getContentPane().add(goButton);

        textArea = new JTextArea();
        scroll = new JScrollPane(textArea);
        scroll.setBounds(10, 330, 514, 103);
        frame.getContentPane().add(scroll);

        JButton unregisterButton = new JButton("Un-register");
        unregisterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // unregister command
                try {
                    String clientUserName = userName.getText();
                    String clientHostName = hostName.getText();
                    String clientConnectionType = speed.getSelectedItem().toString();
                    host.unregisterFromServer(clientUserName, clientHostName, clientConnectionType);
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                } catch (Exception e5){

                }
            }
        });

        unregisterButton.setBounds(140, 68, 137, 23);
        frame.getContentPane().add(unregisterButton);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                message = "quit";

                // disconnect command
                // outToServer.writeBytes(port + " " + sentence + " " + "\n");
                // System.out.println("\nServer Disconnected\n");
                // System.exit(0);
                try {
                    host.disconnectFromCentralServer();
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                } catch (Exception e2) {

                }
            }
        });

        quitButton.setBounds(410, 68, 137, 23);
        frame.getContentPane().add(quitButton);

    }

    public void printResults() {
        // if(results != null){
        // textArea.append("\n>> " + message + "\n");

        // for(int i = 0; i < results.size(); i++){
        // textArea.append(results.get(i));
        // textArea.append("\n");
        // }
        // }
        if (!responseFromClient.equals("")) {
            textArea.append("\n>> " + message + "\n");
            textArea.append(responseFromClient + "\n");
        } else {
            textArea.append("\nNOT WORKING");
        }
    }
}
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
import javax.swing.DefaultListCellRenderer;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
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

    private JLabel currentBalance;
    private JButton addToBalance;
    private JTextField moneyToAdd;

    private String message;
    private ArrayList<String> results;

    private String responseFromClient;
    private HostClient host = new HostClient();
    private HostServer server = new HostServer();

    private JScrollPane scroll;
    private JScrollPane scroll1;

    protected Thread listener;

    private int port = 0;

    private double balance = 0.00;

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
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        getBalance();

        frame = new JFrame();
        frame.setTitle("Client GUI 1");
        frame.setBounds(500, 500, 650, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton connectButton = new JButton("Connect");
        frame.getRootPane().setDefaultButton(connectButton);
        connectButton.setBounds(230, 10, 100, 20);
        frame.getContentPane().add(connectButton);




        // Label
        currentBalance = new JLabel("");
        currentBalance.setBounds(150, 63, 150, 20);
        frame.getContentPane().add(currentBalance);
        currentBalance.setVisible(false);

        // Button
        addToBalance = new JButton("Add Money");
        addToBalance.setBounds(230, 85, 100, 20);
        frame.getContentPane().add(addToBalance);
        addToBalance.setVisible(false);

        // Input Field
        moneyToAdd = new JTextField();
        moneyToAdd.setBounds(125, 85, 100, 20);
        frame.getContentPane().add(moneyToAdd);
        moneyToAdd.setVisible(false);

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
                    currentBalance.setText("Current Balance: " + currencyFormat.format(balance).toString());
                    currentBalance.setVisible(true);
                    addToBalance.setVisible(true);
                    moneyToAdd.setVisible(true);
                    printResults();
                    responseFromClient = "";
                } catch (Exception e) {

                }
            }
        });

        addToBalance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    String addMoney = moneyToAdd.getText(); 
                    balance += Double.parseDouble(addMoney);
                    currentBalance.setText("Current Balance: " + currencyFormat.format(balance).toString());
                    addMoneyToBalance(balance);
                    responseFromClient = "Added money to balance!";
                    printResults();
                    responseFromClient = "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        
        // Label
        JLabel lblServerHostName = new JLabel("Server Host Name:");
        lblServerHostName.setBounds(10, 10, 110, 20);
        frame.getContentPane().add(lblServerHostName);

        // Input Field
        serverHostName = new JTextField();
        serverHostName.setBounds(125, 10, 100, 20);
        frame.getContentPane().add(serverHostName);
        serverHostName.setColumns(10);


        // Label
        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(90, 35, 50, 20);
        frame.getContentPane().add(lblPort);

        // Input Field
        portNumber = new JTextField();
        portNumber.setBounds(125, 35, 100, 20);
        frame.getContentPane().add(portNumber);
        portNumber.setColumns(10);


        

        // Label
        JLabel lblNewLabel = new JLabel("Username:");
        lblNewLabel.setBounds(355, 10, 100, 20);
        frame.getContentPane().add(lblNewLabel);

        // Input Field
        userName = new JTextField();
        userName.setBounds(425, 10, 100, 20);
        frame.getContentPane().add(userName);
        userName.setColumns(10);

        
        // Label
        JLabel lblHostname = new JLabel("Hostname:");
        lblHostname.setBounds(355, 35, 70, 20);
        frame.getContentPane().add(lblHostname);

        // Input Field
        hostName = new JTextField();
        hostName.setBounds(425, 35, 100, 20);
        frame.getContentPane().add(hostName);
        hostName.setColumns(10);




        // Label
        JLabel lblSpeed = new JLabel("Speed:");
        lblSpeed.setBounds(377, 60, 50, 20);
        frame.getContentPane().add(lblSpeed);

        // Combo Box
        speed = new JComboBox();
        speed.setBounds(425, 60, 100, 20);
        speed.addItem("Ethernet");
        speed.addItem("WiFi");
        speed.addItem("T1");
        speed.addItem("T3");
        DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        speed.setRenderer(dlcr);
        dlcr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
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

        registerButton.setBounds(530, 10, 100, 20);
        frame.getContentPane().add(registerButton);




        // Label
        JLabel lblKeyword = new JLabel("Keyword:");
        lblKeyword.setBounds(63, 120, 60, 20);
        frame.getContentPane().add(lblKeyword);

        // Input Field
        searchKeyWord = new JTextField();
        searchKeyWord.setBounds(125, 120, 345, 20);
        frame.getContentPane().add(searchKeyWord);
        searchKeyWord.setColumns(10);

        // Button
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
        searchButton.setBounds(480, 120, 100, 20);
        frame.getContentPane().add(searchButton);

        textKeyArea = new JTextArea();
        scroll1 = new JScrollPane(textKeyArea);
        scroll1.setBounds(30, 150, 590, 100);
        frame.getContentPane().add(scroll1);


        // ???
        table = new JTable();
        table.setBorder(new LineBorder(new Color(0, 0, 0), 3));
        table.setBounds(10, 285, 514, -168);
        frame.getContentPane().add(table);



        // Label
        JLabel lblEnterCommand = new JLabel("Command:");
        lblEnterCommand.setBounds(56, 270, 65, 20);
        frame.getContentPane().add(lblEnterCommand);

        // Input field
        command = new JTextField();
        command.setBounds(125, 270, 345, 20);
        frame.getContentPane().add(command);
        command.setColumns(10);

        // Button
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
                Double costOfItem = 0.00;
                try {
                    costOfItem = host.getCostOfItem(fileName, userHostName);
                    if(costOfItem < 0 || costOfItem > balance){
                        responseFromClient = "Item does not exist or you don't have sufficent funds!";
                        printResults();
                        responseFromClient = "";
                    } else {
                        try {
                            serverToConnectToPort = host.getClientPort(userHostName);
                            responseFromClient = host.responseFromClient;
                            printResults();
                            responseFromClient = "";
                        } catch (Exception e4){
                            
                        }
                        if(serverToConnectToPort != 0){
                            try{
                                host.establishConnection(serverToConnectToPort, retrieveCommand, fileName);
                                responseFromClient = host.responseFromClient;
                                printResults();
                                responseFromClient = "";
                            } catch (Exception e5){
        
                            }
                            try{
                                host.pullData(serverToConnectToPort, retrieveCommand, fileName);
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
                        balance -= costOfItem;
                        currentBalance.setText("Current Balance: " + currencyFormat.format(balance).toString());
                        subMoneyFromBalance(balance);
                        responseFromClient = "Money deducted from balance!";
                        printResults();
                        responseFromClient = "";
                    }
                } catch (Exception e10){
                    e10.printStackTrace();
                }
            }
        });
        goButton.setBounds(480, 270, 100, 20);
        frame.getContentPane().add(goButton);

        textArea = new JTextArea();
        scroll = new JScrollPane(textArea);
        scroll.setBounds(30, 300, 590, 100);
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

        unregisterButton.setBounds(530, 35, 100, 20);
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
                    String clientUserName = userName.getText();
                    String clientHostName = hostName.getText();
                    String clientConnectionType = speed.getSelectedItem().toString();
                    host.unregisterFromServer(clientUserName, clientHostName, clientConnectionType);
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                } catch (Exception e5){

                }

                try {
                    host.disconnectFromCentralServer();
                    responseFromClient = host.responseFromClient;
                    printResults();
                    responseFromClient = "";
                    textArea.setText("");
                    textKeyArea.setText("");
                    currentBalance.setVisible(false);
                    addToBalance.setVisible(false);
                    moneyToAdd.setVisible(false);
                } catch (Exception e2) {

                }
            }
        });

        quitButton.setBounds(230, 35, 100, 20);
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

    public void getBalance() {
        try { 
            balance = host.getBalance();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addMoneyToBalance(Double balance) {
        try {
            host.addToBalance(balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subMoneyFromBalance(Double balance) {
        try {
            host.subFromBalance(balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
import java.io.*;
import java.net.*;
import java.util.*;

class HostServer {

    public static final int PORT = 4000;
    public String responseFromServer = "";
    public static void main(String[] args) throws IOException

    {


        // Create socket server and wait for client to connect
        ServerSocket welcomeSocket = new ServerSocket(PORT);
        do {
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("\n\nUser Connected!\n\n");

            // Creating threads
            ClientHandler handler = new ClientHandler(connectionSocket);
            handler.start();
        } while (true);
    }

    public int getPortNumber(){
        return PORT;
    }
}

class ClientHandler extends Thread {

    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    String fromClient;
    String clientCommand;
    byte[] data;

    // FILE PATH
    File directory = new File(System.getProperty("user.dir"));

    List<String> listOfFiles = new ArrayList<>();
    String firstln;
    private Socket connectionSocket;
    String fileName;
    StringTokenizer tokens = new StringTokenizer("");

    public ClientHandler(Socket socket) {
        connectionSocket = socket;

        try {
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error in connection");
            System.out.println(e);
        }
    }

    public static void serverFiles(File directory, List<String> listOfFiles) {
        if(!(listOfFiles.isEmpty())){
            listOfFiles.clear();
        }
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    listOfFiles.add(file.getName());
                }

            }
        }
    }

    public void run() {
        while(connectionSocket.isClosed() == false){
            try {

                int port = 0;

                fromClient = inFromClient.readLine();
                if (fromClient != null) {
                    tokens = new StringTokenizer(fromClient);
                    firstln = tokens.nextToken();
                    port = Integer.parseInt(firstln);
                    clientCommand = tokens.nextToken();
                    // fileName = tokens.nextToken();
                }

                if (fileName == null) {
                    fileName = "noFileFound.txt";
                }
                serverFiles(directory, listOfFiles);

                if (clientCommand.equals("retr")) {
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                    // String fileName = "file.txt";
                    String fileName = tokens.nextToken();
                    //String filePath = directory.getPath() + "/" + fileName;
                    File myFile = new File(fileName);
                    //System.out.println(filePath);
                    if (myFile.exists()) {
                        byte[] mybytearray = new byte[(int) myFile.length() + 1];
                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        bis.read(mybytearray, 0, mybytearray.length);
                        System.out.println("Sending...");
                        dataOutToClient.write(mybytearray, 0, mybytearray.length);
                        dataOutToClient.flush();
                        bis.close();
                    } else {
                        System.out.println("File Not Found");
                    }

                    dataSocket.close();
                    System.out.println("Data Socket closed");
                }
            }


            catch (IOException e) {
                System.out.println("Error: Unable to disconnect");
                System.out.println(e);
            }
        }
    }
}


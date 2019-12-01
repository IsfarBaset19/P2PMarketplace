import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

class CentralizedServer {

	private static final int PORT = 1200;

	public static List<User> userList = new ArrayList<>();
	// need to iterate through the file that lists
	// available files and their keywords and add them to
	// the filelist here.

	public static void main(String[] args) throws IOException
	{
		//create the new file to hold the client data
		String currentDirectory = System.getProperty("user.dir");
		String serverFileList = currentDirectory + "/allServerFiles.txt";
		File file = new File(serverFileList);
		if(file.exists()){
			file.delete();
		}
		file.createNewFile();
		
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
}

// Class that holds the important information for users of the server
class User {
	String username;
	String userHostName;
	String port;
	String connectionSpeed;

	User(String pUsername, String pUserHostName, String pPort, String pConnectionSpeed) {
		this.username = pUsername;
		this.userHostName = pUserHostName;
		this.port = pPort;
		this.connectionSpeed = pConnectionSpeed;
	}

	public String getUserHostName() {
		return this.userHostName;
	}

	public String getPortNumber() {
		return this.port;
	}

}

// Class that holds multiple file informations for a single user
class UserFileList {
	String hostName;
	List<FileInfo> files;

	void add(FileInfo temp) {
		files.add(temp);
	}

	UserFileList() {
		this.hostName = "";
		this.files = new ArrayList<>();
	}

	UserFileList(String pHostName, List<FileInfo> pFiles) {
		this.hostName = pHostName;
		this.files = new ArrayList<>();
		this.files = pFiles;
	}
}

// Class that holds the filename and any keywords for the file
class FileInfo {
	String fileName;
	List<String> keywords;

	FileInfo() {
		this.fileName = "";
		this.keywords = new ArrayList<>();
	}

	FileInfo(String pFileName, List<String> pKeywords) {
		this.fileName = pFileName;
		this.keywords = new ArrayList<>();
		this.keywords = pKeywords;
	}

}

class ClientHandler extends Thread {

	private DataOutputStream outToClient;
	private BufferedReader inFromClient;

	// This arrayList of Users holds the list of active users currently
	// registered to the server

	// This arrayList holds the list of UserFileLists
	private List<UserFileList> fileList = new ArrayList<>();

	// Code for registering a new user to the server
	private void addUser(User temp) {
		CentralizedServer.userList.add(temp);
	}

	// Code for removing a user from the server
	private void removeUser(User temp) {
		CentralizedServer.userList.remove(temp);
	}

	private void addFiles(List<String> fileList) {
		String fileName = "allServerFiles.txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			int i = 0;
			for (i = 0; i < fileList.size(); i++){
				// String userHostName = fileList.get(i);
				// String userConnectionType = fileList.get(i + 1);
				// String filename = fileList.get(i + 2);
				// String quality = fileList.get(i + 3);
				// String description = fileList.get(i + 4);
				out.write(fileList.get(i));
				//i += 4;
			}
			out.close();
			//byte[] bytesArray = fileList.getBytes();
			System.out.println("Added userFileList to allFiles.txt");
		} catch(Exception e) {

		}
	}

	private void removeFiles(String userHostName, String userConnectionType) {
		String fileName = "allServerFiles.txt";
		try {
			List<String> currentFile = new ArrayList<>();
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String stringIn;
			//String dontAdd = userHostName + ", " + userConnectionType;
			//read file and add everything that the user didnt add into another list
			while((stringIn = in.readLine()) != null) {
				if(!stringIn.contains(userHostName)){
					currentFile.add(stringIn);
				}
			}
			in.close();
			//System.out.println(currentFile);
			this.updateAvailableFiles(currentFile, fileName);
		} catch (Exception e){

		}
	}

	private void updateAvailableFiles(List<String> currentFile, String fileName) throws IOException{
		//remove file and recreate and empty one
		String currentDirectory = System.getProperty("user.dir");
		String serverFileList = currentDirectory + "/allServerFiles.txt";
		File file = new File(serverFileList);
		if(file.exists()){
			file.delete();
		}
		file.createNewFile();
		//read updated file into new file
		System.out.println("updating file");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			int i = 0;
			for (i = 0; i < currentFile.size(); i++){
				out.write(currentFile.get(i) + "\n");
			}
			out.close();
			//byte[] bytesArray = fileList.getBytes();
			System.out.println("File properly updated");
		} catch(Exception e) {

		}
	}

	private String queryFiles (String keyword) {
		String fileName = "allServerFiles.txt";
		String currentFile = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String stringIn;
			//read file and add everything that the user didnt add into another list
			while((stringIn = in.readLine()) != null) {
				if(stringIn.contains(keyword)){
					currentFile += stringIn;
				}
			}
			in.close();
		} catch (Exception e){

		}
		return currentFile;
	}

	String fromClient;
	String clientCommand;
	byte[] data;

	private Scanner input;
	private String received;

	// FILE PATH
	File directory = new File(".");

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
		if (!(listOfFiles.isEmpty())) {
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
		int port = 1200;
		while (connectionSocket.isClosed() == false) {
			try {

				fromClient = inFromClient.readLine();
				if (fromClient != null) {
					tokens = new StringTokenizer(fromClient);
					firstln = tokens.nextToken();
					// int port;
					port = Integer.parseInt(firstln);
					clientCommand = tokens.nextToken();
					// fileName = tokens.nextToken();
				}

				// if (fileName == null) {
				// fileName = "noFileFound.txt";
				// }
				//serverFiles(directory, listOfFiles);

				// if (clientCommand.equals("get")) {

				// 	// int PORT = 1234 + 2;

				// 	Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				// 	DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

				// 	String fileName = tokens.nextToken();
				// 	String filePath = directory.getPath() + "/" + fileName;
				// 	File myFile = new File(filePath);

				// 	if (myFile.exists()) {
				// 		byte[] mybytearray = new byte[(int) myFile.length() + 1];
				// 		FileInputStream fis = new FileInputStream(myFile);
				// 		BufferedInputStream bis = new BufferedInputStream(fis);
				// 		bis.read(mybytearray, 0, mybytearray.length);
				// 		System.out.println("Sending...");
				// 		dataOutToClient.write(mybytearray, 0, mybytearray.length);
				// 		dataOutToClient.flush();
				// 		bis.close();
				// 	} else {
				// 		System.out.println("File Not Found");
				// 	}

				// 	dataSocket.close();
				// 	System.out.println("Data Socket closed");
				// }

				if (clientCommand.equals("register")) {
					String userInformation = tokens.nextToken();
					String[] userCredentials = userInformation.split(",");
					User newUser = new User(userCredentials[0], userCredentials[1], userCredentials[3], userCredentials[2]);
					this.addUser(newUser);
					System.out.println("User Registered\n");
					// System.out.println(this.userList);
					// System.out.println(userList);
				}

				if (clientCommand.equals("uploadFileList")) {
					String userHostName = tokens.nextToken();
					String userConnectionType = tokens.nextToken();
					String fileListString = tokens.nextToken();
					String[] clientFileList = fileListString.split(",");
					List<String> fullStringList = new ArrayList<>();
					int i = 0;
					// check that the fileList isn't empty
					if (clientFileList[0].equals("empty")) {
						System.out.println("Client has no files to list");
					} else {
						// make the array of strings hostname,connectiontype,clientfileName
						for (i = 0; i < clientFileList.length; i++) {
							String filename = clientFileList[i];
							String quality = clientFileList[i + 1];
							String description = clientFileList[i + 2];
							String fullEntry = userHostName + "," + userConnectionType + "," + filename + "," + quality + "," + description + ",\n";
							fullStringList.add(fullEntry);
							i += 2;
						}
						// write to servers file system
						addFiles(fullStringList);
						System.out.println("Files Uploaded");
					}
				}

				if (clientCommand.equals("unregister")) {
					// CODE FOR UNREGISTERING CLIENT
					String userInformation = tokens.nextToken();
					String[] userCredentials = userInformation.split(",");
					User newUser = new User(userCredentials[0], userCredentials[1], firstln, userCredentials[2]);
					this.removeUser(newUser);
					this.removeFiles(userCredentials[1], userCredentials[2]);
					System.out.println("User Un-registered");
				}

				if (clientCommand.equals("query")) {
					// CODE FOR SEARCHING FILES
					String keyword = tokens.nextToken();
					String returnQuery = "";
					returnQuery = this.queryFiles(keyword);
					outToClient.writeBytes(returnQuery + "\n");
					outToClient.flush();
					System.out.println("User queried file list");
				}

				if (clientCommand.equals("quit")) {
					System.out.println("Closing connection with a client");
					connectionSocket.close();
				}

				if (clientCommand.equals("retrievePort")){
					String userHostName = tokens.nextToken();
					int i = 0;
					for(i = 0; i < CentralizedServer.userList.size(); i++){
						User user = CentralizedServer.userList.get(i);
						if(user.getUserHostName() != null && user.getUserHostName().contains(userHostName)){
							outToClient.writeBytes(String.valueOf(user.getPortNumber()) + "\n");
							outToClient.flush();
							System.out.println("Retrieved user server port number" + String.valueOf(user.getPortNumber()));
							break;
						}
					}
				}

			}

			catch (IOException e) {
				System.out.println("Error: Unable to disconnect");
				System.out.println(e);
			}
		}
	}
}
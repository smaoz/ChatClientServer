
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private static List<ConnectionHandler> clients = new ArrayList<>();
    private Integer counter = 0;
    private static final int PORT_NUMBER = 23;

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        chatServer.run();
    }

    private void run() throws IOException {
        try(ServerSocket server = new ServerSocket(PORT_NUMBER)) {
            try {
                while (true) {
                    Socket socket = server.accept();
                    counter++;
                    String clientName = "Client #" + counter;
                    ConnectionHandler connectionHandler = new ConnectionHandler(this, socket, server, clientName);
                    clients.add(connectionHandler);
                    Thread thread = new Thread(connectionHandler);
                    thread.start();
                }
            } catch (SocketException e) {
                System.out.println("Server was disconnected.");
            }
        }
    }

    public void gotMessage(String message, ConnectionHandler connectionHandler, ServerSocket serverSocket) {

        if (message.toLowerCase().equals("list")) {
            sendClientsList(connectionHandler);
        } else if (message.toLowerCase().startsWith("name:")) {
            changeClientName(message, connectionHandler);
        } else if (message.toLowerCase().equals("quit")) {
            clientQuit(connectionHandler);
        } else if (message.toLowerCase().equals("shutdown")){
            serverShutdown(serverSocket);
        } else {
                setMessage(message, connectionHandler);
        }
    }

    private void sendClientsList(ConnectionHandler connectionHandler) {
        connectionHandler.sendMessage("Connected clients:");
        for (ConnectionHandler client: clients) {
            if(!client.getClientName().equals(connectionHandler.getClientName())) {
                connectionHandler.sendMessage(client.getClientName());
            }
        }
    }

    private void setMessage(String message, ConnectionHandler connectionHandler) {
        for (ConnectionHandler client : clients) {
            if (connectionHandler != client) {
                client.sendMessage(connectionHandler.getClientName() + " : " + message);
            }
        }
    }

    private void changeClientName(String message, ConnectionHandler connectionHandler) {
        String newName = message.split("name:")[1].trim();
        for (ConnectionHandler client: clients) {
            if (client.getClientName().equals(newName)) {
                connectionHandler.sendMessage("Name already taken! Please enter a different name.");
                return;
            }
        }

        String oldName = connectionHandler.getClientName();
        connectionHandler.setClientName(newName);
        for (ConnectionHandler client : clients) {
            if (connectionHandler != client) {
                client.sendMessage(oldName + " renamed to '" + newName + "'");
            }
        }
        connectionHandler.sendMessage("Name was changed to '" + newName + "'");

    }

    private void clientQuit(ConnectionHandler connectionHandler) {
        clients.remove(connectionHandler);

        for (ConnectionHandler client : clients) {
            if (connectionHandler != client) {
                client.sendMessage(connectionHandler.getClientName() + " disconnected");
            }
        }
    }

    private void serverShutdown(ServerSocket serverSocket) {
        for (ConnectionHandler client : clients) {
            client.sendMessage("Server was disconnected.");
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

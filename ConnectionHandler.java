package chat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private final PrintStream clientOutput;
    private final BufferedReader clientInput;
    private ChatServer chatServer;
    private ServerSocket serverSocket;
    private String clientName;
    private static final String ENCODING = "UTF8";

    public ConnectionHandler(ChatServer chatServer, Socket socket, ServerSocket server, String clientName) throws IOException {
        this.clientName = clientName;
        this.serverSocket = server;
        this.chatServer = chatServer;
        this.clientOutput = new PrintStream(socket.getOutputStream(), true, ENCODING);
        this.clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENCODING));
    }

    @Override
    public void run() {
        String line = null;
        while(true) {
            try {
                if ((line = clientInput.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            chatServer.gotMessage(line, this, serverSocket);
            assert line != null;
            if(line.equals("shutdown") || line.equals("quit")) {
                break;
            }
        }
        try {
            clientInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientOutput.close();
    }

    public void sendMessage(String message) {
        clientOutput.println(message);
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
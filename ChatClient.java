package chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ChatClient {

    private static final String HOST_IP = "localhost";
    private static final int PORT_NUMBER = 23;
    private static final String ENCODING = "UTF8";
    private static boolean connected = true;

    public static void main(String[] args)  {
        try (Socket socket = new Socket(HOST_IP, PORT_NUMBER);
             BufferedReader socketInput = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), ENCODING));
             PrintStream socketOutput = new PrintStream(
                     socket.getOutputStream(), true, ENCODING);
             BufferedReader input = new BufferedReader(new InputStreamReader(System.in))


        ) {
            System.out.println("Welcome! Please enter your name in the following format - 'name: <your-name>':");
            Thread thread = new Thread(() -> {
                String line = "";
                while(connected) {
                    try {
                        line = input.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    socketOutput.println(line);
                    if (line.equals("quit") || line.equals("shutdown")) {
                        connected = false;
                    }
                }
            });

            thread.start();

            while(connected) {
                try {
                    String line = socketInput.readLine();
                    if (line == null || line.equals("Server was disconnected.")) {
                        connected = false;
                    }
                    System.out.println(line);
                } catch (IOException e) {

                }
            }

            thread.join();
        } catch (InterruptedException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
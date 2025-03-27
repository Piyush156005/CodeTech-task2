import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Start thread to handle incoming messages
            new Thread(new IncomingMessagesHandler(in)).start();

            // Get username
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            out.println(username);

            // Main loop for sending messages
            String message;
            while (true) {
                message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) { // Check for exit command
                    System.out.println("Disconnecting from server...");
                    break; // Exit the loop to close the connection
                }
                out.println(message);
            }

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    // Handler for incoming messages
    private static class IncomingMessagesHandler implements Runnable {
        private BufferedReader in;

        public IncomingMessagesHandler(BufferedReader in) {
            this.in = in;
        }

        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equals("SUBMIT_USERNAME")) {
                        continue;
                    } else if (message.equals("USERNAME_ACCEPTED")) {
                        System.out.println("Connected to chat server. Start typing messages!");
                        continue;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Error reading messages: " + e.getMessage());
            }
        }
    }
}
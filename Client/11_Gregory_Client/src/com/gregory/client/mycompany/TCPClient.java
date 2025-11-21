package com.gregory.client.mycompany;

import java.io.*;
import java.net.*;

/**
 *
 * @author gregeek
 */
public class TCPClient {

    private static final int Port = 2050;//
    private static InetAddress host;//abc

    public static void main(String[] args) {

        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            System.out.println("Host not found!");
            System.exit(1);
        }
        run();
    }

    private static void run() {
        Socket link = null;
        try {
            link = new Socket(host, Port);
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);	 //Step 2.

            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
            String command = null;

            boolean running = true;
            while (running) {

                System.out.println("\nPlease enter command for the server: ");
                command = userEntry.readLine();
                
                if(command.startsWith("import")){
                    String loansURL = command.substring(7).trim();//skips
                    HTTPLibraryFetch f = new HTTPLibraryFetch();
                    f.HttpGet(loansURL, out, in);
                    continue;

                }
                
                out.println(command);
                String reply = in.readLine();

                System.out.println("\nServer reply: " + reply);
                if (reply.equalsIgnoreCase("TERMINATING")) {
                    System.out.println("Terminating session as requested by server.");
                    running = false;
                    System.exit(0);
                    break;
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}

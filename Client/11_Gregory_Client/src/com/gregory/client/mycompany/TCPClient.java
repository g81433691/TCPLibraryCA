/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
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

        //HTTP GET integreated import on command, to access loans.txt at a publicly accesible url using URL or URLConnection
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

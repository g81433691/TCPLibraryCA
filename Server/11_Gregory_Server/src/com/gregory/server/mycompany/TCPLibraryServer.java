/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gregory.server.mycompany;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author gregeek
 */
public class TCPLibraryServer {

    private static ServerSocket serverSocket;
    private static final int Port = 2050;
    private static int client_Connection_Id = 0;
    private static HashMap<String, ArrayList<BookDescription>> loansRecord = new HashMap<>();
    public static void main(String[] args) {
        System.out.println("Attempting to open Port!\n");
        try {
            serverSocket = new ServerSocket(Port);
        } catch (IOException ex) {
            System.out.println("");
            System.exit(1);
        }
        do {
            run();
        } while (true);
    }

    public static void run() {
        Socket link = null;
        try {
            while (true) {
                link = serverSocket.accept();
                client_Connection_Id++;
                String IDClient = "Client " + client_Connection_Id;
                Runnable bookRecord = new ClientConnectionRun(link, IDClient, loansRecord);
                Thread thread = new Thread(bookRecord);
                thread.start();
            }
        } catch (IOException ex1) {
            ex1.printStackTrace();
            try {
                System.out.println("\n* Connectoin closing!");
                link.close();
            } catch (IOException ex) {
                System.out.println("Unable to connect, please see exception: " + ex);
            }
        }

    }
}

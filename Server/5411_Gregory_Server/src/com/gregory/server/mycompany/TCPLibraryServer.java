package com.gregory.server.mycompany;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author gregorym
 */
public class TCPLibraryServer {

    private static ServerSocket serverSocket;
    private static final int Port = 2050;//assign same port
    private static int client_Connection_Id = 0;//var to count the amount of active connections
    private static HashMap<String, ArrayList<BookDescription>> loansRecord = new HashMap<>();//declartion of hashmap with string as key to list of book desc obj's
    
    // i also think a concurrent map could be better as itll help with synchronixation and readwrite across clients
    // i believe concurrent hashmaps are thread safe by default but i had already learned how to manually use theh synchronization blocks
    // ref: see design report for reference to (Shane Crouch - The Coding Zoo, 2020):  tutorial on thr use of maps
    
    public static void main(String[] args) {
        System.out.println("Trying to open server port!!\n");
        try {
            serverSocket = new ServerSocket(Port);//setting the port
        } catch (IOException ex) {
            System.out.println("Failed to open port, error encounted: " +ex.getMessage());//keep the end user informed
            System.exit(1);//exit
        }
        do {//now get the ball rolling
            run();//call upon method
        } while (true);//keep it runnning
    }

    public static void run() {
        
        Socket link = null;
        
        try {
            while (true) {
                //waits for a client connection to emerge, then will give back a socket
                //which is representitive of the client
                link = serverSocket.accept();
                client_Connection_Id++;//increment connection counter
                String IDClient = "Client " + client_Connection_Id;//making the name more readable
                //runnable obj to handle client interaction with the server, with the passed in arguments
                //the socket, the identifier of the client and the map
                Runnable library = new ClientConnectionRun(link, IDClient, loansRecord);
                //wrapping the runnable obj in a thread, which will allow each client to run on its own thread, thus completing multithreaded client objective
                Thread clientThread = new Thread(library);
                //start the thread and kick things off
                clientThread.start();
            }
        } catch (IOException ex1) {
            
                ex1.getMessage();
                
            try {
                System.out.println("\n* Terminating the server!");
                link.close();
            } catch (IOException ex) {
                System.out.println("We have unfortunely encountered an errort, please see the error message attached: " + ex);//better messages for the end user
            }
        }

    }
}

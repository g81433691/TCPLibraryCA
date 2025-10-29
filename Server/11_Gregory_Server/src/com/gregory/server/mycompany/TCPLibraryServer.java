/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gregory.server.mycompany;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author gregeek
 */
public class TCPLibraryServer {
     private static ServerSocket serverSocket;
        private static final int Port = 1440;
        private static int client_Connection_Id = 0;
        
            public static void main(String[] args) {
                System.out.println("Attempting to open Port!\n");
                try{
                   serverSocket = new ServerSocket(Port);
                }catch(IOException ex){
                    System.out.println("");
                    System.exit(1);
                }
                do{
                    run();
                }while(true);
            }
                
            
            public static void run(){
                Socket link = null;
                try{
                    
                    link = serverSocket.accept();
                    client_Connection_Id++;
                    String clientID = "Client "+client_Connection_Id;
                    Runnable bookRecord = new ClientConnectionRun(link, clientID);
                    Thread thread = new Thread(bookRecord);
                    thread.start();
                        }catch(IOException ex1){
                                ex1.printStackTrace();
                                try{
                                    System.out.println("\n* Connectoin closing!");
                                    link.close();
                                }catch(IOException ex){
                                    System.out.println("Unable to connect, please see exception: " +ex);
                                }
                        }

            }
}

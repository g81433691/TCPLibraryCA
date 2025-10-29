/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gregory.server.mycompany;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author gregeek
 */
public class ClientConnectionRun implements Runnable {//

    Socket client_link = null;
    String client_id;

    public ClientConnectionRun(Socket Conn, String client_id) {
        this.client_link = Conn;
        this.client_id = client_id;
    }

   
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client_link.getInputStream()));
            PrintWriter out = new PrintWriter(client_link.getOutputStream(), true);
                        
            String command = in.readLine();
            System.out.println("Command Received from client: " +client_id +" Message: " +command);
            
            //Need to add in ACTIONS, which will consist of STOP, BORROW, RETURM and LIST,  will be case insensitive and shave off spaces using trim(), need to demonstrate understadning of synchronization
            //local storage like an Arraylist for Borrowed
            //will break the users command into 4 parts so "Borrrow' Greg' 12th june 2025; clean code  " into 4 parts, it should be case insensitive and trim() spaces off the edges , so like action[],name[].date[],titlte[]<
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

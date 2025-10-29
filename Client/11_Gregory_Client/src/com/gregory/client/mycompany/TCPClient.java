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

    private static final int Port = 1440;
    private static InetAddress host;

    public static void main(String[] args) {

        //HTTP GET integreated import on compilation, to access loans.txt at a publicly accesible url using URL or URLConnection
        
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
            link = new Socket(host, Port);		//Step 1.
            
            //link1 = new Socket( "0.0.0.0", Port);//need to check breif to see whhich way it needs to be done!
            
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));//Step 2.
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);	 //Step 2.

           
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
            String command = null;
            String reply = null;

            System.out.println("Please enter command for the server: ");
            command = userEntry.readLine();
            out.println(command);
            reply = in.readLine();
            System.out.println("\server reply: " + reply);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } 

    }
}

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
import java.util.ArrayList;

/**
 *
 * @author gregeek
 */
public class ClientConnectionRun implements Runnable {//

    Socket client_link = null;
    String client_id;
    ArrayList<Description> Desc;

    public ClientConnectionRun(Socket Conn, String client_id, ArrayList Desc) {
        this.client_link = Conn;
        this.client_id = client_id;
        this.Desc = Desc;
    }

    @Override
    public void run() {
        try {
            boolean running = true;
            while (running) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client_link.getInputStream()));
                PrintWriter out = new PrintWriter(client_link.getOutputStream(), true);

                String command;

                while ((command = in.readLine()) != null) {
                    System.out.println("Command Received from client: " + client_id + " Message: " + command);
                    //will break the users command into 4 parts so "Borrrow' Greg' 12th june 2025; clean code  " into 4 parts, it should be case insensitive and trim() spaces off the edges , so like action[],name[].date[],titlte[]<
                    String[] Description = command.split(";", 4);
                    String ACTION = Description[0].toUpperCase().trim();
                    if (ACTION.equalsIgnoreCase("STOP")) {
                        out.println("TERMINATING");
                        client_link.close();
                        System.out.println("TERMINATING: " + client_id);
                        running = false;
                        return;
                    }
                    String Username = Description[1].trim();
                    String Date = Description[2].toUpperCase().trim();
                    String BookTitle = Description[3].trim();

                    try {
                        switch (ACTION) {
                            case "BORROW":
                                out.println("User has chosen borrowed action");
                                Desc.add(new Description(Username, Date, BookTitle));
                                break;
                            case "LIST":
                                for (int i = 0; i < Desc.size(); i++) {
                                    if (Username.equals(Desc.get(i).getUsername())) {
                                        out.println("This User currently has on loan:");
                                    } else {
                                        out.println("User not found!");
                                    }
                                }
                                break;
                            case "RETURN":
                                out.println("User has chosen return action");
                                //will complete the return action after the list is working as expected
                                break;
                            default:
                                throw new InvalidCommandException("Unknown Command:" + command);
                        }
                    } catch (InvalidCommandException ex) {
                        out.println("Command error:" + ex);
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

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
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author gregeek
 */

public class ClientConnectionRun implements Runnable {
    
    //class implements runnable so the runnable object can be ran in a thread so every client will be on its own thread,
 

    Socket clientConn = null;
    String id_client;
    private static HashMap<String, ArrayList<BookDescription>> loansRecord;  // changed from arraylist to HashMap , while mapping a String to a description obj, will represent the all loansRecord currently being borrowed by the user

    public ClientConnectionRun(Socket Conn, String id_client, HashMap<String, ArrayList<BookDescription>> loansRecord) {
        this.clientConn = Conn;
        this.id_client = id_client;
        this.loansRecord = loansRecord;
    }

    @Override
    public void run() {//run method for each client
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientConn.getInputStream()));//declaring streams for client
            PrintWriter out = new PrintWriter(clientConn.getOutputStream(), true);

            String command;//holder variable for the end users command
            while ((command = in.readLine()) != null) {  //one line per message, so one loop
                System.out.println("Command Received from client: " + id_client + " Message: " + command);//notify the end user

                String[] BookDescription = command.split(";");//use the .split() method to split the command into 4 parts.

                String serverAction = BookDescription[0].toUpperCase().trim();//assigning the first part to serverAction

                    if (serverAction.equalsIgnoreCase("STOP")) {//just checking early if the user wants to stop as the breif is specific on the server replying "TERMINATE"

                        out.println("TERMINATING");//here the server replies with terminating and theres a condition on the client side toSystem.exit(0);

                        clientConn.close();//close the clients scoket

                        System.out.println("Terminating: " + id_client);//notify the end user
                        return;
                    }

                    try {
                        InvalidCommand(BookDescription, serverAction);//a method to validate the end users command and throw a accurate custom exception as per their scenario
                    } catch (InvalidCommandException e) {
                        out.println("Error: " + e.getMessage());//notify end user
                        continue;//carry on, this line took me a while to figure out
                    }

                    String Username = BookDescription[1].trim();//assign the first part of the split command to username
                    String Date = BookDescription[2].trim();//assign the second part of the split command to the date of the borrow record
                    String BookTitle = BookDescription[3].trim();//assign the third to be assigned to the loansRecord title, this is as per the breif

                        switch (serverAction) {//a simple switch case to determnine our next move based on the first section of the split command , so itll get the commands desired "serverAction"
                            case "BORROW":
                                synchronized (loansRecord) {//synchronized block to hold the monitor and ensure consistent access to the map across clients/threads

                                    if (!loansRecord.containsKey(Username)) {//if the name lines up
                                        loansRecord.put(Username, new ArrayList<BookDescription>());//slot it into the map with the name being the identifier
                                    }

                                    ArrayList<BookDescription> loanedBooks = loansRecord.get(Username);
                                    loanedBooks.add(new BookDescription(Username, Date, BookTitle));
                                }

                                listUserLoans(Username, out);//call upon the method to list what loansRecord the user currently has on laon
                                break;

                            case "LIST":
                                listUserLoans(Username, out);//i made it into a method after realizing it will need to be reused accross multiple actions
                                break;
                            case "RETURN":
                                ArrayList<BookDescription> loanedBooks = loansRecord.get(Username);//access the user loansRecord by the identifier/key

                                if (loanedBooks == null || loanedBooks.isEmpty()) {//check if empty
                                    out.println("This user has no book currently to return.");
                                } else {//if not empty
                                    
                                    boolean wasBookReturned = false;//boolean for the state of the return
                                    
                                    for (int i = 0; i < loanedBooks.size(); i++) {//a simple loop over the users loansRecord they currently have on loan
                                        
                                        if (loanedBooks.get(i).getBookTitle().equalsIgnoreCase(BookTitle)) {//when the index matches the name of the booktitle to be wasBookReturned
                                            loanedBooks.remove(i);//remove it at index
                                            wasBookReturned = true;//set the state of wasBookReturned to true
                                            
                                            if (loanedBooks.isEmpty()) {//and additionally if they have no loansRecord anymore
                                                loansRecord.remove(Username);//remove them from the system
                                            }
                                            break;
                                        }
                                    }
                                    listUserLoans(Username, out);//print the updated list of loansRecord currently on loan from the user
                                }
                                break;
                           }
                      }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }

        public static void listUserLoans(String Username, PrintWriter out) {//broke it into a method to be reused
            ArrayList<BookDescription> allBooks;
            synchronized (loansRecord) {
                allBooks = loansRecord.get(Username);
            }

            if (allBooks == null || allBooks.isEmpty()) {//make sure the end user currently has loansRecord on loan
                out.println(Username + " currently has no loansRecord on loan.");
            } else {//if they do 
                
                String bookList;//a placeholder for the list of loansRecord
                synchronized (allBooks) {//syncrhonized block to hold the methods monitor
                    bookList = allBooks.stream().map(book -> "Book: " + book.getBookTitle() + " - Date: " + book.getDate()).collect(Collectors.joining(" | "));//make the list a stream, and convert every bookname into a string (->), and use collect to concatanate them but i added in the spaces
                }
                out.println(Username + " currently has: " + bookList); //notify the ned user of the book list (loansRecord currently on loan)
            }
        }

        public static void InvalidCommand(String[] BookDescription, String serverAction) throws InvalidCommandException {// a method to further break down the issue for the end user and point it out
            if (BookDescription == null || BookDescription.length == 0 || serverAction == null || serverAction.trim().isEmpty()) {//make sure the user has input a command
                throw new InvalidCommandException("Command cannot be empty");//throws custom exception
            }

            if (BookDescription.length < 4) {//make sure theyve enetered the amount of fields anticipated by the server
                throw new InvalidCommandException("Command requires 4 fields, use '--' for missing fields");
            }
            
            if (BookDescription.length > 4) {//make sure they havent entered over the amount of expected fields
                throw new InvalidCommandException("Too many fields, only 4 expected: command,Username,date,bookTitle");
            }

            if (!serverAction.equals("BORROW") && !serverAction.equals("LIST") && !serverAction.equals("RETURN") && !serverAction.equals("STOP") && !serverAction.equals("IMPORT")) {//if the serverAction or the first field of the command isnt recognized , notify the eend user
                throw new InvalidCommandException("Unknown command: " + serverAction);//notify the end user and throw a accurate exception n message
            }

            if (serverAction.equals("BORROW") || serverAction.equals("RETURN")) {//if the action is one of these
                if (BookDescription[1].trim().isEmpty()) {//and the first section is empty
                    throw new InvalidCommandException("Username cannot be empty");//throw custom exception
                }
                if (BookDescription[2].trim().isEmpty()) {//if the second section is empty
                    throw new InvalidCommandException("Take date cannot be empty");//throw new message n excepttion
                }
                if (BookDescription[3].trim().isEmpty()) {//and check the third
                    throw new InvalidCommandException("Book name cannot be empty");//throw exception
                }
            }

        }

}

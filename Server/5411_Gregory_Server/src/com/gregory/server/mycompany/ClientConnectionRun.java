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
 * @author gregorym
 */
public class ClientConnectionRun implements Runnable {
    
    //class implements runnable so the runnable object can be wrapped in a thread so every client will be on its own thread,
 
    Socket clientConn = null;
    String id_client;
    private static HashMap<String, ArrayList<BookDescription>> loansRecord;  
    
    //so this is my first time ever using a map, so changed from arraylist to HashMap 
    //while mapping a String to a book description obj, will represent the all books currently being borrowed by the user
    //ref: see design report for reference to (Shane Crouch - The Coding Zoo, 2020):  tutorial on thr use of maps

    
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

            String command;//holder variable for the end users command to the serv
            
            while ((command = in.readLine()) != null) {  
                
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
                    //originally attempted to pass in the variables themselves into the validation method
                    //But the use of .split() was throwing a exc ArrayList.OutOfBounds, so i moved it up and just pass in the description array and action, its a lot easier this way
                    //assign the first part of the split command to username
                    String Username = BookDescription[1].trim();
                    //assign the second part of the split command to the date of the borrow record
                    String Date = BookDescription[2].trim();
                    //assign the third to be assigned to the books title, this is as per the breif
                    String BookTitle = BookDescription[3].trim();

                        switch (serverAction) {//a simple switch case to determnine our next move based on the first section of the split command , so itll get the commands desired "serverAction"
                            case "BORROW":
                                synchronized (loansRecord) {//synchronized block to hold the monitor and ensure consistent access to the map across clients/threads

                                    if (!loansRecord.containsKey(Username)) {//if the name lines up
                                        loansRecord.put(Username, new ArrayList<BookDescription>());//slot it into the map with the name being the identifier
                                    }

                                    ArrayList<BookDescription> loanedBooks = loansRecord.get(Username);
                                    loanedBooks.add(new BookDescription(Username, Date, BookTitle));//add to list
                                }

                                listUserLoans(Username, out);//call upon the method to list what books the user currently has on loan
                                break;

                            case "LIST":
                                listUserLoans(Username, out);//i made it into a method after realizing it will need to be reused accross multiple actions
                                break;
                            case "RETURN":
                                ArrayList<BookDescription> loanedBooks = loansRecord.get(Username);//access the user loansRecord by the identifier/key
                                synchronized(loansRecord){

                                if (loanedBooks == null || loanedBooks.isEmpty()) {//check if empty
                                    out.println("This user has no book currently to return.");
                                } else {//if not empty
                                    
                                    boolean wasBookReturned = false;//boolean for the state of the return
                                    
                                    for (int i = 0; i < loanedBooks.size(); i++) {//a simple loop over the users books they currently have on loan
                                        
                                        if (loanedBooks.get(i).getBookTitle().equalsIgnoreCase(BookTitle)) {//when the index matches the name of the booktitle to be wasBookReturned
                                            loanedBooks.remove(i);//remove it at index
                                            wasBookReturned = true;//set the state of " was the book returned yet" to true
                                            
                                            if (loanedBooks.isEmpty()) {//and additionally if they have no books anymore
                                                loansRecord.remove(Username);//remove them from the system
                                            }
                                            break;
                                        }
                                    }
                                }
                                    listUserLoans(Username, out);//print the updated list of books currently on loan from the user
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
                allBooks = loansRecord.get(Username);//get the books of the key
            }

            if (allBooks == null || allBooks.isEmpty()) {//make sure the end user currently has books on loan
                out.println(Username + " currently has no books on loan.");
            } else {//if they do 
                
                String loansList;//a placeholder for the list of books
                synchronized (allBooks) {//syncrhonized block to hold the methods monitor
                    loansList = allBooks.stream().map(book -> "Book: " + book.getBookTitle() + " - Date: " + book.getDate()).collect(Collectors.joining(" | "));//make the list a stream, and convert every bookname into a singular String using Joining; REF; to howtodoinjava article in design report
                }
                out.println(Username + " currently has: " + loansList); //notify the end user of the loans list (books currently on loan)
            }
        }

        public static void InvalidCommand(String[] BookDescription, String serverAction) throws InvalidCommandException {// a method to further break down the issue for the end user and point it out
            //i think ive covered most cases of malformed input or invalid commands
            //if i have time ill error check the input characters to no numbers in the name
            //the correct formatting of the date so "05 september 2023 or DD MMMM YYYY" 
            //and length limits so you cant overload the scanner
            
            if (BookDescription == null || BookDescription.length == 0 || serverAction == null || serverAction.trim().isEmpty()) {//make sure the user has input a command
                throw new InvalidCommandException("Please ensure you entered a valid command. A empty command is invalid");//throws custom exception
            }

            if (BookDescription.length < 4) {//make sure theyve enetered the amount of fields anticipated by the server
                throw new InvalidCommandException("Please enter 4 fields. Please just use the '--' placeholder. Examples: LIST;Greg;--;-- or RETURN;Greg;--;Bitcoin tips");
            }
            
            if (BookDescription.length > 4) {//make sure they havent entered over the amount of expected fields
                throw new InvalidCommandException("The maximum number of fields is 4 and the epected format is Borrow;Name;Date;BookTitle");
            }

            if (!serverAction.equals("BORROW") && !serverAction.equals("LIST") && !serverAction.equals("RETURN") && !serverAction.equals("STOP") && !serverAction.equals("IMPORT")) {//if the Action or the first field of the command isnt recognized
                throw new InvalidCommandException("The acttion of your command is unkown: " + serverAction);//notify the end user and throw a accurate exception n message
            }

            if (serverAction.equals("BORROW") || serverAction.equals("RETURN")) {//if the action is one of these
                if (BookDescription[1].trim().isEmpty()) {//and the first section is empty
                    throw new InvalidCommandException("Please ensure youve entered a username");//throw custom exception
                }
                
                if (BookDescription[1].length() >= 28 ) {//make sure the name isnt longer than 28 characters
                    throw new InvalidCommandException("Please ensure the username you entered is below 28 characters ");//throw custom exception
                }
                if (BookDescription[2].trim().isEmpty()) {//if the second section is empty
                    throw new InvalidCommandException("Please ensure youve entered a valid date");//throw new message n excepttion
                }
                if (BookDescription[3].trim().isEmpty()) {//and check the third
                    throw new InvalidCommandException("Please ensure youve entered a Book title");//throw exception
                 }
                
        }
    }
}

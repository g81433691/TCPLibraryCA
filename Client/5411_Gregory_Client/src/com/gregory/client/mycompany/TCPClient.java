package com.gregory.client.mycompany;

import java.io.*;
import java.net.*;

/**
 *
 * @author gregeek
 */
public class TCPClient {
    //
    private static final int Port = 2050;//variable to just store the same port value
    private static InetAddress localHost;//local host

    public static void main(String[] args) {

        try {
            localHost = InetAddress.getLocalHost();//grabbing local host and assigning it to variable
        } catch (UnknownHostException ex) {
            System.out.println("Host not found!");
            System.exit(1);
        }
        run();//kick things off
    }

    private static void run() {
        Socket link = null;
        try {
            link = new Socket(localHost, Port);//first step: make the socket for the client and pass in the port and host
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));//wrapping the sockets inp stream in a buffered reader, to read the incoming response for the server
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);//wrapping the sokcets outp stream in a printWriter, the true is auto enabling .flush() as to stop congestion

            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));//second step: reading from keybaord and wrapping in a input str reader and br, allowing  us to read line of user input from the console
            String command = null;//init variable to hold command to pass to server

            boolean running = true;//just a boolean for the state of the connection
            while (running) {

                System.out.println("\nPlease enter command for the server: ");
                command = userEntry.readLine();//read the line from the keyboard/console
                
                //so then im gonna slot in some simple conditions
                //just to first see if the command starts with the import desired server action
             
                if(command.startsWith("import")){
                    String loansURL = command.substring(7).trim();//skips and starts the url string at 7th index
                    HTTPLibraryFetch f = new HTTPLibraryFetch();
                    f.start(loansURL, out, in);
                    continue;

                }
                
                //Third step: send the message and read the line sent back from the server
                
                out.println(command);
                String reply = in.readLine();

                System.out.println("\nServers message: " + reply);//print it back out to the client/enduser to see
                //so the breif was quite clear on the desired flow of the client termination logic
                //STOP -> server replies : TERMINATION - > client: if the server replies terminating then system.exit, -> server side displays "Client9 has been termianted"
                if (reply.equalsIgnoreCase("TERMINATING")) {//Fourth step: if server replies terminating, terminate
                    System.out.println("Terminating session as requested by server.");//notify end user
                    running = false;//update
                    System.exit(0);//quit 
                    break;
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}

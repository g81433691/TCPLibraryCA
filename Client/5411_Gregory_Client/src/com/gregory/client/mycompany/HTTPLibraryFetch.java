package com.gregory.client.mycompany;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author gregeek
 */
public class HTTPLibraryFetch {
    
    public void start(String loanURL, PrintWriter serverOut, BufferedReader serverIn) throws IOException {

        URL url = new URL(loanURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        //https://pastebin.com/raw/t3k7nAeZ heres the URL
        
        conn.setRequestMethod("GET");//setting it to GET request
        conn.setConnectTimeout(2000);//5000 felt like something was going wrong so i shortened it to 2
        conn.setReadTimeout(2000);

        if (conn.getResponseCode() != 200) {
            //check the status code of the connection, if its not 200 OK somethings wrong
            //the type of response code helps us identify weather we hit the end point as anticipated
            System.out.println("UNSUCCESFUL, unfortunalely import has failed, response code " + conn.getResponseCode());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            //according to the breif the loans.txt file is formatted with a name on the first line, and subsequent borrowing records for that user
            // first line is the name and all subsequent lines will be the name and date
            String Username = reader.readLine();
            if (Username == null || Username.trim().isEmpty()) {//make sure the first line is a name
                System.out.println("Import of Loans.txt has failed, Username is missing on the first line, aborting import!");//notify the end user
                return;
            }
            Username = Username.trim();//rid of spaces

            System.out.println("Trying to import loans for Username: " + Username);//notify of progress

            String n;
            int lines = 2;  // since we already read first n/line which was just Username
            int read = 0; // starts at 0, how many we have read / imported thus far

            while ((n = reader.readLine()) != null) {//until no lines left

                if (n.isEmpty()) {//this is a condition ro skip empty lines
                    lines++;
                    continue;//carry on
                }

                String[] sections = n.split(";");//as per breif ensure and give reasoning
                if (sections.length != 2) {//a condition to ensure subsequent lines are valid and if theyre not a skip message declaring invalid format
                    System.out.println("Lines skipped: " + lines + " Reasoning: " + n);
                    lines++;
                    continue;
                }

                String date = sections[0].trim();//break them down into sections
                String book = sections[1].trim();

                if (date.isEmpty() || book.isEmpty()) {
                    System.out.println("Lines skipped " + lines + " Fields absent: " + n);//make sure both fields are populated
                    lines++;//mark as done
                    continue;//carry on
                }

                String borrwCMD = "borrow;" + Username + ";" + date + ";" + book;//a command to insert the loan into our system

                serverOut.println(borrwCMD);//send it out to the server
                serverOut.flush();//flush just incase thats whats causing my issue

                System.out.println("Server message: " + serverIn.readLine());// get confirmation from the server of the status of the import

                read++;
                lines++;
            }

            System.out.println("SUCCESFULLY imported: " + read +" loans from the URL: " +loanURL);//notify the end user
        }
    }
}

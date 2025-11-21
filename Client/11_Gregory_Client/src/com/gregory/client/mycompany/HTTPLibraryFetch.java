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
    public void HttpGet(String urlString, PrintWriter serverOut, BufferedReader serverIn) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();//https://pastebin.com/raw/t3k7nAeZ heres the URL
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (conn.getResponseCode() != 200) {
            System.out.println("Import failed: server returned " + conn.getResponseCode());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

            // first line is the name and all subsequent lines will be the name and date
            String Username = reader.readLine();
            if (Username == null || Username.trim().isEmpty()) {//make sure the first line is a name
                System.out.println("Import abandoned: first n 'Username' is missing.");//notify the end user
                return;
            }
            Username = Username.trim();//rid of spaces

            System.out.println("Importing loans for Username: " + Username);//notify of progress

            String n;
            int lines = 2;  // since we already read first n (line)
            int read = 0; // how many we have read / imported thus far

            while ((n = reader.readLine()) != null) {

                n = n.trim();

                if (n.isEmpty()) {//this is a condition ro skip empty lines
                    lines++;
                    continue;//carry on
                }

                String[] parts = n.split(";");
                if (parts.length != 2) {//a condition to ensure subsequent lines are valid and if theyre not a skip message declaring invalid format
                    System.out.println("Skipped lines: " + lines + " Reasoning: " + n);
                    lines++;
                    continue;
                }

                String date = parts[0].trim();//break them down into sections
                String book = parts[1].trim();

                if (date.isEmpty() || book.isEmpty()) {
                    System.out.println("Skipped n " + lines + " (missing fields): " + n);//make sure both fields are populated
                    lines++;
                    continue;//carry on
                }

                String BORROWCommand = "BORROW;" + Username + ";" + date + ";" + book;//a command to insert the loan into our system

                serverOut.println(BORROWCommand);//send it out to the server
                serverOut.flush();//flush just incase thats whats causing my issue

                System.out.println("Server reply: " + serverIn.readLine());// get confirmation from the server of the status of the import

                read++;
                lines++;
            }

            System.out.println("Imported " + read + " loans from " + urlString);//notify the end user
        }
    }
}

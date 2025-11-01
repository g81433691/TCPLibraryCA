/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gregory.server.mycompany;

/**
 *
 * @author gregeek
 */
public class InvalidCommandException extends Exception {

    private int errorNumber;

    public InvalidCommandException(String message) {
        super(message);
    }

    public static void InvalidCommand(String[] Description, String ACTION, String Username, String Date, String BookTitle) throws InvalidCommandException {
        if (ACTION == null) {
            throw new InvalidCommandException("Nothing entered for input. Expected format is Action;UserName;Date;Booktitle");
        }

        if (!ACTION.equalsIgnoreCase("BORROW") && !ACTION.equalsIgnoreCase("LIST") && !ACTION.equalsIgnoreCase("RETURN") && !ACTION.equalsIgnoreCase("STOP")) {
            throw new InvalidCommandException("Invalid action chosen: " + ACTION + "Please  ensure the acrion matches Borrow,List,Return or Stop");
        }
        if (Username.trim().isEmpty()) {
            throw new InvalidCommandException("Please ensure you enter a username to your command in the following foemat: Action; User name; Date; Book title");
        }

        if (ACTION.trim().equalsIgnoreCase("BORROW") || ACTION.trim().equalsIgnoreCase("RETURN")) {
            if (Date.trim().isEmpty()) {
                throw new InvalidCommandException("Please ensure you enter a Date to your command in the following foemat: Action; User name; Date = dd mm yyyy; Book title");
            }
            if (BookTitle.trim().isEmpty()) {
                throw new InvalidCommandException("Please ensure you enter a username to your command in the following foemat: Action; User name; Date; Book title");
            }
        }
        if (!ACTION.equalsIgnoreCase("STOP")) {
            if (Description.length < 3) {
                throw new InvalidCommandException("Please ensure you enter your action followed by 3 fields of the description; For eg., Borrow;Greg;12 June 2025;Java for dummies ");
            }
        }
    }
}

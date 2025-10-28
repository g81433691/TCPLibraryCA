/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gregory.server.mycompany;

import java.time.LocalDate;

/**
 *
 * @author gregeek
 */
public class ClientConnectionRun implements Runnable {
    
    public String BookName,BorrowerName;
    public LocalDate borrwedDate;

    public ClientConnectionRun(String BookName, String BorrowerName, LocalDate borrwedDate) {
        this.BookName = BookName;
        this.BorrowerName = BorrowerName;
        this.borrwedDate = borrwedDate;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String BookName) {
        this.BookName = BookName;
    }

    public String getBorrowerName() {
        return BorrowerName;
    }

    public void setBorrowerName(String BorrowerName) {
        this.BorrowerName = BorrowerName;
    }

    public LocalDate getBorrwedDate() {
        return borrwedDate;
    }

    public void setBorrwedDate(LocalDate borrwedDate) {
        this.borrwedDate = borrwedDate;
    }

    @Override
    public String toString() {
        return "ClientConnectionRun{" + "BookName=" + BookName + ", BorrowerName=" + BorrowerName + ", borrwedDate=" + borrwedDate + '}';
    }
    
    
    @Override
    public void run() {

    }
}

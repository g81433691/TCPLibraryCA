/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gregory.server.mycompany;

/**
 *
 * @author gregeek
 */
public class Description {

    private String Username;
    private String Date;
    private String BookTitle;

    public Description(String Username, String Date, String BookTitle) {
        this.Username = Username;
        this.Date = Date;
        this.BookTitle = BookTitle;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String BookTitle) {
        this.BookTitle = BookTitle;
    }

    @Override
    public String toString() {
        return "Description{" + "Username=" + Username + ", Date=" + Date + ", BookTitle=" + BookTitle + '}';
    }

}

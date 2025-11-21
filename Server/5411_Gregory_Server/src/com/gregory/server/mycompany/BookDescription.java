package com.gregory.server.mycompany;

/**
 *
 * @author gregorym
 */
public class BookDescription {

    private String Username;
    private String Date;
    private String BookTitle;

    public BookDescription(String Username, String Date, String BookTitle) {
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


}

package com.example.khalaf.bookstore.data;

import java.io.Serializable;

/**
 * Created by KHALAF on 10/27/2017.
 */
// de seraiabliazble de 3shan lw 3ayez 2basy object fel intent
    // ya3ny mn el a5er 3shan 23ref 2b3t object fel intent

public class Book implements Serializable{
    private String id;
    private String title;
    private String Desc;
    private String pdfUri;
    private double price;
    private String publisherid;
    private String imageUrl;
    private String date;
    private String pdftitle;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPdftitle() {

        return pdftitle;
    }

    public void setPdftitle(String pdftitle) {

        this.pdftitle = pdftitle;
    }

    public Book(String id, String title, String desc, double price, String publisherid , String date , String pdftitle) {
        this.id = id;
        this.title = title;
        Desc = desc;
        this.price = price;
        this.publisherid = publisherid;
        this.date=date;
        this.pdftitle=pdftitle;

    }

    public Book() {

    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return Desc;
    }

    public String getPdfUri() {
        return pdfUri;
    }

    public double getPrice() {
        return price;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

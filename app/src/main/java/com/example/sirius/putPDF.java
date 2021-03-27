package com.example.sirius;

public class putPDF {

    public String pdfName;
    public String pdfUrRL;

    public putPDF() {
    }

    public putPDF(String pdfName, String pdfUrRL) {
        this.pdfName = pdfName;
        this.pdfUrRL = pdfUrRL;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getPdfUrRL() {
        return pdfUrRL;
    }

    public void setPdfUrRL(String pdfUrRL) {
        this.pdfUrRL = pdfUrRL;
    }
}

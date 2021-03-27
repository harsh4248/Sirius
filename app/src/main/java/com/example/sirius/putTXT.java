package com.example.sirius;

public class putTXT {

    public String txtName;
    public String txtURL;

    public putTXT() {
    }

    public putTXT(String txtName, String txtURL) {
        this.txtName = txtName;
        this.txtURL = txtURL;
    }

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public String getTxtURL() {
        return txtURL;
    }

    public void setTxtURL(String txtURL) {
        this.txtURL = txtURL;
    }
}

package com.ta.wafafood.Model;

public class Users {
    String nama;
    String noHp;
    String email;
    String pw;
    String uid;
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Users() {

    }

    public Users(String uid, String nama, String noHp, String email, String pw, int status) {
        this.uid = uid;
        this.nama = nama;
        this.noHp = noHp;
        this.email = email;
        this.pw = pw;
        this.status= status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }


    //uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Users{" +
                "nama='" + nama + '\'' +
                ", noHp='" + noHp + '\'' +
                ", email='" + email + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }
}

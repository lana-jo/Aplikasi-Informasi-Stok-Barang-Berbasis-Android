package com.ta.wafafood.Model;
public class User {
    private String id, name, harga,  avatar;
    private Long jumlah;
    public User(){}
    public User(String name, String harga, Long jumlah, String avatar){
        this.name = name;
        this.harga = harga;
        this.jumlah= jumlah;
        this.avatar = avatar;
    }
    public Long getJumlah() {
        return jumlah;
    }
    public void setJumlah(Long jumlah) {
        this.jumlah = jumlah;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getharga() {
        return harga;
    }
    public void setharga(String harga) {
        this.harga = harga;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
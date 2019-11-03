package com.example.heart_dog;

public class ItemData {
    private String dogName;
    private String dsn;
    private int count;

    public ItemData(int count, String dsn, String dogName) {
        this.count = count;
        this.dogName = dogName;
        this.dsn = dsn;
    }

    public String getDogName() {
        return dogName;
    }
    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getDsn() {
        return dsn;
    }
    public void setDsn(String dsn) {
        this.dsn = dsn;
    }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}

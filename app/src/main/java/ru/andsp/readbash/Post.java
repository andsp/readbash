package ru.andsp.readbash;


import java.io.Serializable;

class Post implements Serializable {

    private int external;

    private String date;

    private String content;


    int getExternal() {
        return external;
    }

    void setExternal(int external) {
        this.external = external;
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
    }

    Post() {
    }

}

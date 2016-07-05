package com.xya.ValueObject;

import java.io.Serializable;

/**
 * Created by ubuntu on 15-2-26.
 * Happy New Year!
 */
public class WordsVO implements Serializable {
    private int id;
    private String key;
    private String kind;
    private String date;
    private String meaning;

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

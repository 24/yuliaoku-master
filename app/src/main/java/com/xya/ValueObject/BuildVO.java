package com.xya.ValueObject;

/**
 * Created by ubuntu on 14-12-30.
 */
public class BuildVO {
    private int id;
    private String wordpart;        //word
    private String meaning;
    private String json;        //存放的实例
    private int frequenty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordpart() {
        return wordpart;
    }

    public void setWordpart(String wordpart) {
        this.wordpart = wordpart;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getFrequenty() {
        return frequenty;
    }

    public void setFrequenty(int frequenty) {
        this.frequenty = frequenty;
    }
}

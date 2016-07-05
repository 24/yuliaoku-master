package com.xya.ValueObject;

/**
 * Created by ubuntu on 14-12-30.
 */
public class AbbreviationVO {
    private int id;
    private String abbreviation;        //这是word
    private String pronuciation;        //发音
    private String meaning_zh;      //中文翻译
    private int frequenty;      //使用频率

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getPronuciation() {
        return pronuciation;
    }

    public void setPronuciation(String pronuciation) {
        this.pronuciation = pronuciation;
    }

    public String getMeaning_zh() {
        return meaning_zh;
    }

    public void setMeaning_zh(String meaning_zh) {
        this.meaning_zh = meaning_zh;
    }

    public int getFrequenty() {
        return frequenty;
    }

    public void setFrequenty(int frequenty) {
        this.frequenty = frequenty;
    }
}

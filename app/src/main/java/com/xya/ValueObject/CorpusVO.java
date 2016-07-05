package com.xya.ValueObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ubuntu on 14-12-25.
 */
public class CorpusVO {

    private String word;
    private String pronunciation;
    private String definition;      //释义
    private String quotation;       //英语句子
    private String translation;     //翻译
    private String author;
    private String source;
    private String title;
    private String date;        //原本打算定义为Date，但是xlsx文档日期格式参差不齐，无奈，最后改吧！记住*^*



    public HashMap<String, String> getList() {

        HashMap<String, String> temp = new HashMap<>();
        temp.put("word", word);
        temp.put("pronunciation", pronunciation);
        temp.put("translation0", translation);
        temp.put("author", author);
        temp.put("source", source);
        temp.put("title", title);
        temp.put("date", date);
        temp.put("definition", definition);
        temp.put("quotation0", quotation);
        return temp;
    }



    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

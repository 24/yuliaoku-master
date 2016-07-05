package com.xya.DatabaseUtil;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.xya.ValueObject.CorpusVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ubuntu on 15-2-12.
 */
public final class CorpusDataManager {

    private int primaryColor;

    public CorpusDataManager(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String simplifyTranslation(String translation, String word, String quotation) {
        //根据引语,按照比例来判断中文的截取
        int position = quotation.indexOf(word);
        int center = position / quotation.length() * translation.length();
        //判断截取的起始位置
        int startPosition = 0;
        int endPosition = translation.length();
        if (center > 50)
            startPosition = center - 50;
        if (translation.length() - center > 50)
            endPosition = center + 50;
        return "..." + translation.substring(startPosition, endPosition) + "...";
    }

    //对quotation表色处理

    public SpannableStringBuilder getQuotation(String quotations) {
        String quotation = quotations.replaceAll("UBUNTU", " ").replaceAll("\r|\n", "");
        int beginPosition = quotation.indexOf("</Font><Font");        //以</Font>开始，考虑到Font前有空格和换行问题
        int endPosition = quotation.lastIndexOf("</Font><Font>") + 13;       //以<Font>结尾

        String temp = quotation.substring(beginPosition, endPosition).replaceAll("\r|\n", "").replaceAll("</Font>", "").replaceAll("<Font>", "");
        quotation = quotation.replaceAll("\r|\n", "").replaceAll("</Font>", "").replaceAll("<Font>", "");
        //Toast.makeText(this,temp,Toast.LENGTH_SHORT).show();
        int begin = quotation.indexOf(temp);
        int end = begin + temp.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(quotation);
        CharacterStyle span = new ForegroundColorSpan(primaryColor);
        builder.setSpan(span, begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    //对quotation进行简化
    public SpannableStringBuilder simplifyQuotation(String quotations, String key) {
        /*改过数据库中的Quotation后的修改，关键字以</Font><Font></Font><Font>分割
        **以此来解决word与原型不一致的问题
        **原来的写法
        String[] temp = quotation.split(key);
        if (temp.length < 2)
            return null;
        Log.d("quotation", quotation);
        Log.d("key", key);
        Log.d("temp[0]", temp[0]);
        Log.d("temp[1]", temp[1]);
        */

        String quotation = quotations.replaceAll("UBUNTU", " ").replaceAll("\r|\n", "");

        int beginPosition = quotation.indexOf("</Font><Font");        //以</Font>开始，考虑到Font前有空格和换行问题
        int endPosition = quotation.lastIndexOf("</Font><Font>") + 13;       //以<Font>结尾

        String subString = quotation.substring(beginPosition, endPosition).replaceAll("\r|\n", "").replaceAll("</Font>", "").replaceAll("<Font>", "");

        //吧quotation分段，减少长度
        String[] temp = new String[2];
        if (beginPosition > 50)
            temp[0] = "..." + quotation.substring(beginPosition - 50, beginPosition);
        else
            temp[0] = quotation.substring(0, beginPosition);
        if (quotation.length() - endPosition > 50)
            temp[1] = quotation.substring(endPosition, endPosition + 50) + "...";
        else
            temp[1] = quotation.substring(endPosition, quotation.length());
        //SpannableStringBuilder神奇的类，TextView局部变色
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(temp[0] + " " + subString + " " + temp[1]);
        CharacterStyle span = new ForegroundColorSpan(primaryColor);
        //此处应该遇到空格才截止
        stringBuilder.setSpan(span, temp[0].length() + 1, temp[0].length() + subString.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    //对数据的整合操作
    public ArrayList<HashMap<String, String>> Integration(List<CorpusVO> corpus) {
        //这里有点小问题 HashMap的key是唯一的,不要覆盖掉以前的key-value;
        //+i解决;
        ArrayList<HashMap<String, String>> vos = new ArrayList<>();
        HashMap<String, String> current = corpus.get(0).getList();
        int count = 1;
        for (int i = 1; i < corpus.size(); i++) {
            if (corpus.get(i - 1).getDefinition().equals(corpus.get(i).getDefinition())) {
                current.put("quotation" + count, corpus.get(i).getQuotation());
                current.put("translation" + count, corpus.get(i).getTranslation());
                count++;
            } else {
                vos.add(current);
                current = corpus.get(i).getList();
            }
        }
        vos.add(current);
        return vos;
    }
}

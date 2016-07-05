package com.xya.DatabaseUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.xya.MainActivity.R;
import com.xya.ValueObject.AbbreviationVO;
import com.xya.ValueObject.BuildVO;
import com.xya.ValueObject.CorpusVO;
import com.xya.ValueObject.NotesVO;
import com.xya.ValueObject.WordsVO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 14-12-25.
 */
public class LocalDateBaseManager {
    private static final String DB_NAME = "medical"; //数据库名字
    private static final String PACKAGE_NAME = "com.xya.csu"; //应用程序包名
    private static final String DB_PATH = "/data/" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME;
    public static String TAG = "LocalDateBaseManager";
    private final int BUFFER_SIZE = 400000;  //缓冲区大小
    private SQLiteDatabase database;
    private Context context;

    public LocalDateBaseManager(Context cxt) {
        context = cxt;
    }

    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    //从apk拷贝数据库，获取DB
    public SQLiteDatabase openDatabase(String path) {


//        把raw下的数据库文件medical复制到sd卡里面
        if (!new File(path).exists()) {
            InputStream inputStream = context.getResources().openRawResource(R.raw.medical);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(path);
                Log.i("原来数据库目录","path"+path);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "cannot find the DB!");
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            try {
                while ((count = inputStream.read(buffer)) > 0) {
                    assert fileOutputStream != null;
                    fileOutputStream.write(buffer, 0, count);
                }
                assert fileOutputStream != null;
                fileOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "IO exception!");
            }

        }
        return SQLiteDatabase.openOrCreateDatabase(path, null);
    }

    public void closeDatabase() {
        this.database.close();
    }


//    private final static String getCorpuseVOByWord = "select distinct (_word) from ? where _word like ?";


    //获取所有的单词，以供下拉列表（自动提示）使用
    /*
    ** 不要说这是我写的
     */
    public List<String> getWordByWord(String key, int kind) {

        if (TextUtils.isEmpty(key)) {
            return null;
        }

        //  Log.d("bijiao",kind + "  "+R.id.action_words);
        List<String> corpusVOs = new ArrayList<>();
        Cursor cursor = null;
        //数据库查询放在这把，虽然不好，但是还能看的过去
        if (kind == R.id.action_words) {
            cursor = this.database.rawQuery("select distinct (_word) from CorpusTable where _word like ?", new String[]{key + "%"});
            while (cursor.moveToNext()) {
                //把o添加到list
                String temp = cursor.getString(cursor.getColumnIndex("_word"));
                corpusVOs.add(temp);
            }
        } else if (kind == R.id.action_abbreviation) {
            cursor = this.database.rawQuery("select distinct (_abbreviation) from AbbrevTable where _abbreviation like ?", new String[]{key + "%"});
            while (cursor.moveToNext()) {
                //把o添加到list
                String temp = cursor.getString(cursor.getColumnIndex("_abbreviation"));
                corpusVOs.add(temp);
            }
        } else if (kind == R.id.action_build) {
            cursor = this.database.rawQuery("select distinct (_wordpart) from BuildTable where _wordpart like ?", new String[]{key + "%"});
            while (cursor.moveToNext()) {
                //把o添加到list
                String temp = cursor.getString(cursor.getColumnIndex("_wordpart"));
                corpusVOs.add(temp);
            }
        } else if (kind == R.id.action_search) {  //模糊查找
            cursor = this.database.rawQuery("select distinct (_word) from CorpusTable where _word like ?", new String[]{key + "%"});
            while (cursor.moveToNext()) {
                //把o添加到list
                String temp = cursor.getString(cursor.getColumnIndex("_word"));
                corpusVOs.add(temp);
            }
            cursor = this.database.rawQuery("select distinct (_abbreviation) from AbbrevTable where _abbreviation like ?", new String[]{key + "%"});
            while (cursor.moveToNext()) {
                //把o添加到list
                String temp = cursor.getString(cursor.getColumnIndex("_abbreviation"));
                corpusVOs.add(temp);
            }
            cursor = this.database.rawQuery("select distinct (_wordpart) from BuildTable where _wordpart like ?", new String[]{key + "%"});
            while (cursor.moveToNext()) {
                //把o添加到list
                String temp = cursor.getString(cursor.getColumnIndex("_wordpart"));
                corpusVOs.add(temp);
            }
        }

        assert cursor != null;
        cursor.close();

        return corpusVOs;
    }

    //获取corpus的详细信息
    public List<CorpusVO> getCorpusDetailByWord(String word) {
        List<CorpusVO> corpus = new ArrayList<CorpusVO>();

        Cursor cursor = this.database.rawQuery("select * from CorpusTable where _word = ? ", new String[]{word});      //定义游标

        //以防万一，判空
        if (TextUtils.isEmpty(word)) {
            return null;
        }


        //对结果集处理
        while (cursor.moveToNext()) {
            CorpusVO vo = new CorpusVO();
            //  vo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            vo.setWord(cursor.getString(cursor.getColumnIndex("_word")));
            vo.setPronunciation(cursor.getString(cursor.getColumnIndex("_pronunciation")));
            vo.setDefinition(cursor.getString(cursor.getColumnIndex("_definition")));
            vo.setQuotation(cursor.getString(cursor.getColumnIndex("_quotation")));
            vo.setTranslation(cursor.getString(cursor.getColumnIndex("_translation")));
            vo.setTitle(cursor.getString(cursor.getColumnIndex("_title")));
            vo.setSource(cursor.getString(cursor.getColumnIndex("_source")));
            vo.setAuthor(cursor.getString(cursor.getColumnIndex("_author")));
            vo.setDate(cursor.getString(cursor.getColumnIndex("_date")));
            // vo.setSame(cursor.getString(cursor.getColumnIndex("_same")));

            corpus.add(vo);

        }
        cursor.close();

        return corpus;
    }

    //获取abbreviation的详细信息
    public List<AbbreviationVO> getAbbreviationDetailByWord(String abbreviation) {
        List<AbbreviationVO> abbreviations = new ArrayList<AbbreviationVO>();

        Cursor cursor = this.database.rawQuery("select * from AbbrevTable where _abbreviation = ? ", new String[]{abbreviation});      //定义游标

        //以防万一，判空
        if (TextUtils.isEmpty(abbreviation)) {
            return null;
        }


        //对结果集处理
        while (cursor.moveToNext()) {
            AbbreviationVO vo = new AbbreviationVO();
            vo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            vo.setAbbreviation(cursor.getString(cursor.getColumnIndex("_abbreviation")));
            vo.setFrequenty(cursor.getInt(cursor.getColumnIndex("_frequenty")));
            vo.setMeaning_zh(cursor.getString(cursor.getColumnIndex("_meaning_zh")));
            vo.setPronuciation(cursor.getString(cursor.getColumnIndex("_pronunciation")));
            abbreviations.add(vo);

        }
        cursor.close();

        return abbreviations;
    }

    //获取build的详细信息
    public List<BuildVO> getBuildDetailByWord(String build) {
        List<BuildVO> builds = new ArrayList<>();

        Cursor cursor = this.database.rawQuery("select * from BuildTable where _wordpart = ? ", new String[]{build});      //定义游标

        //以防万一，判空
        if (TextUtils.isEmpty(build)) {
            return null;
        }


        //对结果集处理
        while (cursor.moveToNext()) {
            BuildVO vo = new BuildVO();
            vo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            vo.setWordpart(cursor.getString(cursor.getColumnIndex("_wordpart")));
            vo.setFrequenty(cursor.getInt(cursor.getColumnIndex("_frequentity")));
            vo.setMeaning(cursor.getString(cursor.getColumnIndex("_meaning")));
            vo.setJson(cursor.getString(cursor.getColumnIndex("_json")));
            builds.add(vo);

        }
        cursor.close();

        return builds;
    }

    /*
    * 返回本地数据的总数
    * */
    public int getCorpusAmount() {
        int count = 0;
        String sql = "select count(_id) from CorpusTable";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext())
            count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getBuildAmount() {
        int count = 0;
        String sql = "select count(_id) from BuildTable";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext())
            count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getAbbreviationAmount() {
        int count = 0;
        String sql = "select count(_id) from AbbrevTable";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext())
            count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getType(String key) {
        List<String> num = new ArrayList<>();
        Cursor cursor1 = this.database.rawQuery("select * from CorpusTable where _word = ? ", new String[]{key});      //定义游标
        while (cursor1.moveToNext()) {
            //把o添加到list
            String temp = cursor1.getString(cursor1.getColumnIndex("_id"));
            num.add(temp);
        }
        if (num.size() < 1) {
            Cursor cursor2 = this.database.rawQuery("select * from AbbrevTable where _abbreviation = ? ", new String[]{key});      //定义游标
            while (cursor2.moveToNext()) {
                //把o添加到list
                String temp = cursor2.getString(cursor2.getColumnIndex("_id"));
                num.add(temp);
            }
            if (num.size() < 1) {
                Cursor cursor3 = this.database.rawQuery("select * from BuildTable where _wordpart = ? ", new String[]{key});      //定义游标
                while (cursor3.moveToNext()) {
                    //把o添加到list
                    String temp = cursor3.getString(cursor3.getColumnIndex("_id"));
                    num.add(temp);
                }
                if (num.size() < 1)
                    return 0;
                else
                    return R.id.action_build;
            } else
                return R.id.action_abbreviation;
        } else
            return R.id.action_words;
    }


    public void insertNotes(NotesVO vo) {
        ContentValues cv = new ContentValues();
        cv.put("_kind", vo.getKind());
        cv.put("_thumb", vo.getThumb());
        cv.put("_key", vo.getKey());
        cv.put("_path", vo.getPath());
        cv.put("_note", vo.getNote());
        cv.put("date", vo.getDate());
        database.insert("NoteTable", null, cv);
        //database.close();
        //this.database.execSQL("insert into NoteTable('_kind','_thumb','_key','_path','_note','date') values (?,?,?,?,?,?)", new String[]{vo.getKind(), vo.getThumb(), vo.getKey(), vo.getPath(), vo.getNote(), vo.getDate()});
    }

    public List<NotesVO> getNotes(String key) {
        List<NotesVO> notes = new ArrayList<>();

        Cursor cursor = this.database.rawQuery("select * from NoteTable where _key = ? ", new String[]{key});      //定义游标
        while (cursor.moveToNext()) {
            NotesVO note = new NotesVO();
            //把o添加到list
            note.setThumb(cursor.getString(cursor.getColumnIndex("_thumb")));
            note.setKey(cursor.getString(cursor.getColumnIndex("_key")));
            note.setKind(cursor.getString(cursor.getColumnIndex("_kind")));
            note.setNote(cursor.getString(cursor.getColumnIndex("_note")));
            note.setDate(cursor.getString(cursor.getColumnIndex("date")));
            note.setPath(cursor.getString(cursor.getColumnIndex("_path")));
            notes.add(note);
        }
        //database.close();
        return notes;
    }

    public List<NotesVO> getNotes() {
        List<NotesVO> notes = new ArrayList<>();

        Cursor cursor = this.database.rawQuery("select * from NoteTable ", null);      //定义游标
        while (cursor.moveToNext()) {
            NotesVO note = new NotesVO();
            //把o添加到list
            note.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            note.setThumb(cursor.getString(cursor.getColumnIndex("_thumb")));
            note.setKey(cursor.getString(cursor.getColumnIndex("_key")));
            note.setKind(cursor.getString(cursor.getColumnIndex("_kind")));
            note.setNote(cursor.getString(cursor.getColumnIndex("_note")));
            note.setDate(cursor.getString(cursor.getColumnIndex("date")));
            note.setPath(cursor.getString(cursor.getColumnIndex("_path")));
            notes.add(note);
        }
        //database.close();
        return notes;
    }

    public void delNotes(int id) {
        database.delete("NoteTable", "_id =?", new String[]{"" + id});
        //database.close();
    }

    public List<WordsVO> getWords() {
        List<WordsVO> vo = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("select * from WordsTable ", null);      //定义游标
        while (cursor.moveToNext()) {
            WordsVO word = new WordsVO();
            //把o添加到list
            word.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            word.setKey(cursor.getString(cursor.getColumnIndex("_key")));
            word.setKind(cursor.getString(cursor.getColumnIndex("_kind")));
            word.setMeaning(cursor.getString(cursor.getColumnIndex("_meaning")));
            word.setDate(cursor.getString(cursor.getColumnIndex("_date")));
            vo.add(word);
        }
        //database.close();
        return vo;
    }

    public List<WordsVO> getWords(String key) {
        List<WordsVO> vo = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("select * from WordsTable where _key = ? ", new String[]{key});      //定义游标
        while (cursor.moveToNext()) {
            WordsVO word = new WordsVO();
            //把o添加到list
            word.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            word.setKey(cursor.getString(cursor.getColumnIndex("_key")));
            word.setKind(cursor.getString(cursor.getColumnIndex("_kind")));
            word.setMeaning(cursor.getString(cursor.getColumnIndex("_meaning")));
            word.setDate(cursor.getString(cursor.getColumnIndex("_date")));
            vo.add(word);
        }
        //database.close();
        return vo;
    }

    public void delWord(int id) {
        database.delete("WordsTable", "_id =?", new String[]{"" + id});
        //database.close();
    }


    public void insertWords(WordsVO vo) {
        ContentValues cv = new ContentValues();
        cv.put("_kind", vo.getKind());
        cv.put("_key", vo.getKey());
        cv.put("_date", vo.getDate());
        cv.put("_meaning", vo.getMeaning());
        database.insert("WordsTable", null, cv);
        //database.close();
        //this.database.execSQL("insert into NoteTable('_kind','_thumb','_key','_path','_note','date') values (?,?,?,?,?,?)", new String[]{vo.getKind(), vo.getThumb(), vo.getKey(), vo.getPath(), vo.getNote(), vo.getDate()});
    }


}

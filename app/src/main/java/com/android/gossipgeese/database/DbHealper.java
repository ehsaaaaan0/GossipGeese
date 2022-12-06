package com.android.gossipgeese.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHealper extends SQLiteOpenHelper {
    public static final String dbname = "All_Users";
    public static final  String tbl = "users";

    public DbHealper(Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String q = "create table "+tbl +" (id text primary key, name text, image text, lastMsg text, time text, recent text, archive text, token text)";
        sqLiteDatabase.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+tbl);
        onCreate(sqLiteDatabase);
    }
    public boolean insert(String id, String name, String image, String lastMsg,String time,String recent,String archive,String token){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("id", id);
        c.put("name",name);
        c.put("image",image);
        c.put("lastMsg",lastMsg);
        c.put("time",time);
        c.put("recent",recent);
        c.put("archive",archive);
        c.put("token",token);
        long r = db.insert(tbl, null,c);
        if (r==-1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor realAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "select * from "+tbl;
        Cursor cursor = db.rawQuery(qry,null);
        return cursor;
    }





    public boolean updateData(String id,String recent, String archive){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("recent",recent);
        c.put("archive",archive);
        Cursor cursor = db.rawQuery("select * from "+tbl+" where id=?", new String[]{id});
        if (cursor.getCount()>0){
            long r = db.update(tbl, c, "id=?", new String[]{id});
            if (r==-1) return false;
            else
                return true;
        }else{
            return false;
        }
    }
}

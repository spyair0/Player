package com.example.mediaplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

public class MusicAction {
   Music[] songlist={};
    public Music[] findmusic(Context context){
        Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,
                null,MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor.getCount()>0){
            songlist=new Music[cursor.getCount()];
            while(cursor.moveToNext()){
                Music music=new Music();
                music.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                music.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                music.setTimesize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                songlist[cursor.getPosition()]=music;
            }
        }else{
            Toast.makeText(context,"错误",Toast.LENGTH_SHORT).show();
        }
        return songlist;


    }

}

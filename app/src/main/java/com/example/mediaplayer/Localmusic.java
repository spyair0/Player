package com.example.mediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;

public class Localmusic extends AppCompatActivity implements View.OnClickListener {
    private SeekBar seekbar;
    TextView textView;
    TextView totaltime;
    TextView currenttime;
    Thread thread;
    private Timer timer = new Timer();
    private int time=0;
    private int index=0;
    private int Imageid;
    ImageView danqu,xunhuan ,suiji,kong;
    ImageButton stop,start,next,previous;
    final MusicAction musicAction=new MusicAction();
    private Music[] songlist={};
    String[] data={};
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private MediaPlayer mediaPlayer=new MediaPlayer();
  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localmusic);
        songlist = musicAction.findmusic(Localmusic.this);
        data = new String[songlist.length];
        textView=findViewById(R.id.textView);
        totaltime=findViewById(R.id.totaltime);
        stop = findViewById(R.id.stop);
        start = findViewById(R.id.start);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekbar = findViewById(R.id.seekbar);
        currenttime=findViewById(R.id.currenttime);
        danqu=findViewById(R.id.danqu);
        kong=findViewById(R.id.kong);
        xunhuan=findViewById(R.id.xunhuan);
        suiji=findViewById(R.id.suiji);
        seekbar.setProgress(0);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        kong.setOnClickListener(this);
        danqu.setOnClickListener(this);
        xunhuan.setOnClickListener(this);
        suiji.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Localmusic.this, android.R.layout.simple_list_item_1, data
        );
        ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);
        for (int i = 0; i < songlist.length; i++) {
            data[i] = songlist[i].getName();
        }
       verifyStoragePermissions(Localmusic.this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                initMediaPlayer(songlist[i]);
                mediaPlayer.start();
                start.setVisibility(View.GONE);
                stop.setVisibility(view.VISIBLE);
                index=i;
                textView.setText(songlist[i].getName());
                totaltime.setText(ShowTime(Integer.valueOf(songlist[i].getTimesize()).intValue()));
                seekbar.setMax(mediaPlayer.getDuration());
                thread=new Thread(new SeekBarThread());
                thread.start();
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b == true){
                    mediaPlayer.seekTo(i);
                    currenttime.setText(ShowTime(i));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                currenttime.setText(ShowTime(mediaPlayer.getDuration()));
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
        private void initMediaPlayer(Music music){
            try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(music.getAddress());
                mediaPlayer.prepare();
                currenttime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public String ShowTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
    public static void verifyStoragePermissions(Activity activity){
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }

    }
    public void onClick(View view) {
     switch(view.getId()) {
         case R.id.kong:
             Imageid=0;
             kong.setVisibility(View.GONE);
             danqu.setVisibility(View.VISIBLE);
             break;
         case R.id.danqu:
             Imageid=1;
             danqu.setVisibility(View.GONE);
             xunhuan.setVisibility(View.VISIBLE);
             break;
         case R.id.xunhuan:
             Imageid=2;
             xunhuan.setVisibility(View.GONE);
             suiji.setVisibility(View.VISIBLE);
             break;
             case R.id.suiji:
                 Imageid=3;
                 suiji.setVisibility(View.GONE);
                 kong.setVisibility(View.VISIBLE);
                 break;
         case R.id.start:
             if(!mediaPlayer.isPlaying()){
                 mediaPlayer.start();
             }
             start.setVisibility(view.GONE);
             stop.setVisibility(view.VISIBLE);
             thread=new Thread(new SeekBarThread());
             thread.start();
             break;
         case  R.id.stop:
             if(mediaPlayer.isPlaying()){
                 mediaPlayer.pause();
             }
             stop.setVisibility(view.GONE);
             start.setVisibility(view.VISIBLE);
             thread=new Thread(new SeekBarThread());
             thread.start();
             break;
         case R.id.previous:
             if(Imageid==1||Imageid==3) {
                 index--;
                 mediaPlayer.reset();
                 try {
                     mediaPlayer.setDataSource(songlist[index].getAddress());
                     mediaPlayer.prepare();
                     mediaPlayer.start();
                     initMediaPlayer(songlist[index]);
                     mediaPlayer.start();
                     start.setVisibility(View.GONE);
                     stop.setVisibility(view.VISIBLE);
                     textView.setText(songlist[index].getName());
                     currenttime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
                     totaltime.setText(ShowTime(Integer.valueOf(songlist[index].getTimesize()).intValue()));
                     seekbar.setMax(mediaPlayer.getDuration());
                     thread = new Thread(new SeekBarThread());
                     thread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }else if(Imageid==0){
                 mediaPlayer.reset();
                 try {
                     mediaPlayer.setDataSource(songlist[index].getAddress());
                     mediaPlayer.prepare();
                     mediaPlayer.start();
                     initMediaPlayer(songlist[index]);
                     mediaPlayer.start();
                     start.setVisibility(View.GONE);
                     stop.setVisibility(view.VISIBLE);
                     textView.setText(songlist[index].getName());
                     currenttime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
                     totaltime.setText(ShowTime(Integer.valueOf(songlist[index].getTimesize()).intValue()));
                     seekbar.setMax(mediaPlayer.getDuration());
                     thread = new Thread(new SeekBarThread());
                     thread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }else if(Imageid==2){
                 mediaPlayer.reset();
                 try {
                     int random=new Random().nextInt(songlist.length);
                     mediaPlayer.setDataSource(songlist[random].getAddress());
                     mediaPlayer.prepare();
                     mediaPlayer.start();
                     initMediaPlayer(songlist[random]);
                     mediaPlayer.start();
                     start.setVisibility(View.GONE);
                     stop.setVisibility(view.VISIBLE);
                     textView.setText(songlist[random].getName());
                     currenttime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
                     totaltime.setText(ShowTime(Integer.valueOf(songlist[random].getTimesize()).intValue()));
                     seekbar.setMax(mediaPlayer.getDuration());
                     thread = new Thread(new SeekBarThread());
                     thread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             break;
         case R.id.next:
             if(Imageid==1||Imageid==3) {
                 index++;
                 mediaPlayer.reset();
                 try {
                     mediaPlayer.setDataSource(songlist[index].getAddress());
                     mediaPlayer.prepare();
                     mediaPlayer.start();
                     initMediaPlayer(songlist[index]);
                     mediaPlayer.start();
                     start.setVisibility(View.GONE);
                     stop.setVisibility(view.VISIBLE);
                     textView.setText(songlist[index].getName());
                     currenttime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
                     totaltime.setText(ShowTime(Integer.valueOf(songlist[index].getTimesize()).intValue()));
                     seekbar.setMax(mediaPlayer.getDuration());
                     thread = new Thread(new SeekBarThread());
                     thread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }else if(Imageid==0){
                 mediaPlayer.reset();
                 try {
                     mediaPlayer.setDataSource(songlist[index].getAddress());
                     mediaPlayer.prepare();
                     mediaPlayer.start();
                     initMediaPlayer(songlist[index]);
                     mediaPlayer.start();
                     start.setVisibility(View.GONE);
                     stop.setVisibility(view.VISIBLE);
                     textView.setText(songlist[index].getName());
                     currenttime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
                     totaltime.setText(ShowTime(Integer.valueOf(songlist[index].getTimesize()).intValue()));
                     seekbar.setMax(mediaPlayer.getDuration());
                     thread = new Thread(new SeekBarThread());
                     thread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }else if(Imageid==2){
                 mediaPlayer.reset();
                 try {
                     int random=new Random().nextInt(songlist.length);
                     mediaPlayer.setDataSource(songlist[random].getAddress());
                     mediaPlayer.prepare();
                     mediaPlayer.start();
                     initMediaPlayer(songlist[random]);
                     mediaPlayer.start();
                     start.setVisibility(View.GONE);
                     stop.setVisibility(view.VISIBLE);
                     textView.setText(songlist[random].getName());
                     currenttime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
                     totaltime.setText(ShowTime(Integer.valueOf(songlist[random].getTimesize()).intValue()));
                     seekbar.setMax(mediaPlayer.getDuration());
                     thread = new Thread(new SeekBarThread());
                     thread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             break;
             default:
                 break;
     }
    }
    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer.isPlaying()) {
                // 将SeekBar位置设置到当前播放位置
                seekbar.setProgress(mediaPlayer.getCurrentPosition());
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                    //播放进度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}




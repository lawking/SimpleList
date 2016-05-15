package com.example.lawrence.simplelist;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by lawrence on 16/5/14.
 */
public class PlayerService extends Service {

    private MediaPlayer mediaPlayer =  new MediaPlayer();
    private static String path;
    private Mp3Info mp3Info;

    private List<Mp3Info> mp3Infos;
    private int position;
    private int Count;

    private int msg;
    private int way=0;
    private int pause=0;

    private ControlBinder mBinder = new ControlBinder();
    class ControlBinder extends Binder {
        public void play() {
            if (!mediaPlayer.isPlaying()){
                if (pause==0) {
                    initMediaPlayer();
                }
                mediaPlayer.start();
            }
        }

        public void pause() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pause=1;
            }
        }

        public void stop(){
            if ((mediaPlayer != null)&&(mediaPlayer.isPlaying())){
                mediaPlayer.reset();
                initMediaPlayer();
            }
        }

        public int getWay() {
            return way;
        }

        public void setOneRepeat() {
            way=0;
            Log.d("way","OneRepeat");
        }

        public void setListRepeat() {
            way=1;
            Log.d("way","ListRepeat");
        }

        public void setListNoRepeat(){
            way=2;
            Log.d("way","ListNoRepeat");
        }

        public void setShuffe(){
            way=3;
            Log.d("way","Shuffe");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer.isPlaying())
            stop();

        path=intent.getStringExtra("url");
        msg=intent.getIntExtra("MSG",0);


        switch (msg){
            case 0:
                play();
                break;
            case 1:
                pause();
                break;
            case 2:
                stop();
                break;
            default:
                break;
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("PlayerService","finish one");

                switch (way){

                    case 0:
                        try
                        {
                            Log.d("way","One_Repeat to do");
                            mediaPlayer.start();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    case 1:
                        try
                        {
                            Log.d("way","List_Repeat to do");
                            path=nextUrl();
                            play();

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        try
                        {
                            Log.d("way","List_NoRepeat to do");
                            path=nextUrl_NoRepeat();
                            if (path=="stop")
                            {
                                break;
                            }
                            play();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    case 3:
                        try
                        {
                            Log.d("way","Shuff to do");
                            path=shuffUrl();
                            play();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaPlayer(){
        try {
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();  //进行缓冲
            pause=0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play() {
            if (pause==0) {
                initMediaPlayer();
            }
            mediaPlayer.start();
            pause=0;

    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pause=1;
        }
    }
    private void stop(){
        if ((mediaPlayer != null)&&(mediaPlayer.isPlaying())){
            mediaPlayer.reset();
            initMediaPlayer();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private String nextUrl(){

        Log.d("testpath",path);

        MainActivity.position=MainActivity.position+1;

        Log.d("testposition",MainActivity.position+"");


        Log.d("testCount",""+MainActivity.Count);


        //if (this.position<Count-1)
        if(MainActivity.position>MainActivity.Count-1) {
            MainActivity.position=0;
        }

        Log.d("test","hh");
        Mp3Info mp3Info = MainActivity.mp3Infos.get(MainActivity.position);



        Log.d("NextUrl",mp3Info.getUrl().toString());

        String a1 = getResources().getString(R.string.title);
        String b1 = String.format(a1,mp3Info.getTitle().toString());

        String a2 = getResources().getString(R.string.artist);
        String b2 = String.format(a2,mp3Info.getArtist().toString());

        return mp3Info.getUrl().toString();
    }

    public String nextUrl_NoRepeat(){
        MainActivity.position=MainActivity.position+1;

        //if (this.position<Count-1)
        if(MainActivity.position>MainActivity.Count-1) {
            return "stop";
        }

        Mp3Info mp3Info = MainActivity.mp3Infos.get(MainActivity.position);

        Log.d("nextNoUrl",mp3Info.getUrl().toString());

        String a1 = getResources().getString(R.string.title);
        String b1 = String.format(a1,mp3Info.getTitle().toString());

        String a2 = getResources().getString(R.string.artist);
        String b2 = String.format(a2,mp3Info.getArtist().toString());

        return mp3Info.getUrl().toString();

    }

    public String shuffUrl(){
        double shuffNumber=Math.random()*(MainActivity.Count-1);
        MainActivity.position=(int) Math.floor(shuffNumber);
        Log.d("Shuffposition",MainActivity.position+"");

        Mp3Info mp3Info = MainActivity.mp3Infos.get(MainActivity.position);

        Log.d("ShuffUrl",mp3Info.getUrl().toString());

        String a1 = getResources().getString(R.string.title);
        String b1 = String.format(a1,mp3Info.getTitle().toString());

        String a2 = getResources().getString(R.string.artist);
        String b2 = String.format(a2,mp3Info.getArtist().toString());


        return mp3Info.getUrl().toString();
    }

}

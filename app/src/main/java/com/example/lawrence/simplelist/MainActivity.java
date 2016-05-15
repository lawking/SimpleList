package com.example.lawrence.simplelist;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,View.OnClickListener {

    public  static List<Mp3Info> mp3Infos;
    private Intent intent;

    public static int position;

    public  static int Count;

    private ListView mMusiclist;

    private Button previous_music;
    private Button play_music;
    private Button pause_music;
    private Button next_music;

    private Button play_queue;

    private Button repeatone_music;
    private Button repeatlist_music;
    private Button norepeatlist_music;
    private Button shuffle_music;

    private TextView bottom_singer;
    private TextView bottom_song;

    private int isConnectService=0;
    public static final int SHOW_RESPONSE = 0;

    private int start=0;
    private Mp3Info mp3Info;

    private PlayerService.ControlBinder controlMedia;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            controlMedia = (PlayerService.ControlBinder) service;

        } };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj; // 在这里进行UI操作,将结果显示到界面上 responseText.setText(response);
            } }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMusiclist=(ListView)findViewById(R.id.music_list);
        previous_music=(Button)findViewById(R.id.previous_music);

        repeatone_music=(Button)findViewById(R.id.repeatone_music);
        repeatlist_music=(Button)findViewById(R.id.repeatlist_music);
        norepeatlist_music=(Button)findViewById(R.id.norepeatlist_music);
        shuffle_music=(Button)findViewById(R.id.shuffle_music);


        play_music=(Button)findViewById(R.id.play_music);
        pause_music=(Button)findViewById(R.id.pause_music);

        next_music=(Button)findViewById(R.id.next_music);
        play_queue=(Button)findViewById(R.id.play_queue);
        bottom_singer=(TextView)findViewById(R.id.bottom_singer);
        bottom_song=(TextView)findViewById(R.id.bottom_song);

        mp3Infos=getMp3Infos();
        setListAdpter(mp3Infos);

        mMusiclist.setOnItemClickListener(this);

        previous_music.setOnClickListener(this);
        play_music.setOnClickListener(this);
        pause_music.setOnClickListener(this);
        next_music.setOnClickListener(this);
        play_queue.setOnClickListener(this);

        repeatone_music.setOnClickListener(this);
        repeatlist_music.setOnClickListener(this);
        norepeatlist_music.setOnClickListener(this);
        shuffle_music.setOnClickListener(this);



        mp3Info=mp3Infos.get(0);
        intent=setIntent(mp3Info);
        bindService(intent,connection,BIND_AUTO_CREATE);
        isConnectService=1;



        bottom_song.setText(mp3Info.getTitle().toString());
        bottom_singer.setText(mp3Info.getArtist().toString());
    }

    public void setListAdpter(List<Mp3Info> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", mp3Info.getTitle());
            map.put("Artist", mp3Info.getArtist());
            map.put("duration", String.valueOf(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        SimpleAdapter mAdapter = new SimpleAdapter(this, mp3list,
                R.layout.mylist, new String[] { "title", "Artist", "duration" },
                new int[] { R.id.music_title, R.id.music_Artist, R.id.music_duration });
        mMusiclist.setAdapter(mAdapter);
    }

    public List<Mp3Info> getMp3Infos() {
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Count=cursor.getCount();
        if (Count!=0)
            Log.d("MainActivity",""+cursor.getCount());

        mp3Infos = new ArrayList<Mp3Info>();


        for (int i = 0; i < cursor.getCount(); i++) {
            Mp3Info mp3Info = new Mp3Info();
            cursor.moveToNext();
            Log.d("MainActivity","nofuck1");
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id

            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            Log.d("MainActivity",""+title);
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));              //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            if (isMusic != 0) {//只把音乐添加到集合当中
                Log.d("MainActivity",title);
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                MainActivity.mp3Infos.add(mp3Info);
            }
        }
        return MainActivity.mp3Infos;
    }

    public Intent setIntent(Mp3Info mp3Info){
        intent=new Intent(MainActivity.this,PlayerService.class);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("title",mp3Info.getTitle());
        intent.putExtra("artist",mp3Info.getArtist());
        intent.putExtra("MSG", 0);
        return intent;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mp3Infos != null) {
            Log.d("Mainposition",""+position);
            MainActivity.position=position;
            mp3Info = MainActivity.mp3Infos.get(MainActivity.position);
            Toast.makeText(this,mp3Info.getTitle().toString(),Toast.LENGTH_SHORT).show();

            bottom_song.setText(mp3Info.getTitle().toString());
            bottom_singer.setText(mp3Info.getArtist().toString());

            intent=setIntent(mp3Info);
            bindService(intent,connection,BIND_AUTO_CREATE);

            startService(intent);
            start=1;
            //stopService(intent);
            isConnectService=1;

            play_music.setVisibility(View.GONE);
            pause_music.setVisibility(View.VISIBLE);




        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.previous_music:
                if (isConnectService==1){
                    MainActivity.position=MainActivity.position-1;
                    if (MainActivity.position<0){
                        MainActivity.position=MainActivity.Count-1;
                    }



                    mp3Info = MainActivity.mp3Infos.get(MainActivity.position);
                    Toast.makeText(this,mp3Info.getTitle().toString(),Toast.LENGTH_SHORT).show();

                    bottom_song.setText(mp3Info.getTitle().toString());
                    bottom_singer.setText(mp3Info.getArtist().toString());

                    intent=setIntent(mp3Info);
                    bindService(intent,connection,BIND_AUTO_CREATE);
                    isConnectService=1;

                    startService(intent);
                    start=1;

                }else {
                    Toast.makeText(this,"请先选择一首歌曲",Toast.LENGTH_SHORT).show();
                }
                break;



            case R.id.play_music:
                if (isConnectService==1){
                    if (start==0){
                        startService(intent);
                        start=1;
                    }
                    controlMedia.play();
                    play_music.setVisibility(View.GONE);
                    pause_music.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(this,"请先选择一首歌曲",Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.pause_music:
                if (isConnectService==1){
                    controlMedia.pause();
                    pause_music.setVisibility(View.GONE);
                    play_music.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(this,"请先选择一首歌曲",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.repeatone_music:
                if (isConnectService==1){
                    Toast.makeText(this,"已更改为列表循环",Toast.LENGTH_SHORT).show();
                    repeatone_music.setVisibility(View.GONE);
                    repeatlist_music.setVisibility(View.VISIBLE);
                    controlMedia.setListRepeat();
                }
                break;

            case R.id.repeatlist_music:
                if (isConnectService==1){
                    Toast.makeText(this,"已更改为顺序播放",Toast.LENGTH_SHORT).show();
                    repeatlist_music.setVisibility(View.GONE);
                    norepeatlist_music.setVisibility(View.VISIBLE);
                    controlMedia.setListNoRepeat();
                }
                break;

            case R.id.norepeatlist_music:
                if (isConnectService==1){
                    Toast.makeText(this,"已更改为随机播放",Toast.LENGTH_SHORT).show();
                    norepeatlist_music.setVisibility(View.GONE);
                    shuffle_music.setVisibility(View.VISIBLE);
                    controlMedia.setShuffe();
                }
                break;

            case R.id.shuffle_music:
                if (isConnectService==1){
                    Toast.makeText(this,"已更改为单曲循环",Toast.LENGTH_SHORT).show();
                    shuffle_music.setVisibility(View.GONE);
                    repeatone_music.setVisibility(View.VISIBLE);
                    controlMedia.setOneRepeat();
                }
                break;


            case R.id.next_music:
                if (isConnectService==1){

                    MainActivity.position=MainActivity.position+1;

                    if(MainActivity.position>MainActivity.Count-1) {
                        MainActivity.position=0;
                    }


                    mp3Info = MainActivity.mp3Infos.get(MainActivity.position);
                    Toast.makeText(this,mp3Info.getTitle().toString(),Toast.LENGTH_SHORT).show();

                    bottom_song.setText(mp3Info.getTitle().toString());
                    bottom_singer.setText(mp3Info.getArtist().toString());

                    intent=setIntent(mp3Info);
                    bindService(intent,connection,BIND_AUTO_CREATE);
                    isConnectService=1;

                    startService(intent);
                    start=1;



                }else {
                    Toast.makeText(this,"请先选择一首歌曲",Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.play_queue:
                break;


            default:
                break;
        }
    }

    public Mp3Info getMp3Info() {
        return mp3Info;
    }

    public String nextUrl(){
            MainActivity.position=MainActivity.position+1;

            //if (this.position<Count-1)
            if(MainActivity.position>Count-1) {
                MainActivity.position=0;
            }

            mp3Info = MainActivity.mp3Infos.get(MainActivity.position);

            bottom_song.setText(mp3Info.getTitle().toString());
            bottom_singer.setText(mp3Info.getArtist().toString());

        return mp3Info.getUrl().toString();

    }

    public String nextUrl_NoRepeat(){
        MainActivity.position=MainActivity.position+1;

        //if (this.position<Count-1)
        if(MainActivity.position>MainActivity.Count-1) {
            return "stop";
        }

        mp3Info = MainActivity.mp3Infos.get(MainActivity.position);
        Toast.makeText(this,mp3Info.getTitle().toString(),Toast.LENGTH_SHORT).show();

        bottom_song.setText(mp3Info.getTitle().toString());
        bottom_singer.setText(mp3Info.getArtist().toString());

        return mp3Info.getUrl().toString();

    }
}

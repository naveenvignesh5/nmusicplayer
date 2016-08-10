package com.theace56.naveenvignesh.nplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class audiolist extends AppCompatActivity {

    private scan obj;
    private MediaPlayer mp;
    private ListView l;
    HashMap<String,String> mus;
    ArrayList<String> m,p;
    private Button b1,b2,b3;
    private String path;
    private SoundPool sp;
    private int id;
    private visual v;
    private VisualizerView vis;
    private Equalizer eq;
    private Bundle b;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new LoadViewTask().execute();

        setContentView(R.layout.activity_audiolist);

        Intent i = getIntent();
        if(i.getStringExtra("msg") == "true") {
            b = i.getBundleExtra("set");
        }

        createList();  //method to implement list and its media player
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                sp.play(id, 1, 1, 0, 0, 1.5f);
            }
        });


        if(b != null) {
            eq = new Equalizer(1,mp.getAudioSessionId());
            short bandno = b.getShort("bandno");
            short[] bandlevels = b.getShortArray("bandlevels");
            short present = b.getShort("present");
            Equalizer.Settings s = new Equalizer.Settings();
            s.numBands = bandno;
            s.curPreset = present;
            s.bandLevels = bandlevels;
            eq.setProperties(s);
            initmedia(b.getString("mpath"));
            mp.seekTo(b.getInt("mpos"));
            }
        else
        {
            String temp = l.getItemAtPosition(0).toString();
            path = mus.get(temp);
            initmedia(path);
        }
    }

    public void onDestroy()
    {
        mp.release();
        mp = null;
        super.onDestroy();
    }

    //method to create list view from recorded files
    public void createList() {
        l = (ListView) findViewById(R.id.audiolist);

        obj = new scan();

        mus = obj.getPlayList();

        mp = new MediaPlayer();

        m = new ArrayList<>();
        p = new ArrayList<>();

        m.addAll(mus.keySet());
        p.addAll(mus.values());

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, m);
        l.setAdapter(adapter);

        //code to setup visualizer view
        vis = (VisualizerView)findViewById(R.id.al_eq);

        v = new visual();

        //command to invoke alert box to add effect
        initlist(); //method to set commands to list view
        btfun();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });
    }

    private void initlist()
    {
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String)parent.getItemAtPosition(position);
                Toast.makeText(getBaseContext(),name,Toast.LENGTH_SHORT).show();
                path = mus.get(name);
                initmedia(path);
            }
        });
    }

    public void chipit(View v)
    {
        id = sp.load(path,1);

    }

    //code to select audio track and load it into the media player
    private void initmedia(String spath)
    {
        if(v != null) v = null;

        if(mp != null)
        {
            mp.stop();
            mp.release();
            mp = null;
        }

        try
        {
            mp = new MediaPlayer();
            mp.setDataSource(spath);
            mp.prepare();
            v = null;
            v = new visual(mp,vis);
            mp.start();
        }
        catch(IOException|IllegalStateException ex)
        {
            //stop current playing and free the media player
            mp.stop();
            mp.reset();
            mp.release();
            initmedia(spath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_audiolist,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.aitem1:
                Intent i = new Intent(getBaseContext(),equaliser.class);
                Bundle b = new Bundle();
                b.putString("fpath",path);
                b.putInt("pos",mp.getCurrentPosition());
                i.putExtra("path",b);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //method to get alert box on selecting song
    /*public void alertbox(String path)
    {
        alert = null;
        ad = null;

        ad = new AlertDialog.Builder(audiolist.this);
        //using layout inflater to conver layout to view
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.prompt3,null);  //code to set layout for prompt

        ad.setView(v);

        //function to add functionality to alert box

        alert = ad.create();
        alert.show();
    }*/

    //functionalities for buttons
    public void btfun()
    {
        b1 = (Button)findViewById(R.id.ln_bt2);  //play
        b2 = (Button)findViewById(R.id.ln_bt3);  //pause
        b3 = (Button)findViewById(R.id.ln_bt4);  //stop

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mp.isPlaying()) mp.start();
                else Toast.makeText(getBaseContext(),"playing",Toast.LENGTH_SHORT).show();
            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp.isPlaying()) mp.pause();
                else Toast.makeText(getBaseContext(),"Paused",Toast.LENGTH_SHORT).show();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.reset();
            }
        });

    }



    public void onBackPressed()
    {
        Intent i = new Intent(this,record.class);
        startActivity(i);
        this.finish();
    }

    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(audiolist.this, "Loading...",
                    "Loading application View, please wait...", false, false);
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            try
            {
                //Get the current thread's token
                synchronized (this)
                {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;
                    //While the counter is smaller than four
                    while(counter <= 4)
                    {
                        //Wait n milliseconds where n is no inside wait(n)...
                        this.wait(3000);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter*25);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            //close the progress dialog
            progressDialog.dismiss();
            //initialize the View
            setContentView(R.layout.activity_audiolist);
            createList();
        }
    }
}

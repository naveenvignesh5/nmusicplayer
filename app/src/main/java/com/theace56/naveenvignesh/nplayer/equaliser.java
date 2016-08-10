package com.theace56.naveenvignesh.nplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class equaliser extends AppCompatActivity {

    private Button play,pause;
    private MediaPlayer m;
    private Equalizer eq;
    private SeekBar s1,s2,s3,s4,s5,s;
    private Spinner sp;
    private VisualizerView v;
    private visual obj;
    private String out;
    private TextView t1,t2,t3,t4,t5;
    private Handler h1 = new Handler();
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_equaliser);
        //getting data from previous activity
         Intent i = getIntent();
         Bundle b = i.getBundleExtra("path");
         out = b.getString("fpath");
         pos = b.getInt("pos");
         setVolumeControlStream(AudioManager.STREAM_MUSIC);

         v = (VisualizerView)findViewById(R.id.visview);
        //create media player
        m = new MediaPlayer();

        try {
            m.setDataSource(out);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(),"Unable to get data",Toast.LENGTH_SHORT).show();
        }

        try {
            m.prepare();
            m.seekTo(pos);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Player not ready", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException ex) {
            Toast.makeText(getBaseContext(), "Recording going on!!!", Toast.LENGTH_SHORT).show();
        }


        //create equalizer with default priority of 0 & attach media player
        eq = new Equalizer(0,m.getAudioSessionId());

        //set up visualizer and equalizer bars
        obj = new visual(m,v);


        setupgui();
        btfun();
        s.setMax(m.getDuration());

        //getting play button

        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                m.reset();
                s.setProgress(0);
            }
        });

    }

    //menu bar methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_eq,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.eitem1:
                Intent i = new Intent(getBaseContext(),audiolist.class);
                Bundle b = new Bundle();
                Equalizer.Settings s = getsettings();
                b.putShort("present",s.curPreset);
                b.putShort("bandno",s.numBands);
                b.putShortArray("bandlevels",s.bandLevels);
                b.putString("mpath",out);
                b.putInt("mpos",m.getCurrentPosition());
                i.putExtra("set", b);
                i.putExtra("msg","true");
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setsource(String path)
    {
        m.release();
        m = null;
        obj = null;
        m = new MediaPlayer();

        try {
            m.setDataSource(path);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(),"Unable to get data",Toast.LENGTH_SHORT).show();
        }

        try {
            m.prepare();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Player not ready", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException ex) {
            Toast.makeText(getBaseContext(), "Recording going on!!!", Toast.LENGTH_SHORT).show();
        }
        obj = new visual(m,v);
    }



    private void btfun()
    {
        play = (Button)findViewById(R.id.eq_play);
        pause = (Button)findViewById(R.id.eq_pause);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setsource(out);
                if(m.isPlaying()) Toast.makeText(getBaseContext(),"Playing",Toast.LENGTH_SHORT).show();
                else m.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m.isPlaying()) Toast.makeText(getBaseContext(),"Paused",Toast.LENGTH_SHORT).show();
                else m.pause();
            }
        });
    }



    private void initspinner()
    {
        sp = (Spinner)findViewById(R.id.eq_sp);
        List<String> l = new ArrayList<>();
        for(short i =0;i<eq.getNumberOfPresets();i++)
        {
            l.add(eq.getPresetName(i));
        }
        ArrayAdapter<String> list = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,l);
        sp.setAdapter(list);
    }

    private void setupgui()
    {
        //initialising all gui components for the equaliser activity
        initheader();
        initspinner();
        initseek();
        initpredefseek(); //code to implement track bar
        updatepos(); //code to run seekbar when music is player
    }

    //method to initiate all seekars in the layout
    private void initseek()
    {
        //creating object for all elements done here
        s = (SeekBar)findViewById(R.id.eq_seekm);
        s1 = (SeekBar)findViewById(R.id.seek1);
        s2 = (SeekBar)findViewById(R.id.seek2);
        s3 = (SeekBar)findViewById(R.id.seek3);
        s4 = (SeekBar)findViewById(R.id.seek4);
        s5 = (SeekBar)findViewById(R.id.seek5);
        //s5 = (SeekBar)findViewById(R.id.seek5);

        //config for seekbar
        s.setProgress(pos); //setting position when created from other music activity
        setseekconfig(s1, (short) 0);
        setseekconfig(s2, (short) 1);
        setseekconfig(s3, (short) 2);
        setseekconfig(s4, (short) 3);
        setseekconfig(s5, (short) 4);

    }

    //method to initialise fonts for layout
   /* public void font()
    {
        tit = (TextView)findViewById(R.id.eq_tit);

    }
*/
    //method to initiate all middle headers in the layout
    private void initheader()
    {
       //creating header textview
        t1 = (TextView)findViewById(R.id.t1);
        t2 = (TextView)findViewById(R.id.t2);
        t3 = (TextView)findViewById(R.id.t3);
        t4 = (TextView)findViewById(R.id.t4);
        t5 = (TextView)findViewById(R.id.t5);

        //config the headers
        setheadonfig(t1, (short) 0);
        setheadonfig(t2, (short) 1);
        setheadonfig(t3, (short) 2);
        setheadonfig(t4, (short) 3);
        setheadonfig(t5, (short) 4);
    }


    public void onDestroy()
    {
        m.release();
        m = null;
        super.onDestroy();
    }

    //method to get middle frequency values for textviews of equaliser activity
    private void setheadonfig(TextView t,short bandindex)
    {
        t.setText(eq.getCenterFreq(bandindex) / 1000 + "Hz");
    }

    //method to add basic parameteric configuration for seekbar
    private void setseekconfig(SeekBar s, final short bandindex)
    {
        final short low = eq.getBandLevelRange()[0];
        final short high = eq.getBandLevelRange()[1];

        s.setMax(high - low);
        s.setProgress(eq.getBandLevel(bandindex));

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                eq.setBandLevel(bandindex, (short) (progress + low));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }
        });
    }


    Runnable r = new Runnable() {
        @Override
        public void run() {
            updatepos();
        }
    };

    private void updatepos()
    {
        try {
            s.setProgress(m.getCurrentPosition());
            h1.postDelayed(r,1000);
        } catch (Exception e) {
            //do nothing
        }
    }

    //method to add predefined settings to a seekbar
    private void seekcustom(SeekBar s,int pos,int bandindex)
    {
        eq.usePreset((short) pos);
        short bands = eq.getNumberOfBands();
        final short low = eq.getBandLevelRange()[0];
        s.setProgress(eq.getBandLevel((short) bandindex)-low);
    }

    private void initpredefseek()
    {
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //initialising settings for all the 5 seek bars
                seekcustom(s1, position, 0);
                seekcustom(s2, position, 1);
                seekcustom(s3, position, 2);
                seekcustom(s4, position, 3);
                seekcustom(s5, position, 4);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public Equalizer.Settings getsettings()
    {
        Equalizer.Settings s = new Equalizer.Settings();
        short[] bands = new short[5];
        for(int i=0;i<5;i++)
        {
            bands[i] = eq.getBandLevel((short)i);
        }
        s.bandLevels = bands;
        s.curPreset = eq.getCurrentPreset();
        s.numBands = eq.getNumberOfBands();
        return s;
    }


    public void onBackPressed()
    {
        Intent i = new Intent(this,record.class);
        startActivity(i);
        this.finish();
    }
}

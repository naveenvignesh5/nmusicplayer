package com.theace56.naveenvignesh.nplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Naveen Vignesh on 02-07-2016.
 */
public class record extends Activity {

    private MediaRecorder recorder;
    private MediaPlayer m;
    private String outfile = null;
    private ImageButton record, stop;
    private int id;
    private SeekBar s;
    private AlertDialog.Builder ad;
    private AlertDialog alert;
    private EditText tf;
    private Handler h = new Handler();  //creating an handler to implement runnable
    private SoundPool sp;
    public void onCreate(Bundle savedInstanceState) {
        //android code for activity to get sound sample from user
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder);
        createDir();

        //initializing storage location
        outfile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chipmunked/sample.mp3";  //problem here outfile not created

        //initialising buttons for the activity
        record = (ImageButton) findViewById(R.id.rec_record);
        stop = (ImageButton) findViewById(R.id.rec_stop);


        //initialising seekbar

        //initializing the audio manager for recording the voice
        recorder = new MediaRecorder();
        m = new MediaPlayer();
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);


        //setting up storage location and format for audio
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(outfile);

        //initial button setup
        stop.setEnabled(false);
//        play.setEnabled(true);

        //code to set data source to sample.mp3 in chipmunk folder
        try {
            m.setDataSource(outfile);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "I/O Device not ready", Toast.LENGTH_SHORT).show();
        }

        try {
            m.prepare();
        } catch (IllegalStateException|IOException e) {
            Toast.makeText(getBaseContext(), "Player not ready", Toast.LENGTH_SHORT).show();
        }

        initseek(); //initialising the seekbar component

        //method to reset audio when track is ended
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                m.reset();
                s.setProgress(0);
            }
        });


        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                sp.play(id,1,1,0,0,1.5f);
            }
        });


        setUpdate(); //method to update seekbar position on playing the audio track
    }

    /*public void chipmunked(View v)
    {
        int id;
        m.pause();
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        id = sp.load(outfile,0);
        if (id != 0) {
            sp.play(id,1,1,0,0,1.5f);
        }
        else Toast.makeText(getBaseContext(),"Error in playing",Toast.LENGTH_SHORT).show();
    }*/

    public void initseek() {
        s = (SeekBar)findViewById(R.id.rec_seek);
        s.setMax(m.getDuration());
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //do nothing
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                m.seekTo(seekBar.getProgress());
            }
        });
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            setUpdate();
        }
    };

    public void setUpdate()
    {
        try {
            s.setProgress(m.getCurrentPosition());
            h.postDelayed(r,1000);
        } catch (Exception e) {
            //do nothing
        }
    }


    //code to play the audio
    public void play(View view) {
        setsource(outfile);
        if(!m.isPlaying()) m.start();
        else Toast.makeText(getBaseContext(),"Playing",Toast.LENGTH_SHORT).show();
    }

    public void pause(View v)
    {
        if(m.isPlaying()) m.pause();
        else Toast.makeText(getBaseContext(),"Paused",Toast.LENGTH_SHORT).show();
    }

    //code to start service at phone sleep mode


    //method to stop background service at the end of phone wake up

    //method to realease runnable at end of activity
    @Override
    public void onDestroy()
    {
        m.stop();
        m.release();
        super.onDestroy();
    }

    //function to stop recording and store the file
    public void stop(View v) {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
        } catch (IllegalStateException e) {
            Toast.makeText(getBaseContext(), "Audio Playing..", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getBaseContext(), "Recording finished", Toast.LENGTH_SHORT).show();
        stop.setEnabled(false);
        alert1(); //calls the save alert box to save the file
    }

    //code to start recording
    public void recordsound(View v) {
        try {
            m.stop();
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "I/O Device not ready", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException ex) {
            //don't know what this does???
            Toast.makeText(getBaseContext(), "Playing Audio", Toast.LENGTH_SHORT).show();
        }
        record.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(getBaseContext(), "Recording Started", Toast.LENGTH_SHORT).show();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu and adds items if present
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    //defining functions for the option in menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2: //item1 = save
                alert1();
                return true;
            case R.id.item3: //show records
                Intent bi = new Intent(getBaseContext(),audiolist.class);
                startActivity(bi);
                //adding thread to stall for some time till the files load

                finish();
                return true;
            case R.id.item4: //add effect
                Intent ai = new Intent(getBaseContext(),equaliser.class);
                Bundle b = new Bundle();
                b.putString("fpath",outfile);
                b.putInt("pos",m.getCurrentPosition());
                ai.putExtra("path",b);
                startActivity(ai);
                finish();
                return true;
            case R.id.item5:
                Intent i = new Intent(this,credits.class);
                startActivity(i);
                i.putExtra("msg","false");
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //alert dialog for saving the audio file
    private void alert1()
    {
        alert = null;
        ad = null;

        ad = new AlertDialog.Builder(record.this);
        //using layout inflater to conver layout to view
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.prompt,null);

        tf = (EditText)v.findViewById(R.id.prompt_tf);

        ad.setView(v);
        ad.setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //code to save the changed audio file is below
                        String filename = tf.getText().toString();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chiprecords/"+filename+".mp3";
                        File temp = new File(path);
                        try {
                            FileUtils.copyFile(new File(outfile),temp);
                            Toast.makeText(getBaseContext(),"Saved",Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getBaseContext(), "Unable to save the file", Toast.LENGTH_SHORT).show();
                        }
                        catch (IOException e)
                        {
                            Toast.makeText(getBaseContext(),"Unable to copy",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); //method to close the dialog box
            }
        });
        alert = ad.create();
        alert.show();
    }

    private void setsource(String path)
    {
        m.release();
        m = null;
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
    }
    /*private void alert3()
    {
        alert = null;
        ad = null;

        ad = new AlertDialog.Builder(record.this);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.loadprompt,null);

        ad.setView(v);
        alert = ad.create();
        alert.show();
    }*/

    //alert dialog for entering into effects activities -- problems seems to be here. Don't know what to do??
    /*private void alert2() {
        alert = null;
        ad = null;

        ad = new AlertDialog.Builder(record.this);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.prompt2,null);

        ad.setView(v);
        ad.setCancelable(false)
                .setPositiveButton("Add Effect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       gr = (RadioGroup)v.findViewById(R.id.radgrp);
                        int id = gr.getCheckedRadioButtonId();
                        switch (id)
                        {
                            case R.id.eq:
                                Intent i = new Intent(getBaseContext(),equaliser.class);
                                Bundle b = new Bundle();
                                b.putString("fpath",outfile);
                                i.putExtra("path", b);
                                startActivity(i);
                                finish();
                                break;
                            case R.id.pitch:
                                Toast.makeText(getBaseContext(),"chipmunk",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.tempo:
                                Toast.makeText(getBaseContext(),"tempo",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alert = ad.create();
        alert.show();
    }
*/

    public void chip(View v)
    {
        id = sp.load(outfile,1);
    }



    private void createDir() {
        //creating folder at the time of install
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("MyApp", "No SDCARD");
        } else {
            File directory;
            directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Chipmunked"); //problem here folder not created
            directory.mkdirs();
            directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Chiprecords");
            directory.mkdirs();
        }
        //end of directory code
    }

    //unused
    private void delDir() throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("MyApp", "No SDCARD");
        } else {
            File directory;
            directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Chipmunked"); //problem here folder not created
            FileUtils.cleanDirectory(directory);
            FileUtils.deleteDirectory(directory);
            directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Chiprecords");
            FileUtils.cleanDirectory(directory);
            FileUtils.deleteDirectory(directory);
        }
    }

    //overriding home back button to finish activity once it is touched
    public void onBackPressed()
    {
        this.finish();
    }
}

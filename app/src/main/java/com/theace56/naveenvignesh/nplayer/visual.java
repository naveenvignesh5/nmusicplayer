package com.theace56.naveenvignesh.nplayer;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

/**
 * Created by Naveen Vignesh on 12-07-2016.
 */

//code to initiate visualizer for a media player playing any audio data
public class visual {
    private Visualizer mVisualizer;
    private MediaPlayer mMediaPlayer;
    private VisualizerView mVisualizerView;


    public visual()
    {

    }

    public visual(MediaPlayer m,VisualizerView view)
    {
        this.mMediaPlayer = m;
        this.mVisualizerView = view;
        initAudio();
    }

    private void initAudio() {

        setupVisualizerFxAndUI();
        // Make sure the visualizer is enabled only when you actually want to
        // receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);
        // When the stream ends, we don't need to collect any more data. We
        // don't do this in
        // setupVisualizerFxAndUI because we likely want to have more,
        // non-Visualizer related code
        // in this callback.
        mMediaPlayer
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mVisualizer.setEnabled(false);
                    }
                });
        mMediaPlayer.start();

    }

    public void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

}

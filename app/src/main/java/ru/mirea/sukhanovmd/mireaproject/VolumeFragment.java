package ru.mirea.sukhanovmd.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VolumeFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private boolean isRecording = false;
    private MediaRecorder recorder;
    private Timer timer;
    private ImageView audioLevelImage;
    private ProgressBar audioLevelProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_volume, container, false);
        audioLevelImage = rootView.findViewById(R.id.audioLevelImage);
        audioLevelProgressBar = rootView.findViewById(R.id.audioLevelProgressBar);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
        } else {
            startRecording();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
    }

    private void startRecording() {
        isRecording = true;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        String filePath = getActivity().getExternalFilesDir(null).getAbsolutePath() + "/audio_record.3gp";
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAudioLevel();
            }
        }, 0, 100);
    }

    private void stopRecording() {
        isRecording = false;
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void updateAudioLevel() {
        if (isRecording && recorder != null) {
            int amplitude = recorder.getMaxAmplitude();
            int level = (int) (20 * Math.log10(amplitude));

            int imageResource = R.drawable.ic_volume_low;
            if (level > 40) {
                imageResource = R.drawable.ic_volume_medium;
            }
            if (level > 70) {
                imageResource = R.drawable.ic_volume_high;
            }
            audioLevelImage.setImageResource(imageResource);
            audioLevelProgressBar.setProgress(level);
        }
    }
}

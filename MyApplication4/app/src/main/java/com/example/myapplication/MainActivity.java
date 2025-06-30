package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView threetimeTextView, onetimeTextView, onefiveTextView, blackskirtTextView;
    private ImageView imageView, leftIcon, rightIcon, stopIcon;
    private ImageButton likeButton, spotButton;
    private SeekBar seekBar;

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 연결
        threetimeTextView = findViewById(R.id.threetimetextView);
        onetimeTextView = findViewById(R.id.onetimetextView);
        onefiveTextView = findViewById(R.id.onefivetextView);
        blackskirtTextView = findViewById(R.id.blackskirttextView);
        imageView = findViewById(R.id.imageView);
        leftIcon = findViewById(R.id.leftIcon);
        rightIcon = findViewById(R.id.rightIcon);
        stopIcon = findViewById(R.id.stopIcon);
        likeButton = findViewById(R.id.likeButton);
        spotButton = findViewById(R.id.spotButton);
        seekBar = findViewById(R.id.seekBar);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mp.getDuration());
            updateTimeLabels(0, mp.getDuration());
        });

        // 15초 전으로 이동
        leftIcon.setOnClickListener(v -> {
            int newPosition = mediaPlayer.getCurrentPosition() - 15000;
            mediaPlayer.seekTo(Math.max(newPosition, 0)); // 0보다 작아지지 않도록ㄱ
            updateTimeLabels(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
        });

        // 15초 후로 이동
        rightIcon.setOnClickListener(v -> {
            int newPosition = mediaPlayer.getCurrentPosition() + 15000;
            mediaPlayer.seekTo(Math.min(newPosition, mediaPlayer.getDuration())); // 총 시간보다 넘지 않게
            updateTimeLabels(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
        });


        // 재생/정지 버튼
        stopIcon.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
                stopIcon.setImageResource(R.drawable.baseline_play_arrow_24);
            } else {
                mediaPlayer.start();
                stopIcon.setImageResource(R.drawable.baseline_pause_24);
                updateSeekBar();
            }
            isPlaying = !isPlaying;
        });

        // 음악이 끝났을 때
        mediaPlayer.setOnCompletionListener(mp -> {
            stopIcon.setImageResource(R.drawable.baseline_play_arrow_24);
            isPlaying = false;
            seekBar.setProgress(0);
            updateTimeLabels(0, mediaPlayer.getDuration());
        });

        // 재생바 누를 때
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateTimeLabels(progress, mediaPlayer.getDuration());
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 초기 시간 표시
        updateTimeLabels(0, mediaPlayer.getDuration());
    }

    // 시간 포맷 표시 업데이트
    private void updateTimeLabels(int current, int total) {
        threetimeTextView.setText(formatTime(current));
        onetimeTextView.setText(formatTime(total));
    }

    // 시간 형식 변환
    private String formatTime(int millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // SeekBar 업데이트 반복
    private void updateSeekBar() {
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            handler.postDelayed(this::updateSeekBar, 500);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}

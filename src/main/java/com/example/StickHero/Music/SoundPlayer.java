package com.example.StickHero.Music;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoundPlayer {
    private final List<MediaPlayer> mediaPlayers;
    private static MediaPlayer currentMediaPlayer;
    private final Random random;

    public SoundPlayer(String... soundPaths) {
        mediaPlayers = new ArrayList<>();
        random = new Random();

        // Load sounds from the provided paths
        for (String soundPath : soundPaths) {
            Media media = new Media(Paths.get(soundPath).toUri().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayers.add(mediaPlayer);
        }
    }

    public void playRandomSound() {
        if (!mediaPlayers.isEmpty()) {
            int randomIndex = random.nextInt(mediaPlayers.size());
            MediaPlayer randomMediaPlayer = mediaPlayers.get(randomIndex);
            randomMediaPlayer.play();
        }
    }
    public static void playSound(String soundPath,double rate) {
        Media media = new Media(Paths.get(soundPath).toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setRate(rate);
        mediaPlayer.play();
    }
    public static void playSound1(String soundPath) {
        Media media = new Media(Paths.get(soundPath).toUri().toString());
        currentMediaPlayer = new MediaPlayer(media);
        currentMediaPlayer.play();
    }
    public static void stopSound() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
        }
    }
    public void stopAllSounds() {
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.stop();
        }
    }
}

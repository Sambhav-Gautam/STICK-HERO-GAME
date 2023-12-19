package com.example.StickHero.Music;



import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoundPlayerGame {
    private final List<MediaPlayer> mediaPlayers;
    private final Random random;

    public SoundPlayerGame(String... soundPaths) {
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
            randomMediaPlayer.setVolume(0.5);
            randomMediaPlayer.play();
        }
    }

    public void stopAllSounds() {
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.stop();
        }
    }
}


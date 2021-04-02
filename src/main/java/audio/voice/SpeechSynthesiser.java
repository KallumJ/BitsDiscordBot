package audio.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.IOException;

/**
 * A class to synthesise speech based on input text
 */
public class SpeechSynthesiser {

    private final AudioPlayerManager playerManager;
    private final AudioPlayer audioPlayer;

    /**
     * Constructs a SpeechSynthesiser object
     */
    public SpeechSynthesiser() {
        // Create LavaPlayer audio manager
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);

        // Create LavaPlayer audio player
        this.audioPlayer = playerManager.createPlayer();
    }

    /**
     * Produces speech and stores the output wav file in the sounds directory
     *
     * @param text The text to synthesise
     */
    public void synthesiseSpeech(String text) {
        try {
            // Execute text-to-speech script for pico2wave
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "./tts.sh", text);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();

            // Play the generated response back
            playResponseToUser();
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException("Failed to synthesise speech", ex);
        }
    }

    //TODO: implement
    public void playResponseToUser() {
        playerManager.loadItem("sounds/output.wav", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                // If a response is successfully loaded, play the response
                audioPlayer.playTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                // We will never load a playlist of tracks.
            }

            @Override
            public void noMatches() {
                throw new RuntimeException("Failed to find Bob's response");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                throw new RuntimeException("Failed to load Bob's text to speech response", e);
            }
        });
    }

    /**
     * Returns LavaPlayer audio player
     *
     * @return AudioPlayer object used by SpeechSynthesiser
     */
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}

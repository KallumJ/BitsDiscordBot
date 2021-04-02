package audio.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.PlayerResumeEvent;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import events.audio.AudioScheduler;

import java.io.IOException;

/**
 * A class to synthesise speech based on input text
 */
public class SpeechSynthesiser {

    private final AudioPlayerManager playerManager;
    private final AudioPlayer audioPlayer;

    public SpeechSynthesiser() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);

        this.audioPlayer = playerManager.createPlayer();

        AudioScheduler audioScheduler = new AudioScheduler(audioPlayer);
        audioPlayer.addListener(audioScheduler);
    }

    /**
     * Produces speech and stores the output wav file in the sounds directory
     *
     * @param text The text to synthesise
     */
    public void synthesiseSpeech(String text) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "./tts.sh", text);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
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
                audioPlayer.playTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}

package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A class to manage sound playback
 */
public class SoundManager {

    private static final AudioPlayerManager PLAYER_MANAGER = new DefaultAudioPlayerManager();
    private final static AudioPlayer AUDIO_PLAYER = PLAYER_MANAGER.createPlayer();
    private final Queue<AudioTrack> trackQueue;
    private final SoundScheduler soundScheduler;

    /**
     * Constructs a SoundManager object
     */
    public SoundManager() {
        // Create LavaPlayer audio manager
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);

        this.trackQueue = new LinkedBlockingDeque<>();

        // Add Listener to player
        this.soundScheduler = new SoundScheduler(this.trackQueue);
        AUDIO_PLAYER.addListener(soundScheduler);
    }

    /**
     * Load a sample of speech from the bot
     *
     * @param soundIdentifier the path of the sound to load (file/youtube id)
     */
    public void loadSpeechSound(String soundIdentifier) {
        PLAYER_MANAGER.loadItem(soundIdentifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                // If a Track is currently playing, speak, but then resume afterwards
                AudioTrack currentAudioTrack = AUDIO_PLAYER.getPlayingTrack();
                if (currentAudioTrack != null) {
                    soundScheduler.setPriorityTrack(currentAudioTrack);
                    AUDIO_PLAYER.stopTrack();
                }
                AUDIO_PLAYER.playTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                // We will never load a playlist here
            }

            @Override
            public void noMatches() {
                throw new RuntimeException("Failed to find provided sound " + soundIdentifier);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                throw new RuntimeException("Failed to load provided sound " + soundIdentifier, e);
            }
        });
    }

    /**
     * Load a sound, but queue it up first if there something playing
     *
     * @param soundIdentifier the path of the sound to load (file/youtube id)
     */
    public void loadOrderedSound(String soundIdentifier) {
        PLAYER_MANAGER.loadItem(soundIdentifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                // Play the track if nothing is playing, else queue it
                AudioTrack currentAudioTrack = AUDIO_PLAYER.getPlayingTrack();

                if (currentAudioTrack != null) {
                    trackQueue.add(audioTrack);
                } else {
                    AUDIO_PLAYER.playTrack(audioTrack);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                // Queue the tracks, and if none is playing, start the first one
                trackQueue.addAll(audioPlaylist.getTracks());

                AudioTrack currentAudioTrack = AUDIO_PLAYER.getPlayingTrack();
                if (currentAudioTrack == null) {
                    AUDIO_PLAYER.playTrack(trackQueue.poll());
                }
            }

            @Override
            public void noMatches() {
                throw new RuntimeException("Failed to find provided sound " + soundIdentifier);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                throw new RuntimeException("Failed to load provided sound " + soundIdentifier, e);
            }
        });
    }

    /**
     * Returns the audio player for the SoundManager
     *
     * @return AudioPlayer the audio player
     */
    public AudioPlayer getAudioPlayer() {
        return AUDIO_PLAYER;
    }
}

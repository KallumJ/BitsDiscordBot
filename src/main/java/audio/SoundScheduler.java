package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Queue;

/**
 * A class to manage the scheduling of sounds from an AudioPlayer
 */
public class SoundScheduler extends AudioEventAdapter {
    private final Queue<AudioTrack> soundQueue;
    private AudioTrack priorityTrack;

    /**
     * Constructs a SoundScheduler object
     *
     * @param soundQueue The current queue of sounds
     */
    public SoundScheduler(Queue<AudioTrack> soundQueue) {
        this.soundQueue = soundQueue;
    }

    /**
     * Method to be called every time a track ends
     *
     * @param player    The audio player playing the track
     * @param track     The track that ended
     * @param endReason The reason it ended
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (priorityTrack != null) {
            player.playTrack(priorityTrack);
        } else if (!soundQueue.isEmpty()) {
            player.playTrack(soundQueue.poll());
        }
    }

    /**
     * Method to be called when a track throws an exception
     *
     * @param player    the audio player playing the track
     * @param track     the track that threw the exception
     * @param exception the exception it threw
     */
    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        throw new RuntimeException("Current sound has thrown an exception", exception);
    }

    /**
     * Sets the priority track
     *
     * @param priorityTrack The track to make a priority
     */
    public void setPriorityTrack(AudioTrack priorityTrack) {
        this.priorityTrack = priorityTrack;
    }
}

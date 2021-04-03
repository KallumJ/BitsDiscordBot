package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Queue;

public class SoundScheduler extends AudioEventAdapter {
    private final Queue<AudioTrack> soundQueue;
    private AudioTrack priorityTrack;

    public SoundScheduler(Queue<AudioTrack> soundQueue) {
        this.soundQueue = soundQueue;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (priorityTrack != null) {
            player.playTrack(priorityTrack);
        } else if (!soundQueue.isEmpty()) {
            player.playTrack(soundQueue.poll());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        throw new RuntimeException("Current sound has thrown an exception", exception);
    }

    public void setPriorityTrack(AudioTrack priorityTrack) {
        this.priorityTrack = priorityTrack;
    }
}

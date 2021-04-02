package events.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;

public class AudioScheduler implements AudioEventListener {

    private final AudioPlayer player;

    public AudioScheduler(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void onEvent(AudioEvent audioEvent) {

    }
}

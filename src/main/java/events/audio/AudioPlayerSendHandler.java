package events.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * A class to handle the sending of audio to the user
 */
public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame audioFrame;

    /**
     * Constructs an AudioPlayerSendHandler object.
     *
     * @param audioPlayer The AudioPlayer object in use
     */
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(1024);
        this.audioFrame = new MutableAudioFrame();
        this.audioFrame.setBuffer(buffer);
    }

    /**
     * Returns whether we can currently provide audio
     *
     * @return true if we can, false otherwise
     */
    @Override
    public boolean canProvide() {
        return this.audioPlayer.provide(this.audioFrame);
    }

    /**
     * Provides the audio to the user
     *
     * @return a ByteBuffer of audio information
     */
    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return this.buffer.flip();
    }

    /**
     * Returns whether the audio is encoded with Opus
     *
     * @return true, LavaPlayer audio is Opus by default
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}

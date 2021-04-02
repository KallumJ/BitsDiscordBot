package util;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * A class to assist with the use of sound files for Bob
 */
public class SoundFileUtils {

    /**
     * Creates a wav file (48kHz, 16bit, dual channel, signed, big endian) from byte[] array
     *
     * @param file      The file to wav file as
     * @param audioData The audio data
     * @throws IOException Thrown when file is unable to be written
     */
    public static void createWavFile(File file, byte[] audioData) throws IOException {
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(audioData), AudioReceiveHandler.OUTPUT_FORMAT, audioData.length), AudioFileFormat.Type.WAVE, file);
    }

    /**
     * Takes in a wav file, and converts it to a wav file that is compatible with Vosk transcription models
     *
     * @param source The source wav file
     * @param out    The out wav file
     * @throws IOException                   Thrown when file cannot be accessed
     * @throws UnsupportedAudioFileException Thrown when file is in incorrect format
     */
    public static void generateTranscriptionCompatibleFile(File source, File out) throws IOException, UnsupportedAudioFileException {
        AudioFormat transcriptionFormat = new AudioFormat(16000.0F, 16, 1, true, true);

        AudioInputStream in = AudioSystem.getAudioInputStream(source);
        AudioInputStream convert = AudioSystem.getAudioInputStream(transcriptionFormat, in);

        AudioSystem.write(convert, AudioFileFormat.Type.WAVE, out);
    }
}

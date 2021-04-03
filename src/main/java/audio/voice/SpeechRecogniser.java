package audio.voice;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import util.SoundFileUtils;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class SpeechRecogniser {

    private final Recognizer recognizer;

    private static final String LANGUAGE_MODEL = "small_lang_model";
    private static final String INPUT_FILE = "sounds/input.wav";
    private static final String CONVERTED_FILE = "sounds/convertedInput.wav";

    /**
     * Constructs a SpeechRecongiser object with the appropriate language model
     */
    public SpeechRecogniser() {
        LibVosk.setLogLevel(LogLevel.WARNINGS);
        Model model = new Model(LANGUAGE_MODEL);
        this.recognizer = new Recognizer(model, 16000);
    }

    /**
     * Transcribes audio information from byte array of audio information
     *
     * @param bytes The audio data
     * @return String, The transcription
     */
    public String transcribeAudioFromByteArray(byte[] bytes) {
        // Use byte data to create sound file
        File file = new File(INPUT_FILE);
        try {
            SoundFileUtils.createWavFile(file, bytes);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create wav file. " + ex);
        }

        // Convert file to transcription compatible format
        File convertedFile = new File(CONVERTED_FILE);
        try {
            SoundFileUtils.generateTranscriptionCompatibleFile(file, convertedFile);
        } catch (UnsupportedAudioFileException | IOException ex) {
            throw new RuntimeException("Failed to convert wav file.", ex);
        }

        return transcribeAudio(convertedFile);
    }

    /**
     * Transcribes audio from provided file in correct audio format (16kHz, 16bit, mono)
     *
     * @param file The audio file
     * @return String, The transcription
     */
    public String transcribeAudio(File file) {

        String result;
        try {
            // Open stream to the senders input
            InputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));

            // Analyse the input
            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = inputStream.read(b)) >= 0) {
                recognizer.acceptWaveForm(b, nbytes);
            }

            // Get the output
            JSONObject jsonObject = new JSONObject(recognizer.getResult());
            result = (String) jsonObject.get("text");

        } catch (IOException | UnsupportedAudioFileException | JSONException ex) {
            throw new RuntimeException("Unable to transcribe audio", ex);
        }

        return result;
    }
}

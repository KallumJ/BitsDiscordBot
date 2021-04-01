package voice;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class SpeechRecogniser {

    /**
     * Transcribes audio information from byte array of audio information
     *
     * @param bytes The audio data
     * @return String, The transcription
     */
    public String transcribeAudioFromByteArray(byte[] bytes) {
        // Use that byte data to create sound file
        File file = new File("sounds/input.wav");
        try {
            SoundFileUtils.createWavFile(file, bytes);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create wav file. " + ex);
        }

        // Convert file to transcription compatible format
        File convertedFile = new File("sounds/convertedInput.wav");
        try {
            SoundFileUtils.generateTranscriptionCompatibleFile(file, convertedFile);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
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

        LibVosk.setLogLevel(LogLevel.DEBUG);

        String result = null;
        try (Model model = new Model("model")) {
            InputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
            Recognizer recognizer = new Recognizer(model, 16000);

            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = inputStream.read(b)) >= 0) {
                if (recognizer.acceptWaveForm(b, nbytes)) {
                    System.out.println(recognizer.getResult());
                } else {
                    System.out.println(recognizer.getPartialResult());
                }
            }

            JSONObject jsonObject = new JSONObject(recognizer.getResult());
            result = (String) jsonObject.get("text");

        } catch (IOException | UnsupportedAudioFileException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}

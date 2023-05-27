import com.dropbox.core.v2.DbxClientV2;

import javax.sound.sampled.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class JavaSoundRecorder {

    DbxClientV2 client;

    AudioFileFormat.Type fileType;

    TargetDataLine line;

    AudioFormat format;

    DataLine.Info info;

    public JavaSoundRecorder(DbxClientV2 client) {
        this.client = client;
        this.fileType = AudioFileFormat.Type.WAVE;
        this.format = getAudioFormat();
        this.info = new DataLine.Info(TargetDataLine.class, format);
    }

    public void recordAudio (int milliseconds) {
        String fileName = "temp.wav";
        String folder = "C:/Users/user/Desktop/";
        File file = new File(folder + fileName);
        start(file);
        finish(file, milliseconds);
    }

    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }
    void start(File file) {
        Thread thread = new Thread(() -> {
            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                AudioInputStream ais = new AudioInputStream(line);
                AudioSystem.write(ais, fileType, file);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        thread.start();

    }
    void finish(File file, int milliseconds) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            line.stop();
            line.close();
            recordAudio(milliseconds);

            try (InputStream in = new FileInputStream(file)) {

                String pattern = "yyyyMMdd_kkmmss";
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat(pattern);
                String name = simpleDateFormat.format(new Date());

                client.files().uploadBuilder("/records/" + name + ".wav")
                        .uploadAndFinish(in);

                file.delete();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
        thread.start();
    }
}
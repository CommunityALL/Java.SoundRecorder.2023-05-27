import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
public class Main {

    public static void main(String[] args) {

        String ACCESS_TOKEN =  "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/recorder").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        JavaSoundRecorder recorder =
                new JavaSoundRecorder(client);
        recorder.recordAudio(30000);

    }
}
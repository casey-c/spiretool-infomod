package InfoMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class MiscUtils {
    public static String resourceStreamToString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String read;

        while ((read=br.readLine()) != null) {
            //System.out.println(read);
            sb.append(read);
        }

        br.close();
        return sb.toString();
    }
}

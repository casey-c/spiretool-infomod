package InfoMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
  Random assortment of utility functions to pull on that don't belong anywhere else. Math / probability specific
  functions can be found in ProbabilityUtils instead, while rendering and graphics related utilities can be found in
  RenderingUtils. This is for the rest that don't fall into those groups.
 */
public class MiscUtils {
    // Builds a string from an input stream
    public static String resourceStreamToString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String read;
        while ((read=br.readLine()) != null) {
            sb.append(read);
        }

        br.close();
        return sb.toString();
    }
}

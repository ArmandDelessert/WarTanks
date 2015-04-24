package navalbattle.nethelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public class NetHelper {

    /**
     * Check if the supplied IP address is a valid IPv4 IP address
     * @param ip The textual representation of the IP address to check
     * @return boolean
     */
    
    public static boolean isIPv4Valid(String ip) {
        String[] parts = ip.split(".");

        if (parts.length != 4) {
            return false;
        }

        for (String s : parts) {
            try {
                int part = Integer.parseInt(s);

                if (part < 0 || part > 255) {
                    return false;
                }
            } catch (NumberFormatException ex) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Read from the supplied reader until a new line is encountered
     * @param reader The stream to read from
     * @return byte[] the content read before the new line
     * @throws java.io.IOException in case there is a read error from the stream
     */
    
    public static byte[] readUntilCRLF(InputStream reader) throws IOException {
        ByteArrayOutputStream contentsByte = new ByteArrayOutputStream();
        int read;
        int lastRead = 0;

       while ((read = reader.read()) != -1) {
            if (read == 10 && lastRead == 13) {
                break; // complete line received
            } else if (lastRead == 13) {
                contentsByte.write(lastRead);
                contentsByte.write(read);
            } else if (read != 13) {
                contentsByte.write(read);
            }

            lastRead = read;
        }

        contentsByte.flush();
        return contentsByte.toByteArray();
    }
}

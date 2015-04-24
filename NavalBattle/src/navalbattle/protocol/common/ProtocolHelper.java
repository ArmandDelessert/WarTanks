package navalbattle.protocol.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.transform.TransformerException;
import navalbattle.protocol.messages.common.NavalBattleMessage;
import navalbattle.nethelper.NetHelper;
import org.w3c.dom.Document;
import xmlhelper.XMLHelper;

public class ProtocolHelper {
    
    /**
     * Send the supplied message to the supplied writer stream
     * @param writer The stream to read to
     * @param str The message to write
     */
    
    private static void sendMessage(OutputStream os, String str) {
        
        String trimmed = str.trim();
        
        System.out.println("Sending the following message:");
        System.out.println(trimmed);
        
        try {
            byte[] toSend = (trimmed + "\r\n\r\n").getBytes(NavalBattleProtocol.ENCODING);
            
            os.write(toSend, 0, toSend.length);
            os.flush();
        } catch (IOException ex) {
        }
    }
    
    public static String readMessage(InputStream is) throws IOException
    {
        ByteArrayOutputStream binaryMessage = new ByteArrayOutputStream();

        // Reading until two lines empty are encountered (=end of message)
        while (true) {
            byte[] contents = NetHelper.readUntilCRLF(is);

            // New message received
            if (contents.length == 0) {
                break;
            }
            try {
                binaryMessage.write(contents);
                binaryMessage.write("\r\n".getBytes(NavalBattleProtocol.ENCODING));
            } catch (IOException ex) {
            }
        }

        // Getting the text representation of the message
        String message = null;
        
        try {
            message = new String(binaryMessage.toByteArray(), NavalBattleProtocol.ENCODING).trim();
        } catch (UnsupportedEncodingException ex) {
            // Encoding is valid
        }
        
        System.out.println("Received the following message:");
       System.out.println(message);
        
        return message;
    }

    public static void sendMessage(OutputStream writer, NavalBattleMessage messageToSend) {
        
        if (writer == null)
            return;
        
        Document xmlDoc = messageToSend.getXMLRepresentation();
        String xmlMessagePrettyString = null;

        try {
            xmlMessagePrettyString = XMLHelper.prettyPrintXML(xmlDoc, NavalBattleProtocol.ENCODING);
        } catch (TransformerException ex) {
        }

        ProtocolHelper.sendMessage(writer, xmlMessagePrettyString);
    }
}

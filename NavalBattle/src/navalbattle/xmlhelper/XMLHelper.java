package xmlhelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLHelper {

    public static HashMap<String, Node> getChildren(Node node) {
        HashMap<String, Node> ret = new HashMap<>();
        NodeList nodeList = node.getChildNodes();

        int len = nodeList.getLength();
        for (int i = 0; i < len; i++) {
            Node currentNode = nodeList.item(i);
            ret.put(currentNode.getNodeName(), currentNode);
        }

        return ret;
    }

    public static DocumentBuilder getDocumentBuilder() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;

        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
        }

        return docBuilder;
    }

    public static Document parseXMLFromString(String xml) throws Exception {
        DocumentBuilder builder = XMLHelper.getDocumentBuilder();

        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
    
     public static Document parseXMLFromFile(String filePath) throws Exception {
        DocumentBuilder builder = XMLHelper.getDocumentBuilder();
        return builder.parse(new File(filePath));
    }

    public static Document createBaseDocument() {
        DocumentBuilder docBuilder = XMLHelper.getDocumentBuilder();

        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
        doc.setXmlVersion("1.0");

        return doc;
    }

    public static String prettyPrintXML(Document xmlDoc, String encoding) throws TransformerConfigurationException, TransformerException {
        Transformer tf = TransformerFactory.newInstance().newTransformer();

        tf.setOutputProperty(OutputKeys.ENCODING, encoding);
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        tf.setOutputProperty(OutputKeys.STANDALONE, "yes");

        Writer stringWriter = new StringWriter();
        tf.transform(new DOMSource(xmlDoc), new StreamResult(stringWriter));

        return stringWriter.toString();
    }
    
    public static void writeXMLToFile(Document xmlDoc, String encoding, String outputFile) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), encoding));
        try {
            try {
                out.write(XMLHelper.prettyPrintXML(xmlDoc, encoding));
            } catch (TransformerException ex) {
            }
        } finally {
            out.close();
        }
    }
}

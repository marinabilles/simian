package de.crispda.sola.multitester.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLFile {
    public final Document document;
    public final Element root;
    public final File log;
    private static final Pattern tagNamePattern = Pattern.compile("<([A-Za-z]*)>");

    public XMLFile(String path) throws IOException, ParserConfigurationException, SAXException {
        log = new File(path);
        if (!log.exists()) {
            throw new FileNotFoundException(path);
        }

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        builder.setEntityResolver((publicId, systemId) -> {
            if (systemId.contains("logger.dtd")) {
                return new InputSource(new StringReader(""));
            } else {
                return null;
            }
        });

        String content = new String(Files.readAllBytes(java.nio.file.Paths.get(path)));
        String[] lines = content.split("\n");

        Optional<String> tagName = Optional.empty();
        for (String line : lines) {
            if (!line.startsWith("<?") && !line.startsWith("<!")) {
                Matcher tagNameMatcher = tagNamePattern.matcher(line);
                if (tagNameMatcher.find()) {
                    tagName = Optional.of(tagNameMatcher.group(1));
                    break;
                }
            }
        }

        if (tagName.isPresent()) {
            String lastLine = lines[lines.length - 1];
            if (!lastLine.contains("</" + tagName.get() + ">")) {
                content = content + "</" + tagName.get() + ">\n";
            }
        }

        document = builder.parse(new InputSource(new StringReader(content)));
        root = document.getDocumentElement();
        root.normalize();
    }

    public XMLFile(String path, String rootName) throws ParserConfigurationException, IOException, SAXException {
        log = new File(path);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (!log.exists()) {
            document = builder.newDocument();
            root = document.createElement(rootName);
            document.appendChild(root);
        } else {
            document = builder.parse(log);
            root = document.getDocumentElement();
            root.normalize();
        }
    }

    public static Element createElement(Document doc, String tagName, String textContent) {
        Element el = doc.createElement(tagName);
        el.appendChild(doc.createTextNode(textContent));
        return el;
    }
}

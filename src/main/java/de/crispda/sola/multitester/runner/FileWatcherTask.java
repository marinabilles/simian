package de.crispda.sola.multitester.runner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.concurrent.Task;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileWatcherTask extends Task<String> {
    private volatile Path filename;
    private final Pattern rectanglePattern =
            Pattern.compile("Exclusion rectangle: \\((\\d+), (\\d+), (\\d+), (\\d+)\\)");
    private final List<Rectangle> exclusionRectangles =
            Collections.synchronizedList(new ArrayList<>());

    public void setFilename(Path newFilename) {
        System.out.println("Setting filename to " + newFilename.toString());
        filename = newFilename;
    }

    @Override
    protected String call() throws Exception {
        Path dir = Paths.get(Execution.path);
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                if (isCancelled())
                    return null;

                try {
                    if (!key.isValid())
                        key = watchService.take();
                } catch (InterruptedException e) {
                    return null;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                        continue;

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    Path child;
                    child = dir.resolve(filename);
                    System.out.println("Filename: " + child);
                    if (!child.equals(this.filename))
                        continue;

                    System.out.println("Updating...");
                    loadFile();
                }

                Thread.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void loadFile() {
        exclusionRectangles.clear();

        StringBuilder sb = new StringBuilder();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> {
                if (systemId.contains("logger.dtd")) {
                    return new InputSource(new StringReader(""));
                } else {
                    return null;
                }
            });
            String content = new String(Files.readAllBytes(this.filename));
            String[] lines = content.split("\n");
            String lastLine = lines[lines.length - 1];
            if (!lastLine.contains("</log>")) {
                content = content + "</log>\n";
            }
            Document document = builder.parse(new InputSource(new StringReader(content)));
            document.getDocumentElement().normalize();
            NodeList records = document.getElementsByTagName("record");
            for (int i = 0; i < records.getLength(); i++) {
                Node rNode = records.item(i);
                if (rNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element record = (Element) rNode;
                String date = record.getElementsByTagName("date").item(0).getTextContent();
                String mLevel = record.getElementsByTagName("level").item(0).getTextContent();
                String mClass = record.getElementsByTagName("class").item(0).getTextContent();
                String mMethod = record.getElementsByTagName("method").item(0).getTextContent();
                String mMessage = record.getElementsByTagName("message").item(0).getTextContent();

                if (mMessage.startsWith("Exclusion rectangle")) {
                    Matcher m = rectanglePattern.matcher(mMessage);
                    if (m.find()) {
                        exclusionRectangles.add(new Rectangle(
                                Integer.parseInt(m.group(1)),
                                Integer.parseInt(m.group(2)),
                                Integer.parseInt(m.group(3)),
                                Integer.parseInt(m.group(4))));
                    }
                }

                sb.append(date).append(" ").append(mLevel).append(": ");
                // sb.append(mClass).append(".").append(mMethod).append(" - ");
                sb.append(mMessage).append("\n");
            }
        } catch (IOException ignored) {
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        updateMessage(sb.toString());
    }

    public List<Rectangle> getExclusionRectangles() {
        List<Rectangle> exclusionRectangles = new ArrayList<>();
        synchronized (this.exclusionRectangles) {
            exclusionRectangles.addAll(this.exclusionRectangles);
        }

        return exclusionRectangles;
    }
}

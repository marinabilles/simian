package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.util.Paths;
import de.crispda.sola.multitester.util.XMLFile;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.ScreenshotException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.crispda.sola.multitester.util.XMLFile.createElement;

public class FailureRecorder {
    private final String path;
    private int errorCount = 0;
    private int failureCount = 0;
    private Transformer transformer;

    public FailureRecorder(String path) throws TransformerConfigurationException {
        this.path = path;
        transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    }

    public void recordError(WebDriver firstDriver, WebDriver secondDriver, int runCount, Throwable cause) {
        while (cause != null) {
            Throwable prevCause = cause;
            cause = cause.getCause();
            if (cause == prevCause)
                break;
            if (cause instanceof ScreenshotException && Paths.exists(path)) {
                String errorScr = ((ScreenshotException) cause).getBase64EncodedScreenshot();
                byte[] errorScrBytes = OutputType.BYTES.convertFromBase64Png(errorScr);
                try {
                    StringBuilder firstb = new StringBuilder();
                    StringBuilder secondb = new StringBuilder();
                    LogEntries firstlog = firstDriver.manage().logs().get(LogType.BROWSER);
                    LogEntries secondlog = secondDriver.manage().logs().get(LogType.BROWSER);
                    for (LogEntry entry : firstlog) {
                        firstb.append(entry).append("\n");
                    }
                    for (LogEntry entry : secondlog) {
                        secondb.append(entry).append("\n");
                    }
                    FileUtils.writeStringToFile(
                            new File(String.format("%s/error%d_%d_first.log", path, runCount, errorCount)),
                            firstb.toString());
                    FileUtils.writeStringToFile(
                            new File(String.format("%s/error%d_%d_second.log", path, runCount, errorCount)),
                            secondb.toString());
                    FileUtils.writeByteArrayToFile(
                            new File(String.format("%s/error%d_%d.png", path, runCount, errorCount)),
                            errorScrBytes);
                    errorCount++;
                } catch (IOException ignored) {
                }

                break;
            }
        }
    }

    public void reset() {
        errorCount = 0;
    }

    public void recordFailures(int runCount, TransitionSequence sequence, Test first, Test second) {
        try {
            if (!Paths.exists(path))
                throw new IOException("Path " + path + " not found");

            saveFiles(first, second);

            failureCount++;

            FailureReason fr = first.getFailureReason();
            int threadId = 0, otherIndex = 0;
            if (fr != null) {
                threadId = fr.executingThread ? 1 : 2;
                otherIndex = second.getIndex();
            } else {
                fr = second.getFailureReason();
                if (fr != null) {
                    threadId = fr.executingThread ? 2 : 1;
                    otherIndex = first.getIndex();
                }
            }

            if (threadId > 0) {
                XMLFile xmlFile = new XMLFile(path + "/failures.xml", "failures");
                Document doc = xmlFile.document;
                Element failureEl = doc.createElement("failure");
                xmlFile.root.appendChild(failureEl);

                failureEl.appendChild(createElement(doc, "runId", Integer.toString(runCount)));
                failureEl.appendChild(createElement(doc, "id", Integer.toString(failureCount - 1)));
                Element sequenceEl = doc.createElement("sequence");
                for (int i = 0; i < sequence.size(); i++) {
                    sequenceEl.appendChild(createElement(doc, "transition", sequence.get(i).toString()));
                }
                failureEl.appendChild(sequenceEl);

                Element stateEl = doc.createElement("state");
                Transition latest = sequence.getLatest(threadId, fr.index, threadId == 1 ? 2 : 1, otherIndex);
                if (latest == null) {
                    stateEl.appendChild(createElement(doc, "threadId", Integer.toString(threadId)));
                    stateEl.appendChild(createElement(doc, "sequenceId", Integer.toString(fr.index)));
                } else {
                    stateEl.appendChild(createElement(doc, "threadId", Integer.toString(latest.threadId)));
                    stateEl.appendChild(createElement(doc, "sequenceId", Integer.toString(latest.sequenceId)));
                }
                failureEl.appendChild(stateEl);

                failureEl.appendChild(createElement(doc, "failedThreadId", Integer.toString(threadId)));
                failureEl.appendChild(createElement(doc, "neutralEventName", fr.neutralEventName));

                transformer.transform(new DOMSource(doc), new StreamResult(xmlFile.log));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordFailures(int runCount, ScheduleVariant<CombinedTest> variant, Test first, Test second) {
        try {
            if (!Paths.exists(path))
                throw new IOException("Path " + path + " not found");

            saveFiles(first, second);

            FailureReason fr = first.getFailureReason();
            int threadId = 0;
            if (fr != null) {
                threadId = fr.executingThread ? 1 : 2;
            } else {
                fr = second.getFailureReason();
                if (fr != null) {
                    threadId = fr.executingThread ? 2 : 1;
                }
            }

            if (threadId > 0) {
                recordVariantFailure(runCount, variant, threadId, fr.neutralEventName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFiles(Test first, Test second) {
        String formatString = path + "/failed_%s" + Integer.toString(failureCount) + "_%d.png";
        saveFiles(first, second, formatString);
    }

    public static void saveFiles(Test first, Test second, String formatString) {
        List<byte[]> firstTestFailures = first.getTestFailures();
        for (int i = 0; i < firstTestFailures.size(); i++) {
            try {
                FileUtils.writeByteArrayToFile(
                        new File(String.format(formatString, "f", i)),
                        firstTestFailures.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<byte[]> secondTestFailures = second.getTestFailures();
        for (int i = 0; i < secondTestFailures.size(); i++) {
            try {
                FileUtils.writeByteArrayToFile(
                        new File(String.format(formatString, "s", i)),
                        secondTestFailures.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void recordVariantFailure(int runCount, ScheduleVariant<CombinedTest> variant, int failedThreadId,
                                      String neutralEventName) {
        failureCount++;

        try {
            XMLFile xmlFile = new XMLFile(path + "/failures.xml", "failures");
            Document doc = xmlFile.document;
            Element failureEl = doc.createElement("failure");
            xmlFile.root.appendChild(failureEl);

            failureEl.appendChild(createElement(doc, "runId", Integer.toString(runCount)));
            failureEl.appendChild(createElement(doc, "id", Integer.toString(failureCount - 1)));

            Element testsEl = doc.createElement("tests");
            List<CombinedTest> tests = Lists.newArrayList(variant.getFirstTest(), variant.getSecondTest());
            for (int i = 0; i < 2; i++) {
                Element testEl = doc.createElement("test");
                testsEl.appendChild(testEl);
                tests.get(i).getInteractions().forEach(act -> testEl.appendChild(
                        createElement(doc, "interaction", act.toString())));
            }
            failureEl.appendChild(testsEl);

            Element variantEl = doc.createElement("variant");
            List<List<MaybeWait>> schedules = new ArrayList<>();
            schedules.add(new ArrayList<>(variant.getFirstSchedule()));
            schedules.add(new ArrayList<>(variant.getSecondSchedule()));
            for (int i = 0; i < 2; i++) {
                Element threadEl = doc.createElement("thread");
                threadEl.appendChild(createElement(doc, "threadId", Integer.toString(i + 1)));
                Element scheduleEl = doc.createElement("schedule");
                threadEl.appendChild(scheduleEl);
                schedules.get(i).forEach(mw -> scheduleEl.appendChild(createElement(doc, "mw", mw.toString())));
                variantEl.appendChild(threadEl);
            }
            failureEl.appendChild(variantEl);

            failureEl.appendChild(createElement(doc, "failedThreadId", Integer.toString(failedThreadId)));
            failureEl.appendChild(createElement(doc, "neutralEventName", neutralEventName));

            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile.log));
        } catch (TransformerException | SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}

package de.crispda.sola.multitester.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecutionStringLogParser {
    public static void main(String[] args) throws IOException {
        if (args.length < 1)
            return;

        String logContents = FileUtils.readFileToString(new File(args[0]));
        String[] lines = logContents.split("\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm:ss a");

        List<Record> records = new ArrayList<>();
        Pattern pat = Pattern.compile(
                "^(.*) de\\.crispda\\.sola\\.multitester\\.GuidedStateSpaceExplorer parallelRun");

        for (int i = 1; i < lines.length; i++) {
            Matcher matcher = pat.matcher(lines[i]);
            if (matcher.find()) {
                String timeStamp = matcher.group(1);
                LocalDateTime datetime = LocalDateTime.parse(timeStamp, formatter);
                records.add(new Record(datetime));
            }

            if (lines[i].contains("Exploring in parallel") && !records.isEmpty()) {
                records.get(records.size() - 1).logMessage = lines[i];
            }

            if (lines[i].contains("handleException") && !records.isEmpty()) {


                StringBuilder errorMessage = new StringBuilder().append(lines[i]).append("\n");
                i++;
                while (i < lines.length && !lines[i].trim().equals("")) {
                    errorMessage.append(lines[i]).append("\n");
                    i++;
                }

                records.get(records.size() - 1).setErrorMessage(errorMessage.toString());
            }
        }

        System.out.println("Records:         " + records.size());
        System.out.println("Failed records:  " + records.stream().filter(r -> r.failed).count());


        if (args.length > 1 && records.size() > 1) {
            LocalTime near = LocalTime.parse(args[1],
                    DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            records.sort(Comparator.comparing(r2 -> Duration.between(
                    r2.dateTime.toLocalTime(), near).abs()));

            List<Record> chosenRecords = records.subList(0, 2);
            for (Record r : chosenRecords) {
                System.out.println("Record " + formatter.format(r.dateTime));
                System.out.println(r.logMessage);
                if (r.failed)
                    System.out.println(r.errorMessage + "\n");
            }
        }
    }

    private static class Record {
        String logMessage;
        String errorMessage;
        boolean failed = false;
        LocalDateTime dateTime;

        Record(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

        void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            failed = true;
        }
    }
}

package de.crispda.sola.multitester.util;

import com.google.common.collect.Sets;
import de.crispda.sola.multitester.ImageDiff;
import de.crispda.sola.multitester.ImageDimensionException;
import de.crispda.sola.multitester.Images;
import de.crispda.sola.multitester.scenario.GDocs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ImageBatchDiff {
    public static void main(String[] args) throws Exception {
        boolean failureMode = false;
        if (args.length > 0 && args[0].equals("-failure")) {
            failureMode = true;
        }

        List<Integer> numbers = new ArrayList<>();
        if (!failureMode) {
            if (args.length > 0) {
                for (String arg : args) {
                    numbers.add(Integer.parseInt(arg));
                }
            }
        }

        Pattern prefixPattern = Pattern.compile("^(failed_[fs]\\d+_)\\d+.png");
        Pattern numberPattern = Pattern.compile("^([0-9]*)_");

        SetMap map = new SetMap();
        Optional<File[]> folder = Optional.ofNullable(new File("./").listFiles());
        if (folder.isPresent()) {
            for (File file : folder.get()) {
                if (failureMode) {
                    if (!file.getName().startsWith("fail"))
                        continue;

                    Matcher prefixMatcher = prefixPattern.matcher(file.getName());
                    if (prefixMatcher.find()) {
                        String prefix = prefixMatcher.group(1);
                        map.put(prefix, file);
                    }
                } else {
                    if (file.getName().startsWith("fail") || file.getName().startsWith("error")
                            || !file.getName().endsWith(".png"))
                        continue;

                    Optional<Integer> number = numbers.stream().filter(n ->
                            file.getName().startsWith(Integer.toString(n))).findFirst();
                    if (number.isPresent()) {
                        String suffix = file.getName().substring(
                                Integer.toString(number.get()).length());
                        map.put(suffix, file);
                    } else if (numbers.size() == 0) {
                        Matcher numberMatcher = numberPattern.matcher(file.getName());
                        if (numberMatcher.find()) {
                            map.put(numberMatcher.group(1), file);
                        }
                    }
                }
            }

            map.entrySet().stream()
                    .filter(entry -> entry.getValue().size() == 2)
                    .map(entry -> new ImmutablePair<>(entry.getKey(), compare(entry.getValue())))
                    .filter(ImmutablePair::getRight)
                    .map(ImmutablePair::getLeft)
                    .sorted()
                    .forEach(str ->
                            System.out.println(
                                    String.format("%s difference", str)));
        }
    }

    private static boolean compare(Set<File> files) {
        List<byte[]> scrs =
                files.stream().map(ImageBatchDiff::readFile).collect(Collectors.toList());

        try {
            ImageDiff diff = Images.getDiff(scrs.get(0), scrs.get(1),
                    GDocs.exclusionRectangles);
            return diff.hasDifference();

        } catch (IOException | ImageDimensionException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static byte[] readFile(File f) {
        try {
            return FileUtils.readFileToByteArray(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static class SetMap extends HashMap<String, Set<File>> {
        public void put(String suffix, File file) {
            if (!containsKey(suffix)) {
                put(suffix, Sets.newHashSet(file));
            } else {
                get(suffix).add(file);
            }
        }
    }
}

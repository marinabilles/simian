package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.DiffWrapper;
import de.crispda.sola.multitester.ExplorationState;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.MapMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadDiffMaps {
    public static void main(String[] args) throws Exception {

        List<Pair<String, Integer>> experiments = new ArrayList<>();
        experiments.add(new ImmutablePair<>("GDocs AS5", 3));
        experiments.add(new ImmutablePair<>("Firepad AS5", 3));
        experiments.add(new ImmutablePair<>("Owncloud AS5", 3));
        for (int i = 1; i < 4; i++) {
            experiments.add(new ImmutablePair<>("GDocs AS10", i));
            experiments.add(new ImmutablePair<>("Firepad AS10", i));
            experiments.add(new ImmutablePair<>("Owncloud AS10", i));
        }

        String cachedFolder = Paths.get("execution") + "../cached/";

        for (Pair<String, Integer> experiment : experiments) {
            String withDashes = experiment.getLeft().replace(" ", "-");
            String diffMapName = cachedFolder + "diffmap-" + withDashes + "-" +
                    Integer.toString(experiment.getRight()) + ".zip";
            MapMap<ExplorationState, Interaction, DiffWrapper> diffMap =
                    SerializationUtils.deserialize(
                            ZipUtils.unzip(FileUtils.readFileToByteArray(new File(diffMapName))));
            HashMap<ExplorationState, byte[]> stateScreenshotMap =
                    SerializationUtils.deserialize(
                            ZipUtils.unzip(FileUtils.readFileToByteArray(new File(diffMapName + ".scr"))));
            System.out.println(String.format("%s - depth: %d", experiment.getLeft(), experiment.getRight()));
            System.out.println(diffMap.size());
            System.out.println(stateScreenshotMap.size());
        }
    }
}

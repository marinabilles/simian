package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.runner.ExperimentSpec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SerializeSpecs {
    public static void main(String[] args) throws IOException {
        List<ExperimentSpec> specs = ExperimentSpec.getExperimentSpecs()
                .stream()
                .filter(spec -> Optional.ofNullable(spec.getName()).map(n -> !n.trim().equals("")).orElse(false))
                .collect(Collectors.toList());

        for (ExperimentSpec spec : specs) {
            String name = spec.getName().replaceAll("[^A-Za-z0-9\\-_]", "-");
            FileUtils.writeByteArrayToFile(new File(name + ".spec"), SerializationUtils.serialize(spec));
        }
    }
}

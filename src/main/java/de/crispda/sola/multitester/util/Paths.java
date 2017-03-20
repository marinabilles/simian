package de.crispda.sola.multitester.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class Paths {
    private final Properties prop;

    private Paths() throws IOException {
        prop = new Properties();
        BufferedReader propfile = Resources.asCharSource(Resources.getResource("paths.properties"), Charsets.UTF_8)
                .openBufferedStream();
        prop.load(propfile);
        propfile.close();
    }

    private String getProperty(String property) {
        return prop.getProperty(property);
    }

    private static Paths _instance;
    private static Paths instance() {
        if (_instance == null) {
            try {
                _instance = new Paths();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return _instance;
    }

    public static String get(String str) {
        return Optional.ofNullable(instance().getProperty(str))
                .orElseThrow(() -> new PropertyNotFoundException(str));
    }

    public static boolean exists(String path) {
        if (path == null)
            return false;
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            if (!pathFile.mkdir()) {
                return false;
            }
        }

        return true;
    }
}

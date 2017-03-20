package de.crispda.sola.multitester.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtils {
    public static byte[] zip(byte[] unzipped) throws IOException {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        compressor.setInput(unzipped);
        compressor.finish();
        ByteArrayOutputStream out = new ByteArrayOutputStream(unzipped.length);
        byte[] buffer = new byte[8192];
        while (!compressor.finished()) {
            int count = compressor.deflate(buffer);
            out.write(buffer, 0, count);
        }
        out.close();
        return out.toByteArray();
    }

    public static byte[] unzip(byte[] zipped) throws IOException, DataFormatException {
        Inflater decompressor = new Inflater();
        decompressor.setInput(zipped);
        ByteArrayOutputStream out = new ByteArrayOutputStream(zipped.length);
        byte[] buffer = new byte[8192];
        while (!decompressor.finished()) {
            int count = decompressor.inflate(buffer);
            out.write(buffer, 0, count);
        }
        out.close();
        return out.toByteArray();
    }
}

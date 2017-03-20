package de.crispda.sola.multitester;

import de.crispda.sola.multitester.util.ZipUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.zip.DataFormatException;

public class DiffWrapper implements Serializable {
    private ImageDiff diff = null;
    private byte[] zipped = null;
    private static final long serialVersionUID = 7207800191881127405L;

    public DiffWrapper(ImageDiff diff, boolean useZip) {
        this.diff = diff;
        if (useZip)
            zip();
    }

    public void zip() {
        if (diff == null)
            return;
        try {
            zipped = ZipUtils.zip(SerializationUtils.serialize(diff));
            diff = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unzip() {
        if (zipped == null)
            return;
        try {
            diff = SerializationUtils.deserialize(ZipUtils.unzip(zipped));
            zipped = null;
        } catch (IOException | DataFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isZipped() {
        return diff == null;
    }

    public ImageDiff get() {
        return Optional.ofNullable(diff).orElseThrow(() ->
                new UnsupportedOperationException("Unable to access diff in zipped state."));
    }
}

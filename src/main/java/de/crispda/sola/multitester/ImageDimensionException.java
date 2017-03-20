package de.crispda.sola.multitester;

import org.openqa.selenium.Dimension;

public class ImageDimensionException extends Exception {
    ImageDimensionException(Dimension firstDimension, Dimension secondDimension) {
        super(String.format("Incompatible image dimensions! first: %dx%d; second: %dx%d",
                firstDimension.width, firstDimension.height, secondDimension.width, secondDimension.height));
    }
}

package de.crispda.sola.multitester.util;

import org.openqa.selenium.Keys;

public class StringConverter {
    public static String convert(CharSequence[] chars) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (CharSequence charseq : chars) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            if (charseq instanceof Keys) {
                sb.append("Keys.").append(((Keys) charseq).name());
            } else {
                sb.append(charseq);
            }
        }
        sb.append("}");
        return sb.toString();
    }
}

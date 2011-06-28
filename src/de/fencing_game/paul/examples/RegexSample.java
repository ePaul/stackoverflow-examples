package de.fencing_game.paul.examples;



/**
 *
 */
import java.util.regex.*;

public class RegexSample {

    static String[] regexps = {
        "[^\\pL\\pM]", "[\\PL\\PM]",
        ".", "\\pL", "\\pM",
        "\\PL", "\\PM"
    };

    static String[] strings = {
        "x", "A", "3", "\n", ".", "\t", "\r", "\f",
        " ", "-", "!", "»", "›", "‹", "«",
        "ͳ", "Θ", "Σ", "Ϫ", "Ж", "ؤ",
        "༬", "༺", "༼", "ང", "⃓", "✄",
        "⟪", "や", "゙", 
        "+", "→", "∑", "∢", "※", "⁉", "⧓", "⧻",
        "⑪", "⒄", "⒰", "ⓛ", "⓶",
        "\u0300" /* COMBINING GRAVE ACCENT, Mn */,
        "\u0BCD" /* TAMIL SIGN VIRAMA, Me */,
        "\u20DD" /* COMBINING ENCLOSING CIRCLE, Me */,
        "\u2166" /* ROMAN NUMERAL SEVEN, Nl */,
    };


    public static void main(String[] params) {
        Pattern[] patterns = new Pattern[regexps.length];

        System.out.print("       ");
        for(int i = 0; i < regexps.length; i++) {
            patterns[i] = Pattern.compile(regexps[i]);
            System.out.print("| " + patterns[i] + " ");
        }
        System.out.println();
        System.out.print("-------");
        for(int i = 0; i < regexps.length; i++) {
            System.out.print("|-" +
                             "--------------".substring(0,
                                                        regexps[i].length()) +
                             "-");
        }
        System.out.println();

        for(int j = 0; j < strings.length; j++) {
            System.out.printf("U+%04x ", (int)strings[j].charAt(0));
            for(int i = 0; i < regexps.length; i++) {
                boolean match = patterns[i].matcher(strings[j]).matches();
                System.out.print("| " + (match ? "✔" : "-")  +
                                 "         ".substring(0, regexps[i].length()));
            }
            System.out.println();
        }
    }



}
package readability;

import java.io.FileInputStream;
import java.util.Scanner;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

class TextStatistics {

    int sentences = 0;
    int words = 0;
    int chars = 0;
    int syllables = 0;
    int polysyllables = 0;
    static final String vowels = "aeiouyAEIOUY";

    TextStatistics(FileInputStream inputStream) {
        Scanner sc = new Scanner(inputStream);
        sc.useDelimiter("(?<=[.!?])\\s");

        while (sc.hasNext()) {
            String sentence = sc.next();
            String[] wordsArray = sentence.replaceAll("[!.?,]", "").split("\\s+");
            sentences++;
            words += wordsArray.length;
            chars += sentence.replaceAll("\\s+", "").toCharArray().length;
            for (String word : wordsArray) {
                syllables += syllablesCounter(word);
            }
        }
        sc.close();
    }

    private int syllablesCounter(String word) {
        int n = 0;

        for (int i = 0; i < word.length(); i++) {
            if (vowels.indexOf(word.charAt(i)) >= 0
                    && i > 0
                    && vowels.indexOf(word.charAt(i - 1)) >= 0) {
                continue;
            }
            if (vowels.indexOf(word.charAt(i)) >= 0) {
                n++;
            }
        }
        if (word.endsWith("e")) {
            n--;
        }
        if (n > 2) {
            polysyllables++;
        }
        return n == 0 ? 1 : n;
    }

    void printStatistics() {
        System.out.printf(String.join( "%n",
                "Words: %d",
                "Sentences: %d",
                "Characters: %d",
                "Syllables: %d",
                "Polysyllables: %d%n"),
                words, sentences, chars, syllables, polysyllables);
    }
}

enum ReadabilityIndex {

    ARI ("Automated Readability Index",
            txt -> 4.71 * txt.chars / txt.words + 0.5 * txt.words / txt.sentences - 21.43),
    FK ("Flesch–Kincaid readability tests",
             txt -> 0.39 * txt.words / txt.sentences + 11.8 * txt.syllables / txt.words - 15.59),
    SMOG("Simple Measure of Gobbledygook",
            txt -> 1.043 * Math.sqrt(txt.polysyllables * 30.0 / txt.sentences) + 3.1291
            ),
    CL("Coleman–Liau index",
            txt -> {
        double l = (double) txt.chars / txt.words * 100;
        double s = (double) txt.sentences / txt.words * 100;
        return 0.0588 * l - 0.296 * s - 15.8;
            });
    final String name;
    final ToDoubleFunction<TextStatistics> equation;

    final int[] ages = new int[] {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 22};

    ReadabilityIndex(String name, ToDoubleFunction<TextStatistics> equation) {
        this.name = name;
        this.equation = equation;
    }

    String getAsString(TextStatistics txt) {
        double score = getScore(txt);
        return String.format("\n%s: %.2f (about %d-year-olds).", name, score, getAge(score));
    }

    double getScore(TextStatistics txt) {
        return equation.applyAsDouble(txt);
    }

    int getAge(double score) {
        int i = (int) Math.ceil(score);
        return ages[Math.min(13, Math.max(i, 0))];
    }
}

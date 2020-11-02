package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private final static String[] ages = new String[]{"5", "6", "7", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "24+"};
    private final static String[] options = new String[]{"ARI", "FK", "SMOG", "CL"};
    private static String[] words;
    private static int sentences;
    private static int characters;
    private static int syllables;
    private static int polysyllables;

    private static String realFileAsString(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static boolean isVowel(char c) {
        return (c + "").matches("(?i)[aieuoy]");
    }

    private static int count(String word) {
        int count = 0;
        boolean isPreVowel = false;
        for (int i = 0; i < word.length(); i++) {
            if (isVowel(word.charAt(i)) && !isPreVowel) {
                count++;
                isPreVowel = true;
            } else if (!isVowel(word.charAt(i))) isPreVowel = false;
        }
        if (word.charAt(word.length() - 1) == 'e') count--;
        return Math.max(count, 1);
    }

    private static int countSyllables(String[] words) {
        int totalCount = 0;
        for (String word : words) totalCount += count(word.toLowerCase());
        return totalCount;
    }

    private static int countPolysyllables(String[] words) {

        int count = 0;
        for (String word : words) {
            if (count(word) > 2) count++;
        }
        return count;
    }

    private static void analyze(String filename) {
        String text = realFileAsString(filename);
        words = text.trim().split("[\\s!?.,]*\\s+");
        sentences = text.trim().split("[?!.]").length;
        characters = text.replaceAll("[\\s]+", "").length();
        syllables = countSyllables(words);
        polysyllables = countPolysyllables(words);

        System.out.println("The text is:\n" + text + "\n");

        System.out.println("Words: " + words.length);
        System.out.println("Sentences: " + sentences);
        System.out.println("Characters: " + characters);
        System.out.println("Syllables: " + syllables);
        System.out.println("Polysyllables: " + polysyllables);

        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String option = scanner.nextLine().toUpperCase();
        System.out.println();

        if ("ALL".equals(option)) {
            for (String op : options) getScore(op);
        } else if (contains(option)) {
            getScore(option);
        } else {
            System.out.println("There is no such index: " + option);
        }
    }

    private static boolean contains(String option) {
        for (String op : options) if (option.equals(op)) return true;
        return false;
    }

    private static double getARI() {
        return 4.71 * ((double) characters / words.length) + 0.5 * ((double) words.length / sentences) - 21.43;
    }

    private static double getFK() {
        return 0.39 * ((double) words.length / sentences) + 11.8 * ((double) syllables / words.length) - 15.59;
    }

    private static double getSMOG() {
        return 1.043 * Math.sqrt(polysyllables * ((double) 30 / sentences)) + 3.1291;
    }

    private static double getCL() {
        int s = 100 * sentences / words.length;
        int l = 100 * characters / words.length;
        return 0.0588 * l - 0.296 * s - 15.8;
    }

    private static void getScore(String index) {
        String text = "";
        double score = 0;
        if ("ARI".equals(index)) {
            score = getARI();
            text = "Automated Readability Index";
        } else if ("FK".equals(index)) {
            score = getFK();
            text = "Flesch-Kincaid readability tests";
        } else if ("SMOG".equals(index)) {
            score = getSMOG();
            text = "Simple Measure of Gobbledygook";
        } else if ("CL".equals(index)) {
            score = getCL();
            text = "Coleman-Liau index";
        }
        System.out.printf("%s: %.2f", text, score);
        printAge((int) Math.ceil(score));
    }

    private static void printAge(int score) {
        int age = (int) Math.ceil(score);
        System.out.printf(" (about %s year olds).\n", age > 14 ? "24+" : ages[age - 1]);
    }

    public static void main(String[] args) {
        analyze(args[0]);
    }
}


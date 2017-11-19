import org.datavec.api.util.ClassPathResource;

import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {



    public static void main(String[] args) throws IOException {

        // Load file.
        List<String> lines = loadFile(new ClassPathResource("bilbes.txt").getFile());

        // Create cases.
        List<List<String>> cases = new ArrayList<>();
        for (int i = 0; i < lines.size(); i = i+2) {
            cases.add(Arrays.asList(lines.get(i), lines.get(i+1)));
        }

        // Create features.
        List<List<String>> features = new ArrayList<>();

        for (List<String> aCase : cases) {
            features.addAll(createFeatures(aCase.get(0), aCase.get(1)));
        }

        int count = 0;
        for (List<String> feature : features) {
            //System.out.println(feature);

            String input = feature.get(0);
            String output = feature.get(1);

            List<Integer> inputVector0 = charToVector(input.charAt(0));
            List<Integer> inputVector1 = charToVector(input.charAt(1));
            List<Integer> inputVector2 = charToVector(input.charAt(2));
            inputVector0.addAll(inputVector1);
            inputVector0.addAll(inputVector2);
            //System.out.println(inputVector0);

            List<Integer> outputVector0 = charToVector(output.charAt(0));
            //System.out.println(outputVector0);

            inputVector0.addAll(outputVector0);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < inputVector0.size(); i++) {
                sb.append(inputVector0.get(i));
                sb.append(",");
            }
            String s = sb.toString();
            System.out.println(s.substring(0, s.length() - 1));
            count++;
        }
        System.out.println(count);
    }

    private static List<String> loadFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        }

        return lines;
    }

    private static List<List<String>> createFeatures(String s1, String s2) {
        List<List<String>> results = new ArrayList<>();

        if (s1.length() != s2.length()) {
            System.out.println(String.format("Error #1: [%s] [%s]", s1, s2));
            return results;
        }

        // Text cleanup.
        s1 = Normalizer.normalize(s1.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s2 = Normalizer.normalize(s2.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");

        List<String> s1Tokens = Arrays.asList(s1.split("\\s"));
        List<String> s2Tokens = Arrays.asList(s2.split("\\s"));

        if (s1Tokens.size() != s2Tokens.size()) {
            System.out.println(String.format("Error #2: [%s] [%s]", s1, s2));
            return results;
        }

        for (int i = 0; i < s1Tokens.size(); i++) {
            List<String> stream1 = word2stream(s1Tokens.get(i));
            List<String> stream2 = word2stream(s2Tokens.get(i));

            if (stream1.size() != stream2.size()) {
                System.out.println(String.format("Error #3: [%s] [%s]", s1, s2));
                return results;
            }

            for (int j = 0; j < stream1.size(); j++) {
                String w1 = stream1.get(j);
                String w2 = stream2.get(j);

                results.add(Arrays.asList(w1, w2.charAt(1) + ""));
            }
        }

        return results;
    }

    public static List<List<Integer>> phrase2vector(String s1) {
        List<String> results = new ArrayList<>();

        // Text cleanup.
        s1 = Normalizer.normalize(s1.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");

        List<String> s1Tokens = Arrays.asList(s1.split("\\s"));

        for (int i = 0; i < s1Tokens.size(); i++) {
            results.addAll(word2stream(s1Tokens.get(i)));
        }

        List<List<Integer>> vectors = new ArrayList<>();

        for (String result : results) {
            List<Integer> inputVector0 = charToVector(result.charAt(0));
            List<Integer> inputVector1 = charToVector(result.charAt(1));
            List<Integer> inputVector2 = charToVector(result.charAt(2));
            inputVector0.addAll(inputVector1);
            inputVector0.addAll(inputVector2);

            //System.out.println(result);
            //System.out.println(inputVector0);

            vectors.add(inputVector0);
        }

        return vectors;
    }

    public static List<String> word2stream(String word) {
        List<String> stream = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            String previousChar = i - 1 >= 0 ? word.charAt(i - 1) + "" : "_";
            String currentChar = word.charAt(i) + "";
            String nextChar = i + 1 < word.length() ? word.charAt(i + 1) + "" : "_";

            stream.add(previousChar + currentChar + nextChar);
        }

        return stream;
    }

    public static List<Integer> charToVector(char c) {
        int value = ((int) c) - 97;
        if (value < 0) {
            value = 26;
        }
        List<Integer> vector = new ArrayList<>(Collections.nCopies(27, 0));
        vector.set(value, 1);
        return vector;
    }

    public static char vectorToChar(List<Integer> vector) {
        int pos = vector.indexOf(1);
        if (pos == 26)
            pos = -2; // Space
        return (char) (pos + 97);
    }

    public static char labelToChar(int label) {
        if (label == 26)
            label = -2; // Space
        return (char) (label + 97);

    }
}
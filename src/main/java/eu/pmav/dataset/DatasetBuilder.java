package eu.pmav.dataset;

import eu.pmav.network.ModelBuilder;
import org.datavec.api.util.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DatasetBuilder {

    private static Logger log = LoggerFactory.getLogger(ModelBuilder.class);

    private DatasetBuilder(String filePath) {
    }

    public static DataSet build(String filePath) throws Exception { // Add custom exception.
        // Load file.
        List<String> lines = loadFile(new ClassPathResource(filePath).getFile());

        // Create cases.
        List<List<String>> cases = new ArrayList<>();
        for (int i = 0; i < lines.size(); i = i + 2) {
            cases.add(Arrays.asList(lines.get(i), lines.get(i + 1)));
        }

        // Create features.
        List<List<String>> features = new ArrayList<>();
        for (List<String> aCase : cases) {
            features.addAll(createFeatures(aCase.get(0), aCase.get(1)));
        }

        INDArray featuresArray = Nd4j.create(features.size(), 27*3);
        INDArray labelsArray = Nd4j.create(features.size(), 27);

        int row = 0;
        for (List<String> feature : features) {
            String input = feature.get(0);
            String output = feature.get(1);

            List<Integer> inputVector = new ArrayList<>();
            inputVector.addAll(charToVector(input.charAt(0)));
            inputVector.addAll(charToVector(input.charAt(1)));
            inputVector.addAll(charToVector(input.charAt(2)));

            List<Integer> outputVector = new ArrayList<>();
            outputVector.addAll(charToVector(output.charAt(0)));

            featuresArray.putRow(row, Nd4j.create(inputVector.stream().mapToDouble(i -> i).toArray()));
            labelsArray.putRow(row, Nd4j.create(outputVector.stream().mapToDouble(i -> i).toArray()));
            row++;
        }

        log.info("Dataset size: " + features.size());

        return new DataSet(featuresArray, labelsArray);
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

    private static List<List<String>> createFeatures(String s1, String s2) throws Exception {
        List<List<String>> results = new ArrayList<>();

        if (s1.length() != s2.length()) {
            throw new Exception(String.format("Error #1: [%s] [%s]", s1, s2)); // TODO Better message.
        }

        // Text cleanup.
        s1 = Normalizer.normalize(s1.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s2 = Normalizer.normalize(s2.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");

        List<String> s1Tokens = Arrays.asList(s1.split("\\s"));
        List<String> s2Tokens = Arrays.asList(s2.split("\\s"));

        if (s1Tokens.size() != s2Tokens.size()) {
            throw new Exception(String.format("Error #2: [%s] [%s]", s1, s2)); // TODO Better message.
        }

        for (int i = 0; i < s1Tokens.size(); i++) {
            List<String> stream1 = word2stream(s1Tokens.get(i));
            List<String> stream2 = word2stream(s2Tokens.get(i));

            if (stream1.size() != stream2.size()) {
                throw new Exception(String.format("Error #3: [%s] [%s]", s1, s2)); // TODO Better message.
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

    public static char labelToChar(int label) {
        if (label == 26)
            label = -2; // Space
        return (char) (label + 97);

    }
}

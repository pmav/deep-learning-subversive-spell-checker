package eu.pmav.dataset;

import eu.pmav.dataset.exception.DatabaseBuilderException;
import eu.pmav.model.ModelBuilder;
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

    private DatasetBuilder() {
    }

    // --

    public static DataSet build(String filePath) throws DatabaseBuilderException {
        try {
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

            INDArray featuresArray = Nd4j.create(features.size(), 27 * 3);
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

        } catch (IOException ioException) {
            throw new DatabaseBuilderException(ioException);
        }
    }

    public static INDArray token2array(String token) {
        token = cleanText(token);

        List<INDArray> indArrays = new ArrayList<>();
        List<String> stream = token2stream(token);

        for (String s : stream) {
            List<Integer> inputVector0 = charToVector(s.charAt(0));
            List<Integer> inputVector1 = charToVector(s.charAt(1));
            List<Integer> inputVector2 = charToVector(s.charAt(2));
            inputVector0.addAll(inputVector1);
            inputVector0.addAll(inputVector2);

            indArrays.add(Nd4j.create(inputVector0.stream().mapToDouble(i -> i).toArray()));
        }

        return Nd4j.vstack(indArrays);
    }

    public static char labelToChar(int label) {
        if (label == 26)
            label = -2; // Space
        return (char) (label + 97);

    }

    public static List<String> tokenize(String input) {
        return Arrays.asList(input.split("\\s"));
    }

    // --

    private static String cleanText(String input) {
        input = input.toLowerCase();
        input = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        input = input.replaceAll("[^A-z\\s]", "");
        return input;
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

    private static List<List<String>> createFeatures(String s1, String s2) throws DatabaseBuilderException {
        List<List<String>> results = new ArrayList<>();

        // Text cleanup.
        s1 = cleanText(s1);
        s2 = cleanText(s2);

        if (s1.length() != s2.length()) {
            throw new DatabaseBuilderException(String.format("Error #1: [%s] [%s]", s1, s2)); // TODO Better message.
        }

        List<String> s1Tokens = tokenize(s1);
        List<String> s2Tokens = tokenize(s2);

        if (s1Tokens.size() != s2Tokens.size()) {
            throw new DatabaseBuilderException(String.format("Error #2: [%s] [%s]", s1, s2)); // TODO Better message.
        }

        for (int i = 0; i < s1Tokens.size(); i++) {
            List<String> stream1 = token2stream(s1Tokens.get(i));
            List<String> stream2 = token2stream(s2Tokens.get(i));

            if (stream1.size() != stream2.size()) {
                throw new DatabaseBuilderException(String.format("Error #3: [%s] [%s]", s1, s2)); // TODO Better message.
            }

            for (int j = 0; j < stream1.size(); j++) {
                String w1 = stream1.get(j);
                String w2 = stream2.get(j);

                results.add(Arrays.asList(w1, String.format("%s", w2.charAt(1))));
            }
        }

        return results;
    }

    private static List<String> token2stream(String word) {
        List<String> stream = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            String previousChar = i - 1 >= 0 ? word.charAt(i - 1) + "" : "_";
            String currentChar = word.charAt(i) + "";
            String nextChar = i + 1 < word.length() ? word.charAt(i + 1) + "" : "_";

            stream.add(previousChar + currentChar + nextChar);
        }

        return stream;
    }

    private static List<Integer> charToVector(char c) {
        int value = ((int) c) - 97;
        if (value < 0) {
            value = 26;
        }
        List<Integer> vector = new ArrayList<>(Collections.nCopies(27, 0));
        vector.set(value, 1);
        return vector;

//        String s = Integer.toBinaryString(c);
//        s = String.format("%1$8s", s).replace(' ', '0');
//        List<Integer> integerList = Arrays.stream(s.split("")).map(Integer::parseInt).collect(Collectors.toList());
//        return  integerList;
    }

}

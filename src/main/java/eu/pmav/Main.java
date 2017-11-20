package eu.pmav;

import eu.pmav.dataset.DatasetBuilder;
import eu.pmav.network.ModelBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        // Create dataset.
        DataSet dataset = DatasetBuilder.build("training-data-pt/data.txt");

        // Create model.
        MultiLayerNetwork model = ModelBuilder.build(dataset);

        System.out.println("Enter phrase:");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            StringBuilder sb = new StringBuilder();
            for (String s : line.split(" ")) {
                List<List<Integer>> vectors = DatasetBuilder.phrase2vector(s);

                List<INDArray> INDArrays = new ArrayList<>();
                for (List<Integer> vector : vectors) {

                    INDArray indArray = Nd4j.create(vector.stream().mapToDouble(i -> i).toArray());
                    INDArrays.add(indArray);
                }

                int[] predict = model.predict(Nd4j.vstack(INDArrays));


                for (int i = 0; i < predict.length; i++) {
                    sb.append(DatasetBuilder.labelToChar(predict[i]));
                }

                sb.append(" ");
            }

            System.out.println(sb.toString().replaceAll("_", ""));
        }
    }
}
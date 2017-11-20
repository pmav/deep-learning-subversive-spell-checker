package eu.pmav;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import eu.pmav.dataset.DatasetBuilder;
import eu.pmav.network.ModelBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        // Setup logger level.
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

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

            for (String token : DatasetBuilder.tokenize(line)) {
                INDArray indArray = DatasetBuilder.token2array(token);

                int[] predicts = model.predict(indArray);

                for (int predict : predicts) {
                    sb.append(DatasetBuilder.labelToChar(predict));
                }

                sb.append(" ");
            }

            System.out.println(sb.toString().replaceAll("_", ""));
        }
    }
}
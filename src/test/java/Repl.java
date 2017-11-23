import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import eu.pmav.dataset.DatasetBuilder;
import eu.pmav.model.ModelBuilder;
import eu.pmav.predictor.Predictor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Repl {

    public static void main(String[] args) throws Exception {
        // Setup logger level.
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        // Create dataset.
        DataSet dataset = DatasetBuilder.build("training-data-pt/data.txt");

        // Create model.
        MultiLayerNetwork model = ModelBuilder.build(dataset);

        // Create predictor.
        Predictor predictor = new Predictor(model);

        System.out.println("Enter phrase:");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println(predictor.predict(line));
        }
    }
}

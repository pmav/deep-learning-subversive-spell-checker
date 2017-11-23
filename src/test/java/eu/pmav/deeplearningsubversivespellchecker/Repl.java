package eu.pmav.deeplearningsubversivespellchecker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import eu.pmav.Provider;
import eu.pmav.predictor.Predictor;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Repl {

    public static void main(String[] args) throws Exception {
        // Setup logger level.
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        // Create predictor.
        Predictor predictor = Provider.getInstance().getPredictor();

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

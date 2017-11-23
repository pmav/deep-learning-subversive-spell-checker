package eu.pmav;

import eu.pmav.dataset.DatasetBuilder;
import eu.pmav.dataset.exception.DatabaseBuilderException;
import eu.pmav.model.ModelBuilder;
import eu.pmav.predictor.Predictor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;

public class Provider {
    private static Provider ourInstance = new Provider();

    public static Provider getInstance() {
        return ourInstance;
    }

    private static Predictor predictor;

    private Provider() {
        try {
            init();
        } catch (DatabaseBuilderException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void init() throws DatabaseBuilderException {
        // Create dataset.
        DataSet dataset = DatasetBuilder.build("training-data-pt/data.txt");

        // Create model.
        MultiLayerNetwork model = ModelBuilder.build(dataset);

        // Create predictor;
        predictor = new Predictor(model);
    }

    public static Predictor getPredictor() {
        return predictor;
    }
}

package eu.pmav.predictor;

import eu.pmav.dataset.DatasetBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

public class Predictor {

    private MultiLayerNetwork model;

    public Predictor(MultiLayerNetwork model) {
        this.model = model;
    }

    public String predict(String input) {
        StringBuilder sb = new StringBuilder();

        for (String token : DatasetBuilder.tokenize(input)) {
            INDArray indArray = DatasetBuilder.token2array(token);

            int[] predicts = model.predict(indArray);

            for (int predict : predicts) {
                sb.append(DatasetBuilder.labelToChar(predict));
            }

            sb.append(" ");
        }

        return sb.toString().trim().replaceAll("_", "");
    }
}

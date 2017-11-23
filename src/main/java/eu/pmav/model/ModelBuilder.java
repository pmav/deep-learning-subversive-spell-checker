package eu.pmav.model;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ModelBuilder {

    private static Logger log = LoggerFactory.getLogger(ModelBuilder.class);

    private static final int ITERATIONS = 2500;
    private static final long SEED = 6;
    private static final int HIDDEN_LAYER_NODE_COUNT = 90;

    private ModelBuilder() {
    }

    public static MultiLayerNetwork  build(DataSet dataset) {
        dataset.shuffle();
        SplitTestAndTrain testAndTrain = dataset.splitTestAndTrain(0.65);  // Use 65% of data for training.
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        int numInputs = trainingData.getFeatures().size(1);
        int numOutput = trainingData.getLabels().size(1);

        log.info("Build model....");
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .iterations(ITERATIONS)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .learningRate(0.1)
                .regularization(true).l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(HIDDEN_LAYER_NODE_COUNT).build())
                .layer(1, new DenseLayer.Builder().nIn(HIDDEN_LAYER_NODE_COUNT).nOut(HIDDEN_LAYER_NODE_COUNT).build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).activation(Activation.SOFTMAX).nIn(HIDDEN_LAYER_NODE_COUNT).nOut(numOutput).build())
                .backprop(true)
                .pretrain(false)
                .build();


        // Train the model.
        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        model.setListeners(new ScoreIterationListener(100));
        model.fit(trainingData);

        // Evaluate the model on the test set.
        INDArray output = model.output(testData.getFeatureMatrix());
        Evaluation eval = new Evaluation(numOutput);
        eval.eval(testData.getLabels(), output);
        log.info(eval.stats());

        return model;
    }

}
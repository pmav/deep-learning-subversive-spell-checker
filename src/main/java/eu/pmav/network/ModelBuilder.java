package eu.pmav.network;

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

    private ModelBuilder() {
    }

    public static MultiLayerNetwork  build(DataSet dataset) {
        //First: get the dataset using the record reader. CSVRecordReader handles loading/parsing
        //int numLinesToSkip = 0;
        //char delimiter = ',';
        //RecordReader recordReader = new CSVRecordReader(numLinesToSkip,delimiter);
        //recordReader.initialize(new FileSplit(new ClassPathResource("bilbes-raw.txt").getFile()));



        //int labelIndexFrom = 81;
        //int labelIndexTo = 107;



        //DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader,10000,labelIndexFrom,labelIndexTo,true);
        //DataSet allData = iterator.next();
        //DataSet allData = new DataSet();
        dataset.shuffle();
        SplitTestAndTrain testAndTrain = dataset.splitTestAndTrain(0.65);  //Use 65% of data for training

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        final int numInputs = 81;
        int outputNum = 27;
        int iterations = 1000;
        long seed = 6;


        log.info("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .learningRate(0.1)
                .regularization(true).l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(100).build())
                .layer(1, new DenseLayer.Builder().nIn(100).nOut(100).build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).activation(Activation.SOFTMAX).nIn(100).nOut(outputNum).build())
                .backprop(true)
                .pretrain(false)
                .build();


        //run the model
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100));
        model.fit(trainingData);

        //evaluate the model on the test set
        INDArray output = model.output(testData.getFeatureMatrix());

        Evaluation eval = new Evaluation(27);
        eval.eval(testData.getLabels(), output);
        log.info(eval.stats());

        return model;
    }

}
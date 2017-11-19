import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
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
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MLPMnistTwoLayerExample {

    private static Logger log = LoggerFactory.getLogger(MLPMnistTwoLayerExample.class);

    public static void main(String[] args) throws Exception {

        //First: get the dataset using the record reader. CSVRecordReader handles loading/parsing
        int numLinesToSkip = 0;
        char delimiter = ',';
        RecordReader recordReader = new CSVRecordReader(numLinesToSkip,delimiter);
        recordReader.initialize(new FileSplit(new ClassPathResource("bilbes-raw.txt").getFile()));

        int labelIndexFrom = 81;
        int labelIndexTo = 107;

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader,10000,labelIndexFrom,labelIndexTo,true);
        DataSet allData = iterator.next();
        allData.shuffle();
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        final int numInputs = 81;
        int outputNum = 27;
        int iterations = 3000;
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

        System.out.println("Enter phrase:");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            StringBuilder sb = new StringBuilder();
            for (String s : line.split(" ")) {
                List<List<Integer>> vectors = Main.phrase2vector(s);

                List<INDArray> INDArrays = new ArrayList<>();
                for (List<Integer> vector : vectors) {

                    INDArray indArray = Nd4j.create(vector.stream().mapToDouble(i -> i).toArray());
                    INDArrays.add(indArray);
                }

                int[] predict = model.predict(Nd4j.vstack(INDArrays));


                for (int i = 0; i < predict.length; i++) {
                    sb.append(Main.labelToChar(predict[i]));
                }

                sb.append(" ");
            }

            System.out.println(sb.toString().replaceAll("_", ""));



        }


    }

}
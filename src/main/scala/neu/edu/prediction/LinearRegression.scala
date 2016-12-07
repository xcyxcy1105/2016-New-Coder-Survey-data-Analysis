package neu.edu.prediction

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionModel, LinearRegressionWithSGD}

/**
  * Created by xcyxcy1105 on 11/30/16.
  */
object LinearRegression {
  def main(args: Array[String]): Unit = {
    val sourceFile = "2016-FCC-New-Coders-Survey-Data.csv"
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val dataLine = sc.textFile(sourceFile).map(line => line.split(";")).cache()
    def getMax(index: Integer) = {
        dataLine
        .filter(records => records(0) != "NA" && records(10) != "NA" && records(9) != "NA" && records(17) != "NA")
        .map(records => records(index).toDouble)
        .sortBy(-_)
        .take(1)(0)
    }
    val maxIncome = getMax(10)
    val maxAge = getMax(0)
    val maxHoursLearning = getMax(9)
    val maxMonthsProgramming = getMax(17)
    val parsedData = dataLine
      .filter(records => records(0) != "NA" && records(10) != "NA" && records(9) != "NA" && records(17) != "NA")
      .map(records => LabeledPoint(records(10).toDouble , Vectors.dense(records(0).toDouble / maxAge, records(9).toDouble / maxHoursLearning, records(17).toDouble / maxMonthsProgramming)))
      //.map(records => (records(0).toDouble, records(10).toDouble))
    val splits = parsedData.randomSplit(Array(0.7, 0.3))
    val trainingData = splits(0).cache()
    val testData = splits(1).cache()

    //val numTraining = trainingData.count()
    val numTest = testData.count()

    val algorithm = new LinearRegressionWithSGD()
    algorithm.optimizer
      .setNumIterations(80)
      .setStepSize(1.0)
      .setRegParam(0.01)
    val model = algorithm.run(trainingData)

    val prediction = model.predict(testData.map(_.features))
    val predictionAndLabel = prediction.zip(testData.map(_.label))
    predictionAndLabel.foreach((result) => println(s"predicted label: ${result._1}, actual label: ${result._2}"))

    val loss = predictionAndLabel.map { case (p, l) =>
      val err = p - l
      err * err
    }.reduce(_ + _)
    val rmse = math.sqrt(loss / numTest)

    println(s"Test RMSE = $rmse.")

    model.save(sc, "target/tmp/myLinearRegressionModel")
    val sameModel = LinearRegressionModel.load(sc, "target/tmp/myLinearRegressionModel")

    sc.stop()
  }
}

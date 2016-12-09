import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FlatSpec

/**
  * Created by xcyxcy1105 on 12/8/16.
  */


class LinearRegressionSpec extends FlatSpec {
  "Root Mean Squared Error (RMSE)" should "smaller than" in {
    val sourceFile = "2016-FCC-New-Coders-Survey-Data.csv"
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val dataLine = sc.textFile(sourceFile).map(line => line.split(";")).cache()
    def parseString(input: String, p1: String, p2: String): Double = {
      var result: Double = 0.0
      if (input == p1) result = 1.0
      if (input == p2) result = 0.0
      result
    }
    def degreeToString(input: String): Double = {
      var result: Double = 0.0
      if (input == "some high school") result = 0.0
      if (input == "high school diploma or equivalent (GED)") result = 0.167
      if (input == "some college credit, no degree") result = 0.333
      if (input == "associate's degree" || input == "trade, technical, or vocational training") result = 0.5
      if (input == "bachelor's degree") result = 0.667
      if (input == "master's degree (non-professional)" || input == "professional degree (MBA, MD, JD, etc.)") result = 0.833
      if (input == "Ph.D.") result = 1.0
      result
    }
    def getMax(index: Integer) = {
      dataLine
        .filter(records => records(0) != "NA" && records(10) != "NA" && records(9) != "NA" && records(17) != "NA" && records(18) != "NA")
        .map(records => records(index).toDouble)
        .sortBy(-_)
        .take(1)(0)
    }
    val maxAge = getMax(0)
    val maxHoursLearning = getMax(9)
    val maxMonthsProgramming = getMax(17)
    val parsedData = dataLine
      .filter(records => records(0) != "NA" && records(10) != "NA" && records(9) != "NA" && records(17) != "NA" && records(18) != "NA" && records(11) != "NA")
      .map(records => LabeledPoint(records(10).toDouble, Vectors.dense(records(0).toDouble / maxAge, records(9).toDouble / maxHoursLearning, records(17).toDouble / maxMonthsProgramming,
         parseString(records(11), "1", "0"), degreeToString(records(18)))))
    val splits = parsedData.randomSplit(Array(0.8, 0.2))
    val trainingData = splits(0).cache()
    val testData = splits(1).cache()

    val numTest = testData.count()

    val algorithm = new LinearRegressionWithSGD()
    algorithm.optimizer
      .setNumIterations(50)
      .setStepSize(1.0)
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
    assert(rmse < 30000)
  }
}
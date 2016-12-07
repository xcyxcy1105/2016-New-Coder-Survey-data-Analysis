/**
  * Created by xcyxcy1105 on 12/6/16.
  */
package neu.edu.prediction

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.rdd.RDD

object IncomePredict extends App {
  //  val sourceFile = "/Users/xcyxcy1105/Downloads/2016-FCC-New-Coders-Survey-Data_copy.csv"
  //  val dataLine = sc.textFile(sourceFile).map(line => line.split(";")).cache()

  val conf = new SparkConf().setAppName("incomePredict").setMaster("local[4]")
  val sc = new SparkContext(conf)
  val model = LinearRegressionModel.load(sc, "target/tmp/myLinearRegressionModel")

  val testData = "0.33,0.25,0.33 0.24,0.41,0.64 0.78,0.77,0.89 0.62,0.57,0.44"

  def vectorParser(testData: String): RDD[Vector] = {
    val dataArray = testData.split(' ')

    val vectors = dataArray.map { data =>
      val array = data.split(',')
      Vectors.dense(array(0).toDouble, array(1).toDouble, array(2).toDouble)
    }

    sc.parallelize(vectors)
  }

  val testVector = vectorParser(testData)

  val prediction = model.predict(testVector)
  prediction.foreach(println)
  //print(prediction)
}

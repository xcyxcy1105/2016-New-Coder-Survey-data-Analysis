package neu.edu.prediction

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{FlatSpec, Matchers}


/**
  * Created by xcyxcy1105 on 12/8/16.
  */


class LinearRegressionSpec extends FlatSpec with Matchers {
  behavior of "LinearRegression"

  it should "getMax" in {
    val sourceFile = "testData.csv"
    val conf = new SparkConf().setAppName("incomePredict").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val dataLine = sc.textFile(sourceFile).map(line => line.split(",")).cache()
    val max = LinearRegression.getMax(0, dataLine)
    max should matchPattern {
      case 4.0 =>
    }
  }

  it should "degreeToString" in {
    val a = LinearRegression.degreeToString("some high school")
    val b = LinearRegression.degreeToString("high school diploma or equivalent (GED)")
    val c = LinearRegression.degreeToString("some college credit, no degree")
    val d = LinearRegression.degreeToString("associate's degree")
    val e = LinearRegression.degreeToString("bachelor's degree")
    val f = LinearRegression.degreeToString("master's degree (non-professional)")
    val g = LinearRegression.degreeToString("Ph.D.")
    a should matchPattern {
      case 0.0 =>
    }
    b should matchPattern {
      case 0.167 =>
    }
    c should matchPattern {
      case 0.333 =>
    }
    d should matchPattern {
      case 0.5 =>
    }
    e should matchPattern {
      case 0.667 =>
    }
    f should matchPattern {
      case 0.833 =>
    }
    g should matchPattern {
      case 1.0 =>
    }
  }

  it should "parseString" in {
    val a = LinearRegression.parseString("1", "1", "0")
    val b = LinearRegression.parseString("0", "1", "0")
    a should matchPattern {
      case 1.0 =>
    }
    b should matchPattern {
      case 0.0 =>
    }
  }
}
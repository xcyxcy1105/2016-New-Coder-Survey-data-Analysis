import neu.edu.prediction.IncomePredict
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by xcyxcy1105 on 12/9/16.
  */
class IncomePredictSpec extends FlatSpec with Matchers {
  behavior of "IncomePredict"

  it should "IncomePredict" in {
    val vector = IncomePredict.vectorParser("0.33,0.25,0.33,1.0,0.333 0.24,0.41,0.64,0.0,0.5")
    val v1 = Vectors.dense(0.33,0.25,0.33,1.0,0.333)
    val v2 = Vectors.dense(0.24,0.41,0.64,0.0,0.5)
    vector should matchPattern {
      case Array(v1, v2)=>
    }
  }
}

/**
  * Created by xcyxcy1105 on 11/14/16.
  */
package neu.edu.statistic

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object BasicStatistic {
  def main(args: Array[String]) {
    val sourceFile = "2016-FCC-New-Coders-Survey-Data.csv"
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val dataLine = sc.textFile(sourceFile).map(line => line.split(";")).cache()
    val data = dataLine
      .map(record => (record(0), record(1), record(2), record(3), record(4), record(5), record(6), record(7), record(8), record(9), record(10), record(11), record(12), record(13), record(14), record(15), record(16), record(17), record(18), record(19)))
      .cache()
    val uniqueCountries = data.map { case (_, _, countryCitizen, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => countryCitizen }.distinct()

    val countriesByCoders = data
      .map { case (_, _, countryCitizen, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => (countryCitizen, 1) }
      .filter(map => map._1 != "NA")
      .reduceByKey(_ + _)

    val ages = dataLine
      .filter(records => records(0) != "NA" && records(0) != "Age")
      .map(records => records(0).toInt)
    val underTwenty = ages.filter(age => age < 20)
    val twentyToThirty = ages.filter(age => age < 30 && age >= 20)
    val thirtyToForty = ages.filter(age => age < 40 && age >= 30)
    val fortyToFifty = ages.filter(age => age < 50 && age >= 40)
    val greaterThanFifty = ages.filter(age => age >= 50)
    val maleNum = dataLine.filter(records => records(6) == "male")
    val femaleNum = dataLine.filter(records => records(6) == "female")
    val incomes = dataLine
      .filter(records => records(10) != "NA" && records(10) != "Income")
      .map(records => records(10).toInt)
    val income1 = incomes.filter(incomes => incomes < 20000)
    val income2 = incomes.filter(incomes => incomes < 40000 && incomes >= 20000)
    val income3 = incomes.filter(incomes => incomes < 60000 && incomes >= 40000)
    val income4 = incomes.filter(incomes => incomes < 80000 && incomes >= 60000)
    val income5 = incomes.filter(incomes => incomes < 100000 && incomes >= 80000)
    val income6 = incomes.filter(incomes => incomes >= 100000)

    val incomeByCountry = dataLine
      .filter(records => records(2) != "NA" && records(10) != "NA")
      .map(records => (records(2), records(10).toDouble))
      .groupByKey()
      .filter(_._2.size > 10)
      .map(records => (records._1, records._2.sum / records._2.size))
      .filter(_._2 > 50000)
      .sortBy(-_._2)

    val incomeByAges = dataLine
      .filter(records => records(0) != "NA" && records(10) != "NA")
      .map(records => (records(0).toInt, records(10).toDouble))
    val incomeByAge1 = incomeByAges
      .filter(_._1 < 20)
      .map(records => records._2)
    val incomeByAge2 = incomeByAges
      .filter(age => age._1 >= 20 && age._1 < 30)
      .map(records => records._2)
    val incomeByAge3 = incomeByAges
      .filter(age => age._1 >= 30 && age._1 < 40)
      .map(records => records._2)
    val incomeByAge4 = incomeByAges
      .filter(age => age._1 >= 40 && age._1 < 50)
      .map(records => records._2)
    val incomeByAge5 = incomeByAges
      .filter(_._1 >= 50)
      .map(records => records._2)

    println("Total records: " + data.count())
    println("Unique countries: " + uniqueCountries.count())
    countriesByCoders
      .collect()
      .sortBy(-_._2)
      .take(10)
      .foreach(println)
    println("Age distribution:")
    println("< 20: " + underTwenty.count())
    println("20 to 29: " + twentyToThirty.count())
    println("30 to 39: " + thirtyToForty.count())
    println("40 to 49: " + fortyToFifty.count())
    println(">= 50: " + greaterThanFifty.count())
    println("Age average: " + ages.sum() / ages.count())
    println("Gender radio:")
    println("Male number: " + maleNum.count() + " Percentage: " + maleNum.count() * 100 / (maleNum.count() + femaleNum.count()).toDouble + "%")
    println("Female number: " + femaleNum + " Percentage: " + femaleNum.count() * 100 / (maleNum.count() + femaleNum.count()).toDouble + "%")
    println("Income average: " + incomes.sum() / incomes.count())
    println("Income distribution: ")
    println("< 20000: " + income1.count())
    println("20000 to 39999: " + income2.count())
    println("40000 to 59999: " + income3.count())
    println("60000 to 79999: " + income4.count())
    println("80000 to 99999: " + income5.count())
    println(">= 100000: " + income6.count())
    println("Average income greater than 50000 in terms of country: ")
    incomeByCountry.foreach(println)
    println("Average income in terms of age: ")
    println("< 20: " + incomeByAge1.sum() / incomeByAge1.count())
    println("20 to 29: " + incomeByAge2.sum() / incomeByAge2.count())
    println("30 to 39: " + incomeByAge3.sum() / incomeByAge3.count())
    println("40 to 49: " + incomeByAge4.sum() / incomeByAge4.count())
    println(">= 50: " + incomeByAge5.sum() / incomeByAge5.count())
  }
}


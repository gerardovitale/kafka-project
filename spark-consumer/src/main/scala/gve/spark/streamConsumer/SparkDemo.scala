package gve.spark.streamConsumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._

object SparkDemo {

  def exampleStreaming(): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("SparkDemo-kafka")
    val ssc = new StreamingContext(conf, Seconds(2))
    val topics = "wikimedia.recent_changes"
    val brokers = "127.0.0.1:9093"
    val groupId = "consumer-spark-group-id"

    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer])

    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))

    val lines = messages.map(_.value)
    lines.print()
    val words = lines.flatMap(_.split(" "))
    words.print()
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
    wordCounts.print()

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    exampleStreaming()
  }
}

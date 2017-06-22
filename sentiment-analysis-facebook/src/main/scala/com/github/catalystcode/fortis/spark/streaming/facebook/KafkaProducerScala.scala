package com.github.catalystcode.fortis.spark.streaming.facebook

/**
  * Created by stagiaire4 on 16/05/2017.
  */
import java.util.Properties
import org.apache.kafka.clients.producer._

object KafkaProducerScala extends App {

  val props = new Properties()
  props.put("bootstrap.servers", "10.10.1.150:6667")
  props.put("acks","1")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  val topic="mytweet"


  for(i<- 1 to 50) {
    val record = new ProducerRecord(topic, "key"+i, "value"+i)
    producer.send(record)
  }
  producer.close()
}
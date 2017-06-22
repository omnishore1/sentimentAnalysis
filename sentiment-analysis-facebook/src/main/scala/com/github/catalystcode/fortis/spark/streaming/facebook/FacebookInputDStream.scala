package com.github.catalystcode.fortis.spark.streaming.facebook

import java.util.{Date, Properties}
import com.blood.jonathan.sentiment.Sentiment
import com.blood.jonathan.sentiment.model.SentimentResult
import com.github.catalystcode.fortis.spark.streaming.facebook.client.FacebookClient
import com.github.catalystcode.fortis.spark.streaming.facebook.dto.FacebookPost
import com.github.catalystcode.fortis.spark.streaming.{PollingReceiver, PollingSchedule}
import facebook4j.{Comment, PagableList}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.Receiver
import scala.collection.JavaConversions._

private class FacebookReceiver(
                                client: FacebookClient,
                                pollingSchedule: PollingSchedule,
                                storageLevel: StorageLevel,
                                pollingWorkers: Int
                              ) extends PollingReceiver[FacebookPost](pollingSchedule, pollingWorkers, storageLevel) with Logger {

  @volatile private var lastIngestedDate: Option[Date] = None

  var nbComments = 0
  var lastPost = ""
  var lastComment=""
  var numérateur = 0
  var dénominateur = 0
  var CountPos = 0
  var CountNeg = 0
  var Rapport = 0
  override protected def poll(): Unit = {
    client
      .loadNewFacebooks(lastIngestedDate)
      .filter(x => {
        logDebug(s"--${
          // Kafka start
          val props = new Properties()
          props.put("bootstrap.servers", "10.10.1.150:6667")
          props.put("acks","1")
          props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
          props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

          val producer = new KafkaProducer[String, String](props)
          val topic="calc2"
          // Kafka end

          //
          val sentiment: Sentiment = Sentiment.getInstance
          val comments = x.post.getComments
          if(x.post.getMessage != lastPost | comments.size() != nbComments) {
            System.out.println("\t\t-Post: " + x.post.getFrom.getName + ", " + x.post.getMessage + " has :" + comments.size() + " comments")
            for (comment <- comments.drop(nbComments)) {
              val result: SentimentResult = sentiment.analyze(comment.getMessage)
              System.out.println("\t\t-Comment: " + comment.getFrom.getName + "---" + comment.getMessage)
              System.out.println("\t\t-The state is: " + result.getState)
              System.out.println("+.......................................+")
              if (result.getState == "POSITIVE")
                CountPos = CountPos + 1
              else if (result.getState == "NEGATIVE")
                CountNeg = CountNeg + 1

              numérateur = CountPos - CountNeg
              dénominateur = CountNeg + CountPos
              if (numérateur !=0 | dénominateur != 0)
                Rapport = numérateur*100/dénominateur

              println("----Denum----"+dénominateur+"----Num----"+numérateur+"----Neg----"+CountNeg+"----Pos----"+CountPos+"---Rapport---"+Rapport)

              //val record = new ProducerRecord(topic, "", "Number of comments :"+comments.size()+" Post :"+x.post.getFrom.getName+", "+ x.post.getMessage+" Comment :"+comment.getFrom.getName+", "+comment.getMessage+" Sentiment :"+result.getState)
              val record = new ProducerRecord(topic, "", "Facebook" + "|" + comments.size() + "|" + x.post.getFrom.getName + "|" + x.post.getMessage + "|" + comment.getFrom.getName + "|" + comment.getMessage + "|" + result.getState+ "|" +Rapport)
              producer.send(record)
              lastPost = x.post.getMessage
              nbComments= comments.size()
            }
           }
        }")
        isNew(x)
      })
    //.foreach(x => {
    //logInfo(s"Storing facebook ${x.post.getMessage}")
    //  store(x)
    //  markStored(x)
    //})
  }

  private def isNew(item: FacebookPost) = {
    lastIngestedDate.isEmpty || item.post.getCreatedTime.after(lastIngestedDate.get)
  }

  private def markStored(item: FacebookPost): Unit = {
    if (isNew(item)) {
      lastIngestedDate = Some(item.post.getCreatedTime)
      logDebug(s"Updating last ingested date to ${item.post.getCreatedTime}")
    }
  }
}

class FacebookInputDStream(
                            ssc: StreamingContext,
                            client: FacebookClient,
                            pollingSchedule: PollingSchedule,
                            pollingWorkers: Int,
                            storageLevel: StorageLevel
                          ) extends ReceiverInputDStream[FacebookPost](ssc) {

  override def getReceiver(): Receiver[FacebookPost] = {
    logDebug("Creating facebook receiver")
    new FacebookReceiver(client, pollingSchedule, storageLevel, pollingWorkers)
  }
}

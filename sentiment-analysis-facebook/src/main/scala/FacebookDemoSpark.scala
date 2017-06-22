import java.util.Properties

import com.github.catalystcode.fortis.spark.streaming.facebook.{FacebookAuth, FacebookUtils}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

class FacebookDemoSpark(pageId: String, auth: FacebookAuth) {
  def run(): Unit = {
    // set up the spark context and streams
    // val conf = new SparkConf().setAppName("Facebook Spark Streaming Demo Application")
    val conf = new SparkConf().setAppName("FacebookAnalytics").setMaster("local[4]")
                              .set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))

    //FacebookUtils.createPageStream(ssc, auth, pageId).map(x => s"Post: ${x.comments}").print()
    FacebookUtils.createPageStream(ssc, auth, pageId).map(y => s"Post: ${y.post}").print()

    // run forever
    ssc.start()
    ssc.awaitTermination()
  }

}

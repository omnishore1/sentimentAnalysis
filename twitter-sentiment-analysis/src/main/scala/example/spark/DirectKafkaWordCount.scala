/**
  * Created by gene on 1/18/16.
  */
import org.apache.spark._
import org.apache.spark.SparkContext
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import java.util.Properties
import org.apache.kafka.clients.producer._

object DirectKafkaWordCount {

  // Program Inputs
  val consumerKey = "QeekFEv7zPW6ESV2hWx04AN6v"
  val consumerSecret = "icABMl5PyQsMmhkWBKHm4YamyoEEYpydQ6FlHqN9SBP3eMGZOV"
  val accessToken = "490804146-UpLlA4ZBj6CPO5HxD1hjRS4P6XMMQGRUjj1cwZW2"
  val accessTokenSecret = "s7NkIp4CTGpqGA2uhkSWYIDh9oYbiuAlEF7dir4JIrB4O"
  val filters = Array("Omnishore")

  var somme = 0
  var countPos = 0
  var countNeg = 0
  var lfou = 0
  var ltaht = 0
  var test = 0
  var Rapport = 0
  var lastData = ""

  var tweet = ""
  var sentiment = ""

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("SentimentAnalysisDictionary")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(2))

    // Define the Twitter Connection details so twitter4j can connect.
    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    // Create the DStream that finds tweets based on a filter
    val tweetStream = TwitterUtils.createStream(ssc,None,filters)

    // Create the Sentiment Dictionary RDD based on the AFINN Dictionary.
    // Note: The AFINN dictionary rates each word based on a number between -5 and +5.
    // + indicates positive sentiment.  - indicates negative sentiment.
    val source = getClass.getResource("/AFINN-111.txt")
    val sentimentDictionary = sc.textFile(source.toString,1)

    // Convert sentimentDictionary into a group of tuples
    val sentDictPairedRDD = sentimentDictionary.map(line => {
      val splitLine = line.split("\t")
      (splitLine(0), splitLine(1).toLong)
    })
    sentDictPairedRDD.cache()   // Cache the RDD as we will reuse regularly

    // Take each stream of tweets (as measured every 2 seconds).
    // Break them down to individual words and 'measure' based on value of the words
    // against the corresponding dictionary
    tweetStream.foreachRDD( tweetRDD => {

      val origTweets = tweetRDD.collect()   // Save the original tweets so we can reference them later.

      // Begin breaking down each tweet by removing noise such as punctuation and capitalisation
      val cleanedTweets = tweetRDD.map(tweet => tweet.getText.toLowerCase().replaceAll("""[\p{Punct}]""", ""))
      val langTweets = tweetRDD.map(tweet => tweet.getLang.toLowerCase().replaceAll("""[\p{Punct}]""", ""))
      // Give an index value to each tweet based on their position in the RDD.
      // Then break down each tweet into its constituent words with the index associated with it.
      // We create (word,index) tuples from this.
      val cleanedAndIndexedTweets = cleanedTweets.zipWithIndex()
      val tweetsIndexedAsWordPairs = cleanedAndIndexedTweets.flatMap( {case (tweet, index) => {
        val tweetWords = tweet.split(" ")
        tweetWords.map(word => (word, index))
      }})

      // Here we join the individual words with the dictionary.  This allows us to associate a sentiment value
      // based on the words that make up the tweet.  Because we no longer care about the word itself
      // we get rid of the associated word and keep the tweet index and sentiment value for each word in a tweet
      val individualWordSentimentPerTweet = tweetsIndexedAsWordPairs.join(sentDictPairedRDD).values

      // Now add up the value of each index to get a final sentiment value of the tweet.
      val indexTweetSentiment = individualWordSentimentPerTweet.reduceByKey((a, b) => a + b)

      // Create an output tuple associating the original tweet with the final sentiment value.
      // If the sentiment value is positive, then it represents positive sentiment.  Negative represents negative sentiment etc.
      val finalSentiments = indexTweetSentiment.map( {case (index, value) =>
        (origTweets(index.toInt).getText, if(value < 0) "negative"; else if (value > 0) "positive"; else "neutral")
      })

      // Print to screen the results.
      // Kafka producer start
      val  props = new Properties()
      props.put("bootstrap.servers", "10.10.1.150:6667")
      props.put("acks","1")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

      val producer = new KafkaProducer[String, String](props)
      val topic="calc2"
      //
      finalSentiments.foreach( { case (index, value) => println(s"($index, $value)")
        if (s"($value)" == "(positive)"){
          countPos = countPos + 1
        }
        else{
          countNeg = countNeg + 1
        }
        test = countPos
        lfou = countPos - countNeg
        ltaht = countPos + countNeg
        tweet = s"($index)"
        sentiment = s"($value)"
      })
      if ( ltaht != 0 )
        Rapport = (lfou*100)/ltaht
      somme = somme + tweetRDD.count().toInt
      println("---Somme : "+somme+"---Rapport : "+Rapport+"---Tweet : "+tweet+"---LastData : "+lastData)
      if (tweet != lastData) {
        lastData = tweet
        val record = new ProducerRecord(topic, "", "Twitter"+"|"+tweet + "|" + sentiment + "|" + somme + "|" + Rapport)
        producer.send(record)
      }
      producer.close()
      // Kafka producer end
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
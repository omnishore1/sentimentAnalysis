import com.github.catalystcode.fortis.spark.streaming.facebook.FacebookAuth
import org.apache.log4j.{BasicConfigurator, Level, Logger}

object FacebookDemo {
  def main(args: Array[String]) {
    // configure page for which to ingest posts

    val pageId = "OMINSHORE"

    // configure interaction with facebook api

    val FACEBOOK_APP_ID="845853688903779"
    val FACEBOOK_APP_SECRET="2f163039cabd91501a5c7f31b5824961"
    val FACEBOOK_AUTH_TOKEN="EAAMBTKiouGMBACIk1H18TxbPx40wq6HVwzU6vZBU6kZBJ1tc6XLv5iiMsdQOKyvjxtfO2cGb90U5G4i3HZBIdD0QIpAaQGYsjuwxg8GKTPyaqwbR0iYRfVIWL93bx8ice8fCeldkD7cLs2x4xux6zxztaids30ZD"
    val auth = FacebookAuth(accessToken = FACEBOOK_AUTH_TOKEN, appId = FACEBOOK_APP_ID, appSecret = FACEBOOK_APP_SECRET)

    // configure logging
    BasicConfigurator.configure()
    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getLogger("libfacebook").setLevel(Level.DEBUG)

    new FacebookDemoSpark(pageId, auth).run()
  }
}

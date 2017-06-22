package com.github.catalystcode.fortis.spark.streaming.facebook;

/**
 * Created by stagiaire3 on 17/05/2017.
 */
import facebook4j.Comment;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PagableList;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.StreamingContext;

import java.net.URL;

import static org.apache.spark.streaming.Duration.*;

public class PostsFromPageExtractor {

        /**
         * A simple Facebook4J client which
         * illustrates how to access group feeds / posts / comments.
         *
         * @param args
         * @throws FacebookException
         */
        public static void main(String[] args) throws FacebookException {
            /*SparkConf sparkConfiguration = new SparkConf().
                    setAppName("spark-twitter-stream-example").
                    setMaster("local[*]");

            SparkContext sparkContext = new SparkContext(sparkConfiguration);
            StreamingContext streamingContext = new StreamingContext(sparkContext, new Duration(2000));
*/


            // Generate facebook instance.
            Facebook facebook = new FacebookFactory().getInstance();
            // Use default values for oauth app id.
            facebook.setOAuthAppId("1179257195517533", "f870a7b19edb0615a9b3c5cc6c33d1c1");
            // Get an access token from:
            // https://developers.facebook.com/tools/explorer
            // Copy and paste it below.
            String accessTokenString = "EAACEdEose0cBAPQOojeJl2Xi11zxcUv6pyNtXXKrlYgd88HEkiSpiV6ZAbgbgLC3AWZC4RrZCl4rymyba6UZBRZCJCvXvIkmEdZB28KMW2u25vz4uYYDkh0wsngUMfYRqTpfSehoogJZA37ZAeOGRx9mmps9nSnsLHd6ZAXuA5RASW9YnThQd7sgvSeTBzoBpHLUZD";
            AccessToken at = new AccessToken(accessTokenString);
            // Set access token.
            facebook.setOAuthAccessToken(at);

            // We're done.
            // Access group feeds.
            // You can get the group ID from:
            // https://developers.facebook.com/tools/explorer

            // Set limit to 25 feeds.
            ResponseList<Post> feeds = facebook.getFeed("149486508564346",
                    new Reading().limit(100));
            //ResponseList<Comment> comments = facebook.get("369619183435064",
                 //   new Reading().limit(25));

            // For all 25 feeds...
            for (int i = 0; i < feeds.size(); i++) {
                // Get post.
                Post post = feeds.get(i);
                // Get (string) message
                String status = post.getMessage();
                URL photo=post.getPicture();
                URL video=post.getSource();
                String url=post.getType();
                // Print out the message.
                if(url.equals("status"))
                    System.out.println("-Post[Type="+url+"]:"+status);
                else if(url.equals("photo"))
                    System.out.println("-Post[Type="+url+"]:"+photo);
                else
                    System.out.println("-Post[Type="+url+"]:"+video);
                System.out.println("+-----------------------------------+");
                // Get more stuff...
                PagableList<Comment> comments = post.getComments();
                for (Comment comment : comments) {

                    System.out.println("\t\t-User_Comment: "+comment.getFrom().getName());
                    System.out.println("\t\t-Comment: "+comment.getMessage());
                    System.out.println("+.......................................+");


                }
                System.out.println("+-----------------------------------+");
               // String date = post.getCreatedTime().toString();
                //String name = post.getFrom().getName();
                //String id = post.getId();
                //System.out.println(comments);
            }
            //streamingContext.start();

            // Let's await the stream to end - forever
            // streamingContext.awaitTermination();
        }
    }

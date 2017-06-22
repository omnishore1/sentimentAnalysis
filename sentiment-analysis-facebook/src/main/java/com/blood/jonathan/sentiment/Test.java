package com.blood.jonathan.sentiment;

import com.blood.jonathan.sentiment.model.SentimentResult;

/**
 * Created by stagiaire4 on 19/05/2017.
 */
public class Test {
    public static void main (String[] args){
        Sentiment sentiment = Sentiment.getInstance();
        SentimentResult result = sentiment.analyze("i dislike your products");

        System.out.println("The score is: " + result.getScore());
        System.out.println("The state is: " + result.getState());
        System.out.println("The number of detected words are: " + result.getDetectedWords().size());
    }
}

package com.blood.jonathan.sentiment.loader;

import java.util.Map;

/**
 * @author Jonathan Blood
 */
public interface SentimentLoader {

  /**
   * Loads AFINN text and sentiment scores into a {@link Map}
   * @return a map containing text and sentiment scores
   */
  Map<String, Integer> loadData();
}

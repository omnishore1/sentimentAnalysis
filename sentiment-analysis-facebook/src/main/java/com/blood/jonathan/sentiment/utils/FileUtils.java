package com.blood.jonathan.sentiment.utils;

import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * @author Jonathan Blood
 */
public class FileUtils {

  private static final String TEXT_FILE_EXTENSION = ".txt";

  public static boolean isTextFile(Path path) {
    return path != null && path.toString().endsWith(TEXT_FILE_EXTENSION);
  }
}

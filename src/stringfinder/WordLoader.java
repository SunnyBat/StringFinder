package stringfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 *
 * @author SunnyBat
 */
public class WordLoader {

  public static final String[] BASE_DEFS = {"TestWord", "ADSFASDFASDF This is a test!",
    "SecondTest", "ASDFASDFASDF This is another test!",
    "ThirdTest", "ASDFASDFADSF Yet another!"};

  /**
   * Loads the base definitions defined within the program. Mostly for testing purposes.
   */
  public static synchronized void loadBaseDefinitions() {
    for (int i = 0; i < BASE_DEFS.length; i++) {
      WordStorage.addWord(BASE_DEFS[i++], BASE_DEFS[i]);
    }
  }

  /**
   * Loads definitions from a file. If the file does not exist, or is null, this method does nothing.
   *
   * @param f The file to load the definitions from
   */
  public static synchronized void loadDefinitions(File f) {
    if (f == null) {
      System.out.println("File is null.");
      return;
    }
    try {
      Scanner scan = new Scanner(new java.io.InputStreamReader(new java.io.FileInputStream(f), "Cp1252")); // Notepad saves in ANSI, which is Cp1252
      while (scan.hasNextLine()) {
        String word = loadNextLine(scan);
        String def = loadNextLine(scan);
        if (!word.equals("[NONE]") && !def.equals("[NONE]")) {
          WordStorage.addWord(word, def);
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("Error loading file!");
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      System.out.println("Unsupported encoding!");
      e.printStackTrace();
    }
  }

  /**
   * Loads the next line from the given Scanner. Note that this consumes all lines until it finds on of ample length (eg blank lines are consumed and
   * skipped over). This also strips comments. Lines comprised only of commends will be treated as blank lines.
   *
   * @param in The Scanner to read from
   * @return The next line, or [NONE] if the scanner has no more relevant data to read
   */
  private static String loadNextLine(Scanner in) {
    String word = "";
    while (word.length() < 3 && in.hasNextLine()) {
      word = strip(in.nextLine());
    }
    if (word.length() < 3) {
      return "[NONE]";
    }
    return word.trim();
  }

  /**
   * Strips comments from the given String, then trims the whitespace from the outside of it.
   *
   * @param s The String to strip
   * @return The stripped String
   */
  private static String strip(String s) {
    if (s.contains("#")) {
      s = s.substring(0, s.indexOf("#"));
    }
    return s.trim();
  }
}

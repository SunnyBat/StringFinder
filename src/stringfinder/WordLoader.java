package stringfinder;

import java.io.File;
import java.io.FileNotFoundException;
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
      Scanner scan = new Scanner(f);
      while (scan.hasNext()) {
        String word = scan.nextLine();
        while (word.length() < 3) {
          word = scan.nextLine();
        }
        String def = scan.nextLine();
        while (def.length() < 3) {
          def = scan.nextLine();
        }
        WordStorage.addWord(word, def);
      }
    } catch (FileNotFoundException e) {
      System.out.println("Error loading file!");
      e.printStackTrace();
    }
  }
}

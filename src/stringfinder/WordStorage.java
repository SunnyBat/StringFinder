package stringfinder;

import java.io.*;
import java.util.*;

/**
 *
 * @author Sunny
 */
public class WordStorage {

  public static final String[] BASE_DEFS = {"TestWord", "ADSFASDFASDF This is a test!",
    "SecondTest", "ASDFASDFASDF This is another test!",
    "ThirdTest", "ASDFASDFADSF Yet another!"};
  private static final ArrayList<Word> wordList = new ArrayList<>();

  public static synchronized void loadBaseDefinitions() {
    String tempWord = null;
    for (String s : BASE_DEFS) {
      if (tempWord == null) {
        tempWord = s;
      } else {
        addWord(tempWord, s);
        tempWord = null;
      }
    }
  }

  public static synchronized void loadDefinitions(File f) {
    if (f == null) {
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
        addWord(word, def);
      }
    } catch (FileNotFoundException e) {
    }
  }

  public static synchronized void exportDefinitions() {
    try {
      String path = StringFinder.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      PrintStream write = new PrintStream(new File(path.substring(0, path.lastIndexOf(".jar")) + ".defs.txt"));
      for (Word w : wordList) {
        write.println(w.getWord());
        write.println(w.getDefinition());
      }
      write.close();
    } catch (Exception e) {
    }
  }

  public static synchronized String getWord(String def) {
    for (Word w : wordList) {
      if (w.getDefinition().toLowerCase().contains(def.toLowerCase())) {
        return w.getWord();
      }
    }
    return "[NOT FOUND]";
  }

  public static synchronized void addWord(String word, String def) {
    if (getWord(def).equalsIgnoreCase(def)) {
      System.out.println("ERROR: Word already added: " + word + "::" + def);
      return;
    }
    wordList.add(new Word(word, def));
  }

  public static String[][] getWordList() {
    String[][] words = new String[wordList.size()][2];
    for (int a = 0; a < wordList.size(); a++) {
      words[a][0] = wordList.get(a).getWord();
      words[a][1] = wordList.get(a).getDefinition();
    }
    return words;
  }

  private static class Word {

    private final String definition;
    private final String word;

    public Word(String wrd, String def) {
      definition = def;
      word = wrd;
      System.out.println("New word ~~ " + toString());
    }

    public String getWord() {
      return word;
    }

    public String getDefinition() {
      return definition;
    }

    @Override
    public String toString() {
      return word + " :: " + definition;
    }
  }

}

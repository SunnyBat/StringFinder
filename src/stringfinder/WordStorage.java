package stringfinder;

import java.util.*;

/**
 * Stores and loads all words for the program.
 *
 * @author Sunny
 */
public class WordStorage {

  private static final ArrayList<Word> wordList = new ArrayList<>();

  /**
   * Adds a word and definition to the list of words and definitions stored by the program. Note that if word or def is null, this method does
   * nothing.
   *
   * @param word The word to add
   * @param def The definition to add (normally substantially longer than word)
   */
  public static synchronized void addWord(String word, String def) {
    if (word == null || def == null) {
      System.out.println("ERROR: Word or definition cannot be null");
    } else if (!getWord(def).equalsIgnoreCase("[NOT FOUND]")) {
      System.out.println("ERROR: Word already added: " + word + "::" + def);
    } else if (word.equals("[NOT FOUND]")) {
      System.out.println("Error -- Word cannot be [NOT FOUND] -- Skipping");
    } else {
      wordList.add(new Word(word, def));
    }
  }

  public static synchronized void removeWord(String word) {
    if (word == null) {
      System.out.println("ERROR: Word cannot be null to remove it");
    } else {
      for (Word w : wordList) {
        if (w.getWord().equals(word)) {
          wordList.remove(w);
          break;
        }
      }
    }
  }

  /**
   * Gets the word associated with the given definition.
   *
   * @param def The definition to search for
   * @return The word, or [NOT FOUND] if not found.
   */
  public static synchronized String getWord(String def) {
    String returnWord = "[NOT FOUND]";
    for (Word w : wordList) {
      if (w.getDefinition().toLowerCase().contains(def.toLowerCase())) {
        if (!returnWord.equals("[NOT FOUND]")) {
          System.out.println("CONFLICT FOUND -- OVERWRITING " + returnWord + " WITH " + w.getWord());
        }
        returnWord = w.getWord();
      }
    }
    return "[NOT FOUND]";
  }

  /**
   * Checks whether or not the given String is a word in the word database.
   *
   * @param word The word to check
   * @return True if it is, false if not
   */
  public static synchronized boolean isWord(String word) {
    for (Word w : wordList) {
      if (w.getWord().equalsIgnoreCase(word)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a new array of String arrays containing all the definitions of words. Every String array (wordList[0], for example) is guaranteed to have
   * two non-null Strings, with index 0 being the word and index 1 being the definition. This will return a zero-length array if no words have been
   * added.
   *
   * @return The array of String arrays containing all words and definitions.
   */
  public static String[][] getWordList() {
    String[][] words = new String[wordList.size()][2];
    for (int a = 0; a < wordList.size(); a++) {
      words[a][0] = wordList.get(a).getWord();
      words[a][1] = wordList.get(a).getDefinition();
    }
    return words;
  }

  /**
   * Checks whether or not the program has words added to it.
   *
   * @return True if the program has words, false if not
   */
  public static boolean hasWords() {
    return getWordList().length > 0;
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

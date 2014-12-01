/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringfinder;

import java.io.*;
import java.util.*;
import stringfinder.update.UpdateHandler;

/**
 *
 * @author Sunny
 */
public class StringFinder {

  public static final String VERSION = "1.0.0";
  public static final String[] BASE_DEFS = {"TestWord", "ADSFASDFASDF This is a test!",
    "SecondTest", "ASDFASDFASDF This is another test!",
    "ThirdTest", "ASDFASDFADSF Yet another!"};
  private static final ArrayList<word> wordList = new ArrayList<>();

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    ClipboardManager.init();
    UpdateHandler.init();
    MainWindow myWin = new MainWindow();
    myWin.setVisible(true);
    UpdateHandler.loadVersionNotes();
    if (UpdateHandler.updateAvailable()) {
      UpdateHandler.promptUpdate(args);
    }
    loadBaseDefinitions();
    while (true) {
      String s = ClipboardManager.getStringFromClipboard();
      if (s != null) {
        System.out.println("String found: " + s);
        System.out.println("Word: " + getWord(s));
        if (!getWord(s).equalsIgnoreCase("[NOT FOUND]")) {
          ClipboardManager.setClipboardContents(getWord(s));
        }
      } else {
        try {
          Thread.sleep(200);
        } catch (InterruptedException iE) {
        }
      }
    }
  }

  private static void loadBaseDefinitions() {
    String tempWord = null;
    for (String s : BASE_DEFS) {
      if (tempWord == null) {
        tempWord = s;
      } else {
        wordList.add(new word(tempWord, s));
        tempWord = null;
      }
    }
  }

  public static void loadDefinitions(File f) {
    if (f == null) {
      return;
    }
    try {
      Scanner scan = new Scanner(f);
      while (scan.hasNext()) {
        String word = scan.nextLine();
        String def = scan.nextLine();
        System.out.println(word + " :: " + def);
        wordList.add(new word(word, def));
      }
    } catch (FileNotFoundException e) {
    }
  }

  public static String getWord(String def) {
    for (word w : wordList) {
      if (w.getDefinition().toLowerCase().contains(def.toLowerCase())) {
        return w.word;
      }
    }
    return "[NOT FOUND]";
  }

  private static class word {

    private final String definition;
    private final String word;

    public word(String wrd, String def) {
      definition = def;
      word = wrd;
    }

    public String getDefinition() {
      return definition;
    }
  }

}

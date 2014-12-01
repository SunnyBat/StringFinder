/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringfinder;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 *
 * @author Sunny
 */
public class ClipboardManager {

  private static Toolkit toolkit;
  private static Clipboard clipboard;
  private static int fails = 0;

  public static void init() {
    toolkit = Toolkit.getDefaultToolkit();
    clipboard = toolkit.getSystemClipboard();
  }

  public static String getStringFromClipboard() {
    checkFails();
    try {
      String result = (String) clipboard.getData(DataFlavor.stringFlavor);
      if (result.length() < 15) {
        return null;
      }
      System.out.println("String from Clipboard: " + result);
      resetClipboard();
      fails = 0;
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      fails++;
    }
    return null;
  }

  public static void setClipboardContents(String s) {
    checkFails();
    try {
      StringSelection stringSelection = new StringSelection(s);
      clipboard.setContents(stringSelection, null);
      fails = 0;
    } catch (Exception e) {
      fails++;
    }
  }

  private static void resetClipboard() {
    setClipboardContents("");
  }

  private static void checkFails() {
    if (fails >= 100) {
      System.out.println("ERROR: Too many consecutive fails! Exiting program.");
      System.exit(0);
    }
  }

}

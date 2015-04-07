package stringfinder;

import java.awt.Toolkit;
import java.awt.datatransfer.*;

/**
 *
 * @author Sunny
 */
public class ClipboardManager {

  private static Toolkit toolkit;
  private static Clipboard clipboard;
  private static int fails = 0;

  /**
   * Initializes the ClipboardManager.
   */
  public static void init() {
    toolkit = Toolkit.getDefaultToolkit();
    clipboard = toolkit.getSystemClipboard();
  }

  /**
   * Gets the current String from the clipboard.
   *
   * @return The String in the clipboard, or null if not a String
   */
  public static String getStringFromClipboard() {
    checkFails();
    try {
      String result = (String) clipboard.getData(DataFlavor.stringFlavor);
      if (result.length() < 5 || WordStorage.isWord(result)) {
        return null;
      }
      System.out.println("String from Clipboard: " + result);
      resetClipboard();
      fails = 0;
      return result;
    } catch (java.awt.datatransfer.UnsupportedFlavorException ufe) {
      System.out.println("Unsupported Flavor -- Clipboard does not contain text.");
      resetClipboard();
    } catch (Exception e) {
      e.printStackTrace();
      fails++;
    }
    return null;
  }

  /**
   * Sets the clipboard contents. This overwrites anything currently in the clipboard.
   *
   * @param s The String to set the clipboard contents to
   */
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

  /**
   * Resets the clipboard contents.
   */
  private static void resetClipboard() {
    setClipboardContents("");
  }

  /**
   * Checks the amount of the times in a row the program has failed. If it's failed too many times, it kills the program with error code 1.
   */
  private static void checkFails() {
    if (fails >= 100) {
      System.out.println("ERROR: Too many consecutive fails! Exiting program.");
      System.exit(1);
    }
  }

}

package stringfinder;

import stringfinder.update.UpdateHandler;

/**
 *
 * @author Sunny
 */
public class StringFinder {

  public static final String VERSION = "1.0.5";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    ClipboardManager.init();
    UpdateHandler.init();
    MainWindow myWin = new MainWindow();
    myWin.setVisible(true);
    boolean update = true;
    for (String s : args) {
      switch (s.toLowerCase()) {
        case "-basedefs":
          WordLoader.loadBaseDefinitions();
          if (WordStorage.hasWords()) {
            myWin.enableReplaceButton();
          }
          break;
        case "-noupdate":
          update = false;
          break;
        default:
          System.out.println("Invalid command: " + s);
          System.out.println("====Commands====");
          System.out.println("-basedefs      -- Load base definitions for testing");
          System.out.println("-noupdate      -- Do not load updates");
          break;
      }
    }
    if (update) {
      UpdateHandler.loadVersionNotes();
      if (UpdateHandler.updateAvailable()) {
        UpdateHandler.promptUpdate(args);
      }
    }
    if (args.length >= 1) {
      if (args[0].equals("-basedefs")) {
        WordLoader.loadBaseDefinitions();
        if (WordStorage.hasWords()) {
          myWin.enableReplaceButton();
        }
      }
    }
  }
}

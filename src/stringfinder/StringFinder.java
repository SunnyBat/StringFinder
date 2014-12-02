/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringfinder;

import stringfinder.update.UpdateHandler;

/**
 *
 * @author Sunny
 */
public class StringFinder {

  public static final String VERSION = "1.0.3 R1";

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
    if (args.length >= 1) {
      if (args[0].equals("-basedefs")) {
        WordStorage.loadBaseDefinitions();
      }
    }
  }
}

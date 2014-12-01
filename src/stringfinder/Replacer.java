/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringfinder;

/**
 *
 * @author Sunny
 */
public class Replacer {

  private static final Runnable myRun = new Runnable() {
    @Override
    public synchronized void run() {
      do {
        String s = ClipboardManager.getStringFromClipboard();
        if (s != null) {
          System.out.println("String found: " + s);
          System.out.println("Word: " + WordStorage.getWord(s));
          if (!WordStorage.getWord(s).equalsIgnoreCase("[NOT FOUND]")) {
            ClipboardManager.setClipboardContents(WordStorage.getWord(s));
          }
        } else {
          try {
            Thread.sleep(200);
          } catch (InterruptedException iE) {
            System.out.println("Interrupted!");
            myThread = null;
            return;
          }
        }
      } while (!myThread.isInterrupted());
      myThread = null;
      System.out.println("Finished!");
    }
  };
  private static Thread myThread;

  public static synchronized void startReplacing() {
    myThread = new Thread(myRun);
    myThread.start();
  }

  public static synchronized void stopReplacing() {
    myThread.interrupt();
  }

  public static synchronized boolean isReplacing() {
    return myThread != null;
  }

}

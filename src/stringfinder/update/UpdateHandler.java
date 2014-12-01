package stringfinder.update;

import java.io.*;
import java.net.*;
import stringfinder.StringFinder;

/**
 *
 * @author SunnyBat
 */
public class UpdateHandler {

  private static volatile String versionNotes;
  private static volatile boolean useBetaVersion;
  private static volatile int updateLevel = -1;
  private static long updateSize;
  private static final String UPDATE_LINK = "https://dl.dropboxusercontent.com/u/16152108/StringFinder.jar";
  private static final String BETA_UPDATE_LINK = "https://dl.dropboxusercontent.com/u/16152108/StringFinderBETA.jar";
  private static final String PATCH_NOTES_LINK = "https://dl.dropboxusercontent.com/u/16152108/StringFinderUpdates.txt";
  public static stringfinder.update.Update update;

  public static void init() {
    update = new Update();
  }

  /**
   * Returns the current Version Notes found. This returns all of the notes after the supplied version (useful for things like patch notes when
   * updating). Note that the version must be the same as in the update notes, otherwise this will return
   *
   * @param version The Version (raw String of version number)
   * @return The version notes after the given version, or null if notes have not been retrieved yet
   */
  public static String getVersionNotes(String version) {
    String versNotes = getVersionNotes();
    if (versNotes == null) {
      return null;
    }
    try {
      versNotes = versNotes.substring(0, versNotes.indexOf("~~~" + version + "~~~")).trim();
    } catch (IndexOutOfBoundsException e) {
      System.out.println("ERROR: Unable to find update notes for version " + version);
    }
    return versNotes;
  }

  /**
   * Gets the currently loaded version notes. This returns all of the notes in one String. Note that this returns null if the version notes have not
   * been loaded yet.
   *
   * @return The currently loaded version notes, or null if notes are not loaded yet.
   */
  public static String getVersionNotes() {
    return versionNotes;
  }

  /**
   * Loads the current version notes from online. This retreives all of the version notes possible and stores them in one String, with each line
   * separated by a line break (\n). Note that this method blocks until finished, which depends on the user's internet speed. This also parses tokens
   * from the version notes (and does not add them into the version notes String).
   *
   * @see paxchecker.PAXChecker#loadPatchNotesInBackground()
   * @see #getVersionNotes()
   */
  public static void loadVersionNotes() {
    URLConnection inputConnection;
    InputStream textInputStream;
    BufferedReader myReader = null;
    try {
      URL patchNotesURL = new URL(PATCH_NOTES_LINK);
      inputConnection = patchNotesURL.openConnection();
      textInputStream = inputConnection.getInputStream();
      myReader = new BufferedReader(new InputStreamReader(textInputStream));
      String line;
      String lineSeparator = System.getProperty("line.separator", "\n");
      String allText = "";
      boolean versionFound = false;
      while ((line = myReader.readLine()) != null) {
        line = line.trim();
        if (line.contains("~~~" + StringFinder.VERSION + "~~~")) {
          setUpdateLevel(0);
          versionFound = true;
        }
        if (line.startsWith("TOKEN:")) {
          try {
            String d = line.substring(6);
            if (d.startsWith("UPDATETYPE:")) {
              if (!versionFound) {
                String load = d.substring(11);
                if (load.equals("BETA")) {
                  setUpdateLevel(1);
                } else if (load.equals("UPDATE")) {
                  setUpdateLevel(2);
                } else if (load.equals("MAJORUPDATE")) {
                  setUpdateLevel(3);
                } else if (load.equals("INVALIDUPDATE")) {
                  setUpdateLevel(4);
                } else {
                  System.out.println("Unknown updateType token: " + load);
                }
              }
            } else {
              System.out.println("Unknown token: " + d);
            }
          } catch (NumberFormatException numberFormatException) {
            System.out.println("Unable to set token: " + line);
          }
        } else {
          allText += line + lineSeparator;
        }
      }
      versionNotes = allText.trim();
      if (update != null) {
        update.setYesButtonText(getUpdateLevel());
      }
      System.out.println("Finished loading version notes.");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (myReader != null) {
          myReader.close();
        }
      } catch (IOException e) {
        // nothing to see here
      }
    }
  }

  /**
   * Sets whether or not to use BETA versions of the program. Note that this should be called before {@link #updateAvailable()} is called, otherwise
   * it will be essentially useless.
   *
   * @param use True to use BETA versions, false to not
   */
  public static void setUseBeta(boolean use) {
    System.out.println("Browser Use Beta =  " + use);
    useBetaVersion = use;
  }

  /**
   * Checks whether or not the program should use BETA versions.
   *
   * @return True for use BETA, false for not
   */
  public static boolean getUseBeta() {
    return useBetaVersion;
  }

  /**
   * Returns the size of the update file found online. This will return 0 if the size has not been loaded yet.
   *
   * @return The size of the update file found online, or 0 if the size has not been loaded yet
   */
  public static long getUpdateSize() {
    return updateSize;
  }

  /**
   * Sets the level of the update. Note that this can only increase the level -- attempting to set the update level lower will have no effect.<br>
   * Level 0 = Unknown (should be treated the same as Level 2 in program)<br>
   * Level 1 = BETA<br>
   * Level 2 = Update<br>
   * Level 3 = Major Update
   *
   * @param level The level to set the update to
   */
  public static void setUpdateLevel(int level) {
    if (updateLevel < level) {
      updateLevel = level;
      if (update != null) {
        update.setYesButtonText(updateLevel);
      }
    }
  }

  /**
   * Gets the current level of update available.<br>
   * 0 = Unknown update level<br>
   * 1 = BETA version<br>
   * 2 = Minor version<br>
   * 3 = Major version
   *
   * @return 0-3 depending on the update level available
   */
  public static int getUpdateLevel() {
    return updateLevel;
  }

  public static boolean promptUpdate(String[] args) {
    update.showWindow();
    System.out.println("Update!");
    try {
      update.await();
    } catch (InterruptedException iE) {
      System.out.println("CDL interrupted, continuing...");
    }
    if (update.shouldUpdateProgram()) {
      update.setStatusLabelText("Downloading update...");
      updateProgram();
      startNewProgramInstance(args);
      System.exit(0);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks whether or not an update to the program is available. Note that this compares the file sizes between the current file and the file on the
   * Dropbox server. This means that if ANY modification is made to the JAR file, it's likely to trigger an update. This THEORETICALLY works well.
   * We'll find out whether or not it will actually work in practice.
   *
   * @return True if an update is available, false if not.
   */
  public static boolean updateAvailable() {
    try {
      if (getUpdateLevel() == -1) {
        System.out.println("Unable to load version notes -- Unable to check for updates!");
        return false;
      } else if (getUpdateLevel() == 0) {
        System.out.println("Using most recent version.");
        return false;
      } else if (getUpdateLevel() == 4) {
        System.out.println("Unable to determine update availability -- Version not found in version notes");
        return false;
      }
      URL updateURL;
      if (getUpdateLevel() == 1) {
        return false;
      } else {
        updateURL = new URL(UPDATE_LINK);
      }
      URLConnection conn = updateURL.openConnection();
      updateSize = conn.getContentLengthLong();
      System.out.println("Update size = " + updateSize);
      if (updateSize == -1) {
        System.out.println("Update size listed as -1 -- Program most likely unable to connect!");
        return false;
      }
      return true;
    } catch (Exception e) {
      System.out.println("ERROR updating program: The program was unable to check for new updates.");
    }
    return false;
  }

  /**
   * Downloads the latest JAR file from the Dropbox server. Note that this automatically closes the program once finished. Also note that once this is
   * run, the program WILL eventually close, either through finishing the update or failing to properly update.
   */
  public static void updateProgram() {
    try {
      URL updateURL;
      if (getUpdateLevel() == 1) {
        updateURL = new URL(BETA_UPDATE_LINK);
      } else {
        updateURL = new URL(UPDATE_LINK);
      }
      URLConnection conn = updateURL.openConnection();
      InputStream inputStream = conn.getInputStream();
      long remoteFileSize = conn.getContentLength();
      System.out.println("Update Size(compressed): " + remoteFileSize + " Bytes");
      String path = StringFinder.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      BufferedOutputStream buffOutputStream = new BufferedOutputStream(new FileOutputStream(new File(path.substring(0, path.lastIndexOf(".jar")) + ".temp.jar")));
      byte[] buffer = new byte[32 * 1024];
      int bytesRead;
      int in = 0;
      int prevPercent = 0;
      System.out.println("Downloading update...");
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        in += bytesRead;
        buffOutputStream.write(buffer, 0, bytesRead);
        if (update != null) {
          if ((int) (((in * 100) / remoteFileSize)) != prevPercent) {
            prevPercent = (int) (((in * 100) / remoteFileSize));
            update.updateProgress(prevPercent);
          }
        }
      }
      buffOutputStream.flush();
      buffOutputStream.close();
      inputStream.close();
      if (update != null) {
        update.setStatusLabelText("Finishing up...");
      }
      try { // Code to make a copy of the current JAR file
        File inputFile = new File(path.substring(0, path.lastIndexOf(".jar")) + ".temp.jar");
        InputStream fIn = new BufferedInputStream(new FileInputStream(inputFile));
        File outputFile = new File(path);
        buffOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        buffer = new byte[32 * 1024];
        in = 0;
        while ((bytesRead = fIn.read(buffer)) != -1) {
          in += bytesRead;
          buffOutputStream.write(buffer, 0, bytesRead);
        }
        buffOutputStream.flush();
        buffOutputStream.close();
        fIn.close();
        inputFile.delete();
      } catch (Exception e) {
        System.out.println("Unable to complete update -- unable to copy temp JAR file to current JAR file.");
        e.printStackTrace();
      }
      System.out.println("Download Complete!");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR updating program: The program was unable to successfully download the update. If the problem continues, please manually download the latest version at " + UPDATE_LINK);
    }
  }

  /**
   * Starts a new instance of the program with the given arguments.
   *
   * @param args
   */
  public static void startNewProgramInstance(String... args) {
    try {
      String[] nArgs;
      String path = StringFinder.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      if (args != null && args.length > 0) {
        nArgs = new String[args.length + 3];
        System.arraycopy(args, 0, nArgs, 3, args.length);
      } else {
        nArgs = new String[3];
      }
      File temp = new File(System.getProperty("java.home"));
      temp = new File(temp, "bin");
      File javaExe = new File(temp, "javaw.exe");
      if (!javaExe.exists()) {
        javaExe = new File(temp, "java.exe");
        if (!javaExe.exists()) {
          javaExe = new File(temp, "java");
        }
      }
      nArgs[0] = javaExe.getAbsolutePath();
      nArgs[1] = "-jar";
      nArgs[2] = new File(path).getAbsolutePath(); // path can have leading / on it, getAbsolutePath() removes them
      ProcessBuilder pb = new ProcessBuilder(nArgs);
      pb.start();
    } catch (Exception e) {
      System.out.println("Unable to automatically run update.");
      e.printStackTrace();
    }
  }
}

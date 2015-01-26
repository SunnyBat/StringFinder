package stringfinder;

import java.io.*;

/**
 *
 * @author SunnyBat
 */
public class Exporter {

  private final File myFile;

  /**
   * Creates a new Exporter for exporting file definitions.
   *
   * @param f The File to export to
   * @throws IllegalArgumentException If f is null
   * @throws IOException If the program fails to delete the file or create the file
   */
  public Exporter(File f) throws IOException {
    if (f == null) {
      throw new IllegalArgumentException("File cannot be null");
    }
    if (f.exists()) {
      f.delete();
    }
    f.createNewFile();
    myFile = f;
  }

  /**
   * Exports the given definitions.
   *
   * @param defs The definitions to export
   */
  public void exportDefinitions(String[][] defs) {
    try {
      FileOutputStream myOutput = new FileOutputStream(myFile);
      for (String[] word : defs) {
        for (String s : word) {
          myOutput.write(s.getBytes());
          myOutput.write(System.getProperty("line.separator").getBytes());
        }
        myOutput.write(System.getProperty("line.separator").getBytes());
      }
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    }
  }

}

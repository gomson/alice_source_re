package edu.cmu.cs.stage3.alice.gallery.batch;
import java.io.File;

public abstract class LinkBatch { public LinkBatch() {}
  public void forEachLink(File dir, LinkHandler linkHandler) { File[] dirs = dir.listFiles(new java.io.FileFilter() {
      public boolean accept(File file) {
        return file.isDirectory();
      }
    });
    for (int i = 0; i < dirs.length; i++) {
      forEachLink(dirs[i], linkHandler);
    }
    
    File[] files = dir.listFiles(new java.io.FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".link");
      }
    });
    for (int i = 0; i < files.length; i++) {
      File fileI = files[i];
      try {
        java.io.BufferedReader r = new java.io.BufferedReader(new java.io.FileReader(files[i]));
        String s = r.readLine();
        linkHandler.handleLink(fileI, s);
      } catch (java.io.IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
}

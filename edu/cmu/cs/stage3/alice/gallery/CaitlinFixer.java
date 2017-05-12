package edu.cmu.cs.stage3.alice.gallery;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Vector;

public class CaitlinFixer
{
  public CaitlinFixer() {}
  
  private static edu.cmu.cs.stage3.alice.core.World m_world = null;
  
  private static void clear(ElementArrayProperty eap) { while (eap.size() > 0) {
      Element element = (Element)eap.get(0);
      
      element.HACK_removeFromParentWithoutCheckingForExternalReferences();
    }
    eap.clear();
  }
  
  private static void removeAllMarkersAndLights(Element element) { Vector v = new Vector();
    for (int i = 0; i < element.getChildCount(); i++) {
      Element child = element.getChildAt(i);
      if ((child instanceof edu.cmu.cs.stage3.alice.core.Light)) {
        v.addElement(child);
      } else {
        String allLowercaseName = name.getStringValue().toLowerCase();
        if ((allLowercaseName.indexOf("marker") != -1) || 
          (allLowercaseName.equals("chase")) || 
          (allLowercaseName.equals("whole")) || 
          (allLowercaseName.equals("closeup")) || 
          (allLowercaseName.equals("close up")) || 
          (allLowercaseName.equals("far")) || 
          (allLowercaseName.equals("revolve")))
        {
          v.addElement(child);
        }
      }
    }
    for (int i = 0; i < v.size(); i++) {
      Element elementsToRemoveI = (Element)v.elementAt(i);
      System.err.println("removing: " + elementsToRemoveI);
      elementsToRemoveI.removeFromParent();
    }
  }
  
  private static void removeMethodsFromObject(File srcRoot, File dstRoot, File srcFile) {
    try { File dstFile = new File(dstRoot.getAbsolutePath() + srcFile.getAbsolutePath().substring(srcRoot.getAbsolutePath().length()));
      System.err.println(dstFile);
      dstFile.getParentFile().mkdirs();
      if (!dstFile.exists())
      {

        Element e = Element.load(srcFile, m_world, null);
        e.setParent(m_world);
        if ((e instanceof Sandbox)) {
          Sandbox sandbox = (Sandbox)e;
          clear(variables);
          clear(responses);
          clear(questions);
          clear(behaviors);
          clear(sounds);
        }
        Model[] models = (Model[])e.getDescendants(Model.class);
        for (int i = 0; i < models.length; i++) {
          clear(poses);
        }
        
        removeAllMarkersAndLights(e);
        
        java.util.Dictionary map = new java.util.Hashtable();
        String srcPath = srcFile.getAbsolutePath();
        File thumbnailFile = new File(srcPath.substring(0, srcPath.length() - 3) + "png");
        if (thumbnailFile.exists()) {
          BufferedInputStream bis = new BufferedInputStream(new java.io.FileInputStream(thumbnailFile));
          int n = bis.available();
          byte[] data = new byte[n];
          if (bis.read(data, 0, n) == n) {
            map.put("thumbnail.png", data);
          } else {
            System.err.println("did not read entire thumbnail: " + thumbnailFile);
          }
        }
        e.store(dstFile, null, map);
        e.store(dstFile);
        e.removeFromParent();
        e.release();
        e = null;
      }
    } catch (Throwable t) {
      System.err.println(srcFile);
      t.printStackTrace();
      System.exit(-1);
    }
  }
  
  private static void removeMethodsFromObjectsInDirectory(File srcRoot, File dstRoot, File srcDir) { System.err.println("removeMethodsFromObjectsInDirectory: " + srcDir);
    File[] directories = srcDir.listFiles(new java.io.FileFilter() {
      public boolean accept(File file) {
        return file.isDirectory();
      }
    });
    for (int i = 0; i < directories.length; i++) {
      removeMethodsFromObjectsInDirectory(srcRoot, dstRoot, directories[i]);
    }
    
    File[] files = srcDir.listFiles(new java.io.FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".a2c");
      }
    });
    for (int i = 0; i < files.length; i++) {
      removeMethodsFromObject(srcRoot, dstRoot, files[i]);
    }
  }
}

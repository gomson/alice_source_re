package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.io.File;
import java.io.FileFilter;




























































class TutorialWorldFilter
  implements FileFilter
{
  TutorialWorldFilter() {}
  
  public boolean accept(File file)
  {
    if (file.getName().endsWith(".stl")) {
      return true;
    }
    return false;
  }
}

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;




















public class ExtensionFileFilter
  extends FileFilter
  implements Comparable
{
  private String extension;
  private String description;
  
  public ExtensionFileFilter(String extension, String description)
  {
    this.extension = extension.toUpperCase();
    this.description = description;
  }
  
  public boolean accept(File f) {
    return (f.isDirectory()) || (getExtension(f).equalsIgnoreCase(extension));
  }
  
  public String getDescription() {
    return description;
  }
  
  public String getExtension() {
    return extension;
  }
  
  private String getExtension(File f) {
    String ext = "";
    String fullName = f.getName();
    int i = fullName.lastIndexOf('.');
    
    if ((i > 0) && (i < fullName.length() - 1)) {
      ext = fullName.substring(i + 1).toUpperCase();
    }
    return ext;
  }
  
  public int compareTo(Object o) {
    if ((o instanceof ExtensionFileFilter)) {
      return description.compareTo(((ExtensionFileFilter)o).getDescription());
    }
    return description.compareTo(o.toString());
  }
}

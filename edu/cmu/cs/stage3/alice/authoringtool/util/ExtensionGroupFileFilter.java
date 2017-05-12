package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;



















public class ExtensionGroupFileFilter
  extends FileFilter
  implements Comparable
{
  private ArrayList extensions;
  private String description = "";
  private String baseDescription = "";
  
  public ExtensionGroupFileFilter(String baseDescription) {
    extensions = new ArrayList();
    this.baseDescription = baseDescription;
  }
  
  public ExtensionGroupFileFilter(ArrayList extensions, String baseDescription) {
    this.extensions = extensions;
    this.baseDescription = baseDescription;
  }
  
  public void addExtensionFileFilter(ExtensionFileFilter ext) {
    extensions.add(ext);
  }
  
  private void recalculateDescription() {
    StringBuffer d = new StringBuffer(baseDescription);
    d.append(" (");
    
    Iterator iter = extensions.iterator();
    if (iter.hasNext()) {
      ExtensionFileFilter ext = (ExtensionFileFilter)iter.next();
      d.append(ext.getExtension());
    }
    while (iter.hasNext()) {
      ExtensionFileFilter ext = (ExtensionFileFilter)iter.next();
      d.append(";" + ext.getExtension());
    }
    
    d.append(")");
    
    description = d.toString();
  }
  
  public boolean accept(File f) {
    for (Iterator iter = extensions.iterator(); iter.hasNext();) {
      ExtensionFileFilter ext = (ExtensionFileFilter)iter.next();
      if (ext.accept(f)) {
        return true;
      }
    }
    
    return false;
  }
  
  public String getDescription() {
    recalculateDescription();
    return description;
  }
  
  public int compareTo(Object o)
  {
    if ((o instanceof String)) {
      return description.compareTo((String)o);
    }
    return -1;
  }
}

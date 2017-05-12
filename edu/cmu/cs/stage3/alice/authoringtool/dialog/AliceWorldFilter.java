package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.io.File;













































class AliceWorldFilter
  implements java.io.FileFilter
{
  private javax.swing.filechooser.FileFilter m_filter;
  
  public AliceWorldFilter(javax.swing.filechooser.FileFilter filter)
  {
    m_filter = filter;
  }
  
  public boolean accept(File file) {
    if (m_filter != null) {
      if (file.isDirectory()) {
        return true;
      }
      return m_filter.accept(file);
    }
    return false;
  }
}

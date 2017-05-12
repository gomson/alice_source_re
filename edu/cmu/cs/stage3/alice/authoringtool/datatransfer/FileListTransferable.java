package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;




















public class FileListTransferable
  implements Transferable
{
  protected List fileList;
  protected DataFlavor[] flavors;
  
  public FileListTransferable(List fileList)
  {
    this.fileList = fileList;
    
    flavors = new DataFlavor[2];
    flavors[0] = DataFlavor.javaFileListFlavor;
    flavors[1] = DataFlavor.stringFlavor;
  }
  
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }
  
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    for (int i = 0; i < flavors.length; i++) {
      if (flavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(DataFlavor.javaFileListFlavor))
      return fileList;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      String files = "";
      for (Iterator iter = fileList.iterator(); iter.hasNext();) {
        files = files + ((File)iter.next()).getAbsolutePath() + "; ";
      }
      return files;
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

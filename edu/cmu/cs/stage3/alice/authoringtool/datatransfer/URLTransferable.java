package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;

















public class URLTransferable
  implements Transferable
{
  public static DataFlavor urlFlavor = AuthoringToolResources.getReferenceFlavorForClass(URL.class);
  
  protected URL url;
  protected DataFlavor myFlavor;
  protected DataFlavor[] flavors;
  
  public URLTransferable(URL url)
  {
    this.url = url;
    try
    {
      myFlavor = AuthoringToolResources.getReferenceFlavorForClass(URL.class);
      myFlavor.setHumanPresentableName("urlTransferable(" + url + ")");
    } catch (Exception e) {
      AuthoringTool.showErrorDialog(e.getMessage(), e);
    }
    
    flavors = new DataFlavor[2];
    flavors[0] = myFlavor;
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
    if (flavor.getRepresentationClass().equals(URL.class))
      return url;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return url.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

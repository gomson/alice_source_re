package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

















public class CopyFactoryTransferable
  implements Transferable
{
  public static DataFlavor copyFactoryFlavor = AuthoringToolResources.getReferenceFlavorForClass(CopyFactory.class);
  
  protected CopyFactory copyFactory;
  protected DataFlavor myFlavor;
  protected DataFlavor[] flavors;
  
  public CopyFactoryTransferable(CopyFactory copyFactory)
  {
    this.copyFactory = copyFactory;
    try
    {
      myFlavor = AuthoringToolResources.getReferenceFlavorForClass(CopyFactory.class);
      myFlavor.setHumanPresentableName("copyFactoryTransferable(" + copyFactory.getValueClass().getName() + ")");
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
    if (flavor.getRepresentationClass().equals(CopyFactory.class))
      return copyFactory;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return copyFactory.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

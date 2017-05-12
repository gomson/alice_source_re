package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyItem;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;



















public class ObjectArrayPropertyItemTransferable
  implements Transferable
{
  public static final DataFlavor objectArrayPropertyItemFlavor = new DataFlavor("application/x-java-jvm-local-objectref; class=edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyItem", "objectArrayPropertyItemFlavor");
  protected DataFlavor[] flavors;
  protected ObjectArrayPropertyItem objectArrayPropertyItem;
  
  public ObjectArrayPropertyItemTransferable(ObjectArrayPropertyItem objectArrayPropertyItem)
  {
    this.objectArrayPropertyItem = objectArrayPropertyItem;
    
    flavors = new DataFlavor[2];
    flavors[0] = objectArrayPropertyItemFlavor;
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
    if (flavor.equals(objectArrayPropertyItemFlavor))
      return objectArrayPropertyItem;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return objectArrayPropertyItem.getObjectArrayProperty() + "[" + objectArrayPropertyItem.getIndex() + "]";
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

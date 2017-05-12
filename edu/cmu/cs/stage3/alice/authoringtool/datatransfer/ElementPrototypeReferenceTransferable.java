package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;



















public class ElementPrototypeReferenceTransferable
  implements Transferable
{
  public static final DataFlavor elementPrototypeReferenceFlavor = new DataFlavor("application/x-java-jvm-local-objectref; class=edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype", "elementPrototypeReferenceFlavor");
  protected DataFlavor[] flavors;
  protected ElementPrototype elementPrototype;
  
  public ElementPrototypeReferenceTransferable(ElementPrototype elementPrototype)
  {
    this.elementPrototype = elementPrototype;
    
    flavors = new DataFlavor[2];
    flavors[0] = elementPrototypeReferenceFlavor;
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
    if (flavor.equals(elementPrototypeReferenceFlavor))
      return elementPrototype;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return elementPrototype.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

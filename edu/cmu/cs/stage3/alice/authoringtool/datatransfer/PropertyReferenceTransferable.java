package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Property;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;






















public class PropertyReferenceTransferable
  implements Transferable
{
  public static final DataFlavor propertyReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Property.class);
  protected DataFlavor[] flavors;
  protected Property property;
  
  public PropertyReferenceTransferable(Property property)
  {
    this.property = property;
    
    flavors = new DataFlavor[2];
    flavors[0] = propertyReferenceFlavor;
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
    if (flavor.equals(propertyReferenceFlavor))
      return property;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return property.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

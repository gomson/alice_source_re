package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


















public class TextureMapReferenceTransferable
  extends ElementReferenceTransferable
{
  public static final DataFlavor textureMapReferenceFlavor = new DataFlavor("application/x-java-jvm-local-objectref; class=edu.cmu.cs.stage3.alice.core.TextureMap", "textureMapReferenceFlavor");
  protected TextureMap textureMap;
  
  public TextureMapReferenceTransferable(TextureMap textureMap)
  {
    super(textureMap);
    this.textureMap = textureMap;
    
    flavors = new DataFlavor[3];
    flavors[0] = textureMapReferenceFlavor;
    flavors[1] = ElementReferenceTransferable.elementReferenceFlavor;
    flavors[2] = DataFlavor.stringFlavor;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(textureMapReferenceFlavor))
      return textureMap;
    if (flavor.equals(ElementReferenceTransferable.elementReferenceFlavor))
      return textureMap;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return textureMap.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
  
  public ElementReferenceTransferable createCopy()
  {
    if (element != null) {
      Element copy = element.createCopyNamed(element.name.getStringValue());
      return new TextureMapReferenceTransferable((TextureMap)copy);
    }
    return null;
  }
}

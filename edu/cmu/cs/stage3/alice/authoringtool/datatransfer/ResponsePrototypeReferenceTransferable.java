package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;




















public class ResponsePrototypeReferenceTransferable
  extends ElementPrototypeReferenceTransferable
{
  public static final DataFlavor responsePrototypeReferenceFlavor = new DataFlavor("application/x-java-jvm-local-objectref; class=edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype", "responsePrototypeReferenceFlavor");
  protected ResponsePrototype responsePrototype;
  
  public ResponsePrototypeReferenceTransferable(ResponsePrototype responsePrototype)
  {
    super(responsePrototype);
    this.responsePrototype = responsePrototype;
    
    flavors = new DataFlavor[3];
    flavors[0] = responsePrototypeReferenceFlavor;
    flavors[1] = ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor;
    flavors[2] = DataFlavor.stringFlavor;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(responsePrototypeReferenceFlavor))
      return responsePrototype;
    if (flavor.equals(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor))
      return responsePrototype;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return responsePrototype.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

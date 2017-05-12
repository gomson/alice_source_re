package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;




















public class CallToUserDefinedResponsePrototypeReferenceTransferable
  extends ResponsePrototypeReferenceTransferable
{
  public static final DataFlavor callToUserDefinedResponsePrototypeReferenceFlavor = new DataFlavor("application/x-java-jvm-local-objectref; class=edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype", "callToUserDefinedResponsePrototypeReferenceFlavor");
  protected CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype;
  
  public CallToUserDefinedResponsePrototypeReferenceTransferable(CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype)
  {
    super(callToUserDefinedResponsePrototype);
    this.callToUserDefinedResponsePrototype = callToUserDefinedResponsePrototype;
    
    flavors = new DataFlavor[4];
    flavors[0] = callToUserDefinedResponsePrototypeReferenceFlavor;
    flavors[1] = ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor;
    flavors[2] = ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor;
    flavors[3] = DataFlavor.stringFlavor;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(callToUserDefinedResponsePrototypeReferenceFlavor))
      return callToUserDefinedResponsePrototype;
    if (flavor.equals(ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor))
      return callToUserDefinedResponsePrototype;
    if (flavor.equals(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor))
      return callToUserDefinedResponsePrototype;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return callToUserDefinedResponsePrototype.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

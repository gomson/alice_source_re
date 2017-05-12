package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;




















public class QuestionPrototypeReferenceTransferable
  extends ElementPrototypeReferenceTransferable
{
  public static final DataFlavor questionPrototypeReferenceFlavor = new DataFlavor("application/x-java-jvm-local-objectref; class=edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype", "questionPrototypeReferenceFlavor");
  protected QuestionPrototype questionPrototype;
  
  public QuestionPrototypeReferenceTransferable(QuestionPrototype questionPrototype)
  {
    super(questionPrototype);
    this.questionPrototype = questionPrototype;
    
    flavors = new DataFlavor[3];
    flavors[0] = questionPrototypeReferenceFlavor;
    flavors[1] = ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor;
    flavors[2] = DataFlavor.stringFlavor;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(questionPrototypeReferenceFlavor))
      return questionPrototype;
    if (flavor.equals(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor))
      return questionPrototype;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return questionPrototype.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

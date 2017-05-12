package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;




















public class CallToUserDefinedQuestionPrototypeReferenceTransferable
  extends QuestionPrototypeReferenceTransferable
{
  public static final DataFlavor callToUserDefinedQuestionPrototypeReferenceFlavor = new DataFlavor("application/x-java-jvm-local-objectref; class=edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype", "callToUserDefinedQuestionPrototypeReferenceFlavor");
  protected CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype;
  
  public CallToUserDefinedQuestionPrototypeReferenceTransferable(CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype)
  {
    super(callToUserDefinedQuestionPrototype);
    this.callToUserDefinedQuestionPrototype = callToUserDefinedQuestionPrototype;
    
    flavors = new DataFlavor[4];
    flavors[0] = callToUserDefinedQuestionPrototypeReferenceFlavor;
    flavors[1] = QuestionPrototypeReferenceTransferable.questionPrototypeReferenceFlavor;
    flavors[2] = ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor;
    flavors[3] = DataFlavor.stringFlavor;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(callToUserDefinedQuestionPrototypeReferenceFlavor))
      return callToUserDefinedQuestionPrototype;
    if (flavor.equals(QuestionPrototypeReferenceTransferable.questionPrototypeReferenceFlavor))
      return callToUserDefinedQuestionPrototype;
    if (flavor.equals(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor))
      return callToUserDefinedQuestionPrototype;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return callToUserDefinedQuestionPrototype.toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}

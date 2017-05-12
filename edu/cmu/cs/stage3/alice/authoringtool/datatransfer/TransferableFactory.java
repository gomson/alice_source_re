package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyItem;
import edu.cmu.cs.stage3.alice.authoringtool.util.QuestionPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import java.awt.datatransfer.Transferable;











public class TransferableFactory
{
  public TransferableFactory() {}
  
  public static Transferable createTransferable(Object object)
  {
    if ((object instanceof Element))
      return new ElementReferenceTransferable((Element)object);
    if ((object instanceof CallToUserDefinedResponsePrototype))
      return new CallToUserDefinedResponsePrototypeReferenceTransferable((CallToUserDefinedResponsePrototype)object);
    if ((object instanceof CallToUserDefinedQuestionPrototype))
      return new CallToUserDefinedQuestionPrototypeReferenceTransferable((CallToUserDefinedQuestionPrototype)object);
    if ((object instanceof ResponsePrototype))
      return new ResponsePrototypeReferenceTransferable((ResponsePrototype)object);
    if ((object instanceof QuestionPrototype))
      return new QuestionPrototypeReferenceTransferable((QuestionPrototype)object);
    if ((object instanceof ElementPrototype))
      return new ElementPrototypeReferenceTransferable((ElementPrototype)object);
    if ((object instanceof ObjectArrayPropertyItem))
      return new ObjectArrayPropertyItemTransferable((ObjectArrayPropertyItem)object);
    if ((object instanceof Property))
      return new PropertyReferenceTransferable((Property)object);
    if ((object instanceof CopyFactory)) {
      return new CopyFactoryTransferable((CopyFactory)object);
    }
    AuthoringTool.showErrorDialog("cannot create Transferable for: " + object, null);
    return null;
  }
}

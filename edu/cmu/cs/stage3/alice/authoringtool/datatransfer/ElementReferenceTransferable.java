package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;



public class ElementReferenceTransferable
  implements Transferable
{
  public static final DataFlavor elementReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Element.class);
  public static final DataFlavor expressionReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Expression.class);
  public static final DataFlavor questionReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Question.class);
  public static final DataFlavor responseReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Response.class);
  public static final DataFlavor callToUserDefinedQuestionReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedQuestion.class);
  public static final DataFlavor callToUserDefinedResponseReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedResponse.class);
  public static final DataFlavor behaviorReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Behavior.class);
  public static final DataFlavor textureMapReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(TextureMap.class);
  public static final DataFlavor transformableReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Transformable.class);
  public static final DataFlavor variableReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Variable.class);
  public static final DataFlavor worldReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(World.class);
  protected DataFlavor[] flavors;
  protected Element element;
  
  public ElementReferenceTransferable(Element element)
  {
    this.element = element;
    
    HashSet assignables = new HashSet();
    if (element != null) {
      AuthoringToolResources.findAssignables(element.getClass(), assignables, true);
    } else {
      AuthoringToolResources.findAssignables(Element.class, assignables, true);
    }
    
    flavors = new DataFlavor[assignables.size() + 1];
    int i = 0;
    for (Iterator iter = assignables.iterator(); iter.hasNext();) {
      flavors[(i++)] = AuthoringToolResources.getReferenceFlavorForClass((Class)iter.next());
    }
    
    flavors[(i++)] = DataFlavor.stringFlavor;
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
    if (flavor.equals(DataFlavor.stringFlavor))
      return element.toString();
    if (flavor.getRepresentationClass().isAssignableFrom(element.getClass())) {
      return element;
    }
    throw new UnsupportedFlavorException(flavor);
  }
  
  public ElementReferenceTransferable createCopy()
  {
    if (element != null) {
      Element copy = element.createCopyNamed(element.name.getStringValue());
      return new ElementReferenceTransferable(copy);
    }
    return null;
  }
}

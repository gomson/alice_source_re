package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.util.Criterion;









































































class CodeCopyReferenceGenerator
  extends CopyReferenceGenerator
{
  public CodeCopyReferenceGenerator(Element internalRoot, Class[] classesToShare)
  {
    super(internalRoot, classesToShare);
  }
  
  public Criterion generateReference(Element element) {
    Criterion criterion = super.generateReference(element);
    if ((element instanceof Variable)) {
      Element parent = element.getParent();
      if (!(parent instanceof Sandbox))
      {

        criterion = new VariableCriterion(name.getStringValue(), criterion);
      }
    }
    return criterion;
  }
}

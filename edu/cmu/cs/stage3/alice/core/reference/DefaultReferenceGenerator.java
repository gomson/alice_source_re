package edu.cmu.cs.stage3.alice.core.reference;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.util.Criterion;














public class DefaultReferenceGenerator
  implements ReferenceGenerator
{
  Element m_internalRoot;
  
  public DefaultReferenceGenerator(Element internalRoot)
  {
    m_internalRoot = internalRoot;
  }
  
  protected boolean isExternal(Element element) { return !element.isReferenceInternalTo(m_internalRoot); }
  
  public Criterion generateReference(Element element)
  {
    if (element != null) {
      if (m_internalRoot != null) {
        if (isExternal(element)) {
          return new ExternalReferenceKeyedCriterion(element.getKey(m_internalRoot.getRoot()));
        }
        return new InternalReferenceKeyedCriterion(element.getKey(m_internalRoot));
      }
      
      return new ExternalReferenceKeyedCriterion(element.getKey());
    }
    
    return null;
  }
}

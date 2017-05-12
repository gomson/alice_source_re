package edu.cmu.cs.stage3.alice.core.reference;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceResolver;
import edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException;
import edu.cmu.cs.stage3.alice.core.criterion.ElementKeyedCriterion;
import edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.util.Criterion;














public class DefaultReferenceResolver
  implements ReferenceResolver
{
  private Element m_internalRoot;
  private Element m_externalRoot;
  
  public DefaultReferenceResolver(Element internalRoot, Element externalRoot)
  {
    m_internalRoot = internalRoot;
    m_externalRoot = externalRoot;
  }
  
  public Element getInternalRoot() {
    return m_internalRoot;
  }
  
  public void setInternalRoot(Element internalRoot) { m_internalRoot = internalRoot; }
  
  public Element getExternalRoot()
  {
    return m_externalRoot;
  }
  
  public void setExternalRoot(Element externalRoot) { m_externalRoot = externalRoot; }
  
  public Element resolveReference(Criterion criterion) throws UnresolvableReferenceException
  {
    if ((criterion instanceof ElementKeyedCriterion)) {
      String key = ((ElementKeyedCriterion)criterion).getKey();
      Element resolved = null;
      if ((criterion instanceof InternalReferenceKeyedCriterion)) {
        resolved = m_internalRoot.getDescendantKeyedIgnoreCase(key);
      } else if ((criterion instanceof ExternalReferenceKeyedCriterion)) {
        if (m_externalRoot != null) {
          resolved = m_externalRoot.getDescendantKeyedIgnoreCase(key);
        } else
          throw new UnresolvableReferenceException(criterion, "external root is null");
      } else {
        Element root;
        Element root;
        if (m_externalRoot != null) {
          root = m_externalRoot;
        } else {
          root = m_internalRoot;
        }
        int index = key.indexOf('.');
        String trimmedKey;
        String trimmedKey; if (index == -1) {
          trimmedKey = "";
        } else {
          trimmedKey = key.substring(index + 1);
        }
        resolved = root.getDescendantKeyedIgnoreCase(trimmedKey);
      }
      if (resolved != null) {
        return resolved;
      }
      throw new UnresolvableReferenceException(criterion, "internal root: " + m_internalRoot + " external root: " + m_externalRoot);
    }
    

    throw new UnresolvableReferenceException(criterion, "must be edu.cmu.cs.stage3.alice.core.criterion.ElementKeyedCriterion");
  }
}

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceGenerator;

























class CopyReferenceGenerator
  extends DefaultReferenceGenerator
{
  private Class[] m_classesToShare;
  
  public CopyReferenceGenerator(Element internalRoot, Class[] classesToShare)
  {
    super(internalRoot);
    m_classesToShare = classesToShare;
  }
  
  protected boolean isExternal(Element element) {
    if (element.isAssignableToOneOf(m_classesToShare)) {
      return true;
    }
    return super.isExternal(element);
  }
}

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;


















public class UnresolvablePropertyReferencesException
  extends Exception
{
  private PropertyReference[] m_propertyReferences;
  private Element m_element;
  
  public UnresolvablePropertyReferencesException(PropertyReference[] propertyReferences, Element element, String detail)
  {
    super(detail);
    m_propertyReferences = propertyReferences;
    m_element = element;
  }
  
  public PropertyReference[] getPropertyReferences() { return m_propertyReferences; }
  
  public Element getElement() {
    return m_element;
  }
}

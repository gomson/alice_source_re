package edu.cmu.cs.stage3.alice.core.reference;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.ReferenceResolver;
import edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException;
import edu.cmu.cs.stage3.util.Criterion;




















public class PropertyReference
{
  private Property m_property;
  private Criterion m_criterion;
  
  public PropertyReference(Property property, Criterion criterion)
  {
    m_property = property;
    m_criterion = criterion;
  }
  
  public Property getProperty() { return m_property; }
  
  public Criterion getCriterion() {
    return m_criterion;
  }
  
  public Element getReference() { return (Element)m_property.get(); }
  
  protected Object resolveReference(ReferenceResolver referenceResolver) throws UnresolvableReferenceException
  {
    return referenceResolver.resolveReference(m_criterion);
  }
  
  public void resolve(ReferenceResolver referenceResolver) throws UnresolvableReferenceException { m_property.set(resolveReference(referenceResolver)); }
  
  public String toString()
  {
    return "PropertyReference[property=" + getProperty() + ",criterion=" + getCriterion() + "]";
  }
}

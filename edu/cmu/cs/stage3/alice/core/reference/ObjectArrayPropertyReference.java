package edu.cmu.cs.stage3.alice.core.reference;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceResolver;
import edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.util.Criterion;


















public class ObjectArrayPropertyReference
  extends PropertyReference
{
  private int m_index;
  private int m_precedingTotal;
  
  public ObjectArrayPropertyReference(ObjectArrayProperty objectArrayProperty, Criterion criterion, int index, int precedingTotal)
  {
    super(objectArrayProperty, criterion);
    m_index = index;
    m_precedingTotal = precedingTotal;
  }
  
  public Element getReference() {
    return (Element)getObjectArrayProperty().get(m_index);
  }
  
  public ObjectArrayProperty getObjectArrayProperty() { return (ObjectArrayProperty)getProperty(); }
  
  public void resolve(ReferenceResolver referenceResolver) throws UnresolvableReferenceException
  {
    getObjectArrayProperty().set(m_index, resolveReference(referenceResolver));
  }
  
  public int getIndex() { return m_index; }
  
  public int getPrecedingTotal() {
    return m_precedingTotal;
  }
  
  public String toString() {
    return "ObjectArrayPropertyReference[property=" + getObjectArrayProperty() + ",criterion=" + getCriterion() + ",index=" + getIndex() + "]";
  }
}

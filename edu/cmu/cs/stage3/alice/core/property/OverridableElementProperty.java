package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;






















public class OverridableElementProperty
  extends ElementProperty
{
  private Class m_overrideValueClass = null;
  
  public OverridableElementProperty(Element owner, String name, Element defaultValue) { super(owner, name, defaultValue, Element.class); }
  
  public Class getOverrideValueClass() {
    return m_overrideValueClass;
  }
  
  public void setOverrideValueClass(Class overrideValueClass) { m_overrideValueClass = overrideValueClass; }
  
  public Class getValueClass()
  {
    if (m_overrideValueClass != null) {
      return m_overrideValueClass;
    }
    return super.getValueClass();
  }
}

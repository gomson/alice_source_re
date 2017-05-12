package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;






















public class ValueProperty
  extends ObjectProperty
{
  private Class m_overrideValueClass = null;
  
  public ValueProperty(Element owner, String name, Object defaultValue) { super(owner, name, defaultValue, Object.class); }
  
  public void setOverrideValueClass(Class overrideValueClass) {
    m_overrideValueClass = overrideValueClass;
  }
  
  public Class getValueClass() {
    if (m_overrideValueClass != null) {
      return m_overrideValueClass;
    }
    return super.getValueClass();
  }
}

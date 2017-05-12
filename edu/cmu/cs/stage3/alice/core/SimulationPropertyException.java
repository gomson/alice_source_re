package edu.cmu.cs.stage3.alice.core;

import java.util.Stack;


















public class SimulationPropertyException
  extends SimulationException
{
  private Property m_property;
  
  public SimulationPropertyException(String detail, Stack stack, Property property)
  {
    super(detail, stack, property.getOwner());
    m_property = property;
  }
  
  public Property getProperty() { return m_property; }
}

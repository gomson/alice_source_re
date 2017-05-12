package edu.cmu.cs.stage3.alice.core;

import java.util.Stack;


















public class SimulationException
  extends RuntimeException
{
  private Element m_element;
  private Stack m_stack;
  
  public SimulationException(String detail, Stack stack, Element element)
  {
    super(detail);
    m_element = element;
    m_stack = stack;
  }
  
  public Element getElement() { return m_element; }
  
  public Stack getStack() {
    return m_stack;
  }
}

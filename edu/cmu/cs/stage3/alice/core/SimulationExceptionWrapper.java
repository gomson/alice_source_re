package edu.cmu.cs.stage3.alice.core;

import java.util.Stack;


















public class SimulationExceptionWrapper
  extends SimulationException
{
  private Exception m_exception;
  
  public SimulationExceptionWrapper(String detail, Stack stack, Element element, Exception exception)
  {
    super(detail, stack, element);
    m_exception = exception;
  }
  
  public Exception getWrappedException() { return m_exception; }
}

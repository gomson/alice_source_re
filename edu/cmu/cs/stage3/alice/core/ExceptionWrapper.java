package edu.cmu.cs.stage3.alice.core;











public class ExceptionWrapper
  extends RuntimeException
{
  private Exception m_exception;
  









  public ExceptionWrapper(Exception exception, String detail)
  {
    super(detail);
    m_exception = exception;
  }
  
  public Exception getWrappedException() { return m_exception; }
}

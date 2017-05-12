package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.lang.Messages;





















public class ForEachTogether
  extends ForEach
{
  public ForEachTogether() {}
  
  private static Class[] s_supportedCoercionClasses = new Class[0];
  

  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  public class RuntimeForEachTogether extends ForEach.RuntimeForEach { public RuntimeForEachTogether() { super(); }
    private Response.RuntimeResponse[][] m_runtimeResponsesArray = null;
    private int m_listIndex;
    
    protected Response.RuntimeResponse[] getRuntimeResponses()
    {
      if ((m_listIndex >= 0) && (m_listIndex < m_runtimeResponsesArray.length)) {
        return m_runtimeResponsesArray[m_listIndex];
      }
      throw new ArrayIndexOutOfBoundsException(m_listIndex + " " + Messages.getString("is_out_of_bounds") + " [0," + m_runtimeResponsesArray.length + ").");
    }
    
    protected int getChildCount()
    {
      if ((m_runtimeResponsesArray != null) && (m_runtimeResponsesArray.length > 0)) {
        return m_runtimeResponsesArray[0].length;
      }
      return 0;
    }
    
    protected double getChildTimeRemaining(int index, double t)
    {
      double timeRemaining = 0.0D;
      for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex += 1) {
        timeRemaining = Math.max(timeRemaining, m_runtimeResponsesArray[m_listIndex][index].getTimeRemaining(t));
      }
      return timeRemaining;
    }
    
    protected void childPrologueIfNecessary(int index, double t) {
      for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex += 1) {
        setForkIndex(m_listIndex);
        super.childPrologueIfNecessary(index, t);
      }
      setForkIndex(-1);
    }
    
    protected void childUpdate(int index, double t) {
      for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex += 1) {
        setForkIndex(m_listIndex);
        super.childUpdate(index, t);
      }
      setForkIndex(-1);
    }
    
    protected void childEpilogue(int index, double t) {
      for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex += 1) {
        setForkIndex(m_listIndex);
        super.childEpilogue(index, t);
      }
      setForkIndex(-1);
    }
    
    protected void childrenEpiloguesIfNecessary(double t)
    {
      for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex += 1) {
        super.childrenEpiloguesIfNecessary(t);
      }
    }
    








    public void prologue(double t)
    {
      super.prologue(t);
      m_runtimeResponsesArray = new Response.RuntimeResponse[m_listSize][];
      for (m_listIndex = 0; m_listIndex < m_runtimeResponsesArray.length; m_listIndex += 1) {
        setForkIndex(m_listIndex);
        m_runtimeResponsesArray[m_listIndex] = manufactureComponentRuntimeResponses(componentResponses);
      }
      setForkIndex(-1);
    }
  }
}

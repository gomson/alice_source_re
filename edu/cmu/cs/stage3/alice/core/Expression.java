package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.event.ExpressionEvent;
import edu.cmu.cs.stage3.alice.core.event.ExpressionListener;
import java.util.Enumeration;
import java.util.Vector;













public abstract class Expression
  extends Element
{
  public Expression() {}
  
  public abstract Object getValue();
  
  public abstract Class getValueClass();
  
  private Vector m_expressionListeners = new Vector();
  private ExpressionListener[] m_expressionListenerArray = null;
  
  public void addExpressionListener(ExpressionListener expressionListener) { m_expressionListeners.addElement(expressionListener);
    m_expressionListenerArray = null;
  }
  
  public void removeExpressionListener(ExpressionListener expressionListener) { m_expressionListeners.removeElement(expressionListener);
    m_expressionListenerArray = null;
  }
  
  public ExpressionListener[] getExpressionListeners() { if (m_expressionListenerArray == null) {
      m_expressionListenerArray = new ExpressionListener[m_expressionListeners.size()];
      m_expressionListeners.copyInto(m_expressionListenerArray);
    }
    return m_expressionListenerArray;
  }
  
  protected void onExpressionChange() { ExpressionEvent expressionEvent = new ExpressionEvent(this);
    Enumeration enum0 = m_expressionListeners.elements();
    while (enum0.hasMoreElements()) {
      ExpressionListener expressionListener = (ExpressionListener)enum0.nextElement();
      expressionListener.expressionChanged(expressionEvent);
    }
  }
}

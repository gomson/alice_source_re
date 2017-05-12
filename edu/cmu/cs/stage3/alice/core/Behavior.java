package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;













public abstract class Behavior
  extends Element
{
  public Behavior() {}
  
  public final BooleanProperty isEnabled = new BooleanProperty(this, Messages.getString("isEnabled"), Boolean.TRUE);
  public final ElementArrayProperty details = new ElementArrayProperty(this, Messages.getString("details"), null, [Ledu.cmu.cs.stage3.alice.core.Expression.class);
  
  private double m_prevT;
  
  private boolean m_isActive = false;
  
  private class RuntimeStack { private RuntimeStack() {}
    private Behavior.Item m_front = new Behavior.Single(Behavior.this, null);
    
    public void clear() { m_front.setNext(null); }
    
    public void push(Behavior.Item item) {
      Behavior.Item t = top();
      t.setNext(item);
      item.setPrev(t);
    }
    
    public void pop() { Behavior.Item t = top();
      if (t.getPrev() != null)
        t.getPrev().setNext(null);
      t.setPrev(null);
    }
    
    public Behavior.Item top() { Behavior.Item prev = m_front;
      for (;;) {
        Behavior.Item curr = prev.getNext();
        if (curr == null) {
          return prev;
        }
        prev = curr;
      }
    }
    
    public Stack getCurrentStack() {
      Stack stack = new Stack();
      Behavior.Item prev = m_front;
      for (;;) {
        Behavior.Item curr = prev.getNext();
        if (curr == null) {
          break;
        }
        prev = curr;
        stack.push(prev);
      }
      
      return stack; } }
  
  private abstract class Item { private Item m_prev;
    
    private Item() {}
    
    public Item getPrev() { return m_prev; }
    

    protected void setPrev(Item prev) { m_prev = prev; }
    
    public abstract Item getNext();
    
    public abstract void setNext(Item paramItem);
    public Variable lookup(Variable variable) { if (m_prev == null) {
        return variable;
      }
      return m_prev.lookup(variable);
    }
    
    public Variable lookup(String name) {
      if (m_prev == null) {
        return null;
      }
      return m_prev.lookup(name);
    } }
  
  private class Fork extends Behavior.Item { private Behavior.Item[] m_nexts;
    private int m_index;
    
    public Fork(int n) { super(null);
      m_nexts = new Behavior.Item[n];
      m_index = -1;
    }
    

    public Behavior.Item getNext()
    {
      if ((m_index < 0) || (m_index >= m_nexts.length))
      {
        m_index = 0;
      }
      return m_nexts[m_index];
    }
    
    public void setNext(Behavior.Item item) {
      m_nexts[m_index] = item;
    }
    
    public void setIndex(int index) { m_index = index; } }
  
  private class Single extends Behavior.Item { private Behavior.Item m_next;
    
    private Single() { super(null); }
    
    public Behavior.Item getNext()
    {
      return m_next;
    }
    

    public void setNext(Behavior.Item next) { m_next = next; }
  }
  
  private class Context extends Behavior.Single {
    private Context() { super(null, null); }
    private Dictionary m_variableMap = new Hashtable();
    private Dictionary m_nameMap = new Hashtable();
    private boolean m_isCeiling = true;
    
    public Variable lookup(Variable variable) {
      Variable runtimeVariable = (Variable)m_variableMap.get(variable);
      if (runtimeVariable != null) {
        return runtimeVariable;
      }
      if (m_isCeiling) {
        return variable;
      }
      return getPrev().lookup(variable);
    }
    

    public Variable lookup(String name)
    {
      Variable runtimeVariable = (Variable)m_nameMap.get(name);
      if (runtimeVariable != null) {
        return runtimeVariable;
      }
      if (m_isCeiling) {
        return null;
      }
      return getPrev().lookup(name);
    }
  }
  

  public void openFork(Object key, int n)
  {
    Fork fork = new Fork(n);
    m_forkMap.put(key, fork);
    m_stack.push(fork);
  }
  
  public void setForkIndex(Object key, int i) { Fork fork = (Fork)m_forkMap.get(key);
    fork.setIndex(i);
  }
  
  public void closeFork(Object key) { if (m_isActive) {
      m_stack.pop();
      m_forkMap.remove(key);
    }
  }
  
  public Stack getCurrentStack() {
    return m_stack.getCurrentStack();
  }
  
  private RuntimeStack m_stack = new RuntimeStack(null);
  private Hashtable m_detailNameMap = new Hashtable();
  private Hashtable m_forkMap = new Hashtable();
  
  public void manufactureAnyNecessaryDetails() {}
  
  protected void enabled() {}
  
  protected void disabled() {}
  
  protected void propertyChanged(Property property, Object value)
  {
    if (property == isEnabled) {
      if (m_isActive) {
        if (value == Boolean.TRUE) {
          enabled();
        } else {
          disabled();
        }
      }
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public void manufactureDetails() {}
  
  public void preSchedule(double t) {}
  
  public void postSchedule(double t) {}
  
  protected abstract void internalSchedule(double paramDouble1, double paramDouble2);
  
  public abstract void stopAllRuntimeResponses(double paramDouble);
  
  public void schedule(double time)
  {
    if (isEnabled.booleanValue()) {
      double dt = time - m_prevT;
      if (dt > 0.0D)
      {
        internalSchedule(time, dt);
        



        m_prevT = time;
      }
    }
  }
  
























  private Variable createRuntimeVariable(Variable other)
  {
    Variable v = new Variable();
    if (isWatch) {
      v = other;
    }
    name.set(name.getStringValue());
    Class cls = other.getValueClass();
    valueClass.set(cls);
    Object value = other.getValue();
    value.set(value);
    return v;
  }
  
  public Variable stackLookup(Variable variable) { Variable returnValue = m_stack.top().lookup(variable);
    return returnValue;
  }
  
  public Variable stackLookup(String name) { return m_stack.top().lookup(name); }
  
































  public Expression detailLookup(String name) { return (Expression)m_detailNameMap.get(name); }
  
  public void pushEach(Variable variable, Variable runtimeVariable) {
    Context context = new Context(null);
    m_isCeiling = false;
    m_variableMap.put(variable, runtimeVariable);
    m_nameMap.put(name.getStringValue(), runtimeVariable);
    m_stack.push(context);
  }
  
  public void pushStack(Variable[] variables, boolean isCeiling) { Context context = new Context(null);
    m_isCeiling = isCeiling;
    for (int i = 0; i < variables.length; i++) {
      Variable variable = variables[i];
      Variable runtimeVariable = createRuntimeVariable(variable);
      m_variableMap.put(variable, runtimeVariable);
      m_nameMap.put(name.getStringValue(), runtimeVariable);
    }
    m_stack.push(context);
  }
  
  public void pushStack(Variable[] actualRequired, Variable[] actualKeyword, Variable[] formalRequired, Variable[] formalKeyword, Variable[] localVariables, boolean isCeiling) { Context context = new Context(null);
    m_isCeiling = isCeiling;
    for (int i = 0; i < formalRequired.length; i++) {
      Variable formal = formalRequired[i];
      String nameValue = name.getStringValue();
      for (int j = 0; j < actualRequired.length; j++) {
        Variable actual = actualRequired[j];
        if (nameValue.equals(name.getStringValue())) {
          Variable runtime = createRuntimeVariable(actual);
          m_nameMap.put(nameValue, runtime);
          m_variableMap.put(formal, runtime);
          break; }
        if (j == actualRequired.length - 1) {
          throw new RuntimeException(Messages.getString("missing_required_parameter__") + nameValue);
        }
      }
    }
    
    for (int i = 0; i < localVariables.length; i++) {
      Variable localVariable = localVariables[i];
      Variable runtime = createRuntimeVariable(localVariable);
      m_nameMap.put(name.getStringValue(), runtime);
      m_variableMap.put(localVariable, runtime);
    }
    m_stack.push(context);
  }
  
  public void popStack() { if (m_isActive) {
      Object context = m_stack.top();
      

      m_stack.pop();
    }
  }
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v)
  {
    for (int i = 0; i < details.size(); i++) {
      internalAddExpressionIfAssignableTo((Expression)details.get(i), cls, v);
    }
    super.internalFindAccessibleExpressions(cls, v);
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    m_prevT = time;
    m_stack.clear();
    m_detailNameMap.clear();
    m_forkMap.clear();
    for (int i = 0; i < details.size(); i++) {
      Expression detail = (Expression)details.get(i);
      m_detailNameMap.put(name.getStringValue(), detail);
    }
    m_isActive = true;
  }
  
  protected void stopped(World world, double time)
  {
    super.stopped(world, time);
    m_isActive = false;
    stopAllRuntimeResponses(time);
    m_stack.clear();
    m_detailNameMap.clear();
    m_forkMap.clear();
  }
}

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.util.Criterion;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;










public class ObjectArrayProperty
  extends ObjectProperty
{
  private Object[] m_arrayValueIfNull = null;
  private Vector m_objectArrayPropertyListeners = new Vector();
  private ObjectArrayPropertyListener[] m_objectArrayPropertyListenerArray = null;
  
  public ObjectArrayProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Object[] defaultValue, Class valueClass) { super(owner, name, defaultValue, valueClass); }
  
  public void addObjectArrayPropertyListener(ObjectArrayPropertyListener objectArrayPropertyListener)
  {
    if (!m_objectArrayPropertyListeners.contains(objectArrayPropertyListener))
    {

      m_objectArrayPropertyListeners.addElement(objectArrayPropertyListener);
      m_objectArrayPropertyListenerArray = null;
    }
  }
  
  public void removeObjectArrayPropertyListener(ObjectArrayPropertyListener objectArrayPropertyListener) { m_objectArrayPropertyListeners.removeElement(objectArrayPropertyListener);
    m_objectArrayPropertyListenerArray = null;
  }
  
  public ObjectArrayPropertyListener[] getObjectArrayPropertyListeners() { if (m_objectArrayPropertyListenerArray == null) {
      m_objectArrayPropertyListenerArray = new ObjectArrayPropertyListener[m_objectArrayPropertyListeners.size()];
      m_objectArrayPropertyListeners.copyInto(m_objectArrayPropertyListenerArray);
    }
    return m_objectArrayPropertyListenerArray;
  }
  

  public Class getComponentType() { return getValueClass().getComponentType(); }
  
  public void setComponentType(Class componentType) {
    Object value = get();
    Object[] prevArray = null;
    int length = 0;
    if ((value != null) && 
      ((value instanceof Object[]))) {
      prevArray = (Object[])value;
      length = prevArray.length;
    }
    


    Object[] currArray = (Object[])Array.newInstance(componentType, length);
    setValueClass(currArray.getClass());
    if (value != null) {
      System.arraycopy(prevArray, 0, currArray, 0, length);
      set(currArray);
    }
  }
  
  public Object[] getArrayValue() { Object value = getValue();
    if (value != null) {
      if ((value instanceof Object[])) {
        return (Object[])value;
      }
      Object[] obj = { value };
      return obj;
    }
    
    if (m_arrayValueIfNull == null) {
      m_arrayValueIfNull = ((Object[])Array.newInstance(getComponentType(), 0));
    }
    return m_arrayValueIfNull;
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    String componentTypeName = node.getAttribute("componentClass");
    try {
      Class arrayComponentCls = Class.forName(componentTypeName);
      NodeList itemNodeList = node.getElementsByTagName("item");
      Object[] array = (Object[])Array.newInstance(arrayComponentCls, itemNodeList.getLength());
      int precedingReferenceTotal = 0;
      for (int i = 0; i < array.length; i++) {
        org.w3c.dom.Element itemNode = (org.w3c.dom.Element)itemNodeList.item(i);
        String criterionTypeName = itemNode.getAttribute("criterionClass");
        if (criterionTypeName.length() > 0) {
          try {
            Class criterionType = Class.forName(criterionTypeName);
            String text = getNodeText(itemNode);
            Criterion criterion;
            Criterion criterion; if (criterionType.isAssignableFrom(InternalReferenceKeyedCriterion.class)) {
              criterion = new InternalReferenceKeyedCriterion(text); } else { Criterion criterion;
              if (criterionType.isAssignableFrom(ExternalReferenceKeyedCriterion.class)) {
                criterion = new ExternalReferenceKeyedCriterion(text);
              } else
                criterion = (Criterion)getValueOf(criterionType, text);
            }
            referencesToBeResolved.addElement(new ObjectArrayPropertyReference(this, criterion, i, precedingReferenceTotal++));
          } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(criterionTypeName);
          }
        } else {
          String itemTypeName = itemNode.getAttribute("class");
          if (itemTypeName.length() > 0) {
            try {
              Class itemType = Class.forName(itemTypeName);
              array[i] = getValueOf(itemType, getNodeText(itemNode));
            } catch (ClassNotFoundException cnfe) {
              throw new RuntimeException(itemTypeName);
            }
          } else {
            array[i] = null;
          }
        }
      }
      set(array);
    } catch (ClassNotFoundException cnfe) {
      throw new RuntimeException(componentTypeName);
    }
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    if (node == null) {
      System.err.println("node==null");
      System.err.println(this);
    }
    if (getComponentType() == null) {
      System.err.println("getComponentType()==null");
      System.err.println(this);
    }
    node.setAttribute("componentClass", getComponentType().getName());
    Object[] array = getArrayValue();
    if ((array != null) && (array.length > 0)) {
      for (int i = 0; i < array.length; i++) {
        Object item = array[i];
        org.w3c.dom.Element itemNode = document.createElement("item");
        if ((item instanceof edu.cmu.cs.stage3.alice.core.Element)) {
          encodeReference(document, itemNode, referenceGenerator, (edu.cmu.cs.stage3.alice.core.Element)item);
        } else if (item != null) {
          itemNode.setAttribute("class", item.getClass().getName());
          itemNode.appendChild(createNodeForString(document, item.toString()));
        }
        
        node.appendChild(itemNode);
      }
    }
  }
  
  private void onItemChanging(Object item, int changeType, int oldIndex, int newIndex) {
    if (!m_objectArrayPropertyListeners.isEmpty()) {
      ObjectArrayPropertyEvent objectArrayPropertyEvent = new ObjectArrayPropertyEvent(this, item, changeType, oldIndex, newIndex);
      ObjectArrayPropertyListener[] objectArrayPropertyListeners = getObjectArrayPropertyListeners();
      for (int i = 0; i < objectArrayPropertyListeners.length; i++) {
        objectArrayPropertyListeners[i].objectArrayPropertyChanging(objectArrayPropertyEvent);
      }
    }
  }
  





  private void onItemChanged(Object item, int changeType, int oldIndex, int newIndex)
  {
    if (!m_objectArrayPropertyListeners.isEmpty()) {
      ObjectArrayPropertyEvent objectArrayPropertyEvent = new ObjectArrayPropertyEvent(this, item, changeType, oldIndex, newIndex);
      ObjectArrayPropertyListener[] objectArrayPropertyListeners = getObjectArrayPropertyListeners();
      for (int i = 0; i < objectArrayPropertyListeners.length; i++) {
        objectArrayPropertyListeners[i].objectArrayPropertyChanged(objectArrayPropertyEvent);
      }
    }
  }
  






  public void add(int index, Object o)
  {
    if (index == -1) {
      add(o);
    } else {
      Object[] prev = getArrayValue();
      
      if (prev == null) {
        if (index == 0) {
          add(o);
        }
        else {
          throw new RuntimeException();
        }
      } else {
        onItemChanging(o, 1, -1, index);
        int n = prev.length;
        Object[] curr = (Object[])Array.newInstance(getComponentType(), n + 1);
        if (index > 0) {
          System.arraycopy(prev, 0, curr, 0, index);
        }
        if (index < n) {
          System.arraycopy(prev, index, curr, index + 1, n - index);
        }
        curr[index] = o;
        set(curr);
        onItemChanged(o, 1, -1, index);
      }
    }
  }
  
  public void addValue(int index, Object o) { add(index, evaluateIfNecessary(o)); }
  
  public void add(Object o) {
    Object[] prev = getArrayValue();
    int index;
    int index;
    if (prev == null) {
      index = 0;
    } else {
      index = prev.length;
    }
    onItemChanging(o, 1, -1, index);
    Object[] curr; Object[] curr; if (prev == null) {
      curr = (Object[])Array.newInstance(getComponentType(), 1);
    } else {
      curr = (Object[])Array.newInstance(getComponentType(), index + 1);
      System.arraycopy(prev, 0, curr, 0, index);
    }
    curr[index] = o;
    set(curr);
    onItemChanged(o, 1, -1, index);
  }
  
  public void addValue(Object o) { add(evaluateIfNecessary(o)); }
  
  public void remove(int index) {
    Object[] prev = getArrayValue();
    onItemChanging(prev[index], 3, index, -1);
    int n = prev.length;
    Object[] curr = (Object[])Array.newInstance(getComponentType(), n - 1);
    if (index > 0) {
      System.arraycopy(prev, 0, curr, 0, index);
    }
    if (index < n - 1) {
      System.arraycopy(prev, index + 1, curr, index, n - 1 - index);
    }
    set(curr);
    onItemChanged(prev[index], 3, index, -1);
  }
  
  public void remove(Object o) { Object[] prev = getArrayValue();
    if (prev != null) {
      int n = prev.length;
      for (int i = 0; i < n; i++) {
        if (prev[i] == o) {
          remove(i);
          break;
        }
      }
    }
  }
  
  public void removeValue(Object o) { remove(evaluateIfNecessary(o)); }
  
  public void set(int index, Object o) {
    ensureCapacity(index + 1);
    int n = size();
    if ((index >= 0) && (index < n)) {
      Object[] prev = getArrayValue();
      onItemChanging(prev[index], 3, index, -1);
      onItemChanging(o, 1, -1, index);
      Object[] curr = (Object[])Array.newInstance(getComponentType(), n);
      System.arraycopy(prev, 0, curr, 0, n);
      if (((o instanceof Expression)) && 
        (!Expression.class.isAssignableFrom(getComponentType()))) {
        o = ((Expression)o).getValue();
      }
      
      curr[index] = o;
      set(curr);
      onItemChanged(prev[index], 3, index, -1);
      onItemChanged(o, 1, -1, index);
    } else {
      throw new ArrayIndexOutOfBoundsException("index " + index + " is out of bounds [ 0, " + n + ")");
    }
  }
  
  public void setValue(int index, Object o) { set(index, evaluateIfNecessary(o)); }
  
  public void clear() {
    Object[] prev = getArrayValue();
    for (int i = 0; i < prev.length; i++) {
      onItemChanging(prev[i], 3, i, -1);
    }
    set(Array.newInstance(getComponentType(), 0));
    for (int i = 0; i < prev.length; i++) {
      onItemChanged(prev[i], 3, i, -1);
    }
  }
  
  public void shift(int fromIndex, int toIndex)
  {
    if (fromIndex != toIndex) {
      Object[] prev = getArrayValue();
      
      onItemChanging(prev[fromIndex], 2, fromIndex, toIndex);
      
      Object[] curr = (Object[])Array.newInstance(getComponentType(), prev.length);
      if (fromIndex < toIndex) {
        for (int i = 0; i < fromIndex; i++) {
          curr[i] = prev[i];
        }
        for (int i = fromIndex; i < toIndex; i++) {
          curr[i] = prev[(i + 1)];
        }
        curr[toIndex] = prev[fromIndex];
        for (int i = toIndex + 1; i < prev.length; i++) {
          curr[i] = prev[i];
        }
      } else {
        for (int i = 0; i < toIndex; i++) {
          curr[i] = prev[i];
        }
        curr[toIndex] = prev[fromIndex];
        for (int i = toIndex + 1; i < fromIndex + 1; i++) {
          curr[i] = prev[(i - 1)];
        }
        for (int i = fromIndex + 1; i < prev.length; i++) {
          curr[i] = prev[i];
        }
      }
      set(curr);
      onItemChanged(prev[fromIndex], 2, fromIndex, toIndex);
    }
  }
  
  public Object get(int index) {
    int n = size();
    if (index == -1) {
      index = n - 1;
    }
    if ((index >= 0) && (index < n)) {
      Object[] array = getArrayValue();
      return array[index];
    }
    return null;
  }
  
  public Object getValue(int index)
  {
    return evaluateIfNecessary(get(index));
  }
  
  private static boolean areEqual(Object a, Object b, boolean evaluateExpressionIfNecessary)
  {
    if (evaluateExpressionIfNecessary) {
      if (a == null) {
        return b == null;
      }
      if (a.equals(b))
        return true;
      if ((a instanceof Expression)) {
        Expression e = (Expression)a;
        Object v = e.getValue();
        if (v == null) {
          return b == null;
        }
        return v.equals(b);
      }
      
      return false;
    }
    

    return a == b;
  }
  
  private int indexOf(Object o, int index, boolean evaluateExpressionIfNecessary) {
    Object[] array = getArrayValue();
    if (array != null) {
      for (int i = index; i < array.length; i++) {
        if (areEqual(o, array[i], evaluateExpressionIfNecessary)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public int indexOf(Object o, int index) { return indexOf(o, index, false); }
  
  public int indexOf(Object o) {
    return indexOf(o, 0);
  }
  
  public int indexOfValue(Object o, int index) { return indexOf(o, index, true); }
  

  public int indexOfValue(Object o) { return indexOfValue(o, size() - 1); }
  
  private int lastIndexOf(Object o, int index, boolean evaluateExpressionIfNecessary) {
    Object[] array = getArrayValue();
    if (array != null) {
      for (int i = index; i >= 0; i--) {
        if (areEqual(o, array[i], evaluateExpressionIfNecessary)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public int lastIndexOf(Object o, int index) { return lastIndexOf(o, index, false); }
  
  public int lastIndexOf(Object o) {
    return lastIndexOf(o, size() - 1);
  }
  
  public int lastIndexOfValue(Object o, int index) { return lastIndexOf(o, index, true); }
  
  public int lastIndexOfValue(Object o) {
    return lastIndexOfValue(o, size() - 1);
  }
  
  public boolean contains(Object o) {
    return indexOf(o) != -1;
  }
  
  public boolean containsValue(Object o) { return indexOfValue(o) != -1; }
  


  public boolean isEmpty() { return size() == 0; }
  
  public int size() {
    Object[] value = getArrayValue();
    if (value != null) {
      return value.length;
    }
    return 0;
  }
  
  public void ensureCapacity(int minCapacity) {
    Object[] prev = getArrayValue();
    if (prev.length < minCapacity) {
      Object[] curr = (Object[])Array.newInstance(getComponentType(), minCapacity);
      System.arraycopy(prev, 0, curr, 0, prev.length);
      set(curr);
    }
  }
}

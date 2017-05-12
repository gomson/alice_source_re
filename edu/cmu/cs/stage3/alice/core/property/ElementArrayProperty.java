package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;


















public class ElementArrayProperty
  extends ObjectArrayProperty
{
  public ElementArrayProperty(Element owner, String name, Object[] defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
  
  public Element[] getElementArrayValue() { return (Element[])getArrayValue(); }
  
  private void sortByName(final boolean ignoreCase) {
    Element[] src = getElementArrayValue();
    Element[] dst = (Element[])Array.newInstance(getComponentType(), src.length);
    System.arraycopy(src, 0, dst, 0, dst.length);
    Arrays.sort(dst, new Comparator() {
      public int compare(Object o1, Object o2) {
        Element e1 = (Element)o1;
        Element e2 = (Element)o2;
        String n1 = name.getStringValue();
        String n2 = name.getStringValue();
        if (n1 != null) {
          if (n2 != null) {
            if (ignoreCase) {
              return n1.compareTo(n2);
            }
            return n1.compareToIgnoreCase(n2);
          }
          
          return -1;
        }
        
        if (n2 != null) {
          return 1;
        }
        return 0;
      }
      

      public boolean equals(Object obj)
      {
        return super.equals(obj);
      }
      
    });
    set(dst);
  }
  
  public void sortByName() {
    sortByName(false);
  }
  
  public void sortByNameIgnoreCase() { sortByName(true); }
}

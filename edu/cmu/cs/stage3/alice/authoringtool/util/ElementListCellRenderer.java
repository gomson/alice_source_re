package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


















public class ElementListCellRenderer
  extends DefaultListCellRenderer
{
  public static final int JUST_NAME = 0;
  public static final int DEFAULT_REPR = 1;
  public static final int FULL_KEY = 2;
  protected int verbosity;
  
  public ElementListCellRenderer(int verbosity)
  {
    this.verbosity = verbosity;
  }
  
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    if ((value instanceof Element)) {
      Element element = (Element)value;
      if (verbosity == 0) {
        value = name.getStringValue();
      } else if (verbosity == 1) {
        value = AuthoringToolResources.getReprForValue(element, true);
      } else if (verbosity == 2) {
        value = element.getKey();
      }
    }
    
    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
  }
}

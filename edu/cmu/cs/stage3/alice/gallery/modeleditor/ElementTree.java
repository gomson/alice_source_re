package edu.cmu.cs.stage3.alice.gallery.modeleditor;

import edu.cmu.cs.stage3.alice.core.Element;

class ElementTree extends javax.swing.JTree { public ElementTree(ElementTreeModel model) { super(model); }
  
  public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
  {
    Element element = (Element)value;
    if (element != null) {
      return name.getStringValue();
    }
    return null;
  }
}

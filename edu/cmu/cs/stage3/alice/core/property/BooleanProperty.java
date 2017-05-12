package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.util.HowMuch;
import java.io.IOException;
import java.util.Vector;
import org.w3c.dom.Document;















public class BooleanProperty
  extends ObjectProperty
{
  public BooleanProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Boolean defaultValue)
  {
    super(owner, name, defaultValue, Boolean.class);
  }
  
  public Object getValue() {
    Object value = super.getValue();
    if ((value instanceof Number)) {
      Number number = (Number)value;
      if (number.byteValue() != 0) {
        value = Boolean.TRUE;
      } else {
        value = Boolean.FALSE;
      }
    }
    return value;
  }
  
  public Boolean getBooleanValue() { return (Boolean)getValue(); }
  
  public boolean booleanValue(boolean valueIfNull) {
    Boolean b = getBooleanValue();
    if (b != null) {
      return b.booleanValue();
    }
    return valueIfNull;
  }
  
  public boolean booleanValue() {
    return booleanValue(false);
  }
  
  public void set(boolean value) throws IllegalArgumentException {
    if (value) {
      set(Boolean.TRUE);
    } else
      set(Boolean.FALSE);
  }
  
  public void set(boolean value, HowMuch howMuch) throws IllegalArgumentException {
    if (value) {
      set(Boolean.TRUE, howMuch);
    } else {
      set(Boolean.FALSE, howMuch);
    }
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException {
    set(Boolean.valueOf(getNodeText(node)));
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    node.appendChild(createNodeForString(document, getBooleanValue().toString()));
  }
}

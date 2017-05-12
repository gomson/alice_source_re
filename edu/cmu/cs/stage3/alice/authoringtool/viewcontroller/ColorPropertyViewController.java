package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.Box;














public class ColorPropertyViewController
  extends PropertyViewController
{
  public ColorPropertyViewController() {}
  
  protected Component strut = Box.createHorizontalStrut(16);
  
  public void set(Property property, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory) {
    super.set(property, true, allowExpressions, true, omitPropertyName, factory);
    setPopupEnabled(true);
    refreshGUI();
  }
  
  protected String getHTMLColorString(Color color) {
    String r = Integer.toHexString((int)(color.getRed() * 255.0F));
    String g = Integer.toHexString((int)(color.getGreen() * 255.0F));
    String b = Integer.toHexString((int)(color.getBlue() * 255.0F));
    
    if (r.length() == 1) {
      r = "0" + r;
    }
    if (g.length() == 1) {
      g = "0" + g;
    }
    if (b.length() == 1) {
      b = "0" + b;
    }
    return new String("#" + r + g + b);
  }
  
  protected String getReadableColorString(Color color) {
    DecimalFormat numberFormatter = new DecimalFormat();
    String toReturn = new String("(");
    toReturn = toReturn + numberFormatter.format(color.getRed());
    toReturn = toReturn + ", ";
    toReturn = toReturn + numberFormatter.format(color.getGreen());
    toReturn = toReturn + ", ";
    toReturn = toReturn + numberFormatter.format(color.getBlue());
    toReturn = toReturn + ")";
    return toReturn;
  }
  
  public void getHTML(StringBuffer toWriteTo) {
    if ((property.get() instanceof Color)) {
      Color color = (Color)property.get();
      String colorString = getHTMLColorString(color);
      String colorName = AuthoringToolResources.getName(color);
      if (colorName == null) {
        colorName = getReadableColorString(color);
      }
      toWriteTo.append("<span style=\"background-color:" + colorString + "\"><font color=" + colorString + "\">" + colorName + "</font></span>");
    } else {
      for (int i = 0; i < getComponentCount(); i++) {
        toWriteTo.append(GUIFactory.getHTMLStringForComponent(getComponent(i)));
      }
    }
  }
  

  protected Component getNativeComponent()
  {
    return strut;
  }
  
  protected Class getNativeClass() {
    return Color.class;
  }
  
  protected void updateNativeComponent() {
    setBackground(((Color)property.getValue()).createAWTColor());
  }
  
  protected void refreshGUI() {
    setBackground(AuthoringToolResources.getColor("propertyViewControllerBackground"));
    super.refreshGUI();
  }
}

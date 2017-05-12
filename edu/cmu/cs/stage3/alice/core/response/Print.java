package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;














public class Print
  extends Response
{
  public final StringProperty text = new StringProperty(this, "text", null);
  public final ObjectProperty object = new ObjectProperty(this, "object", null, Object.class)
  {

    protected boolean getValueOfExpression() { return true; } };
  
  public Print() {}
  public final BooleanProperty addNewLine = new BooleanProperty(this, "addNewLine", Boolean.TRUE);
  
  protected Number getDefaultDuration() {
    return new Double(0.0D); }
  
  public String getPrefix()
  {
    String t = text.getStringValue();
    if (t != null) {
      return null;
    }
    Object o = object.get();
    if (o != null) {
      if ((o instanceof Element)) {
        return Messages.getString("the_value_of_") + ((Element)o).getTrimmedKey() + " " + Messages.getString("is_");
      }
      return Messages.getString("the_value_of_") + o + " " + Messages.getString("is_");
    }
    
    return null;
  }
  


  public static String outputtext = null;
  public class RuntimePrint extends Response.RuntimeResponse { public RuntimePrint() { super(); }
    
    public void update(double t) {
      super.update(t);
      Print.outputtext = null;
      String s = text.getStringValue();
      Object o = object.get();
      










































      Object value = object.getValue();
      if ((value instanceof Double)) {
        NumberFormat formatter = new DecimalFormat("#.######");
        value = Double.valueOf(formatter.format(value));
      }
      
      String valueText = Messages.getString("None");
      
      if ((value instanceof Element)) {
        valueText = ((Element)value).getTrimmedKey();
        if ((value instanceof List)) {
          valueText = valueText.substring(0, valueText.indexOf("__") - 1);
        }
      } else if ((value instanceof Color)) {
        double blue = ((Color)value).getBlue();
        double green = ((Color)value).getGreen();
        double red = ((Color)value).getRed();
        if ((blue == 1.0D) && (green == 1.0D) && (red == 1.0D)) {
          valueText = Messages.getString("white");
        } else if ((blue == 0.0D) && (green == 0.0D) && (red == 0.0D)) {
          valueText = Messages.getString("black");
        } else if ((blue == 0.0D) && (green == 0.0D) && (red == 1.0D)) {
          valueText = Messages.getString("red");
        } else if ((blue == 0.0D) && (green == 1.0D) && (red == 0.0D)) {
          valueText = Messages.getString("green");
        } else if ((blue == 1.0D) && (green == 0.0D) && (red == 0.0D)) {
          valueText = Messages.getString("blue");
        } else if ((blue == 0.0D) && (green == 1.0D) && (red == 1.0D)) {
          valueText = Messages.getString("yellow");
        } else if ((blue == 0.501960813999176D) && (green == 0.0D) && (red == 0.501960813999176D)) {
          valueText = Messages.getString("purple");
        } else if ((blue == 0.0D) && (green == 0.6470588445663452D) && (red == 1.0D)) {
          valueText = Messages.getString("orange");
        } else if ((blue == 0.686274528503418D) && (green == 0.686274528503418D) && (red == 1.0D)) {
          valueText = Messages.getString("pink");
        } else if ((blue == 0.16470588743686676D) && (green == 0.16470588743686676D) && (red == 0.6352941393852234D)) {
          valueText = Messages.getString("brown");
        } else if ((blue == 1.0D) && (green == 1.0D) && (red == 0.0D)) {
          valueText = Messages.getString("cyan");
        } else if ((blue == 1.0D) && (green == 0.0D) && (red == 1.0D)) {
          valueText = Messages.getString("magenta");
        } else if ((blue == 0.501960813999176D) && (green == 0.501960813999176D) && (red == 0.501960813999176D)) {
          valueText = Messages.getString("gray");
        } else if ((blue == 0.7529411911964417D) && (green == 0.7529411911964417D) && (red == 0.7529411911964417D)) {
          valueText = Messages.getString("light_gray");
        } else if ((blue == 0.250980406999588D) && (green == 0.250980406999588D) && (red == 0.250980406999588D)) {
          valueText = Messages.getString("dark_gray");
        } else {
          valueText = value.toString();
          valueText = valueText.substring(valueText.indexOf(Messages.getString("Color")), valueText.length());
        }
      } else if (value != null) {
        valueText = value.toString();
      }
      
      String output = "";
      if (s != null) {
        if (o != null) {
          output = s + valueText;
        } else {
          output = s;
        }
      }
      else if (o != null)
      {



        output = output + valueText;
      } else {
        output = valueText;
      }
      
      if (addNewLine.booleanValue()) {
        System.out.println(output);
      } else {
        System.out.print(output);
      }
    }
  }
}

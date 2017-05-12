package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.Enumerable;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;









public class PropertyCellRenderer
  extends DefaultTableCellRenderer
{
  protected ColorRenderer colorRenderer = new ColorRenderer();
  
  public PropertyCellRenderer() {}
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    String toolTipText = null;
    if (value != null) {
      if ((value instanceof Element)) {
        toolTipText = ((Element)value).getKey();
      } else if ((value instanceof String)) {
        toolTipText = "\"" + getText() + "\"";
      } else {
        toolTipText = getText();
      }
      String classString = value.getClass().getName();
      if (classString.startsWith("edu.cmu.cs.stage3.")) {
        classString = classString.substring("edu.cmu.cs.stage3.".length());
      }
      toolTipText = toolTipText + " (" + classString + ")";
    }
    setToolTipText(toolTipText);
    
    if ((value instanceof Element)) {
      setText(AuthoringToolResources.getReprForValue(value));
    } else { if (((value instanceof java.awt.Color)) || ((value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color))) {
        colorRenderer.setToolTipText(toolTipText);
        return colorRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); }
      if ((value != null) && (value.getClass().isArray())) {
        String text = "";
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        text = "{ ";
        int m = Array.getLength(value);
        for (int i = 0; i < m; i++) {
          Object o = Array.get(value, i);
          if (o.getClass().isArray()) {
            text = text + "{ ";
            int n = Array.getLength(o);
            for (int j = 0; j < n; j++) {
              Object p = Array.get(o, j);
              if ((p instanceof Number)) {
                text = text + nf.format(p);
              } else {
                text = text + p;
              }
              if (j < n - 1) {
                text = text + ", ";
              }
              else {
                text = text + " ";
              }
            }
            text = text + "}";
          }
          else if ((o instanceof Element)) {
            text = text + AuthoringToolResources.getReprForValue(o);
          } else if ((o instanceof Number)) {
            text = text + nf.format(o);
          } else {
            text = text + o;
          }
          
          if (i < m - 1) {
            text = text + ", ";
          } else {
            text = text + " ";
          }
          if (text.length() > 64) {
            text = text + "...";
            break;
          }
        }
        text = text + "}";
        setText(text);
      } else if ((value instanceof Enumerable)) {
        setText(((Enumerable)value).getRepr());
      } else if ((value instanceof Enumeration)) {
        setText(Messages.getString("_enumeration_"));
      }
    }
    return this;
  }
  
  class ColorRenderer extends DefaultTableCellRenderer {
    Hashtable colorsToIcons = new Hashtable();
    
    public ColorRenderer() {}
    
    public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
      if ((color instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)) {
        edu.cmu.cs.stage3.alice.scenegraph.Color c = (edu.cmu.cs.stage3.alice.scenegraph.Color)color;
        color = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
      }
      
      setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
      
      Icon icon = (Icon)colorsToIcons.get(color);
      if (icon == null) {
        int height = table.getRowHeight() - 4;
        int width = height * 2;
        BufferedImage colorImage = new BufferedImage(width, height, 1);
        Graphics2D g = colorImage.createGraphics();
        g.setColor((java.awt.Color)color);
        g.fill3DRect(0, 0, width, height, true);
        icon = new ImageIcon(colorImage);
        colorsToIcons.put(color, icon);
      }
      
      setIcon(icon);
      setFont(table.getFont());
      setText(getTextFromColor((java.awt.Color)color));
      
      return this;
    }
    
    public String getTextFromColor(java.awt.Color color) {
      String text = "";
      if (color.equals(java.awt.Color.black)) { text = Messages.getString("black");
      } else if (color.equals(java.awt.Color.blue)) { text = Messages.getString("blue");
      } else if (color.equals(java.awt.Color.cyan)) { text = Messages.getString("cyan");
      } else if (color.equals(java.awt.Color.darkGray)) { text = Messages.getString("darkGray");
      } else if (color.equals(java.awt.Color.gray)) { text = Messages.getString("gray");
      } else if (color.equals(java.awt.Color.green)) { text = Messages.getString("green");
      } else if (color.equals(java.awt.Color.lightGray)) { text = Messages.getString("lightGray");
      } else if (color.equals(java.awt.Color.magenta)) { text = Messages.getString("magenta");
      } else if (color.equals(java.awt.Color.orange)) { text = Messages.getString("orange");
      } else if (color.equals(java.awt.Color.pink)) { text = Messages.getString("pink");
      } else if (color.equals(java.awt.Color.red)) { text = Messages.getString("red");
      } else if (color.equals(java.awt.Color.white)) { text = Messages.getString("white");
      } else if (color.equals(java.awt.Color.yellow)) { text = Messages.getString("yellow");
      } else {
        float[] rgba = new float[4];
        color.getComponents(rgba);
        text = Messages.getString("_red___") + rgba[0] + Messages.getString("__green___") + rgba[1] + Messages.getString("__blue___") + rgba[2] + Messages.getString("__alpha___") + rgba[3] + ">";
      }
      
      return text;
    }
  }
}

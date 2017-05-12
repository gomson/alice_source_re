package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;










class ColorRenderer
  extends JLabel
  implements ListCellRenderer, TableCellRenderer, TreeCellRenderer
{
  public ColorRenderer() {}
  
  public Component getListCellRendererComponent(JList list, Object color, int index, boolean isSelected, boolean cellHasFocus)
  {
    return getComponent(color, isSelected, list.getBackground(), list.getSelectionBackground(), list.getFixedCellHeight(), list.getFont());
  }
  
  public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
    return getComponent(color, isSelected, table.getBackground(), table.getSelectionBackground(), table.getRowHeight(), table.getFont());
  }
  

  public Component getTreeCellRendererComponent(JTree tree, Object color, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus)
  {
    return getComponent(color, isSelected, tree.getBackground(), tree.getBackground(), tree.getRowHeight(), tree.getFont());
  }
  
  public Component getComponent(Object color, boolean isSelected, java.awt.Color backgroundColor, java.awt.Color selectionBackgroundColor, int cellHeight, Font font) {
    setOpaque(true);
    setBackground(isSelected ? selectionBackgroundColor : backgroundColor);
    setFont(font);
    
    if ((color instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)) {
      edu.cmu.cs.stage3.alice.scenegraph.Color c = (edu.cmu.cs.stage3.alice.scenegraph.Color)color;
      color = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    } else if ((color instanceof String)) {
      setIcon(null);
      setText((String)color);
      return this;
    }
    
    int height = cellHeight - 4;
    if (height < 10) {
      height = 10;
    }
    int width = height * 2;
    
    setIcon(getIconFromColor((java.awt.Color)color, width, height));
    setText(getTextFromColor((java.awt.Color)color));
    
    return this;
  }
  
  public static Icon getIconFromColor(java.awt.Color color, int width, int height) {
    BufferedImage colorImage = new BufferedImage(width, height, 1);
    Graphics2D g = colorImage.createGraphics();
    g.setColor(color);
    g.fill3DRect(0, 0, width, height, true);
    return new ImageIcon(colorImage);
  }
  
  public static String getTextFromColor(java.awt.Color color) {
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

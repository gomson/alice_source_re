package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;









public class GroupingPanel
  extends JPanel
  implements DropTargetListener, Releasable
{
  protected Border outerBorder;
  protected Border innerBorder;
  protected Border border;
  protected Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  
  private class PartialLineBorder extends AbstractBorder {
    protected Color color;
    protected int thickness;
    protected boolean includeTop;
    protected boolean includeLeft;
    protected boolean includeBottom;
    protected boolean includeRight;
    
    public PartialLineBorder(Color color, int thickness, boolean includeTop, boolean includeLeft, boolean includeBottom, boolean includeRight) {
      this.color = color;
      this.thickness = thickness;
      this.includeTop = includeTop;
      this.includeLeft = includeLeft;
      this.includeBottom = includeBottom;
      this.includeRight = includeRight;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      g.setColor(color);
      for (int i = 0; i < thickness; i++) {
        if (includeTop) {
          g.drawLine(x, y + i, x + width - 1, y + i);
        }
        if (includeLeft) {
          g.drawLine(x + i, y, x + i, y + height - 1);
        }
        if (includeBottom) {
          g.drawLine(x, y - i + height - 1, x + width - 1, y - i + height - 1);
        }
        if (includeRight) {
          g.drawLine(x - i + width - 1, y, x - i + width - 1, y + height - 1);
        }
      }
      g.setColor(oldColor);
    }
    
    public Insets getBorderInsets(Component c) {
      return new Insets(includeTop ? thickness : 0, includeLeft ? thickness : 0, includeBottom ? thickness : 0, includeRight ? thickness : 0);
    }
    
    public Insets getBorderInsets(Component c, Insets insets) {
      top = (includeTop ? thickness : 0);
      left = (includeLeft ? thickness : 0);
      bottom = (includeBottom ? thickness : 0);
      right = (includeRight ? thickness : 0);
      return insets;
    }
    
    public Color getLineColor() {
      return color;
    }
    
    public int getThickness() {
      return thickness;
    }
    
    public boolean isBorderOpaque() {
      return true;
    }
  }
  
  public GroupingPanel() {
    outerBorder = new PartialLineBorder(Color.lightGray, 1, false, false, true, true);
    innerBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    border = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
    setBorder(border);
    setDoubleBuffered(true);
    


    setDropTarget(new DropTarget(this, this));
    
    addContainerListener(
      new ContainerAdapter() {
        public void componentAdded(ContainerEvent ev) {
          if (ev.getChild().getDropTarget() == null)
            ev.getChild().setDropTarget(new DropTarget(ev.getChild(), GroupingPanel.this));
        }
        
        public void componentRemoved(ContainerEvent ev) {
          if (ev.getChild().getDropTarget() != null) {
            ev.getChild().getDropTarget().setActive(false);
            ev.getChild().setDropTarget(null);
          }
          
        }
      });
    addContainerListener(GUIElementContainerListener.getStaticListener());
  }
  
  public void release() {
    removeAll();
  }
  



  public void paintBackground(Graphics g) {}
  



  public void paintForeground(Graphics g) {}
  



  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    paintBackground(g);
  }
  
  public void paintChildren(Graphics g) {
    super.paintChildren(g);
    paintForeground(g);
  }
  
  public void printComponent(Graphics g) {
    if (authoringToolConfig.getValue("printing.fillBackground").equalsIgnoreCase("true")) {
      super.printComponent(g);
    }
  }
  


  public void dragEnter(DropTargetDragEvent dtde)
  {
    if ((getParent() instanceof DropTargetListener)) {
      ((DropTargetListener)getParent()).dragEnter(dtde);
    } else {
      dtde.rejectDrag();
    }
  }
  
  public void dragExit(DropTargetEvent dte) {
    if ((getParent() instanceof DropTargetListener)) {
      ((DropTargetListener)getParent()).dragExit(dte);
    }
  }
  
  public void dragOver(DropTargetDragEvent dtde) {
    if ((getParent() instanceof DropTargetListener)) {
      ((DropTargetListener)getParent()).dragOver(dtde);
    } else {
      dtde.rejectDrag();
    }
  }
  
  public void drop(DropTargetDropEvent dtde) {
    if ((getParent() instanceof DropTargetListener)) {
      ((DropTargetListener)getParent()).drop(dtde);
    } else {
      dtde.rejectDrop();
    }
  }
  
  public void dropActionChanged(DropTargetDragEvent dtde) {
    if ((getParent() instanceof DropTargetListener)) {
      ((DropTargetListener)getParent()).dropActionChanged(dtde);
    } else {
      dtde.rejectDrag();
    }
  }
}

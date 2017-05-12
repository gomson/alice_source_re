package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;













public class InsertPanel
  extends JPanel
  implements DropTargetListener
{
  protected JLabel m_label = new JLabel();
  protected String m_doNothingLabel = " " + Messages.getString("Do_Nothing");
  private boolean highlight = false;
  
  public InsertPanel() {
    setDropTarget(new DropTarget(this, this));
    m_label.setDropTarget(new DropTarget(this, this));
    m_label.setFont(m_label.getFont().deriveFont(2));
    setOpaque(false);
    setLayout(new GridBagLayout());
    add(m_label, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
    add(Box.createHorizontalGlue(), new GridBagConstraints(1, 0, 1, 1, 1.0D, 1.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
    setMaximumSize(new Dimension(2400, 30));
    setMinimumSize(new Dimension(0, 0));
    setBorder(null);
    m_label.setText(" " + m_doNothingLabel);
  }
  
  public void setHighlight(boolean toSet) {
    if (highlight != toSet) {
      highlight = toSet;
      repaint();
    }
  }
  
  public void paintComponent(Graphics g) {
    Rectangle r = new Rectangle(0, 0, getWidth(), getHeight());
    if (highlight) {
      int arcWidth = 12;
      int arcHeight = 10;
      Object oldAntialiasing = null;
      if ((g instanceof Graphics2D)) {
        oldAntialiasing = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
      }
      Rectangle bounds = getBounds();
      g.setColor(new Color(255, 255, 255));
      g.fillRoundRect(0, 0, width, height, arcWidth, arcHeight);
      g.setColor(Color.lightGray);
      g.drawRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight);
      if ((g instanceof Graphics2D)) {
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
      }
    }
    GUIEffects.paintTrough(g, r, 12, 10);
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

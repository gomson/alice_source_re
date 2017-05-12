package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;











public class HighlightingGlassPane
  extends JPanel
{
  protected AuthoringTool authoringTool;
  protected Component savedGlassPane;
  protected String highlightID;
  protected Timer blinkTimer;
  protected Color highlightColor = Color.red;
  protected MousePassThroughListener mousePassThroughListener = new MousePassThroughListener();
  
  public HighlightingGlassPane(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    
    setOpaque(false);
    addMouseListener(mousePassThroughListener);
    addMouseMotionListener(mousePassThroughListener);
    
    blinkTimer = new Timer(10, new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        Rectangle r = getHighlightRect();
        if (r != null) {
          highlightColor = new Color(1.0F, 0.0F, 0.0F, (float)(0.5D * Math.sin(System.currentTimeMillis() / 400.0D) + 0.5D));
          x -= 4;
          y -= 4;
          width += 8;
          height += 8;
          repaint(r);
        }
      }
    });
  }
  
  public void setHighlightID(String highlightID) {
    boolean oldEnabled = blinkTimer.isRunning();
    setHighlightingEnabled(false);
    this.highlightID = highlightID;
    if (oldEnabled) {
      setHighlightingEnabled(true);
    }
  }
  
  public void setHighlightingEnabled(boolean enabled)
  {
    if (enabled) {
      if (authoringTool.getJAliceFrame().getGlassPane() != this) {
        savedGlassPane = authoringTool.getJAliceFrame().getGlassPane();
        authoringTool.setGlassPane(this);
        setVisible(true);
        blinkTimer.start();
        revalidate();
        repaint();
      }
    }
    else if (authoringTool.getJAliceFrame().getGlassPane() != savedGlassPane) {
      if (savedGlassPane == null) {
        savedGlassPane = authoringTool.getJAliceFrame().getGlassPane();
      }
      else {
        authoringTool.setGlassPane(savedGlassPane);
      }
      blinkTimer.stop();
    }
  }
  
  public Rectangle getHighlightRect()
  {
    Rectangle r = null;
    
    if (highlightID != null) {
      try {
        if (!authoringTool.isIDVisible(highlightID)) {
          authoringTool.makeIDVisible(highlightID);
        }
        
        r = authoringTool.getBoxForID(highlightID);
        if ((r != null) && (!r.isEmpty())) {
          x -= 2;
          y -= 2;
          width += 4;
          height += 4;
        }
      }
      catch (IDDoesNotExistException localIDDoesNotExistException) {}
    }
    

    return r;
  }
  
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    Rectangle r = getHighlightRect();
    if ((r != null) && (!r.isEmpty())) {
      g.setColor(highlightColor);
      
      for (int i = 0; i < 4; i++)
        g.drawRect(x - i, y - i, width + 2 * i, height + 2 * i);
    }
  }
  
  class MousePassThroughListener extends MouseInputAdapter {
    MousePassThroughListener() {}
    
    public void mouseMoved(MouseEvent ev) {
      redispatchMouseEvent(ev);
    }
    
    public void mouseDragged(MouseEvent ev) {
      redispatchMouseEvent(ev);
    }
    
    public void mouseClicked(MouseEvent ev) {
      redispatchMouseEvent(ev);
    }
    
    public void mouseEntered(MouseEvent ev) {
      redispatchMouseEvent(ev);
    }
    
    public void mouseExited(MouseEvent ev) {
      redispatchMouseEvent(ev);
    }
    
    public void mousePressed(MouseEvent ev) {
      redispatchMouseEvent(ev);
    }
    
    public void mouseReleased(MouseEvent ev) {
      redispatchMouseEvent(ev);
    }
    
    private void redispatchMouseEvent(MouseEvent ev) {
      authoringTool.handleMouseEvent(ev);
    }
  }
}

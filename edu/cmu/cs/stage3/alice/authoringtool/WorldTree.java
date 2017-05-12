package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;









public class WorldTree
  extends JTree
{
  protected Point cursorLocation;
  protected boolean dropLinesActive;
  protected boolean showDropLines = false;
  protected int totalChildIndent;
  protected Insets insets;
  protected int depthOffset;
  protected int legBuffer = 8;
  protected TreePath fromPath = null;
  protected TreePath toPath = null;
  
  public WorldTree()
  {
    init();
  }
  
  public WorldTree(TreeModel model) {
    super(model);
    init();
  }
  
  private void init() {
    cursorLocation = new Point(0, 0);
    BasicTreeUI ui = (BasicTreeUI)getUI();
    totalChildIndent = (ui.getLeftChildIndent() + ui.getRightChildIndent());
    insets = getInsets();
    updateVars();
    dropLinesActive = true;
    needChange();
    dropLinesActive = false;
    getSelectionModel().setSelectionMode(1);
    
    setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Object_Tree") + "<p><p>" + Messages.getString("The_Object_Tree_shows_all_p_of_the_objects_in_the_world__p_Some_objects_have_parts_") + "</font></html>");
    ToolTipManager.sharedInstance().registerComponent(this);
  }
  
  public String getToolTipText(MouseEvent ev) {
    String tip = super.getToolTipText(ev);
    if (tip != null) {
      return tip;
    }
    return getToolTipText();
  }
  
  protected void paintLines(Graphics g, Rectangle clipBounds, Insets insets, TreePath from, TreePath to)
  {
    int lineX = (from.getPathCount() - 1 + depthOffset) * totalChildIndent + 8 + left;
    

    int clipLeft = 0;
    int clipRight = width - 1;
    
    if ((lineX > clipLeft) && (lineX < clipRight)) {
      int clipTop = 0;
      int clipBottom = height - 1;
      Rectangle fromBounds = getPathBounds(from);
      Rectangle toBounds = getPathBounds(to);
      
      int top;
      int top;
      if (fromBounds == null) {
        top = Math.max(top, clipTop);
      } else
        top = Math.max(y + height - legBuffer, clipTop);
      int bottom;
      int bottom; if (toBounds == null) {
        bottom = Math.min(bottom, clipBottom);
      } else {
        bottom = Math.min(y + height, clipBottom);
      }
      
      g.drawLine(lineX, top, lineX, bottom);
      g.drawLine(lineX, bottom, clipRight, bottom);
    }
  }
  
  public void setCursorLocation(Point p) {
    cursorLocation = p;
    if (needChange()) {
      repaint();
    }
    
    autoscrollIfNecessary();
  }
  
  protected synchronized boolean needChange()
  {
    if ((dropLinesActive) && (cursorLocation != null))
    {




      int lastRow = 0;
      while (getPathForRow(lastRow) != null)
      {
        lastRow++;
      }
      lastRow--;
      if ((lastRow > 0) && (getPathForRow(lastRow).equals(getPathForRow(0)))) {
        lastRow--;
      }
      

      Rectangle lastBounds = getRowBounds(lastRow);
      
      int bottomy = 0;
      if (lastBounds != null) {
        bottomy = y + height;
      }
      
      TreePath pathToDrawFrom;
      TreePath pathToDrawTo;
      TreePath pathToDrawFrom;
      if (cursorLocation.y > bottomy) {
        TreePath pathToDrawTo = getPathForRow(lastRow);
        pathToDrawFrom = new TreePath(getModel().getRoot());
      } else {
        int cursorLocationRow = getClosestRowForLocation(cursorLocation.x, cursorLocation.y);
        
        pathToDrawTo = getPathForRow(cursorLocationRow);
        

        Object toNode = pathToDrawTo.getLastPathComponent();
        TreePath pathToDrawFrom; if ((getModel().getChildCount(toNode) != 0) && (isExpanded(pathToDrawTo))) {
          pathToDrawFrom = pathToDrawTo;
        } else {
          pathToDrawFrom = pathToDrawTo;
          Rectangle bounds = getRowBounds(cursorLocationRow);
          
          if (y + height - cursorLocation.y < legBuffer) {
            int cursorLevel = (int)((cursorLocation.x - insets.left - 8 + totalChildIndent / 2.0D) / totalChildIndent);
            int maxLevel = pathToDrawTo.getPathCount() - 1;
            int minLevel;
            int minLevel; if (cursorLocationRow == lastRow) {
              minLevel = 1;
            } else {
              TreePath next = getPathForRow(cursorLocationRow + 1);
              minLevel = next.getPathCount() - 1;
            }
            cursorLevel = Math.min(cursorLevel, maxLevel);
            cursorLevel = Math.max(cursorLevel, minLevel);
            for (int depthDelta = maxLevel - cursorLevel + 1; depthDelta > 0; depthDelta--) {
              pathToDrawFrom = pathToDrawFrom.getParentPath();
            }
          }
        }
      }
      
      if ((!pathToDrawFrom.equals(fromPath)) || (!pathToDrawTo.equals(toPath))) {
        fromPath = pathToDrawFrom;
        toPath = pathToDrawTo;
        return true;
      }
    }
    
    return false;
  }
  
  public boolean getDropLinesActive() {
    return dropLinesActive;
  }
  
  public void setDropLinesActive(boolean a) {
    if (dropLinesActive != a) {
      dropLinesActive = a;
      if (a) {
        updateVars();
      }
      repaint();
    }
  }
  
  public boolean getShowDropLines() {
    return showDropLines;
  }
  
  public void setShowDropLines(boolean b) {
    if (showDropLines != b) {
      showDropLines = b;
      repaint();
    }
  }
  
  protected void updateVars() {
    updateDepthOffset();
    insets = getInsets();
  }
  
  protected void updateDepthOffset() {
    if (isRootVisible()) {
      if (getShowsRootHandles()) {
        depthOffset = 1;
      } else {
        depthOffset = 0;
      }
    } else if (!getShowsRootHandles()) {
      depthOffset = -1;
    } else {
      depthOffset = 0;
    }
  }
  
  public TreePath getParentPath() {
    return fromPath;
  }
  
  public TreePath[] getParentToPredecessorPaths() {
    return getPathBetweenRows(getRowForPath(fromPath), getRowForPath(toPath));
  }
  
  public void paintComponent(Graphics g) {
    try {
      super.paintComponent(g);
    } catch (NullPointerException e) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_painting_tree_"), e);
    }
    if ((dropLinesActive) && (showDropLines)) {
      paintLines(g, getBounds(), insets, fromPath, toPath);
    }
  }
  
  public synchronized void autoscrollIfNecessary() {
    Container parent = getParent();
    if ((parent instanceof JViewport)) {
      Rectangle viewRect = ((JViewport)parent).getViewRect();
      int desiredRow = -1;
      if (cursorLocation.y < y + 10) {
        desiredRow = getClosestRowForLocation(cursorLocation.x, cursorLocation.y) - 1;
      } else if (cursorLocation.y > y + height - 10) {
        desiredRow = getClosestRowForLocation(cursorLocation.x, cursorLocation.y) + 1;
      }
      
      int lastRow = getRowCount() - 1 - (isRootVisible() ? 0 : 1);
      if ((desiredRow > -1) && (desiredRow <= lastRow)) {
        scrollRowToVisible(desiredRow);
      }
    }
  }
}

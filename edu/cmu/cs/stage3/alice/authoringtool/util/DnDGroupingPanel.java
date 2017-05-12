package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener;
import edu.cmu.cs.stage3.awt.AWTUtilities;
import edu.cmu.cs.stage3.awt.SemitransparentWindow;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class DnDGroupingPanel extends GroupingPanel
{
  protected Transferable transferable;
  protected DnDGrip grip = new DnDGrip();
  protected DragSource dragSource = new DragSource();
  protected Point hotSpot = new Point(0, 0);
  protected java.awt.dnd.DragSourceListener dndManagerListener = DnDManager.getInternalListener();
  protected DragWindow dragWindow;
  protected SemitransparentWindow dragWindow2;
  protected Point dragOffset;
  protected int arcWidth = 12;
  protected int arcHeight = 10;
  protected GroupingPanelDragGestureListener dragGestureListener = new GroupingPanelDragGestureListener();
  protected LinkedList dragGestureRecognizers = new LinkedList();
  protected boolean dragEnabled = true;
  protected boolean drawFaded = false;
  protected java.awt.Composite defaultComposite = AlphaComposite.SrcOver;
  protected AlphaComposite alphaComposite = AlphaComposite.getInstance(3, 0.5F);
  protected boolean isSystemDefined = false;
  
  protected static Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  
  public DnDGroupingPanel() {
    setLayout(new java.awt.BorderLayout(2, 2));
    


    setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
    setOpaque(false);
    
    add(grip, "West");
    addDragSourceComponent(grip);
    addDragSourceComponent(this);
  }
  
  public Transferable getTransferable() {
    return transferable;
  }
  
  public void setTransferable(Transferable transferable) {
    this.transferable = transferable;
    if (transferable != null) {
      if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
        isSystemDefined = true;
      } else if ((AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) && (!AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor))) {
        isSystemDefined = true;
      } else if ((AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.QuestionPrototypeReferenceTransferable.questionPrototypeReferenceFlavor)) && (!AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor))) {
        isSystemDefined = true;
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.commonMathQuestionsFlavor)) {
        isSystemDefined = true;
      } else {
        isSystemDefined = false;
      }
    }
  }
  
  public boolean isDragEnabled() {
    return dragEnabled;
  }
  
  public void setDragEnabled(boolean b) {
    dragEnabled = b;
  }
  
  public void addDragSourceComponent(java.awt.Component component)
  {
    for (Iterator iter = dragGestureRecognizers.iterator(); iter.hasNext();) {
      DragGestureRecognizer dgr = (DragGestureRecognizer)iter.next();
      if (dgr.getComponent() == component) {
        return;
      }
    }
    if (dragSource != null) {
      dragGestureRecognizers.add(dragSource.createDefaultDragGestureRecognizer(component, 1073741827, dragGestureListener));
    } else {
      AuthoringTool.showErrorDialog("dragSource is null", null);
    }
  }
  
  public void removeDragSourceComponent(java.awt.Component component) {
    for (ListIterator iter = dragGestureRecognizers.listIterator(); iter.hasNext();) {
      DragGestureRecognizer dgr = (DragGestureRecognizer)iter.next();
      if (dgr.getComponent() == component) {
        dgr.removeDragGestureListener(dragGestureListener);
        dgr.setComponent(null);
        iter.remove();
        break;
      }
    }
  }
  
  public DragSource getDragSource() {
    return dragSource;
  }
  
  public void reset() {
    add(grip, "West");
    addDragSourceComponent(grip);
    addDragSourceComponent(this);
  }
  
  public void release() {
    super.release();
    













    for (Iterator iter = dragGestureRecognizers.listIterator(); iter.hasNext();) {
      DragGestureRecognizer dgr = (DragGestureRecognizer)iter.next();
      if (dragGestureListener != null) {
        dgr.removeDragGestureListener(dragGestureListener);
        iter.remove();
      }
      


      dgr.setComponent(null);
    }
  }
  
  public Image getImage()
  {
    Rectangle bounds = getBounds();
    BufferedImage image = new BufferedImage(width, height, 2);
    Graphics2D g = image.createGraphics();
    paintAll(g);
    return image;
  }
  





  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Object oldAntialiasing = null;
    if ((g instanceof Graphics2D)) {
      oldAntialiasing = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
      ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    }
    Rectangle bounds = getBounds();
    
    g.setColor(getBackground());
    g.fillRoundRect(0, 0, width, height, arcWidth, arcHeight);
    g.setColor(Color.lightGray);
    g.drawRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight);
    
    if ((g instanceof Graphics2D)) {
      ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
    }
  }
  
  public void paintForeground(Graphics g) {
    super.paintForeground(g);
  }
  





































  public void printComponent(Graphics g)
  {
    if (!authoringToolConfig.getValue("printing.fillBackground").equalsIgnoreCase("true")) {
      Object oldAntialiasing = null;
      if ((g instanceof Graphics2D)) {
        oldAntialiasing = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
      }
      Rectangle bounds = getBounds();
      
      g.setColor(Color.lightGray);
      g.drawRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight);
      
      if ((g instanceof Graphics2D)) {
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
      }
    } else {
      super.printComponent(g);
    } }
  
  public class GroupingPanelDragGestureListener implements java.awt.dnd.DragGestureListener { public GroupingPanelDragGestureListener() {}
    
    protected class DragListener extends java.awt.event.MouseAdapter implements java.awt.event.MouseMotionListener { protected DragListener() {}
      
      public void mouseReleased(MouseEvent ev) { drawFaded = false;
        repaint();
        if ((DnDGroupingPanel.authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true")) && (dragWindow2 != null)) {
          dragWindow2.hide();
          AWTUtilities.pumpMessageQueue();
        } else if (dragWindow != null) {
          dragWindow.setVisible(false);
        }
        AWTUtilities.removeMouseListener(this);
        AWTUtilities.removeMouseMotionListener(this);
        DnDManager.removeListener(dragSourceListener);
      }
      
      public void mouseDragged(MouseEvent ev) {
        Point p = ev.getPoint();
        x += 5;
        y += 5;
        if ((DnDGroupingPanel.authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true")) && (dragWindow2 != null)) {
          if (DnDGroupingPanel.authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("false")) {
            dragWindow2.hide();
          }
          else {
            dragWindow2.show();
          }
          dragWindow2.setLocationOnScreen(x, y);
          AWTUtilities.pumpMessageQueue();
        } else if (dragWindow != null) {
          if (DnDGroupingPanel.authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("false")) {
            dragWindow.setVisible(false);
          }
          else {
            dragWindow.setVisible(true);
          }
          dragWindow.setLocation(p);
        }
      }
      
      public void mouseMoved(MouseEvent ev) {} }
    
    protected DragListener dragListener = new DragListener();
    
    protected class DragSourceListener implements DnDManagerListener { protected DragSourceListener() {}
      
      public void dragEnter(DragSourceDragEvent ev) { DnDGroupingPanel.GroupingPanelDragGestureListener.this.updateImages(true); }
      


      public void dragExit(DragSourceEvent ev) { DnDGroupingPanel.GroupingPanelDragGestureListener.this.updateImages(false); }
      
      public void dragDropEnd(DragSourceDropEvent ev) {}
      
      public void dragOver(DragSourceDragEvent ev) {}
      
      public void dropActionChanged(DragSourceDragEvent ev) {}
      public void dragGestureRecognized(DragGestureEvent ev) {}
      public void dragStarted() {} }
    protected DragSourceListener dragSourceListener = new DragSourceListener();
    
    public void dragGestureRecognized(DragGestureEvent dge) {
      if (transferable != null) {
        DnDManager.fireDragGestureRecognized(dge);
        try {
          if (dragEnabled) {
            BufferedImage empty = new BufferedImage(1, 1, 1);
            dge.startDrag(DragSource.DefaultCopyDrop, empty, new Point(), transferable, DnDManager.getInternalListener());
            DnDManager.fireDragStarted(transferable, DnDGroupingPanel.this);
            
            if ((DnDGroupingPanel.authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("true")) && (AWTUtilities.mouseListenersAreSupported()) && (AWTUtilities.mouseMotionListenersAreSupported())) {
              if ((DnDGroupingPanel.authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true")) && (SemitransparentWindow.isSupported())) {
                if (dragWindow2 == null) {
                  dragWindow2 = new SemitransparentWindow();
                }
                dragWindow2.show();
              } else {
                if (dragWindow == null) {
                  dragWindow = new DragWindow(AuthoringTool.getHack().getJAliceFrame());
                }
                dragWindow.setLocation(55536, 55536);
                dragWindow.setVisible(true);
              }
              
              boolean scaledAndCropped = updateImages(false);
              
              if (scaledAndCropped) {
                dragOffset = new Point(3, 3);
              } else {
                dragOffset = dge.getDragOrigin();
                dragOffset = javax.swing.SwingUtilities.convertPoint(dge.getComponent(), dragOffset, DnDGroupingPanel.this);
              }
              AWTUtilities.addMouseListener(dragListener);
              AWTUtilities.addMouseMotionListener(dragListener);
              DnDManager.addListener(dragSourceListener);
            }
            
            drawFaded = true;
            repaint();
          }
        } catch (Throwable t) {
          AuthoringTool.showErrorDialog("Error initiating drag of tile.", t);
        }
      }
    }
    
    private boolean updateImages(boolean valid) {
      Image tileImage = getImage();
      boolean scaledAndCropped = false;
      
      if ((AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.responseReferenceFlavor)) || (AuthoringToolResources.safeIsDataFlavorSupported(transferable, AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class)))) {
        int width = tileImage.getWidth(GUIEffects.sizeObserver);
        int height = tileImage.getHeight(GUIEffects.sizeObserver);
        
        if ((width > 64) || (height > 64)) {
          double scaleFactor = 1.0D;
          Rectangle cropRect = new Rectangle(0, 0, 64, 64);
          if ((width > 128) && (height > 128)) {
            scaleFactor = 0.5D;
          } else if (height < 32) {
            cropRect = new Rectangle(0, 0, 128, height);
          } else if ((width > 128) || (height > 128)) {
            scaleFactor = Math.min(1.0D, 64.0D / Math.min(width, height));
          } else {
            cropRect = new Rectangle(0, 0, width, height);
          }
          tileImage = GUIEffects.getImageScaledAndCropped(tileImage, scaleFactor, cropRect);
          scaledAndCropped = true;
        }
      }
      
      if (valid) {
        tileImage = GUIEffects.getImageWithColoredBorder(tileImage, AuthoringToolResources.getColor("dndHighlight2"));
      } else {
        tileImage = GUIEffects.getImageWithColoredBorder(tileImage, AuthoringToolResources.getColor("dndHighlight3"));
      }
      
      if ((DnDGroupingPanel.authoringToolConfig.getValue("gui.useAlphaTiles").equalsIgnoreCase("true")) && (dragWindow2 != null)) {
        Image image = GUIEffects.getImageWithDropShadow(tileImage, 8, 8, arcWidth, arcHeight);
        try {
          dragWindow2.setImage(image);
        } catch (InterruptedException ie) {
          throw new RuntimeException();
        }
      } else if (dragWindow != null) {
        dragWindow.setImage(tileImage);
      }
      
      return scaledAndCropped;
    }
  }
  
  public class DnDGrip extends javax.swing.JComponent {
    protected Color highlightColor = MetalLookAndFeel.getControlHighlight();
    protected Color shadowColor = MetalLookAndFeel.getControlDarkShadow();
    
    public DnDGrip() {
      setMinimumSize(new Dimension(6, 0));
      setMaximumSize(new Dimension(6, Integer.MAX_VALUE));
      setPreferredSize(new Dimension(6, 0));
    }
    

    protected void printComponent(Graphics g) {}
    
    protected void paintComponent(Graphics g)
    {
      Dimension size = getSize();
      
      g.setColor(highlightColor);
      for (int x = 0; x < width; x += 4) {
        for (int y = 0; y < height; y += 4) {
          g.drawLine(x, y, x, y);
          g.drawLine(x + 2, y + 2, x + 2, y + 2);
        }
      }
      
      g.setColor(shadowColor);
      for (int x = 0; x < width; x += 4) {
        for (int y = 0; y < height; y += 4) {
          g.drawLine(x + 1, y + 1, x + 1, y + 1);
          g.drawLine(x + 3, y + 3, x + 3, y + 3);
        }
      }
    }
  }
}

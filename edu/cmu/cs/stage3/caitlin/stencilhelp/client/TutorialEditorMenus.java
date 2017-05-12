package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Double;
import java.util.Iterator;
import java.util.Vector;

public class TutorialEditorMenus implements StencilObject, StencilPanelMessageListener, MouseEventListener, StencilFocusListener, ReadWriteListener
{
  protected Vector shapes = new Vector();
  protected Vector stencilObjectPositionListeners = new Vector();
  protected StencilManager stencilManager = null;
  protected boolean isShowing = false;
  protected int level = 0;
  protected Point centerPoint;
  protected boolean writeEnabled = true;
  protected boolean isModified = true;
  protected Rectangle previousRect = null;
  protected Rectangle contextMenuRect = null;
  private RoundRectangle2D newNoteOption = null;
  private RoundRectangle2D newNoteNextOption = null;
  private RoundRectangle2D newReferenceNote = null;
  private RoundRectangle2D newHoleNote = null;
  private RoundRectangle2D newHoleNoteAutoAdvance = null;
  private RoundRectangle2D newReferenceNextNote = null;
  private static int fontSize = 13;
  private static Font font = new Font("SansSerif", 0, fontSize);
  private static final int MENU_WIDTH = 350;
  protected static Vector editorShapes = new Vector();
  protected static Vector<RoundRectangle2D> editorRects = new Vector();
  private static RoundRectangle2D.Double editorMenuTitle;
  private static RoundRectangle2D.Double editorMenuNewNote;
  private static RoundRectangle2D.Double editorMenuSave;
  private static RoundRectangle2D.Double editorMenuNew;
  private static RoundRectangle2D.Double editorMenuOpen;
  private static RoundRectangle2D.Double editorMenuNewFrame;
  private static RoundRectangle2D.Double editorMenuNewPage;
  private static RoundRectangle2D.Double editorMenuClearPage;
  private static RoundRectangle2D.Double editorMenuRemovePage;
  private static RoundRectangle2D.Double editorMenuRemoveLastNote;
  private static RoundRectangle2D.Double editorMenuExitPage;
  static Color defaultShapeBGColor = new Color(220, 220, 220, 220);
  static Color defaultTextColor = new Color(0, 0, 255);
  static final double padX = 7.0D;
  static final double padY = 7.0D;
  
  public TutorialEditorMenus(StencilManager paramStencilManager)
  {
    stencilManager = paramStencilManager;
  }
  
  public RoundRectangle2D.Double createEditingShapeContextMenu(String paramString, Point paramPoint)
  {
    Shape localShape = null;
    Color localColor1 = new Color(255, 200, 240, 100);
    Color localColor2 = new Color(220, 220, 220, 220);
    Color localColor3 = new Color(0, 0, 255);
    TextLayout localTextLayout = new TextLayout(paramString, font, new FontRenderContext(null, false, false));
    AffineTransform localAffineTransform = new AffineTransform();
    double d1 = localTextLayout.getBounds().getWidth();
    double d2 = localTextLayout.getBounds().getHeight();
    localAffineTransform.translate(x, y + 10);
    localShape = localTextLayout.getOutline(localAffineTransform);
    RoundRectangle2D.Double localDouble = new RoundRectangle2D.Double(x - 3.5D, y - 3.5D, d1 + 7.0D, d2 + 7.0D, 10.0D, 10.0D);
    shapes.add(new ScreenShape(localColor1, localDouble, true, shapes.size() + 1));
    shapes.add(new ScreenShape(localColor2, localDouble, true, shapes.size() + 1));
    shapes.add(new ScreenShape(localColor3, localShape, true, shapes.size() + 1));
    return localDouble;
  }
  
  protected void updateContextMenuShapes(Point paramPoint)
  {
    centerPoint = paramPoint;
    Point2D.Double localDouble = Note.translatePositionToVisiblePart(new java.awt.geom.Rectangle2D.Double(x, y, 240.0D, 190.0D));
    paramPoint = new Point(x + (int)localDouble.getX(), y + (int)localDouble.getY());
    previousRect = getRectangle();
    int i = (int)(fontSize * 1.5D);
    shapes.removeAllElements();
    if (writeEnabled)
    {
      Point localPoint = paramPoint.getLocation();
      newNoteNextOption = createEditingShapeContextMenu("[ new note with 'next' ]", localPoint);
      localPoint.translate(0, i);
      newNoteOption = createEditingShapeContextMenu("[ new note ]", localPoint);
      localPoint.translate(0, i);
      newReferenceNextNote = createEditingShapeContextMenu("[ new reference note with 'next' ]", localPoint);
      localPoint.translate(0, i);
      newReferenceNote = createEditingShapeContextMenu("[ new reference note ]", localPoint);
      localPoint.translate(0, i);
      newHoleNoteAutoAdvance = createEditingShapeContextMenu("[ new note with hole - auto advance ]", localPoint);
      localPoint.translate(0, i);
      newHoleNote = createEditingShapeContextMenu("[ new note with hole ]", localPoint);
      previousRect = contextMenuRect;
      contextMenuRect = new Rectangle((int)newNoteOption.getX(), (int)newNoteOption.getY(), (int)newNoteOption.getWidth(), (int)newNoteOption.getHeight()).union(new Rectangle((int)newHoleNote.getX(), (int)newHoleNote.getY(), (int)newHoleNote.getWidth(), (int)newHoleNote.getHeight()));
    }
    else
    {
      previousRect = contextMenuRect;
      contextMenuRect = new Rectangle(x - 40, y - 15, x + 25, y + 50);
    }
    isModified = true;
  }
  
  public static RoundRectangle2D.Double createEditingShape(String paramString, Point paramPoint)
  {
    return createEditingShape(paramString, paramPoint, defaultShapeBGColor, defaultTextColor);
  }
  
  public static RoundRectangle2D.Double createEditingShape(String paramString, Point paramPoint, Color paramColor1, Color paramColor2)
  {
    Shape localShape = null;
    Color localColor1 = new Color(255, 200, 240, 100);
    Color localColor2 = paramColor1 != null ? paramColor1 : defaultShapeBGColor;
    Color localColor3 = paramColor2 != null ? paramColor2 : defaultTextColor;
    TextLayout localTextLayout = new TextLayout(paramString, font, new FontRenderContext(null, false, false));
    AffineTransform localAffineTransform = new AffineTransform();
    double d1 = localTextLayout.getBounds().getWidth();
    double d2 = localTextLayout.getBounds().getHeight();
    localAffineTransform.translate(x, y + 10);
    localShape = localTextLayout.getOutline(localAffineTransform);
    RoundRectangle2D.Double localDouble = new RoundRectangle2D.Double(x - 3.5D, y - 3.5D, d1 + 7.0D, d2 + 7.0D, 10.0D, 10.0D);
    editorShapes.add(new ScreenShape(localColor1, localDouble, true, editorShapes.size() + 1));
    editorShapes.add(new ScreenShape(localColor2, localDouble, true, editorShapes.size() + 1));
    editorShapes.add(new ScreenShape(localColor3, localShape, true, editorShapes.size() + 1));
    editorRects.add(localDouble);
    return localDouble;
  }
  
  private static int prevItemWidth(RoundRectangle2D.Double paramDouble)
  {
    return (int)(x + width) + 15;
  }
  
  public static void generateEditorMenu()
  {
    double d = AuthoringTool.getHack().getScreenSize().getWidth();
    Point localPoint = new Point((int)(d - 350.0D), 5);
    editorMenuTitle = createEditingShape("Instructor Mode", localPoint, null, Color.black);
    int i = (int)(editorMenuTitley + editorMenuTitleheight + 7.0D);
    editorMenuOpen = createEditingShape("Open", new Point(x, i));
    editorMenuNew = createEditingShape("New", new Point(prevItemWidth(editorMenuOpen), i));
    editorMenuSave = createEditingShape("Save", new Point(prevItemWidth(editorMenuNew), i));
    editorMenuExitPage = createEditingShape("Exit Editor", new Point(prevItemWidth(editorMenuSave), i));
    int j = (int)(editorMenuOpeny + editorMenuOpenheight + 7.0D);
    editorMenuNewPage = createEditingShape("New Page", new Point(x, j));
    editorMenuClearPage = createEditingShape("Clear Page", new Point(prevItemWidth(editorMenuNewPage), j));
    editorMenuRemovePage = createEditingShape("Remove Page", new Point(prevItemWidth(editorMenuClearPage), j));
    int k = (int)(editorMenuNewPagey + editorMenuNewPageheight + 7.0D);
    RoundRectangle2D.Double localDouble = createEditingShape("(Right-click to add new notes)", new Point(x, k), null, Color.black);
    editorMenuRemoveLastNote = createEditingShape("Remove Last Note", new Point(prevItemWidth(localDouble), k));
  }
  
  public static Vector getEditorShapes()
  {
    return editorShapes;
  }
  
  protected Shape createWordShape(String paramString, Point paramPoint)
  {
    TextLayout localTextLayout = new TextLayout(paramString, font, new FontRenderContext(null, false, false));
    AffineTransform localAffineTransform = new AffineTransform();
    localAffineTransform.translate(x, y);
    Shape localShape = localTextLayout.getOutline(localAffineTransform);
    return localShape;
  }
  
  public Vector getShapes()
  {
    if (isShowing)
      return shapes;
    return null;
  }
  
  public Rectangle getRectangle()
  {
    if (isShowing)
      return contextMenuRect;
    return null;
  }
  
  public Rectangle getPreviousRectangle()
  {
    Rectangle localRectangle = previousRect;
    return localRectangle;
  }
  
  public boolean isModified()
  {
    if (isModified)
    {
      isModified = false;
      return true;
    }
    return false;
  }
  
  public boolean intersectsRectangle(Rectangle paramRectangle)
  {
    Rectangle localRectangle = getRectangle();
    if (localRectangle != null)
      return paramRectangle.intersects(localRectangle);
    return false;
  }
  
  public void addStencilObjectPositionListener(StencilObjectPositionListener paramStencilObjectPositionListener)
  {
    stencilObjectPositionListeners.addElement(paramStencilObjectPositionListener);
  }
  
  public void removeStencilObjectPositionListener(StencilObjectPositionListener paramStencilObjectPositionListener)
  {
    stencilObjectPositionListeners.remove(paramStencilObjectPositionListener);
  }
  
  public String getComponentID()
  {
    return null;
  }
  
  public void messageReceived(int paramInt, Object paramObject)
  {
    if (writeEnabled)
    {
      isShowing = true;
      level = 0;
      updateContextMenuShapes((Point)paramObject);
      stencilManager.requestFocus(this);
    }
  }
  
  public boolean contains(Point paramPoint)
  {
    if (writeEnabled)
    {
      Iterator localIterator = editorRects.iterator();
      while (localIterator.hasNext())
      {
        RoundRectangle2D localRoundRectangle2D = (RoundRectangle2D)localIterator.next();
        if (localRoundRectangle2D.contains(paramPoint))
          return true;
      }
      if (isShowing)
        return true;
    }
    return false;
  }
  
  public boolean mousePressed(MouseEvent paramMouseEvent)
  {
    return false;
  }
  
  public boolean mouseReleased(MouseEvent paramMouseEvent)
  {
    return false;
  }
  
  public boolean mouseClicked(MouseEvent paramMouseEvent)
  {
    isShowing = false;
    int i = 0;
    if (writeEnabled)
    {
      previousRect = contextMenuRect;
      if (newNoteOption != null)
        if (newNoteOption.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.createNewNote(centerPoint, false);
          i = 1;
        }
        else if (newNoteNextOption.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.createNewNote(centerPoint, true);
          i = 1;
        }
        else if (newReferenceNote.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.createNewFrame(centerPoint, false);
          i = 1;
        }
        else if (newReferenceNextNote.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.createNewFrame(centerPoint, true);
          i = 1;
        }
        else if (newHoleNote.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.createNewHole(centerPoint, false);
          i = 1;
        }
        else if (newHoleNoteAutoAdvance.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.createNewHole(centerPoint, true);
          i = 1;
        }
      if ((i == 0) && (stencilManager.isInstructorMode()))
        if (editorMenuOpen.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.loadStencilsFile();
          stencilManager.setWriteEnabled(true);
        }
        else if (editorMenuNew.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.newTutorial();
          stencilManager.setWriteEnabled(true);
        }
        else if (editorMenuSave.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.saveStencilsFile();
        }
        else if (editorMenuNewPage.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.insertNewStencil(true);
        }
        else if (editorMenuClearPage.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.removeAllObjectsFromCurrentStencil();
        }
        else if (editorMenuRemovePage.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.removeCurrStencil();
        }
        else if (editorMenuRemoveLastNote.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.removeLastStencilObject();
        }
        else if (editorMenuExitPage.contains(paramMouseEvent.getPoint()))
        {
          stencilManager.setInstructorMode(false);
        }
      isModified = true;
    }
    else
    {
      previousRect = contextMenuRect;
      isModified = true;
    }
    return true;
  }
  
  public boolean mouseEntered(MouseEvent paramMouseEvent)
  {
    return false;
  }
  
  public boolean mouseExited(MouseEvent paramMouseEvent)
  {
    return false;
  }
  
  public boolean mouseMoved(MouseEvent paramMouseEvent)
  {
    return false;
  }
  
  public boolean mouseDragged(MouseEvent paramMouseEvent)
  {
    return false;
  }
  
  public void focusGained()
  {
    isShowing = true;
  }
  
  public void focusLost()
  {
    isShowing = false;
    previousRect = contextMenuRect;
    isModified = true;
  }
  
  public void setWriteEnabled(boolean paramBoolean)
  {
    writeEnabled = paramBoolean;
  }
  
  static
  {
    generateEditorMenu();
  }
}

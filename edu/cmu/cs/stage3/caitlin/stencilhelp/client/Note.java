package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.RoundRectangle2D.Double;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Note implements StencilObject, MouseEventListener, KeyEventListener, StencilObjectPositionListener, ReadWriteListener, LayoutChangeListener
{
  protected Vector shapes = new Vector();
  protected Paragraph paragraph = null;
  protected Vector stencilObjectPositionListeners = new Vector();
  protected ObjectPositionManager positionManager = null;
  protected StencilManager stencilManager = null;
  protected Point clickPos = null;
  protected Point initialPos = null;
  protected StencilObject scrObject = null;
  private RoundRectangle2D.Double bgRect = null;
  private RoundRectangle2D.Double underRect = null;
  private RoundRectangle2D.Double tabRect = null;
  private Line2D.Double line = null; private Line2D.Double line2 = null; private Line2D.Double line3 = null; private Line2D.Double line4 = null; private Line2D.Double line5 = null;
  private Polygon triangle = null;
  protected boolean writeEnabled = true;
  

  protected Point startClick = null;
  protected Point startInitial = null;
  
  private boolean hasNext = false;
  private RoundRectangle2D.Double bgNext = null;
  private RoundRectangle2D.Double underNext = null;
  private ScreenShape nextShape = null;
  private Font font = new Font("Arial", 1, 16);
  
  protected boolean isModified = true;
  protected Rectangle rect = null;
  protected Rectangle previousRect = null;
  private boolean prevBlank = false;
  
  private ScreenShape currentShape = null;
  private boolean startNewLine = false;
  protected boolean isInitialized = false;
  
  private Point startDragging = null;
  private Point currentDragPosition = null;
  private ScreenShape moveRect = null;
  private int dragInProgress = 0;
  
  private int fontSize = 20;
  private int dropOffset = 4;
  
  private int rectWidth = 240;
  private int rectHeight = 190;
  private int noteOpacity = 255;
  private int tabRectOffset = 15;
  private int tabRectWidth = 20;
  private int tabRectHeight = 30;
  
  private String noteNumber = "";
  private Color defaultColor = new Color(18, 21, 116);
  private Color accentColor = new Color(48, 52, 221);
  
  public Note(Point clickPos, Point initPos, StencilObject scrObject, ObjectPositionManager positionManager, StencilManager stencilManager, boolean hasNext)
  {
    this.positionManager = positionManager;
    this.stencilManager = stencilManager;
    this.clickPos = clickPos;
    startClick = clickPos;
    initialPos = initPos;
    startInitial = initPos;
    this.scrObject = scrObject;
    this.hasNext = hasNext;
  }
  
  public void addText(String text, String color) {
    if (text.length() > 0) {
      char c = text.charAt(0);
      if (Character.isDigit(c)) {
        noteNumber = new Character(c).toString();
        text = text.substring(1);
      }
      if (paragraph == null) {
        paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
      }
      if ((color != null) && (color.equals("blue"))) {
        paragraph.addText(text, accentColor);
      } else {
        paragraph.addText(text, defaultColor);
      }
      if (isInitialized) {
        generateShapes();
        setModified();
      }
    }
  }
  
  public void setText(Vector msgs) {
    paragraph.clearText();
    for (int i = 0; i < msgs.size(); i++) {
      String text = (String)msgs.elementAt(i);
      if (text.length() > 0) {
        char c = text.charAt(0);
        if (Character.isDigit(c)) {
          noteNumber = new Character(c).toString();
          text = text.substring(1);
        }
        paragraph.addText(text, defaultColor);
        if (isInitialized) {
          generateShapes();
          setModified();
        }
      }
    }
  }
  
  protected void drawArrow(Point holePoint, Point wordPoint) {
    double delX = wordPoint.getX() - holePoint.getX();
    double delY = wordPoint.getY() - holePoint.getY();
    double distance = Math.sqrt(delX * delX + delY * delY);
    delX /= distance;
    delY /= distance;
    
    Point basePt = new Point((int)(holePoint.getX() + 20.0D * delX), (int)(holePoint.getY() + 20.0D * delY));
    Point side1Pt = new Point((int)(basePt.getX() + 10.0D * delY), (int)(basePt.getY() - 10.0D * delX));
    Point side2Pt = new Point((int)(basePt.getX() - 10.0D * delY), (int)(basePt.getY() + 10.0D * delX));
    
    Point junkPt = new Point((int)(holePoint.getX() + 20.0D), (int)holePoint.getY());
    
    triangle = new Polygon();
    triangle.addPoint((int)holePoint.getX(), (int)holePoint.getY());
    triangle.addPoint((int)side2Pt.getX(), (int)side2Pt.getY());
    triangle.addPoint((int)side1Pt.getX(), (int)side1Pt.getY());
  }
  

  public void updatePosition()
  {
    if (stencilManager.writeEnabled) {
      if ((!isInitialized) && (scrObject == null)) {
        initializeNote();
      } else if ((!isInitialized) && (scrObject != null) && (scrObject.getRectangle() != null)) {
        initializeNote();
      }
    }
    else if (scrObject == null) {
      initializeNote();
    } else if ((scrObject != null) && (scrObject.getRectangle() != null)) {
      initializeNote();
    }
    

    if ((isInitialized) && (scrObject != null)) {
      if ((scrObject instanceof Hole)) {
        clickPos = ((Hole)scrObject).getNotePoint();
      } else if ((scrObject instanceof Frame)) {
        clickPos = ((Frame)scrObject).getNotePoint();
      } else if ((scrObject instanceof NavigationBar)) {
        clickPos = ((NavigationBar)scrObject).getNotePoint();
      }
      if (scrObject != null) {
        Point holePoint = getHolesPoint();
        clickPos = new Point(x + initialPos.x, y + initialPos.y);
      } else {
        clickPos = new Point(clickPos.x + initialPos.x, clickPos.y + initialPos.y);
      }
      underRect.setFrame(clickPos.x + dropOffset, clickPos.y - fontSize + dropOffset, bgRect.width, bgRect.height);
      bgRect.setFrame(clickPos.x, clickPos.y - fontSize, bgRect.width, bgRect.height);
      if (tabRect != null) tabRect.setFrame(clickPos.x - tabRectOffset, clickPos.y, tabRectWidth, tabRectHeight);
      if (hasNext) {
        underNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60.0D + 2.0D, bgRect.getY() + bgRect.getHeight() + 2.0D, underNext.getWidth(), underNext.getHeight());
        bgNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60.0D, bgRect.getY() + bgRect.getHeight(), bgNext.getWidth(), bgNext.getHeight());
      }
      if (paragraph != null) paragraph.setTextOrigin(new Point(clickPos.x + 10, clickPos.y));
      if (line != null) {
        Point holePoint = getHolesPoint();
        
        Point wordPoint = getWordsPoint();
        if ((holePoint != null) && (wordPoint != null))
        {

          line.setLine(holePoint, wordPoint);
          line2.setLine(x - 1, y, x - 1, y);
          line3.setLine(x + 1, y, x + 1, y);
          line4.setLine(x - 2, y, x - 2, y);
          line5.setLine(x + 2, y, x + 2, y);
        }
      }
    }
    if (isInitialized) { generateShapes();
    }
  }
  
  public static java.awt.geom.Point2D.Double translatePositionToVisiblePart(Rectangle2D.Double paramDouble)
  {
    double d1 = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    double d2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    Rectangle2D.Double localDouble1 = new Rectangle2D.Double(0.0D, 0.0D, d1, d2);
    Rectangle2D.Double localDouble2 = new Rectangle2D.Double(0.0D, 0.0D, d1, d2);
    
    localDouble2 = new Rectangle2D.Double(localDouble2.getX() - 15.0D, localDouble2.getY(), localDouble2.getWidth() + 30.0D, localDouble2.getHeight());
    double d3 = paramDouble.getX();
    double d4 = paramDouble.getY();
    if (localDouble2.intersects(paramDouble))
    {
      double d5 = Math.abs(paramDouble.getX() - localDouble2.getX());
      double d6 = Math.abs(paramDouble.getX() - (x + width));
      double d7 = Math.abs(paramDouble.getY() + paramDouble.getHeight() - y);
      if (Math.min(d5, d6) < d7)
      {
        if (d5 < d6) {
          d3 = x - paramDouble.getWidth();
        } else {
          d3 = x + width;
        }
      } else
        d4 = y + height;
    }
    Rectangle2D.Double localDouble3 = new Rectangle2D.Double(d3, d4, paramDouble.getWidth(), paramDouble.getHeight());
    if (!localDouble1.contains(localDouble3))
    {
      if (localDouble3.getX() < 0.0D)
        d3 = 0.0D;
      if (localDouble3.getX() + localDouble3.getWidth() > d1)
        d3 = (int)d1 - localDouble3.getWidth();
      if (localDouble3.getY() + localDouble3.getHeight() > d2)
        d4 = (int)d2 - localDouble3.getHeight();
    }
    return new java.awt.geom.Point2D.Double(d3 - paramDouble.getX(), d4 - paramDouble.getY());
  }
  
  public String getFirstNote() {
    return paragraph != null ? (String)paragraph.getText().get(0) : "";
  }
  
  protected Rectangle getBoundingRectangle()
  {
    double minX = 0.0D;
    double minY = 0.0D;
    double maxX = 0.0D;
    double maxY = 0.0D;
    
    Rectangle area = bgRect.getBounds().union(underRect.getBounds());
    if (tabRect != null) area = area.union(tabRect.getBounds());
    if (line != null) {
      area = area.union(new Rectangle(line.getBounds()));
      if (triangle != null) area = area.union(triangle.getBounds());
    }
    if (moveRect != null) {
      Rectangle movingRect = moveRect.getShape().getBounds();
      movingRect.grow(10, 10);
      area = area.union(movingRect);
    }
    return area;
  }
  
  protected void setModified() {
    if (isInitialized) {
      if ((isModified) && (previousRect != null)) {
        previousRect = previousRect.union(rect);
        if (tabRect != null) previousRect = previousRect.union(tabRect.getBounds());
        if ((hasNext) && (bgNext != null)) previousRect = previousRect.union(bgNext.getBounds());
      } else {
        previousRect = rect;
        if (previousRect != null) {
          previousRect.grow(20, 20);
        }
        if ((hasNext) && (bgNext != null) && (previousRect != null)) {
          previousRect = previousRect.union(bgNext.getBounds());
        }
        if (tabRect != null) previousRect = previousRect.union(tabRect.getBounds());
      }
      rect = getBoundingRectangle();
    }
    isModified = true;
  }
  
  public void initializeNote()
  {
    if (!stencilManager.writeEnabled) {
      clickPos = startClick;
      initialPos = startInitial;
      shapes.removeAllElements();
      
      if (paragraph != null) {
        paragraph.setTextOrigin(new Point(clickPos.x + 10, clickPos.y));
      }
    }
    

    if (scrObject != null) {
      Point holePoint = getHolesPoint(initialPos);
      clickPos = new Point(x + initialPos.x, y + initialPos.y);
      underRect = new RoundRectangle2D.Double(clickPos.x + dropOffset, clickPos.y - fontSize + dropOffset, rectWidth, rectHeight, 10.0D, 10.0D);
      Color transGray = new Color(75, 75, 75, 100);
      ScreenShape sShape = new ScreenShape(transGray, underRect, true, 0);
      shapes.addElement(sShape);
      
      bgRect = new RoundRectangle2D.Double(clickPos.x, clickPos.y - fontSize, rectWidth, rectHeight, 10.0D, 10.0D);
      sShape = new ScreenShape(new Color(255, 255, 150, noteOpacity), bgRect, true, 1);
      shapes.addElement(sShape);
    } else {
      clickPos = new Point(clickPos.x + initialPos.x, clickPos.y + initialPos.y);
      underRect = new RoundRectangle2D.Double(clickPos.x + dropOffset, clickPos.y - fontSize + dropOffset, rectWidth, rectHeight, 10.0D, 10.0D);
      Color transGray = new Color(75, 75, 75, 100);
      ScreenShape sShape = new ScreenShape(transGray, underRect, true, 0);
      shapes.addElement(sShape);
      
      bgRect = new RoundRectangle2D.Double(clickPos.x, clickPos.y - fontSize, rectWidth, rectHeight, 10.0D, 10.0D);
      sShape = new ScreenShape(new Color(255, 255, 150, noteOpacity), bgRect, true, 1);
      shapes.addElement(sShape);
    }
    if (hasNext) {
      underNext = new RoundRectangle2D.Double(bgRect.getX() + bgRect.getWidth() - 60.0D + 2.0D, bgRect.getY() + bgRect.getHeight() + 2.0D, 60.0D, 20.0D, 10.0D, 10.0D);
      ScreenShape sShape = new ScreenShape(new Color(255, 200, 240, 100), underNext, true, 2);
      shapes.addElement(sShape);
      
      bgNext = new RoundRectangle2D.Double(bgRect.getX() + bgRect.getWidth() - 60.0D, bgRect.getY() + bgRect.getHeight(), 60.0D, 20.0D, 10.0D, 10.0D);
      sShape = new ScreenShape(new Color(255, 180, 210, 220), bgNext, true, 3);
      shapes.addElement(sShape);
    }
    

    if (scrObject != null) {
      Point holePt = getHolesPoint();
      Point wordsPt = getWordsPoint();
      if ((holePt != null) && (wordsPt != null)) {
        line = new Line2D.Double(holePt, wordsPt);
        line2 = new Line2D.Double(x - 1, y, x - 1, y);
        line3 = new Line2D.Double(x + 1, y, x + 1, y);
        line4 = new Line2D.Double(x - 2, y, x - 2, y);
        line5 = new Line2D.Double(x + 2, y, x + 2, y);
        ScreenShape sShape = new ScreenShape(Color.red, line, false, 2);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line2, false, 3);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line3, false, 4);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line4, false, 5);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line5, false, 6);
        shapes.addElement(sShape);
      } else {
        line = new Line2D.Double(0.0D, 0.0D, 0.0D, 0.0D);
        line2 = new Line2D.Double(0.0D, 0.0D, 0.0D, 0.0D);
        line3 = new Line2D.Double(0.0D, 0.0D, 0.0D, 0.0D);
        line4 = new Line2D.Double(0.0D, 0.0D, 0.0D, 0.0D);
        line5 = new Line2D.Double(0.0D, 0.0D, 0.0D, 0.0D);
        ScreenShape sShape = new ScreenShape(Color.red, line, false, 2);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line2, false, 3);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line3, false, 4);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line4, false, 5);
        shapes.addElement(sShape);
        sShape = new ScreenShape(Color.red, line5, false, 6);
        shapes.addElement(sShape);
      }
    }
    isInitialized = true;
    setModified();
  }
  
  protected void generateShapes() {
    int index = 0;
    if (scrObject != null)
      index = 7; else {
      index = 2;
    }
    if (hasNext) { index += 2;
    }
    Vector newShapes = new Vector();
    for (int i = 0; i < index; i++) {
      newShapes.addElement(shapes.elementAt(i));
    }
    shapes = newShapes;
    
    if ((scrObject != null) && (getHolesPoint() != null) && (getWordsPoint() != null)) {
      drawArrow(getHolesPoint(), getWordsPoint());
      ScreenShape sShape = new ScreenShape(Color.red, triangle, true, index);
      shapes.addElement(sShape);
      index++;
    }
    
    if (noteNumber.length() > 0) {
      tabRect = new RoundRectangle2D.Double(bgRect.getX() - tabRectOffset, bgRect.getY(), tabRectWidth, tabRectHeight, 15.0D, 15.0D);
      ScreenShape sShape = new ScreenShape(new Color(255, 255, 150, noteOpacity), tabRect, true, index);
      shapes.addElement(sShape);
      index++;
    }
    
    if (hasNext) {
      TextLayout wordLayout = new TextLayout("next", font, new java.awt.font.FontRenderContext(null, false, false));
      AffineTransform textAt = new AffineTransform();
      textAt.translate(bgRect.getX() + bgRect.getWidth() - 50.0D, bgRect.getY() + bgRect.getHeight() + 15.0D);
      Shape s = wordLayout.getOutline(textAt);
      nextShape = new ScreenShape(new Color(0, 0, 180), s, true, 8);
      shapes.addElement(nextShape);
      index++;
    }
    
    if (noteNumber.length() == 0) noteNumber = "-1";
    TextLayout tl = new TextLayout(noteNumber, new Font("Comic Sans MS", 1, fontSize), new java.awt.font.FontRenderContext(null, false, false));
    AffineTransform at = new AffineTransform();
    at.translate(bgRect.getX() - 10.0D, bgRect.getY() + 20.0D);
    Shape s = tl.getOutline(at);
    ScreenShape sShape = new ScreenShape(Color.blue, s, true, index);
    if (noteNumber.equals("-1")) {
      noteNumber = "";
    } else {
      shapes.addElement(sShape);
      index++;
    }
    
    if (paragraph != null) {
      Vector wordShapes = paragraph.getShapes();
      for (int i = 0; i < wordShapes.size(); i++) {
        ScreenShape wShape = (ScreenShape)wordShapes.elementAt(i);
        shapes.addElement(new ScreenShape(wShape.getColor(), wShape.getShape(), wShape.getIsFilled(), index));
        index++;
      }
    }
    
    if ((paragraph != null) && 
      (writeEnabled)) {
      Shape caret = paragraph.getCaretShape();
      if (caret != null) {
        shapes.addElement(new ScreenShape(Color.red, caret, false, index));
        index++;
      }
    }
    

    if (moveRect != null) {
      shapes.addElement(moveRect);
    }
  }
  
  protected Point getClosestPoint(Rectangle rect, Point center) {
    if (rect == null) return new Point(0, 0);
    if (Math.abs(y - y) > Math.abs(y + height - y))
      return new Point(x + width / 2, y + height + 1);
    return new Point(x + width / 2, y + 1);
  }
  
  protected Point getHolesPoint(Point offset) {
    if (scrObject != null) {
      Rectangle rect = scrObject.getRectangle();
      if (rect != null) {
        if (offset.getY() > 0.0D) {
          return new Point(x + width / 2, y + height + 1);
        }
        return new Point(x + width / 2, y + 1);
      }
      return new Point(0, 0); }
    return new Point(0, 0);
  }
  
  protected Point getHolesPoint() {
    return getClosestPoint(scrObject.getRectangle(), new Point((int)bgRect.getCenterX(), (int)bgRect.getCenterY()));
  }
  
  protected Point getWordsPoint() {
    Rectangle holeRect = scrObject.getRectangle();
    if (holeRect != null)
      return getClosestPoint(bgRect.getBounds(), new Point((int)holeRect.getCenterX(), (int)holeRect.getCenterY()));
    return null;
  }
  
  public Vector getShapes()
  {
    if ((scrObject != null) && (scrObject.getRectangle() == null)) {
      prevBlank = true;
      return null;
    }
    prevBlank = false;
    return shapes;
  }
  
  public Rectangle getRectangle() {
    return rect;
  }
  
  public Rectangle getPreviousRectangle() { return previousRect; }
  
  public boolean isModified() {
    if (isModified) {
      isModified = false;
      
      return true; }
    return false;
  }
  
  public boolean intersectsRectangle(Rectangle testRect) { if (rect != null)
      return rect.intersects(testRect);
    return false;
  }
  
  public void addStencilObjectPositionListener(StencilObjectPositionListener posListener) { stencilObjectPositionListeners.addElement(posListener); }
  
  public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener) {
    stencilObjectPositionListeners.remove(posListener);
  }
  
  public String getComponentID() { return null; }
  

  public boolean contains(Point point)
  {
    if (bgNext != null) return (bgRect.contains(point.getX(), point.getY())) || (bgNext.contains(point.getX(), point.getY()));
    if (bgRect != null) return bgRect.contains(point.getX(), point.getY());
    return false;
  }
  
  public boolean mousePressed(MouseEvent e) { if ((dragInProgress == 0) && (bgRect.contains(e.getX(), e.getY()))) {
      startDragging = e.getPoint();
      RoundRectangle2D.Double rect = new RoundRectangle2D.Double(clickPos.x - 5, clickPos.y - fontSize, rectWidth, rectHeight, 10.0D, 10.0D);
      moveRect = new ScreenShape(new Color(255, 255, 255, 100), rect, true, shapes.size());
      dragInProgress = 1;
      generateShapes();
      setModified();
    }
    return false;
  }
  
  public boolean mouseReleased(MouseEvent e) { if (dragInProgress == 1) {
      moveRect = null;
      Point p = new Point(getPointx - startDragging.x, getPointy - startDragging.y);
      clickPos = new Point(clickPos.x + x, clickPos.y + y);
      Point scrObjAttach = null;
      if ((scrObject instanceof Hole)) {
        scrObjAttach = ((Hole)scrObject).getNotePoint();
      } else if ((scrObject instanceof Frame)) {
        scrObjAttach = ((Frame)scrObject).getNotePoint();
      } else if ((scrObject instanceof NavigationBar)) {
        scrObjAttach = ((NavigationBar)scrObject).getNotePoint();
      }
      if (scrObjAttach != null) {
        initialPos = new Point(clickPos.x - x, clickPos.y - y);
      }
      underRect.setFrame(clickPos.x - 5 + dropOffset, clickPos.y - fontSize + dropOffset, bgRect.width, bgRect.height);
      bgRect.setFrame(clickPos.x - 5, clickPos.y - fontSize, bgRect.width, bgRect.height);
      
      if (tabRect != null) { tabRect.setFrame(bgRect.getX() - tabRectOffset, bgRect.getY(), tabRectWidth, tabRectHeight);
      }
      if (hasNext) {
        underNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60.0D + 2.0D, bgRect.getY() + bgRect.getHeight() + 2.0D, underNext.getWidth(), underNext.getHeight());
        bgNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60.0D, bgRect.getY() + bgRect.getHeight(), bgNext.getWidth(), bgNext.getHeight());
      }
      
      dragInProgress = 0;
      if (scrObject != null) {
        Point holePoint = getHolesPoint();
        Point wordPoint = getWordsPoint();
        line.setLine(holePoint, wordPoint);
        line2.setLine(x - 1, y, x - 1, y);
        line3.setLine(x + 1, y, x + 1, y);
        line4.setLine(x - 2, y, x - 2, y);
        line5.setLine(x + 2, y, x + 2, y);
      }
      if (paragraph != null) paragraph.setTextOrigin(new Point(clickPos.x + 10, clickPos.y)); else {
        paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
      }
      generateShapes();
      setModified();
    }
    return true;
  }
  
  public boolean mouseClicked(MouseEvent e) { if (bgRect.contains(e.getX(), e.getY())) {
      if (paragraph != null) paragraph.updateCaretPosition(e.getPoint());
      generateShapes();
    } else if (bgNext.contains(e.getX(), e.getY())) {
      stencilManager.showNextStencil();
    }
    
    return true;
  }
  
  public boolean mouseEntered(MouseEvent e) { return false; }
  
  public boolean mouseExited(MouseEvent e) {
    return false;
  }
  
  public boolean mouseMoved(MouseEvent e) { return false; }
  
  public boolean mouseDragged(MouseEvent e) {
    if (dragInProgress == 1) {
      Point p = new Point(getPointx - startDragging.x, getPointy - startDragging.y);
      RoundRectangle2D.Double rect = (RoundRectangle2D.Double)moveRect.getShape();
      rect.setFrame(clickPos.x + x, clickPos.y - fontSize + y, width, height);
      moveRect.setShape(rect);
      if (line != null) {
        Point holePoint = new Point((int)line.getP1().getX(), (int)line.getP1().getY());
        Point wordPoint = new Point(new Point(clickPos.x + x + (int)(width / 2.0D), clickPos.y + y - fontSize));
        line.setLine(line.getP1(), new Point(clickPos.x + x + (int)(width / 2.0D), clickPos.y + y - fontSize));
        line2.setLine(x - 1, y, x - 1, y);
        line3.setLine(x + 1, y, x + 1, y);
        line4.setLine(x - 2, y, x - 2, y);
        line5.setLine(x + 2, y, x + 2, y);
        
        drawArrow(holePoint, wordPoint);
        ScreenShape sShape = new ScreenShape(Color.red, triangle, true, 6);
        shapes.setElementAt(sShape, 6);
      }
      setModified();
      return true; }
    return false;
  }
  
  public boolean keyTyped(KeyEvent e)
  {
    if (Character.isISOControl(e.getKeyChar()))
    {
      return false;
    }
    
    if (writeEnabled) {
      if (paragraph == null) {
        paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
      }
      paragraph.insertChar(e.getKeyChar());
      generateShapes();
      setModified();
      return true; }
    return false;
  }
  
  public boolean keyPressed(KeyEvent e) {
    if (Character.isISOControl(e.getKeyChar())) {
      if (e.getKeyCode() == 10)
      {
        if (paragraph == null) {
          paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
        }
        paragraph.createNewLine();
        generateShapes();
        setModified();
        return true; }
      if (e.getKeyCode() == 8) {
        if (writeEnabled) {
          if (paragraph == null) {
            paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
          }
          paragraph.deleteChar();
          generateShapes();
          setModified();
          return true; }
        return false; }
      return false;
    }
    return false;
  }
  
  public boolean keyReleased(KeyEvent e) {
    return false;
  }
  

  public void stencilObjectMoved(Rectangle newPos) {}
  
  public void write(Document document, Element element)
  {
    Element noteElement = document.createElement("note");
    

    String id = "null";
    if (scrObject != null) {
      id = scrObject.getComponentID();
      if (id == null) {
        if ((scrObject instanceof NavigationBar))
          id = "navbar"; else {
          id = "null";
        }
      }
    }
    Element idNode = document.createElement("id");
    org.w3c.dom.CDATASection idSection = document.createCDATASection(id);
    idNode.appendChild(idSection);
    noteElement.appendChild(idNode);
    
    Vector msgs = new Vector();
    Vector colors = new Vector();
    if (paragraph != null) {
      msgs = paragraph.getText();
      colors = paragraph.getColors();
      for (int i = 0; i < msgs.size(); i++) {
        Element messageNode = document.createElement("message");
        String msg = (String)msgs.elementAt(i);
        if (colors.elementAt(i).equals(accentColor)) {
          messageNode.setAttribute("color", "blue");
        }
        if (noteNumber.length() > 0) msg = noteNumber + msg;
        org.w3c.dom.CDATASection messageSection = document.createCDATASection(msg);
        messageNode.appendChild(messageSection);
        

        noteElement.appendChild(messageNode);
      }
    }
    

    String type = "null";
    if ((scrObject instanceof Hole)) { type = "hole";
    } else if ((scrObject instanceof Frame)) { type = "frame";
    } else if ((scrObject instanceof NavigationBar)) type = "navBar";
    noteElement.setAttribute("type", type);
    
    if (type.equals("hole")) {
      boolean autoAdvance = ((Hole)scrObject).getAutoAdvance();
      if (autoAdvance)
        noteElement.setAttribute("autoAdvance", "true"); else
        noteElement.setAttribute("autoAdvance", "false");
      int advanceEvent = ((Hole)scrObject).getAdvanceEvent();
      if (advanceEvent == 1) {
        noteElement.setAttribute("advanceEvent", "mousePress");
      } else if (advanceEvent == 0) {
        noteElement.setAttribute("advanceEvent", "mouseClick");
      } else {
        noteElement.setAttribute("advanceEvent", "enterKey");
      }
    }
    
    if (hasNext)
      noteElement.setAttribute("hasNext", "true"); else {
      noteElement.setAttribute("hasNext", "false");
    }
    
    double x = 0.0D;
    double y = 0.0D;
    if (scrObject != null)
    {
      Point holePoint = getHolesPoint();
      if (rect != null) {
        x = clickPos.x - holePoint.getX();
        y = clickPos.y - holePoint.getY();
      }
    } else {
      x = clickPos.x / positionManager.getScreenWidth();
      y = clickPos.y / positionManager.getScreenHeight();
    }
    noteElement.setAttribute("xPos", new Double(x).toString());
    noteElement.setAttribute("yPos", new Double(y).toString());
    
    element.appendChild(noteElement);
  }
  
  public void setWriteEnabled(boolean enabled)
  {
    writeEnabled = enabled;
    if (isInitialized) generateShapes();
    setModified();
  }
  
  public boolean layoutChanged()
  {
    if (!isInitialized) {
      updatePosition();
    }
    if ((line != null) && (scrObject != null)) {
      Point holePoint = getHolesPoint();
      Point wordPoint = getWordsPoint();
      
      if ((holePoint != null) && (wordPoint != null)) {
        line.setLine(new Point(x, y), new Point(x, y));
        line2.setLine(x - 1, y, x - 1, y);
        line3.setLine(x + 1, y, x + 1, y);
        line4.setLine(x - 2, y, x - 2, y);
        line5.setLine(x + 2, y, x + 2, y);
        
        drawArrow(holePoint, wordPoint);
        ScreenShape sShape = new ScreenShape(Color.red, triangle, true, 6);
        shapes.setElementAt(sShape, 6);
      }
    }
    setModified();
    return true;
  }
}

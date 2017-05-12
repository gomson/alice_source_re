package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import javax.media.opengl.GL;













public class Graphics
  extends java.awt.Graphics
{
  private RenderContext m_renderContext;
  private Color m_color = Color.black;
  private static int SINE_AND_COSINE_CACHE_LENGTH = 8;
  private static double[] s_cosines = null;
  private static double[] s_sines = null;
  
  private static void cacheSinesAndCosinesIfNecessary() {
    if (s_cosines == null) {
      s_cosines = new double[SINE_AND_COSINE_CACHE_LENGTH];
      s_sines = new double[SINE_AND_COSINE_CACHE_LENGTH];
      double theta = 0.0D;
      double dtheta = 1.5707963267948966D / s_cosines.length;
      for (int i = 0; i < s_cosines.length; i++) {
        s_cosines[i] = Math.cos(theta);
        s_sines[i] = Math.sin(theta);
        theta += dtheta;
      }
    }
  }
  
  protected Graphics(RenderContext renderContext) { m_renderContext = renderContext;
    setColor(m_color);
    
    int width = m_renderContext.getWidth();
    int height = m_renderContext.getHeight();
    m_renderContext.gl.glMatrixMode(5889);
    m_renderContext.gl.glLoadIdentity();
    m_renderContext.gl.glOrtho(0.0D, width - 1, height - 1, 0.0D, -1.0D, 1.0D);
    
    m_renderContext.gl.glMatrixMode(5888);
    m_renderContext.gl.glLoadIdentity();
    
    m_renderContext.gl.glDisable(2929);
    m_renderContext.gl.glDisable(2896);
    m_renderContext.gl.glDisable(2884);
    m_renderContext.setTextureMapProxy(null);
  }
  
  public void dispose() {
    m_renderContext.gl.glFlush();
    m_renderContext = null;
  }
  
  public java.awt.Graphics create() {
    throw new RuntimeException("not implemented");
  }
  
  public void translate(int x, int y) {
    m_renderContext.gl.glTranslatef(x, y, 0.0F);
  }
  
  public Color getColor() {
    return m_color;
  }
  
  public void setColor(Color c) {
    m_color = c;
    m_renderContext.gl.glColor3ub((byte)m_color.getRed(), (byte)m_color.getGreen(), (byte)m_color.getBlue());
  }
  


  public void setPaintMode() {}
  

  public void setXORMode(Color c1) {}
  

  private Font m_font = new Font(null, 0, 12);
  
  public Font getFont() {
    return m_font; }
  
  public void setFont(Font font)
  {
    m_font = font;
  }
  
  public FontMetrics getFontMetrics(Font f) {
    return Toolkit.getDefaultToolkit().getFontMetrics(f);
  }
  
  public Rectangle getClipBounds() {
    throw new RuntimeException("not implemented");
  }
  
  public void clipRect(int x, int y, int width, int height) {
    throw new RuntimeException("not implemented");
  }
  
  public void setClip(int x, int y, int width, int height) {
    throw new RuntimeException("not implemented");
  }
  
  public Shape getClip() {
    throw new RuntimeException("not implemented");
  }
  
  public void setClip(Shape clip) {
    throw new RuntimeException("not implemented");
  }
  
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    throw new RuntimeException("not implemented");
  }
  
  public void drawLine(int x1, int y1, int x2, int y2) {
    m_renderContext.gl.glBegin(1);
    m_renderContext.gl.glVertex2i(x1, y1);
    m_renderContext.gl.glVertex2i(x2, y2);
    m_renderContext.gl.glEnd();
  }
  
  public void fillRect(int x, int y, int width, int height) {
    m_renderContext.gl.glBegin(9);
    m_renderContext.gl.glVertex2i(x, y);
    m_renderContext.gl.glVertex2i(x + width, y);
    m_renderContext.gl.glVertex2i(x + width, y + height);
    m_renderContext.gl.glVertex2i(x, y + height);
    m_renderContext.gl.glEnd();
  }
  
  public void clearRect(int x, int y, int width, int height) {
    throw new RuntimeException("not implemented");
  }
  
  private void glQuarterOval(double centerX, double centerY, double radiusX, double radiusY, int whichQuarter)
  {
    int n = s_cosines.length;
    int max = n - 1;
    for (int i = 0; i < n; i++) { double sin;
      double sin;
      double sin;
      double sin; switch (whichQuarter) {
      case 0: 
        double cos = s_cosines[i];
        sin = s_sines[i];
        break;
      case 1: 
        double cos = -s_cosines[(max - i)];
        sin = s_sines[(max - i)];
        break;
      case 2: 
        double cos = -s_cosines[i];
        sin = -s_sines[i];
        break;
      case 3: 
        double cos = s_cosines[(max - i)];
        sin = -s_sines[(max - i)];
        break;
      default: 
        throw new IllegalArgumentException(); }
      double sin;
      double cos; m_renderContext.gl.glVertex2d(centerX + cos * radiusX, centerY + sin * radiusY);
    }
  }
  
  private void glRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    cacheSinesAndCosinesIfNecessary();
    

    int x1 = x + arcWidth;
    int x2 = x + width - arcWidth;
    


    int y1 = y + arcHeight;
    int y2 = y + height - arcHeight;
    

    glQuarterOval(x1, y1, arcWidth, arcHeight, 2);
    
    glQuarterOval(x2, y1, arcWidth, arcHeight, 3);
    
    glQuarterOval(x2, y2, arcWidth, arcHeight, 0);
    
    glQuarterOval(x1, y2, arcWidth, arcHeight, 1);
  }
  
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
  {
    m_renderContext.gl.glBegin(2);
    glRoundRect(x, y, width, height, arcWidth, arcHeight);
    m_renderContext.gl.glEnd();
  }
  
  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    m_renderContext.gl.glBegin(6);
    glRoundRect(x, y, width, height, arcWidth, arcHeight);
    m_renderContext.gl.glEnd();
  }
  
  private void glOval(int x, int y, int width, int height) {
    double radiusX = width * 0.5D;
    double radiusY = height * 0.5D;
    double centerX = x + radiusX;
    double centerY = y + radiusY;
    cacheSinesAndCosinesIfNecessary();
    glQuarterOval(centerX, centerY, radiusX, radiusY, 0);
    glQuarterOval(centerX, centerY, radiusX, radiusY, 1);
    glQuarterOval(centerX, centerY, radiusX, radiusY, 2);
    glQuarterOval(centerX, centerY, radiusX, radiusY, 3);
  }
  
  public void drawOval(int x, int y, int width, int height)
  {
    m_renderContext.gl.glBegin(2);
    glOval(x, y, width, height);
    m_renderContext.gl.glEnd();
  }
  
  public void fillOval(int x, int y, int width, int height) {
    m_renderContext.gl.glBegin(6);
    glOval(x, y, width, height);
    m_renderContext.gl.glEnd();
  }
  
  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    throw new RuntimeException("not implemented");
  }
  

  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) { throw new RuntimeException("not implemented"); }
  
  private void glPoly(int[] xPoints, int[] yPoints, int nPoints) {
    for (int i = 0; i < nPoints; i++) {
      m_renderContext.gl.glVertex2i(xPoints[i], yPoints[i]);
    }
  }
  
  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    m_renderContext.gl.glBegin(3);
    glPoly(xPoints, yPoints, nPoints);
    m_renderContext.gl.glEnd();
  }
  
  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    m_renderContext.gl.glBegin(2);
    glPoly(xPoints, yPoints, nPoints);
    m_renderContext.gl.glEnd();
  }
  
  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    m_renderContext.gl.glBegin(9);
    glPoly(xPoints, yPoints, nPoints);
    m_renderContext.gl.glEnd();
  }
  
  public void drawString(String str, int x, int y) {
    TextRenderer renderer = new TextRenderer(m_font);
    renderer.beginRendering(m_renderContext.getWidth(), m_renderContext.getHeight());
    renderer.setColor(m_color);
    renderer.draw(str, x, m_renderContext.getHeight() - y);
    renderer.endRendering();
  }
  
  public void drawString(AttributedCharacterIterator iterator, int x, int y) {
    throw new RuntimeException("not implemented");
  }
  
  public void drawChars(char[] data, int offset, int length, int x, int y) {
    throw new RuntimeException("not implemented");
  }
  
  public void drawBytes(byte[] data, int offset, int length, int x, int y) {
    throw new RuntimeException("not implemented");
  }
  
  public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
    throw new RuntimeException("not implemented");
  }
  
  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
    throw new RuntimeException("not implemented");
  }
  
  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
    throw new RuntimeException("not implemented");
  }
  
  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
    throw new RuntimeException("not implemented");
  }
  
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
    throw new RuntimeException("not implemented");
  }
  
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
    throw new RuntimeException("not implemented");
  }
}

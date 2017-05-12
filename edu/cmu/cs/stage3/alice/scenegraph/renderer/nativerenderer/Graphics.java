package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;












public abstract class Graphics
  extends java.awt.Graphics
{
  private int m_nativeInstance = 0;
  private RenderTarget m_renderTarget;
  
  protected abstract void createNativeInstance(RenderTargetAdapter paramRenderTargetAdapter);
  
  protected abstract void createNativeInstance(RenderTargetAdapter paramRenderTargetAdapter, TextureMapProxy paramTextureMapProxy);
  
  protected abstract void releaseNativeInstance();
  
  protected Graphics(RenderTarget renderTarget) { createNativeInstance(renderTarget.getAdapter());
    m_renderTarget = renderTarget;
    m_textureMapProxy = null;
  }
  
  protected Graphics(RenderTarget renderTarget, TextureMapProxy textureMapProxy) { createNativeInstance(renderTarget.getAdapter(), textureMapProxy);
    m_renderTarget = renderTarget;
    m_textureMapProxy = textureMapProxy;
  }
  
  public void dispose() {
    releaseNativeInstance();
    if (m_textureMapProxy != null) {
      if ((m_renderTarget instanceof OnscreenRenderTarget)) {
        ((OnscreenRenderTarget)m_renderTarget).getAWTComponent().repaint();
      }
      m_textureMapProxy.setRenderTargetWithLatestImage(m_renderTarget);
    }
  }
  private TextureMapProxy m_textureMapProxy;
  
  public java.awt.Graphics create() { throw new RuntimeException("not implemented"); }
  

  public abstract void translate(int paramInt1, int paramInt2);
  
  public abstract Color getColor();
  
  public abstract void setColor(Color paramColor);
  
  public abstract void setPaintMode();
  
  public abstract void setXORMode(Color paramColor);
  
  private Font m_font = null;
  
  protected abstract void setFont(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt);
  
  public Font getFont() {
    return m_font; }
  
  public void setFont(Font font)
  {
    m_font = font;
    int style = m_font.getStyle();
    boolean isBold = (style & 0x1) != 0;
    boolean isItalic = (style & 0x2) != 0;
    setFont(m_font.getFamily(), m_font.getName(), isBold, isItalic, m_font.getSize());
  }
  
  public FontMetrics getFontMetrics(Font f) {
    throw new RuntimeException("not implemented");
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
  
  public abstract void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public abstract void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public abstract void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public abstract void drawString(String paramString, int paramInt1, int paramInt2);
  
  public void drawString(AttributedCharacterIterator iterator, int x, int y) {
    throw new RuntimeException("not implemented");
  }
  
  public abstract void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
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

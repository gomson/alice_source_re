package edu.cmu.cs.stage3.alice.core.bubble;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
















class SubText
{
  private String m_text;
  private Rectangle2D m_bound;
  
  public SubText(String text, Rectangle2D bound, int yOffset)
  {
    m_text = text;
    m_bound = new Rectangle2D.Double(bound.getX(), bound.getY() + yOffset, bound.getWidth(), bound.getHeight());
  }
  
  public String getText() { return m_text; }
  
  public Rectangle2D getBound() {
    return m_bound;
  }
  
  public Rectangle2D getSafeBound() { return new Rectangle2D.Double(m_bound.getX(), m_bound.getY(), m_bound.getWidth(), m_bound.getHeight()); }
  
  int getPixelX()
  {
    return (int)m_bound.getX();
  }
  
  int getPixelY() { return (int)m_bound.getY(); }
  


  public String toString()
  {
    return m_text + " " + m_bound.getX() + " " + m_bound.getY() + " " + m_bound.getWidth() + " " + m_bound.getHeight();
  }
}

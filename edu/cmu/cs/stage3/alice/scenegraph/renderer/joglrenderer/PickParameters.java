package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;






public class PickParameters
{
  private int m_x;
  



  private int m_y;
  



  private boolean m_isSubElementRequired;
  



  private boolean m_isOnlyFrontMostRequired;
  




  public PickParameters(int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired)
  {
    m_x = x;
    m_y = y;
    m_isSubElementRequired = isSubElementRequired;
    m_isOnlyFrontMostRequired = isOnlyFrontMostRequired;
  }
  
  public int getX() {
    return m_x;
  }
  
  public int getY() { return m_y; }
  
  public boolean isSubElementRequired() {
    return m_isSubElementRequired;
  }
  
  public boolean isOnlyFrontMostRequired() { return m_isOnlyFrontMostRequired; }
}

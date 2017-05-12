package edu.cmu.cs.stage3.alice.core;











public abstract class Geometry
  extends Element
{
  private edu.cmu.cs.stage3.alice.scenegraph.Geometry m_sgGeometry;
  









  public edu.cmu.cs.stage3.alice.scenegraph.Geometry getSceneGraphGeometry()
  {
    return m_sgGeometry;
  }
  
  protected Geometry(edu.cmu.cs.stage3.alice.scenegraph.Geometry sgGeometry) {
    m_sgGeometry = sgGeometry;
    m_sgGeometry.setBonus(this);
  }
  
  protected void internalRelease(int pass) {
    switch (pass) {
    case 2: 
      m_sgGeometry.release();
      m_sgGeometry = null;
    }
    
    super.internalRelease(pass);
  }
  
  protected void nameValueChanged(String value) {
    super.nameValueChanged(value);
    String s = null;
    if (value != null) {
      s = value + ".m_sgGeometry";
    }
    m_sgGeometry.setName(s);
  }
}

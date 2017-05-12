package edu.cmu.cs.stage3.alice.core;











public class ClippingPlane
  extends Affector
{
  private edu.cmu.cs.stage3.alice.scenegraph.ClippingPlane m_sgClippingPlane;
  










  public edu.cmu.cs.stage3.alice.scenegraph.Affector getSceneGraphAffector()
  {
    return getSceneGraphClippingPlane();
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.ClippingPlane getSceneGraphClippingPlane() { return m_sgClippingPlane; }
  
  public ClippingPlane()
  {
    m_sgClippingPlane = new edu.cmu.cs.stage3.alice.scenegraph.ClippingPlane();
    m_sgClippingPlane.setParent(getSceneGraphTransformable());
    m_sgClippingPlane.setBonus(this);
  }
  
  protected void nameValueChanged(String value) {
    super.nameValueChanged(value);
    String s = null;
    if (value != null) {
      s = value + ".m_sgClippingPlane";
    }
    m_sgClippingPlane.setName(s);
  }
}

package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Matrix44;
import javax.vecmath.Matrix4d;























public class Scene
  extends ReferenceFrame
{
  public Scene() {}
  
  public static final Property BACKGROUND_PROPERTY = new Property(Scene.class, "BACKGROUND");
  private Background m_background = null;
  
  protected void releasePass1() {
    if (m_background != null) {
      warnln(Messages.getString("WARNING__released_scene_") + this + " " + Messages.getString("still_has_background_") + m_background + ".");
      setBackground(null);
    }
    super.releasePass1();
  }
  
  public Matrix4d getAbsoluteTransformation() {
    Matrix4d m = new Matrix4d();
    m.setIdentity();
    return m;
  }
  
  public Matrix4d getInverseAbsoluteTransformation() {
    Matrix4d m = new Matrix4d();
    m.setIdentity();
    return m;
  }
  
  public Container getRoot() {
    return this;
  }
  


  public Background getBackground()
  {
    return m_background;
  }
  



  public void setBackground(Background background)
  {
    if (m_background != background) {
      m_background = background;
      onPropertyChange(BACKGROUND_PROPERTY);
    }
  }
  
  public Matrix44 getTransformation(ReferenceFrame asSeenBy)
  {
    return new Matrix44(asSeenBy.getInverseAbsoluteTransformation());
  }
}

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Transformable;
import java.nio.DoubleBuffer;
import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;














class TransformableProxy
  extends ReferenceFrameProxy
{
  TransformableProxy() {}
  
  private double[] m_local = new double[16];
  private DoubleBuffer m_localBuffer = DoubleBuffer.wrap(m_local);
  
  protected void changed(Property property, Object value) {
    if (property == Transformable.LOCAL_TRANSFORMATION_PROPERTY) {
      copy(m_local, (Matrix4d)value);
    } else if (property != Transformable.IS_FIRST_CLASS_PROPERTY)
    {

      super.changed(property, value);
    }
  }
  
  public void render(RenderContext context) {
    gl.glPushMatrix();
    gl.glMultMatrixd(m_localBuffer);
    super.render(context);
    gl.glPopMatrix();
  }
  
  public void pick(PickContext context, PickParameters pickParameters) {
    gl.glPushMatrix();
    gl.glMultMatrixd(m_localBuffer);
    super.pick(context, pickParameters);
    gl.glPopMatrix();
  }
}

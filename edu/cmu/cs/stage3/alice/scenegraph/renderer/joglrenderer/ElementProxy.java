package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;











abstract class ElementProxy
  extends AbstractProxy
{
  ElementProxy() {}
  
  protected static void copy(float[] dst, Color src)
  {
    dst[0] = src.getRed();
    dst[1] = src.getGreen();
    dst[2] = src.getBlue();
    if (dst.length > 3) {
      dst[3] = src.getAlpha();
    }
  }
  
  protected static void copy(double[] dst, Matrix4d src) {
    dst[0] = ((float)m00);
    dst[1] = ((float)m01);
    dst[2] = (-(float)m02);
    dst[3] = ((float)m03);
    
    dst[4] = ((float)m10);
    dst[5] = ((float)m11);
    dst[6] = (-(float)m12);
    dst[7] = ((float)m13);
    
    dst[8] = (-(float)m20);
    dst[9] = (-(float)m21);
    dst[10] = ((float)m22);
    dst[11] = (-(float)m23);
    
    dst[12] = ((float)m30);
    dst[13] = ((float)m31);
    dst[14] = (-(float)m32);
    dst[15] = ((float)m33);
  }
  
  protected static void copy(double[] dst, Matrix3d src) { dst[0] = ((float)m00);
    dst[1] = ((float)m01);
    dst[2] = (-(float)m02);
    dst[3] = 0.0D;
    
    dst[4] = ((float)m10);
    dst[5] = ((float)m11);
    dst[6] = (-(float)m12);
    dst[7] = 0.0D;
    
    dst[8] = (-(float)m20);
    dst[9] = (-(float)m21);
    dst[10] = ((float)m22);
    dst[11] = 0.0D;
    
    dst[12] = 0.0D;
    dst[13] = 0.0D;
    dst[14] = 0.0D;
    dst[15] = 1.0D;
  }
  
  public void initialize(Element sgElement, AbstractProxyRenderer renderer)
  {
    super.initialize(sgElement, renderer);
  }
  
  protected void changed(Property property, Object value)
  {
    if ((property != Element.NAME_PROPERTY) && 
      (property != Element.BONUS_PROPERTY))
    {
      Element.warnln("unhandled property: " + property + " " + value);
    }
  }
}

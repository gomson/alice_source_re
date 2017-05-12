package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy;
















public abstract class ElementProxy
  extends AbstractProxy
{
  protected abstract void createNativeInstance();
  
  private int m_nativeInstance = 0;
  private int m_nativeTypeID = 0;
  
  protected abstract void releaseNativeInstance();
  
  ElementProxy() {
    createNativeInstance();
  }
  
  public void release() {
    releaseNativeInstance();
    super.release();
  }
  
  protected int getNativeInstance() { return m_nativeInstance; }
  
  protected void changed(Property property, Object value)
  {
    if ((property != Element.NAME_PROPERTY) && 
      (property != Element.BONUS_PROPERTY))
    {
      Element.warnln("unhandled property: " + property + " " + value);
    }
  }
}

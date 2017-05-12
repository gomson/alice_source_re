package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer;
import edu.cmu.cs.stage3.math.Sphere;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.Vector3d;











public abstract class GeometryProxy
  extends ElementProxy
{
  public GeometryProxy() {}
  
  protected abstract void onBoundChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  private static Vector s_changed = new Vector();
  
  private Geometry getSceneGraphGeometry() { return (Geometry)getSceneGraphElement(); }
  
  protected boolean listenToBoundChanges() {
    return ((Renderer)getRenderer()).requiresBoundListening();
  }
  
  public void initialize(Element sgElement, AbstractProxyRenderer renderer) {
    super.initialize(sgElement, renderer);
    if (listenToBoundChanges())
      onBoundChange();
  }
  
  public void onBoundChange() {
    if (!s_changed.contains(this))
    {

      s_changed.addElement(this); }
  }
  
  static void updateBoundChanges() {
    if (s_changed.size() > 0) {
      Enumeration enum0 = s_changed.elements();
      while (enum0.hasMoreElements()) {
        GeometryProxy geometryProxy = (GeometryProxy)enum0.nextElement();
        Sphere sphere = geometryProxy.getSceneGraphGeometry().getBoundingSphere();
        double x = NaN.0D;
        double y = NaN.0D;
        double z = NaN.0D;
        double radius = NaN.0D;
        if (sphere != null) {
          Vector3d center = sphere.getCenter();
          if (center != null) {
            x = x;
            y = y;
            z = z;
          }
          radius = sphere.getRadius();
        }
        geometryProxy.onBoundChange(x, y, z, radius);
      }
      s_changed.removeAllElements();
    }
  }
}

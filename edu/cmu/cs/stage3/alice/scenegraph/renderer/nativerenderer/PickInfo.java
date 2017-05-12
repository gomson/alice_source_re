package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Plane;
import edu.cmu.cs.stage3.math.Ray;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;







public class PickInfo
  implements edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo
{
  private Component m_source = null;
  private Matrix4d m_projection;
  private Visual[] m_visuals;
  private boolean[] m_isFrontFacings;
  private Geometry[] m_geometries;
  private int[] m_subElements;
  private double[] m_zs;
  
  public PickInfo(Component component, Matrix4d projection, Visual[] visuals, boolean[] isFrontFacings, Geometry[] geometries, int[] subElements, double[] zs) {
    m_source = component;
    m_projection = projection;
    m_visuals = visuals;
    m_isFrontFacings = isFrontFacings;
    m_geometries = geometries;
    m_subElements = subElements;
    m_zs = zs;
  }
  
  public Component getSource() {
    return m_source;
  }
  
  public Visual[] getVisuals() { return m_visuals; }
  
  public Geometry[] getGeometries() {
    return m_geometries;
  }
  
  public boolean[] isFrontFacings() { return m_isFrontFacings; }
  
  public int[] getSubElements() {
    return m_subElements;
  }
  
  public double[] getZs() { return m_zs; }
  
  public int getCount()
  {
    if (m_visuals != null) {
      return m_visuals.length;
    }
    return 0;
  }
  
  public Visual getVisualAt(int index) {
    return m_visuals[index];
  }
  
  public boolean isFrontFacingAt(int index) { return m_isFrontFacings[index]; }
  
  public Geometry getGeometryAt(int index) {
    return m_geometries[index];
  }
  
  public int getSubElementAt(int index) { return m_subElements[index]; }
  

  public double getZAt(int index) { return m_zs[index]; }
  
  /**
   * @deprecated
   */
  private RenderTarget m_renderTarget = null; /**
   * @deprecated
   */
  private Camera m_camera = null; /**
   * @deprecated
   */
  private int m_x = -1; /**
   * @deprecated
   */
  private int m_y = -1;
  
  public PickInfo(RenderTarget renderTarget, Camera camera, int x, int y, Visual[] visuals, boolean[] isFrontFacings, Geometry[] geometries, int[] subElements) {
    m_renderTarget = renderTarget;
    m_camera = camera;
    m_source = camera;
    m_x = x;
    m_y = y;
    m_visuals = visuals;
    m_isFrontFacings = isFrontFacings;
    m_geometries = geometries;
    m_subElements = subElements;
  }
  
  public Vector3d getLocalPositionAt(int index) {
    if ((m_source != null) && (m_zs != null)) {
      Matrix4d componentInverseAbsolute = m_source.getInverseAbsoluteTransformation();
      Matrix4d visualAbsolute = getVisualAt(index).getAbsoluteTransformation();
      Vector4d xyzw = new Vector4d(0.0D, 0.0D, m_zs[index], 1.0D);
      m_projection.transform(xyzw);
      componentInverseAbsolute.transform(xyzw);
      visualAbsolute.transform(xyzw);
      return new Vector3d(x / w, y / w, z / w);
    }
    Visual visual = getVisualAt(index);
    Geometry geometry = getGeometryAt(index);
    int subElement = getSubElementAt(index);
    IndexedTriangleArray ita = (IndexedTriangleArray)geometry;
    Vertex3d[] vertices = ita.getVertices();
    int[] indices = ita.getIndices();
    int i0 = subElement * 3;
    Vector3d v0 = new Vector3d(position);
    Vector3d v1 = new Vector3d(1position);
    Vector3d v2 = new Vector3d(2position);
    
    Matrix3d scale = visual.getScale();
    if (scale != null) {
      v0 = MathUtilities.multiply(scale, v0);
      v1 = MathUtilities.multiply(scale, v1);
      v2 = MathUtilities.multiply(scale, v2);
    }
    Plane plane = new Plane(v0, v1, v2);
    


















    Ray ray = m_renderTarget.getRayAtPixel(m_camera, m_x, m_y);
    
    ReferenceFrame cameraFrame = (ReferenceFrame)m_camera.getParent();
    ray.transform(cameraFrame.getAbsoluteTransformation());
    ReferenceFrame visualFrame = (ReferenceFrame)visual.getParent();
    Matrix4d inverseVisual = visualFrame.getInverseAbsoluteTransformation();
    ray.transform(inverseVisual);
    double t = plane.intersect(ray);
    return ray.getPoint(t);
  }
}

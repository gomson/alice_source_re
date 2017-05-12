package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Comparator;
import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;













public class PickInfo
  implements edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo
{
  private Component m_source;
  private Matrix4d m_projection;
  private Visual[] m_visuals;
  private boolean[] m_isFrontFacings;
  private Geometry[] m_geometries;
  private int[] m_subElements;
  private double[] m_zs;
  
  public PickInfo(PickContext context, IntBuffer pickBuffer, Component source)
  {
    m_source = source;
    int length = gl.glRenderMode(7168);
    if (length < 0)
    {
      length = 0;
    }
    pickBuffer.rewind();
    






















    Object pickItems = new 1PickItem[length];
    int offset = 0;
    for (int i = 0; i < length; i++) {
      pickItems[i = new Object() { Visual visual; boolean isFrontFacing; Geometry geometry; int subElement; int zFront; };
      offset += 6;
    }
    
    Arrays.sort(pickItems, new Comparator() {
      public int compare(Object o1, Object o2) {
        PickInfo.1PickItem pi1 = (PickInfo.1PickItem)o1;
        PickInfo.1PickItem pi2 = (PickInfo.1PickItem)o2;
        return zFront - zFront;
      }
      
      public boolean equals(Object obj) {
        return super.equals(obj);
      }
      
    });
    m_visuals = new Visual[length];
    m_isFrontFacings = new boolean[length];
    m_geometries = new Geometry[length];
    m_subElements = new int[length];
    m_zs = new double[length];
    for (int i = 0; i < length; i++) {
      m_visuals[i] = visual;
      m_isFrontFacings[i] = isFrontFacing;
      m_geometries[i] = geometry;
      m_subElements[i] = subElement;
      m_zs[i] = (zFront / 2.14748365E9F);
    }
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
  
  public double getZAt(int index) {
    return m_zs[index];
  }
  
  public Vector3d getLocalPositionAt(int index) { return null; }
}

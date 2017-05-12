package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.LineArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;















public abstract class ViewVolumeDecorator
  extends Decorator
{
  public ViewVolumeDecorator() {}
  
  private Visual m_sgVisualLines = null;
  private LineArray m_sgLineArray = null;
  private Visual m_sgVisualFaces = null;
  private IndexedTriangleArray m_sgITA = null;
  

  protected ReferenceFrame getReferenceFrame() { return getCamera(); }
  
  protected abstract Camera getCamera();
  
  public void internalRelease(int pass) {
    switch (pass) {
    case 1: 
      if (m_sgVisualLines != null) {
        m_sgVisualLines.setFrontFacingAppearance(null);
        m_sgVisualLines.setGeometry(null);
      }
      if (m_sgVisualFaces != null) {
        m_sgVisualFaces.setFrontFacingAppearance(null);
        m_sgVisualFaces.setGeometry(null);
      }
      break;
    case 2: 
      if (m_sgVisualLines != null) {
        m_sgVisualLines.release();
        m_sgVisualLines = null;
      }
      if (m_sgLineArray != null) {
        m_sgLineArray.release();
        m_sgLineArray = null;
      }
      if (m_sgVisualFaces != null) {
        m_sgVisualFaces.release();
        m_sgVisualFaces = null;
      }
      if (m_sgITA != null) {
        m_sgITA.release();
        m_sgITA = null;
      }
      break;
    }
    super.internalRelease(pass);
  }
  
  protected abstract double[] getXYNearAndXYFar(double paramDouble1, double paramDouble2);
  
  protected void update() { super.update();
    boolean requiresVerticesToBeUpdated = isDirty();
    if (m_sgLineArray == null) {
      m_sgLineArray = new LineArray();
      requiresVerticesToBeUpdated = true;
    }
    if (m_sgITA == null) {
      int[] indices = {
        0, 1, 2, 2, 3, 
        0, 5, 6, 2, 2, 1, 5, 
        6, 7, 3, 3, 2, 6, 
        7, 4, 0, 0, 3, 7, 
        4, 5, 1, 1, 0, 4, 
        7, 6, 5, 5, 4, 7, 
        3, 2, 1, 1, 0, 3, 
        1, 2, 6, 6, 5, 1, 
        2, 3, 7, 7, 6, 2, 
        3, 0, 4, 4, 7, 3, 
        0, 1, 5, 5, 4, 
        0, 4, 5, 6, 6, 7, 4 };
      
      m_sgITA = new IndexedTriangleArray();
      m_sgITA.setIndices(indices);
      requiresVerticesToBeUpdated = true;
    }
    
    if (m_sgVisualLines == null) {
      m_sgVisualLines = new Visual();
      

      m_sgVisualLines.setIsShowing(false);
      m_sgVisualLines.setGeometry(m_sgLineArray);
      m_sgVisualLines.setParent(getReferenceFrame().getSceneGraphContainer());
    }
    if (m_sgVisualFaces == null) {
      m_sgVisualFaces = new Visual();
      

      m_sgVisualFaces.setIsShowing(false);
      m_sgVisualFaces.setGeometry(m_sgITA);
      m_sgVisualFaces.setParent(getReferenceFrame().getSceneGraphContainer());
    }
    
    if (requiresVerticesToBeUpdated) {
      Vertex3d[] vertices = new Vertex3d[24];
      double zNear = getCameranearClippingPlaneDistance.doubleValue();
      double zFar = getCamerafarClippingPlaneDistance.doubleValue();
      double[] array = getXYNearAndXYFar(zNear, zFar);
      double xNear = array[0];
      double yNear = array[1];
      double xFar = array[2];
      double yFar = array[3];
      Vertex3d[] vs = new Vertex3d[8];
      vs[0] = Vertex3d.createXYZIJKUV(xNear, yNear, zNear, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      vs[1] = Vertex3d.createXYZIJKUV(-xNear, yNear, zNear, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      vs[2] = Vertex3d.createXYZIJKUV(-xNear, -yNear, zNear, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      vs[3] = Vertex3d.createXYZIJKUV(xNear, -yNear, zNear, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      vs[4] = Vertex3d.createXYZIJKUV(xFar, yFar, zFar, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      vs[5] = Vertex3d.createXYZIJKUV(-xFar, yFar, zFar, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      vs[6] = Vertex3d.createXYZIJKUV(-xFar, -yFar, zFar, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      vs[7] = Vertex3d.createXYZIJKUV(xFar, -yFar, zFar, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
      
      int bottom = 0;
      int top = 8;
      int sides = 16;
      for (int lcv = 0; lcv < 4; lcv++) {
        vertices[(bottom++)] = vs[lcv];
        vertices[(bottom++)] = vs[((lcv + 1) % 4)];
        vertices[(top++)] = vs[(4 + lcv)];
        vertices[(top++)] = vs[(4 + (lcv + 1) % 4)];
        vertices[(sides++)] = vs[lcv];
        vertices[(sides++)] = vs[(lcv + 4)];
      }
      m_sgLineArray.setVertices(vertices);
      m_sgITA.setVertices(vs);
    }
    setIsDirty(false);
  }
  
  protected void showRightNow() {
    if (m_sgVisualLines != null) {
      m_sgVisualLines.setIsShowing(true);
    }
    if (m_sgVisualFaces != null) {
      m_sgVisualFaces.setIsShowing(true);
    }
  }
  
  protected void hideRightNow() {
    if (m_sgVisualLines != null) {
      m_sgVisualLines.setIsShowing(false);
    }
    if (m_sgVisualFaces != null) {
      m_sgVisualFaces.setIsShowing(false);
    }
  }
}

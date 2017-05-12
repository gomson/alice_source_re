package edu.cmu.cs.stage3.alice.gallery;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.util.HowMuch;
import javax.vecmath.Vector3d;













public class ModelFixer
{
  public ModelFixer() {}
  
  private static Vector3d getDirection(Vertex3d vertexA, Vertex3d vertexB, Vertex3d vertexC)
  {
    Vector3d ba = new Vector3d();
    Vector3d bc = new Vector3d();
    ba.sub(position, position);
    bc.sub(position, position);
    ba.normalize();
    bc.normalize();
    Vector3d result = new Vector3d();
    result.cross(bc, ba);
    return result;
  }
  
  private static Vector3d[] getCalculatedNormals(edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray indexedTriangleArray, ProgressObserver progressObserver) throws ProgressCancelException {
    Vector3d[] result = null;
    int triangleCount = indexedTriangleArray.getTriangleCount();
    if (triangleCount > 0) {
      Vertex3d[] vertices = indexedTriangleArray.getVertices();
      result = new Vector3d[vertices.length];
      int[] counts = new int[vertices.length];
      for (int i = 0; i < counts.length; i++) {
        counts[i] = 0;
        result[i] = new Vector3d();
      }
      int[] indices = indexedTriangleArray.getIndices();
      if (progressObserver != null) {
        progressObserver.progressBegin(triangleCount);
      }
      int index = 0;
      for (int t = 0; t < triangleCount; t++) {
        int a = indices[(index++)];
        int b = indices[(index++)];
        int c = indices[(index++)];
        
        Vector3d normal = getDirection(vertices[a], vertices[b], vertices[c]);
        normal.normalize();
        
        result[a].add(normal);
        result[b].add(normal);
        result[c].add(normal);
        counts[a] += 1;
        counts[b] += 1;
        counts[c] += 1;
        if (progressObserver != null) {
          progressObserver.progressUpdate(t, null);
        }
      }
      for (int i = 0; i < result.length; i++) {
        result[i].scale(1.0D / counts[i]);
      }
    }
    return result;
  }
  
  public static void calculateNormals(edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita, ProgressObserver progressObserver) throws ProgressCancelException {
    ita.setNormals(getCalculatedNormals(ita.getSceneGraphIndexedTriangleArray(), progressObserver));
  }
  
  public static void calculateNormals(Element element, HowMuch howMuch, ProgressObserver macroProgressObserver, ProgressObserver microProgressObserver) throws ProgressCancelException {
    edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray[] itas = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray[])element.getDescendants(edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray.class, howMuch);
    if ((itas != null) && (itas.length > 0)) {
      if (macroProgressObserver != null) {
        macroProgressObserver.progressBegin(itas.length);
        Vector3d[][] normalsArray = new Vector3d[itas.length][];
        try {
          for (int i = 0; i < itas.length; i++) {
            normalsArray[i] = getCalculatedNormals(itas[i].getSceneGraphIndexedTriangleArray(), microProgressObserver);
          }
        } finally {
          macroProgressObserver.progressEnd();
        }
        
        for (int i = 0; i < itas.length; i++) {
          itas[i].setNormals(normalsArray[i]);
        }
      } else {
        for (int i = 0; i < itas.length; i++)
          calculateNormals(itas[i], microProgressObserver);
      }
    }
  }
  
  public static void calculateNormals(edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita) {
    try {
      calculateNormals(ita, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
  
  public static void calculateNormals(Element element, HowMuch howMuch) {
    try { calculateNormals(element, howMuch, null, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
}

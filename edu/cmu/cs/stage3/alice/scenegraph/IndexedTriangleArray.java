package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


















public class IndexedTriangleArray
  extends VertexGeometry
{
  public static final Property INDICES_PROPERTY = new Property(IndexedTriangleArray.class, "INDICES");
  public static final Property INDEX_LOWER_BOUND_PROPERTY = new Property(IndexedTriangleArray.class, "INDEX_LOWER_BOUND");
  public static final Property INDEX_UPPER_BOUND_PROPERTY = new Property(IndexedTriangleArray.class, "INDEX_UPPER_BOUND");
  

  public IndexedTriangleArray() {}
  
  private int[] m_indices = null;
  private int m_indexLowerBound = 0;
  private int m_indexUpperBound = -1;
  
  private int[] m_edges = null;
  private int m_subdivisionRefinementLevel = 0;
  
  public int[] getIndices() {
    return m_indices; }
  
  public void setIndices(int[] indices) {
    m_indices = indices;
    onPropertyChange(INDICES_PROPERTY);
  }
  
  public int getIndexLowerBound() { return m_indexLowerBound; }
  
  public void setIndexLowerBound(int indexLowerBound) {
    if (m_indexLowerBound != indexLowerBound) {
      m_indexLowerBound = indexLowerBound;
      onPropertyChange(INDEX_LOWER_BOUND_PROPERTY);
    }
  }
  
  public int getIndexUpperBound() { return m_indexUpperBound; }
  
  public void setIndexUpperBound(int indexUpperBound) {
    if (m_indexUpperBound != indexUpperBound) {
      m_indexUpperBound = indexUpperBound;
      onPropertyChange(INDEX_UPPER_BOUND_PROPERTY);
    }
  }
  


















  public int getIndexCount()
  {
    if (m_indices != null) {
      return m_indices.length;
    }
    return 0;
  }
  
  public int getTriangleCount() {
    if (m_indices != null) {
      return m_indices.length / 3;
    }
    return 0;
  }
  
  public static void storeIndices(int[] indices, OutputStream os) throws IOException
  {
    BufferedOutputStream bos = new BufferedOutputStream(os);
    DataOutputStream dos = new DataOutputStream(bos);
    dos.writeInt(2);
    dos.writeInt(indices.length);
    for (int i = 0; i < indices.length; i++) {
      dos.writeInt(indices[i]);
    }
    dos.flush();
  }
  
  public static int[] loadIndices(InputStream is) throws IOException { int[] indices = null;
    BufferedInputStream bis = new BufferedInputStream(is);
    DataInputStream dis = new DataInputStream(bis);
    int version = dis.readInt();
    if (version == 1) {
      int faceCount = dis.readInt();
      int verticesPerFace = dis.readInt();
      indices = new int[faceCount * 3];
      for (int i = 0; i < indices.length; i++) {
        indices[i] = dis.readInt();
      }
    } else if (version == 2) {
      int indicesCount = dis.readInt();
      indices = new int[indicesCount];
      for (int i = 0; i < indices.length; i++) {
        indices[i] = dis.readInt();
      }
    } else {
      throw new RuntimeException(Messages.getString("invalid_file_version__") + version);
    }
    return indices;
  }
}

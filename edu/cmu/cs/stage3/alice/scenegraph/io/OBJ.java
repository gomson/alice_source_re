package edu.cmu.cs.stage3.alice.scenegraph.io;

import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame;
import edu.cmu.cs.stage3.alice.scenegraph.Transformable;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.math.MathUtilities;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.Vector;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class OBJ
{
  public OBJ() {}
  
  private static double getNextNumber(StreamTokenizer streamTokenizer)
  {
    try
    {
      streamTokenizer.nextToken();
      if (ttype == -2) {
        double f = nval;
        streamTokenizer.nextToken();
        if ((ttype == -3) && 
          (sval.startsWith("E"))) {
          int exponent = Integer.parseInt(sval.substring(1));
          return f * Math.pow(10.0D, exponent);
        }
        
        streamTokenizer.pushBack();
        return f;
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return NaN.0D;
  }
  
  public static Object[] load(InputStream is) throws IOException {
    BufferedReader r = new BufferedReader(new java.io.InputStreamReader(is));
    StreamTokenizer st = new StreamTokenizer(r);
    st.commentChar(35);
    st.slashSlashComments(false);
    st.slashStarComments(false);
    st.whitespaceChars(47, 47);
    st.parseNumbers();
    Vector xyzs = new Vector();
    Vector ijks = new Vector();
    Vector uvs = new Vector();
    Vector fs = new Vector();
    while (st.nextToken() == -3) {
      if (sval.startsWith("vt")) {
        double[] uv = new double[3];
        uv[0] = getNextNumber(st);
        uv[1] = getNextNumber(st);
        uvs.addElement(uv);
      } else if (sval.startsWith("vn")) {
        double[] ijk = new double[3];
        ijk[0] = getNextNumber(st);
        ijk[1] = getNextNumber(st);
        ijk[2] = getNextNumber(st);
        ijks.addElement(ijk);
      } else if (sval.startsWith("v")) {
        double[] xyz = new double[3];
        xyz[0] = getNextNumber(st);
        xyz[1] = getNextNumber(st);
        xyz[2] = getNextNumber(st);
        xyzs.addElement(xyz);
      } else { if (!sval.startsWith("f")) break;
        Vector f = new Vector();
        while (st.nextToken() == -2) {
          f.addElement(new Integer((int)nval - 1));
        }
        st.pushBack();
        fs.addElement(f);
      }
    }
    

    int nVertexCount = xyzs.size();
    Vertex3d[] vertices = new Vertex3d[nVertexCount];
    double[] ijkDefault = new double[3];
    ijkDefault[0] = 0.0D;
    ijkDefault[1] = 1.0D;
    ijkDefault[2] = 0.0D;
    double[] uvDefault = new double[2];
    uvDefault[0] = 0.0D;
    uvDefault[1] = 0.0D;
    for (int v = 0; v < nVertexCount; v++) {
      double[] xyz = (double[])xyzs.elementAt(v);
      double[] ijk;
      try
      {
        ijk = (double[])ijks.elementAt(v);
      } catch (ArrayIndexOutOfBoundsException e) { double[] ijk;
        ijk = ijkDefault;
      }
      double[] uv;
      try { uv = (double[])uvs.elementAt(v);
      } catch (ArrayIndexOutOfBoundsException e) { double[] uv;
        uv = uvDefault;
      }
      vertices[v] = Vertex3d.createXYZIJKUV(xyz[0], xyz[1], xyz[2], ijk[0], ijk[1], ijk[2], (float)uv[0], (float)uv[1]);
    }
    
    int[] indices = new int[fs.size() * 3];
    int i = 0;
    for (int f = 0; f < fs.size(); f++) {
      Vector face = (Vector)fs.elementAt(f);
      switch (face.size()) {
      case 3: 
        indices[(i++)] = ((Integer)face.elementAt(0)).intValue();
        indices[(i++)] = ((Integer)face.elementAt(1)).intValue();
        indices[(i++)] = ((Integer)face.elementAt(2)).intValue();
        break;
      case 6: 
        indices[(i++)] = ((Integer)face.elementAt(0)).intValue();
        indices[(i++)] = ((Integer)face.elementAt(2)).intValue();
        indices[(i++)] = ((Integer)face.elementAt(4)).intValue();
        break;
      case 9: 
        indices[(i++)] = ((Integer)face.elementAt(0)).intValue();
        indices[(i++)] = ((Integer)face.elementAt(3)).intValue();
        indices[(i++)] = ((Integer)face.elementAt(6)).intValue();
        break;
      case 4: case 5: case 7: case 8: default: 
        throw new RuntimeException(edu.cmu.cs.stage3.lang.Messages.getString("unhandled_face_index_size"));
      }
    }
    Object[] array = { vertices, indices };
    return array;
  }
  
  public static void store(OutputStream os, Vertex3d[] vertices, int[] indices, Matrix4d m, String groupNames) throws IOException { if ((vertices != null) && (indices != null)) {
      BufferedOutputStream bos = new BufferedOutputStream(os);
      PrintWriter pw = new PrintWriter(bos);
      if (groupNames != null) {
        pw.println("g " + groupNames);
      }
      for (int lcv = 0; lcv < vertices.length; lcv++) {
        double x = position.x;
        double y = position.y;
        double z = position.z;
        double i = normal.x;
        double j = normal.y;
        double k = normal.z;
        double u = textureCoordinate0.x;
        double v = textureCoordinate0.y;
        if (m != null) {
          Vector4d xyzw = MathUtilities.multiply(x, y, z, 1.0D, m);
          Vector4d ijkw = MathUtilities.multiply(i, j, k, 0.0D, m);
          x = x;
          y = y;
          z = z;
          i = x;
          j = y;
          k = z;
        }
        pw.print("v ");
        pw.print(x);
        pw.print(" ");
        pw.print(y);
        pw.print(" ");
        pw.print(z);
        pw.println();
        pw.print("vt ");
        pw.print(u);
        pw.print(" ");
        pw.print(v);
        pw.println();
        pw.print("vn ");
        pw.print(i);
        pw.print(" ");
        pw.print(j);
        pw.print(" ");
        pw.print(k);
        pw.println();
      }
      for (int i = 0; i < indices.length; i += 3) {
        pw.print("f ");
        for (int j = 0; j < 3; j++) {
          int a = indices[(i + j)] - vertices.length;
          pw.print(a + "/" + a + "/" + a + " ");
        }
        pw.println();
      }
      pw.flush();
    }
  }
  
  private static void store(OutputStream os, IndexedTriangleArray ita, Matrix4d m, String groupNames) throws IOException { store(os, ita.getVertices(), ita.getIndices(), m, groupNames); }
  
  private static void store(OutputStream os, Visual visual, Matrix4d m, String groupNames) throws IOException {
    edu.cmu.cs.stage3.alice.scenegraph.Geometry geometry = visual.getGeometry();
    if ((geometry instanceof IndexedTriangleArray))
      store(os, (IndexedTriangleArray)geometry, m, groupNames);
  }
  
  private static void store(OutputStream os, Transformable transformable, ReferenceFrame root, String groupNames) throws IOException {
    String name = transformable.getName();
    if (name != null) {
      int k = name.indexOf(".m_sgTransformable");
      if (k != -1) {
        name = name.substring(0, k);
      }
    } else {
      name = "null";
    }
    if (groupNames.length() > 0)
    {
      groupNames = name + "_" + groupNames;
    } else {
      groupNames = name;
    }
    for (int i = 0; i < transformable.getChildCount(); i++) {
      edu.cmu.cs.stage3.alice.scenegraph.Component child = transformable.getChildAt(i);
      if ((child instanceof Transformable)) {
        store(os, (Transformable)child, root, groupNames);
      } else if ((child instanceof Visual))
        store(os, (Visual)child, transformable.getTransformation(root), groupNames);
    }
  }
  
  public static void store(OutputStream os, Transformable transformable) throws IOException {
    store(os, transformable, transformable, "");
  }
}

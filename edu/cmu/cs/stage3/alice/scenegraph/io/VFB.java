package edu.cmu.cs.stage3.alice.scenegraph.io;

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;








public class VFB
{
  public VFB() {}
  
  public static Vertex3d[] loadVertices(InputStream is)
    throws IOException, FileNotFoundException
  {
    return (Vertex3d[])load(new BufferedInputStream(is))[0];
  }
  
  public static int[] loadIndices(InputStream is) throws IOException, FileNotFoundException { return (int[])load(new BufferedInputStream(is))[1]; }
  
  public static Object[] load(BufferedInputStream bis) throws IOException, FileNotFoundException {
    int nByteCount = bis.available();
    byte[] byteArray = new byte[nByteCount];
    bis.read(byteArray);
    
    for (int nByteIndex = 0; nByteIndex < nByteCount; nByteIndex += 4)
    {
      byte b = byteArray[nByteIndex];
      byteArray[nByteIndex] = byteArray[(nByteIndex + 3)];
      byteArray[(nByteIndex + 3)] = b;
      b = byteArray[(nByteIndex + 1)];
      byteArray[(nByteIndex + 1)] = byteArray[(nByteIndex + 2)];
      byteArray[(nByteIndex + 2)] = b;
    }
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);
    Object[] verticesAndIndices = new Object[2];
    int nVersion = dis.readInt();
    if (nVersion == 1) {
      int vertexCount = dis.readInt();
      Vertex3d[] vertices = new Vertex3d[vertexCount];
      for (int i = 0; i < vertices.length; i++) {
        vertices[i] = new Vertex3d(19);
        position.x = (-dis.readFloat());
        position.y = dis.readFloat();
        position.z = dis.readFloat();
        normal.x = (-dis.readFloat());
        normal.y = dis.readFloat();
        normal.z = dis.readFloat();
        textureCoordinate0.x = dis.readFloat();
        textureCoordinate0.y = dis.readFloat();
      }
      
      int faceCount = dis.readInt();
      int faceDataCount = dis.readInt();
      int verticesPerFace = dis.readInt();
      int[] indices = new int[faceCount * 3];
      int i = 0;
      for (int f = 0; f < faceCount; f++) { int length;
        int length;
        if (verticesPerFace == 0) {
          length = dis.readInt();
        } else {
          length = verticesPerFace;
        }
        indices[(i + 0)] = dis.readInt();
        indices[(i + 1)] = dis.readInt();
        indices[(i + 2)] = dis.readInt();
        i += 3;
        for (int lcv = 3; lcv < length; lcv++) {
          dis.readInt();
        }
      }
      verticesAndIndices[0] = vertices;
      verticesAndIndices[1] = indices;
    }
    return verticesAndIndices;
  }
  
  private static void store(BufferedOutputStream bos, int i) throws IOException { bos.write((byte)(i & 0xFF));
    bos.write((byte)(i >> 8 & 0xFF));
    bos.write((byte)(i >> 16 & 0xFF));
    bos.write((byte)(i >> 24 & 0xFF));
  }
  
  private static void store(BufferedOutputStream bos, float f) throws IOException { store(bos, Float.floatToIntBits(f)); }
  
  public static void store(OutputStream os, Vertex3d[] vertices, int[] indices) throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(os);
    store(bos, 1);
    if (vertices != null) {
      store(bos, vertices.length);
      for (int i = 0; i < vertices.length; i++) {
        store(bos, (float)position.x);
        store(bos, (float)position.y);
        store(bos, (float)position.z);
        store(bos, (float)normal.x);
        store(bos, (float)normal.y);
        store(bos, (float)normal.z);
        store(bos, textureCoordinate0.x);
        store(bos, textureCoordinate0.y);
      }
    } else {
      store(bos, 0);
    }
    if (indices != null) {
      store(bos, indices.length / 3);
      store(bos, indices.length);
      store(bos, 3);
      for (int i = 0; i < indices.length; i += 3) {
        store(bos, indices[(i + 2)]);
        store(bos, indices[(i + 1)]);
        store(bos, indices[i]);
      }
    } else {
      store(bos, 0);
    }
    bos.flush();
  }
}

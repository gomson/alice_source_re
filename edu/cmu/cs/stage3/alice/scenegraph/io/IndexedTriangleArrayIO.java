package edu.cmu.cs.stage3.alice.scenegraph.io;

import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;













public class IndexedTriangleArrayIO
{
  public IndexedTriangleArrayIO() {}
  
  private static final String[] s_codecNames = { "obj", "vfb" };
  private static final String[] s_objExtensions = { "obj" };
  private static final String[] s_vfbExtensions = { "vfb" };
  
  public static String[] getCodecNames() { return s_codecNames; }
  
  public static String[] getExtensionsForCodec(String codecName) {
    if (codecName.equals("obj"))
      return s_objExtensions;
    if (codecName.equals("vfb")) {
      return s_vfbExtensions;
    }
    return null;
  }
  
  public static String mapExtensionToCodecName(String extension) {
    String[] codecNames = getCodecNames();
    for (int i = 0; i < codecNames.length; i++) {
      String[] extensions = getExtensionsForCodec(codecNames[i]);
      for (int j = 0; j < extensions.length; j++) {
        if (extensions[j].equalsIgnoreCase(extension)) {
          return codecNames[i];
        }
      }
    }
    return null;
  }
  
  private static Object[] decodeOBJ(BufferedInputStream bufferedInputStream) throws IOException
  {
    return OBJ.load(bufferedInputStream);
  }
  
  private static Object[] decodeVFB(BufferedInputStream bufferedInputStream) throws IOException { return VFB.load(bufferedInputStream); }
  
  public static IndexedTriangleArray decode(String codecName, InputStream inputStream) throws IOException {
    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
    Object[] array;
    if (codecName.equals("obj")) {
      array = decodeOBJ(bufferedInputStream); } else { Object[] array;
      if (codecName.equals("vfb")) {
        array = decodeVFB(bufferedInputStream);
      } else
        throw new RuntimeException("unknown codec: " + codecName); }
    Object[] array;
    if (array != null) {
      IndexedTriangleArray ita = new IndexedTriangleArray();
      ita.setVertices((Vertex3d[])array[0]);
      ita.setIndices((int[])array[1]);
      return ita;
    }
    return null;
  }
  
  private static void encodeOBJ(BufferedOutputStream bufferedOutputStream, Vertex3d[] vertices, int[] indices)
    throws IOException
  {
    OBJ.store(bufferedOutputStream, vertices, indices, null, null);
  }
  
  private static void encodeVFB(BufferedOutputStream bufferedOutputStream, Vertex3d[] vertices, int[] indices) throws IOException { VFB.store(bufferedOutputStream, vertices, indices); }
  
  public static void encode(String codecName, OutputStream outputStream, IndexedTriangleArray ita) throws IOException {
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
    if (codecName.equals("obj")) {
      encodeOBJ(bufferedOutputStream, ita.getVertices(), ita.getIndices());
    } else if (codecName.equals("vfb")) {
      encodeVFB(bufferedOutputStream, ita.getVertices(), ita.getIndices());
    } else {
      throw new RuntimeException("unknown codec: " + codecName);
    }
    bufferedOutputStream.flush();
  }
}

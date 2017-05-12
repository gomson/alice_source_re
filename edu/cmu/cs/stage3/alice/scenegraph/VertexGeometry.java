package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Sphere;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;












public abstract class VertexGeometry
  extends Geometry
{
  public static final Property VERTICES_PROPERTY = new Property(VertexGeometry.class, "VERTICES");
  public static final Property VERTEX_LOWER_BOUND_PROPERTY = new Property(VertexGeometry.class, "VERTEX_LOWER_BOUND");
  public static final Property VERTEX_UPPER_BOUND_PROPERTY = new Property(VertexGeometry.class, "VERTEX_UPPER_BOUND");
  private Vertex3d[] m_vertices = null;
  private int m_vertexLowerBound = 0;
  private int m_vertexUpperBound = -1;
  
  public VertexGeometry() {}
  
  protected void updateBoundingBox() { if ((m_vertices != null) && (m_vertices.length > 0)) {
      if (m_vertices[0] != null) {
        Point3d min = new Point3d();
        Point3d max = new Point3d();
        
        Vertex3d v0 = m_vertices[0];
        min.set(position);
        max.set(position);
        for (int i = 1; i < m_vertices.length; i++) {
          Vertex3d vi = m_vertices[i];
          x = Math.min(position.x, x);
          y = Math.min(position.y, y);
          z = Math.min(position.z, z);
          x = Math.max(position.x, x);
          y = Math.max(position.y, y);
          z = Math.max(position.z, z);
        }
        Vector3d minimum = new Vector3d(x, y, z);
        Vector3d maximum = new Vector3d(x, y, z);
        m_boundingBox = new Box(minimum, maximum);
      } else {
        throw new RuntimeException(this + " " + Messages.getString("vertex__0___has_somehow_become_null_"));
      }
    }
    else
      m_boundingBox = null;
  }
  
  private static double getDistanceSquaredBetween(Vertex3d vertex, Vector3d vector) {
    double dx = position.x - x;
    double dy = position.y - y;
    double dz = position.z - z;
    return dx * dx + dy * dy + dz * dz;
  }
  
  protected void updateBoundingSphere() {
    Box box = getBoundingBox();
    if (box != null) {
      Vector3d center = box.getCenter();
      double distanceSquared = 0.0D;
      for (int i = 0; i < m_vertices.length; i++) {
        double d2 = getDistanceSquaredBetween(m_vertices[i], center);
        distanceSquared = Math.max(d2, distanceSquared);
      }
      m_boundingSphere = new Sphere(center, Math.sqrt(distanceSquared));
    }
  }
  

  public Vertex3d[] getVertices() { return m_vertices; }
  
  public void setVertices(Vertex3d[] vertices) {
    m_vertices = vertices;
    onPropertyChange(VERTICES_PROPERTY);
    onBoundsChange();
  }
  

  public int getVertexLowerBound() { return m_vertexLowerBound; }
  
  public void setVertexLowerBound(int vertexLowerBound) {
    if (m_vertexLowerBound != vertexLowerBound) {
      m_vertexLowerBound = vertexLowerBound;
      onPropertyChange(VERTEX_LOWER_BOUND_PROPERTY);
    }
  }
  

  public int getVertexUpperBound() { return m_vertexUpperBound; }
  
  public void setVertexUpperBound(int vertexUpperBound) {
    if (m_vertexUpperBound != vertexUpperBound) {
      m_vertexUpperBound = vertexUpperBound;
      onPropertyChange(VERTEX_UPPER_BOUND_PROPERTY);
    }
  }
  
  public int getVertexCount()
  {
    if (m_vertices != null) {
      return m_vertices.length;
    }
    return 0;
  }
  

  public void transform(Matrix4d trans)
  {
    Vertex3d[] vertices = getVertices();
    for (int i = 0; i < vertices.length; i++) {
      vertices[i].transform(trans);
    }
    setVertices(vertices);
  }
  
  public static Vertex3d[] loadVertices(InputStream is) throws IOException {
    Vertex3d[] vertices = null;
    BufferedInputStream bis = new BufferedInputStream(is);
    DataInputStream dis = new DataInputStream(bis);
    int version = dis.readInt();
    if (version == 1) {
      int vertexCount = dis.readInt();
      vertices = new Vertex3d[vertexCount];
      for (int i = 0; i < vertices.length; i++) {
        vertices[i] = new Vertex3d(19);
        position.x = dis.readDouble();
        position.y = dis.readDouble();
        position.z = dis.readDouble();
        normal.x = dis.readDouble();
        normal.y = dis.readDouble();
        normal.z = dis.readDouble();
        textureCoordinate0.x = ((float)dis.readDouble());
        textureCoordinate0.y = ((float)dis.readDouble());
      }
    } else if (version == 2) {
      int vertexCount = dis.readInt();
      vertices = new Vertex3d[vertexCount];
      for (int i = 0; i < vertices.length; i++) {
        int format = dis.readInt();
        vertices[i] = new Vertex3d(format);
        if ((format & 0x1) != 0) {
          position.x = dis.readDouble();
          position.y = dis.readDouble();
          position.z = dis.readDouble();
        }
        if ((format & 0x2) != 0) {
          normal.x = dis.readDouble();
          normal.y = dis.readDouble();
          normal.z = dis.readDouble();
        }
        if ((format & 0x4) != 0) {
          diffuseColor.red = ((float)dis.readDouble());
          diffuseColor.green = ((float)dis.readDouble());
          diffuseColor.blue = ((float)dis.readDouble());
          diffuseColor.alpha = ((float)dis.readDouble());
        }
        





        if ((format & 0x10) != 0) {
          textureCoordinate0.x = ((float)dis.readDouble());
          textureCoordinate0.y = ((float)dis.readDouble());
        }
      }
    } else if (version == 3) {
      int vertexCount = dis.readInt();
      vertices = new Vertex3d[vertexCount];
      for (int i = 0; i < vertices.length; i++) {
        int format = dis.readInt();
        vertices[i] = new Vertex3d(format);
        if ((format & 0x1) != 0) {
          position.x = dis.readDouble();
          position.y = dis.readDouble();
          position.z = dis.readDouble();
        }
        if ((format & 0x2) != 0) {
          normal.x = dis.readDouble();
          normal.y = dis.readDouble();
          normal.z = dis.readDouble();
        }
        if ((format & 0x4) != 0) {
          diffuseColor.red = dis.readFloat();
          diffuseColor.green = dis.readFloat();
          diffuseColor.blue = dis.readFloat();
          diffuseColor.alpha = dis.readFloat();
        }
        if ((format & 0x8) != 0) {
          specularHighlightColor.red = dis.readFloat();
          specularHighlightColor.green = dis.readFloat();
          specularHighlightColor.blue = dis.readFloat();
          specularHighlightColor.alpha = dis.readFloat();
        }
        if ((format & 0x10) != 0) {
          textureCoordinate0.x = dis.readFloat();
          textureCoordinate0.y = dis.readFloat();
        }
      }
    } else {
      throw new RuntimeException(Messages.getString("invalid_file_version__") + version);
    }
    return vertices;
  }
  
  public static void storeVertices(Vertex3d[] vertices, OutputStream os) throws IOException { BufferedOutputStream bos = new BufferedOutputStream(os);
    DataOutputStream dos = new DataOutputStream(bos);
    dos.writeInt(3);
    dos.writeInt(vertices.length);
    for (int i = 0; i < vertices.length; i++) {
      int format = vertices[i].getFormat();
      dos.writeInt(format);
      if ((format & 0x1) != 0) {
        dos.writeDouble(position.x);
        dos.writeDouble(position.y);
        dos.writeDouble(position.z);
      }
      if ((format & 0x2) != 0) {
        dos.writeDouble(normal.x);
        dos.writeDouble(normal.y);
        dos.writeDouble(normal.z);
      }
      if ((format & 0x4) != 0) {
        dos.writeFloat(diffuseColor.red);
        dos.writeFloat(diffuseColor.green);
        dos.writeFloat(diffuseColor.blue);
        dos.writeFloat(diffuseColor.alpha);
      }
      if ((format & 0x8) != 0) {
        dos.writeFloat(specularHighlightColor.red);
        dos.writeFloat(specularHighlightColor.green);
        dos.writeFloat(specularHighlightColor.blue);
        dos.writeFloat(specularHighlightColor.alpha);
      }
      if ((format & 0x10) != 0) {
        dos.writeFloat(textureCoordinate0.x);
        dos.writeFloat(textureCoordinate0.y);
      }
    }
    dos.flush();
  }
}

package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Vector4;
import java.io.Serializable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;



















public class Vertex3d
  implements Cloneable, Serializable
{
  public static final int FORMAT_POSITION = 1;
  public static final int FORMAT_NORMAL = 2;
  public static final int FORMAT_DIFFUSE_COLOR = 4;
  public static final int FORMAT_SPECULAR_HIGHLIGHT_COLOR = 8;
  public static final int FORMAT_TEXTURE_COORDINATE_0 = 16;
  public Point3d position = null;
  public Vector3d normal = null;
  public Color diffuseColor = null;
  public Color specularHighlightColor = null;
  public TexCoord2f textureCoordinate0 = null;
  
  public Vertex3d() {}
  
  public Vertex3d(int format) {
    if ((format & 0x1) != 0) {
      position = new Point3d();
    }
    if ((format & 0x2) != 0) {
      normal = new Vector3d();
    }
    if ((format & 0x4) != 0) {
      diffuseColor = new Color();
    }
    if ((format & 0x8) != 0) {
      specularHighlightColor = new Color();
    }
    if ((format & 0x10) != 0)
      textureCoordinate0 = new TexCoord2f();
  }
  
  public Vertex3d(Point3d position, Vector3d normal, Color diffuseColor, Color specularHighlightColor, TexCoord2f textureCoordinate0) {
    this.position = position;
    this.normal = normal;
    this.diffuseColor = diffuseColor;
    this.specularHighlightColor = specularHighlightColor;
    this.textureCoordinate0 = textureCoordinate0;
  }
  
  public static Vertex3d createXYZIJKUV(double x, double y, double z, double i, double j, double k, float u, float v) {
    Vertex3d vertex = new Vertex3d();
    position = new Point3d(x, y, z);
    normal = new Vector3d(i, j, k);
    textureCoordinate0 = new TexCoord2f(u, v);
    return vertex;
  }
  

  public synchronized Object clone()
  {
    try
    {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object o) {
    if ((o instanceof Vertex3d)) {
      Vertex3d v = (Vertex3d)o;
      if (position == null) {
        if (position != null) {
          return false;
        }
      }
      else if (!position.equals(position)) {
        return false;
      }
      
      if (normal == null) {
        if (normal != null) {
          return false;
        }
      }
      else if (!normal.equals(normal)) {
        return false;
      }
      
      if (diffuseColor == null) {
        if (diffuseColor != null) {
          return false;
        }
      }
      else if (!diffuseColor.equals(diffuseColor)) {
        return false;
      }
      
      if (specularHighlightColor == null) {
        if (specularHighlightColor != null) {
          return false;
        }
      }
      else if (!specularHighlightColor.equals(specularHighlightColor)) {
        return false;
      }
      
      if (textureCoordinate0 == null) {
        if (textureCoordinate0 != null) {
          return false;
        }
      }
      else if (!textureCoordinate0.equals(textureCoordinate0)) {
        return false;
      }
      
      return true;
    }
    return false;
  }
  
  public int getFormat() {
    int format = 0;
    if (position != null) {
      format |= 0x1;
    }
    if (normal != null) {
      format |= 0x2;
    }
    if (diffuseColor != null) {
      format |= 0x4;
    }
    if (specularHighlightColor != null) {
      format |= 0x8;
    }
    if (textureCoordinate0 != null) {
      format |= 0x10;
    }
    return format;
  }
  
  public void translate(double x, double y, double z) { position.x += x;
    position.y += y;
    position.z += z;
  }
  
  public void scale(double x, double y, double z) { position.x *= x;
    position.y *= y;
    position.z *= z;
  }
  
  public void transform(Matrix4d trans) { if (position != null)
    {
      Vector4 xyz1 = new Vector4(position.x, position.y, position.z, 1.0D);
      xyz1.transform(trans);
      position.x = x;
      position.y = y;
      position.z = z;
    }
    if (normal != null)
    {
      Vector4 ijk0 = new Vector4(normal.x, normal.y, normal.z, 0.0D);
      ijk0.transform(trans);
      normal.x = x;
      normal.y = y;
      normal.z = z;
    }
  }
  
  public String toString() {
    return "edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[position=" + position + ",normal=" + normal + ",diffuseColor=" + diffuseColor + ",specularHighlightColor=" + specularHighlightColor + ",textureCoordinate0=" + textureCoordinate0 + "]";
  }
}

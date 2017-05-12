package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;

public abstract class VertexGeometryProxy
  extends GeometryProxy
{
  public VertexGeometryProxy() {}
  
  protected abstract void onVerticesFormatAndLengthChange(int paramInt1, int paramInt2);
  
  protected abstract void onVerticesVertexPositionChange(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3);
  
  protected abstract void onVerticesVertexNormalChange(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3);
  
  protected abstract void onVerticesVertexDiffuseColorChange(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  protected abstract void onVerticesVertexSpecularHighlightColorChange(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  protected abstract void onVerticesVertexTextureCoordinate0Change(int paramInt, float paramFloat1, float paramFloat2);
  
  protected abstract void onVerticesBeginChange();
  
  protected abstract void onVerticesEndChange();
  
  protected abstract void onVertexLowerBoundChange(int paramInt);
  
  protected abstract void onVertexUpperBoundChange(int paramInt);
  
  private void onVerticesChange(Vertex3d[] vertices)
  {
    if (vertices != null) {
      if (vertices.length > 0) {
        if (vertices[0] != null) {
          int format = vertices[0].getFormat();
          onVerticesFormatAndLengthChange(format, vertices.length);
          onVerticesBeginChange();
          for (int i = 0; i < vertices.length; i++) {
            Vertex3d vertex = vertices[i];
            if (position != null) {
              onVerticesVertexPositionChange(i, position.x, position.y, position.z);
            }
            if (normal != null) {
              onVerticesVertexNormalChange(i, normal.x, normal.y, normal.z);
            }
            if (diffuseColor != null) {
              onVerticesVertexDiffuseColorChange(i, diffuseColor.red, diffuseColor.green, diffuseColor.blue, diffuseColor.alpha);
            }
            if (specularHighlightColor != null) {
              onVerticesVertexSpecularHighlightColorChange(i, specularHighlightColor.red, specularHighlightColor.green, specularHighlightColor.blue, specularHighlightColor.alpha);
            }
            if (textureCoordinate0 != null) {
              onVerticesVertexTextureCoordinate0Change(i, textureCoordinate0.x, textureCoordinate0.y);
            }
          }
          onVerticesEndChange();
        }
      } else {
        onVerticesFormatAndLengthChange(0, 0);
        onVerticesBeginChange();
        onVerticesEndChange();
      }
    } else {
      onVerticesFormatAndLengthChange(0, 0);
      onVerticesBeginChange();
      onVerticesEndChange();
    }
  }
  
  protected void changed(Property property, Object value) {
    if (property == VertexGeometry.VERTICES_PROPERTY) {
      onVerticesChange((Vertex3d[])value);
    } else if (property == VertexGeometry.VERTEX_LOWER_BOUND_PROPERTY) {
      onVertexLowerBoundChange(((Integer)value).intValue());
    } else if (property == VertexGeometry.VERTEX_UPPER_BOUND_PROPERTY) {
      onVertexUpperBoundChange(((Integer)value).intValue());
    } else {
      super.changed(property, value);
    }
  }
}

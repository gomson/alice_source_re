package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.vecmath.Matrix4d;















public abstract class RenderTarget
  extends AbstractRenderTarget
{
  RenderTarget(Renderer renderer)
  {
    super(renderer);
  }
  
  public void markDirty() {}
  
  public boolean updateIsRequired() {
    return false;
  }
  
  public Matrix4d getProjectionMatrix(Camera camera) { return null; }
  
  public double[] getActualPlane(OrthographicCamera sgOrthographicCamera) {
    return null;
  }
  
  public double[] getActualPlane(PerspectiveCamera sgPerspectiveCamera) { return null; }
  
  public double getActualHorizontalViewingAngle(SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
    return NaN.0D;
  }
  
  public double getActualVerticalViewingAngle(SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) { return NaN.0D; }
  
  public Rectangle getActualViewport(Camera sgCamera) {
    return new Rectangle(getSize());
  }
  
  public Rectangle getViewport(Camera sgCamera) { return null; }
  
  public void setViewport(Camera sgCamera, Rectangle viewport) {}
  
  public boolean isLetterboxedAsOpposedToDistorted(Camera sgCamera) {
    return true;
  }
  
  public void setIsLetterboxedAsOpposedToDistorted(Camera sgCamera, boolean isLetterboxedAsOpposedToDistorted) {}
  
  public Image getOffscreenImage() {
    return null;
  }
  
  public Image getZBufferImage() { return null; }
  
  public Image getImage(TextureMap textureMap) {
    return null;
  }
  
  public Graphics getGraphics(TextureMap textureMap) { return null; }
  
  public void copyOffscreenImageToTextureMap(TextureMap textureMap) {}
  
  public void clearAndRenderOffscreen() {}
  
  public boolean rendersOnEdgeTrianglesAsLines(OrthographicCamera orthographicCamera)
  {
    return false;
  }
  
  public void setRendersOnEdgeTrianglesAsLines(OrthographicCamera orthographicCamera, boolean rendersOnEdgeTrianglesAsLines) {}
  
  public Graphics getOffscreenGraphics() { return null; }
  
  public PickInfo pick(int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired)
  {
    return null;
  }
  
  private double m_silhouetteThickness = 0.0D;
  
  public void setSilhouetteThickness(double silhouetteThickness) { m_silhouetteThickness = silhouetteThickness; }
  
  public double getSilhouetteThickness() {
    return m_silhouetteThickness;
  }
}

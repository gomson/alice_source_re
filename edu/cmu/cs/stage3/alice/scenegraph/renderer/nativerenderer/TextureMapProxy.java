package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import edu.cmu.cs.stage3.image.ImageUtilities;
import java.awt.Image;












public abstract class TextureMapProxy
  extends ElementProxy
{
  public TextureMapProxy() {}
  
  protected abstract void onImageChange(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  protected abstract void onFormatChange(int paramInt);
  
  private void onImageChange(Image image)
  {
    if (image != null) {
      try {
        int width = ImageUtilities.getWidth(image);
        int height = ImageUtilities.getHeight(image);
        int[] pixels = ImageUtilities.getPixels(image, width, height);
        onImageChange(pixels, width, height);
      } catch (InterruptedException ie) {
        throw new RuntimeException("interrupted waiting for size");
      }
    } else {
      onImageChange(null, 0, 0);
    }
  }
  
  protected void changed(Property property, Object value) {
    if (property == TextureMap.IMAGE_PROPERTY) {
      onImageChange((Image)value);
    } else if (property == TextureMap.FORMAT_PROPERTY) {
      onFormatChange(((Integer)value).intValue());
    } else {
      super.changed(property, value);
    }
  }
  
  void setRenderTargetWithLatestImage(RenderTarget renderTarget) {
    ((TextureMap)getSceneGraphElement()).setRenderTargetWithLatestImage(renderTarget);
  }
}

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.image.ImageIO;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;













public class Dummy
  extends Model
{
  private static IndexedTriangleArray s_sgITA = null;
  private static TextureMap s_sgTexture = null;
  
  public Dummy() { isFirstClass.set(true);
    opacity.set(new Double(0.5D));
    isShowing.set(false);
    emissiveColor.set(Color.LIGHT_GRAY);
    
    if (s_sgITA == null) {
      s_sgITA = new IndexedTriangleArray();
      try {
        InputStream is = Dummy.class.getResourceAsStream("axesIndices.bin");
        s_sgITA.setIndices(IndexedTriangleArray.loadIndices(is));
        is.close();
      } catch (IOException ioe) {
        throw new ExceptionWrapper(ioe, Messages.getString("failed_to_load_axesIndices_bin_resource"));
      }
      try {
        InputStream is = Dummy.class.getResourceAsStream("axesVertices.bin");
        s_sgITA.setVertices(VertexGeometry.loadVertices(is));
        is.close();
      } catch (IOException ioe) {
        throw new ExceptionWrapper(ioe, Messages.getString("failed_to_load_axesVertices_bin_resource"));
      }
    }
    if (s_sgTexture == null) {
      s_sgTexture = new TextureMap();
      try {
        InputStream is = Dummy.class.getResourceAsStream("axesImage.png");
        Image image = ImageIO.load("png", is);
        is.close();
        s_sgTexture.setImage(image);
      } catch (IOException ioe) {
        throw new ExceptionWrapper(ioe, Messages.getString("failed_to_load_axesImage_png_resource"));
      }
    }
    getSceneGraphVisual().setGeometry(s_sgITA);
    getSceneGraphAppearance().setDiffuseColorMap(s_sgTexture);
  }
  
  protected void propertyChanged(Property property, Object value)
  {
    if (property != diffuseColorMap)
    {
      if (property != geometry)
      {

        super.propertyChanged(property, value);
      }
    }
  }
}

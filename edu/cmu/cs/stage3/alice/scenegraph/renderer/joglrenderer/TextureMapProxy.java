package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import edu.cmu.cs.stage3.image.ImageUtilities;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;



















class TextureMapProxy
  extends ElementProxy
{
  private int m_width;
  private int m_height;
  private int m_width2;
  private int m_height2;
  private float m_uFactor;
  private float m_vFactor;
  private ByteBuffer m_byteBuffer;
  
  TextureMapProxy() {}
  
  private boolean m_isPrepared = false;
  
  private TextureMap getSGTexture() {
    return (TextureMap)getSceneGraphElement();
  }
  
  public boolean isPotentiallyAlphaBlended() { return true; }
  
  public boolean isImageSet() {
    return getSGTexture().getImage() != null;
  }
  
  private static int getPowerOf2(int fold) {
    int ret = 2;
    while (ret < fold) {
      ret *= 2;
    }
    return ret;
  }
  
  public boolean prepareByteBufferIfNecessary()
  {
    if (m_isPrepared) {
      return false;
    }
    Image image = getSGTexture().getImage();
    if (image != null) {
      boolean isPotentiallyAlphaBlended = isPotentiallyAlphaBlended();
      ColorModel colorModel;
      ColorModel colorModel; if (isPotentiallyAlphaBlended) {
        colorModel = new ComponentColorModel(
          ColorSpace.getInstance(1000), 
          new int[] { 8, 8, 8, 8 }, 
          true, 
          false, 
          3, 
          0);
      } else {
        colorModel = new ComponentColorModel(
          ColorSpace.getInstance(1000), 
          new int[] { 8, 8, 8 }, 
          false, 
          false, 
          1, 
          0);
      }
      try
      {
        m_width = ImageUtilities.getWidth(image);
        m_height = ImageUtilities.getHeight(image);
      } catch (InterruptedException ie) {
        throw new ExceptionWrapper(ie, "");
      }
      m_width2 = getPowerOf2(m_width);
      m_height2 = getPowerOf2(m_height);
      
      m_uFactor = (m_width / m_width2);
      m_vFactor = (m_height / m_height2);
      int bands;
      int bands;
      if (isPotentiallyAlphaBlended) {
        bands = 4;
      } else {
        bands = 3;
      }
      
      WritableRaster raster = Raster.createInterleavedRaster(0, m_width2, m_height2, bands, null);
      BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, new Hashtable());
      
      Graphics g = bufferedImage.getGraphics();
      g.drawImage(image, 0, 0, null);
      
      byte[] data = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
      
      m_byteBuffer = ByteBuffer.allocateDirect(data.length);
      m_byteBuffer.order(ByteOrder.nativeOrder());
      m_byteBuffer.put(data, 0, data.length);
      
      m_byteBuffer.rewind();
    } else {
      m_byteBuffer = null;
      m_width = 0;
      m_height = 0;
    }
    m_isPrepared = true;
    return true;
  }
  
  public ByteBuffer getPixels()
  {
    return m_byteBuffer;
  }
  
  public int getWidth() { return m_width; }
  
  public int getWidthPowerOf2() {
    return m_width2;
  }
  
  public int getHeight() {
    return m_height;
  }
  
  public int getHeightPowerOf2() { return m_height2; }
  
  public float mapU(float u)
  {
    return u * m_uFactor;
  }
  
  public float mapV(float v) { return (1.0F - v) * m_vFactor; }
  


  protected void changed(Property property, Object value)
  {
    if (property == TextureMap.IMAGE_PROPERTY) {
      m_isPrepared = false;
    } else if (property == TextureMap.FORMAT_PROPERTY) {
      m_isPrepared = false;
    } else {
      super.changed(property, value);
    }
  }
}

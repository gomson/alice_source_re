package edu.cmu.cs.stage3.image;

import edu.cmu.cs.stage3.image.codec.ImageCodec;
import edu.cmu.cs.stage3.image.codec.ImageDecodeParam;
import edu.cmu.cs.stage3.image.codec.ImageDecoder;
import edu.cmu.cs.stage3.image.codec.ImageEncodeParam;
import edu.cmu.cs.stage3.image.codec.ImageEncoder;
import edu.cmu.cs.stage3.image.codec.PNGEncodeParam;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

public class ImageIO
{
  public ImageIO() {}
  
  private static final String[] s_codecNames = { "png", "jpeg", "tiff", "bmp", "gif" };
  private static final String[] s_pngExtensions = { "png" };
  private static final String[] s_jpegExtensions = { "jpeg", "jpg" };
  private static final String[] s_tiffExtensions = { "tiff", "tif" };
  private static final String[] s_bmpExtensions = { "bmp" };
  private static final String[] s_gifExtensions = { "gif" };
  
  public static String[] getCodecNames() { return s_codecNames; }
  
  public static String[] getExtensionsForCodec(String codecName) {
    if (codecName.equals("png"))
      return s_pngExtensions;
    if (codecName.equals("jpeg"))
      return s_jpegExtensions;
    if (codecName.equals("tiff"))
      return s_tiffExtensions;
    if (codecName.equals("bmp"))
      return s_bmpExtensions;
    if (codecName.equals("gif")) {
      return s_gifExtensions;
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
  


  public static Image load(String codecName, InputStream inputStream) throws IOException { return load(codecName, inputStream, null); }
  
  public static Image load(String codecName, InputStream inputStream, ImageDecodeParam imageDecodeParam) throws IOException { BufferedInputStream bufferedInputStream;
    BufferedInputStream bufferedInputStream;
    if ((inputStream instanceof BufferedInputStream)) {
      bufferedInputStream = (BufferedInputStream)inputStream;
    } else {
      bufferedInputStream = new BufferedInputStream(inputStream);
    }
    ImageDecoder imageDecoder = ImageCodec.createImageDecoder(codecName, bufferedInputStream, imageDecodeParam);
    RenderedImage renderedImage = imageDecoder.decodeAsRenderedImage();
    
    if ((renderedImage instanceof Image)) {
      return (Image)renderedImage;
    }
    Raster raster = renderedImage.getData();
    ColorModel colorModel = renderedImage.getColorModel();
    Hashtable properties = null;
    String[] propertyNames = renderedImage.getPropertyNames();
    if (propertyNames != null) {
      properties = new Hashtable();
      for (int i = 0; i < propertyNames.length; i++) {
        String propertyName = propertyNames[i];
        properties.put(propertyName, renderedImage.getProperty(propertyName));
      } }
    WritableRaster writableRaster;
    WritableRaster writableRaster;
    if ((raster instanceof WritableRaster)) {
      writableRaster = (WritableRaster)raster;
    } else {
      writableRaster = raster.createCompatibleWritableRaster();
    }
    BufferedImage bufferedImage = new BufferedImage(renderedImage.getColorModel(), writableRaster, colorModel.isAlphaPremultiplied(), properties);
    return bufferedImage;
  }
  

  public static void store(String codecName, OutputStream outputStream, Image image) throws InterruptedException, IOException { store(codecName, outputStream, image, null); }
  
  public static void store(String codecName, OutputStream outputStream, Image image, ImageEncodeParam imageEncodeParam) throws InterruptedException, IOException {
    int width = ImageUtilities.getWidth(image);
    int height = ImageUtilities.getHeight(image);
    


    if (codecName.equals("jpeg")) {
      Image originalImage = image;
      image = new BufferedImage(width, height, 5);
      Graphics g = image.getGraphics();
      g.drawImage(originalImage, 0, 0, new java.awt.image.ImageObserver() {
        public boolean imageUpdate(Image image, int infoflags, int x, int y, int width, int height) {
          return true;
        }
        
      });
      g.dispose(); }
    RenderedImage renderedImage;
    RenderedImage renderedImage; if ((image instanceof RenderedImage)) {
      renderedImage = (RenderedImage)image;
    } else {
      int[] pixels = ImageUtilities.getPixels(image, width, height);
      BufferedImage bufferedImage = new BufferedImage(width, height, 2);
      bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
      renderedImage = bufferedImage;
    }
    if ((imageEncodeParam == null) && 
      (codecName.equals("png"))) {
      imageEncodeParam = PNGEncodeParam.getDefaultEncodeParam(renderedImage);
    }
    BufferedOutputStream bufferedOutputStream;
    BufferedOutputStream bufferedOutputStream;
    if ((outputStream instanceof BufferedOutputStream)) {
      bufferedOutputStream = (BufferedOutputStream)outputStream;
    } else {
      bufferedOutputStream = new BufferedOutputStream(outputStream);
    }
    
    ImageEncoder imageEncoder = ImageCodec.createImageEncoder(codecName, bufferedOutputStream, imageEncodeParam);
    imageEncoder.encode(renderedImage);
    bufferedOutputStream.flush();
  }
}

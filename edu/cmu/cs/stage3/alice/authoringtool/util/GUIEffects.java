package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;








public class GUIEffects
{
  public static ImageObserver allBitsObserver = new ImageObserver() {
    public boolean imageUpdate(Image image, int infoflags, int x, int y, int width, int height) {
      if ((infoflags & 0x20) > 0) {
        return false;
      }
      return true;
    }
  };
  
  public static ImageObserver sizeObserver = new ImageObserver()
  {
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) { return (infoflags & 0x1 & 0x2) > 0; } };
  
  public GUIEffects() {}
  
  private static Color disabledBackgroundColor = AuthoringToolResources.getColor("guiEffectsDisabledBackground");
  private static Color disabledLineColor = AuthoringToolResources.getColor("guiEffectsDisabledLine");
  private static BufferedImage disabledImage;
  private static Dimension disabledImageSize = new Dimension(-1, -1);
  private static Color shadowColor = AuthoringToolResources.getColor("guiEffectsShadow");
  private static Color edgeColor = AuthoringToolResources.getColor("guiEffectsEdge");
  private static int shadowSteps = 4;
  private static double dr = edgeColor.getRed() - shadowColor.getRed();
  private static double dg = edgeColor.getGreen() - shadowColor.getGreen();
  private static double db = edgeColor.getBlue() - shadowColor.getBlue();
  private static double da = edgeColor.getAlpha() - shadowColor.getAlpha();
  private static Color troughHighlightColor = AuthoringToolResources.getColor("guiEffectsTroughHighlight");
  private static Color troughShadowColor = AuthoringToolResources.getColor("guiEffectsTroughShadow");
  
  private static void createDisabledImage(int width, int height) {
    disabledImageSize.setSize(width, height);
    disabledImage = new BufferedImage(width, height, 2);
    Graphics2D g = (Graphics2D)disabledImage.getGraphics();
    g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    g.setColor(disabledBackgroundColor);
    g.fillRect(0, 0, width, height);
    g.setColor(disabledLineColor);
    double slope = 2.0D;
    int xOffset = (int)(height / slope);
    int spacing = 10;
    for (int x = 0; x <= width + xOffset; x += spacing) {
      g.drawLine(x, 0, x - xOffset, height);
    }
  }
  
  public static void paintDisabledEffect(Graphics g, Rectangle bounds) {
    if ((width > disabledImageSizewidth) || (height > disabledImageSizeheight)) {
      createDisabledImage(width, height);
    }
    g.setClip(x, y, width, height);
    g.drawImage(disabledImage, x, y, allBitsObserver);
  }
  
  public static BufferedImage getImageScaledAndCropped(Image inputImage, double scaleFactor, Rectangle cropRect) {
    BufferedImage inputBufferedImage = new BufferedImage(inputImage.getWidth(sizeObserver), inputImage.getHeight(sizeObserver), 2);
    Graphics2D g = inputBufferedImage.createGraphics();
    g.drawImage(inputImage, 0, 0, allBitsObserver);
    
    AffineTransformOp scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(scaleFactor, scaleFactor), 2);
    BufferedImage scaledImage = scaleOp.filter(inputBufferedImage, null);
    scaledImage = scaledImage.getSubimage(x, y, Math.min(width, scaledImage.getWidth() - x), Math.min(height, scaledImage.getHeight() - y));
    BufferedImage outputImage;
    BufferedImage outputImage;
    if (AuthoringTool.getHack() != null) {
      outputImage = AuthoringTool.getHack().getJAliceFrame().getGraphicsConfiguration().createCompatibleImage(scaledImage.getWidth(sizeObserver), scaledImage.getHeight(sizeObserver), 3);
    } else {
      outputImage = new BufferedImage(scaledImage.getWidth(sizeObserver), scaledImage.getHeight(sizeObserver), 2);
    }
    g = outputImage.createGraphics();
    g.drawImage(scaledImage, 0, 0, allBitsObserver);
    
    return outputImage;
  }
  
  public static BufferedImage getImageScaledToLongestDimension(Image inputImage, int longestDimension) {
    int width = inputImage.getWidth(sizeObserver);
    int height = inputImage.getHeight(sizeObserver);
    
    double scaleFactor = width > height ? longestDimension / width : longestDimension / height;
    
    BufferedImage inputBufferedImage = new BufferedImage(width, height, 2);
    Graphics2D g = inputBufferedImage.createGraphics();
    g.drawImage(inputImage, 0, 0, allBitsObserver);
    
    AffineTransformOp scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(scaleFactor, scaleFactor), 2);
    BufferedImage scaledImage = scaleOp.filter(inputBufferedImage, null);
    
    return scaledImage;
  }
  
  public static BufferedImage getImageWithDropShadow(Image inputImage, int xOffset, int yOffset, int arcWidth, int arcHeight) {
    int width = inputImage.getWidth(sizeObserver);
    int height = inputImage.getHeight(sizeObserver);
    Rectangle imageBounds = new Rectangle(xOffset > 0 ? 0 : -xOffset + shadowSteps, yOffset > 0 ? 0 : -yOffset + shadowSteps, width, height);
    Rectangle shadowBounds = new Rectangle(xOffset > 0 ? xOffset - shadowSteps : 0, yOffset > 0 ? yOffset - shadowSteps : 0, width + shadowSteps * 2, height + shadowSteps * 2);
    BufferedImage outputImage;
    BufferedImage outputImage;
    if (AuthoringTool.getHack() != null) {
      outputImage = AuthoringTool.getHack().getJAliceFrame().getGraphicsConfiguration().createCompatibleImage(width + Math.abs(xOffset) + shadowSteps, height + Math.abs(yOffset) + shadowSteps, 3);
    } else {
      outputImage = new BufferedImage(width + Math.abs(xOffset) + shadowSteps, height + Math.abs(yOffset) + shadowSteps, 2);
    }
    Graphics2D g = outputImage.createGraphics();
    paintDropShadow(g, shadowBounds, arcWidth, arcHeight);
    g.drawImage(inputImage, x, y, allBitsObserver);
    
    return outputImage;
  }
  
  public static void paintDropShadow(Graphics g, Rectangle bounds, int arcWidth, int arcHeight) {
    if ((g instanceof Graphics2D)) {
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    Color c = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), edgeColor.getAlpha());
    for (int i = 0; i < shadowSteps; i++) {
      double portion = i / shadowSteps;
      c = new Color(edgeColor.getRed() - (int)(portion * dr), 
        edgeColor.getGreen() - (int)(portion * dg), 
        edgeColor.getBlue() - (int)(portion * db), 
        edgeColor.getAlpha() - (int)(portion * da));
      g.setColor(c);
      g.drawRoundRect(x, y, width - 1, height - 1, arcWidth, arcHeight);
      x += 1;
      y += 1;
      width -= 2;
      height -= 2;
    }
    
    g.setColor(shadowColor);
    g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
  }
  
  public static BufferedImage getImageWithColoredBorder(Image inputImage, Color color) {
    int width = inputImage.getWidth(sizeObserver);
    int height = inputImage.getHeight(sizeObserver);
    BufferedImage outputImage;
    BufferedImage outputImage;
    if (AuthoringTool.getHack() != null) {
      outputImage = AuthoringTool.getHack().getJAliceFrame().getGraphicsConfiguration().createCompatibleImage(width, height, 3);
    } else {
      outputImage = new BufferedImage(width, height, 2);
    }
    Graphics2D g = outputImage.createGraphics();
    g.drawImage(inputImage, 0, 0, allBitsObserver);
    paintColoredBorder(g, color, width, height);
    
    return outputImage;
  }
  
  public static void paintColoredBorder(Graphics g, Color color, int width, int height) {
    g.setColor(color);
    g.drawRect(0, 0, width - 1, height - 1);
    g.drawRect(1, 1, width - 3, height - 3);
  }
  
  public static void paintTrough(Graphics g, Rectangle bounds, int arcWidth, int arcHeight) {
    Object oldAntialiasing = null;
    Shape oldClip = g.getClip();
    if ((g instanceof Graphics2D)) {
      oldAntialiasing = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
      ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
      
      int halfHeight = height / 2;
      Polygon highlightClip = new Polygon();
      highlightClip.addPoint(x, y + height);
      highlightClip.addPoint(x + halfHeight, y + halfHeight);
      highlightClip.addPoint(x + width - halfHeight, y + halfHeight);
      highlightClip.addPoint(x + width, y);
      highlightClip.addPoint(x + width, y + height);
      Polygon shadowClip = new Polygon();
      shadowClip.addPoint(x, y + height);
      shadowClip.addPoint(x, y);
      shadowClip.addPoint(x + width, y);
      shadowClip.addPoint(x + width - halfHeight, y + halfHeight);
      shadowClip.addPoint(x + halfHeight, y + halfHeight);
      
      Object edgeTreatment = AuthoringToolResources.getMiscItem("tileEdgeTreatment");
      if (edgeTreatment.equals("square")) {
        ((Graphics2D)g).clip(highlightClip);
        
        g.setColor(troughHighlightColor);
        g.drawRect(x, y, width - 1, height - 1);
        
        g.setClip(oldClip);
        ((Graphics2D)g).clip(shadowClip);
        
        g.setColor(troughShadowColor);
        g.drawRect(x, y, width - 1, height - 1);
      } else {
        ((Graphics2D)g).clip(highlightClip);
        
        g.setColor(troughHighlightColor);
        g.drawRoundRect(x, y, width - 1, height - 1, arcWidth, arcHeight);
        
        g.setClip(oldClip);
        ((Graphics2D)g).clip(shadowClip);
        
        g.setColor(troughShadowColor);
        g.drawRoundRect(x, y, width - 1, height - 1, arcWidth, arcHeight);
      }
      
      g.setClip(oldClip);
      
      ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
    }
  }
}

package edu.cmu.cs.stage3.awt;

import edu.cmu.cs.stage3.image.ImageUtilities;
import java.awt.Image;

















public class SemitransparentWindow
{
  private static boolean s_successfullyLoadedLibrary;
  
  static
  {
    try
    {
      System.loadLibrary("jni_semitransparent");
      s_successfullyLoadedLibrary = true;
    }
    catch (Throwable t) {
      s_successfullyLoadedLibrary = false;
    }
  }
  
  private static native boolean isSupportedNative();
  
  public static boolean isSupported() { if (s_successfullyLoadedLibrary) {
      return isSupportedNative();
    }
    return false; }
  private native void createNative();
  private native void destroyNative();
  private native void showNative();
  private native void hideNative(); private int m_nativeData = 0;
  

  private native void setLocationOnScreenNative(int paramInt1, int paramInt2);
  
  private native void setOpacityNative(double paramDouble);
  
  private native void setImageNative(int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public SemitransparentWindow()
  {
    if (isSupported()) {
      createNative();
    }
  }
  
  protected void finalize() throws Throwable {
    if (isSupported()) {
      destroyNative();
    }
    super.finalize();
  }
  
  public void setImage(Image image) throws InterruptedException { if (isSupported()) {
      int width = ImageUtilities.getWidth(image);
      int height = ImageUtilities.getHeight(image);
      int[] pixels = ImageUtilities.getPixels(image, width, height);
      setImageNative(width, height, pixels);
    }
  }
  
  public void show() {
    if (isSupported())
      showNative();
  }
  
  public void hide() {
    if (isSupported())
      hideNative();
  }
  
  public void setLocationOnScreen(int x, int y) {
    if (isSupported())
      setLocationOnScreenNative(x, y);
  }
  
  public void setOpacity(double opacity) {
    if (isSupported()) {
      setOpacityNative(opacity);
    }
  }
}

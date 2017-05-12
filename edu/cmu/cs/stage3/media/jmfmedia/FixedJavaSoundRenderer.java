package edu.cmu.cs.stage3.media.jmfmedia;
import javax.media.PlugInManager;

public class FixedJavaSoundRenderer extends com.sun.media.renderer.audio.JavaSoundRenderer { public FixedJavaSoundRenderer() {}
  public static void usurpControlFromJavaSoundRenderer() { String OFFENDING_RENDERER_PLUGIN_NAME = com.sun.media.renderer.audio.JavaSoundRenderer.class.getName();
    javax.media.Format[] rendererInputFormats = PlugInManager.getSupportedInputFormats(OFFENDING_RENDERER_PLUGIN_NAME, 4);
    javax.media.Format[] rendererOutputFormats = PlugInManager.getSupportedOutputFormats(OFFENDING_RENDERER_PLUGIN_NAME, 4);
    
    if ((rendererInputFormats != null) || (rendererOutputFormats != null)) {
      String REPLACEMENT_RENDERER_PLUGIN_NAME = FixedJavaSoundRenderer.class.getName();
      PlugInManager.removePlugIn(OFFENDING_RENDERER_PLUGIN_NAME, 4);
      PlugInManager.addPlugIn(REPLACEMENT_RENDERER_PLUGIN_NAME, rendererInputFormats, rendererOutputFormats, 4);
    }
  }
  
  protected com.sun.media.renderer.audio.device.AudioOutput createDevice(javax.media.format.AudioFormat format)
  {
    new com.sun.media.renderer.audio.device.JavaSoundOutput()
    {
      public void setGain(double g) {
        if (gc != null) {
          g = Math.max(g, gc.getMinimum());
          g = Math.min(g, gc.getMaximum());
        }
        super.setGain(g);
      }
    };
  }
}

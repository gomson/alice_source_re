package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.ComponentProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget;












public class Renderer
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.Renderer
{
  private static final String RENDERER_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.";
  private static final String SCENEGRAPH_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.";
  
  static
  {
    System.loadLibrary("jni_directx7renderer");
  }
  
  public Renderer() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void pick(ComponentProxy paramComponentProxy, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt1, boolean[] paramArrayOfBoolean, int[] paramArrayOfInt2, double[] paramArrayOfDouble);
  
  protected native void internalSetIsSoftwareEmulationForced(boolean paramBoolean);
  
  protected boolean requiresHierarchyAndAbsoluteTransformationListening() {
    return true;
  }
  
  protected boolean requiresBoundListening() {
    return true;
  }
  
  protected edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter createRenderTargetAdapter(RenderTarget renderTarget)
  {
    return new RenderTargetAdapter(renderTarget);
  }
  
  protected edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderCanvas createRenderCanvas(OnscreenRenderTarget onscreenRenderTarget) {
    return new RenderCanvas(onscreenRenderTarget);
  }
  


  private static final int SCENEGRAPH_PACKAGE_NAME_COUNT = "edu.cmu.cs.stage3.alice.scenegraph.".length();
  
  protected AbstractProxy createProxyFor(Element sgElement) {
    Class sgClass = sgElement.getClass();
    while (sgClass != null) {
      String className = sgClass.getName();
      if (className.startsWith("edu.cmu.cs.stage3.alice.scenegraph.")) {
        break;
      }
      sgClass = sgClass.getSuperclass();
    }
    try
    {
      Class proxyClass = Class.forName("edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer." + sgClass.getName().substring(SCENEGRAPH_PACKAGE_NAME_COUNT) + "Proxy");
      return (AbstractProxy)proxyClass.newInstance();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    } catch (InstantiationException ie) {
      ie.printStackTrace();
    }
    return null;
  }
}

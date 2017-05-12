package edu.cmu.cs.stage3.alice.gallery.batch;

import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;

public class BatchSaveWithThumbnails extends Batch { public SymmetricPerspectiveCamera m_camera;
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget m_rt;
  
  public BatchSaveWithThumbnails() {}
  
  protected void initialize(edu.cmu.cs.stage3.alice.core.World world) { edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
    m_camera = new SymmetricPerspectiveCamera();
    m_camera.vehicle.set(world);
    m_camera.nearClippingPlaneDistance.set(new Double(0.1D));
    m_camera.verticalViewingAngle.set(new Double(0.39269908169872414D));
    m_camera.horizontalViewingAngle.set(new Double(0.39269908169872414D));
    world.addChild(m_camera);
    
    double brightness = 0.5019607843137255D;
    atmosphereColor.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(0.5019607843137255D, 0.5019607843137255D, 0.5019607843137255D));
    Class rendererClass = null;
    try {
      String[] renderers = authoringtoolConfig.getValueList("rendering.orderedRendererList");
      rendererClass = Class.forName(renderers[0]);
    }
    catch (Throwable localThrowable) {}
    edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory rtf = new edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory(rendererClass);
    
    m_rt = rtf.createOffscreenRenderTarget();
    m_rt.setSize(128, 128);
    m_rt.addCamera(m_camera.getSceneGraphCamera());
  }
}

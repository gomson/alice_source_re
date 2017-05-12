package edu.cmu.cs.stage3.alice.gallery.modeleditor;
import javax.swing.ImageIcon;

class IconManager { private static java.util.Dictionary s_classToImageMap = new java.util.Hashtable();
  






  private static ImageIcon m_defaultIcon = loadImageFromResource("images/default.gif");
  private static ImageIcon m_modelIcon = loadImageFromResource("images/model.png");
  private static ImageIcon m_partIcon = loadImageFromResource("images/subpart.gif");
  private static ImageIcon m_methodIcon = loadImageFromResource("images/method.png");
  private static ImageIcon m_questionIcon = loadImageFromResource("images/question.png");
  private static ImageIcon m_propertyIcon = loadImageFromResource("images/property.png");
  static { s_classToImageMap.put(edu.cmu.cs.stage3.alice.core.light.AmbientLight.class, loadImageFromResource("images/ambientLight.gif"));
    s_classToImageMap.put(edu.cmu.cs.stage3.alice.core.light.DirectionalLight.class, loadImageFromResource("images/directionalLight.gif"));
    s_classToImageMap.put(edu.cmu.cs.stage3.alice.core.light.PointLight.class, loadImageFromResource("images/pointLight.png"));
    
    s_classToImageMap.put(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera.class, loadImageFromResource("images/camera.png"));
    s_classToImageMap.put(edu.cmu.cs.stage3.alice.core.TextureMap.class, loadImageFromResource("images/types/edu.cmu.cs.stage3.alice.core.TextureMap.gif"));
    s_classToImageMap.put(edu.cmu.cs.stage3.alice.core.Sound.class, loadImageFromResource("images/types/edu.cmu.cs.stage3.alice.core.Sound.gif"));
    s_classToImageMap.put(edu.cmu.cs.stage3.alice.core.Pose.class, loadImageFromResource("images/types/edu.cmu.cs.stage3.alice.core.Pose.gif")); }
  
  IconManager() {}
  private static ImageIcon loadImageFromResource(String name) { java.net.URL resource = edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource(name);
    return new ImageIcon(resource);
  }
  
  public static ImageIcon lookupIcon(Object o) { ImageIcon icon = (ImageIcon)s_classToImageMap.get(o.getClass());
    if (icon == null) {
      if ((o instanceof edu.cmu.cs.stage3.alice.core.Model)) {
        edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)o;
        if (isFirstClass.booleanValue()) {
          return m_modelIcon;
        }
        return m_partIcon;
      }
      if ((o instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse))
        return m_methodIcon;
      if ((o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion))
        return m_methodIcon;
      if ((o instanceof edu.cmu.cs.stage3.alice.core.Variable)) {
        return m_propertyIcon;
      }
      return m_defaultIcon;
    }
    
    return icon;
  }
}

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;














public class TextureMapViewController
  extends JPanel
  implements GUIElement, Releasable
{
  protected TextureMap textureMap;
  protected ElementDnDPanel textureMapDnDPanel;
  
  public TextureMapViewController()
  {
    setLayout(new GridBagLayout());
    setOpaque(false);
  }
  
  public void setTextureMap(TextureMap textureMap) {
    this.textureMap = textureMap;
    textureMapDnDPanel = GUIFactory.getElementDnDPanel(textureMap);
    add(textureMapDnDPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
  }
  
  public void goToSleep()
  {
    if (textureMapDnDPanel != null) {
      textureMapDnDPanel.goToSleep();
    }
  }
  
  public void wakeUp() {
    if (textureMapDnDPanel != null) {
      textureMapDnDPanel.wakeUp();
    }
  }
  
  public void clean() {
    goToSleep();
    if (textureMapDnDPanel != null) {
      remove(textureMapDnDPanel);
      textureMapDnDPanel = null;
    }
  }
  
  public void die() {
    clean();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
}

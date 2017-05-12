package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;






public class TextureMapsPanel
  extends ExpandablePanel
{
  protected ObjectArrayProperty textureMaps;
  protected JPanel contentPanel = new JPanel();
  protected HashMap textureMapGuiCache = new HashMap();
  protected JButton importTextureMapButton = new JButton(Messages.getString("import_texture_map"));
  protected AuthoringTool authoringTool;
  protected RefreshListener refreshListener = new RefreshListener();
  
  public TextureMapsPanel(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    guiInit();
  }
  
  private void guiInit() {
    setTitle(Messages.getString("Texture_Maps"));
    contentPanel.setLayout(new GridBagLayout());
    setContent(contentPanel);
    importTextureMapButton.setBackground(new Color(240, 240, 255));
    importTextureMapButton.setMargin(new Insets(2, 4, 2, 4));
    importTextureMapButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          authoringTool.setImportFileFilter("Image Files");
          authoringTool.importElement(null, textureMaps.getOwner());
        }
        
      });
    setOpaque(false);
    contentPanel.setOpaque(false);
    
    importTextureMapButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Load_an_Image_File_into_this_World__p__p_Objects_use_image_files_as_textures__p_You_can_change_an_object_s_texture_by_setting_its__b_skin__b__property_") + "</font></html>");
  }
  
  public void setTextureMaps(ObjectArrayProperty textureMaps) {
    if (this.textureMaps != null) {
      this.textureMaps.removeObjectArrayPropertyListener(refreshListener);
    }
    
    this.textureMaps = textureMaps;
    
    if (textureMaps != null) {
      textureMaps.addObjectArrayPropertyListener(refreshListener);
    }
    
    refreshGUI();
  }
  
  public void refreshGUI() {
    contentPanel.removeAll();
    
    if (textureMaps != null) {
      int count = 0;
      for (int i = 0; i < textureMaps.size(); i++) {
        TextureMap textureMap = (TextureMap)textureMaps.get(i);
        JComponent gui = (JComponent)textureMapGuiCache.get(textureMap);
        if (gui == null) {
          gui = GUIFactory.getGUI(textureMap);
          textureMapGuiCache.put(textureMap, gui);
        }
        if (gui != null) {
          contentPanel.add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_textureMap__") + textureMap, null);
        }
      }
      
      contentPanel.add(importTextureMapButton, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(4, 2, 4, 2), 0, 0));
      Component glue = Box.createGlue();
      contentPanel.add(glue, new GridBagConstraints(0, count++, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(2, 2, 2, 2), 0, 0));
    }
    revalidate();
    repaint(); }
  
  protected class RefreshListener implements ObjectArrayPropertyListener { protected RefreshListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { refreshGUI(); }
  }
}

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Sound;
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





public class SoundsPanel
  extends ExpandablePanel
{
  protected ObjectArrayProperty sounds;
  protected JPanel contentPanel = new JPanel();
  protected HashMap soundGuiCache = new HashMap();
  protected JButton importSoundButton = new JButton(Messages.getString("import_sound"));
  protected JButton recordSoundButton = new JButton(Messages.getString("record_sound"));
  protected AuthoringTool authoringTool;
  protected RefreshListener refreshListener = new RefreshListener();
  
  public SoundsPanel(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    guiInit();
  }
  
  private void guiInit() {
    setTitle(Messages.getString("Sounds"));
    contentPanel.setLayout(new GridBagLayout());
    setContent(contentPanel);
    importSoundButton.setBackground(new Color(240, 240, 255));
    importSoundButton.setMargin(new Insets(2, 4, 2, 4));
    importSoundButton.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent ev) {
          authoringTool.setImportFileFilter("Sound Files");
          authoringTool.importElement(null, sounds.getOwner());
        }
        
      });
    recordSoundButton.setBackground(new Color(240, 240, 255));
    recordSoundButton.setMargin(new Insets(2, 4, 2, 4));
    recordSoundButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          AuthoringTool.getHack().promptUserForRecordedSound(sounds.getOwner().getSandbox());




        }
        




      });
    setOpaque(false);
    contentPanel.setOpaque(false);
    
    importSoundButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Load_a_Sound_File_into_this_World__p__p_You_can_play_a_sound_when_the_world_runs_by_using_an_Object_s__b_PlaySound__b__method_") + "</font></html>");
    recordSoundButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Record_a_Sound__p__p_Use_a_microphone_or_play_a_sound_file_while_recording_to_capture_a_sound__p_You_can_play_a_sound_when_the_world_runs_by_using_an_Object_s__b_PlaySound__b__method_") + "</font></html>");
  }
  
  public void setSounds(ObjectArrayProperty sounds) {
    if (this.sounds != null) {
      this.sounds.removeObjectArrayPropertyListener(refreshListener);
    }
    
    this.sounds = sounds;
    
    if (sounds != null) {
      sounds.addObjectArrayPropertyListener(refreshListener);
    }
    
    refreshGUI();
  }
  
  public void refreshGUI() {
    contentPanel.removeAll();
    if (sounds != null) {
      int count = 0;
      for (int i = 0; i < sounds.size(); i++) {
        Sound sound = (Sound)sounds.get(i);
        if (sound != null) {
          JComponent gui = (JComponent)soundGuiCache.get(sound);
          if (gui == null) {
            gui = GUIFactory.getGUI(sound);
            soundGuiCache.put(sound, gui);
          }
          if (gui != null) {
            contentPanel.add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 2, 0, 2), 0, 0));
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_sound__") + sound, null);
          }
        }
      }
      
      contentPanel.add(importSoundButton, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(4, 2, 4, 2), 0, 0));
      contentPanel.add(recordSoundButton, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(4, 2, 4, 2), 0, 0));
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

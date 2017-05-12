package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Media;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.media.SoundMarker;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @deprecated
 */
public class SoundMarkersPanel
  extends JPanel
{
  protected Media sound = null;
  protected HashMap soundMarkersGuiCache = new HashMap();
  protected JButton dropMarkerButton = new JButton(Messages.getString("Drop_Marker"));
  protected RefreshListener refreshListener = new RefreshListener();
  
  public SoundMarkersPanel() {
    guiInit();
  }
  
  private void guiInit()
  {
    setLayout(new GridBagLayout());
    dropMarkerButton.setBackground(new Color(240, 240, 255));
    dropMarkerButton.setMargin(new Insets(2, 4, 2, 4));
    dropMarkerButton.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent ev) {}
      });
  }
  



















  public void refreshGUI()
  {
    removeAll();
    
    if ((sound != null) && (sound.markers != null)) {
      int count = 0;
      for (int i = 0; i < sound.markers.size(); i++) {
        SoundMarker marker = (SoundMarker)sound.markers.get(i);
        JComponent gui = (JComponent)soundMarkersGuiCache.get(marker);
        if (gui == null) {
          gui = GUIFactory.getGUI(marker);
          soundMarkersGuiCache.put(marker, gui);
        }
        if (gui != null) {
          add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
        } else {
          System.err.println(Messages.getString("Unable_to_create_gui_for_marker__") + marker);
        }
      }
      
      add(dropMarkerButton, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(4, 2, 4, 2), 0, 0));
      Component glue = Box.createGlue();
      add(glue, new GridBagConstraints(0, count++, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(2, 2, 2, 2), 0, 0));
    }
    revalidate();
    repaint(); }
  
  protected class RefreshListener implements ObjectArrayPropertyListener { protected RefreshListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { refreshGUI(); }
  }
}

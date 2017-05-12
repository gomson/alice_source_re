package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.property.DataSourceProperty;
import edu.cmu.cs.stage3.media.DataSource;
import edu.cmu.cs.stage3.media.Player;
import edu.cmu.cs.stage3.media.event.DataSourceEvent;
import edu.cmu.cs.stage3.media.event.DataSourceListener;
import edu.cmu.cs.stage3.media.event.PlayerEvent;
import edu.cmu.cs.stage3.media.event.PlayerListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class SoundViewController
  extends JPanel
  implements GUIElement, Releasable
{
  private Sound m_sound;
  private Player m_player;
  private SoundPlayStopToggleButton m_soundPlayStopToggleButton = new SoundPlayStopToggleButton();
  private SoundDurationLabel m_soundDurationLabel = new SoundDurationLabel();
  
  private ElementDnDPanel m_soundDnDPanel;
  private DataSourceListener m_dataSourceListener = new DataSourceListener() {
    public void durationUpdated(DataSourceEvent e) {
      m_soundDurationLabel.updateComponent();
    }
  };
  private PlayerListener m_playerListener = new PlayerListener() {
    public void stateChanged(PlayerEvent e) {
      m_soundPlayStopToggleButton.updateComponent();
    }
    
    public void endReached(PlayerEvent e) {}
  };
  
  public SoundViewController() {
    setLayout(new GridBagLayout());
    add(m_soundPlayStopToggleButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(m_soundDurationLabel, new GridBagConstraints(2, 0, 1, 1, 1.0D, 0.0D, 13, 0, new Insets(0, 4, 0, 4), 0, 0));
    setOpaque(false);
  }
  
  public void setSound(Sound sound) {
    if (m_sound != null) {
      DataSource dataSourceValue = dataSource.getDataSourceValue();
      if (dataSourceValue != null) {
        dataSourceValue.removeDataSourceListener(m_dataSourceListener);
      }
      if (m_player != null) {
        m_player.stop();
        m_player.removePlayerListener(m_playerListener);
        m_player = null;
      }
    }
    
    m_sound = sound;
    if (sound != null) {
      m_soundDnDPanel = GUIFactory.getElementDnDPanel(sound);
      add(m_soundDnDPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
      

      DataSource dataSourceValue = dataSource.getDataSourceValue();
      if (dataSourceValue != null) {
        dataSourceValue.addDataSourceListener(m_dataSourceListener);
        dataSourceValue.waitForRealizedPlayerCount(1, 0L);
      }
      
      m_soundDurationLabel.updateComponent();
    }
  }
  


  protected void startListening() {}
  

  protected void stopListening() {}
  

  public void goToSleep()
  {
    stopListening();
    if (m_soundDnDPanel != null) {
      m_soundDnDPanel.goToSleep();
    }
  }
  
  public void wakeUp() {
    startListening();
    if (m_soundDnDPanel != null) {
      m_soundDnDPanel.wakeUp();
    }
  }
  
  public void clean() {
    stopSound();
    stopListening();
    if (m_soundDnDPanel != null) {
      remove(m_soundDnDPanel);
      m_soundDnDPanel = null;
    }
  }
  
  public void die() {
    clean();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
    if (m_player != null) {
      m_player.setIsAvailable(true);
      m_player = null;
    }
  }
  
  public double getSoundDuration() {
    double t = NaN.0D;
    DataSource dataSourceValue = m_sound.dataSource.getDataSourceValue();
    if (dataSourceValue != null) {
      t = dataSourceValue.getDuration(true);
    }
    return t;
  }
  
  public void playSound() { if ((m_player == null) && 
      (m_sound != null)) {
      DataSource dataSourceValue = m_sound.dataSource.getDataSourceValue();
      if (dataSourceValue != null) {
        m_player = dataSourceValue.acquirePlayer();
        m_player.addPlayerListener(m_playerListener);
      }
    }
    
    if (m_player != null) {
      m_player.startFromBeginning();
    }
  }
  
  public void stopSound() {
    if (m_player != null) {
      m_player.stop();
    }
  }
  
  public int getSoundState() {
    if (m_player != null) {
      return m_player.getState();
    }
    return 0;
  }
  
  protected class SoundPlayStopToggleButton extends JButton
  {
    protected ImageIcon playIcon;
    protected ImageIcon stopIcon;
    protected ActionListener buttonListener = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        if (m_sound != null) {
          if (getSoundState() == 600) {
            stopSound();
          } else {
            playSound();
          }
        }
      }
    };
    
    public SoundPlayStopToggleButton() {
      playIcon = AuthoringToolResources.getIconForValue("playSound");
      stopIcon = AuthoringToolResources.getIconForValue("stopSound");
      addActionListener(buttonListener);
      setOpaque(false);
      setBorder(null);
      setContentAreaFilled(false);
      setFocusPainted(false);
      setIcon(playIcon);
    }
    
    public void updateComponent() {
      if (getSoundState() == 600) {
        setIcon(stopIcon);
      } else
        setIcon(playIcon);
    }
  }
  
  protected class SoundDurationLabel extends JLabel {
    protected SoundDurationLabel() {}
    
    public void updateComponent() { setText(AuthoringToolResources.formatTime(getSoundDuration())); }
  }
}

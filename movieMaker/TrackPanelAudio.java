package movieMaker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.media.Format;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import jmapps.ui.TrackPanel;





























public class TrackPanelAudio
  extends TrackPanel
  implements ActionListener
{
  private AudioFormat formatOld;
  private String strContentType = null;
  private AudioFormatChooser chooserAudioFormat;
  
  public TrackPanelAudio(TrackControl trackControl, ActionListener listenerEnableTrack) {
    super(trackControl, listenerEnableTrack);
    try
    {
      init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  



  public void setContentType(String strContentType)
  {
    arrSupportedFormats = trackControl.getSupportedFormats();
    this.strContentType = strContentType;
    int nSize = arrSupportedFormats.length;
    vectorContSuppFormats = new Vector();
    
    for (int i = 0; i < nSize; i++) {
      if ((arrSupportedFormats[i] instanceof AudioFormat))
      {
        AudioFormat formatAudio = (AudioFormat)arrSupportedFormats[i];
        vectorContSuppFormats.addElement(formatAudio);
      }
    }
    chooserAudioFormat.setSupportedFormats(vectorContSuppFormats);
    chooserAudioFormat.setCurrentFormat(formatOld);
  }
  

  public boolean isTrackEnabled()
  {
    boolean boolEnabled = chooserAudioFormat.isTrackEnabled();
    return boolEnabled;
  }
  

  public Format getFormat()
  {
    Format format = chooserAudioFormat.getFormat();
    return format;
  }
  
  public void setDefaults(boolean boolTrackEnable, Format format) {
    chooserAudioFormat.setTrackEnabled(boolTrackEnable);
    if ((format instanceof AudioFormat)) {
      formatOld = ((AudioFormat)format);
      chooserAudioFormat.setCurrentFormat(formatOld);
    }
  }
  
  private void init() throws Exception {
    setLayout(new BorderLayout());
    formatOld = ((AudioFormat)trackControl.getFormat());
    chooserAudioFormat = new AudioFormatChooser(arrSupportedFormats, formatOld, true, this);
    add(chooserAudioFormat, "North");
  }
  







  public void actionPerformed(ActionEvent event)
  {
    String strCmd = event.getActionCommand();
    if (strCmd.equals("ACTION_AUDIO_TRACK_ENABLED")) {
      ActionEvent eventNew = new ActionEvent(this, 1001, event.getActionCommand());
      listenerEnableTrack.actionPerformed(eventNew);
    }
    else if (strCmd.equals("ACTION_AUDIO_TRACK_DISABLED")) {
      ActionEvent eventNew = new ActionEvent(this, 1001, event.getActionCommand());
      listenerEnableTrack.actionPerformed(eventNew);
    }
  }
}

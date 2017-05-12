package movieMaker;

import com.sun.media.ui.TabControl;
import com.sun.media.util.JMFI18N;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Duration;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Processor;
import javax.media.Time;
import javax.media.control.MonitorControl;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import jmapps.ui.ImageArea;
import jmapps.ui.JMDialog;
import jmapps.ui.JMPanel;
import jmapps.ui.MessageDialog;
import jmapps.ui.ProgressDialog;
import jmapps.ui.ProgressThread;
import jmapps.util.JMAppsCfg;
import jmapps.util.JMAppsCfg.TrackData;








































public class SaveAsDialog
  extends JMDialog
  implements ControllerListener, DataSinkListener, ItemListener
{
  private JMAppsCfg cfgJMApps;
  private String inputURL;
  private DataSource dataSource = null;
  private Processor processor = null;
  private DataSink dataSink = null;
  private TrackControl[] arrTrackControls;
  private int nAudioTrackCount = 0;
  private String strContentType = null;
  
  private boolean boolSaving = false;
  private ProgressDialog dlgProgress = null;
  private ProgressThread threadProgress = null;
  
  private TabControl tabControl;
  
  private Hashtable hashtablePanelsAudio = new Hashtable();
  
  private Choice comboContentType;
  private Image imageAudioEn = null;
  private Image imageAudioDis = null;
  private String strFailMessage = null;
  










  public SaveAsDialog(Frame frame, String inputURL, Format format, JMAppsCfg cfgJMApps)
  {
    super(frame, JMFI18N.getResource("jmstudio.saveas.title"), false);
    
    this.cfgJMApps = cfgJMApps;
    this.inputURL = inputURL;
    
    try
    {
      init();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  








  public SaveAsDialog(Frame frame, DataSource dataSource, JMAppsCfg cfgJMApps)
  {
    super(frame, JMFI18N.getResource("jmstudio.saveas.title"), false);
    
    this.cfgJMApps = cfgJMApps;
    this.dataSource = dataSource;
    inputURL = "Capture";
    try
    {
      init();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  














  private void init()
    throws Exception
  {
    imageAudioEn = ImageArea.loadImage("audio.gif", this, true);
    imageAudioDis = ImageArea.loadImage("audio-disabled.gif", this, true);
    
    frameOwner.setCursor(new Cursor(3));
    
    if (dataSource == null) {
      try {
        MediaLocator mediaSource = new MediaLocator(inputURL);
        dataSource = Manager.createDataSource(mediaSource);
      }
      catch (Exception exception) {
        AuthoringTool.showErrorDialog(Messages.getString("Alice_has_encountered_an_error"), exception);
        frameOwner.setCursor(Cursor.getDefaultCursor());
        throw exception;
      }
    }
    
    strContentType = dataSource.getContentType();
    try {
      processor = Manager.createProcessor(dataSource);
    }
    catch (NoPlayerException exception) {
      AuthoringTool.showErrorDialog(Messages.getString("Alice_has_encountered_an_error"), exception);
      frameOwner.setCursor(Cursor.getDefaultCursor());
      throw exception;
    }
    processor.addControllerListener(this);
    

    boolean boolResult = waitForState(processor, 180);
    if (!boolResult) {
      frameOwner.setCursor(Cursor.getDefaultCursor());
      return;
    }
    
    arrTrackControls = processor.getTrackControls();
    for (int i = 0; i < arrTrackControls.length; i++) {
      Format format = arrTrackControls[i].getFormat();
      if ((format instanceof AudioFormat)) {
        nAudioTrackCount += 1;
      }
    }
    setLayout(new BorderLayout());
    
    JMPanel panelBorder = new JMPanel(new BorderLayout(6, 6));
    panelBorder.setEmptyBorder(6, 6, 6, 6);
    panelBorder.setBackground(Color.lightGray);
    add(panelBorder, "Center");
    
    Panel panel = createPanelGeneral();
    panelBorder.add(panel, "North");
    
    panel = createPanelProperties();
    panelBorder.add(panel, "Center");
    
    changeContentType();
    
    pack();
    Dimension dim = getSize();
    width += 64;
    setSize(dim);
    
    addWindowListener(this);
    setResizable(false);
    

    doSave();
    frameOwner.setCursor(Cursor.getDefaultCursor());
  }
  









  private Panel createPanelProperties()
    throws Exception
  {
    String strAudio = "Audio";
    


    tabControl = new TabControl(0);
    
    int nCount = arrTrackControls.length;
    for (int i = 0; i < nCount; i++) {
      Format format = arrTrackControls[i].getFormat();
      if ((format instanceof AudioFormat)) {
        TrackPanelAudio panelAudio = new TrackPanelAudio(arrTrackControls[i], this);
        tabControl.addPage(panelAudio, strAudio, imageAudioEn);
        hashtablePanelsAudio.put(strAudio, panelAudio);
        
        if (cfgJMApps != null) {
          JMAppsCfg.TrackData dataTrack = cfgJMApps.getLastSaveFileTrackData(strAudio);
          if (dataTrack != null) {
            panelAudio.setDefaults(boolEnable, format);
          }
        }
      }
    }
    
    return tabControl;
  }
  








  private Panel createPanelGeneral()
    throws Exception
  {
    Panel panelGeneral = new Panel(new GridLayout(0, 1, 4, 4));
    
    Panel panelFormat = new Panel(new BorderLayout());
    panelGeneral.add(panelFormat);
    
    Label label = new Label(Messages.getString("Format_"));
    panelFormat.add(label, "West");
    
    label = new Label("Wave(wav)");
    panelFormat.add(label, "Center");
    
    return panelGeneral;
  }
  











  private void doSave()
  {
    String strFileContentType = null;
    

    String strDirName = null;
    String strFileName = null;
    



    Component monitor = null;
    




    setCursor(new Cursor(3));
    try
    {
      processor.setContentDescriptor(new FileTypeDescriptor(strContentType));
      

      Enumeration enumKeys = hashtablePanelsAudio.keys();
      while (enumKeys.hasMoreElements()) {
        String strPanel = (String)enumKeys.nextElement();
        TrackPanelAudio panelAudio = (TrackPanelAudio)hashtablePanelsAudio.get(strPanel);
        panelAudio.updateTrack();
        
        if (cfgJMApps != null) {
          JMAppsCfg.TrackData dataTrack = cfgJMApps.createTrackDataObject();
          TrackControl trackControl = panelAudio.getTrackControl();
          boolEnable = trackControl.isEnabled();
          format = trackControl.getFormat();
          cfgJMApps.setLastSaveFileTrackData(dataTrack, strPanel);
        }
      }
      
      boolean boolResult = waitForState(processor, 300);
      if (!boolResult) {
        setCursor(Cursor.getDefaultCursor());
        processor.close();
        dispose();
        return;
      }
      

      DataSource dataSource = processor.getDataOutput();
      
      inputURL = (inputURL.substring(0, inputURL.lastIndexOf(".")) + "a.wav");
      MediaLocator mediaDest = new MediaLocator(inputURL);
      dataSink = Manager.createDataSink(dataSource, mediaDest);
      boolSaving = true;
      MonitorControl monitorControl = (MonitorControl)
        processor.getControl("javax.media.control.MonitorControl");
      if (monitorControl != null) {
        monitor = monitorControl.getControlComponent();
      }
      Time duration = processor.getDuration();
      int nMediaDuration = (int)duration.getSeconds();
      
      dataSink.addDataSinkListener(this);
      try {
        dataSink.open();
      } catch (Exception e) {
        processor.close();
        throw e;
      }
      dataSink.start();
      processor.start();
      
      if ((nMediaDuration > 0) && (duration != Duration.DURATION_UNBOUNDED) && 
        (duration != Duration.DURATION_UNKNOWN)) {
        dlgProgress = new ProgressDialog(frameOwner, 
          JMFI18N.getResource("jmstudio.saveprogress.title"), 
          0, nMediaDuration, this);
      }
      else {
        dlgProgress = new ProgressDialog(frameOwner, 
          JMFI18N.getResource("jmstudio.saveprogress.title"), 
          JMFI18N.getResource("jmstudio.saveprogress.label"), 
          monitor, this);
      }
      

      threadProgress = new ProgressThread(processor, dlgProgress);
      threadProgress.start();
    }
    catch (Exception exception)
    {
      boolSaving = false;
      AuthoringTool.showErrorDialog(Messages.getString("Alice_has_encountered_an_error__"), exception);
    }
    
    setCursor(Cursor.getDefaultCursor());
    dispose();
  }
  








  public void actionPerformed(ActionEvent event)
  {
    String strCmd = event.getActionCommand();
    if (strCmd.equals(ACTION_CANCEL)) {
      stopSaving();
      dispose();
    }
    else if (strCmd.equals(ACTION_SAVE)) {
      doSave();
    }
    else if (((strCmd.equals(ProgressDialog.ACTION_ABORT)) || 
      (strCmd.equals(ProgressDialog.ACTION_STOP))) && 
      (boolSaving)) {
      stopSaving();
    }
    else if ((strCmd.equals(ProgressDialog.ACTION_PAUSE)) && (boolSaving)) {
      processor.stop();
      dlgProgress.setPauseButtonText(ProgressDialog.ACTION_RESUME);
      threadProgress.pauseThread();
    }
    else if ((strCmd.equals(ProgressDialog.ACTION_RESUME)) && (boolSaving)) {
      processor.start();
      dlgProgress.setPauseButtonText(ProgressDialog.ACTION_PAUSE);
      threadProgress.resumeThread();
    }
    else if (strCmd.equals("ACTION_AUDIO_TRACK_ENABLED")) {
      Object objectSource = event.getSource();
      if ((objectSource instanceof TrackPanelAudio)) {
        tabControl.setPageImage((Panel)objectSource, imageAudioEn);
      }
    } else if (strCmd.equals("ACTION_AUDIO_TRACK_DISABLED")) {
      Object objectSource = event.getSource();
      if ((objectSource instanceof TrackPanelAudio)) {
        tabControl.setPageImage((Panel)objectSource, imageAudioDis);
      }
    }
  }
  





  public void itemStateChanged(ItemEvent event)
  {
    Object objectSource = event.getSource();
    if (objectSource == comboContentType) {
      changeContentType();
    }
  }
  




  public void windowClosing(WindowEvent event)
  {
    stopSaving();
    dispose();
  }
  



  public void controllerUpdate(ControllerEvent event)
  {
    if ((event instanceof ControllerErrorEvent)) {
      strFailMessage = ((ControllerErrorEvent)event).getMessage();
      
      if (boolSaving) {
        stopSaving();
        MessageDialog.createErrorDialogModeless(frameOwner, 
          JMFI18N.getResource("jmstudio.error.processor.savefile") + 
          "\n" + JMFI18N.getResource("jmstudio.error.controller") + 
          "\n" + strFailMessage);
      }
      else {
        MessageDialog.createErrorDialogModeless(frameOwner, 
          JMFI18N.getResource("jmstudio.error.controller") + 
          "\n" + strFailMessage);
      }
    }
    else if (((event instanceof EndOfMediaEvent)) && 
      (boolSaving)) {
      stopSaving();
    }
  }
  




  public void dataSinkUpdate(DataSinkEvent event)
  {
    if ((event instanceof EndOfStreamEvent)) {
      closeDataSink();
    }
    else if ((event instanceof DataSinkErrorEvent)) {
      stopSaving();
      MessageDialog.createErrorDialogModeless(frameOwner, 
        JMFI18N.getResource("jmstudio.error.processor.writefile"));
    }
  }
  
  private void closeDataSink() {
    synchronized (this) {
      if (dataSink != null)
        dataSink.close();
      dataSink = null;
    }
  }
  


  private void stopSaving()
  {
    boolSaving = false;
    
    if (threadProgress != null) {
      threadProgress.terminateNormaly();
      threadProgress = null;
    }
    if (processor != null) {
      processor.stop();
      processor.close();
    }
    if (dlgProgress != null) {
      dlgProgress.dispose();
      dlgProgress = null;
    }
  }
  







  Object stateLock = new Object();
  boolean stateFailed = false;
  
  private synchronized boolean waitForState(Processor p, int state) {
    p.addControllerListener(new StateListener());
    stateFailed = false;
    
    if (state == 180) {
      p.configure();
    }
    else if (state == 300) {
      p.realize();
    }
    
    while ((p.getState() < state) && (!stateFailed)) {
      synchronized (stateLock) {
        try {
          stateLock.wait();
        }
        catch (InterruptedException ie) {
          return false;
        }
      }
    }
    
    return !stateFailed;
  }
  






  private void changeContentType()
  {
    strContentType = "audio.x_wav";
    

    if (processor.setContentDescriptor(new FileTypeDescriptor(strContentType)) == null) {
      System.err.println(Messages.getString("Error_setting_content_descriptor_on_processor"));
    }
    
    Enumeration enumPanels = hashtablePanelsAudio.elements();
    while (enumPanels.hasMoreElements()) {
      TrackPanelAudio panelAudio = (TrackPanelAudio)enumPanels.nextElement();
      panelAudio.setContentType(strContentType);
    }
  }
  
  class StateListener implements ControllerListener
  {
    StateListener() {}
    
    public void controllerUpdate(ControllerEvent ce) {
      if ((ce instanceof ControllerClosedEvent))
        stateFailed = true;
      if ((ce instanceof ControllerEvent)) {
        synchronized (stateLock) {
          stateLock.notifyAll();
        }
      }
    }
  }
}

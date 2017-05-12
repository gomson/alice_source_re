package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.property.DataSourceProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.media.DataSource;
import edu.cmu.cs.stage3.media.Manager;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;





























public class SoundRecorder
  extends ContentPane
{
  private static byte[] s_wavHeader = new byte[44];
  
  static { System.arraycopy("RIFF????WAVEfmt ".getBytes(), 0, s_wavHeader, 0, 16);
    s_wavHeader[16] = 16;
    s_wavHeader[17] = 0;
    s_wavHeader[18] = 0;
    s_wavHeader[19] = 0;
    s_wavHeader[20] = 1;
    s_wavHeader[21] = 0;
    s_wavHeader[22] = 2;
    s_wavHeader[23] = 0;
    s_wavHeader[24] = 68;
    s_wavHeader[25] = -84;
    s_wavHeader[26] = 0;
    s_wavHeader[27] = 0;
    s_wavHeader[28] = 16;
    s_wavHeader[29] = -79;
    s_wavHeader[30] = 2;
    s_wavHeader[31] = 0;
    s_wavHeader[32] = 4;
    s_wavHeader[33] = 0;
    s_wavHeader[34] = 16;
    s_wavHeader[35] = 0;
    System.arraycopy("data????".getBytes(), 0, s_wavHeader, 36, 8);
  }
  
  Capture capture = new Capture();
  Playback playback = new Playback();
  
  AudioInputStream audioInputStream;
  final int bufSize = 16384;
  
  private static final int IDLE = 0;
  
  private static final int RECORDING = 1;
  private static final int PAUSE = 2;
  private static final int RESUME = 3;
  private static final int PLAYING = 4;
  private int m_state = 0;
  
  private Element m_parentToCheckForNameValidity;
  
  private Sound m_sound;
  
  private DataSource m_dataSource;
  
  private JTextField m_nameTextField;
  private JLabel m_durationLabel;
  private JButton m_recordButton;
  private JButton m_pauseButton;
  private JButton m_playButton;
  private JButton m_okButton;
  private JButton m_cancelButton;
  private Timer m_durationUpdateTimer = new Timer(
    100, new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      SoundRecorder.this.onDurationUpdate();
    }
  }
    );
  

  private long m_durationT0;
  
  private File soundDirectory;
  

  public SoundRecorder(File currentWorldLocation)
  {
    soundDirectory = currentWorldLocation;
    m_durationLabel = new JLabel();
    onDurationUpdate();
    
    m_recordButton = new JButton(Messages.getString("Record"));
    m_recordButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SoundRecorder.this.onRecord();
      }
      
    });
    m_pauseButton = new JButton(Messages.getString("Pause"));
    m_pauseButton.setEnabled(false);
    m_pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SoundRecorder.this.onPause();
      }
      
    });
    m_playButton = new JButton(Messages.getString("Play"));
    m_playButton.setEnabled(false);
    m_playButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SoundRecorder.this.onPlay();
      }
      
    });
    m_nameTextField = new JTextField();
    m_nameTextField.setText(Messages.getString("unnamedSound"));
    m_nameTextField.getDocument().addDocumentListener(
      new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
          SoundRecorder.this.checkNameForValidity();
        }
        
        public void insertUpdate(DocumentEvent e) {
          SoundRecorder.this.checkNameForValidity();
        }
        
        public void removeUpdate(DocumentEvent e) {
          SoundRecorder.this.checkNameForValidity();
        }
        
      });
    m_okButton = new JButton(Messages.getString("OK"));
    m_okButton.setPreferredSize(new Dimension(80, 26));
    m_okButton.setEnabled(false);
    m_okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SoundRecorder.this.onOK();
      }
      
    });
    m_cancelButton = new JButton(Messages.getString("Cancel"));
    m_cancelButton.setPreferredSize(new Dimension(80, 26));
    m_cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SoundRecorder.this.onCancel();
      }
      
    });
    checkNameForValidity();
    


    JPanel namePanel = new JPanel();
    namePanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    fill = 2;
    gridwidth = -1;
    namePanel
      .add(new JLabel(Messages.getString("Name__")), gbc);
    weightx = 1.0D;
    gridwidth = 0;
    namePanel.add(m_nameTextField, gbc);
    
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    fill = 2;
    gridwidth = 1;
    weightx = 1.0D;
    controlPanel.add(m_recordButton, gbc);
    controlPanel.add(m_pauseButton, gbc);
    controlPanel.add(m_playButton, gbc);
    
    JPanel okCancelPanel = new JPanel();
    okCancelPanel.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    weightx = 1.0D;
    okCancelPanel.add(new JLabel(), gbc);
    weightx = 0.0D;
    okCancelPanel.add(m_okButton, gbc);
    okCancelPanel.add(m_cancelButton, gbc);
    
    int opad = 16;
    setLayout(new GridBagLayout());
    
    gbc = new GridBagConstraints();
    anchor = 18;
    fill = 1;
    insets.top = 16;
    insets.left = 16;
    insets.right = 16;
    gridwidth = 0;
    weightx = 1.0D;
    weighty = 0.0D;
    
    add(namePanel, gbc);
    
    insets.top = 0;
    
    JLabel spacer1 = new JLabel();
    spacer1.setPreferredSize(new Dimension(480, 16));
    weighty = 1.0D;
    add(spacer1, gbc);
    weighty = 0.0D;
    
    anchor = 17;
    add(m_durationLabel, gbc);
    add(controlPanel, gbc);
    
    JLabel spacer2 = new JLabel();
    spacer2.setPreferredSize(new Dimension(480, 16));
    weighty = 1.0D;
    add(spacer2, gbc);
    weighty = 0.0D;
    
    insets.bottom = 16;
    
    anchor = 14;
    add(okCancelPanel, gbc);
  }
  
  public void handleDispose() {
    onCancel();
    super.handleDispose();
  }
  
  public void addOKActionListener(ActionListener l) {
    m_okButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okButton.removeActionListener(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    m_cancelButton.addActionListener(l);
  }
  
  public void removeCancelActionListener(ActionListener l) {
    m_cancelButton.removeActionListener(l);
  }
  
  public Sound getSound() {
    return m_sound;
  }
  
  public void setSound(Sound sound) {
    m_sound = sound;
  }
  
  public Element getParentToCheckForNameValidity() {
    return m_parentToCheckForNameValidity;
  }
  
  public void setParentToCheckForNameValidity(Element parentToCheckForNameValidity)
  {
    m_parentToCheckForNameValidity = parentToCheckForNameValidity;
    m_nameTextField
      .setText(
      AuthoringToolResources.getNameForNewChild(Messages.getString("unnamedSound"), 
      m_parentToCheckForNameValidity));
  }
  
  private void checkNameForValidity() {
    Color color = Color.black;
    
    if (Element.isPotentialNameValid(m_nameTextField.getText())) {
      color = Color.black;
    } else {
      color = Color.red;
    }
    
    if ((m_parentToCheckForNameValidity != null) && 
      (m_parentToCheckForNameValidity
      .getChildNamedIgnoreCase(m_nameTextField.getText()) != null)) {
      color = Color.red;
    }
    

    m_nameTextField.setForeground(color);
    updateOKButtonEnabled();
  }
  
  private void updateOKButtonEnabled()
  {
    if (audioInputStream != null) {} m_okButton.setEnabled(
      m_nameTextField.getForeground().equals(
      Color.black));
  }
  
  private String formatTime(double seconds) {
    if (Double.isNaN(seconds)) {
      return "?:??";
    }
    DecimalFormat decFormatter = new DecimalFormat(
      ".000");
    DecimalFormat secMinFormatter1 = new DecimalFormat(
      "00");
    DecimalFormat secMinFormatter2 = new DecimalFormat(
      "#0");
    
    double secondsFloored = (int)Math.floor(seconds);
    double decimal = seconds - secondsFloored;
    double secs = secondsFloored % 60.0D;
    double minutes = (secondsFloored - secs) / 60.0D % 60.0D;
    double hours = (secondsFloored - 60.0D * minutes - secs) / 
      3600.0D;
    
    String timeString = secMinFormatter1.format(secs) + 
      decFormatter.format(decimal);
    if (hours > 0.0D) {
      timeString = 
        secMinFormatter1.format(minutes) + ":" + timeString;
      timeString = secMinFormatter2.format(hours) + ":" + timeString;
    } else {
      timeString = 
        secMinFormatter2.format(minutes) + ":" + timeString;
    }
    
    return timeString;
  }
  

  double pauseTime = 0.0D;
  long dt = 0L;
  double t = 0.0D; double totalPauseTime = 0.0D;
  
  private void onDurationUpdate() {
    switch (m_state) {
    case 1: 
    case 4: 
      dt = (System.currentTimeMillis() - m_durationT0);
      t = (dt * 0.001D - totalPauseTime);
      break;
    case 2: 
      pauseTime = (System.currentTimeMillis() - m_durationT0 - dt);
      break;
    case 3: 
      totalPauseTime += pauseTime * 0.001D;
      if (capture.thread != null) {
        m_state = 1;
      }
      else if (playback.thread != null) {
        m_state = 4;
      }
      break;
    }
    
    m_durationLabel.setText(Messages.getString("Duration__") + 
      formatTime(t));
  }
  
  private void onStop() {
    pauseTime = 0.0D;
    totalPauseTime = 0.0D;
    m_state = 0;
    m_playButton.setText(Messages.getString("Play"));
    m_playButton.setEnabled(true);
    m_recordButton.setText(Messages.getString("Record"));
    m_recordButton.setEnabled(true);
    m_pauseButton.setEnabled(false);
    m_durationUpdateTimer.stop();
    checkNameForValidity();
  }
  
  private void onRecord() {
    if (m_recordButton.getText().startsWith(Messages.getString("Record"))) {
      capture.start();
      m_playButton.setEnabled(false);
      m_pauseButton.setEnabled(true);
      m_recordButton.setText(Messages.getString("Stop"));
      m_state = 1;
      m_durationT0 = System.currentTimeMillis();
      m_durationUpdateTimer.start();
    } else {
      capture.stop();
    }
  }
  
  private void onPause() {
    if (m_pauseButton.getText().startsWith(Messages.getString("Pause"))) {
      if (capture.thread != null) {
        capture.line.stop();
        m_state = 2;
      }
      else if (playback.thread != null) {
        playback.line.stop();
        m_state = 2;
      }
      
      m_pauseButton.setText(Messages.getString("Resume"));
    } else {
      if (capture.thread != null) {
        capture.line.start();
        m_state = 3;
      }
      else if (playback.thread != null) {
        playback.line.start();
        m_state = 3;
      }
      
      m_pauseButton.setText(Messages.getString("Pause"));
    }
  }
  
  private void onPlay() {
    if (m_playButton.getText().startsWith(Messages.getString("Play"))) {
      playback.start();
      m_recordButton.setEnabled(false);
      m_pauseButton.setEnabled(true);
      m_playButton.setText(Messages.getString("Stop"));
      m_state = 4;
      m_durationT0 = System.currentTimeMillis();
      m_durationUpdateTimer.start();
    } else {
      playback.stop();
    }
  }
  
  private void onCancel() {
    onStop();
    setSound(null);
  }
  
  private void onOK() {
    onStop();
    Sound sound = new Sound();
    name.set(m_nameTextField.getText());
    dataSource.set(m_dataSource);
    setSound(sound);
  }
  
  public class Playback implements Runnable
  {
    SourceDataLine line;
    Thread thread;
    
    public Playback() {}
    
    public void start()
    {
      thread = new Thread(this);
      thread.setName(Messages.getString("Playback"));
      thread.start();
    }
    
    public void stop() {
      thread = null;
    }
    
    private void shutDown(String message)
    {
      if (message != null) {
        AuthoringTool.showErrorDialog(message, null);
      }
      if (thread != null) {
        thread = null;
      }
      SoundRecorder.this.onStop();
    }
    
    public void run()
    {
      if (audioInputStream == null) {
        shutDown(Messages.getString("No_loaded_audio_to_play_back"));
        return;
      }
      try
      {
        audioInputStream.reset();
      } catch (Exception e) {
        shutDown(Messages.getString("Unable_to_reset_the_stream_n") + e);
        return;
      }
      

      AudioFormat format = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, 
        true);
      AudioInputStream playbackInputStream = 
        AudioSystem.getAudioInputStream(format, audioInputStream);
      
      if (playbackInputStream == null) {
        shutDown(
          Messages.getString("Unable_to_convert_stream_of_format_") + 
          audioInputStream + " " + 
          Messages.getString("to_format_") + 
          format);
        return;
      }
      


      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        shutDown(Messages.getString("Line_matching_") + info + " " + 
          Messages.getString("not_supported_"));
        return;
      }
      
      try
      {
        line = ((SourceDataLine)AudioSystem.getLine(info));
        line.open(format, 16384);
      } catch (LineUnavailableException ex) {
        shutDown(Messages.getString("Unable_to_open_the_line__") + ex);
        return;
      }
      

      int frameSizeInBytes = format.getFrameSize();
      int bufferLengthInFrames = line.getBufferSize() / 8;
      int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
      byte[] data = new byte[bufferLengthInBytes];
      int numBytesRead = 0;
      

      line.start();
      
      while (thread != null) {
        try {
          if ((numBytesRead = playbackInputStream.read(data)) == -1) {
            break;
          }
          int numBytesRemaining = numBytesRead;
          while (numBytesRemaining > 0)
          {
            numBytesRemaining = numBytesRemaining - line.write(data, 0, numBytesRemaining);
          }
        } catch (Exception e) {
          shutDown(Messages.getString("Error_during_playback__") + e);
          break;
        }
      }
      

      if (thread != null) {
        line.drain();
      }
      line.stop();
      line.close();
      line = null;
      shutDown(null);
    }
  }
  
  class Capture implements Runnable
  {
    TargetDataLine line;
    Thread thread;
    
    Capture() {}
    
    public void start()
    {
      thread = new Thread(this);
      thread.setName(Messages.getString("Capture"));
      thread.start();
    }
    
    public void stop() {
      thread = null;
    }
    
    private void shutDown(String message) {
      if (message != null) {
        AuthoringTool.showErrorDialog(message, null);
      }
      if (thread != null) {
        thread = null;
      }
      SoundRecorder.this.onStop();
    }
    
    public void run()
    {
      audioInputStream = null;
      


      AudioFormat format = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, 
        true);
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
      
      if (!AudioSystem.isLineSupported(info)) {
        shutDown(Messages.getString("Line_matching_") + info + " " + 
          Messages.getString("not_supported_"));
        return;
      }
      
      try
      {
        line = ((TargetDataLine)AudioSystem.getLine(info));
        line.open(format, line.getBufferSize());
      } catch (LineUnavailableException ex) {
        shutDown(Messages.getString("Unable_to_open_the_line__") + ex);
        return;
      } catch (SecurityException ex) {
        shutDown(ex.toString());
        return;
      } catch (Exception ex) {
        shutDown(ex.toString());
        return;
      }
      

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int frameSizeInBytes = format.getFrameSize();
      int bufferLengthInFrames = line.getBufferSize() / 8;
      int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
      byte[] data = new byte[bufferLengthInBytes];
      

      line.start();
      
      while (thread != null) { int numBytesRead;
        if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
          break;
        }
        out.write(data, 0, numBytesRead);
      }
      

      line.stop();
      line.close();
      line = null;
      
      try
      {
        out.flush();
        out.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      

      byte[] audioBytes = out.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
      audioInputStream = new AudioInputStream(bais, format, 
        audioBytes.length / frameSizeInBytes);
      try
      {
        audioInputStream.reset();
      } catch (Exception e) {
        return;
      }
      
      File file = new File(soundDirectory + "/" + 
        m_nameTextField.getText() + ".wav");
      
      try
      {
        if (AudioSystem.write(audioInputStream, 
          AudioFileFormat.Type.WAVE, file) == -1) {
          throw new IOException(
            Messages.getString("Problems_writing_to_file"));
        }
      }
      catch (Exception localException1)
      {
        try {
          m_dataSource = 
            Manager.createDataSource(file);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        m_dataSource.waitForRealizedPlayerCount(1, 0L);
        
        shutDown(null);
      }
    }
  }
}

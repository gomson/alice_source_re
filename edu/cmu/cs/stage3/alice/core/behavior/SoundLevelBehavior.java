package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.Renderer;
import javax.media.UnsupportedPlugInException;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;

























public class SoundLevelBehavior
  extends TriggerBehavior
  implements ControllerListener, Renderer
{
  public final NumberProperty level = new NumberProperty(this, "level", new Double(0.3D));
  
  private Processor player;
  private DataSource dataSource;
  private ControllerErrorEvent m_errorEvent;
  Format inputFormat;
  Format[] inputFormats;
  
  public SoundLevelBehavior()
  {
    inputFormats = new Format[] {
      new AudioFormat("LINEAR", 
      -1.0D, 
      16, 
      -1, 
      0, 
      -1, 
      -1, 
      -1.0D, 
      Format.byteArray) };
    
    multipleRuntimeResponsePolicy.set(MultipleRuntimeResponsePolicy.IGNORE_MULTIPLE);
  }
  
  public void started(World world, double time) {
    super.started(world, time);
    
    AudioFormat format = new AudioFormat("LINEAR", 
      -1.0D, 
      16, 
      1);
    Vector captureDeviceList = CaptureDeviceManager.getDeviceList(format);
    if (captureDeviceList.size() <= 0) {
      System.err.println("No Audio Capture Devices Detected");
      return;
    }
    CaptureDeviceInfo captureDevice = (CaptureDeviceInfo)captureDeviceList.firstElement();
    MediaLocator inputLocator = captureDevice.getLocator();
    dataSource = null;
    try {
      dataSource = Manager.createDataSource(inputLocator);
    } catch (NoDataSourceException ndse) {
      ndse.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    if (dataSource == null) { return;
    }
    player = null;
    try {
      player = Manager.createProcessor(dataSource);
    } catch (NoProcessorException npe) {
      npe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    if (player == null) { return;
    }
    player.addControllerListener(this);
    m_errorEvent = null;
    player.configure();
    while ((player.getState() == 140) && (m_errorEvent == null)) {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }
    if ((m_errorEvent != null) || (player.getState() != 180)) { player.removeControllerListener(this);return;
    }
    player.setContentDescriptor(null);
    try
    {
      player.getTrackControls()[0].setRenderer(this);
    } catch (UnsupportedPlugInException upie) {
      upie.printStackTrace();
    }
    
    m_errorEvent = null;
    player.realize();
    while ((player.getState() == 200) && (m_errorEvent == null)) {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }
    player.removeControllerListener(this);
    if ((m_errorEvent != null) || (player.getState() != 300)) { return;
    }
    



























    player.start();
  }
  
  public void stopped(World world, double time) {
    super.stopped(world, time);
    try {
      player.stop();
      dataSource.stop();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    dataSource.disconnect();
  }
  


  public void controllerUpdate(ControllerEvent event)
  {
    if ((event instanceof ControllerErrorEvent)) {
      m_errorEvent = ((ControllerErrorEvent)event);
    }
  }
  

  public Format[] getSupportedInputFormats()
  {
    return inputFormats;
  }
  
  public Format setInputFormat(Format format) {
    inputFormat = format;
    return format;
  }
  
  public String getName()
  {
    return "SoundLevelBehavior";
  }
  
  public void close() {}
  
  public void open() {}
  
  public void reset() {}
  
  public void start() {}
  
  public void stop() {}
  
  public Object[] getControls() {
    return new Object[0];
  }
  
  public Object getControl(String name) {
    return null;
  }
  
  public int process(Buffer input) {
    byte[] inData = (byte[])input.getData();
    int inOffset = input.getOffset();
    int dataLength = input.getLength();
    
    int numSamples = dataLength / 2;
    
    long start_t = input.getTimeStamp() / 1000000L;
    
    for (int i = 0; i < numSamples / ((AudioFormat)input.getFormat()).getChannels(); i++)
    {
      int tempL = inData[(inOffset++)];
      int tempH = inData[(inOffset++)];
      int lsample = tempH << 8 | tempL & 0xFF;
      
      int rsample;
      int rsample;
      if (((AudioFormat)input.getFormat()).getChannels() == 2) {
        tempL = inData[(inOffset++)];
        tempH = inData[(inOffset++)];
        rsample = tempH << 8 | tempL & 0xFF;
      } else {
        rsample = lsample;
      }
      if ((lsample / 65535.0D >= level.doubleValue()) || (rsample / 65535.0D >= level.doubleValue())) {
        trigger(System.currentTimeMillis() * 0.001D);
      }
    }
    return 0;
  }
}

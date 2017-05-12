package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.media.ConfigureCompleteEvent;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;



































public class Merge
  implements ControllerListener, DataSinkListener
{
  Processor[] processors = null;
  
  String outputFile = null;
  
  String videoEncoding = "JPEG";
  
  String audioEncoding = "LINEAR";
  
  String outputType = "video.quicktime";
  
  DataSource[] dataOutputs = null;
  
  DataSource merger = null;
  
  DataSource outputDataSource;
  
  Processor outputProcessor;
  
  ProcessorModel outputPM;
  
  DataSink outputDataSink;
  
  MediaLocator outputLocator;
  
  boolean done = false;
  
  VideoFormat videoFormat = null;
  
  AudioFormat audioFormat = null;
  
  public Merge(String output) {
    outputFile = output;
  }
  
  public Merge(String output, String outputTy)
  {
    outputFile = output;
    outputType = outputTy;
  }
  
  public void doMerge(Vector sourcesURLs) {
    processors = new Processor[sourcesURLs.size()];
    dataOutputs = new DataSource[sourcesURLs.size()];
    
    for (int i = 0; i < sourcesURLs.size(); i++) {
      String source = (String)sourcesURLs.elementAt(i);
      MediaLocator ml = new MediaLocator(source);
      ProcessorModel pm = new MyPM(ml);
      try {
        processors[i] = Manager.createRealizedProcessor(pm);
        dataOutputs[i] = processors[i].getDataOutput();
        processors[i].start();
      } catch (Exception e) {
        System.err.println(Messages.getString("Failed_to_create_a_processor__") + e);
      }
    }
    
    try
    {
      merger = Manager.createMergingDataSource(dataOutputs);
      merger.connect();
      merger.start();
    } catch (Exception ex) {
      System.err.println(Messages.getString("Failed_to_merge_data_sources_") + ex);
    }
    
    if (merger == null) {
      System.err.println(Messages.getString("Failed_to_merge_data_sources_"));
    }
    







    ProcessorModel outputPM = new MyPMOut(merger);
    try
    {
      outputProcessor = Manager.createRealizedProcessor(outputPM);
      outputDataSource = outputProcessor.getDataOutput();
    } catch (Exception exc) {
      System.err.println(Messages.getString("Failed_to_create_output_processor__") + exc);
    }
    

    while (outputDataSink == null) {
      try {
        outputLocator = new MediaLocator(outputFile);
        outputDataSink = Manager.createDataSink(outputDataSource, 
          outputLocator);
        outputDataSink.open();
      }
      catch (Exception localException1) {}
    }
    

    outputProcessor.addControllerListener(this);
    outputDataSink.addDataSinkListener(this);
    try
    {
      outputDataSink.start();
      outputProcessor.start();
    } catch (Exception excep) {
      System.err.println(Messages.getString("Failed_to_start_file_writing__") + excep);
    }
    















    waitForFileDone();
    
    if (outputDataSink != null)
    {
      outputDataSink.close();
      outputDataSink.removeDataSinkListener(this);
    }
    
    synchronized (this)
    {
      if (outputProcessor != null)
      {
        outputProcessor.close();
        outputProcessor.removeControllerListener(this);
      }
    }
    try
    {
      merger.stop();
      merger.disconnect();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    for (int ii = 0; ii < processors.length; ii++) {
      processors[ii].stop();
      processors[ii].close();
      processors[ii].deallocate();
    }
  }
  

  class MyPM
    extends ProcessorModel
  {
    MediaLocator inputLocator;
    
    public MyPM(MediaLocator inputLocator)
    {
      this.inputLocator = inputLocator;
    }
    
    public ContentDescriptor getContentDescriptor()
    {
      return new ContentDescriptor("raw");
    }
    
    public DataSource getInputDataSource()
    {
      return null;
    }
    
    public MediaLocator getInputLocator()
    {
      return inputLocator;
    }
    
    public Format getOutputTrackFormat(int index)
    {
      return null;
    }
    
    public int getTrackCount(int n)
    {
      return n;
    }
    
    public boolean isFormatAcceptable(int index, Format format)
    {
      if (videoFormat == null) {
        videoFormat = new VideoFormat(videoEncoding);
      }
      if (audioFormat == null) {
        audioFormat = new AudioFormat(audioEncoding);
      }
      if ((format.matches(videoFormat)) || (format.matches(audioFormat))) {
        return true;
      }
      return false;
    }
  }
  
  class MyPMOut extends ProcessorModel
  {
    DataSource inputDataSource;
    
    public MyPMOut(DataSource inputDataSource) {
      this.inputDataSource = inputDataSource;
    }
    
    public ContentDescriptor getContentDescriptor()
    {
      return new FileTypeDescriptor(outputType);
    }
    
    public DataSource getInputDataSource()
    {
      return inputDataSource;
    }
    
    public MediaLocator getInputLocator()
    {
      return null;
    }
    
    public Format getOutputTrackFormat(int index)
    {
      return null;
    }
    
    public int getTrackCount(int n)
    {
      return n;
    }
    
    public boolean isFormatAcceptable(int index, Format format)
    {
      if (videoFormat == null) {
        videoFormat = new VideoFormat(videoEncoding);
      }
      if (audioFormat == null) {
        audioFormat = new AudioFormat(audioEncoding);
      }
      if ((format.matches(videoFormat)) || (format.matches(audioFormat))) {
        return true;
      }
      return false;
    }
  }
  
  private void showUsage()
  {
    System.err.println(Messages.getString("Usage__Merge__url1___url2____url3__________o__out_URL_____v__video_encoding_____a__audio_encoding_____t__content_type__"));
  }
  
  public void doSingle(DataSource ds)
  {
    Processor p = null;
    MediaLocator outML = new MediaLocator(outputFile);
    

    try
    {
      p = Manager.createProcessor(ds);
    } catch (Exception e) {
      System.err.println(Messages.getString("Cannot_create_a_processor_from_the_data_source_"));
    }
    
    p.addControllerListener(this);
    


    p.configure();
    if (!waitForState(p, 180)) {
      System.err.println(Messages.getString("Failed_to_configure_the_processor_"));
    }
    

    p.setContentDescriptor(new ContentDescriptor("audio.x_wav"));
    


    TrackControl[] tcs = p.getTrackControls();
    Format[] f = tcs[0].getSupportedFormats();
    if ((f == null) || (f.length <= 0)) {
      System.err.println(Messages.getString("The_mux_does_not_support_the_input_format__") + 
        tcs[0].getFormat());
    }
    
    tcs[0].setFormat(f[0]);
    




    p.realize();
    if (!waitForState(p, 300)) {
      System.err.println(Messages.getString("Failed_to_realize_the_processor_"));
    }
    
    DataSink dsink;
    
    while ((dsink = createDataSink(p, outML)) == null) {}
    




    dsink.addDataSinkListener(this);
    fileDone = false;
    


    try
    {
      p.start();
      dsink.start();
    }
    catch (IOException localIOException) {}
    



    waitForFileDone();
    
    try
    {
      dsink.close();
    }
    catch (Exception localException1) {}
    p.removeControllerListener(this);
    dsink.removeDataSinkListener(this);
    p.close();
  }
  


  Object waitSync = new Object();
  
  boolean stateTransitionOK = true;
  



  boolean waitForState(Processor p, int state)
  {
    synchronized (waitSync) {
      try {
        do {
          waitSync.wait();
          if (p.getState() >= state) break; } while (stateTransitionOK);
      }
      catch (Exception localException) {}
    }
    
    return stateTransitionOK;
  }
  



  public void controllerUpdate(ControllerEvent evt)
  {
    if (((evt instanceof ConfigureCompleteEvent)) || 
      ((evt instanceof RealizeCompleteEvent)) || 
      ((evt instanceof PrefetchCompleteEvent)))
      synchronized (waitSync) {
        stateTransitionOK = true;
        waitSync.notifyAll();
      }
    if ((evt instanceof ResourceUnavailableEvent))
      synchronized (waitSync) {
        stateTransitionOK = false;
        waitSync.notifyAll();
      }
    if ((evt instanceof EndOfMediaEvent)) {
      evt.getSourceController().stop();
      evt.getSourceController().close();
    }
  }
  
  Object waitFileSync = new Object();
  
  boolean fileDone = false;
  
  boolean fileSuccess = true;
  


  boolean waitForFileDone()
  {
    synchronized (waitFileSync) {
      try {
        while (!fileDone) {
          waitFileSync.wait();
        }
      } catch (Exception localException) {}
    }
    return fileSuccess;
  }
  



  public void dataSinkUpdate(DataSinkEvent evt)
  {
    if ((evt instanceof EndOfStreamEvent))
      synchronized (waitFileSync) {
        fileDone = true;
        waitFileSync.notifyAll();
      }
    if ((evt instanceof DataSinkErrorEvent)) {
      synchronized (waitFileSync) {
        fileDone = true;
        fileSuccess = false;
        waitFileSync.notifyAll();
      }
    }
  }
  

  DataSink createDataSink(Processor p, MediaLocator outML)
  {
    DataSource ds;
    if ((ds = p.getDataOutput()) == null)
    {
      System.err.println(Messages.getString("Something_is_really_wrong__the_processor_does_not_have_an_output_DataSource"));
      return null;
    }
    


    try
    {
      DataSink dsink = Manager.createDataSink(ds, outML);
      dsink.open();
    }
    catch (Exception e) {
      return null;
    }
    DataSink dsink;
    return dsink;
  }
}

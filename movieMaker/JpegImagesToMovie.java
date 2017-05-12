package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import javax.media.Buffer;
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
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;









































public class JpegImagesToMovie
  implements ControllerListener, DataSinkListener
{
  private int fRate = 16;
  




  public JpegImagesToMovie() {}
  




  public boolean doItQuicktime(int width, int height, int frameRate, List inFiles, MediaLocator outML)
  {
    fRate = frameRate;
    return doIt(width, height, frameRate, inFiles, outML, 
      "video.quicktime");
  }
  











  public boolean doItQuicktime(int width, int height, int frameRate, List inFiles, String outputURL)
  {
    fRate = frameRate;
    MediaLocator oml = createMediaLocator(outputURL);
    return doItQuicktime(width, height, frameRate, inFiles, oml);
  }
  










  public boolean doItAVI(int width, int height, int frameRate, List inFiles, String outputURL)
  {
    fRate = frameRate;
    MediaLocator oml = createMediaLocator(outputURL);
    return doItAVI(width, height, frameRate, inFiles, oml);
  }
  










  public boolean doItAVI(int width, int height, int frameRate, List inFiles, MediaLocator outML)
  {
    fRate = frameRate;
    return doItAVI(width, height, frameRate, inFiles, outML, 
      "video.x_msvideo");
  }
  











  public boolean doIt(int width, int height, int frameRate, List inFiles, MediaLocator outML, String type)
  {
    fRate = frameRate;
    ImageDataSource ids = new ImageDataSource(width, height, 
      frameRate, inFiles);
    


    try
    {
      p = Manager.createProcessor(ids);
    } catch (Exception e) {
      Processor p;
      return false;
    }
    Processor p;
    p.addControllerListener(this);
    


    p.configure();
    if (!waitForState(p, 180))
    {
      return false;
    }
    

    p.setContentDescriptor(new ContentDescriptor(type));
    


    TrackControl[] tcs = p.getTrackControls();
    Format[] f = tcs[0].getSupportedFormats();
    if ((f == null) || (f.length <= 0))
    {
      return false;
    }
    
    tcs[0].setFormat(f[0]);
    




    p.realize();
    if (!waitForState(p, 300))
    {
      return false;
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
    catch (IOException e) {
      return false;
    }
    

    waitForFileDone();
    
    try
    {
      dsink.removeDataSinkListener(this);
      dsink.stop();
      dsink.close();
    } catch (Exception e) {
      System.err.println(Messages.getString("DataSink_did_not_close"));
    }
    
    p.removeControllerListener(this);
    p.close();
    p.deallocate();
    


    return true;
  }
  











  public boolean doItAVI(int width, int height, int frameRate, List inFiles, MediaLocator outML, String type)
  {
    ImageDataSource ids = new ImageDataSource(width, height, 
      frameRate, inFiles);
    

    try
    {
      System.err.println(Messages.getString("__create_processor_for_the_image_datasource____"));
      p = Manager.createProcessor(ids);
    } catch (Exception e) { Processor p;
      System.err.println(Messages.getString("Yikes____Cannot_create_a_processor_from_the_data_source_"));
      return false;
    }
    Processor p;
    p.addControllerListener(this);
    


    p.configure();
    if (!waitForState(p, 180)) {
      System.err.println(Messages.getString("Failed_to_configure_the_processor_"));
      return false;
    }
    

    p.setContentDescriptor(new ContentDescriptor(type));
    


    TrackControl[] tcs = p.getTrackControls();
    
    TrackControl[] arrTrackControls = p.getTrackControls();
    for (int i = 0; i < arrTrackControls.length; i++) {
      Format format = arrTrackControls[i].getFormat();
      if ((format instanceof VideoFormat)) {
        arrTrackControls[i].setFormat(new VideoFormat("rgb"));
      }
    }
    





    p.realize();
    if (!waitForState(p, 300)) {
      System.err.println(Messages.getString("Failed_to_realize_the_processor_"));
      return false;
    }
    
    DataSink dsink;
    
    if ((dsink = createDataSink(p, outML)) == null) {
      System.err.println(Messages.getString("Failed_to_create_a_DataSink_for_the_given_output_MediaLocator__") + outML);
      return false;
    }
    
    dsink.addDataSinkListener(this);
    fileDone = false;
    
    System.err.println(Messages.getString("start_processing___"));
    
    try
    {
      p.start();
      dsink.start();
    } catch (IOException e) {
      System.err.println(Messages.getString("IO_error_during_processing"));
      return false;
    }
    

    waitForFileDone();
    
    try
    {
      dsink.close();
    } catch (Exception localException1) {}
    p.removeControllerListener(this);
    p.close();
    p.deallocate();
    

    return true;
  }
  



  DataSink createDataSink(Processor p, MediaLocator outML)
  {
    DataSource ds;
    

    if ((ds = p.getDataOutput()) == null)
    {
      return null;
    }
    


    try
    {
      dsink = Manager.createDataSink(ds, outML);
    } catch (Exception e) {
      DataSink dsink;
      return null; }
    DataSink dsink;
    if (dsink == null) {
      return null;
    }
    try {
      dsink.open();
    } catch (SecurityException e) {
      try {
        dsink.stop();
      } catch (IOException e1) {
        return null;
      }
      dsink.close();
      return null;
    } catch (IOException e) {
      try {
        dsink.stop();
      } catch (IOException e1) {
        dsink.close();
        return null;
      }
      dsink.close();
      return null;
    }
    return dsink;
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
        while (!fileDone)
          waitFileSync.wait();
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
  

  public static void main(String[] args)
  {
    if (args.length == 0) {
      prUsage();
    }
    
    int i = 0;
    int width = -1;int height = -1;int frameRate = 1;
    List inputFiles = new ArrayList();
    String outputURL = null;
    
    while (i < args.length)
    {
      if (args[i].equals("-w")) {
        i++;
        if (i >= args.length)
          prUsage();
        width = new Integer(args[i]).intValue();
      } else if (args[i].equals("-h")) {
        i++;
        if (i >= args.length)
          prUsage();
        height = new Integer(args[i]).intValue();
      } else if (args[i].equals("-f")) {
        i++;
        if (i >= args.length)
          prUsage();
        frameRate = new Integer(args[i]).intValue();
      } else if (args[i].equals("-o")) {
        i++;
        if (i >= args.length)
          prUsage();
        outputURL = args[i];
      } else {
        inputFiles.add(args[i]);
      }
      i++;
    }
    
    if ((outputURL == null) || (inputFiles.size() == 0)) {
      prUsage();
    }
    
    if ((!outputURL.endsWith(".mov")) && (!outputURL.endsWith(".MOV"))) {
      System.err.println(Messages.getString("The_output_file_extension_should_end_with_a__mov_extension"));
      prUsage();
    }
    
    if ((width < 0) || (height < 0)) {
      System.err.println(Messages.getString("Please_specify_the_correct_image_size_"));
      prUsage();
    }
    

    if (frameRate < 1) {
      frameRate = 1;
    }
    
    MediaLocator oml;
    
    if ((oml = createMediaLocator(outputURL)) == null) {
      System.err.println(Messages.getString("Cannot_build_media_locator_from__") + outputURL);
      System.exit(0);
    }
    
    JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
    imageToMovie.doItQuicktime(width, height, frameRate, inputFiles, oml);
    
    System.exit(0);
  }
  
  static void prUsage() {
    System.err.println(Messages.getString("Usage__java_JpegImagesToMovie__w__width___h__height___f__frame_rate___o__output_URL___input_JPEG_file_1___input_JPEG_file_2_____"));
    System.exit(-1);
  }
  


  static MediaLocator createMediaLocator(String url)
  {
    MediaLocator ml;
    

    if ((url.indexOf(":") > 0) && ((ml = new MediaLocator(url)) != null)) {
      return ml;
    }
    if (url.startsWith(File.separator)) { MediaLocator ml;
      if ((ml = new MediaLocator("file:" + url)) != null)
        return ml;
    } else {
      String file = "file:" + System.getProperty("user.dir") + File.separator + url;
      MediaLocator ml; if ((ml = new MediaLocator(file)) != null) {
        return ml;
      }
    }
    return null;
  }
  






  class ImageDataSource
    extends PullBufferDataSource
  {
    JpegImagesToMovie.ImageSourceStream[] streams;
    





    ImageDataSource(int width, int height, int frameRate, List images)
    {
      streams = new JpegImagesToMovie.ImageSourceStream[1];
      streams[0] = new JpegImagesToMovie.ImageSourceStream(JpegImagesToMovie.this, width, height, 
        frameRate, images);
    }
    

    public void setLocator(MediaLocator source) {}
    

    public MediaLocator getLocator()
    {
      return null;
    }
    




    public String getContentType()
    {
      return "raw";
    }
    



    public void connect() {}
    


    public void disconnect() {}
    


    public void start() {}
    


    public void stop() {}
    


    public PullBufferStream[] getStreams()
    {
      return streams;
    }
    





    public Time getDuration()
    {
      return DURATION_UNKNOWN;
    }
    
    public Object[] getControls()
    {
      return new Object[0];
    }
    
    public Object getControl(String type)
    {
      return null;
    }
  }
  

  class ImageSourceStream
    implements PullBufferStream
  {
    List images;
    
    int width;
    int height;
    VideoFormat format;
    long frame = 0L;
    
    int nextImage = 0;
    boolean ended = false;
    
    public ImageSourceStream(int width, int height, int frameRate, List images)
    {
      this.width = width;
      this.height = height;
      this.images = images;
      
      format = new VideoFormat(
        "jpeg", 
        new Dimension(width, height), 
        -1, 
        Format.byteArray, 
        frameRate);
    }
    


    public boolean willReadBlock()
    {
      return false;
    }
    




    public void read(Buffer buf)
      throws IOException
    {
      if (nextImage >= images.size())
      {

        buf.setEOM(true);
        buf.setOffset(0);
        buf.setLength(0);
        ended = true;
        return;
      }
      
      String imageFile = (String)images.get(nextImage);
      nextImage += 1;
      




      RandomAccessFile raFile = new RandomAccessFile(imageFile, "r");
      
      byte[] data = null;
      


      if ((buf.getData() instanceof byte[])) {
        data = (byte[])buf.getData();
      }
      
      if ((data == null) || (data.length < raFile.length())) {
        data = new byte[(int)raFile.length()];
        buf.setData(data);
      }
      

      raFile.readFully(data, 0, (int)raFile.length());
      


      buf.setOffset(0);
      buf.setLength((int)raFile.length());
      buf.setFormat(format);
      buf.setFlags(buf.getFlags() | 0x10);
      


      raFile.close();
    }
    


    public Format getFormat()
    {
      return format;
    }
    
    public ContentDescriptor getContentDescriptor() {
      return new ContentDescriptor("raw");
    }
    
    public long getContentLength() {
      return 0L;
    }
    
    public boolean endOfStream() {
      return ended;
    }
    
    public Object[] getControls() {
      return new Object[0];
    }
    
    public Object getControl(String type) {
      return null;
    }
  }
}

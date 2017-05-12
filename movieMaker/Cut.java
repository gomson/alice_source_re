package movieMaker;

import com.sun.media.MimeManager;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Owned;
import javax.media.Player;
import javax.media.Processor;
import javax.media.Time;
import javax.media.control.FramePositioningControl;
import javax.media.control.QualityControl;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;



































public class Cut
  implements ControllerListener, DataSinkListener
{
  public Cut() {}
  
  public static void main(String[] args)
  {
    String inputURL = null;
    String outputURL = null;
    
    Vector startV = new Vector();
    Vector endV = new Vector();
    boolean frameMode = false;
    
    if (args.length == 0) {
      prUsage();
    }
    
    int i = 0;
    while (i < args.length)
    {
      if (args[i].equals("-o")) {
        i++;
        if (i >= args.length)
          prUsage();
        outputURL = args[i];
      } else if (args[i].equals("-f")) {
        frameMode = true;
      } else if (args[i].equals("-s")) {
        i++;
        if (i >= args.length)
          prUsage();
        startV.addElement(new Long(args[i]));
      } else if (args[i].equals("-e")) {
        i++;
        if (i >= args.length)
          prUsage();
        endV.addElement(new Long(args[i]));
        


        if (startV.size() != endV.size()) {
          if (startV.size() == 0) {
            startV.addElement(new Long(0L));
          } else
            prUsage();
        }
      } else {
        inputURL = args[i];
      }
      i++;
    }
    
    if (inputURL == null) {
      System.err.println(Messages.getString("No_input_url_specified_"));
      prUsage();
    }
    
    if (outputURL == null) {
      System.err.println(Messages.getString("No_output_url_specified_"));
      prUsage();
    }
    
    if ((startV.size() == 0) && (endV.size() == 0)) {
      System.err.println(Messages.getString("No_start_and_end_point_specified_"));
      prUsage();
    }
    

    if (startV.size() > endV.size()) {
      if (startV.size() == endV.size() + 1) {
        endV.addElement(new Long(Long.MAX_VALUE));
      } else {
        prUsage();
      }
    }
    long[] start = new long[startV.size()];
    long[] end = new long[startV.size()];
    long prevEnd = 0L;
    

    for (int j = 0; j < start.length; j++)
    {
      start[j] = ((Long)startV.elementAt(j)).longValue();
      end[j] = ((Long)endV.elementAt(j)).longValue();
      
      if (prevEnd > start[j]) {
        System.err.println(Messages.getString("Previous_end_point_cannot_be___the_next_start_point_"));
        prUsage();
      } else if (start[j] >= end[j]) {
        System.err.println(Messages.getString("Start_point_cannot_be____end_point_"));
        prUsage();
      }
      
      prevEnd = end[j];
    }
    
    if (frameMode) {
      System.err.println(Messages.getString("Start_and_end_points_are_specified_in_frames_"));
    }
    else {
      for (int j = 0; j < start.length; j++) {
        start[j] *= 1000000L;
        if (end[j] != Long.MAX_VALUE)
          end[j] *= 1000000L;
        System.err.println(Messages.getString("Start__") + start[j] + " " + Messages.getString("End__") + end[j]);
      }
    }
    

    MediaLocator iml;
    

    if ((iml = createMediaLocator(inputURL)) == null) {
      System.err.println(Messages.getString("Cannot_build_media_locator_from__") + inputURL);
      System.exit(0);
    }
    MediaLocator oml;
    if ((oml = createMediaLocator(outputURL)) == null) {
      System.err.println(Messages.getString("Cannot_build_media_locator_from__") + outputURL);
      System.exit(0);
    }
    

    Cut cut = new Cut();
    
    if (!cut.doIt(iml, oml, start, end, frameMode)) {
      System.err.println(Messages.getString("Failed_to_cut_the_input"));
    }
    
    System.exit(0);
  }
  
  public boolean doCut(String input, String output, Vector startV, Vector endV)
  {
    String inputURL = input;
    String outputURL = output;
    


    long[] start = new long[startV.size()];
    long[] end = new long[startV.size()];
    long prevEnd = 0L;
    

    for (int j = 0; j < start.length; j++)
    {
      start[j] = ((Long)startV.elementAt(j)).longValue();
      end[j] = ((Long)endV.elementAt(j)).longValue();
      
      if (prevEnd > start[j])
      {
        prUsage();
      } else if (start[j] >= end[j])
      {
        prUsage();
      }
      
      prevEnd = end[j];
    }
    

    for (int j = 0; j < start.length; j++) {
      start[j] *= 1000000L;
      if (end[j] != Long.MAX_VALUE) {
        end[j] *= 1000000L;
      }
    }
    
    MediaLocator iml;
    if ((iml = createMediaLocator(inputURL)) == null)
    {

      return false;
    }
    MediaLocator oml;
    if ((oml = createMediaLocator(outputURL)) == null)
    {

      return false;
    }
    
    return doIt(iml, oml, start, end, false);
  }
  





  public boolean doIt(MediaLocator inML, MediaLocator outML, long[] start, long[] end, boolean frameMode)
  {
    ContentDescriptor cd;
    




    if ((cd = fileExtToCD(outML.getRemainder())) == null) {
      System.err.println(Messages.getString("Couldn_t_figure_out_from_the_file_extension_the_type_of_output_needed"));
      return false;
    }
    


    try
    {
      p = Manager.createProcessor(inML);
    } catch (Exception e) { Processor p;
      System.err.println(Messages.getString("Cannot_create_a_processor_from_the_given_url__") + e);
      return false;
    }
    
    Processor p;
    if (!waitForState(p, 180)) {
      System.err.println(Messages.getString("Failed_to_configure_the_processor_"));
      return false;
    }
    
    checkTrackFormats(p);
    

    if (!waitForState(p, 300)) {
      System.err.println(Messages.getString("Failed_to_realize_the_processor_"));
      return false;
    }
    

    setJPEGQuality(p, 0.5F);
    

    if (frameMode) {
      FramePositioningControl fpc = (FramePositioningControl)p.getControl("javax.media.control.FramePositioningControl");
      
      if (fpc != null)
      {
        for (int i = 0; i < start.length; i++) {
          Time t = fpc.mapFrameToTime((int)start[i]);
          if (t == FramePositioningControl.TIME_UNKNOWN) {
            fpc = null;
            break;
          }
          start[i] = t.getNanoseconds();
          if (end[i] != Long.MAX_VALUE)
          {
            t = fpc.mapFrameToTime((int)end[i]);
            if (t == FramePositioningControl.TIME_UNKNOWN) {
              fpc = null;
              break;
            }
            end[i] = t.getNanoseconds();
          }
        }
      }
      if (fpc == null)
      {
        return false;
      }
    }
    
    SuperCutDataSource ds = new SuperCutDataSource(p, inML, start, end);
    
    try
    {
      p = Manager.createProcessor(ds);
    }
    catch (Exception e) {
      return false;
    }
    
    p.addControllerListener(this);
    

    if (!waitForState(p, 180)) {
      System.err.println(Messages.getString("Failed_to_configure_the_processor_"));
      return false;
    }
    


    if (p.setContentDescriptor(cd) == null) {
      System.err.println(Messages.getString("Failed_to_set_the_output_content_descriptor_on_the_processor_"));
      return false;
    }
    


    if (!waitForState(p, 500)) {
      System.err.println(Messages.getString("Failed_to_realize_the_processor_"));
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
    } catch (IOException e) {
      System.err.println(Messages.getString("IO_error_during_concatenation"));
      return false;
    }
    

    waitForFileDone();
    
    try
    {
      if (dsink != null) {
        dsink.close();
        dsink.removeDataSinkListener(this);
      }
    } catch (Exception localException1) {}
    p.removeControllerListener(this);
    
    p.close();
    p.deallocate();
    

    return true;
  }
  





  void checkTrackFormats(Processor p)
  {
    TrackControl[] tc = p.getTrackControls();
    VideoFormat mpgVideo = new VideoFormat("mpeg");
    AudioFormat rawAudio = new AudioFormat("LINEAR");
    
    for (int i = 0; i < tc.length; i++) {
      Format preferred = null;
      
      if (tc[i].getFormat().matches(mpgVideo)) {
        preferred = new VideoFormat("jpeg");
      } else if (((tc[i].getFormat() instanceof AudioFormat)) && 
        (!tc[i].getFormat().matches(rawAudio))) {
        preferred = rawAudio;
      }
      
      if (preferred != null) {
        Format[] supported = tc[i].getSupportedFormats();
        Format selected = null;
        
        for (int j = 0; j < supported.length; j++) {
          if (supported[j].matches(preferred)) {
            selected = supported[j];
            break;
          }
        }
        
        if (selected != null) {
          System.err.println("  " + Messages.getString("Transcode_"));
          System.err.println(Messages.getString("from__") + tc[i].getFormat());
          System.err.println(Messages.getString("to__") + selected);
          tc[i].setFormat(selected);
        }
      }
    }
  }
  





  void setJPEGQuality(Player p, float val)
  {
    Control[] cs = p.getControls();
    QualityControl qc = null;
    VideoFormat jpegFmt = new VideoFormat("jpeg");
    


    for (int i = 0; i < cs.length; i++)
    {
      if (((cs[i] instanceof QualityControl)) && 
        ((cs[i] instanceof Owned))) {
        Object owner = ((Owned)cs[i]).getOwner();
        


        if ((owner instanceof Codec)) {
          Format[] fmts = ((Codec)owner).getSupportedOutputFormats(null);
          for (int j = 0; j < fmts.length; j++) {
            if (fmts[j].matches(jpegFmt)) {
              qc = (QualityControl)cs[i];
              qc.setQuality(val);
              System.err.println(Messages.getString("__Set_quality_to_") + 
                val + " " + Messages.getString("on_") + qc);
              break;
            }
          }
        }
        if (qc != null) {
          break;
        }
      }
    }
  }
  


  boolean isRawAudio(Format fmt)
  {
    return ((fmt instanceof AudioFormat)) && 
      (fmt.getEncoding().equalsIgnoreCase("LINEAR"));
  }
  


  public class StateWaiter
    implements ControllerListener
  {
    Processor p;
    
    boolean error = false;
    
    StateWaiter(Processor p) {
      this.p = p;
      p.addControllerListener(this);
    }
    
    public synchronized boolean waitForState(int state)
    {
      switch (state) {
      case 180: 
        p.configure(); break;
      case 300: 
        p.realize(); break;
      case 500: 
        p.prefetch(); break;
      case 600: 
        p.start();
      }
      
      while ((p.getState() < state) && (!error)) {
        try {
          wait(1000L);
        }
        catch (Exception localException) {}
      }
      
      return !error;
    }
    
    public void controllerUpdate(ControllerEvent ce) {
      if ((ce instanceof ControllerErrorEvent)) {
        error = true;
      }
      synchronized (this) {
        notifyAll();
      }
    }
  }
  



  DataSink createDataSink(Processor p, MediaLocator outML)
  {
    DataSource ds;
    

    if ((ds = p.getDataOutput()) == null) {
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
  




  boolean waitForState(Processor p, int state)
  {
    return new StateWaiter(p).waitForState(state);
  }
  




  public void controllerUpdate(ControllerEvent evt)
  {
    if ((evt instanceof ControllerErrorEvent)) {
      System.err.println(Messages.getString("Failed_to_cut_the_file_"));
    }
    else if ((evt instanceof EndOfMediaEvent)) {
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
          waitFileSync.wait(1000L);
        }
      }
      catch (Exception localException) {}
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
  




  ContentDescriptor fileExtToCD(String name)
  {
    int p;
    



    if ((p = name.lastIndexOf('.')) < 0) {
      return null;
    }
    String ext = name.substring(p + 1).toLowerCase();
    
    String type;
    
    String type;
    if (ext.equals("mp3")) {
      type = "audio.mpeg";
    } else { String type;
      if (ext.equals("wav"))
      {
        type = "audio.x_wav";
      } else {
        if ((type = MimeManager.getMimeType(ext)) == null)
          return null;
        type = ContentDescriptor.mimeTypeToPackageName(type);
      }
    }
    return new FileTypeDescriptor(type);
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
  



  static void prUsage() {}
  



  class SuperCutDataSource
    extends PushBufferDataSource
  {
    Processor p;
    


    MediaLocator ml;
    


    PushBufferDataSource ds;
    


    Cut.SuperCutStream[] streams;
    


    public SuperCutDataSource(Processor p, MediaLocator ml, long[] start, long[] end)
    {
      this.p = p;
      this.ml = ml;
      ds = ((PushBufferDataSource)p.getDataOutput());
      
      TrackControl[] tcs = p.getTrackControls();
      PushBufferStream[] pbs = ds.getStreams();
      
      streams = new Cut.SuperCutStream[pbs.length];
      for (int i = 0; i < pbs.length; i++) {
        streams[i] = new Cut.SuperCutStream(Cut.this, tcs[i], pbs[i], start, end);
      }
    }
    
    public void connect()
      throws IOException
    {}
    
    public PushBufferStream[] getStreams()
    {
      return streams;
    }
    
    public void start() throws IOException
    {
      p.start();
      ds.start();
    }
    

    public void stop()
      throws IOException
    {}
    
    public Object getControl(String name)
    {
      return null;
    }
    

    public Object[] getControls()
    {
      return new Control[0];
    }
    
    public Time getDuration()
    {
      return ds.getDuration();
    }
    

    public void disconnect() {}
    

    public String getContentType()
    {
      return "raw";
    }
    
    public MediaLocator getLocator()
    {
      return ml;
    }
    
    public void setLocator(MediaLocator ml)
    {
      System.err.println(Messages.getString("Not_interested_in_a_media_locator"));
    }
  }
  

  class SuperCutStream
    implements PushBufferStream, BufferTransferHandler
  {
    TrackControl tc;
    
    PushBufferStream pbs;
    long[] start;
    long[] end;
    boolean[] startReached;
    boolean[] endReached;
    int idx = 0;
    
    BufferTransferHandler bth;
    long timeStamp = 0L;
    long lastTS = 0L;
    int audioLen = 0;
    int audioElapsed = 0;
    boolean eos = false;
    
    Format format;
    
    Buffer buffer;
    int bufferFilled = 0;
    
    public SuperCutStream(TrackControl tc, PushBufferStream pbs, long[] start, long[] end)
    {
      this.tc = tc;
      this.pbs = pbs;
      this.start = start;
      this.end = end;
      startReached = new boolean[start.length];
      endReached = new boolean[end.length];
      for (int i = 0; i < start.length; i++) {
        int tmp103_102 = 0;endReached[i] = tmp103_102;startReached[i] = tmp103_102;
      }
      buffer = new Buffer();
      pbs.setTransferHandler(this);
    }
    





    void processData()
    {
      synchronized (buffer) {
        while (bufferFilled == 1) {
          try {
            buffer.wait();
          }
          catch (Exception localException) {}
        }
      }
      try
      {
        pbs.read(buffer);
      }
      catch (IOException localIOException) {}
      format = buffer.getFormat();
      
      if (idx >= end.length)
      {

        buffer.setOffset(0);
        buffer.setLength(0);
        buffer.setEOM(true);
      }
      
      if (buffer.isEOM()) {
        eos = true;
      }
      int len = buffer.getLength();
      

      if (checkTimeToSkip(buffer))
      {
        if (isRawAudio(buffer.getFormat()))
          audioLen += len;
        return;
      }
      

      if (isRawAudio(buffer.getFormat())) {
        audioLen += len;
      }
      
      synchronized (buffer) {
        bufferFilled = 1;
        buffer.notifyAll();
      }
      

      if (bth != null) {
        bth.transferData(this);
      }
    }
    






    public void read(Buffer rdBuf)
      throws IOException
    {
      synchronized (buffer) {
        while (bufferFilled == 0) {
          try {
            buffer.wait();
          }
          catch (Exception localException) {}
        }
      }
      
      Object oldData = rdBuf.getData();
      
      rdBuf.copy(buffer);
      buffer.setData(oldData);
      


      if (isRawAudio(rdBuf.getFormat()))
      {
        rdBuf.setTimeStamp(computeDuration(audioElapsed, rdBuf.getFormat()));
        audioElapsed += buffer.getLength();
      } else if (rdBuf.getTimeStamp() != -1L) {
        long diff = rdBuf.getTimeStamp() - lastTS;
        lastTS = rdBuf.getTimeStamp();
        if (diff > 0L)
          timeStamp += diff;
        rdBuf.setTimeStamp(timeStamp);
      }
      
      synchronized (buffer) {
        bufferFilled = 0;
        buffer.notifyAll();
      }
    }
    





    boolean checkTimeToSkip(Buffer buf)
    {
      if (idx >= startReached.length) {
        return false;
      }
      if ((!eos) && (startReached[idx] == 0) && 
        (!(startReached[idx] = checkStartTime(buf, start[idx])))) {
        return true;
      }
      

      if ((!eos) && (endReached[idx] == 0)) {
        if ((endReached[idx] = checkEndTime(buf, end[idx]))) {
          idx += 1;
          return true;
        }
      } else if (endReached[idx] != 0) {
        if (!eos) {
          return true;
        }
        buf.setOffset(0);
        buf.setLength(0);
      }
      

      return false;
    }
    



    boolean checkStartTime(Buffer buf, long startTS)
    {
      if (isRawAudio(buf.getFormat())) {
        long ts = computeDuration(audioLen + buf.getLength(), 
          buf.getFormat());
        if (ts > startTS) {
          int len = computeLength(ts - startTS, buf.getFormat());
          buf.setOffset(buf.getOffset() + buf.getLength() - len);
          buf.setLength(len);
          lastTS = buf.getTimeStamp();
          return true;
        }
      } else if (buf.getTimeStamp() >= startTS) {
        if ((buf.getFormat() instanceof VideoFormat))
        {
          if ((buf.getFlags() & 0x10) != 0) {
            lastTS = buf.getTimeStamp();
            return true;
          }
        } else {
          lastTS = buf.getTimeStamp();
          return true;
        }
      }
      return false;
    }
    



    boolean checkEndTime(Buffer buf, long endTS)
    {
      if (isRawAudio(buf.getFormat())) {
        if (computeDuration(audioLen, buf.getFormat()) >= endTS) {
          return true;
        }
        long ts = computeDuration(audioLen + buf.getLength(), 
          buf.getFormat());
        if (ts >= endTS) {
          int len = computeLength(ts - endTS, buf.getFormat());
          buf.setLength(buf.getLength() - len);
        }
        
      }
      else if (buf.getTimeStamp() > endTS) {
        return true;
      }
      
      return false;
    }
    



    public long computeDuration(int len, Format fmt)
    {
      if (!(fmt instanceof AudioFormat))
        return -1L;
      return ((AudioFormat)fmt).computeDuration(len);
    }
    



    public int computeLength(long duration, Format fmt)
    {
      if (!(fmt instanceof AudioFormat))
        return -1;
      AudioFormat af = (AudioFormat)fmt;
      
      return (int)(duration / 1000L * (af.getChannels() * af.getSampleSizeInBits()) / 1000L * af.getSampleRate() / 8000.0D);
    }
    
    public ContentDescriptor getContentDescriptor()
    {
      return new ContentDescriptor("raw");
    }
    
    public boolean endOfStream() {
      return eos;
    }
    
    public long getContentLength() {
      return -1L;
    }
    
    public Format getFormat() {
      return tc.getFormat();
    }
    
    public void setTransferHandler(BufferTransferHandler bth) {
      this.bth = bth;
    }
    
    public Object getControl(String name)
    {
      return null;
    }
    
    public Object[] getControls()
    {
      return new Control[0];
    }
    
    public synchronized void transferData(PushBufferStream pbs) {
      processData();
    }
  }
}

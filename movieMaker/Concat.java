package movieMaker;

import com.sun.media.MimeManager;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
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
import javax.media.Duration;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Owned;
import javax.media.Player;
import javax.media.Processor;
import javax.media.Time;
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



































public class Concat
  implements ControllerListener, DataSinkListener
{
  static int AUDIO = 0;
  static int VIDEO = AUDIO + 1;
  static int MEDIA_TYPES = VIDEO + 1;
  
  int totalTracks;
  
  boolean transcodeMsg = false;
  String TRANSCODE_MSG = Messages.getString("The_given_inputs_require_transcoding_to_have_a_common_format_for_concatenation_");
  

  public Concat() {}
  

  public static void main(String[] args)
  {
    Vector inputURL = new Vector();
    String outputURL = null;
    
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
      } else {
        inputURL.addElement(args[i]);
      }
      i++;
    }
    
    if (inputURL.size() == 0) {
      System.err.println(Messages.getString("No_input_url_is_specified"));
      prUsage();
    }
    
    if (outputURL == null) {
      System.err.println(Messages.getString("No_output_url_is_specified"));
      prUsage();
    }
    

    MediaLocator[] iml = new MediaLocator[inputURL.size()];
    

    for (i = 0; i < inputURL.size(); i++) {
      if ((iml[i] =  = createMediaLocator((String)inputURL.elementAt(i))) == null) {
        System.err.println(Messages.getString("Cannot_build_media_locator_from__") + inputURL);
        System.exit(0);
      }
    }
    MediaLocator oml;
    if ((oml = createMediaLocator(outputURL)) == null) {
      System.err.println(Messages.getString("Cannot_build_media_locator_from__") + outputURL);
      System.exit(0);
    }
    
    Concat concat = new Concat();
    
    if (!concat.doIt(iml, oml)) {
      System.err.println(Messages.getString("Failed_to_concatenate_the_inputs"));
    }
    
    System.exit(0);
  }
  




  public boolean doIt(MediaLocator[] inML, MediaLocator outML)
  {
    ContentDescriptor cd;
    



    if ((cd = fileExtToCD(outML.getRemainder())) == null) {
      System.err.println(Messages.getString("Couldn_t_figure_out_from_the_file_extension_the_type_of_output_needed"));
      return false;
    }
    

    ProcInfo[] pInfo = new ProcInfo[inML.length];
    
    for (int i = 0; i < inML.length; i++) {
      pInfo[i] = new ProcInfo();
      ml = inML[i];
      
      try
      {
        p = Manager.createProcessor(inML[i]);
      } catch (Exception e) {
        System.err.println(Messages.getString("Cannot_create_a_processor_from_the_given_url__") + e);
        return false;
      }
    }
    

    if (!matchTracks(pInfo, cd)) {
      System.err.println(Messages.getString("Failed_to_match_the_tracks_"));
      return false;
    }
    


    if (!buildTracks(pInfo)) {
      System.err.println(Messages.getString("Failed_to_build_processors_for_the_inputs_"));
      return false;
    }
    

    SuperGlueDataSource ds = new SuperGlueDataSource(pInfo);
    

    try
    {
      p = Manager.createProcessor(ds);
    } catch (Exception e) { Processor p;
      System.err.println(Messages.getString("Failed_to_create_a_processor_to_concatenate_the_inputs_"));
      return false;
    }
    Processor p;
    p.addControllerListener(this);
    

    if (!waitForState(p, 180)) {
      System.err.println(Messages.getString("Failed_to_configure_the_processor_"));
      return false;
    }
    


    if (p.setContentDescriptor(cd) == null) {
      System.err.println(Messages.getString("Failed_to_set_the_output_content_descriptor_on_the_processor_"));
      return false;
    }
    


    if (!waitForState(p, 300)) {
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
      dsink.removeDataSinkListener(this);
      dsink.close();
    } catch (Exception localException1) {}
    p.removeControllerListener(this);
    

    for (int ii = 0; ii < inML.length; ii++)
    {
      p.close();
      p.deallocate();
    }
    
    p.close();
    p.deallocate();
    


    return true;
  }
  













  public boolean matchTracks(ProcInfo[] pInfo, ContentDescriptor cd)
  {
    for (int i = 0; i < pInfo.length; i++)
    {
      if (!waitForState(p, 180)) {
        System.err.println(Messages.getString("__Failed_to_configure_the_processor_"));
        return false;
      }
      
      TrackControl[] tcs = p.getTrackControls();
      
      tracksByType = new TrackInfo[MEDIA_TYPES][];
      for (int type = AUDIO; type < MEDIA_TYPES; type++) {
        tracksByType[type] = new TrackInfo[tcs.length];
      }
      numTracksByType = new int[MEDIA_TYPES];
      int vIdx; int aIdx = vIdx = 0;
      

      for (int j = 0; j < tcs.length; j++) {
        if ((tcs[j].getFormat() instanceof AudioFormat)) {
          TrackInfo tInfo = new TrackInfo();
          idx = j;
          tc = tcs[j];
          tracksByType[AUDIO][(aIdx++)] = tInfo;
        } else if ((tcs[j].getFormat() instanceof VideoFormat)) {
          TrackInfo tInfo = new TrackInfo();
          idx = j;
          tc = tcs[j];
          tracksByType[VIDEO][(vIdx++)] = tInfo;
        }
      }
      
      numTracksByType[AUDIO] = aIdx;
      numTracksByType[VIDEO] = vIdx;
      p.setContentDescriptor(cd);
    }
    






    int[] total = new int[MEDIA_TYPES];
    
    for (int type = AUDIO; type < MEDIA_TYPES; type++) {
      total[type] = 0numTracksByType[type];
    }
    
    for (i = 1; i < pInfo.length; i++) {
      for (type = AUDIO; type < MEDIA_TYPES; type++) {
        if (numTracksByType[type] < total[type]) {
          total[type] = numTracksByType[type];
        }
      }
    }
    if ((total[AUDIO] < 1) && (total[VIDEO] < 1)) {
      System.err.println(Messages.getString("There_is_no_audio_or_video_tracks_to_concatenate_"));
      return false;
    }
    
    totalTracks = 0;
    for (type = AUDIO; type < MEDIA_TYPES; type++) {
      totalTracks += total[type];
    }
    

    for (i = 0; i < pInfo.length; i++) {
      for (type = AUDIO; type < MEDIA_TYPES; type++) {
        for (int j = total[type]; j < numTracksByType[type]; j++) {
          TrackInfo tInfo = tracksByType[type][j];
          disableTrack(pInfo[i], tInfo);
          System.err.println(Messages.getString("__Disable_the_following_track_since_the_other_input_media_do_not_have_a_matching_type_"));
          System.err.println("  " + tc.getFormat());
        }
        numTracksByType[type] = total[type];
      }
    }
    



    for (type = AUDIO; type < MEDIA_TYPES; type++) {
      for (i = 0; i < total[type]; i++) {
        if (!tryMatch(pInfo, type, i)) {
          System.err.println(Messages.getString("__Cannot_transcode_the_tracks_to_a_common_format_for_concatenation_"));
          return false;
        }
      }
    }
    
    return true;
  }
  



  void disableTrack(ProcInfo pInfo, TrackInfo remove)
  {
    tc.setEnabled(false);
    disabled = true;
    


    for (int type = AUDIO; type < MEDIA_TYPES; type++) {
      for (int j = 0; j < numTracksByType[type]; j++) {
        TrackInfo ti = tracksByType[type][j];
        if (idx >= idx) {
          idx -= 1;
        }
      }
    }
  }
  




  public boolean buildTracks(ProcInfo[] pInfo)
  {
    ContentDescriptor cd = new ContentDescriptor("raw");
    

    for (int i = 0; i < pInfo.length; i++) {
      Processor p = p;
      p.setContentDescriptor(cd);
      


      if (!waitForState(p, 300)) {
        System.err.println(Messages.getString("__Failed_to_realize_the_processor_"));
        return false;
      }
      

      setJPEGQuality(p, 0.5F);
      






      ds = ((PushBufferDataSource)p.getDataOutput());
      PushBufferStream[] pbs = ds.getStreams();
      


      for (int type = AUDIO; type < MEDIA_TYPES; type++) {
        for (int trackID = 0; trackID < numTracksByType[type]; trackID++) {
          TrackInfo tInfo = tracksByType[type][trackID];
          pbs = pbs[idx];
        }
      }
    }
    
    return true;
  }
  




  public boolean tryMatch(ProcInfo[] pInfo, int type, int trackID)
  {
    TrackControl tc = 0tracksByType[type][trackID].tc;
    Format origFmt = tc.getFormat();
    
    Format[] supported = tc.getSupportedFormats();
    
    for (int i = 0; i < supported.length; i++)
    {
      if ((!(supported[i] instanceof AudioFormat)) || 
      


        (supported[i].matches(tc.getFormat())) || 
        (supported[i].getEncoding().equalsIgnoreCase("LINEAR")))
      {


        if (tryTranscode(pInfo, 1, type, trackID, supported[i]))
        {




          for (int j = 0; j < pInfo.length; j++) {
            tc = tracksByType[type][trackID].tc;
            Format oldFmt = tc.getFormat();
            Format newFmt = supported[i];
            

            if (!oldFmt.matches(newFmt)) {
              if (!transcodeMsg) {
                transcodeMsg = true;
                System.err.println(TRANSCODE_MSG);
              }
              
              System.err.println(Messages.getString("__Transcoding__") + ml);
              System.err.println("  " + oldFmt);
              System.err.println(Messages.getString("to__"));
              System.err.println("  " + newFmt);
            }
            

            if ((oldFmt instanceof VideoFormat)) {
              Dimension newSize = ((VideoFormat)origFmt).getSize();
              Dimension oldSize = ((VideoFormat)oldFmt).getSize();
              
              if ((oldSize != null) && (!oldSize.equals(newSize)))
              {

                if (!transcodeMsg) {
                  transcodeMsg = true;
                  System.err.println(TRANSCODE_MSG);
                }
                System.err.println(Messages.getString("__Scaling__") + ml);
                System.err.println("  " + Messages.getString("from__") + width + 
                  " x " + height);
                System.err.println("  " + Messages.getString("to__") + width + 
                  " x " + height);
                newFmt = new VideoFormat(null, 
                  newSize, 
                  -1, 
                  null, 
                  -1.0F).intersects(newFmt);
              }
            }
            tc.setFormat(newFmt);
          }
          
          return true;
        }
      }
    }
    return false;
  }
  






  public boolean tryTranscode(ProcInfo[] pInfo, int procID, int type, int trackID, Format candidate)
  {
    if (procID >= pInfo.length) {
      return true;
    }
    boolean matched = false;
    TrackControl tc = tracksByType[type][trackID].tc;
    Format[] supported = tc.getSupportedFormats();
    
    for (int i = 0; i < supported.length; i++) {
      if ((candidate.matches(supported[i])) && 
        (tryTranscode(pInfo, procID + 1, type, trackID, candidate))) {
        matched = true;
        break;
      }
    }
    
    return matched;
  }
  



  boolean isRawAudio(TrackInfo tInfo)
  {
    Format fmt = tc.getFormat();
    return ((fmt instanceof AudioFormat)) && 
      (fmt.getEncoding().equalsIgnoreCase("LINEAR"));
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
        if (qc != null) break;
      } } }
  
  public class ProcInfo { public MediaLocator ml;
    public Processor p;
    public PushBufferDataSource ds;
    public Concat.TrackInfo[][] tracksByType;
    public int[] numTracksByType;
    public int numTracks;
    
    public ProcInfo() {} }
  
  public class StateWaiter implements ControllerListener { Processor p;
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
      dsink = Manager.createDataSink(ds, outML);
    } catch (Exception e) {
      DataSink dsink;
      return null;
    }
    DataSink dsink;
    if (dsink == null) {
      return null;
    }
    try {
      dsink.open();
    } catch (SecurityException e) {
      dsink.close();
      return null;
    } catch (IOException e) {
      dsink.close();
      return null;
    }
    
    return dsink;
  }
  




  boolean waitForState(Processor p, int state)
  {
    return new StateWaiter(p).waitForState(state);
  }
  




  public void controllerUpdate(ControllerEvent evt)
  {
    if ((evt instanceof ControllerErrorEvent)) {
      System.err.println(Messages.getString("Failed_to_concatenate_the_files_"));
      System.exit(-1);
    } else if ((evt instanceof EndOfMediaEvent)) {
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
      if (ext.equals("wav")) {
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
  
  static void prUsage()
  {
    System.err.println("Usage: java Concat -o <output> <input> ...");
    System.err.println("     <output>: input URL or file name");
    System.err.println("     <input>: output URL or file name");
    System.exit(0);
  }
  



































  boolean masterFound = false;
  
  class SuperGlueDataSource extends PushBufferDataSource
  {
    Concat.ProcInfo[] pInfo;
    int current;
    Concat.SuperGlueStream[] streams;
    
    public SuperGlueDataSource(Concat.ProcInfo[] pInfo) {
      this.pInfo = pInfo;
      streams = new Concat.SuperGlueStream[totalTracks];
      for (int i = 0; i < totalTracks; i++)
        streams[i] = new Concat.SuperGlueStream(Concat.this, this);
      current = 0;
      setStreams(pInfo[current]);
    }
    
    void setStreams(Concat.ProcInfo pInfo) {
      int j = 0;
      masterFound = false;
      for (int type = Concat.AUDIO; type < Concat.MEDIA_TYPES; type++) {
        for (int i = 0; i < numTracksByType[type]; i++) {
          if ((!masterFound) && 
            (isRawAudio(tracksByType[type][i]))) {
            streams[j].setStream(tracksByType[type][i], true);
            masterFound = true;
          } else {
            streams[j].setStream(tracksByType[type][i], false); }
          j++;
        }
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
      pInfo[current].p.start();
      pInfo[current].ds.start();
    }
    
    public void stop() throws IOException
    {}
    
    synchronized boolean handleEOM(Concat.TrackInfo tInfo)
    {
      boolean lastProcessor = current >= pInfo.length - 1;
      


      for (int type = Concat.AUDIO; type < Concat.MEDIA_TYPES; type++) {
        for (int i = 0; i < pInfo[current].numTracksByType[type]; i++) {
          if (!pInfo[current].tracksByType[type][i].done) {
            return lastProcessor;
          }
        }
      }
      
      try
      {
        pInfo[current].p.stop();
        pInfo[current].ds.stop();
      }
      catch (Exception localException) {}
      if (lastProcessor)
      {
        return lastProcessor;
      }
      


      if ((!masterFound) && 
        (pInfo[current].p.getDuration() != Duration.DURATION_UNKNOWN)) {
        masterTime += pInfo[current].p.getDuration().getNanoseconds();
      }
      

      current += 1;
      setStreams(pInfo[current]);
      try {
        start();
      } catch (Exception localException1) {}
      return lastProcessor;
    }
    

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
      return Duration.DURATION_UNKNOWN;
    }
    

    public void disconnect() {}
    

    public String getContentType()
    {
      return "raw";
    }
    
    public MediaLocator getLocator()
    {
      return pInfo[current].ml;
    }
    
    public void setLocator(MediaLocator ml)
    {
      System.err.println(Messages.getString("Not_interested_in_a_media_locator"));
    }
  }
  






  long masterTime = 0L;
  

  long masterAudioLen = 0L;
  
  class SuperGlueStream implements PushBufferStream, BufferTransferHandler
  {
    Concat.SuperGlueDataSource ds;
    Concat.TrackInfo tInfo;
    PushBufferStream pbs;
    BufferTransferHandler bth;
    boolean useAsMaster = false;
    long timeStamp = 0L;
    long lastTS = 0L;
    
    public SuperGlueStream(Concat.SuperGlueDataSource ds) {
      this.ds = ds;
    }
    
    public void setStream(Concat.TrackInfo tInfo, boolean useAsMaster) {
      this.tInfo = tInfo;
      this.useAsMaster = useAsMaster;
      if (pbs != null)
        pbs.setTransferHandler(null);
      pbs = pbs;
      

      if (masterTime > 0L)
        timeStamp = masterTime;
      lastTS = 0L;
      
      pbs.setTransferHandler(this);
    }
    
    public void read(Buffer buffer) throws IOException {
      pbs.read(buffer);
      


      if (buffer.getTimeStamp() != -1L) {
        long diff = buffer.getTimeStamp() - lastTS;
        lastTS = buffer.getTimeStamp();
        if (diff > 0L)
          timeStamp += diff;
        buffer.setTimeStamp(timeStamp);
      }
      


      if (useAsMaster) {
        if ((buffer.getFormat() instanceof AudioFormat)) {
          AudioFormat af = (AudioFormat)buffer.getFormat();
          masterAudioLen += buffer.getLength();
          long t = af.computeDuration(masterAudioLen);
          if (t > 0L) {
            masterTime = t;
          } else {
            masterTime = buffer.getTimeStamp();
          }
        } else {
          masterTime = buffer.getTimeStamp();
        }
      }
      
      if (buffer.isEOM()) {
        tInfo.done = true;
        if (!ds.handleEOM(tInfo))
        {

          buffer.setEOM(false);
          buffer.setDiscard(true);
        }
      }
    }
    
    public ContentDescriptor getContentDescriptor() {
      return new ContentDescriptor("raw");
    }
    
    public boolean endOfStream() {
      return false;
    }
    
    public long getContentLength() {
      return -1L;
    }
    
    public Format getFormat() {
      return tInfo.tc.getFormat();
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
      if (bth != null) {
        bth.transferData(this);
      }
    }
  }
  
  public class TrackInfo
  {
    public TrackControl tc;
    public PushBufferStream pbs;
    public int idx;
    public boolean done;
    public boolean disabled;
    
    public TrackInfo() {}
  }
}

package movieMaker;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.MediaLocator;

public class SoundStorage
{
  private ArrayList soundList = new ArrayList();
  
  public ArrayList frameList = new ArrayList();
  
  private boolean listening = false;
  
  private double totalLength = 0.0D;
  
  private Vector startCaptureTimes = new Vector();
  
  private Vector stopCaptureTimes = new Vector();
  

  public SoundStorage() {}
  
  public void add(Long start, Double len, edu.cmu.cs.stage3.media.jmfmedia.DataSource ds, Object to, Object from, Object rate, Object volume)
  {
    soundList.add(new SoundData(start, len, ds, to, from, 
      rate, volume));
  }
  
  public ArrayList getList() {
    return soundList;
  }
  
  public void setListening(boolean isListening, double time) {
    listening = isListening;
    
    if (listening) {
      startCaptureTimes.add(new Double(time));
    }
    else {
      stopCaptureTimes.add(new Double(time));
      totalLength += time - ((Double)startCaptureTimes.get(startCaptureTimes.size() - 1)).doubleValue();
    }
  }
  
  public void convertTimes()
  {
    long startNum = ((Long)frameList.get(0)).longValue();
    
    for (int x = 0; x < frameList.size(); x++) {
      frameList.set(x, new Long(((Long)frameList.get(x)).longValue() - startNum));
    }
    
    for (int x = 0; x < soundList.size(); x++) {
      soundList.get(x)).worldTime -= startNum;
    }
    
    for (int x = 0; x < soundList.size(); x++) {
      int index = 0;
      
      while ((index < frameList.size()) && (((Long)frameList.get(index)).longValue() < soundList.get(x)).worldTime)) {
        index++;
      }
      if (index != frameList.size())
      {


        long time2 = ((Long)frameList.get(index)).longValue();
        long time1 = ((Long)frameList.get(index == 0 ? 0 : index - 1)).longValue();
        
        if (time1 == time2) {
          soundList.get(x)).worldTime /= 1000.0D;
        } else {
          double percentage = (soundList.get(x)).worldTime - time1) / (time2 - time1);
          soundList.get(x)).worldTime = ((index - 1 + percentage) / 16.0D);
        }
      }
    }
  }
  
  public void convertCaptureTimes() { double start = ((Double)startCaptureTimes.get(0)).doubleValue();
    for (int x = 0; x < startCaptureTimes.size(); x++) {
      startCaptureTimes.set(x, new Double(((Double)startCaptureTimes.get(x)).doubleValue() - start));
      stopCaptureTimes.set(x, new Double(((Double)stopCaptureTimes.get(x)).doubleValue() - start));
    }
  }
  



  public void convertNumbers(double length)
  {
    for (int x = 0; x < soundList.size(); x++)
    {
      SoundData sd = (SoundData)soundList.get(x);
      worldTime *= length / totalLength;
      duration *= length / totalLength;
      clippedDuration *= length / totalLength;
    }
    
    for (int x = 0; x < startCaptureTimes.size(); x++) {
      double d = ((Double)startCaptureTimes.get(x)).doubleValue();
      startCaptureTimes.set(x, new Double(d * length / totalLength));
      d = ((Double)stopCaptureTimes.get(x)).doubleValue();
      stopCaptureTimes.set(x, new Double(d * length / totalLength));
    }
  }
  
  Object stateLock = new Object();
  boolean stateFailed = false;
  
  public Vector encodeFiles(double length, String exportDirectory)
  {
    Vector newDS = new Vector();
    
    String orig_file = "";
    String final_sound = "";
    String sound_slice = "";
    String sound_cut = "";
    String track_file = "";
    String silence = exportDirectory + "silence.wav";
    
    int currentChunk = 0;
    int currentLength = 0;
    
    convertTimes();
    convertCaptureTimes();
    

    for (int y = 0; y < soundList.size(); y++)
    {
      double blankLength = 0.0D;
      SoundData sd = (SoundData)soundList.get(y);
      
      if ((duration < clippedDuration) || (clippedDuration == 0.0D)) {
        clippedDuration = duration;
      }
      stopTime = duration;
      

      while (worldTime > ((Double)stopCaptureTimes.get(currentChunk)).doubleValue()) {
        currentLength = (int)(currentLength + ((Double)stopCaptureTimes.get(currentChunk)).doubleValue());
        currentChunk++;
        if (currentChunk >= stopCaptureTimes.size()) {
          break;
        }
      }
      
      if (currentChunk >= stopCaptureTimes.size())
        break;
      if (((Double)startCaptureTimes.get(currentChunk)).doubleValue() <= worldTime + clippedDuration)
      {





        if (worldTime > ((Double)startCaptureTimes.get(currentChunk)).doubleValue()) {
          blankLength = worldTime - ((Double)startCaptureTimes.get(currentChunk)).doubleValue() + currentLength;
        } else {
          blankLength = currentLength;
        }
        
        orig_file = exportDirectory + "sound" + y + "." + data.getExtension();
        
        sound_slice = exportDirectory + "sound" + y + ".wav";
        sound_cut = exportDirectory + "soundCut" + y + ".wav";
        track_file = exportDirectory + "track" + y + ".wav";
        
        writeToFile(data.getJMFDataSource(), createURL(orig_file));
        
        if (data.getExtension().equals("MP3"))
        {
          SimpleSound.convert(orig_file, sound_slice);
        }
        

        double beginning = 0.0D;
        double ending = 0.0D;
        
        beginning = cropBeginning(sd, length, currentChunk);
        ending = cropEnding(sd, length, currentChunk);
        

        if ((beginning != 0.0D) || (ending != 0.0D)) {
          sound_slice = tryToCut(length, sd, sound_slice, sound_cut, beginning, ending, blankLength);
        }
        






        try
        {
          SimpleSound s = new SimpleSound();
          s.loadFromFile(sound_slice);
          

          SimpleSound blank = new SimpleSound(blankLength, s);
          blank.writeToFile(silence);
        } catch (SoundException e) {
          AuthoringTool.showErrorDialog(edu.cmu.cs.stage3.lang.Messages.getString("Error_encoding_sound_file__"), e);
          return null;
        }
        
        if (blankLength > 0.0D) {
          Vector v = new Vector();
          v.add(createURL(silence));
          v.add(createURL(sound_slice));
          final_sound = createURL(track_file);
          
          if (!concat(v, final_sound)) {
            return null;
          }
        }
        else {
          final_sound = createURL(sound_slice);
        }
        
        newDS.add(final_sound);
      }
    }
    


    return newDS;
  }
  

  public double cropBeginning(SoundData sd, double length, int current)
  {
    if (((Double)startCaptureTimes.get(current)).doubleValue() < worldTime)
      return 0.0D;
    if (((Double)startCaptureTimes.get(current)).doubleValue() > worldTime)
      return ((Double)startCaptureTimes.get(current)).doubleValue() - worldTime;
    return 0.0D;
  }
  
  public double cropEnding(SoundData sd, double length, int current) {
    if (((Double)stopCaptureTimes.get(current)).doubleValue() < duration + worldTime)
      return worldTime + duration - ((Double)stopCaptureTimes.get(current)).doubleValue();
    if (((Double)stopCaptureTimes.get(current)).doubleValue() > duration + worldTime)
      return 0.0D;
    return 0.0D;
  }
  
  public String tryToCut(double length, SoundData sd, String file3, String file4, double cropFromBeginning, double cropFromEnding, double blankLength)
  {
    Vector start = new Vector();
    Vector stop = new Vector();
    
    if ((cropFromBeginning != 0.0D) || (cropFromEnding != 0.0D) || 
      (clippedDuration + worldTime > length))
    {
      if (duration - clippedDuration > cropFromEnding) {
        cropFromEnding = duration - clippedDuration;
      }
      startTime += cropFromBeginning;
      stopTime -= cropFromEnding;
      







      if (blankLength + (stopTime - startTime) > length) {
        stopTime = (length - blankLength + startTime - 0.02D);
      }
      if (stopTime < startTime) {
        return file3;
      }
      




      start.add(new Long((int)(startTime * 1000.0D)));
      stop.add(new Long((int)(stopTime * 1000.0D)));
      


      Cut cut = new Cut();
      if ((((Long)start.get(0)).longValue() != 0.0D) || (((Long)stop.get(0)).longValue() != clippedDuration))
        cut.doCut(createURL(file3), createURL(file4), start, stop);
      cut = null;
      
      return file4;
    }
    
    return file3;
  }
  
  public String createURL(String s)
  {
    try
    {
      url = new File(s).toURL().toString();
    } catch (MalformedURLException e) { String url;
      e.printStackTrace();
      return "";
    }
    String url;
    String mod = url.replaceFirst("file:/", "file:///");
    return mod;
  }
  


  public void writeToFile(javax.media.protocol.DataSource ds, String fileName)
  {
    Merge m = new Merge(fileName);
    if (m != null)
      m.doSingle(ds);
    m = null;
  }
  
  public boolean concat(Vector inputURL, String outputURL)
  {
    MediaLocator[] iml = new MediaLocator[2];
    
    int i = 0;
    for (i = 0; i < inputURL.size(); i++) {
      if ((iml[i] =  = Concat.createMediaLocator(
        (String)inputURL.elementAt(i))) == null)
      {

        return false;
      }
    }
    MediaLocator oml;
    if ((oml = Concat.createMediaLocator(outputURL)) == null)
    {
      return false;
    }
    
    Concat concat = new Concat();
    
    if (!concat.doIt(iml, oml))
    {
      return false;
    }
    
    return true;
  }
  
  class SoundData
  {
    double startTime = 0.0D;
    
    double worldTime = 0.0D;
    
    double duration = 0.0D;
    
    double volume = 1.0D;
    
    double clippedDuration = 11000.0D;
    
    double stopTime = 0.0D;
    
    edu.cmu.cs.stage3.media.jmfmedia.DataSource data = null;
    



    public SoundData(Long start, Double len, edu.cmu.cs.stage3.media.jmfmedia.DataSource ds, Object to, Object from, Object rate, Object vol)
    {
      worldTime = start.longValue();
      duration = len.doubleValue();
      data = ds;
      if (rate != null)
        clippedDuration = ((Double)rate).doubleValue();
      if (to != null)
        startTime = ((Double)to).doubleValue();
      if (from != null)
        stopTime = ((Double)from).doubleValue();
      if (vol != null)
        volume = ((Double)vol).doubleValue();
    }
  }
  
  class StateListener implements javax.media.ControllerListener {
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

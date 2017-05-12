package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;























public class Playback
  extends Thread
{
  private static final int BUFFER_SIZE = 16384;
  private SourceDataLine line;
  private boolean playing = false;
  




  private SimpleSound sound;
  





  public Playback(SimpleSound sound)
  {
    this.sound = sound;
  }
  



  private void shutDown(String message, Exception e)
  {
    if (message != null)
    {
      System.err.println(message);
      e.printStackTrace();
    }
    playing = false;
  }
  




  public void stopPlaying()
  {
    playing = false;
  }
  





  public boolean getPlaying()
  {
    return playing;
  }
  










  public void run()
  {
    AudioFileFormat audioFileFormat = sound.getAudioFileFormat();
    SoundExplorer soundExplorer = sound.getSoundExplorer();
    

    AudioInputStream audioInputStream = sound.makeAIS();
    if (audioInputStream == null)
    {
      shutDown(Messages.getString("There_is_no_input_stream_to_play"), null);
      return;
    }
    
    try
    {
      audioInputStream.reset();
    } catch (Exception e) {
      shutDown(Messages.getString("Problems_resetting_the_stream_n"), e);
      return;
    }
    


    DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
      audioFileFormat.getFormat());
    if (!AudioSystem.isLineSupported(info))
    {
      shutDown(Messages.getString("Line_matching_") + info + Messages.getString("not_supported_"), null);
      return;
    }
    
    try
    {
      line = ((SourceDataLine)AudioSystem.getLine(info));
      if (sound.getSoundExplorer() != null)
        line.addLineListener(soundExplorer);
      line.open(audioFileFormat.getFormat(), 16384);
    } catch (LineUnavailableException e) {
      shutDown(Messages.getString("Unable_to_open_the_line__"), e);
      return;
    }
    

    int frameSizeInBytes = audioFileFormat.getFormat().getFrameSize();
    int bufferLengthInBytes = line.getBufferSize();
    int bufferLengthInFrames = bufferLengthInBytes / frameSizeInBytes;
    byte[] data = new byte[bufferLengthInBytes];
    int numBytesRead = 0;
    

    line.start();
    playing = true;
    

    while (playing) {
      try
      {
        if ((numBytesRead = audioInputStream.read(data)) == 
          -1) {
          break;
        }
        
        int numBytesRemaining = numBytesRead;
        while (numBytesRemaining > 0)
        {

          numBytesRemaining = numBytesRemaining - line.write(data, 0, numBytesRemaining);
        }
      } catch (Exception e) {
        shutDown(Messages.getString("Error_during_playback__"), e);
        break;
      }
    }
    




    if (playing)
      line.drain();
    line.stop();
    line.close();
    line = null;
    shutDown(null, null);
    if (sound.getDEBUG()) {
      System.out.println(Messages.getString("exiting_run_method"));
    }
    


    sound.removePlayback(this);
  }
}

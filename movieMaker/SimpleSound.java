package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javazoom.jl.converter.Converter;


















































public class SimpleSound
{
  public static final int MAX_NEG = -32768;
  public static final int MAX_POS = 32767;
  private static final int SAMPLE_RATE = 22050;
  private static final int NUM_BITS_PER_SAMPLE = 16;
  private static final boolean DEBUG = false;
  private byte[] buffer;
  private AudioFileFormat audioFileFormat = null;
  



  private Vector playbacks = new Vector();
  





  private SoundExplorer soundExplorer = null;
  





  private String fileName = null;
  





  public SimpleSound()
  {
    this(66150);
  }
  




















  public SimpleSound(int numFrames)
  {
    int numChannels = 1;
    int bytesPerSample = 2;
    




    AudioFormat audioFormat = 
      new AudioFormat(22050.0F, 16, 
      numChannels, true, false);
    





    int lengthInFrames = numChannels * numFrames;
    int lengthInBytes = lengthInFrames * bytesPerSample;
    



    audioFileFormat = 
      new AudioFileFormat(AudioFileFormat.Type.WAVE, 
      audioFormat, lengthInFrames);
    

    buffer = new byte[lengthInBytes];
  }
  



  public SimpleSound(double seconds, SimpleSound sound)
  {
    AudioFormat audioFormat = audioFileFormat.getFormat();
    int numChannels = audioFormat.getChannels();
    int bytesPerSample = audioFormat.getSampleSizeInBits() / 8;
    int numFrames = (int)(audioFormat.getFrameRate() * seconds);
    





    int lengthInFrames = numChannels * numFrames;
    int lengthInBytes = lengthInFrames * bytesPerSample;
    



    audioFileFormat = 
      new AudioFileFormat(AudioFileFormat.Type.WAVE, 
      audioFormat, lengthInFrames);
    

    buffer = new byte[lengthInBytes];
  }
  






















  public SimpleSound(int numFrames, int sampleRate)
  {
    int numChannels = 1;
    int bytesPerSample = 2;
    




    AudioFormat audioFormat = 
      new AudioFormat(sampleRate, 16, 
      numChannels, true, false);
    





    int lengthInFrames = numChannels * numFrames;
    int lengthInBytes = lengthInFrames * bytesPerSample;
    



    audioFileFormat = 
      new AudioFileFormat(AudioFileFormat.Type.WAVE, 
      audioFormat, lengthInFrames);
    

    buffer = new byte[lengthInBytes];
  }
  








  public SimpleSound(int sampleSizeInBits, boolean isBigEndian)
  {
    int numBytesInSample = sampleSizeInBits / 8;
    int numberOfChannels = 2;
    boolean signedFlag = true;
    

    AudioFormat audioFormat = 
      new AudioFormat(22050.0F, sampleSizeInBits, 
      numberOfChannels, 
      signedFlag, isBigEndian);
    

    int lengthInBytes = 
      22050 * numberOfChannels * 5 * numBytesInSample;
    

    audioFileFormat = 
      new AudioFileFormat(AudioFileFormat.Type.WAVE, 
      audioFormat, 
      lengthInBytes / (
      numBytesInSample * numberOfChannels));
    

    buffer = new byte[lengthInBytes];
  }
  





  public SimpleSound(String fileName)
  {
    try
    {
      loadFromFile(fileName);
    } catch (Exception ex) {
      printError(Messages.getString("Exception_during_load_of_file_") + fileName);
    }
  }
  





  public SimpleSound(SimpleSound sound)
  {
    audioFileFormat = audioFileFormat;
    fileName = fileName;
    playbacks = new Vector();
    

    if (buffer != null)
    {
      buffer = new byte[buffer.length];
      for (int i = 0; i < buffer.length; i++) {
        buffer[i] = buffer[i];
      }
    }
  }
  






  public byte[] getBuffer()
  {
    return buffer;
  }
  






  public AudioFileFormat getAudioFileFormat()
  {
    return audioFileFormat;
  }
  




  public double getSamplingRate()
  {
    return audioFileFormat.getFormat().getSampleRate();
  }
  




  public SoundExplorer getSoundExplorer()
  {
    return soundExplorer;
  }
  





  public byte[] asArray()
  {
    return getBuffer();
  }
  





  public Vector getPlaybacks()
  {
    return playbacks;
  }
  







  public String getFileName()
  {
    return fileName;
  }
  




  public boolean getDEBUG()
  {
    return false;
  }
  







  public void setBuffer(byte[] newBuffer)
  {
    buffer = newBuffer;
  }
  






  public void setAudioFileFormat(AudioFileFormat newAudioFileFormat)
  {
    audioFileFormat = newAudioFileFormat;
  }
  





  public void setSoundExplorer(SoundExplorer soundExplorer)
  {
    this.soundExplorer = soundExplorer;
  }
  










  public AudioInputStream makeAIS()
  {
    AudioFileFormat.Type fileType = audioFileFormat.getType();
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
    int frameSize = audioFileFormat.getFormat().getFrameSize();
    
    AudioInputStream audioInputStream = 
      new AudioInputStream(bais, audioFileFormat.getFormat(), 
      buffer.length / frameSize);
    return audioInputStream;
  }
  








  public void printError(String message)
  {
    printError(message, null);
  }
  













  public void printError(String message, Exception e)
  {
    if (message != null)
    {
      SimpleOutput.showError(message);
      System.err.println(message);
      if (e != null)
      {
        e.printStackTrace();
      }
    }
  }
  






  public boolean isStereo()
  {
    if (audioFileFormat.getFormat().getChannels() == 1) {
      return false;
    }
    return true;
  }
  





  public void write(String fileName)
  {
    try
    {
      writeToFile(fileName);
    }
    catch (SoundException ex) {
      printError(Messages.getString("Couldn_t_write_file_to_") + fileName);
    }
  }
  
















  public void writeToFile(String outFileName)
    throws SoundException
  {
    AudioInputStream audioInputStream = makeAIS();
    AudioFileFormat.Type type = audioFileFormat.getType();
    try
    {
      audioInputStream.reset();
    }
    catch (Exception e)
    {
      printError(Messages.getString("Unable_to_reset_the_Audio_stream___Please_") + 
        Messages.getString("try_again_"), e);
    }
    


    File file = new File(outFileName);
    if (!file.exists())
    {
      try
      {

        file.createNewFile();
      }
      catch (IOException e)
      {
        printError(Messages.getString("That_file_does_not_already_exist__and") + 
          Messages.getString("there_were_problems_creating_a_new_file") + 
          Messages.getString("of_that_name___Are_you_sure_the_path") + 
          Messages.getString("to__") + outFileName + Messages.getString("exists_"), e);
      }
    }
    


    try
    {
      if (AudioSystem.write(audioInputStream, type, file) == -1)
      {
        printError(Messages.getString("Problems_writing_to_file___Please_") + 
          Messages.getString("try_again_"));


      }
      else
      {

        fileName = outFileName;
      }
    }
    catch (FileNotFoundException e)
    {
      printError(Messages.getString("The_file_you_specified_did_not_already_exist_") + 
        Messages.getString("so_we_tried_to_create_a_new_one__but_were_unable") + 
        Messages.getString("to_do_so___Please_try_again___If_problems_") + 
        Messages.getString("persit_see_your_TA_"), e);
    }
    catch (Exception e)
    {
      printError(Messages.getString("Problems_writing_to_file__") + outFileName, e);
    }
    


    try
    {
      audioInputStream.close();
    }
    catch (Exception e)
    {
      printError(Messages.getString("Unable_to_close_the_Audio_stream_"));
    }
  }
  














  public void loadFromFile(String inFileName)
    throws SoundException
  {
    if (inFileName == null)
    {
      printError(Messages.getString("You_must_pass_in_a_valid_file_name___Please_try") + 
        Messages.getString("again_"));
    }
    


    File file = new File(inFileName);
    if (!file.exists())
    {
      printError(Messages.getString("The_file__") + inFileName + " " + Messages.getString("doesn_t_exist"));
    }
    


    try
    {
      audioInputStream = AudioSystem.getAudioInputStream(file);
    } catch (Exception e) { AudioInputStream audioInputStream;
      printError(Messages.getString("Unable_to_read_from_file_") + 
        inFileName + Messages.getString("___The_file_type_is_unsupported___") + 
        Messages.getString("Are_you_sure_you_re_using_a_WAV__AU__or_") + 
        Messages.getString("AIFF_file__some__wav_files_are_encoded_") + 
        Messages.getString("using_gsm__sbc__mp3__celp__ulaw__or_adpcm__"), e); return;
    }
    





    AudioInputStream audioInputStream;
    




    if (audioInputStream.getFrameLength() * audioInputStream.getFormat().getFrameSize() > 2147483647L)
    {
      printError(Messages.getString("The_sound_in_file__") + inFileName + " " + 
        Messages.getString("is_too_long_") + "  " + 
        Messages.getString("Try_using_a_shorter_sound_"));
    }
    
    int bufferSize = (int)audioInputStream.getFrameLength() * 
      audioInputStream.getFormat().getFrameSize();
    
    buffer = new byte[bufferSize];
    
    int numBytesRead = 0;
    int offset = 0;
    
    try
    {
      for (;;)
      {
        numBytesRead = 
          audioInputStream.read(buffer, offset, bufferSize);
        if (numBytesRead == -1) {
          break;
        }
        offset += numBytesRead;
      }
    } catch (Exception e) { printError(Messages.getString("Problems_reading_the_input_stream___") + 
        Messages.getString("You_might_want_to_try_again_using_this_") + " " + 
        Messages.getString("file__") + inFileName + Messages.getString("or_a_different") + " " + 
        Messages.getString("file___If_problems_persist__ask_your_TA_"), 
        e);
    }
    




    if (inFileName.toLowerCase().endsWith(".wav"))
    {
      audioFileFormat = 
        new AudioFileFormat(AudioFileFormat.Type.WAVE, 
        audioInputStream.getFormat(), 
        (int)audioInputStream.getFrameLength());
    }
    else if (inFileName.toLowerCase().endsWith(".au"))
    {
      audioFileFormat = 
        new AudioFileFormat(AudioFileFormat.Type.AU, 
        audioInputStream.getFormat(), 
        (int)audioInputStream.getFrameLength());
    }
    else if ((inFileName.toLowerCase().endsWith(".aif")) || 
      (inFileName.toLowerCase().endsWith(".aiff")))
    {
      audioFileFormat = 
        new AudioFileFormat(AudioFileFormat.Type.AIFF, 
        audioInputStream.getFormat(), 
        (int)audioInputStream.getFrameLength());
    }
    else
    {
      printError(Messages.getString("Unsupported_file_type___Please_try_again_with_a_") + 
        Messages.getString("file_that_ends_in__wav___au___aif__or__aiff"));
    }
    









    fileName = inFileName;
    try {
      audioInputStream.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  




















  public void play()
  {
    Playback playback = new Playback(this);
    playbacks.add(playback);
    playback.start();
  }
  











  public void blockingPlayOld()
  {
    Playback playback = new Playback(this);
    playbacks.add(playback);
    playback.start();
    
    while (playback.isAlive()) {}
  }
  




  public void blockingPlay()
  {
    play();
    try {
      double timeToSleep = 
        1000.0D * (
        getLength() / getSamplingRate());
      Thread.sleep((int)timeToSleep);
    } catch (Exception ex) {
      System.out.println(Messages.getString("Exception_occurred__") + ex);
    }
  }
  
















  public void playAtRateDur(double rate, double durInFrames)
    throws SoundException
  {
    if (durInFrames > getLengthInFrames())
    {
      printError(Messages.getString("The_given_duration_in_frames__") + durInFrames + " " + 
        Messages.getString("is_out_of_the_playable_range___Try_something_") + 
        Messages.getString("between_1_and_") + getLengthInFrames());
    }
    if (rate > 3.4028234663852886E38D)
    {
      printError(Messages.getString("The_new_sample_rate__") + rate + Messages.getString("is_out_of_the_") + 
        Messages.getString("playable_range___Try_something_between_") + 
        Messages.getString("0_and_") + Float.MAX_VALUE);
    }
    playAtRateInRange((float)rate, 0, (int)durInFrames - 1, false);
  }
  
















  public void blockingPlayAtRateDur(double rate, double durInFrames)
    throws SoundException
  {
    if (durInFrames > getLengthInFrames())
    {
      printError(Messages.getString("The_given_duration_in_frames__") + durInFrames + " " + 
        Messages.getString("is_out_of_the_playable_range___Try_something_") + 
        Messages.getString("between_1_and_") + getLengthInFrames());
    }
    if (rate > 3.4028234663852886E38D)
    {
      printError(Messages.getString("The_new_sample_rate__") + rate + Messages.getString("is_out_of_the_") + 
        Messages.getString("playable_range___Try_something_between_") + 
        Messages.getString("0_and_") + Float.MAX_VALUE);
    }
    
    playAtRateInRange((float)rate, 0, (int)durInFrames - 1, true);
  }
  


















  public void playAtRateInRange(float rate, int startFrame, int endFrame)
    throws SoundException
  {
    playAtRateInRange(rate, startFrame, endFrame, false);
  }
  

















  public void blockingPlayAtRateInRange(float rate, int startFrame, int endFrame)
    throws SoundException
  {
    playAtRateInRange(rate, startFrame, endFrame, true);
  }
  





























  public void playAtRateInRange(float rate, int startFrame, int endFrame, boolean isBlocking)
    throws SoundException
  {
    if (endFrame >= getAudioFileFormat().getFrameLength())
    {
      printError(Messages.getString("You_are_trying_to_play_to_index__") + (endFrame + 1) + 
        Messages.getString("___The_sound_only_has_") + 
        getAudioFileFormat().getFrameLength() + " " + 
        Messages.getString("samples_total_"));
    }
    if (startFrame < 0)
    {
      printError(Messages.getString("You_cannot_start_playing_at_index_") + (
        startFrame + 1) + 
        Messages.getString("___Choose_1_to_start_at_the_begining_"));
    }
    if (endFrame < startFrame)
    {
      printError(Messages.getString("You_cannot_start_playing_at_index_") + (
        startFrame + 1) + " " + Messages.getString("and_stop_playing_at_index_") + (
        endFrame + 1) + Messages.getString("___The_start_index_must_be_before") + 
        Messages.getString("the_stop_index_"));
    }
    





    byte[] oldBuffer = buffer;
    AudioFileFormat oldAFF = getAudioFileFormat();
    

    int frameSize = getAudioFileFormat().getFormat().getFrameSize();
    int durInFrames = endFrame - startFrame + 1;
    



    int newBufferSize = durInFrames * frameSize;
    
    byte[] newBuffer = new byte[newBufferSize];
    for (int i = 0; i < newBufferSize; i++)
    {
      newBuffer[i] = oldBuffer[(startFrame * frameSize + i)];
    }
    


    AudioFormat newAF = 
      new AudioFormat(oldAFF.getFormat().getEncoding(), 
      oldAFF.getFormat().getSampleRate() * rate, 
      oldAFF.getFormat().getSampleSizeInBits(), 
      oldAFF.getFormat().getChannels(), 
      oldAFF.getFormat().getFrameSize(), 
      oldAFF.getFormat().getFrameRate() * rate, 
      oldAFF.getFormat().isBigEndian());
    


    AudioFileFormat newAFF = 
      new AudioFileFormat(oldAFF.getType(), newAF, durInFrames);
    




    setBuffer(newBuffer);
    setAudioFileFormat(newAFF);
    











    Playback playback = new Playback(this);
    playbacks.add(playback);
    playback.start();
    









    while (!playback.getPlaying()) {}
    
    setBuffer(oldBuffer);
    setAudioFileFormat(oldAFF);
  }
  







  public void removePlayback(Playback playbackToRemove)
  {
    if (playbacks.contains(playbackToRemove))
    {
      playbacks.remove(playbackToRemove);
      playbackToRemove = null;
    }
  }
  













  public byte[] getFrame(int frameNum)
    throws SoundException
  {
    if (frameNum >= getAudioFileFormat().getFrameLength())
    {
      printError(Messages.getString("That_index_") + frameNum + Messages.getString("__does_not_exist__") + 
        Messages.getString("The_last_valid_index_is_") + (
        getAudioFileFormat().getFrameLength() - 1));
    }
    
    int frameSize = getAudioFileFormat().getFormat().getFrameSize();
    byte[] theFrame = new byte[frameSize];
    for (int i = 0; i < frameSize; i++)
    {
      theFrame[i] = buffer[(frameNum * frameSize + i)];
    }
    return theFrame;
  }
  







  public int getLengthInFrames()
  {
    return getAudioFileFormat().getFrameLength();
  }
  




  public int getNumSamples()
  {
    return getAudioFileFormat().getFrameLength();
  }
  





  public SoundSample getSample(int frameNum)
  {
    return new SoundSample(this, frameNum);
  }
  




  public SoundSample[] getSamples()
  {
    int numSamples = getLengthInFrames();
    SoundSample[] samples = new SoundSample[numSamples];
    for (int i = 0; i < numSamples; i++)
      samples[i] = new SoundSample(this, i);
    return samples;
  }
  



  private void reportIndexException(int index, Exception ex)
  {
    System.out.println(Messages.getString("The_index_") + index + " " + 
      Messages.getString("isn_t_valid_for_this_sound"));
  }
  






  public int getSampleValueAt(int index)
  {
    int value = 0;
    try
    {
      value = getSampleValue(index);
    } catch (Exception ex) {
      reportIndexException(index, ex);
    }
    return value;
  }
  










  public int getSampleValue(int frameNum)
    throws SoundException
  {
    if (frameNum >= getAudioFileFormat().getFrameLength())
    {
      printError(Messages.getString("You_are_trying_to_access_the_sample_at_index__") + 
        frameNum + Messages.getString("__but_the_last_valid_index_is_at_") + (
        getAudioFileFormat().getFrameLength() - 1));
    }
    else if (frameNum < 0)
    {
      printError(Messages.getString("You_asked_for_the_sample_at_index__") + frameNum + 
        Messages.getString("___This_number_is_less_than_zero___Please_try") + 
        Messages.getString("again_using_an_index_in_the_range__0_") + (
        getAudioFileFormat().getFrameLength() - 1) + "]");
    }
    
    AudioFormat format = getAudioFileFormat().getFormat();
    int sampleSizeInBits = format.getSampleSizeInBits();
    boolean isBigEndian = format.isBigEndian();
    
    byte[] theFrame = getFrame(frameNum);
    
    if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
    {



      if (sampleSizeInBits == 8)
        return theFrame[0];
      if (sampleSizeInBits == 16)
        return TConversionTool.bytesToInt16(theFrame, 0, 
          isBigEndian);
      if (sampleSizeInBits == 24)
        return TConversionTool.bytesToInt24(theFrame, 0, 
          isBigEndian);
      if (sampleSizeInBits == 32) {
        return TConversionTool.bytesToInt32(theFrame, 0, 
          isBigEndian);
      }
      
      printError(Messages.getString("Unsupported_audio_encoding___The_sample_") + 
        Messages.getString("size_is_not_recognized_as_a_standard_") + 
        Messages.getString("format_"));
      return -1;
    }
    
    if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
    {
      if (sampleSizeInBits == 8)
        return TConversionTool.unsignedByteToInt(theFrame[0]) - 
          (int)Math.pow(2.0D, 7.0D);
      if (sampleSizeInBits == 16)
        return TConversionTool.unsignedByteToInt16(theFrame, 0, 
          isBigEndian) - 
          (int)Math.pow(2.0D, 15.0D);
      if (sampleSizeInBits == 24)
        return TConversionTool.unsignedByteToInt24(theFrame, 0, 
          isBigEndian) - 
          (int)Math.pow(2.0D, 23.0D);
      if (sampleSizeInBits == 32) {
        return TConversionTool.unsignedByteToInt32(theFrame, 0, 
          isBigEndian) - 
          (int)Math.pow(2.0D, 31.0D);
      }
      
      printError(Messages.getString("Unsupported_audio_encoding___The_sample_") + 
        Messages.getString("size_is_not_recognized_as_a_standard_") + 
        Messages.getString("format_"));
      return -1;
    }
    
    if (format.getEncoding().equals(AudioFormat.Encoding.ALAW))
    {
      return TConversionTool.alaw2linear(buffer[0]);
    }
    if (format.getEncoding().equals(AudioFormat.Encoding.ULAW))
    {
      return TConversionTool.ulaw2linear(buffer[0]);
    }
    

    printError(Messages.getString("unsupported_audio_encoding__") + 
      format.getEncoding() + Messages.getString("___Currently_only_PCM__") + 
      Messages.getString("ALAW_and_ULAW_are_supported___Please_try_again") + 
      Messages.getString("with_a_different_file_"));
    return -1;
  }
  











  public int getLeftSample(int frameNum)
    throws SoundException
  {
    return getSampleValue(frameNum);
  }
  












  public int getRightSample(int frameNum)
    throws SoundException
  {
    if (frameNum >= getAudioFileFormat().getFrameLength())
    {
      printError(Messages.getString("You_are_trying_to_access_the_sample_at_index__") + 
        frameNum + Messages.getString("__but_the_last_valid_index_is_at_") + (
        getAudioFileFormat().getFrameLength() - 1));
    }
    else if (frameNum < 0)
    {
      printError(Messages.getString("You_asked_for_the_sample_at_index__") + (frameNum + 1) + 
        Messages.getString("___This_number_is_less_than_zero___Please_try") + " " + 
        Messages.getString("again_using_an_index_in_the_range__0_") + (
        getAudioFileFormat().getFrameLength() - 1) + "].");
    }
    
    AudioFormat format = getAudioFileFormat().getFormat();
    int channels;
    if ((channels = format.getChannels()) == 1)
    {
      printError(Messages.getString("Only_stereo_sounds_have_different_right_and_left") + " " + 
        Messages.getString("samples___You_are_using_a_mono_sound__try_") + 
        "getSample(" + frameNum + Messages.getString("__instead"));
      return -1;
    }
    int sampleSizeInBits = format.getSampleSizeInBits();
    boolean isBigEndian = format.isBigEndian();
    
    byte[] theFrame = getFrame(frameNum);
    
    if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
    {
      if (sampleSizeInBits == 8)
        return theFrame[1];
      if (sampleSizeInBits == 16)
        return TConversionTool.bytesToInt16(theFrame, 2, isBigEndian);
      if (sampleSizeInBits == 24)
        return TConversionTool.bytesToInt24(theFrame, 3, isBigEndian);
      if (sampleSizeInBits == 32) {
        return TConversionTool.bytesToInt32(theFrame, 4, isBigEndian);
      }
      
      printError(Messages.getString("Unsupported_audio_encoding___The_sample") + " " + 
        Messages.getString("size_is_not_recognized_as_a_standard") + " " + 
        Messages.getString("format_"));
      return -1;
    }
    
    if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
    {
      if (sampleSizeInBits == 8)
        return TConversionTool.unsignedByteToInt(theFrame[1]);
      if (sampleSizeInBits == 16)
        return TConversionTool.unsignedByteToInt16(theFrame, 2, isBigEndian);
      if (sampleSizeInBits == 24)
        return TConversionTool.unsignedByteToInt24(theFrame, 3, isBigEndian);
      if (sampleSizeInBits == 32) {
        return TConversionTool.unsignedByteToInt32(theFrame, 4, isBigEndian);
      }
      
      printError(Messages.getString("Unsupported_audio_encoding___The_sample") + " " + 
        Messages.getString("size_is_not_recognized_as_a_standard") + " " + 
        Messages.getString("format_"));
      return -1;
    }
    
    if (format.getEncoding().equals(AudioFormat.Encoding.ALAW))
    {
      return TConversionTool.alaw2linear(buffer[1]);
    }
    if (format.getEncoding().equals(AudioFormat.Encoding.ULAW))
    {
      return TConversionTool.ulaw2linear(buffer[1]);
    }
    

    printError(Messages.getString("unsupported_audio_encoding__") + 
      format.getEncoding() + Messages.getString("___Currently_only_PCM__") + 
      Messages.getString("ALAW_and_ULAW_are_supported___Please_try_again") + 
      Messages.getString("with_a_different_file_"));
    return -1;
  }
  









  public int getLengthInBytes()
  {
    return buffer.length;
  }
  




  public int getLength()
  {
    return getNumSamples();
  }
  







  public int getChannels()
  {
    return getAudioFileFormat().getFormat().getChannels();
  }
  












  public void setFrame(int frameNum, byte[] theFrame)
    throws SoundException
  {
    if (frameNum >= getAudioFileFormat().getFrameLength())
    {
      printError(Messages.getString("That_frame__number_") + frameNum + Messages.getString("__does_not_exist__") + 
        Messages.getString("The_last_valid_frame_number_is_") + (
        getAudioFileFormat().getFrameLength() - 1));
    }
    int frameSize = getAudioFileFormat().getFormat().getFrameSize();
    if (frameSize != theFrame.length)
      printError(Messages.getString("Frame_size_doesn_t_match__line_383___This_should") + " " + 
        Messages.getString("never_happen___Please_report_the_problem_to_a_TA_"));
    for (int i = 0; i < frameSize; i++)
    {
      buffer[(frameNum * frameSize + i)] = theFrame[i];
    }
  }
  




  public void setSampleValueAt(int index, int value)
  {
    try
    {
      setSampleValue(index, value);
    } catch (Exception ex) {
      reportIndexException(index, ex);
    }
  }
  










  public void setSampleValue(int frameNum, int sample)
    throws SoundException
  {
    AudioFormat format = getAudioFileFormat().getFormat();
    int sampleSizeInBits = format.getSampleSizeInBits();
    boolean isBigEndian = format.isBigEndian();
    
    byte[] theFrame = getFrame(frameNum);
    
    if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
    {
      if (sampleSizeInBits == 8)
      {
        theFrame[0] = ((byte)sample);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 16)
      {
        TConversionTool.intToBytes16(sample, theFrame, 0, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 24)
      {
        TConversionTool.intToBytes24(sample, theFrame, 0, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 32)
      {
        TConversionTool.intToBytes32(sample, theFrame, 0, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else
      {
        printError(Messages.getString("Unsupported_audio_encoding___The_sample") + 
          Messages.getString("size_is_not_recognized_as_a_standard_format"));
      }
    }
    else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
    {
      if (sampleSizeInBits == 8)
      {
        theFrame[0] = TConversionTool.intToUnsignedByte(sample);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 16)
      {
        TConversionTool.intToUnsignedBytes16(sample, theFrame, 0, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 24)
      {
        TConversionTool.intToUnsignedBytes24(sample, theFrame, 0, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 32)
      {
        TConversionTool.intToUnsignedBytes32(sample, theFrame, 0, isBigEndian);
        setFrame(frameNum, theFrame);

      }
      else
      {
        printError(Messages.getString("Unsupported_audio_encoding___The_sample") + " " + 
          Messages.getString("size_is_not_recognized_as_a_standard_") + 
          Messages.getString("format_"));
      }
    }
    else if (format.getEncoding().equals(AudioFormat.Encoding.ALAW))
    {
      if ((sample > 32767) || (sample < 32768))
        printError(Messages.getString("You_are_trying_to_set_the_sample_value_to__") + 
          sample + Messages.getString("__but_the_maximum_value_for_a_sample") + " " + 
          Messages.getString("in_this_format_is__") + 32767 + 
          Messages.getString("__and_the_minimum_value_is__") + 32768 + 
          Messages.getString("___Please_choose_a_value_in_that_range_"));
      theFrame[0] = TConversionTool.linear2alaw((short)sample);
      setFrame(frameNum, theFrame);
    }
    else if (format.getEncoding().equals(AudioFormat.Encoding.ULAW))
    {

      if ((sample > 32767) || (sample < 32768))
        printError(Messages.getString("You_are_trying_to_set_the_sample_value_to__") + 
          sample + Messages.getString("__but_the_maximum_value_for_a_sample") + " " + 
          Messages.getString("in_this_format_is__") + 32767 + 
          Messages.getString("__and_the_minimum_value_is__") + 32768 + 
          Messages.getString("___Please_choose_a_value_in_that_range_"));
      theFrame[0] = TConversionTool.linear2ulaw((short)sample);
      setFrame(frameNum, theFrame);
    }
    else
    {
      printError(Messages.getString("unsupported_audio_encoding__") + 
        format.getEncoding() + Messages.getString("___Currently_only_PCM__") + 
        Messages.getString("ALAW_and_ULAW_are_supported___Please_try_again") + 
        Messages.getString("with_a_different_file_"));
    }
  }
  
  public void setLeftSample(int frameNum, int sample) throws SoundException
  {
    setSampleValue(frameNum, sample);
  }
  
  public void setRightSample(int frameNum, int sample)
    throws SoundException
  {
    AudioFormat format = getAudioFileFormat().getFormat();
    int sampleSizeInBits = format.getSampleSizeInBits();
    boolean isBigEndian = format.isBigEndian();
    
    if (format.getChannels() == 1) {
      printError(Messages.getString("this_is_a_mono_sound___only_stereo_sounds_have") + " " + 
        Messages.getString("different_left_and_right_samples_"));
    }
    byte[] theFrame = getFrame(frameNum);
    
    if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
    {

      if (sampleSizeInBits == 8)
      {
        theFrame[1] = ((byte)sample);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 16)
      {
        TConversionTool.intToBytes16(sample, theFrame, 2, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 24)
      {
        TConversionTool.intToBytes24(sample, theFrame, 3, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 32)
      {
        TConversionTool.intToBytes32(sample, theFrame, 4, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else
      {
        printError(Messages.getString("Unsupported_audio_encoding___The_sample") + 
          Messages.getString("size_is_not_recognized_as_a_standard_format"));
      }
    }
    else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
    {
      if (sampleSizeInBits == 8)
      {
        theFrame[1] = TConversionTool.intToUnsignedByte(sample);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 16)
      {
        TConversionTool.intToUnsignedBytes16(sample, theFrame, 2, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 24)
      {
        TConversionTool.intToUnsignedBytes24(sample, theFrame, 3, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else if (sampleSizeInBits == 32)
      {
        TConversionTool.intToUnsignedBytes32(sample, theFrame, 4, isBigEndian);
        setFrame(frameNum, theFrame);
      }
      else
      {
        printError(Messages.getString("Unsupported_audio_encoding___The_sample") + " " + 
          Messages.getString("size_is_not_recognized_as_a_standard") + " " + 
          Messages.getString("format"));
      }
    }
    else if (format.getEncoding().equals(AudioFormat.Encoding.ALAW))
    {
      if ((sample > 32767) || (sample < 32768))
        printError(Messages.getString("You_are_trying_to_set_the_sample_value_to__") + 
          sample + Messages.getString("__but_the_maximum_value_for_a_sample") + " " + 
          Messages.getString("in_this_format_is__") + 32767 + 
          Messages.getString("__and_the_minimum_value_is__") + 32768 + 
          Messages.getString("___Please_choose_a_value_in_that_range_"));
      theFrame[1] = TConversionTool.linear2alaw((short)sample);
      setFrame(frameNum, theFrame);
    }
    else if (format.getEncoding().equals(AudioFormat.Encoding.ULAW))
    {
      if ((sample > 32767) || (sample < 32768))
        printError(Messages.getString("You_are_trying_to_set_the_sample_value_to__") + 
          sample + Messages.getString("__but_the_maximum_value_for_a_sample") + " " + 
          Messages.getString("in_this_format_is__") + 32767 + 
          Messages.getString("__and_the_minimum_value_is__") + 32768 + 
          Messages.getString("___Please_choose_a_value_in_that_range_"));
      theFrame[1] = TConversionTool.linear2ulaw((short)sample);
      setFrame(frameNum, theFrame);
    }
    else
    {
      printError(Messages.getString("unsupported_audio_encoding__") + 
        format.getEncoding() + Messages.getString("___Currently_only_PCM__") + 
        Messages.getString("ALAW_and_ULAW_are_supported___Please_try_again") + 
        Messages.getString("with_a_different_file_"));
    }
  }
  



  public void explore()
  {
    SimpleSound sound = new SimpleSound(this);
    new SoundExplorer(sound, isStereo());
  }
  






  public static void playNote(int key, int duration, int intensity) {}
  






  public static void convert(String mp3File, String wavFile)
  {
    try
    {
      Converter converter = new Converter();
      converter.convert(mp3File, wavFile);
    } catch (Exception ex) {
      SimpleOutput.showError(Messages.getString("Couldn_t_covert_the_file_") + mp3File);
    }
  }
  






  public String toString()
  {
    String output = Messages.getString("SimpleSound");
    

    if (fileName != null) {
      output = output + " " + Messages.getString("file__") + fileName;
    }
    
    output = output + " " + Messages.getString("length__") + getLengthInBytes();
    
    return output;
  }
}

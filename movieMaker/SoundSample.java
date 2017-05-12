package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;











public class SoundSample
{
  private SimpleSound sound = null;
  

  private int frameNumber = 0;
  







  public SoundSample(SimpleSound sound, int frameNumber)
  {
    this.sound = sound;
    this.frameNumber = frameNumber;
  }
  






  public int getValue()
  {
    int value = 0;
    try {
      value = sound.getSampleValue(frameNumber);
    }
    catch (SoundException localSoundException) {}
    return value;
  }
  




  public void setValue(int value)
  {
    try
    {
      sound.setSampleValue(frameNumber, value);
    }
    catch (SoundException localSoundException) {}
  }
  






  public String toString()
  {
    return Messages.getString("Sample_at_index_") + frameNumber + " " + Messages.getString("has_value_") + getValue();
  }
}

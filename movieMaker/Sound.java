package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
















public class Sound
  extends SimpleSound
{
  public Sound(String fileName)
  {
    super(fileName);
  }
  






  public Sound(int numSamples)
  {
    super(numSamples);
  }
  







  public Sound(int numSamples, int sampleRate)
  {
    super(numSamples, sampleRate);
  }
  




  public Sound(Sound copySound)
  {
    super(copySound);
  }
  







  public String toString()
  {
    String output = "Sound";
    String fileName = getFileName();
    

    if (fileName != null) {
      output = output + " " + Messages.getString("file__") + fileName;
    }
    
    output = output + " " + Messages.getString("number_of_samples__") + getLengthInFrames();
    
    return output;
  }
}

package edu.cmu.cs.stage3.image.codec;

import java.io.Serializable;

public class PNGSuggestedPaletteEntry
  implements Serializable
{
  public String name;
  public int sampleDepth;
  public int red;
  public int green;
  public int blue;
  public int alpha;
  public int frequency;
  
  public PNGSuggestedPaletteEntry() {}
}

package edu.cmu.cs.stage3.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;





















public class EStreamTokenizer
  extends StreamTokenizer
{
  private String next_sval;
  private double next_nval;
  private int next_ttype;
  private boolean hold_back;
  
  public EStreamTokenizer(BufferedReader br)
  {
    super(br);
    hold_back = false;
  }
  
  public int nextToken() throws IOException
  {
    if (hold_back) {
      ttype = next_ttype;
      sval = next_sval;
      nval = next_nval;
      hold_back = false;
    } else {
      super.nextToken();
    }
    if (ttype == -2) {
      double f = nval;
      super.nextToken();
      if ((ttype == -3) && 
        (sval.startsWith("e"))) {
        int exponent = Integer.parseInt(sval.substring(1));
        ttype = -2;
        nval = (f * Math.pow(10.0D, exponent));
        return ttype;
      }
      
      hold_back = true;
      next_sval = sval;
      next_nval = nval;
      next_ttype = ttype;
      nval = f;
      ttype = -2;
    }
    return ttype;
  }
}

package edu.cmu.cs.stage3.image.codec;

import java.awt.image.DataBuffer;


























































public class DataBufferFloat
  extends DataBuffer
{
  protected float[][] bankdata;
  protected float[] data;
  
  public DataBufferFloat(int size)
  {
    super(4, size);
    data = new float[size];
    bankdata = new float[1][];
    bankdata[0] = data;
  }
  









  public DataBufferFloat(int size, int numBanks)
  {
    super(4, size, numBanks);
    bankdata = new float[numBanks][];
    for (int i = 0; i < numBanks; i++) {
      bankdata[i] = new float[size];
    }
    data = bankdata[0];
  }
  










  public DataBufferFloat(float[] dataArray, int size)
  {
    super(4, size);
    data = dataArray;
    bankdata = new float[1][];
    bankdata[0] = data;
  }
  













  public DataBufferFloat(float[] dataArray, int size, int offset)
  {
    super(4, size, 1, offset);
    data = dataArray;
    bankdata = new float[1][];
    bankdata[0] = data;
  }
  










  public DataBufferFloat(float[][] dataArray, int size)
  {
    super(4, size, dataArray.length);
    bankdata = dataArray;
    data = bankdata[0];
  }
  












  public DataBufferFloat(float[][] dataArray, int size, int[] offsets)
  {
    super(4, size, dataArray.length, offsets);
    bankdata = dataArray;
    data = bankdata[0];
  }
  
  public float[] getData()
  {
    return data;
  }
  
  public float[] getData(int bank)
  {
    return bankdata[bank];
  }
  
  public float[][] getBankData()
  {
    return bankdata;
  }
  








  public int getElem(int i)
  {
    return (int)data[(i + offset)];
  }
  









  public int getElem(int bank, int i)
  {
    return (int)bankdata[bank][(i + offsets[bank])];
  }
  







  public void setElem(int i, int val)
  {
    data[(i + offset)] = val;
  }
  








  public void setElem(int bank, int i, int val)
  {
    bankdata[bank][(i + offsets[bank])] = val;
  }
  








  public float getElemFloat(int i)
  {
    return data[(i + offset)];
  }
  









  public float getElemFloat(int bank, int i)
  {
    return bankdata[bank][(i + offsets[bank])];
  }
  







  public void setElemFloat(int i, float val)
  {
    data[(i + offset)] = val;
  }
  








  public void setElemFloat(int bank, int i, float val)
  {
    bankdata[bank][(i + offsets[bank])] = val;
  }
  








  public double getElemDouble(int i)
  {
    return data[(i + offset)];
  }
  









  public double getElemDouble(int bank, int i)
  {
    return bankdata[bank][(i + offsets[bank])];
  }
  







  public void setElemDouble(int i, double val)
  {
    data[(i + offset)] = ((float)val);
  }
  








  public void setElemDouble(int bank, int i, double val)
  {
    bankdata[bank][(i + offsets[bank])] = ((float)val);
  }
}

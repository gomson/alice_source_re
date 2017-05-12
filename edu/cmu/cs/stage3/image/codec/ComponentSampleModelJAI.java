package edu.cmu.cs.stage3.image.codec;

import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;





















































































public class ComponentSampleModelJAI
  extends ComponentSampleModel
{
  public ComponentSampleModelJAI(int dataType, int w, int h, int pixelStride, int scanlineStride, int[] bandOffsets)
  {
    super(dataType, w, h, pixelStride, scanlineStride, bandOffsets);
  }
  
























  public ComponentSampleModelJAI(int dataType, int w, int h, int pixelStride, int scanlineStride, int[] bankIndices, int[] bandOffsets)
  {
    super(dataType, w, h, pixelStride, scanlineStride, bankIndices, bandOffsets);
  }
  



  private long getBufferSize()
  {
    int maxBandOff = bandOffsets[0];
    for (int i = 1; i < bandOffsets.length; i++) {
      maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
    }
    long size = 0L;
    if (maxBandOff >= 0)
      size += maxBandOff + 1;
    if (pixelStride > 0)
      size += pixelStride * (width - 1);
    if (scanlineStride > 0)
      size += scanlineStride * (height - 1);
    return size;
  }
  


  private int[] JAIorderBands(int[] orig, int step)
  {
    int[] map = new int[orig.length];
    int[] ret = new int[orig.length];
    
    for (int i = 0; i < map.length; i++) { map[i] = i;
    }
    for (int i = 0; i < ret.length; i++) {
      int index = i;
      for (int j = i + 1; j < ret.length; j++) {
        if (orig[map[index]] > orig[map[j]]) {
          index = j;
        }
      }
      ret[map[index]] = (i * step);
      map[index] = map[i];
    }
    return ret;
  }
  









  public SampleModel createCompatibleSampleModel(int w, int h)
  {
    SampleModel ret = null;
    
    int minBandOff = bandOffsets[0];
    int maxBandOff = bandOffsets[0];
    for (int i = 1; i < bandOffsets.length; i++) {
      minBandOff = Math.min(minBandOff, bandOffsets[i]);
      maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
    }
    maxBandOff -= minBandOff;
    
    int bands = bandOffsets.length;
    
    int pStride = Math.abs(pixelStride);
    int lStride = Math.abs(scanlineStride);
    int bStride = Math.abs(maxBandOff);
    int[] bandOff;
    int[] bandOff; if (pStride > lStride) {
      if (pStride > bStride) {
        if (lStride > bStride) {
          int[] bandOff = new int[bandOffsets.length];
          for (int i = 0; i < bands; i++)
            bandOff[i] = (bandOffsets[i] - minBandOff);
          lStride = bStride + 1;
          pStride = lStride * h;
        } else {
          int[] bandOff = JAIorderBands(bandOffsets, lStride * h);
          pStride = bands * lStride * h;
        }
      } else {
        pStride = lStride * h;
        bandOff = JAIorderBands(bandOffsets, pStride * w);
      }
    }
    else if (pStride > bStride) {
      int[] bandOff = new int[bandOffsets.length];
      for (int i = 0; i < bands; i++)
        bandOff[i] = (bandOffsets[i] - minBandOff);
      pStride = bStride + 1;
      lStride = pStride * w;
    }
    else if (lStride > bStride) {
      int[] bandOff = JAIorderBands(bandOffsets, pStride * w);
      lStride = bands * pStride * w;
    } else {
      lStride = pStride * w;
      bandOff = JAIorderBands(bandOffsets, lStride * h);
    }
    



    int base = 0;
    if (scanlineStride < 0) {
      base += lStride * h;
      lStride *= -1;
    }
    if (pixelStride < 0) {
      base += pStride * w;
      pStride *= -1;
    }
    
    for (int i = 0; i < bands; i++)
      bandOff[i] += base;
    return new ComponentSampleModelJAI(dataType, w, h, pStride, 
      lStride, bankIndices, bandOff);
  }
  










  public SampleModel createSubsetSampleModel(int[] bands)
  {
    int[] newBankIndices = new int[bands.length];
    int[] newBandOffsets = new int[bands.length];
    for (int i = 0; i < bands.length; i++) {
      int b = bands[i];
      newBankIndices[i] = bankIndices[b];
      newBandOffsets[i] = bandOffsets[b];
    }
    return new ComponentSampleModelJAI(dataType, width, height, 
      pixelStride, 
      scanlineStride, 
      newBankIndices, 
      newBandOffsets);
  }
  





  public DataBuffer createDataBuffer()
  {
    DataBuffer dataBuffer = null;
    
    int size = (int)getBufferSize();
    switch (dataType) {
    case 0: 
      dataBuffer = new DataBufferByte(size, numBanks);
      break;
    case 1: 
      dataBuffer = new DataBufferUShort(size, numBanks);
      break;
    case 3: 
      dataBuffer = new DataBufferInt(size, numBanks);
      break;
    case 2: 
      dataBuffer = new DataBufferShort(size, numBanks);
      break;
    case 4: 
      dataBuffer = new DataBufferFloat(size, numBanks);
      break;
    case 5: 
      dataBuffer = new DataBufferDouble(size, numBanks);
    }
    
    
    return dataBuffer;
  }
  







































  public Object getDataElements(int x, int y, Object obj, DataBuffer data)
  {
    int type = getTransferType();
    int numDataElems = getNumDataElements();
    int pixelOffset = y * scanlineStride + x * pixelStride;
    
    switch (type)
    {
    case 0: 
      byte[] bdata;
      
      byte[] bdata;
      if (obj == null) {
        bdata = new byte[numDataElems];
      } else {
        bdata = (byte[])obj;
      }
      for (int i = 0; i < numDataElems; i++) {
        bdata[i] = ((byte)data.getElem(bankIndices[i], 
          pixelOffset + bandOffsets[i]));
      }
      
      obj = bdata;
      break;
    case 1: 
      short[] usdata;
      
      short[] usdata;
      
      if (obj == null) {
        usdata = new short[numDataElems];
      } else {
        usdata = (short[])obj;
      }
      for (int i = 0; i < numDataElems; i++) {
        usdata[i] = ((short)data.getElem(bankIndices[i], 
          pixelOffset + bandOffsets[i]));
      }
      
      obj = usdata;
      break;
    case 3: 
      int[] idata;
      
      int[] idata;
      
      if (obj == null) {
        idata = new int[numDataElems];
      } else {
        idata = (int[])obj;
      }
      for (int i = 0; i < numDataElems; i++) {
        idata[i] = data.getElem(bankIndices[i], 
          pixelOffset + bandOffsets[i]);
      }
      
      obj = idata;
      break;
    case 2: 
      short[] sdata;
      
      short[] sdata;
      
      if (obj == null) {
        sdata = new short[numDataElems];
      } else {
        sdata = (short[])obj;
      }
      for (int i = 0; i < numDataElems; i++) {
        sdata[i] = ((short)data.getElem(bankIndices[i], 
          pixelOffset + bandOffsets[i]));
      }
      
      obj = sdata;
      break;
    case 4: 
      float[] fdata;
      
      float[] fdata;
      
      if (obj == null) {
        fdata = new float[numDataElems];
      } else {
        fdata = (float[])obj;
      }
      for (int i = 0; i < numDataElems; i++) {
        fdata[i] = data.getElemFloat(bankIndices[i], 
          pixelOffset + bandOffsets[i]);
      }
      
      obj = fdata;
      break;
    case 5: 
      double[] ddata;
      
      double[] ddata;
      
      if (obj == null) {
        ddata = new double[numDataElems];
      } else {
        ddata = (double[])obj;
      }
      for (int i = 0; i < numDataElems; i++) {
        ddata[i] = data.getElemDouble(bankIndices[i], 
          pixelOffset + bandOffsets[i]);
      }
      
      obj = ddata;
    }
    
    


    return obj;
  }
  















































  public Object getDataElements(int x, int y, int w, int h, Object obj, DataBuffer data)
  {
    int type = getTransferType();
    int numDataElems = getNumDataElements();
    int cnt = 0;
    Object o = null;
    
    switch (type)
    {
    case 0: 
      byte[] bdata;
      byte[] bdata;
      if (obj == null) {
        bdata = new byte[numDataElems * w * h];
      } else {
        bdata = (byte[])obj;
      }
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          o = getDataElements(j, i, o, data);
          byte[] btemp = (byte[])o;
          for (int k = 0; k < numDataElems; k++) {
            bdata[(cnt++)] = btemp[k];
          }
        }
      }
      obj = bdata;
      break;
    case 1: 
      short[] usdata;
      

      short[] usdata;
      
      if (obj == null) {
        usdata = new short[numDataElems * w * h];
      } else {
        usdata = (short[])obj;
      }
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          o = getDataElements(j, i, o, data);
          short[] ustemp = (short[])o;
          for (int k = 0; k < numDataElems; k++) {
            usdata[(cnt++)] = ustemp[k];
          }
        }
      }
      
      obj = usdata;
      break;
    case 3: 
      int[] idata;
      

      int[] idata;
      
      if (obj == null) {
        idata = new int[numDataElems * w * h];
      } else {
        idata = (int[])obj;
      }
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          o = getDataElements(j, i, o, data);
          int[] itemp = (int[])o;
          for (int k = 0; k < numDataElems; k++) {
            idata[(cnt++)] = itemp[k];
          }
        }
      }
      
      obj = idata;
      break;
    case 2: 
      short[] sdata;
      

      short[] sdata;
      
      if (obj == null) {
        sdata = new short[numDataElems * w * h];
      } else {
        sdata = (short[])obj;
      }
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          o = getDataElements(j, i, o, data);
          short[] stemp = (short[])o;
          for (int k = 0; k < numDataElems; k++) {
            sdata[(cnt++)] = stemp[k];
          }
        }
      }
      
      obj = sdata;
      break;
    case 4: 
      float[] fdata;
      

      float[] fdata;
      
      if (obj == null) {
        fdata = new float[numDataElems * w * h];
      } else {
        fdata = (float[])obj;
      }
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          o = getDataElements(j, i, o, data);
          float[] ftemp = (float[])o;
          for (int k = 0; k < numDataElems; k++) {
            fdata[(cnt++)] = ftemp[k];
          }
        }
      }
      
      obj = fdata;
      break;
    case 5: 
      double[] ddata;
      

      double[] ddata;
      
      if (obj == null) {
        ddata = new double[numDataElems * w * h];
      } else {
        ddata = (double[])obj;
      }
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          o = getDataElements(j, i, o, data);
          double[] dtemp = (double[])o;
          for (int k = 0; k < numDataElems; k++) {
            ddata[(cnt++)] = dtemp[k];
          }
        }
      }
      
      obj = ddata;
    }
    
    

    return obj;
  }
  







































  public void setDataElements(int x, int y, Object obj, DataBuffer data)
  {
    int type = getTransferType();
    int numDataElems = getNumDataElements();
    int pixelOffset = y * scanlineStride + x * pixelStride;
    
    switch (type)
    {

    case 0: 
      byte[] barray = (byte[])obj;
      
      for (int i = 0; i < numDataElems; i++) {
        data.setElem(bankIndices[i], pixelOffset + bandOffsets[i], 
          barray[i] & 0xFF);
      }
      break;
    

    case 1: 
      short[] usarray = (short[])obj;
      
      for (int i = 0; i < numDataElems; i++) {
        data.setElem(bankIndices[i], pixelOffset + bandOffsets[i], 
          usarray[i] & 0xFFFF);
      }
      break;
    

    case 3: 
      int[] iarray = (int[])obj;
      
      for (int i = 0; i < numDataElems; i++) {
        data.setElem(bankIndices[i], 
          pixelOffset + bandOffsets[i], iarray[i]);
      }
      break;
    

    case 2: 
      short[] sarray = (short[])obj;
      
      for (int i = 0; i < numDataElems; i++) {
        data.setElem(bankIndices[i], 
          pixelOffset + bandOffsets[i], sarray[i]);
      }
      break;
    

    case 4: 
      float[] farray = (float[])obj;
      
      for (int i = 0; i < numDataElems; i++) {
        data.setElemFloat(bankIndices[i], 
          pixelOffset + bandOffsets[i], farray[i]);
      }
      break;
    

    case 5: 
      double[] darray = (double[])obj;
      
      for (int i = 0; i < numDataElems; i++) {
        data.setElemDouble(bankIndices[i], 
          pixelOffset + bandOffsets[i], darray[i]);
      }
    }
    
  }
  









































  public void setDataElements(int x, int y, int w, int h, Object obj, DataBuffer data)
  {
    int cnt = 0;
    Object o = null;
    int type = getTransferType();
    int numDataElems = getNumDataElements();
    
    switch (type)
    {

    case 0: 
      byte[] barray = (byte[])obj;
      byte[] btemp = new byte[numDataElems];
      
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          for (int k = 0; k < numDataElems; k++) {
            btemp[k] = barray[(cnt++)];
          }
          
          setDataElements(j, i, btemp, data);
        }
      }
      break;
    

    case 1: 
      short[] usarray = (short[])obj;
      short[] ustemp = new short[numDataElems];
      
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          for (int k = 0; k < numDataElems; k++) {
            ustemp[k] = usarray[(cnt++)];
          }
          setDataElements(j, i, ustemp, data);
        }
      }
      break;
    

    case 3: 
      int[] iArray = (int[])obj;
      int[] itemp = new int[numDataElems];
      
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          for (int k = 0; k < numDataElems; k++) {
            itemp[k] = iArray[(cnt++)];
          }
          
          setDataElements(j, i, itemp, data);
        }
      }
      break;
    


    case 2: 
      short[] sArray = (short[])obj;
      short[] stemp = new short[numDataElems];
      
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          for (int k = 0; k < numDataElems; k++) {
            stemp[k] = sArray[(cnt++)];
          }
          
          setDataElements(j, i, stemp, data);
        }
      }
      break;
    

    case 4: 
      float[] fArray = (float[])obj;
      float[] ftemp = new float[numDataElems];
      
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          for (int k = 0; k < numDataElems; k++) {
            ftemp[k] = fArray[(cnt++)];
          }
          
          setDataElements(j, i, ftemp, data);
        }
      }
      break;
    

    case 5: 
      double[] dArray = (double[])obj;
      double[] dtemp = new double[numDataElems];
      
      for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
          for (int k = 0; k < numDataElems; k++) {
            dtemp[k] = dArray[(cnt++)];
          }
          
          setDataElements(j, i, dtemp, data);
        }
      }
    }
    
  }
  
















  public void setSample(int x, int y, int b, float s, DataBuffer data)
  {
    data.setElemFloat(bankIndices[b], 
      y * scanlineStride + x * pixelStride + bandOffsets[b], 
      s);
  }
  












  public float getSampleFloat(int x, int y, int b, DataBuffer data)
  {
    float sample = 
      data.getElemFloat(bankIndices[b], 
      y * scanlineStride + x * pixelStride + 
      bandOffsets[b]);
    return sample;
  }
  















  public void setSample(int x, int y, int b, double s, DataBuffer data)
  {
    data.setElemDouble(bankIndices[b], 
      y * scanlineStride + x * pixelStride + bandOffsets[b], 
      s);
  }
  












  public double getSampleDouble(int x, int y, int b, DataBuffer data)
  {
    double sample = 
      data.getElemDouble(bankIndices[b], 
      y * scanlineStride + x * pixelStride + 
      bandOffsets[b]);
    return sample;
  }
  














  public double[] getPixels(int x, int y, int w, int h, double[] dArray, DataBuffer data)
  {
    int Offset = 0;
    double[] pixels;
    double[] pixels; if (dArray != null) {
      pixels = dArray;
    } else {
      pixels = new double[numBands * w * h];
    }
    for (int i = y; i < h + y; i++) {
      for (int j = x; j < w + x; j++) {
        for (int k = 0; k < numBands; k++) {
          pixels[(Offset++)] = getSampleDouble(j, i, k, data);
        }
      }
    }
    
    return pixels;
  }
  

  public String toString()
  {
    String ret = "ComponentSampleModelJAI:   dataType=" + 
      getDataType() + 
      "  numBands=" + getNumBands() + 
      "  width=" + getWidth() + 
      "  height=" + getHeight() + 
      "  bandOffsets=[ ";
    for (int i = 0; i < numBands; i++) {
      ret = ret + getBandOffsets()[i] + " ";
    }
    ret = ret + "]";
    return ret;
  }
}

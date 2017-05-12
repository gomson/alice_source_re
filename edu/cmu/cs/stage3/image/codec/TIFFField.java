package edu.cmu.cs.stage3.image.codec;









public class TIFFField
{
  public static final int TIFF_BYTE = 1;
  







  public static final int TIFF_ASCII = 2;
  







  public static final int TIFF_SHORT = 3;
  






  public static final int TIFF_LONG = 4;
  






  public static final int TIFF_RATIONAL = 5;
  






  public static final int TIFF_SBYTE = 6;
  






  public static final int TIFF_UNDEFINED = 7;
  






  public static final int TIFF_SSHORT = 8;
  






  public static final int TIFF_SLONG = 9;
  






  public static final int TIFF_SRATIONAL = 10;
  






  public static final int TIFF_FLOAT = 11;
  






  public static final int TIFF_DOUBLE = 12;
  






  int tag;
  






  int type;
  






  int count;
  






  Object data;
  







  TIFFField() {}
  







  public TIFFField(int tag, int type, int count, Object data)
  {
    this.tag = tag;
    this.type = type;
    this.count = count;
    this.data = data;
  }
  


  public int getTag()
  {
    return tag;
  }
  






  public int getType()
  {
    return type;
  }
  


  public int getCount()
  {
    return count;
  }
  











  public byte[] getAsBytes()
  {
    return (byte[])data;
  }
  






  public char[] getAsChars()
  {
    return (char[])data;
  }
  






  public short[] getAsShorts()
  {
    return (short[])data;
  }
  






  public int[] getAsInts()
  {
    return (int[])data;
  }
  






  public long[] getAsLongs()
  {
    return (long[])data;
  }
  





  public float[] getAsFloats()
  {
    return (float[])data;
  }
  





  public double[] getAsDoubles()
  {
    return (double[])data;
  }
  





  public int[][] getAsSRationals()
  {
    return (int[][])data;
  }
  





  public long[][] getAsRationals()
  {
    return (long[][])data;
  }
  












  public int getAsInt(int index)
  {
    switch (type) {
    case 1: case 7: 
      return ((byte[])data)[index] & 0xFF;
    case 6: 
      return ((byte[])data)[index];
    case 3: 
      return ((char[])data)[index] & 0xFFFF;
    case 8: 
      return ((short[])data)[index];
    case 9: 
      return ((int[])data)[index];
    }
    throw new ClassCastException();
  }
  













  public long getAsLong(int index)
  {
    switch (type) {
    case 1: case 7: 
      return ((byte[])data)[index] & 0xFF;
    case 6: 
      return ((byte[])data)[index];
    case 3: 
      return ((char[])data)[index] & 0xFFFF;
    case 8: 
      return ((short[])data)[index];
    case 9: 
      return ((int[])data)[index];
    case 4: 
      return ((long[])data)[index];
    }
    throw new ClassCastException();
  }
  











  public float getAsFloat(int index)
  {
    switch (type) {
    case 1: 
      return ((byte[])data)[index] & 0xFF;
    case 6: 
      return ((byte[])data)[index];
    case 3: 
      return ((char[])data)[index] & 0xFFFF;
    case 8: 
      return ((short[])data)[index];
    case 9: 
      return ((int[])data)[index];
    case 4: 
      return (float)((long[])data)[index];
    case 11: 
      return ((float[])data)[index];
    case 12: 
      return (float)((double[])data)[index];
    case 10: 
      int[] ivalue = getAsSRational(index);
      return (float)(ivalue[0] / ivalue[1]);
    case 5: 
      long[] lvalue = getAsRational(index);
      return (float)(lvalue[0] / lvalue[1]);
    }
    throw new ClassCastException();
  }
  









  public double getAsDouble(int index)
  {
    switch (type) {
    case 1: 
      return ((byte[])data)[index] & 0xFF;
    case 6: 
      return ((byte[])data)[index];
    case 3: 
      return ((char[])data)[index] & 0xFFFF;
    case 8: 
      return ((short[])data)[index];
    case 9: 
      return ((int[])data)[index];
    case 4: 
      return ((long[])data)[index];
    case 11: 
      return ((float[])data)[index];
    case 12: 
      return ((double[])data)[index];
    case 10: 
      int[] ivalue = getAsSRational(index);
      return ivalue[0] / ivalue[1];
    case 5: 
      long[] lvalue = getAsRational(index);
      return lvalue[0] / lvalue[1];
    }
    throw new ClassCastException();
  }
  






  public String getAsString(int index)
  {
    return ((String[])data)[index];
  }
  






  public int[] getAsSRational(int index)
  {
    return ((int[][])data)[index];
  }
  






  public long[] getAsRational(int index)
  {
    return ((long[][])data)[index];
  }
}

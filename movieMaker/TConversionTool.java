package movieMaker;








public class TConversionTool
{
  private static final boolean ZEROTRAP = true;
  






  private static final short BIAS = 132;
  





  private static final int CLIP = 32635;
  






  public TConversionTool() {}
  






  public static int bytesToInt16(byte[] buffer, int byteOffset, boolean bigEndian)
  {
    return bigEndian ? 
      buffer[byteOffset] << 8 | buffer[(byteOffset + 1)] & 0xFF : 
      
      buffer[(byteOffset + 1)] << 8 | buffer[byteOffset] & 0xFF;
  }
  









  public static int bytesToInt24(byte[] buffer, int byteOffset, boolean bigEndian)
  {
    return bigEndian ? 
      buffer[byteOffset] << 16 | 
      (buffer[(byteOffset + 1)] & 0xFF) << 8 | 
      buffer[(byteOffset + 2)] & 0xFF : 
      
      buffer[(byteOffset + 2)] << 16 | 
      (buffer[(byteOffset + 1)] & 0xFF) << 8 | 
      buffer[byteOffset] & 0xFF;
  }
  









  public static int bytesToInt32(byte[] buffer, int byteOffset, boolean bigEndian)
  {
    return bigEndian ? 
      buffer[byteOffset] << 24 | 
      (buffer[(byteOffset + 1)] & 0xFF) << 16 | 
      (buffer[(byteOffset + 2)] & 0xFF) << 8 | 
      buffer[(byteOffset + 3)] & 0xFF : 
      
      buffer[(byteOffset + 3)] << 24 | 
      (buffer[(byteOffset + 2)] & 0xFF) << 16 | 
      (buffer[(byteOffset + 1)] & 0xFF) << 8 | 
      buffer[byteOffset] & 0xFF;
  }
  





  private static final int[] exp_lut1 = {
    0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 
    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
    5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
    5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 };
  



  private static short[] u2l = {
    33412, 34436, 35460, 36484, 37508, 38532, 39556, 40580, 
    41604, 42628, 43652, 44676, 45700, 46724, 47748, 48772, 
    49540, 50052, 50564, 51076, 51588, 52100, 52612, 53124, 
    53636, 54148, 54660, 55172, 55684, 56196, 56708, 57220, 
    57604, 57860, 58116, 58372, 58628, 58884, 59140, 59396, 
    59652, 59908, 60164, 60420, 60676, 60932, 61188, 61444, 
    61636, 61764, 61892, 62020, 62148, 62276, 62404, 62532, 
    62660, 62788, 62916, 63044, 63172, 63300, 63428, 63556, 
    63652, 63716, 63780, 63844, 63908, 63972, 64036, 64100, 
    64164, 64228, 64292, 64356, 64420, 64484, 64548, 64612, 
    64660, 64692, 64724, 64756, 64788, 64820, 64852, 64884, 
    64916, 64948, 64980, 65012, 65044, 65076, 65108, 65140, 
    65164, 65180, 65196, 65212, 65228, 65244, 65260, 65276, 
    65292, 65308, 65324, 65340, 65356, 65372, 65388, 65404, 
    -120, -112, -104, -96, -88, -80, -72, -64, 
    -56, -48, -40, -32, -24, -16, -8, 
    0, 32124, 31100, 30076, 29052, 28028, 27004, 25980, 24956, 
    23932, 22908, 21884, 20860, 19836, 18812, 17788, 16764, 
    15996, 15484, 14972, 14460, 13948, 13436, 12924, 12412, 
    11900, 11388, 10876, 10364, 9852, 9340, 8828, 8316, 
    7932, 7676, 7420, 7164, 6908, 6652, 6396, 6140, 
    5884, 5628, 5372, 5116, 4860, 4604, 4348, 4092, 
    3900, 3772, 3644, 3516, 3388, 3260, 3132, 3004, 
    2876, 2748, 2620, 2492, 2364, 2236, 2108, 1980, 
    1884, 1820, 1756, 1692, 1628, 1564, 1500, 1436, 
    1372, 1308, 1244, 1180, 1116, 1052, 988, 924, 
    876, 844, 812, 780, 748, 716, 684, 652, 
    620, 588, 556, 524, 492, 460, 428, 396, 
    372, 356, 340, 324, 308, 292, 276, 260, 
    244, 228, 212, 196, 180, 164, 148, 132, 
    120, 112, 104, 96, 88, 80, 72, 64, 
    56, 48, 40, 32, 24, 16, 8 };
  private static final byte QUANT_MASK = 15;
  private static final byte SEG_SHIFT = 4;
  
  public static short ulaw2linear(byte ulawbyte) { return u2l[(ulawbyte & 0xFF)]; }
  










  public static byte linear2ulaw(int sample)
  {
    if (sample > 32767) { sample = 32767;
    } else if (sample < 32768) { sample = 32768;
    }
    int sign = sample >> 8 & 0x80;
    if (sign != 0) sample = -sample;
    if (sample > 32635) { sample = 32635;
    }
    
    sample += 132;
    int exponent = exp_lut1[(sample >> 7 & 0xFF)];
    int mantissa = sample >> exponent + 3 & 0xF;
    int ulawbyte = (sign | exponent << 4 | mantissa) ^ 0xFFFFFFFF;
    
    if (ulawbyte == 0) ulawbyte = 2;
    return (byte)ulawbyte;
  }
  


























  private static final short[] seg_end = {
    255, 511, 1023, 2047, 4095, 8191, 16383, Short.MAX_VALUE };
  





  private static short[] a2l = {
    60032, 60288, 59520, 59776, 61056, 61312, 60544, 60800, 
    57984, 58240, 57472, 57728, 59008, 59264, 58496, 58752, 
    62784, 62912, 62528, 62656, 63296, 63424, 63040, 63168, 
    61760, 61888, 61504, 61632, 62272, 62400, 62016, 62144, 
    43520, 44544, 41472, 42496, 47616, 48640, 45568, 46592, 
    35328, 36352, 33280, 34304, 39424, 40448, 37376, 38400, 
    54528, 55040, 53504, 54016, 56576, 57088, 55552, 56064, 
    50432, 50944, 49408, 49920, 52480, 52992, 51456, 51968, 
    65192, 65208, 65160, 65176, 65256, 65272, 65224, 65240, 
    65064, 65080, 65032, 65048, 65128, 65144, 65096, 65112, 
    -88, -72, -120, -104, -24, -8, -56, -40, 
    65320, 65336, 65288, 65304, 65384, 65400, 65352, 65368, 
    64160, 64224, 64032, 64096, 64416, 64480, 64288, 64352, 
    63648, 63712, 63520, 63584, 63904, 63968, 63776, 63840, 
    64848, 64880, 64784, 64816, 64976, 65008, 64912, 64944, 
    64592, 64624, 64528, 64560, 64720, 64752, 64656, 64688, 
    5504, 5248, 6016, 5760, 4480, 4224, 4992, 4736, 
    7552, 7296, 8064, 7808, 6528, 6272, 7040, 6784, 
    2752, 2624, 3008, 2880, 2240, 2112, 2496, 2368, 
    3776, 3648, 4032, 3904, 3264, 3136, 3520, 3392, 
    22016, 20992, 24064, 23040, 17920, 16896, 19968, 18944, 
    30208, 29184, 32256, 31232, 26112, 25088, 28160, 27136, 
    11008, 10496, 12032, 11520, 8960, 8448, 9984, 9472, 
    15104, 14592, 16128, 15616, 13056, 12544, 14080, 13568, 
    344, 328, 376, 360, 280, 264, 312, 296, 
    472, 456, 504, 488, 408, 392, 440, 424, 
    88, 72, 120, 104, 24, 8, 56, 40, 
    216, 200, 248, 232, 152, 136, 184, 168, 
    1376, 1312, 1504, 1440, 1120, 1056, 1248, 1184, 
    1888, 1824, 2016, 1952, 1632, 1568, 1760, 1696, 
    688, 656, 752, 720, 560, 528, 624, 592, 
    944, 912, 1008, 976, 816, 784, 880, 848 };
  

  public static short alaw2linear(byte ulawbyte)
  {
    return a2l[(ulawbyte & 0xFF)];
  }
  


  public static byte linear2alaw(short pcm_val)
  {
    byte seg = 8;
    byte mask;
    byte mask;
    if (pcm_val >= 0) {
      mask = -43;
    } else {
      mask = 85;
      pcm_val = (short)(-pcm_val - 8);
    }
    

    for (int i = 0; i < 8; i++) {
      if (pcm_val <= seg_end[i]) {
        seg = (byte)i;
        break;
      }
    }
    

    if (seg >= 8) {
      return (byte)((0x7F ^ mask) & 0xFF);
    }
    byte aval = (byte)(seg << 4);
    if (seg < 2) {
      aval = (byte)(aval | pcm_val >> 4 & 0xF);
    } else
      aval = (byte)(aval | pcm_val >> seg + 3 & 0xF);
    return (byte)((aval ^ mask) & 0xFF);
  }
  
















  public static void intToBytes16(int sample, byte[] buffer, int byteOffset, boolean bigEndian)
  {
    if (bigEndian)
    {
      buffer[(byteOffset++)] = ((byte)(sample >> 8));
      buffer[byteOffset] = ((byte)(sample & 0xFF));
    }
    else
    {
      buffer[(byteOffset++)] = ((byte)(sample & 0xFF));
      buffer[byteOffset] = ((byte)(sample >> 8));
    }
  }
  













  public static void intToBytes24(int sample, byte[] buffer, int byteOffset, boolean bigEndian)
  {
    if (bigEndian)
    {
      buffer[(byteOffset++)] = ((byte)(sample >> 16));
      buffer[(byteOffset++)] = ((byte)(sample >>> 8 & 0xFF));
      buffer[byteOffset] = ((byte)(sample & 0xFF));
    }
    else
    {
      buffer[(byteOffset++)] = ((byte)(sample & 0xFF));
      buffer[(byteOffset++)] = ((byte)(sample >>> 8 & 0xFF));
      buffer[byteOffset] = ((byte)(sample >> 16));
    }
  }
  











  public static void intToBytes32(int sample, byte[] buffer, int byteOffset, boolean bigEndian)
  {
    if (bigEndian)
    {
      buffer[(byteOffset++)] = ((byte)(sample >> 24));
      buffer[(byteOffset++)] = ((byte)(sample >>> 16 & 0xFF));
      buffer[(byteOffset++)] = ((byte)(sample >>> 8 & 0xFF));
      buffer[byteOffset] = ((byte)(sample & 0xFF));
    }
    else
    {
      buffer[(byteOffset++)] = ((byte)(sample & 0xFF));
      buffer[(byteOffset++)] = ((byte)(sample >>> 8 & 0xFF));
      buffer[(byteOffset++)] = ((byte)(sample >>> 16 & 0xFF));
      buffer[byteOffset] = ((byte)(sample >> 24));
    }
  }
  











  public static int unsignedByteToInt(byte b)
  {
    return b & 0xFF;
  }
  








  public static int unsignedByteToInt16(byte[] buffer, int offset, boolean isBigEndian)
  {
    if (isBigEndian)
    {
      return unsignedByteToInt(buffer[offset]) << 8 | 
        unsignedByteToInt(buffer[(offset + 1)]);
    }
    

    return unsignedByteToInt(buffer[(offset + 1)]) << 8 | 
      unsignedByteToInt(buffer[offset]);
  }
  



  public static int unsignedByteToInt24(byte[] buffer, int offset, boolean isBigEndian)
  {
    if (isBigEndian)
    {
      return unsignedByteToInt(buffer[offset]) << 16 | 
        unsignedByteToInt(buffer[(offset + 1)]) << 8 | 
        unsignedByteToInt(buffer[(offset + 2)]);
    }
    

    return unsignedByteToInt(buffer[(offset + 2)]) << 16 | 
      unsignedByteToInt(buffer[(offset + 1)]) << 8 | 
      unsignedByteToInt(buffer[offset]);
  }
  


  public static int unsignedByteToInt32(byte[] buffer, int offset, boolean isBigEndian)
  {
    if (isBigEndian)
    {
      return unsignedByteToInt(buffer[offset]) << 24 | 
        unsignedByteToInt(buffer[(offset + 1)]) << 16 | 
        unsignedByteToInt(buffer[(offset + 2)]) << 8 | 
        unsignedByteToInt(buffer[(offset + 3)]);
    }
    

    return unsignedByteToInt(buffer[(offset + 3)]) << 24 | 
      unsignedByteToInt(buffer[(offset + 2)]) << 16 | 
      unsignedByteToInt(buffer[(offset + 1)]) << 8 | 
      unsignedByteToInt(buffer[offset]);
  }
  































  public static byte intToUnsignedByte(int sample)
  {
    return (byte)(sample ^ 0xFFFFFF80);
  }
  


















  public static void intToUnsignedBytes16(int sample, byte[] buffer, int byteOffset, boolean bigEndian)
  {
    if (bigEndian)
    {
      buffer[byteOffset] = ((byte)(sample >>> 8 ^ 0xFFFFFF80));
      buffer[(byteOffset + 1)] = ((byte)(sample & 0xFF));
    }
    else
    {
      buffer[(byteOffset + 1)] = ((byte)(sample >>> 8 ^ 0xFFFFFF80));
      buffer[byteOffset] = ((byte)(sample & 0xFF));
    }
  }
  

  public static void intToUnsignedBytes24(int sample, byte[] buffer, int byteOffset, boolean bigEndian)
  {
    if (bigEndian)
    {
      buffer[byteOffset] = ((byte)(sample >>> 16 ^ 0xFFFFFF80));
      buffer[(byteOffset + 1)] = ((byte)(sample >>> 8));
      buffer[(byteOffset + 2)] = ((byte)(sample & 0xFF));
    }
    else
    {
      buffer[(byteOffset + 2)] = ((byte)(sample >>> 16 ^ 0xFFFFFF80));
      buffer[(byteOffset + 1)] = ((byte)(sample >>> 8));
      buffer[byteOffset] = ((byte)(sample & 0xFF));
    }
  }
  

  public static void intToUnsignedBytes32(int sample, byte[] buffer, int byteOffset, boolean bigEndian)
  {
    if (bigEndian)
    {
      buffer[byteOffset] = ((byte)(sample >>> 24 ^ 0xFFFFFF80));
      buffer[(byteOffset + 1)] = ((byte)(sample >>> 16));
      buffer[(byteOffset + 2)] = ((byte)(sample >>> 8));
      buffer[(byteOffset + 3)] = ((byte)(sample & 0xFF));
    }
    else
    {
      buffer[(byteOffset + 3)] = ((byte)(sample >>> 24 ^ 0xFFFFFF80));
      buffer[(byteOffset + 2)] = ((byte)(sample >>> 16));
      buffer[(byteOffset + 1)] = ((byte)(sample >>> 8));
      buffer[byteOffset] = ((byte)(sample & 0xFF));
    }
  }
}

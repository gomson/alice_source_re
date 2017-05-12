package edu.cmu.cs.stage3.image.codec;




























































public class JPEGEncodeParam
  implements ImageEncodeParam
{
  private static int JPEG_MAX_BANDS = 3;
  
  private int[] hSamp;
  
  private int[] vSamp;
  
  private int[][] qTab;
  
  private int[] qTabSlot;
  
  private float qual;
  
  private int rstInterval;
  private boolean writeImageOnly;
  private boolean writeTablesOnly;
  private boolean writeJFIFHeader;
  private boolean qualitySet;
  private boolean[] qTabSet;
  
  public JPEGEncodeParam()
  {
    hSamp = new int[JPEG_MAX_BANDS];
    vSamp = new int[JPEG_MAX_BANDS];
    qTabSlot = new int[JPEG_MAX_BANDS];
    qTab = new int[JPEG_MAX_BANDS][];
    qTabSet = new boolean[JPEG_MAX_BANDS];
    

    hSamp[0] = 1;
    vSamp[0] = 1;
    qTabSlot[0] = 0;
    qTab[0] = null;
    qTabSet[0] = false;
    

    hSamp[1] = 2;
    vSamp[1] = 2;
    qTabSlot[1] = 1;
    qTab[1] = null;
    qTabSet[1] = false;
    

    hSamp[2] = 2;
    vSamp[2] = 2;
    qTabSlot[2] = 1;
    qTab[2] = null;
    qTabSet[2] = false;
    
    qual = 0.75F;
    rstInterval = 0;
    writeImageOnly = false;
    writeTablesOnly = false;
    writeJFIFHeader = true;
  }
  






  public void setHorizontalSubsampling(int component, int subsample)
  {
    hSamp[component] = subsample;
  }
  




  public int getHorizontalSubsampling(int component)
  {
    return hSamp[component];
  }
  







  public void setVerticalSubsampling(int component, int subsample)
  {
    vSamp[component] = subsample;
  }
  




  public int getVerticalSubsampling(int component)
  {
    return vSamp[component];
  }
  






  public void setLumaQTable(int[] qTable)
  {
    setQTable(0, 0, qTable);
    qTabSet[0] = true;
    qualitySet = false;
  }
  







  public void setChromaQTable(int[] qTable)
  {
    setQTable(1, 1, qTable);
    setQTable(2, 1, qTable);
    qTabSet[1] = true;
    qTabSet[2] = true;
    qualitySet = false;
  }
  







  public void setQTable(int component, int tableSlot, int[] qTable)
  {
    qTab[component] = ((int[])qTable.clone());
    qTabSlot[component] = tableSlot;
    qTabSet[component] = true;
    qualitySet = false;
  }
  



  public boolean isQTableSet(int component)
  {
    return qTabSet[component];
  }
  





  public int[] getQTable(int component)
  {
    if (qTabSet[component] == 0) {
      throw new IllegalStateException(
        JaiI18N.getString("A_quantization_table_has_not_been_set_for_this_component_"));
    }
    return qTab[component];
  }
  





  public int getQTableSlot(int component)
  {
    if (qTabSet[component] == 0) {
      throw new IllegalStateException(
        JaiI18N.getString("A_quantization_table_has_not_been_set_for_this_component_"));
    }
    return qTabSlot[component];
  }
  






  public void setRestartInterval(int restartInterval)
  {
    rstInterval = restartInterval;
  }
  



  public int getRestartInterval()
  {
    return rstInterval;
  }
  





















  public void setQuality(float quality)
  {
    qual = quality;
    
    for (int i = 0; i < JPEG_MAX_BANDS; i++) {
      qTabSet[i] = false;
    }
    qualitySet = true;
  }
  



  public boolean isQualitySet()
  {
    return qualitySet;
  }
  





  public float getQuality()
  {
    return qual;
  }
  





  public void setWriteTablesOnly(boolean tablesOnly)
  {
    writeTablesOnly = tablesOnly;
  }
  



  public boolean getWriteTablesOnly()
  {
    return writeTablesOnly;
  }
  






  public void setWriteImageOnly(boolean imageOnly)
  {
    writeImageOnly = imageOnly;
  }
  



  public boolean getWriteImageOnly()
  {
    return writeImageOnly;
  }
  




  public void setWriteJFIFHeader(boolean writeJFIF)
  {
    writeJFIFHeader = writeJFIF;
  }
  



  public boolean getWriteJFIFHeader()
  {
    return writeJFIFHeader;
  }
}

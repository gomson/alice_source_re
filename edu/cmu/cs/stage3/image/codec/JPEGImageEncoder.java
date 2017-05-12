package edu.cmu.cs.stage3.image.codec;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGQTable;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;























































public class JPEGImageEncoder
  extends ImageEncoderImpl
{
  private JPEGEncodeParam jaiEP = null;
  
  public JPEGImageEncoder(OutputStream output, ImageEncodeParam param)
  {
    super(output, param);
    if (param != null) {
      jaiEP = ((JPEGEncodeParam)param);
    }
  }
  









  private void modifyEncodeParam(JPEGEncodeParam jaiEP, com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP, int nbands)
  {
    for (int i = 0; i < nbands; i++)
    {


      int val = jaiEP.getHorizontalSubsampling(i);
      j2dEP.setHorizontalSubsampling(i, val);
      
      val = jaiEP.getVerticalSubsampling(i);
      j2dEP.setVerticalSubsampling(i, val);
      



      if (jaiEP.isQTableSet(i)) {
        int[] qTab = jaiEP.getQTable(i);
        val = jaiEP.getQTableSlot(i);
        j2dEP.setQTableComponentMapping(i, val);
        j2dEP.setQTable(val, new JPEGQTable(qTab));
      }
    }
    

    if (jaiEP.isQualitySet()) {
      float fval = jaiEP.getQuality();
      j2dEP.setQuality(fval, true);
    }
    

    int val = jaiEP.getRestartInterval();
    j2dEP.setRestartInterval(val);
    

    if (jaiEP.getWriteTablesOnly()) {
      j2dEP.setImageInfoValid(false);
      j2dEP.setTableInfoValid(true);
    }
    

    if (jaiEP.getWriteImageOnly()) {
      j2dEP.setTableInfoValid(false);
      j2dEP.setImageInfoValid(true);
    }
    

    if (!jaiEP.getWriteJFIFHeader()) {
      j2dEP.setMarkerData(
        224, null);
    }
  }
  








  public void encode(RenderedImage im)
    throws IOException
  {
    SampleModel sampleModel = im.getSampleModel();
    ColorModel colorModel = im.getColorModel();
    

    int numBands = colorModel.getNumColorComponents();
    int transType = sampleModel.getTransferType();
    if ((transType != 0) || (
      (numBands != 1) && (numBands != 3))) {
      throw new RuntimeException(JaiI18N.getString("Only_1__or_3_band_byte_data_may_be_written_"));
    }
    

    int cspaceType = colorModel.getColorSpace().getType();
    if ((cspaceType != 6) && 
      (cspaceType != 5)) {
      throw new Error(JaiI18N.getString("ColorSpace_must_be_TYPE_RGB_for_numBands___1"));
    }
    














    WritableRaster wRas = (WritableRaster)im.getData();
    if ((wRas.getMinX() != 0) || (wRas.getMinY() != 0)) {
      wRas = wRas.createWritableTranslatedChild(0, 0);
    }
    

    com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP = null;
    BufferedImage bi; if ((colorModel instanceof IndexColorModel))
    {



      IndexColorModel icm = (IndexColorModel)colorModel;
      BufferedImage bi = icm.convertToIntDiscrete(wRas, false);
      j2dEP = JPEGCodec.getDefaultJPEGEncodeParam(bi);
    } else {
      bi = new BufferedImage(colorModel, wRas, false, null);
      j2dEP = JPEGCodec.getDefaultJPEGEncodeParam(bi);
    }
    



    if (jaiEP != null) {
      modifyEncodeParam(jaiEP, j2dEP, numBands);
    }
    


    com.sun.image.codec.jpeg.JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(
      output, j2dEP);
    
    try
    {
      encoder.encode(bi);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}

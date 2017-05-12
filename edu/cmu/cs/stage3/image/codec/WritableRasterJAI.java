package edu.cmu.cs.stage3.image.codec;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;



























































class WritableRasterJAI
  extends WritableRaster
{
  protected WritableRasterJAI(SampleModel sampleModel, DataBuffer dataBuffer, Rectangle aRegion, Point sampleModelTranslate, WritableRaster parent)
  {
    super(sampleModel, dataBuffer, aRegion, sampleModelTranslate, parent);
  }
}

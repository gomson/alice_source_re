package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.property.ImageProperty;
import edu.cmu.cs.stage3.image.ImageUtilities;
import edu.cmu.cs.stage3.util.VisitListener;
import java.awt.Image;














public class TextureMapCounter
  implements VisitListener
{
  public TextureMapCounter() {}
  
  int m_textureMapCount = 0;
  int m_textureMapMemoryCount = 0;
  
  public void visited(Object o) { if ((o instanceof TextureMap)) {
      TextureMap textureMap = (TextureMap)o;
      m_textureMapCount += 1;
      Image image = image.getImageValue();
      try {
        int width = ImageUtilities.getWidth(image);
        int height = ImageUtilities.getHeight(image);
        int depth = 4;
        if ((width != -1) || (height != -1)) {
          m_textureMapMemoryCount += width * height * depth;
        }
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public int getTextureMapCount() {
    return m_textureMapCount;
  }
  
  public int getTextureMapMemoryCount() { return m_textureMapMemoryCount; }
}

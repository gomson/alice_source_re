package edu.cmu.cs.stage3.alice.authoringtool.importers;

import edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.property.ImageProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.image.ImageIO;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;







public class ImageImporter
  extends AbstractImporter
{
  public ImageImporter() {}
  
  public Map getExtensionMap()
  {
    HashMap knownCodecPrettyNames = new HashMap();
    knownCodecPrettyNames.put("BMP", Messages.getString("Windows_Bitmap"));
    knownCodecPrettyNames.put("GIF", Messages.getString("Graphic_Interchange_Format"));
    knownCodecPrettyNames.put("JPEG", Messages.getString("Joint_Photographic_Experts_Group_format"));
    knownCodecPrettyNames.put("PNG", Messages.getString("Portable_Network_Graphics_format"));
    knownCodecPrettyNames.put("TIFF", Messages.getString("Tagged_Image_File_Format"));
    
    HashMap map = new HashMap();
    
    String[] codecNames = ImageIO.getCodecNames();
    for (int i = 0; i < codecNames.length; i++) {
      String prettyName = (String)knownCodecPrettyNames.get(codecNames[i].toUpperCase());
      if (prettyName == null) {
        prettyName = codecNames[i];
      }
      String[] extensions = ImageIO.getExtensionsForCodec(codecNames[i]);
      for (int j = 0; j < extensions.length; j++) {
        map.put(extensions[j].toUpperCase(), prettyName);
      }
    }
    
    return map;
  }
  
  protected Element load(InputStream istream, String ext) throws IOException {
    String codecName = ImageIO.mapExtensionToCodecName(ext);
    if (codecName == null) {
      throw new IllegalArgumentException(Messages.getString("Unsupported_Extension__") + ext);
    }
    BufferedInputStream bis;
    BufferedInputStream bis;
    if ((istream instanceof BufferedInputStream)) {
      bis = (BufferedInputStream)istream;
    } else {
      bis = new BufferedInputStream(istream);
    }
    Image image = ImageIO.load(codecName, bis);
    
    TextureMap texture = new TextureMap();
    
    if ((image instanceof BufferedImage)) {
      BufferedImage bi = (BufferedImage)image;
      if (bi.getColorModel().hasAlpha()) {
        format.set(new Integer(3));
      }
    }
    
    name.set(plainName);
    image.set(image);
    
    return texture;
  }
}

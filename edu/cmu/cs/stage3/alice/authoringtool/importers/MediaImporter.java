package edu.cmu.cs.stage3.alice.authoringtool.importers;

import edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.property.DataSourceProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.media.DataSource;
import edu.cmu.cs.stage3.media.Manager;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

































public class MediaImporter
  extends AbstractImporter
{
  public MediaImporter() {}
  
  public Map getExtensionMap()
  {
    HashMap map = new HashMap();
    map.put("WAV", "Windows sound");
    map.put("MP3", "Mpeg layer 3");
    return map;
  }
  
  protected Element load(InputStream istream, String ext) throws IOException {
    DataSource dataSource = Manager.createDataSource(istream, ext);
    Sound sound = new Sound();
    name.set(plainName);
    dataSource.set(dataSource);
    return sound;
  }
}

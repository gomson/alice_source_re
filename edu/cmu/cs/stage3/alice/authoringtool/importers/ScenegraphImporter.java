package edu.cmu.cs.stage3.alice.authoringtool.importers;

import edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.util.ScenegraphConverter;
import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.io.XML;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;



























public class ScenegraphImporter
  extends AbstractImporter
{
  public ScenegraphImporter() {}
  
  public Map getExtensionMap()
  {
    HashMap map = new HashMap();
    map.put("ASG", "Alice SceneGraph");
    return map;
  }
  
  protected Element load(InputStream is, String ext) throws IOException {
    Component sgSrc = XML.load(is);
    return ScenegraphConverter.convert((Container)sgSrc);
  }
}

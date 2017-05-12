package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Group;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.util.Criterion;







public class InAppropriateObjectArrayPropertyCriterion
  implements Criterion
{
  public InAppropriateObjectArrayPropertyCriterion() {}
  
  public boolean accept(Object object)
  {
    if ((object instanceof Element)) {
      Element element = (Element)object;
      Element parent = element.getParent();
      
      if (parent != null) {
        ObjectArrayProperty oap = null;
        if ((element instanceof Transformable)) {
          if ((parent instanceof World)) {
            oap = sandboxes;
          } else if ((parent instanceof Transformable)) {
            oap = parts;
          } else if ((parent instanceof Group)) {
            oap = values;
          }
        } else if ((element instanceof Response)) {
          if ((parent instanceof Sandbox)) {
            oap = responses;
          }
        } else if ((element instanceof Behavior)) {
          if ((parent instanceof Sandbox)) {
            oap = behaviors;
          }
        } else if ((element instanceof Variable)) {
          if ((parent instanceof Sandbox)) {
            oap = variables;
          }
        } else if ((element instanceof Question)) {
          if ((parent instanceof Sandbox)) {
            oap = questions;
          }
        } else if ((element instanceof Sound)) {
          if ((parent instanceof Sandbox)) {
            oap = sounds;
          }
        } else if ((element instanceof TextureMap)) {
          if ((parent instanceof Sandbox)) {
            oap = textureMaps;
          }
        } else if ((element instanceof Pose)) {
          if ((parent instanceof Transformable)) {
            oap = poses;
          }
        }
        else if ((parent instanceof Sandbox)) {
          oap = misc;
        }
        
        if (oap != null) {
          return oap.contains(element);
        }
      }
    }
    
    return true;
  }
}

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import java.awt.event.MouseEvent;

















public class PickQuestion
  extends Question
{
  public PickQuestion() {}
  
  public final BooleanProperty ascend = new BooleanProperty(this, "ascend", Boolean.TRUE);
  

  public void setMouseEvent(MouseEvent mouseEvent) { m_mouseEvent = mouseEvent; }
  
  private Model ascend(Model part) {
    if (part.doEventsStopAscending()) {
      return part;
    }
    Element parent = part.getParent();
    if ((parent instanceof Model)) {
      return ascend((Model)parent);
    }
    return part;
  }
  
  private MouseEvent m_mouseEvent;
  public Object getValue()
  {
    PickInfo pickInfo = RenderTarget.pick(m_mouseEvent);
    if ((pickInfo != null) && (pickInfo.getCount() > 0)) {
      Object o = pickInfo.getVisualAt(0).getBonus();
      if ((o instanceof Model)) {
        Model part = (Model)o;
        if (ascend.booleanValue()) {
          return ascend(part);
        }
        return part;
      }
      
      return null;
    }
    
    return null;
  }
  
  public Class getValueClass()
  {
    return Model.class;
  }
}

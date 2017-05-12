package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.math.Matrix33;
import java.util.Vector;
import javax.vecmath.Vector3d;


public class StraightenAnimation
  extends TransformAnimation
{
  public StraightenAnimation() {}
  
  public class RuntimeStraightenAnimation
    extends TransformAnimation.RuntimeTransformAnimation
  {
    public RuntimeStraightenAnimation() { super(); }
    Vector bodyPartInitialOrientations = null;
    Vector bodyParts = null;
    
    Matrix33 normalOrientation = new Matrix33();
    
    public void prologue(double t)
    {
      super.prologue(t);
      bodyPartInitialOrientations = new Vector();
      bodyParts = new Vector();
      normalOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, 1.0D), new Vector3d(0.0D, 1.0D, 0.0D));
      
      if (m_subject != null) {
        if (!(m_subject.getParent() instanceof World)) addBodyPart(m_subject);
        findChildren(m_subject);
      }
    }
    

    public void update(double t)
    {
      for (int i = 0; i < bodyPartInitialOrientations.size(); i++) {
        setOrientation((Transformable)bodyParts.elementAt(i), (Matrix33)bodyPartInitialOrientations.elementAt(i), normalOrientation, getPortion(t));
      }
      
      super.update(t);
    }
    
    private void findChildren(Transformable part) {
      Element[] kids = part.getChildren(Transformable.class);
      for (int i = 0; i < kids.length; i++) {
        Transformable trans = (Transformable)kids[i];
        addBodyPart(trans);
        
        if (trans.getChildCount() > 0) {
          findChildren(trans);
        }
      }
    }
    
    private void addBodyPart(Transformable partToAdd) {
      bodyPartInitialOrientations.addElement(partToAdd.getOrientationAsAxes((ReferenceFrame)partToAdd.getParent()));
      bodyParts.addElement(partToAdd);
    }
    
    private void setOrientation(Transformable part, Matrix33 initialOrient, Matrix33 finalOrient, double portion)
    {
      Matrix33 currentOrient = Matrix33.interpolate(initialOrient, finalOrient, portion);
      if (part != null)
      {
        part.setOrientationRightNow(currentOrient, (ReferenceFrame)part.getParent());
      }
    }
  }
}

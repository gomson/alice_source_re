package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;
import java.util.Vector;
import javax.vecmath.Vector3d;


public class BetterStandUpAnimation
  extends StandUpAnimation
{
  public final BooleanProperty scootForward = new BooleanProperty(this, "scoot forward", Boolean.TRUE);
  
  public class RuntimeBetterStandUpAnimation extends StandUpAnimation.RuntimeStandUpAnimation { public RuntimeBetterStandUpAnimation() { super(); }
    Vector bodyPartInitialOrientations = null;
    Vector bodyParts = null;
    
    Matrix33 normalOrientation = new Matrix33();
    
    private Vector3d m_positionBegin;
    
    private Vector3d m_positionEnd;
    
    public void prologue(double t)
    {
      super.prologue(t);
      bodyPartInitialOrientations = new Vector();
      bodyParts = new Vector();
      normalOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, 1.0D), new Vector3d(0.0D, 1.0D, 0.0D));
      
      m_positionBegin = m_subject.getPosition(m_subject.getWorld());
      
      if (m_subject != null) {
        findChildren(m_subject);
      }
    }
    
    protected Vector3d getPositionEnd()
    {
      if (m_subject != null) {
        int level = (int)Math.round(m_positionBegin.y / 256.0D);
        
        Vector3d forward = getTargetQuaternion().getMatrix33().getRow(2);
        
        double moveAmount = 0.0D;
        
        Element[] legs = m_subject.search(new ElementNameContainsCriterion("UpperLeg"));
        if (legs.length > 0) {
          Model upperLeg = (Model)legs[0];
          moveAmount = upperLeg.getBoundingBox(upperLeg).getHeight();
        }
        


        if (scootForward.booleanValue())
        {
          m_positionEnd = new Vector3d(m_positionBegin.x + x * moveAmount, 256.0D * level, m_positionBegin.z + z * moveAmount);
        } else {
          m_positionEnd = new Vector3d(m_positionBegin.x, 256.0D * level, m_positionBegin.z);
        }
        
        return m_positionEnd;
      }
      return m_positionBegin;
    }
    

    public void update(double t)
    {
      for (int i = 0; i < bodyPartInitialOrientations.size(); i++) {
        setOrientation((Transformable)bodyParts.elementAt(i), (Matrix33)bodyPartInitialOrientations.elementAt(i), normalOrientation, getPortion(t));
      }
      
      if (m_positionEnd == null) {
        m_positionEnd = getPositionEnd();
      }
      
      m_subject.setPositionRightNow(MathUtilities.interpolate(m_positionBegin, m_positionEnd, getPortion(t)), ReferenceFrame.ABSOLUTE);
      
      adjustHeight();
      
      super.update(t);
    }
    
    public void epilogue(double t)
    {
      super.epilogue(t);
      
      m_positionEnd = null;
    }
    
    private void findChildren(Transformable part) {
      Element[] kids = part.getChildren(Transformable.class);
      for (int i = 0; i < kids.length; i++) {
        Transformable trans = (Transformable)kids[i];
        bodyPartInitialOrientations.addElement(trans.getOrientationAsAxes((ReferenceFrame)trans.getParent()));
        bodyParts.addElement(trans);
        
        if (trans.getChildCount() > 0) {
          findChildren(trans);
        }
      }
    }
    
    protected void adjustHeight() {
      double distanceAboveGround = 0.0D;
      
      if (m_subject != null) {
        distanceAboveGround = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y;
        
        double roundHeight = Math.round(m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y);
        int level = (int)Math.round(roundHeight / 256.0D);
        m_subject.moveRightNow(Direction.DOWN, distanceAboveGround - 256.0D * level, m_subject.getWorld());
      }
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
  
  public BetterStandUpAnimation() {}
}

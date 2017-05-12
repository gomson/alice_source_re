package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.vecmath.Vector3d;




























public class MouseLookingBehavior
  extends InternalResponseBehavior
  implements MouseListener, MouseMotionListener
{
  private RenderTarget renderTarget;
  private Vector3d turning = new Vector3d();
  private boolean mouseActive;
  private int lastX;
  private int lastY;
  
  public MouseLookingBehavior() {}
  
  public NumberProperty turningRate = new NumberProperty(this, "turningRate", new Double(0.001D));
  public TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public BooleanProperty onlyAffectsYaw = new BooleanProperty(this, "onlyAffectsYaw", Boolean.TRUE);
  
  protected void disable() {
    renderTarget.removeMouseListener(this);
    renderTarget.removeMouseMotionListener(this);
    mouseActive = false;
  }
  
  protected void enable() {
    renderTarget.addMouseListener(this);
    renderTarget.addMouseMotionListener(this);
    mouseActive = false;
  }
  
  public void started(World world, double time)
  {
    super.started(world, time);
    
    if (isEnabled.booleanValue()) {
      RenderTarget[] rts = (RenderTarget[])world.getDescendants(RenderTarget.class);
      if (rts.length > 0) {
        renderTarget = rts[0];
        if (subject.get() == null) {
          Camera[] cameras = renderTarget.getCameras();
          if (cameras.length > 0) {
            subject.set(cameras[0]);
          }
        }
      }
      enable();
    }
  }
  
  public void stopped(World world, double time)
  {
    super.stopped(world, time);
    if (isEnabled.booleanValue()) {
      disable();
    }
  }
  


  public void internalSchedule(double time, double dt)
  {
    subject.getTransformableValue().turnRightNow(Direction.FORWARD, turning.x);
    

    Transformable t = new Transformable();
    t.setPositionRightNow(subject.getTransformableValue().getPosition());
    t.setOrientationRightNow(subject.getTransformableValue().vehicle.getReferenceFrameValue().getOrientationAsQuaternion());
    
    subject.getTransformableValue().turnRightNow(Direction.RIGHT, turning.y, t);
    
    turning.x = 0.0D;
    turning.y = 0.0D;
    turning.z = 0.0D;
  }
  
  public void mouseClicked(MouseEvent e) {}
  
  public void mouseEntered(MouseEvent e) {
    lastX = e.getX();
    lastY = e.getY();
  }
  
  public void mouseExited(MouseEvent e) {}
  
  public void mousePressed(MouseEvent e) { lastX = e.getX();
    lastY = e.getY();
    mouseActive = true;
  }
  
  public void mouseReleased(MouseEvent e) { mouseActive = false; }
  

  public void mouseDragged(MouseEvent e)
  {
    int x = e.getX() - lastX;
    int y = e.getY() - lastY;
    if (lastX == -1) x = 0;
    if (lastY == -1) y = 0;
    lastX = e.getX();
    lastY = e.getY();
    if (mouseActive) {
      if (((Boolean)onlyAffectsYaw.get()).booleanValue()) {
        turning.y += x * turningRate.getNumberValue().doubleValue();
      } else {
        turning.y += x * turningRate.getNumberValue().doubleValue();
        turning.x += y * turningRate.getNumberValue().doubleValue();
      }
    }
  }
  
  public void mouseMoved(MouseEvent e) {}
}

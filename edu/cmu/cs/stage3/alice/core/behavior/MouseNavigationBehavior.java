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
import edu.cmu.cs.stage3.math.Vector3;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.vecmath.Vector3d;
























public class MouseNavigationBehavior
  extends InternalResponseBehavior
  implements MouseListener, MouseMotionListener
{
  private RenderTarget renderTarget;
  private Vector3d turning = new Vector3d(0.0D, 0.0D, 0.0D);
  private double movement = 0.0D;
  private boolean mouseActive;
  private int lastX;
  private int lastY;
  
  public MouseNavigationBehavior() {}
  
  public NumberProperty speed = new NumberProperty(this, "speed", new Double(0.1D));
  public NumberProperty turningRate = new NumberProperty(this, "turningRate", new Double(0.001D));
  public TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public BooleanProperty looking = new BooleanProperty(this, "looking", Boolean.FALSE);
  public BooleanProperty stayOnGround = new BooleanProperty(this, "stayOnGround", Boolean.TRUE);
  public BooleanProperty positionRelative = new BooleanProperty(this, "positionRelative", Boolean.TRUE);
  
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
  
  public void started(World world, double time) {
    super.started(world, time);
    if (isEnabled.booleanValue()) {
      RenderTarget[] rts = (RenderTarget[])world.getDescendants(RenderTarget.class);
      if (rts.length > 0) {
        renderTarget = rts[0];
        if (subject.getTransformableValue() == null) {
          Camera[] cameras = renderTarget.getCameras();
          if (cameras.length > 0) {
            subject.set(cameras[0]);
          }
        }
      }
      enable();
    }
  }
  
  public void stopped(World world, double time) {
    super.stopped(world, time);
    if (isEnabled.booleanValue()) {
      disable();
    }
  }
  
  public void internalSchedule(double time, double dt)
  {
    if (!mouseActive) { return;
    }
    

    if (!positionRelative.getBooleanValue().booleanValue()) {
      dt = 1.0D;
    }
    


    subject.getTransformableValue().turnRightNow(Direction.FORWARD, turning.x * dt);
    

    Transformable t = new Transformable();
    t.setPositionRightNow(subject.getTransformableValue().getPosition());
    t.setOrientationRightNow(subject.getTransformableValue().vehicle.getReferenceFrameValue().getOrientationAsQuaternion());
    
    subject.getTransformableValue().turnRightNow(Direction.RIGHT, turning.y * dt, t);
    


    double yPos = subject.getTransformableValue().getPosition().y;
    subject.getTransformableValue().moveRightNow(Direction.FORWARD, dt * movement);
    if (stayOnGround.getBooleanValue().booleanValue()) {
      Vector3d pos = subject.getTransformableValue().getPosition();
      y = yPos;
      subject.getTransformableValue().setPositionRightNow(pos);
    }
    
    if (!positionRelative.getBooleanValue().booleanValue()) {
      turning.x = 0.0D;
      turning.y = 0.0D;
      turning.z = 0.0D;
      movement = 0.0D;
    }
  }
  
  public void mouseClicked(MouseEvent e) {}
  
  public void mouseEntered(MouseEvent e) {
    if (!positionRelative.getBooleanValue().booleanValue()) {
      lastX = e.getX();
      lastY = e.getY();
    }
  }
  
  public void mouseExited(MouseEvent e) {}
  
  public void mousePressed(MouseEvent e) {
    lastX = e.getX();
    lastY = e.getY();
    mouseActive = true;
  }
  
  public void mouseReleased(MouseEvent e) {
    mouseActive = false;
    turning.x = 0.0D;
    turning.y = 0.0D;
    turning.z = 0.0D;
    movement = 0.0D;
  }
  
  public void mouseDragged(MouseEvent e)
  {
    int x = e.getX() - lastX;
    int y = e.getY() - lastY;
    if (lastX == -1) x = 0;
    if (lastY == -1) y = 0;
    if (!positionRelative.getBooleanValue().booleanValue()) {
      lastX = e.getX();
      lastY = e.getY();
    }
    if (mouseActive) {
      turning.y = ((positionRelative.getBooleanValue().booleanValue() ? 0.0D : turning.y) + x * turningRate.getNumberValue().doubleValue());
      if (((Boolean)looking.get()).booleanValue()) {
        turning.x = ((positionRelative.getBooleanValue().booleanValue() ? 0.0D : turning.x) + y * turningRate.getNumberValue().doubleValue());
      } else {
        movement = ((positionRelative.getBooleanValue().booleanValue() ? 0.0D : movement) - y * speed.getNumberValue().doubleValue());
      }
    }
  }
  
  public void mouseMoved(MouseEvent e) {
    mouseActive = false;
  }
}

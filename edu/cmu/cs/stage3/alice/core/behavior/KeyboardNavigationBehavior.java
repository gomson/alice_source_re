package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.navigation.KeyMapping;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import javax.vecmath.Vector3d;






public class KeyboardNavigationBehavior
  extends InternalResponseBehavior
{
  private RenderTarget renderTarget;
  
  public KeyboardNavigationBehavior() {}
  
  private Vector3d speed = new Vector3d(0.0D, 0.0D, 0.0D);
  private Vector3d turning = new Vector3d(0.0D, 0.0D, 0.0D);
  
  public NumberProperty maxSpeed = new NumberProperty(this, "maxSpeed", new Double(15.0D));
  public NumberProperty maxTurning = new NumberProperty(this, "maxTurning", new Double(0.15D));
  public NumberProperty speedAccel = new NumberProperty(this, "speedAccel", new Double(20.0D));
  public NumberProperty turningAccel = new NumberProperty(this, "turningAccel", new Double(0.35D));
  public BooleanProperty stayOnGround = new BooleanProperty(this, "stayOnGround", Boolean.TRUE);
  public TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public ElementProperty keyMap = new ElementProperty(this, "keyMap", null, KeyMapping.class);
  
  protected void disable() {
    renderTarget.removeKeyListener((KeyMapping)keyMap.get());
  }
  
  protected void enable() {
    renderTarget.addKeyListener((KeyMapping)keyMap.get());
  }
  
  public void started(World world, double time) {
    super.started(world, time);
    if (isEnabled.booleanValue()) {
      RenderTarget[] rts = (RenderTarget[])world.getDescendants(RenderTarget.class);
      if (rts.length > 0) {
        renderTarget = rts[0];
        if (subject.get() == null) {
          Camera[] cameras = renderTarget.getCameras();
          if (cameras.length > 0)
            subject.set(cameras[0]);
        }
      }
      if (keyMap.get() == null)
        keyMap.set(new KeyMapping());
      ((KeyMapping)keyMap.get()).cleanState();
      
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
  
  public void internalSchedule(double time, double dt) {
    KeyMapping keyMapping = (KeyMapping)keyMap.getElementValue();
    

    int actions = keyMapping.getActions();
    




    if ((actions & 0x1) != 0) {
      speed.z += dt * speedAccel.getNumberValue().doubleValue();
    } else if ((actions & 0x2) != 0) {
      speed.z -= dt * speedAccel.getNumberValue().doubleValue();
    } else if (Math.abs(speed.z) >= dt * speedAccel.getNumberValue().doubleValue()) {
      speed.z += (speed.z < 0.0D ? 1 : -1) * dt * speedAccel.getNumberValue().doubleValue();
    } else
      speed.z = 0.0D;
    if ((actions & 0x4) != 0) {
      speed.x -= dt * speedAccel.getNumberValue().doubleValue();
    } else if ((actions & 0x8) != 0) {
      speed.x += dt * speedAccel.getNumberValue().doubleValue();
    } else if (Math.abs(speed.x) >= dt * speedAccel.getNumberValue().doubleValue()) {
      speed.x += (speed.x < 0.0D ? 1 : -1) * dt * speedAccel.getNumberValue().doubleValue();
    } else
      speed.x = 0.0D;
    if ((actions & 0x10) != 0) {
      speed.y += dt * speedAccel.getNumberValue().doubleValue();
    } else if ((actions & 0x20) != 0) {
      speed.y -= dt * speedAccel.getNumberValue().doubleValue();
    } else if (Math.abs(speed.y) >= dt * speedAccel.getNumberValue().doubleValue()) {
      speed.y += (speed.y < 0.0D ? 1 : -1) * dt * speedAccel.getNumberValue().doubleValue();
    } else
      speed.y = 0.0D;
    if ((actions & 0x40) != 0) {
      turning.y -= dt * turningAccel.getNumberValue().doubleValue();
    } else if ((actions & 0x80) != 0) {
      turning.y += dt * turningAccel.getNumberValue().doubleValue();
    } else if (Math.abs(turning.y) >= dt * turningAccel.getNumberValue().doubleValue()) {
      turning.y += (turning.y < 0.0D ? 1 : -1) * dt * turningAccel.getNumberValue().doubleValue();
    } else
      turning.y = 0.0D;
    if ((actions & 0x100) != 0) {
      turning.x -= dt * turningAccel.getNumberValue().doubleValue();
    } else if ((actions & 0x200) != 0) {
      turning.x += dt * turningAccel.getNumberValue().doubleValue();
    } else if (Math.abs(turning.x) >= dt * turningAccel.getNumberValue().doubleValue()) {
      turning.x += (turning.x < 0.0D ? 1 : -1) * dt * turningAccel.getNumberValue().doubleValue();
    } else
      turning.x = 0.0D;
    if ((actions & 0x400) != 0) {
      turning.z += dt * turningAccel.getNumberValue().doubleValue();
    } else if ((actions & 0x800) != 0) {
      turning.z -= dt * turningAccel.getNumberValue().doubleValue();
    } else if (Math.abs(turning.z) >= dt * turningAccel.getNumberValue().doubleValue()) {
      turning.z += (turning.z < 0.0D ? 1 : -1) * dt * turningAccel.getNumberValue().doubleValue();
    } else {
      turning.z = 0.0D;
    }
    


    if (speed.x < -maxSpeed.getNumberValue().doubleValue())
      speed.x = (-maxSpeed.getNumberValue().doubleValue());
    if (speed.x > maxSpeed.getNumberValue().doubleValue())
      speed.x = maxSpeed.getNumberValue().doubleValue();
    if (speed.y < -maxSpeed.getNumberValue().doubleValue())
      speed.y = (-maxSpeed.getNumberValue().doubleValue());
    if (speed.y > maxSpeed.getNumberValue().doubleValue())
      speed.y = maxSpeed.getNumberValue().doubleValue();
    if (speed.z < -maxSpeed.getNumberValue().doubleValue())
      speed.z = (-maxSpeed.getNumberValue().doubleValue());
    if (speed.z > maxSpeed.getNumberValue().doubleValue())
      speed.z = maxSpeed.getNumberValue().doubleValue();
    if (turning.x < -maxTurning.getNumberValue().doubleValue())
      turning.x = (-maxTurning.getNumberValue().doubleValue());
    if (turning.x > maxTurning.getNumberValue().doubleValue())
      turning.x = maxTurning.getNumberValue().doubleValue();
    if (turning.y < -maxTurning.getNumberValue().doubleValue())
      turning.y = (-maxTurning.getNumberValue().doubleValue());
    if (turning.y > maxTurning.getNumberValue().doubleValue())
      turning.y = maxTurning.getNumberValue().doubleValue();
    if (turning.z < -maxTurning.getNumberValue().doubleValue())
      turning.z = (-maxTurning.getNumberValue().doubleValue());
    if (turning.z > maxTurning.getNumberValue().doubleValue()) {
      turning.z = maxTurning.getNumberValue().doubleValue();
    }
    
    Vector3d vector = new Vector3d(dt * speed.x, dt * speed.y, dt * speed.z);
    Transformable subjectTransformable = subject.getTransformableValue();
    try {
      vector = subjectTransformable.preventPassingThroughOtherObjects(vector, 2.0D);
    }
    catch (Throwable localThrowable) {}
    
    subjectTransformable.moveRightNow(vector);
    










    subjectTransformable.turnRightNow(Direction.FORWARD, dt * turning.x);
    if (((Boolean)stayOnGround.get()).booleanValue()) {
      Transformable t = new Transformable();
      t.setPositionRightNow(subjectTransformable.getPosition(subjectTransformable.getWorld()));
      
      subjectTransformable.turnRightNow(Direction.RIGHT, dt * turning.y, t);
    } else {
      subjectTransformable.turnRightNow(Direction.RIGHT, dt * turning.y);
    }
    
    subjectTransformable.rollRightNow(Direction.LEFT, dt * turning.z);
  }
}

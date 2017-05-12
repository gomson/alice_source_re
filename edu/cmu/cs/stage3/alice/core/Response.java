package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

















public abstract class Response
  extends Code
{
  public Response() {}
  
  public final NumberProperty duration = new NumberProperty(this, "duration", getDefaultDuration());
  
  protected Number getDefaultDuration() {
    return new Double(1.0D);
  }
  
  protected Class getRuntimeResponseClass() {
    Class cls = getClass();
    Class[] declaredClasses = cls.getDeclaredClasses();
    for (int i = 0; i < declaredClasses.length; i++) {
      if (RuntimeResponse.class.isAssignableFrom(declaredClasses[i])) {
        return declaredClasses[i];
      }
    }
    return null;
  }
  
  public RuntimeResponse manufactureRuntimeResponse() { Class runtimeResponseClass = getRuntimeResponseClass();
    if (runtimeResponseClass != null) {
      Constructor[] constructors = runtimeResponseClass.getConstructors();
      if (constructors.length > 0) {
        try {
          Object[] initargs = { this };
          return (RuntimeResponse)constructors[0].newInstance(initargs);
        }
        catch (IllegalAccessException iae) {
          throw new ExceptionWrapper(iae, null);
        } catch (InstantiationException ie) {
          throw new ExceptionWrapper(ie, null);
        } catch (InvocationTargetException ite) {
          throw new ExceptionWrapper(ite, null);
        }
      }
    }
    return null; }
  
  public abstract class RuntimeResponse { public RuntimeResponse() {}
    
    private boolean HACK_m_isMarkedForRemoval = false;
    
    public void HACK_markForRemoval() { HACK_m_isMarkedForRemoval = true; }
    
    public boolean HACK_isMarkedForRemoval() {
      return HACK_m_isMarkedForRemoval;
    }
    

    private boolean m_isActive = false;
    private double m_t0 = NaN.0D;
    private double m_tPrev = NaN.0D;
    private double m_duration = NaN.0D;
    private double m_dt = NaN.0D;
    
    public boolean isActive() {
      return m_isActive;
    }
    
    protected double getDuration() { return m_duration; }
    
    protected void setDuration(double duration) {
      m_duration = duration;
    }
    
    protected double getTimeElapsed(double t) { return t - m_t0; }
    
    protected double getDT() {
      return m_dt;
    }
    
    public double getTimeRemaining(double t) { return m_duration - getTimeElapsed(t); }
    
    public void prologue(double t) {
      m_t0 = t;
      m_tPrev = t;
      m_dt = 0.0D;
      m_duration = duration.doubleValue(NaN.0D);
      m_isActive = true;
    }
    
    public void update(double t) { m_dt = (t - m_tPrev);
      m_tPrev = t;
    }
    
    public void epilogue(double t) { m_isActive = false; }
    
    public void stop(double t) {
      if (isActive())
        epilogue(t);
    }
    
    public void finish() {
      m_t0 = Double.NEGATIVE_INFINITY;
    }
    
    protected Behavior getCurrentBehavior() {
      World world = getWorld();
      if (world != null) {
        Sandbox sandbox = world.getCurrentSandbox();
        if (sandbox != null) {
          return sandbox.getCurrentBehavior();
        }
      }
      return null;
    }
    
    protected Stack getCurrentStack() {
      Behavior behavior = getCurrentBehavior();
      if (behavior != null) {
        return behavior.getCurrentStack();
      }
      return null;
    }
  }
}

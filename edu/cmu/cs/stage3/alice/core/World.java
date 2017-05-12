package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.bubble.Bubble;
import edu.cmu.cs.stage3.alice.core.bubble.BubbleManager;
import edu.cmu.cs.stage3.alice.core.event.MessageListener;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.scenegraph.AmbientLight;
import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.ExponentialFog;
import edu.cmu.cs.stage3.alice.scenegraph.LinearFog;
import edu.cmu.cs.stage3.alice.scenegraph.Scene;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.collision.CollisionManager;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory;
import edu.cmu.cs.stage3.alice.scripting.Code;
import edu.cmu.cs.stage3.alice.scripting.CompileType;
import edu.cmu.cs.stage3.alice.scripting.Interpreter;
import edu.cmu.cs.stage3.alice.scripting.ScriptingFactory;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Sphere;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class World extends ReferenceFrame
{
  private static boolean HACK_s_isPropetryListeningDisabledWhileWorldIsRunning = false;
  
  static {
    try { HACK_s_isPropetryListeningDisabledWhileWorldIsRunning = Boolean.getBoolean("alice.isPropetryListeningDisabledWhileWorldIsRunning");
    } catch (Throwable t) {
      HACK_s_isPropetryListeningDisabledWhileWorldIsRunning = false;
    }
  }
  
  public final ElementArrayProperty sandboxes = new ElementArrayProperty(this, "sandboxes", null, [Ledu.cmu.cs.stage3.alice.core.Sandbox.class);
  public final ElementArrayProperty groups = new ElementArrayProperty(this, "groups", null, [Ledu.cmu.cs.stage3.alice.core.Group.class);
  public final ColorProperty atmosphereColor = new ColorProperty(this, "atmosphereColor", Color.BLACK);
  public final ColorProperty ambientLightColor = new ColorProperty(this, "ambientLightColor", Color.WHITE);
  public final NumberProperty ambientLightBrightness = new NumberProperty(this, "ambientLightBrightness", new Double(0.2D));
  public final ObjectProperty fogStyle = new ObjectProperty(this, "fogStyle", FogStyle.NONE, FogStyle.class);
  public final NumberProperty fogDensity = new NumberProperty(this, "fogDensity", new Double(0.0D));
  public final NumberProperty fogNearDistance = new NumberProperty(this, "fogNearDistance", new Double(0.0D));
  public final NumberProperty fogFarDistance = new NumberProperty(this, "fogFarDistance", new Double(1.0D));
  
  public final ObjectArrayProperty bubbles = new ObjectArrayProperty(this, "bubbles", null, [Ledu.cmu.cs.stage3.alice.core.bubble.Bubble.class);
  
  public final NumberProperty speedMultiplier = new NumberProperty(this, "speedMultiplier", new Double(1.0D));
  
  private Scene m_sgScene;
  
  private AmbientLight m_sgAmbientLight;
  private Background m_sgBackground;
  private LinearFog m_sgLinearFog;
  private ExponentialFog m_sgExponentialFog;
  private CollisionManager m_collisionManager = new CollisionManager();
  private Visual[][] m_collisions = new Visual[0][];
  
  private Vector m_capsulePropertyValuePairs = new Vector();
  private Hashtable m_capsuleElements = new Hashtable();
  private RenderTargetFactory m_renderTargetFactory = null;
  
  private ScriptingFactory m_scriptingFactory;
  
  private Interpreter m_interpreter;
  
  private Vector m_messageListeners = new Vector();
  private MessageListener[] m_messageListenerArray = null;
  
  private Sandbox m_currentSandbox = null;
  
  private BubbleManager m_bubbleManager = new BubbleManager();
  
  private Clock m_clock = null;
  private boolean m_isRunning = false;
  
  public World()
  {
    m_sgScene = new Scene();
    m_sgScene.setBonus(this);
    m_sgBackground = new Background();
    m_sgBackground.setBonus(this);
    m_sgScene.setBackground(m_sgBackground);
    m_sgAmbientLight = new AmbientLight();
    m_sgAmbientLight.setBonus(this);
    m_sgAmbientLight.setParent(m_sgScene);
    m_sgLinearFog = new LinearFog();
    m_sgLinearFog.setBonus(this);
    m_sgExponentialFog = new ExponentialFog();
    m_sgExponentialFog.setBonus(this);
    atmosphereColor.set(m_sgBackground.getColor());
    ambientLightColor.set(m_sgAmbientLight.getColor());
    ambientLightBrightness.set(new Double(m_sgAmbientLight.getBrightness()));
    fogNearDistance.set(new Double(m_sgLinearFog.getNearDistance()));
    fogFarDistance.set(new Double(m_sgLinearFog.getFarDistance()));
    fogDensity.set(new Double(m_sgExponentialFog.getDensity()));
    fogStyle.set(FogStyle.NONE);
  }
  
  public Clock getClock() {
    return m_clock;
  }
  
  public void setClock(Clock clock) { m_clock = clock; }
  
  public BubbleManager getBubbleManager()
  {
    return m_bubbleManager;
  }
  
  private Interpreter getInterpreter() {
    if ((m_scriptingFactory != null) && 
      (m_interpreter == null)) {
      m_interpreter = m_scriptingFactory.manufactureInterpreter();
      m_interpreter.setWorld(this);
    }
    
    return m_interpreter;
  }
  
  public Code compile(String script, Object source, CompileType compileType) {
    return getInterpreter().compile(script, source, compileType);
  }
  
  public Object eval(Code code) {
    return getInterpreter().eval(code);
  }
  
  public void exec(Code code) {
    getInterpreter().exec(code);
  }
  

  public Visual[][] getCollisions() { return m_collisions; }
  
  public void addCollisionManagementFor(Transformable t) {
    if (t != null) {
      Visual[] sgVisuals = t.getAllSceneGraphVisuals();
      for (int i = 0; i < sgVisuals.length; i++) {
        Sphere sphere = sgVisuals[i].getBoundingSphere();
        if ((sphere != null) && (sphere.getRadius() > 0.0D)) {
          m_collisionManager.activateObject(sgVisuals[i]);
        }
      }
      for (int i = 0; i < sgVisuals.length; i++) {
        for (int j = i + 1; j < sgVisuals.length; j++) {
          Sphere sphereI = sgVisuals[i].getBoundingSphere();
          if ((sphereI != null) && (sphereI.getRadius() > 0.0D)) {
            Sphere sphereJ = sgVisuals[j].getBoundingSphere();
            if ((sphereJ != null) && (sphereJ.getRadius() > 0.0D)) {
              m_collisionManager.deactivatePair(sgVisuals[i], sgVisuals[j]);
            }
          }
        }
      }
    }
  }
  


  public void removeCollisionManagementFor(Transformable t) {}
  


  protected void internalRelease(int pass)
  {
    switch (pass) {
    case 1: 
      if (m_interpreter != null) {
        m_interpreter.release();
        m_interpreter = null;
      }
      if (m_sgExponentialFog != null) {
        m_sgExponentialFog.setParent(null);
      }
      if (m_sgLinearFog != null) {
        m_sgLinearFog.setParent(null);
      }
      m_sgAmbientLight.setParent(null);
      m_sgScene.setBackground(null);
      break;
    case 2: 
      m_sgScene.release();
      m_sgScene = null;
      m_sgAmbientLight.release();
      m_sgAmbientLight = null;
      m_sgBackground.release();
      m_sgBackground = null;
      if (m_sgExponentialFog != null) {
        m_sgExponentialFog.release();
        m_sgExponentialFog = null;
      }
      if (m_sgLinearFog != null) {
        m_sgLinearFog.release();
        m_sgLinearFog = null;
      }
      break;
    }
    super.internalRelease(pass);
  }
  
  public Scene getSceneGraphScene() {
    return m_sgScene;
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.Container getSceneGraphContainer() {
    return m_sgScene;
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame getSceneGraphReferenceFrame() {
    return m_sgScene;
  }
  
  public AmbientLight getSceneGraphAmbientLight() { return m_sgAmbientLight; }
  
  public Background getSceneGraphBackground() {
    return m_sgBackground;
  }
  
  public ExponentialFog getSceneGraphExponentialFog() { return m_sgExponentialFog; }
  
  public LinearFog getSceneGraphLinearFog() {
    return m_sgLinearFog;
  }
  

  public void addAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener) {}
  

  public void removeAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener) {}
  

  protected void nameValueChanged(String value)
  {
    super.nameValueChanged(value);
    if (value != null) {
      m_sgScene.setName(value + ".m_sgScene");
      m_sgBackground.setName(value + ".m_sgBackground");
      m_sgAmbientLight.setName(value + ".m_sgAmbientLight");
      m_sgExponentialFog.setName(value + ".m_sgExponentialFog");
      m_sgLinearFog.setName(value + ".m_sgLinearFog");
    } else {
      m_sgScene.setName(null);
      m_sgBackground.setName(null);
      m_sgAmbientLight.setName(null);
      m_sgExponentialFog.setName(null);
      m_sgLinearFog.setName(null);
    }
  }
  
  protected void atmosphereColorValueChanged(Color value) { m_sgBackground.setColor(value);
    m_sgLinearFog.setColor(value);
    m_sgExponentialFog.setColor(value);
  }
  
  protected void ambientLightColorValueChanged(Color value) { m_sgAmbientLight.setColor(value); }
  
  protected void ambientLightBrightnessValueChanged(Number value) {
    double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    m_sgAmbientLight.setBrightness(d);
  }
  
  protected void fogDensityValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    m_sgExponentialFog.setDensity(d);
  }
  
  protected void fogNearDistanceValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    m_sgLinearFog.setNearDistance(d);
  }
  
  protected void fogFarDistanceValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    m_sgLinearFog.setFarDistance(d);
  }
  
  protected void fogStyleValueChanged(FogStyle value) { if (value == FogStyle.EXPONENTIAL) {
      m_sgLinearFog.setParent(null);
      m_sgExponentialFog.setParent(m_sgScene);
    } else if (value == FogStyle.LINEAR) {
      m_sgLinearFog.setParent(m_sgScene);
      m_sgExponentialFog.setParent(null);
    } else {
      m_sgLinearFog.setParent(null);
      m_sgExponentialFog.setParent(null);
    }
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == atmosphereColor) {
      atmosphereColorValueChanged((Color)value);
    } else if (property == ambientLightColor) {
      ambientLightColorValueChanged((Color)value);
    } else if (property == ambientLightBrightness) {
      ambientLightBrightnessValueChanged((Number)value);
    } else if (property == fogStyle) {
      fogStyleValueChanged((FogStyle)value);
    } else if (property == fogDensity) {
      fogDensityValueChanged((Number)value);
    } else if (property == fogNearDistance) {
      fogNearDistanceValueChanged((Number)value);
    } else if (property == fogFarDistance) {
      fogFarDistanceValueChanged((Number)value);
    } else if (property == bubbles) {
      m_bubbleManager.setBubbles((Bubble[])bubbles.getArrayValue());
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public Matrix44 getTransformation(ReferenceFrame asSeenBy)
  {
    if (asSeenBy == null) {
      return Matrix44.IDENTITY;
    }
    return super.getTransformation(asSeenBy);
  }
  

  protected void internalFindAccessibleExpressions(Class cls, Vector v)
  {
    for (int i = 0; i < sandboxes.size(); i++) {
      Sandbox sandbox = (Sandbox)sandboxes.get(i);
      for (int j = 0; j < variables.size(); j++) {
        internalAddExpressionIfAssignableTo((Expression)variables.get(j), cls, v);
      }
      for (int j = 0; j < questions.size(); j++) {
        internalAddExpressionIfAssignableTo((Expression)questions.get(j), cls, v);
      }
    }
    super.internalFindAccessibleExpressions(cls, v);
  }
  
  public ScriptingFactory getScriptingFactory() {
    return m_scriptingFactory;
  }
  
  public void setScriptingFactory(ScriptingFactory scriptingFactory) { m_scriptingFactory = scriptingFactory; }
  



  public RenderTargetFactory getRenderTargetFactory() { return m_renderTargetFactory; }
  
  public void setRenderTargetFactory(RenderTargetFactory renderTargetFactory) {
    m_renderTargetFactory = renderTargetFactory;
    RenderTarget[] renderTargets = (RenderTarget[])getDescendants(RenderTarget.class);
    for (int i = 0; i < renderTargets.length; i++) {
      renderTargets[i].commit(m_renderTargetFactory);
    }
  }
  
  public void sendMessage(Element source, String message, Transformable fromWho, Transformable toWhom, long when) {
    edu.cmu.cs.stage3.alice.core.event.MessageEvent messageEvent = new edu.cmu.cs.stage3.alice.core.event.MessageEvent(source, message, fromWho, toWhom, when);
    for (int i = 0; i < m_messageListeners.size(); i++) {
      MessageListener messageListener = (MessageListener)m_messageListeners.elementAt(i);
      messageListener.messageSent(messageEvent);
    }
  }
  
  public void addMessageListener(MessageListener messageListener) { m_messageListeners.addElement(messageListener);
    m_messageListenerArray = null;
  }
  
  public void removeMessageListener(MessageListener messageListener) { m_messageListeners.removeElement(messageListener);
    m_messageListenerArray = null;
  }
  
  public MessageListener[] getMessageListeners() { if (m_messageListenerArray == null) {
      m_messageListenerArray = new MessageListener[m_messageListeners.size()];
      m_messageListeners.copyInto(m_messageListenerArray);
    }
    return m_messageListenerArray;
  }
  
  public void preserve() {
    m_capsulePropertyValuePairs.clear();
    m_capsuleElements.clear();
    Element[] elements = getDescendants();
    for (int i = 0; i < elements.length; i++) {
      Element element = elements[i];
      edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = element.getChildrenListeners();
      if ((childrenListeners != null) && (childrenListeners.length != 0)) {
        warnln("WARNING: " + element.getKey() + " has CHILDREN listeners: ");
        for (int j = 0; j < childrenListeners.length; j++) {
          warnln("\t" + childrenListeners[j].getClass());
        }
      }
      m_capsuleElements.put(element, Boolean.TRUE);
      Property[] properties = element.getProperties();
      for (int j = 0; j < properties.length; j++) {
        Property property = properties[j];
        edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propertyListeners = property.getPropertyListeners();
        if ((propertyListeners != null) && (propertyListeners.length != 0)) {
          warnln("WARNING: " + element.getKey() + "." + property.getName() + " has PROPERTY listeners: ");
          for (int k = 0; k < propertyListeners.length; k++) {
            warnln("\t" + propertyListeners[k].getClass());
          }
        }
        if ((property instanceof ObjectArrayProperty)) {
          ObjectArrayProperty objectArrayPropery = (ObjectArrayProperty)property;
          edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener[] objectArrayProperyListeners = objectArrayPropery.getObjectArrayPropertyListeners();
          if ((objectArrayProperyListeners != null) && (objectArrayProperyListeners.length != 0)) {
            warnln("WARNING: " + element.getKey() + "." + objectArrayPropery.getName() + " has OBJECT ARRAY PROPERTY listeners: ");
            for (int k = 0; k < objectArrayProperyListeners.length; k++) {
              warnln("\t" + objectArrayProperyListeners[k].getClass());
            }
          }
        }
        Object[] tuple = { property, property.get() };
        m_capsulePropertyValuePairs.addElement(tuple);
      }
    }
  }
  
  public void restore() { Enumeration preserves = m_capsulePropertyValuePairs.elements();
    while (preserves.hasMoreElements()) {
      Object[] tuple = (Object[])preserves.nextElement();
      Property property = (Property)tuple[0];
      Object value = tuple[1];
      property.set(value);
    }
    Element[] elements = getDescendants();
    for (int i = 0; i < elements.length; i++) {
      Element element = elements[i];
      if (m_capsuleElements.get(element) == null) {
        element.removeFromParent();
      }
    }
    for (int i = 0; i < elements.length; i++) {
      Element element = elements[i];
      if (m_capsuleElements.get(element) == null) {
        element.release();
      }
    }
  }
  

  protected void scheduleBehaviors(double t)
  {
    super.scheduleBehaviors(t);
    for (int i = 0; i < sandboxes.size(); i++) {
      m_currentSandbox = ((Sandbox)sandboxes.get(i));
      m_currentSandbox.scheduleBehaviors(t);
    }
  }
  


  public Sandbox getCurrentSandbox()
  {
    return m_currentSandbox;
  }
  





  public boolean isRunning()
  {
    return m_isRunning;
  }
  
  public void start() {
    if (HACK_s_isPropetryListeningDisabledWhileWorldIsRunning) {
      Property.HACK_disableListening();
    }
    if (m_scriptingFactory != null) {
      getInterpreter().start();
      Code code = script.getCode(CompileType.EXEC_MULTIPLE);
      if (code != null) {
        exec(code);
      }
    }
    bubbles.clear();
    started(this, m_clock.getTime());
    m_isRunning = true;
  }
  
  public void schedule() {
    m_currentSandbox = this;
    scheduleBehaviors(m_clock.getTime());
    m_collisions = m_collisionManager.update(256);
    m_currentSandbox = null;
  }
  
  public void stop() { bubbles.clear();
    m_isRunning = false;
    if (m_scriptingFactory != null) {
      getInterpreter().stop();
    }
    stopped(this, m_clock.getTime());
    if (HACK_s_isPropetryListeningDisabledWhileWorldIsRunning) {
      Property.HACK_enableListening();
    }
  }
}

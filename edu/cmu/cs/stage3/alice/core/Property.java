package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.io.KeepFileNotSupportedException;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.HowMuch;
import edu.cmu.cs.stage3.xml.NodeUtilities;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import javax.vecmath.Matrix4d;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class Property
{
  private static boolean HACK_s_isListeningEnabled = true;
  
  public static void HACK_enableListening() { HACK_s_isListeningEnabled = true; }
  
  public static void HACK_disableListening() {
    HACK_s_isListeningEnabled = false;
  }
  
  private boolean m_isAcceptingOfHowMuch = false;
  private Element m_owner;
  
  public boolean isAcceptingOfHowMuch() { return m_isAcceptingOfHowMuch; }
  
  public void setIsAcceptingOfHowMuch(boolean isAcceptingOfHowMuch) {
    m_isAcceptingOfHowMuch = isAcceptingOfHowMuch;
  }
  

  private String m_name;
  
  private Object m_defaultValue;
  
  private Class m_valueClass;
  private Object m_value;
  private Vector m_propertyListeners = new Vector();
  private PropertyListener[] m_propertyListenerArray = null;
  
  private boolean m_isDeprecated = false;
  
  protected Object m_associatedFileKey = null;
  
  protected Property(Element owner, String name, Object defaultValue, Class valueClass) {
    m_owner = owner;
    m_name = name;
    m_defaultValue = defaultValue;
    m_valueClass = valueClass;
    m_isAcceptingOfHowMuch = false;
    m_value = m_defaultValue;
    m_owner.propertyCreated(this);
  }
  
  private static Dictionary s_ownerClassMap = new Hashtable();
  
  public static String[] getPropertyNames(Class ownerClass, Class valueClass) { Dictionary valueClassMap = (Dictionary)s_ownerClassMap.get(ownerClass);
    if (valueClassMap == null) {
      valueClassMap = new Hashtable();
      s_ownerClassMap.put(ownerClass, valueClassMap);
    }
    String[] propertyNameArray = (String[])valueClassMap.get(valueClass);
    if (propertyNameArray == null) {
      Vector propertyNames = new Vector();
      Field[] fields = ownerClass.getFields();
      for (int i = 0; i < fields.length; i++) {
        if (Property.class.isAssignableFrom(fields[i].getType())) {
          String propertyName = fields[i].getName();
          Class cls = Element.getValueClassForPropertyNamed(ownerClass, propertyName);
          if (cls != null) {
            if (valueClass.isAssignableFrom(cls)) {
              propertyNames.addElement(propertyName);
            }
          } else {
            System.err.println(ownerClass + " " + propertyName);
          }
        }
      }
      propertyNameArray = new String[propertyNames.size()];
      propertyNames.copyInto(propertyNameArray);
      valueClassMap.put(valueClass, propertyNameArray);
    }
    return propertyNameArray;
  }
  

  public static String[] getPropertyNames(Class ownerClass) { return getPropertyNames(ownerClass, Object.class); }
  
  public boolean isAlsoKnownAs(Class cls, String name) {
    if (cls.isAssignableFrom(m_owner.getClass())) {
      try {
        Field field = cls.getField(name);
        Object o = field.get(m_owner);
        return o == this;
      }
      catch (NoSuchFieldException localNoSuchFieldException) {}catch (IllegalAccessException localIllegalAccessException) {}
    }
    
    return false;
  }
  

  public Element getOwner() { return m_owner; }
  /**
   * @deprecated
   */
  public Element getElement() { return getOwner(); }
  
  public Class getValueClass() {
    return m_valueClass;
  }
  
  protected void setValueClass(Class valueClass) {
    m_valueClass = valueClass;
  }
  
  public Object getDefaultValue() { return m_defaultValue; }
  
  public String getName() {
    return m_name;
  }
  
  public void addPropertyListener(PropertyListener propertyListener)
  {
    if (!m_propertyListeners.contains(propertyListener))
    {

      m_propertyListeners.addElement(propertyListener);
      m_propertyListenerArray = null;
    }
  }
  
  public void removePropertyListener(PropertyListener propertyListener) { m_propertyListeners.removeElement(propertyListener);
    m_propertyListenerArray = null;
  }
  
  public PropertyListener[] getPropertyListeners() { if (m_propertyListenerArray == null) {
      m_propertyListenerArray = new PropertyListener[m_propertyListeners.size()];
      m_propertyListeners.copyInto(m_propertyListenerArray);
    }
    return m_propertyListenerArray;
  }
  
  public Class getDeclaredClass()
  {
    if (m_owner != null) {
      Class cls = m_owner.getClass();
      while (cls != null) {
        try {
          Field field = cls.getDeclaredField(m_name);
          if (field.get(m_owner) == this) {
            return cls;
          }
          throw new RuntimeException(m_owner + " " + Messages.getString("has_field_named_") + m_name + " " + Messages.getString("that_is_not_") + this);
        }
        catch (NoSuchFieldException nsfe) {
          cls = cls.getSuperclass();
        } catch (IllegalAccessException iae) {
          throw new ExceptionWrapper(iae, null);
        }
      }
      return null;
    }
    return null;
  }
  


  public Object get()
  {
    if ((m_value instanceof Cloneable)) {
      if (m_value.getClass().isArray())
      {
        return m_value;
      }
      try {
        Class[] parameterTypes = new Class[0];
        Object[] parameterValues = new Object[0];
        Method method = m_value.getClass().getMethod("clone", parameterTypes);
        if (method.isAccessible()) {
          return method.invoke(m_value, parameterValues);
        }
        return m_value;
      }
      catch (NoSuchMethodException nsme) {
        Element.warnln(Messages.getString("property_get_failure_to_clone__") + this + " " + nsme);
        
        return m_value;
      } catch (IllegalAccessException iae) {
        Element.warnln(Messages.getString("property_get_failure_to_clone__") + this + " " + iae);
        
        return m_value;
      } catch (InvocationTargetException ite) {
        Element.warnln(Messages.getString("property_get_failure_to_clone__") + this + " " + ite);
        
        return m_value;
      }
    }
    
    return m_value;
  }
  
  private boolean isValueInADifferentWorld(Object value)
  {
    World world = getElement().getWorld();
    if ((world != null) && 
      ((value instanceof Element))) {
      Element element = (Element)value;
      return world != getElement().getWorld();
    }
    
    return false;
  }
  
  public void checkForBadReferences(Object value) {
    if ((value instanceof Object[])) {
      Object[] array = (Object[])value;
      for (int i = 0; i < array.length; i++) {
        if (isValueInADifferentWorld(array[i])) {
          throw new IllegalArrayPropertyValueException(this, i, value, Messages.getString("value_must_be_in_world"));
        }
      }
    }
    else if (isValueInADifferentWorld(value)) {
      throw new IllegalPropertyValueException(this, value, Messages.getString("value_must_be_in_world"));
    }
  }
  

  protected void checkValueType(Object value)
  {
    if (value != null) {
      Class valueClass = getValueClass();
      if ((value instanceof Expression)) {
        Expression expression = (Expression)value;
        
        if (!valueClass.isAssignableFrom(Expression.class))
        {
          if (!valueClass.isAssignableFrom(Variable.class))
          {
            if (!valueClass.isAssignableFrom(Question.class))
            {

              if (!Question.class.isAssignableFrom(valueClass))
              {

                if (expression.getValueClass() != null) {
                  valueClass.isAssignableFrom(expression.getValueClass());
                }
                
              }
            }
          }
        }
      }
      else if (!valueClass.isAssignableFrom(value.getClass()))
      {

        throw new IllegalPropertyValueException(this, value, Messages.getString("Cannot_set_property_") + getName() + " " + Messages.getString("on_") + getOwner() + ".  " + valueClass + " " + Messages.getString("is_not_assignable_from_") + value.getClass());
      }
    }
  }
  























































  protected boolean getValueOfExpression()
  {
    Class valueClass = getValueClass();
    if ((valueClass.equals(List.class)) || (valueClass.equals(Object.class)))
      return true;
    if (valueClass.isAssignableFrom(Expression.class))
      return false;
    if (valueClass.isAssignableFrom(Variable.class))
      return false;
    if (valueClass.isAssignableFrom(Question.class)) {
      return false;
    }
    return true;
  }
  
  private static Behavior m_currentBehavior = null;
  
  protected Object evaluateIfNecessary(Object o) {
    if ((o instanceof Expression)) {
      Expression expression = (Expression)o;
      if ((m_currentBehavior == null) && 
        (!isDeprecated()))
      {

        if ((expression instanceof Variable)) {
          Element owner = getOwner();
          if (owner != null) {
            World world = owner.getWorld();
            if (world != null) {
              Sandbox sandbox = world.getCurrentSandbox();
              if (sandbox != null) {
                m_currentBehavior = sandbox.getCurrentBehavior();
                if (m_currentBehavior != null) {
                  Variable runtimeVariable = m_currentBehavior.stackLookup((Variable)expression);
                  if (runtimeVariable != null) {
                    expression = runtimeVariable;
                  }
                }
              }
            }
          }
        }
      }
      Object value;
      Object value;
      if (getValueOfExpression()) {
        value = expression.getValue();
      } else {
        value = expression;
      }
      














      m_currentBehavior = null;
      
      return value;
    }
    return o;
  }
  

  public Object getValue() { return evaluateIfNecessary(m_value); }
  
  private void onChanging(PropertyEvent propertyEvent) {
    m_owner.propertyChanging(propertyEvent);
    if (HACK_s_isListeningEnabled) {
      PropertyListener[] propertyListeners = getPropertyListeners();
      for (int i = 0; i < propertyListeners.length; i++) {
        propertyListeners[i].propertyChanging(propertyEvent);
      }
    }
  }
  

  private void onChanged(PropertyEvent propertyEvent)
  {
    getElement().markKeepKeyDirty();
    m_owner.propertyChanged(propertyEvent);
    if (HACK_s_isListeningEnabled) {
      PropertyListener[] propertyListeners = getPropertyListeners();
      for (int i = 0; i < propertyListeners.length; i++) {
        propertyListeners[i].propertyChanged(propertyEvent);
      }
    }
  }
  
  protected void onSet(Object value)
  {
    Class valueClass = getValueClass();
    PropertyEvent propertyEvent = new PropertyEvent(this, value);
    onChanging(propertyEvent);
    if (((m_value instanceof Variable[])) && ((value instanceof Variable))) {
      Variable[] temp = (Variable[])m_value;
      for (int i = 0; i < temp.length; i++) {
        if (temp[i] == null) {
          temp[i] = ((Variable)value);
          break;
        }
      }
      m_value = temp;
    } else {
      m_value = value;
    }
    onChanged(propertyEvent);
    m_associatedFileKey = null;
  }
  
  public void set(Object value) throws IllegalArgumentException { if (m_value == null) {
      if (value != null) {}

    }
    else if (m_value.equals(value)) {
      return;
    }
    if (!Element.s_isLoading)
    {

      checkValueType(value);
      checkForBadReferences(value);
    }
    onSet(value);
  }
  
  private static void setHowMuch(Element owner, String propertyName, Object value, HowMuch howMuch) {
    Property property = owner.getPropertyNamed(propertyName);
    if (property != null) {
      property.set(value);
    }
    if (howMuch.getDescend()) {
      for (int i = 0; i < owner.getChildCount(); i++) {
        Element child = owner.getChildAt(i);
        if ((!isFirstClass.booleanValue()) || (!howMuch.getRespectDescendant()))
        {

          setHowMuch(child, propertyName, value, howMuch);
        }
      }
    }
  }
  
  public void set(Object value, HowMuch howMuch) throws IllegalArgumentException {
    if ((m_owner instanceof Element)) {
      setHowMuch(m_owner, m_name, value, howMuch);
    }
  }
  



























































  protected Object getValueOf(Class type, String text)
  {
    if (type.equals(Double.class)) {
      if (text.equals("Infinity"))
        return new Double(Double.POSITIVE_INFINITY);
      if (text.equals("NaN")) {
        return new Double(NaN.0D);
      }
      return Double.valueOf(text);
    }
    if (type.equals(String.class)) {
      return text;
    }
    try {
      if (type.equals(Matrix4d.class)) {
        String[] t = text.split(",|\n");
        Matrix44 m = new Matrix44();
        m00 = Double.valueOf(t[0]).doubleValue();
        m01 = Double.valueOf(t[1]).doubleValue();
        m02 = Double.valueOf(t[2]).doubleValue();
        m03 = Double.valueOf(t[3]).doubleValue();
        m10 = Double.valueOf(t[4]).doubleValue();
        m11 = Double.valueOf(t[5]).doubleValue();
        m12 = Double.valueOf(t[6]).doubleValue();
        m13 = Double.valueOf(t[7]).doubleValue();
        m20 = Double.valueOf(t[8]).doubleValue();
        m21 = Double.valueOf(t[9]).doubleValue();
        m22 = Double.valueOf(t[10]).doubleValue();
        m23 = Double.valueOf(t[11]).doubleValue();
        m30 = Double.valueOf(t[12]).doubleValue();
        m31 = Double.valueOf(t[13]).doubleValue();
        m32 = Double.valueOf(t[14]).doubleValue();
        m33 = Double.valueOf(t[15]).doubleValue();
        return m;
      }
      Class[] parameterTypes = { String.class };
      Method valueOfMethod = type.getMethod("valueOf", parameterTypes);
      int modifiers = valueOfMethod.getModifiers();
      if ((Modifier.isPublic(modifiers)) && (Modifier.isStatic(modifiers))) {
        Object[] parameters = { text };
        return valueOfMethod.invoke(null, parameters);
      }
      throw new RuntimeException(Messages.getString("valueOf_method_not_public_static_"));
    }
    catch (NoSuchMethodException nsme)
    {
      throw new RuntimeException("NoSuchMethodException:" + type);
    } catch (IllegalAccessException iae) {
      throw new RuntimeException("IllegalAccessException: " + type);
    } catch (InvocationTargetException ite) {
      throw new RuntimeException("java.lang.reflect.InvocationTargetException: " + type + " " + text);
    }
  }
  
  protected String getNodeText(Node node)
  {
    return NodeUtilities.getNodeText(node);
  }
  
  protected Node createNodeForString(Document document, String s) {
    char[] cdataCharacters = { ' ', '\t', '\n', '"', '\'', '>', '<', '&' };
    for (int i = 0; i < cdataCharacters.length; i++) {
      if (s.indexOf(cdataCharacters[i]) != -1) {
        return document.createCDATASection(s);
      }
    }
    return document.createTextNode(s);
  }
  
  protected String getFilename(String text) {
    String[] markers = { "java.io.File[", "]" };
    int begin = text.indexOf(markers[0]) + markers[0].length();
    int end = text.lastIndexOf(markers[1]);
    return text.substring(begin, end);
  }
  
  protected void decodeReference(org.w3c.dom.Element node, Vector referencesToBeResolved, double version, String typeName) {
    try { Class type = Class.forName(typeName);
      String text = getNodeText(node);
      
      if (text.equals("."))
        text = "";
      Criterion criterion;
      Criterion criterion;
      if (type.isAssignableFrom(InternalReferenceKeyedCriterion.class)) {
        criterion = new InternalReferenceKeyedCriterion(text); } else { Criterion criterion;
        if (type.isAssignableFrom(ExternalReferenceKeyedCriterion.class)) {
          criterion = new ExternalReferenceKeyedCriterion(text);
        } else {
          criterion = (Criterion)getValueOf(type, text);
        }
      }
      




      referencesToBeResolved.addElement(new PropertyReference(this, criterion));
    } catch (ClassNotFoundException cnfe) {
      throw new RuntimeException(typeName);
    }
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException {
    String typeName = node.getAttribute("class");
    if (typeName.length() > 0) {
      String text = getNodeText(node);
      try {
        Class type = Class.forName(typeName);
        Object t = getValueOf(type, text);
        set(t);
      } catch (ClassNotFoundException cnfe) {
        throw new RuntimeException(typeName);
      }
    } else {
      System.err.println(this);
      throw new RuntimeException();
    }
  }
  
  public final void decode(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException { if (node.hasChildNodes()) {
      String criterionClassname = node.getAttribute("criterionClass");
      if (criterionClassname.length() > 0) {
        decodeReference(node, referencesToBeResolved, version, criterionClassname);
      } else {
        decodeObject(node, loader, referencesToBeResolved, version);
      }
    } else {
      set(null);
    }
  }
  

  protected void encodeReference(Document document, org.w3c.dom.Element node, ReferenceGenerator referenceGenerator, Element owner)
  {
    Criterion criterion = referenceGenerator.generateReference(owner);
    if (criterion != null) {
      node.setAttribute("criterionClass", criterion.getClass().getName());
      String s;
      String s; if ((criterion instanceof InternalReferenceKeyedCriterion)) {
        s = ((InternalReferenceKeyedCriterion)criterion).getKey(); } else { String s;
        if ((criterion instanceof ExternalReferenceKeyedCriterion)) {
          s = ((ExternalReferenceKeyedCriterion)criterion).getKey();
        } else {
          s = criterion.toString();
        }
      }
      if (s.length() == 0) {
        s = ".";
      }
      node.appendChild(createNodeForString(document, s));
    }
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator)
    throws IOException
  {
    Object o = get();
    node.setAttribute("class", o.getClass().getName());
    node.appendChild(createNodeForString(document, o.toString()));
  }
  
  public final void encode(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException { Object o = get();
    if (o != null) {
      if ((o instanceof Element)) {
        encodeReference(document, node, referenceGenerator, (Element)o);
      } else {
        encodeObject(document, node, storer, referenceGenerator);
      }
    }
  }
  
  public void keepAnyAssociatedFiles(DirectoryTreeStorer storer) throws KeepFileNotSupportedException, edu.cmu.cs.stage3.io.KeepFileDoesNotExistException
  {}
  
  public boolean isDeprecated() {
    return m_isDeprecated;
  }
  
  public void deprecate() { m_isDeprecated = true; }
  
  public String toString()
  {
    return getClass().getName() + "[name=" + m_name + ",owner=" + m_owner + "]";
  }
}

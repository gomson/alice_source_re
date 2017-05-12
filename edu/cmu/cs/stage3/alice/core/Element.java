package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.alice.scripting.Code;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.io.KeepFileNotSupportedException;
import edu.cmu.cs.stage3.io.ZipTreeLoader;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.HowMuch;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

public abstract class Element
{
  private static Hashtable s_classnameMap = new Hashtable();
  public static final double VERSION = 2.001D;
  
  static {
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.ConditionalLoopSequentialResponse", edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.ConditionalSequentialResponse", edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.CountLoopSequentialResponse", edu.cmu.cs.stage3.alice.core.response.LoopNInOrder.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.ForEachInListSequentialResponse", edu.cmu.cs.stage3.alice.core.response.ForEach.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.OrientationAnimation", edu.cmu.cs.stage3.alice.core.response.ForwardVectorAnimation.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.ParallelForEachInListSequentialResponse", edu.cmu.cs.stage3.alice.core.response.ForEachTogether.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.ParallelResponse", edu.cmu.cs.stage3.alice.core.response.DoTogether.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.ProxyForScriptDefinedResponse", edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.SequentialForEachInListSequentialResponse", edu.cmu.cs.stage3.alice.core.response.ForEachInOrder.class);
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.SequentialResponse", edu.cmu.cs.stage3.alice.core.response.DoInOrder.class);
    
    s_classnameMap.put("edu.cmu.cs.stage3.bb2.navigation.KeyboardNavigationBehavior", edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior.class);
    s_classnameMap.put("edu.cmu.cs.stage3.bb2.navigation.MouseNavigationBehavior", edu.cmu.cs.stage3.alice.core.behavior.MouseLookingBehavior.class);
    
    s_classnameMap.put("edu.cmu.cs.stage3.pratt.pose.Pose", Pose.class);
    s_classnameMap.put("edu.cmu.cs.stage3.pratt.pose.PoseAnimation", edu.cmu.cs.stage3.alice.core.response.PoseAnimation.class);
    
    s_classnameMap.put("edu.cmu.cs.stage3.alice.core.response.MetaResponse", edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class);
    
    s_classnameMap.put("edu.cmu.cs.stage3.bb2.navigation.KeyMapping", edu.cmu.cs.stage3.alice.core.navigation.KeyMapping.class); }
  
  public static final char SEPARATOR = '.';
  private static final String XML_FILENAME = "elementData.xml";
  
  public Element() {}
  
  private static int s_loadProgress = 0;
  
  public final StringProperty name = new StringProperty(this, "name", null);
  public final BooleanProperty isFirstClass = new BooleanProperty(this, "isFirstClass", Boolean.FALSE);
  public final edu.cmu.cs.stage3.alice.core.property.DictionaryProperty data = new edu.cmu.cs.stage3.alice.core.property.DictionaryProperty(this, "data", null);
  
  private Element m_parent = null;
  private Vector m_children = new Vector();
  private Element[] m_childArray = null;
  private Vector m_childrenListeners = new Vector();
  private ChildrenListener[] m_childrenListenerArray = null;
  
  private Object m_xmlFileKeepKey = null;
  
  public static boolean s_isLoading = false;
  
  public void markKeepKeyDirty() {
    if (!s_isLoading)
    {

      m_xmlFileKeepKey = null;
    }
  }
  
  private Property[] m_propertyArray = null;
  
  private boolean m_isReleased = false;
  private boolean m_updateParentsChildren = true;
  
  public Code compile(String script, Object source, edu.cmu.cs.stage3.alice.scripting.CompileType compileType) {
    return getWorld().compile(script, source, compileType);
  }
  
  public Object eval(Code code) { return getWorld().eval(code); }
  
  public void exec(Code code) {
    getWorld().exec(code);
  }
  
  private static boolean isPropertyField(Field field) {
    int modifiers = field.getModifiers();
    if ((java.lang.reflect.Modifier.isPublic(modifiers)) && 
      (Property.class.isAssignableFrom(field.getType()))) {
      return true;
    }
    
    return false;
  }
  
  private static final char[] ILLEGAL_NAME_CHARACTERS = { '\t', '\n', '\\', '/', ':', '*', '?', '"', '<', '>', '|', '.' };
  
  private static boolean isIllegal(char c) {
    for (int i = 0; i < ILLEGAL_NAME_CHARACTERS.length; i++) {
      if (c == ILLEGAL_NAME_CHARACTERS[i]) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean is8Bit(char c) { int n = c;
    return (n & 0xFF) == n;
  }
  
  public static String generateValidName(String invalidName) { char[] bytes = invalidName.trim().toCharArray();
    StringBuffer sb = new StringBuffer(bytes.length);
    for (int i = 0; i < bytes.length; i++) {
      char c = bytes[i];
      if (is8Bit(c)) {
        if (isIllegal(c)) {
          c = '_';
        }
        
      }
      else {
        c = '_';
      }
      sb.append(c);
    }
    if (sb.length() == 0) {
      sb.append('_');
    }
    return sb.toString();
  }
  





















  public static boolean isPotentialNameValid(String nameValue) { return edu.cmu.cs.stage3.alice.authoringtool.AikMin.isValidName(nameValue); }
  
  private void checkForInvalidName(String nameValue) {
    if (nameValue != null)
    {

      String trimmedNameValue = nameValue.trim();
      if (trimmedNameValue.length() != nameValue.length())
      {
        throw new IllegalNameValueException(nameValue, Messages.getString("We_re_sorry__but_names_in_Alice_may_not_have_spaces_at_the_beginning_or_end_"));
      }
      if (nameValue.length() == 0) {
        throw new IllegalNameValueException(nameValue, Messages.getString("We_re_sorry__but_names_in_Alice_may_not_be_empty_"));
      }
      char[] illegalCharacters = { '\t', '\n', '\\', '/', ':', '*', '?', '"', '<', '>', '|', '.' };
      for (int i = 0; i < illegalCharacters.length; i++) {
        if (nameValue.indexOf(illegalCharacters[i]) != -1)
        {
          throw new IllegalNameValueException(nameValue, Messages.getString("We_re_sorry__but_names_in_Alice_may_only_contain_letters_and_numbers___The_character__") + illegalCharacters[i] + Messages.getString("__can_not_be_used_in_a_name_"));
        }
      }
      


      if (!edu.cmu.cs.stage3.alice.authoringtool.AikMin.isValidName(nameValue))
      {







        throw new IllegalNameValueException(nameValue, Messages.getString("We_re_sorry__but_names_in_Alice_may_only_contain_letters_and_numbers___The_character__") + "??" + Messages.getString("__can_not_be_used_in_a_name_"));
      }
    }
  }
  
  private void checkForNameCollision(Element parentValue, String nameValue) {
    if ((parentValue != null) && (nameValue != null))
    {

      Element siblingToBe = parentValue.getChildNamedIgnoreCase(nameValue);
      

      if ((siblingToBe != null) && (siblingToBe != this)) {
        throw new IllegalNameValueException(nameValue, Messages.getString("Unfortunately__something_else_in_this_world_is_already_named__") + nameValue + Messages.getString("___so_you_can_t_use_that_name_here_"));
      }
    }
  }
  
  public void HACK_nameChanged() {
    markKeepKeyDirty();
    Element[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      children[i].HACK_nameChanged();
    }
  }
  
  protected void nameValueChanging(String nameValueToBe) {
    checkForInvalidName(nameValueToBe);
    checkForNameCollision(m_parent, nameValueToBe);
  }
  
  protected void nameValueChanged(String value) { PropertyReference[] propertyReferences = getExternalPropertyReferences(HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
    for (int i = 0; i < propertyReferences.length; i++) {
      propertyReferences[i].getProperty().getOwner().markKeepKeyDirty();
    }
  }
  

  public void propertyCreated(Property property) { markKeepKeyDirty(); }
  
  protected void propertyChanging(Property property, Object value) {
    if (property == name)
      nameValueChanging((String)value);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == name)
      nameValueChanged((String)value);
  }
  
  public final void propertyChanging(PropertyEvent propertyEvent) {
    if (isReleased()) {
      throw new RuntimeException(Messages.getString("property_change_attempted_on_released_element__") + propertyEvent.getProperty());
    }
    propertyChanging(propertyEvent.getProperty(), propertyEvent.getValue());
  }
  
  public final void propertyChanged(PropertyEvent propertyEvent) { if (isReleased()) {
      throw new RuntimeException(Messages.getString("property_changed_on_released_element") + propertyEvent.getProperty());
    }
    propertyChanged(propertyEvent.getProperty(), propertyEvent.getValue());
    if (propertyEvent.getProperty() == name) {
      getRoot().HACK_nameChanged();
    }
  }
  
  protected void internalRelease(int pass) {
    m_isReleased = true;
    Element[] children = getChildren();
    for (int i = 0; i < children.length; i++)
      children[i].internalRelease(pass);
  }
  
  public final void release() {
    if (!m_isReleased) {
      for (int pass = 0; pass < 3; pass++) {
        internalRelease(pass);
      }
    }
  }
  
  public boolean isReleased() { return m_isReleased; }
  
  protected void finalize() throws Throwable {
    if (!isReleased()) {
      release();
    }
    super.finalize();
  }
  
  private class Property_Value {
    private Property m_property;
    private Element m_value;
    
    public Property_Value(Property property, Element value) { m_property = property;
      m_value = value;
    }
    
    public Property getProperty(Element[] originals, Element[] replacements) { Element propertyOwner = m_property.getOwner();
      int index = propertyOwner.indexIn(originals);
      if (index != -1) {
        return replacements[index].getPropertyNamed(m_property.getName());
      }
      return m_property;
    }
    
    public Element getValue(Element[] originals, Element[] replacements) {
      if ((m_value instanceof Element)) {
        int index = m_value.indexIn(originals);
        if (index != -1) {
          return replacements[index];
        }
      }
      return m_value;
    }
    
    public String toString() { return m_property + " " + m_value; }
  }
  
  private class ObjectArrayProperty_Value_Index extends Element.Property_Value {
    private int m_index;
    
    public ObjectArrayProperty_Value_Index(ObjectArrayProperty objectArrayProperty, Element value, int index) { super(objectArrayProperty, value);
      m_index = index;
    }
    
    public ObjectArrayProperty getObjectArrayProperty(Element[] originals, Element[] replacements) { return (ObjectArrayProperty)getProperty(originals, replacements); }
    
    public int getIndex() {
      return m_index;
    }
    
    public String toString() { return super.toString() + " " + m_index; }
  }
  
  private int indexIn(Element[] array)
  {
    for (int i = 0; i < array.length; i++) {
      if (this == array[i]) {
        return i;
      }
    }
    return -1;
  }
  
  private void clearAllReferences(Element[] originals, Element[] replacements, Element[] childrenWithNoReplacements, Vector toBeResolved) { Element root = getRoot();
    Element[] descendants = root.getDescendants(Element.class, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
    for (int i = 0; i < descendants.length; i++) {
      Element descendant = descendants[i];
      Property[] properties = descendant.getProperties();
      for (int j = 0; j < properties.length; j++) {
        Property property = properties[j];
        if ((properties[j] instanceof ObjectArrayProperty)) {
          ObjectArrayProperty oap = (ObjectArrayProperty)property;
          for (int k = 0; k < oap.size(); k++) {
            Object valueK = oap.get(k);
            if ((valueK instanceof Element)) {
              Element elementK = (Element)valueK;
              ObjectArrayProperty_Value_Index oap_v_i = null;
              int index = elementK.indexIn(originals);
              if (index != -1) {
                oap_v_i = new ObjectArrayProperty_Value_Index(oap, replacements[index], k);
              } else {
                index = elementK.indexIn(childrenWithNoReplacements);
                if (index != -1) {
                  oap_v_i = new ObjectArrayProperty_Value_Index(oap, childrenWithNoReplacements[index], k);
                }
              }
              if (oap_v_i != null) {
                toBeResolved.addElement(oap_v_i);
                oap.set(k, null);
              }
            }
          }
        } else {
          Object value = properties[j].get();
          if ((value instanceof Element)) {
            Element element = (Element)value;
            Property_Value p_v = null;
            int index = element.indexIn(originals);
            if (index != -1) {
              p_v = new Property_Value(property, replacements[index]);
            } else {
              index = element.indexIn(childrenWithNoReplacements);
              if (index != -1) {
                p_v = new Property_Value(property, childrenWithNoReplacements[index]);
              }
            }
            if (p_v != null) {
              toBeResolved.addElement(p_v);
              property.set(null);
            }
          }
        }
      }
    }
  }
  
  private void replace(Element[] originals, Element[] replacements) {
    for (int i = 0; i < originals.length; i++) {
      Element original = originals[i];
      Element replacement = replacements[i];
      Element parent;
      if (original != null) {
        Element parent = original.getParent();
        original.setParent(null);
      }
      else {
        System.err.println(Messages.getString("WARNING__original_is_null_for_") + replacement);
        parent = null;
      }
      if (parent != null) {
        int index = parent.indexIn(originals);
        if (index != -1) {
          parent = replacements[index];
        }
      }
      replacement.setParent(parent);
    }
  }
  
  private Element[] getChildrenThatHaveNoReplacements(Element[] originals, Element[] replacements) {
    Vector vector = new Vector();
    for (int i = 0; i < originals.length; i++) {
      Element original = originals[i];
      Element replacement = replacements[i];
      if (original != null) {
        for (int j = 0; j < original.getChildCount(); j++) {
          Element childJ = original.getChildAt(j);
          int index = childJ.indexIn(originals);
          if (index == -1) {
            vector.addElement(childJ);
          }
        }
      }
    }
    Element[] array = new Element[vector.size()];
    vector.copyInto(array);
    return array;
  }
  








  private void addChildrenThatHaveNoReplacement(Element[] originals, Element[] replacements, Element[] childrenThatHaveNoReplacements)
  {
    for (int i = 0; i < originals.length; i++) {
      Element original = originals[i];
      Element replacement = replacements[i];
      if (original != null) {
        for (int j = 0; j < original.getChildCount(); j++) {
          Element childJ = original.getChildAt(j);
          int index = childJ.indexIn(originals);
          if (index == -1) {
            childJ.setParent(replacement);
          }
        }
      }
    }
  }
  
  public void replaceWith(Element replacement) {
    Element[] replacements = replacement.getDescendants(Element.class, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
    Element[] originals = new Element[replacements.length];
    for (int i = 0; i < replacements.length; i++) {
      String replacementKey = replacements[i].getKey(replacement);
      originals[i] = getDescendantKeyed(replacementKey);
    }
    
    Element[] childrenThatHaveNoReplacements = getChildrenThatHaveNoReplacements(originals, replacements);
    Vector toBeResolved = new Vector();
    clearAllReferences(originals, replacements, childrenThatHaveNoReplacements, toBeResolved);
    
    replace(originals, replacements);
    
    addChildrenThatHaveNoReplacement(originals, replacements, childrenThatHaveNoReplacements);
    
    for (int i = 0; i < toBeResolved.size(); i++) {
      Property_Value p_v = (Property_Value)toBeResolved.elementAt(i);
      if ((p_v instanceof ObjectArrayProperty_Value_Index)) {
        ObjectArrayProperty_Value_Index oap_v_i = (ObjectArrayProperty_Value_Index)p_v;
        oap_v_i.getObjectArrayProperty(originals, replacements).set(oap_v_i.getIndex(), oap_v_i.getValue(originals, replacements));
      } else {
        p_v.getProperty(originals, replacements).set(p_v.getValue(originals, replacements));
      }
    }
  }
  
  public Class[] getSupportedCoercionClasses() { return null; }
  
  public boolean isCoercionSupported() {
    Class[] classes = getSupportedCoercionClasses();
    return (classes != null) && (classes.length > 0);
  }
  
  public Element coerceTo(Class cls) { World world = getWorld();
    PropertyReference[] propertyReferences = new PropertyReference[0];
    String[] keys = new String[0];
    if (world != null) {
      propertyReferences = world.getPropertyReferencesTo(this, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, false);
      keys = new String[propertyReferences.length];
    }
    for (int i = 0; i < propertyReferences.length; i++) {
      PropertyReference propertyReference = propertyReferences[i];
      Element reference = propertyReference.getReference();
      keys[i] = reference.getKey(world);
      if ((propertyReference instanceof ObjectArrayPropertyReference)) {
        ObjectArrayPropertyReference objectArrayPropertyReference = (ObjectArrayPropertyReference)propertyReference;
        ObjectArrayProperty objectArrayProperty = objectArrayPropertyReference.getObjectArrayProperty();
        objectArrayProperty.set(objectArrayPropertyReference.getIndex(), null);
      } else {
        Property property = propertyReference.getProperty();
        property.set(null);
      }
    }
    
    try
    {
      coercedElement = (Element)cls.newInstance();
    } catch (IllegalAccessException iae) { Element coercedElement;
      throw new ExceptionWrapper(iae, cls.toString());
    } catch (InstantiationException ie) {
      throw new ExceptionWrapper(ie, cls.toString()); }
    Element coercedElement;
    Element parentValue = getParent();
    if (parentValue != null) {
      int indexOfChild = parentValue.getIndexOfChild(this);
      setParent(null);
      parentValue.insertChildAt(coercedElement, indexOfChild);
    }
    
    Element[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      children[i].setParent(coercedElement);
    }
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property property = properties[i];
      Property cProperty = coercedElement.getPropertyNamed(property.getName());
      if (cProperty != null) {
        cProperty.set(property.get());
      }
    }
    for (int i = 0; i < propertyReferences.length; i++) {
      PropertyReference propertyReference = propertyReferences[i];
      String key = keys[i];
      Element reference = world.getDescendantKeyed(key);
      Property property = propertyReference.getProperty();
      if (property.getOwner() == this) {
        property = coercedElement.getPropertyNamed(property.getName());
      }
      if (property != null) {
        if ((propertyReference instanceof ObjectArrayPropertyReference)) {
          ObjectArrayProperty objectArrayProperty = (ObjectArrayProperty)property;
          objectArrayProperty.set(((ObjectArrayPropertyReference)propertyReference).getIndex(), reference);
        } else {
          property.set(reference);
        }
      }
    }
    return coercedElement;
  }
  
  public Property[] getProperties() { if (m_propertyArray == null) {
      Class cls = getClass();
      Vector properties = new Vector();
      Field[] fields = cls.getFields();
      for (int i = 0; i < fields.length; i++) {
        Field field = fields[i];
        if (isPropertyField(field)) {
          try {
            Property property = (Property)field.get(this);
            if (property != null) {
              if (!property.isDeprecated())
              {

                properties.addElement(property);
              }
            } else {
              debugln(Messages.getString("warning__cannot_find_property_field__") + field.getName());
            }
          } catch (IllegalAccessException iae) {
            iae.printStackTrace();
          }
        }
      }
      
      m_propertyArray = new Property[properties.size()];
      properties.copyInto(m_propertyArray);
    }
    return m_propertyArray;
  }
  








  public Property getPropertyNamed(String name)
  {
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property property = properties[i];
      if (property.getName().equals(name)) {
        return property;
      }
    }
    return null;
  }
  
  public Property getPropertyNamedIgnoreCase(String name) {
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property property = properties[i];
      if (property.getName().equalsIgnoreCase(name)) {
        return property;
      }
    }
    return null;
  }
  
  public Element getParent()
  {
    return m_parent;
  }
  
  public void setParent(Element parentValue) {
    if (parentValue != m_parent) {
      if (m_parent != null)
      {
        m_parent.internalRemoveChild(this);
        m_parent = null;
      }
      if (parentValue != null) {
        parentValue.addChild(this);
      }
    }
  }
  
  private void checkAllPropertiesForBadReferences() {
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property property = properties[i];
      property.checkForBadReferences(property.get());
    }
    for (int i = 0; i < getChildCount(); i++) {
      getChildAt(i).checkAllPropertiesForBadReferences();
    }
  }
  




















  protected void internalSetParent(Element parentValue)
  {
    if (parentValue != null) {
      checkForNameCollision(parentValue, name.getStringValue());
      if (parentValue == this) {
        throw new RuntimeException(this + " " + Messages.getString("cannot_be_its_own_parent_"));
      }
      if (parentValue.isDescendantOf(this)) {
        throw new RuntimeException(this + " " + Messages.getString("cannot_have_descendant_") + parentValue + " " + Messages.getString("as_its_parent_"));
      }
    }
    Element prevParent = m_parent;
    m_parent = parentValue;
    try {
      checkAllPropertiesForBadReferences();
    } catch (RuntimeException re) {
      m_parent = prevParent;
      throw re;
    }
  }
  
  public Element getRoot() {
    if (m_parent == null) {
      return this;
    }
    return m_parent.getRoot();
  }
  
  public World getWorld()
  {
    Element root = getRoot();
    if ((root instanceof World)) {
      return (World)root;
    }
    return null;
  }
  
  public Sandbox getSandbox() {
    if ((this instanceof Sandbox)) {
      if ((this instanceof World))
        return (World)this;
      if (((m_parent instanceof World)) || ((m_parent instanceof Group))) {
        return (Sandbox)this;
      }
    }
    if (m_parent == null) {
      return null;
    }
    return m_parent.getSandbox();
  }
  
  public boolean isDescendantOf(Element element) {
    Element parentValue = getParent();
    while (parentValue != null) {
      if (parentValue == element) {
        return true;
      }
      parentValue = parentValue.getParent();
    }
    return false;
  }
  
  public boolean isAncestorOf(Element element) { if (element != null) {
      return element.isDescendantOf(this);
    }
    return false;
  }
  
  private void buildDetailedPath(StringBuffer sb)
  {
    if (m_parent != null) {
      m_parent.buildDetailedPath(sb);
    }
    sb.append("\t");
    sb.append(name.getStringValue());
    sb.append(" ");
    sb.append(getClass());
    sb.append("\n");
  }
  
  private String getInternalGetKeyExceptionDescription(Element ancestor, Element self, StringBuffer sbKey) { StringBuffer sb = new StringBuffer();
    sb.append("Could not find ancestor: ");
    if (ancestor != null) {
      sb.append(name.getStringValue());
      sb.append(", class: ");
      sb.append(ancestor.getClass());
    } else {
      sb.append("null");
    }
    sb.append("\nKey: ");
    sb.append(sbKey);
    sb.append("\nDetails: ");
    self.buildDetailedPath(sb);
    return sb.toString();
  }
  
  private void internalGetKey(Element ancestor, Element self, StringBuffer sb) {
    if (m_parent != ancestor)
    {

      if (this != ancestor) {
        if ((m_parent == null) && (ancestor != null)) {
          throw new RuntimeException(getInternalGetKeyExceptionDescription(ancestor, self, sb));
        }
        
        m_parent.internalGetKey(ancestor, self, sb);
      }
    }
    if (this != ancestor) {
      sb.append(getRepr());
    }
    if (this != self)
      sb.append('.');
  }
  
  public String getKey(Element ancestor) {
    StringBuffer sb = new StringBuffer();
    internalGetKey(ancestor, this, sb);
    return new String(sb);
  }
  
  public String getKey() { return getKey(null); }
  
  public String getTrimmedKey()
  {
    Element ancestor = getSandbox();
    if (ancestor != null) {
      ancestor = ancestor.getParent();
    }
    return getKey(ancestor);
  }
  
  public void addPropertyListenerToAllProperties(PropertyListener propertyListener)
  {
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property property = properties[i];
      property.addPropertyListener(propertyListener);
    }
  }
  
  public void removePropertyListenerFromAllProperties(PropertyListener propertyListener) {
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property property = properties[i];
      property.removePropertyListener(propertyListener);
    }
  }
  
  public int getChildCount() { return m_children.size(); }
  
  public Element getChildAt(int index) {
    if (index >= m_children.size()) {
      warnln(this + ".getChildAt( " + index + " ) is out of range [0," + m_children.size() + ").");
      return null;
    }
    return (Element)m_children.elementAt(index);
  }
  
  public int getIndexOfChild(Element child) { return m_children.indexOf(child); }
  

  public boolean hasChild(Element child) { return m_children.contains(child); }
  
  private Element internalGetChildNamed(String nameValue, boolean ignoreCase) {
    if (nameValue != null) {
      if ((nameValue.startsWith("__Unnamed")) && (nameValue.endsWith("__"))) {
        Element child = getChildAt(Integer.parseInt(nameValue.substring(9, nameValue.length() - 2)));
        if (child != null) {
          if (name.get() == null) {
            return child;
          }
          return null;
        }
        
        return null;
      }
      
      for (int i = 0; i < getChildCount(); i++) {
        Element child = getChildAt(i);
        if (nameValue != null) { boolean found;
          boolean found;
          if (ignoreCase) {
            found = nameValue.equalsIgnoreCase(name.getStringValue());
          } else {
            found = nameValue.equals(name.getStringValue());
          }
          if (found) {
            return child;
          }
        }
        else if (name.getStringValue() == null) {
          return child;
        }
      }
      
      return null;
    }
    
    return null;
  }
  
  public Element getChildNamed(String nameValue) {
    return internalGetChildNamed(nameValue, false);
  }
  
  public Element getChildNamedIgnoreCase(String nameValue) { return internalGetChildNamed(nameValue, true); }
  
  private Element internalGetDescendantKeyed(String key, int fromIndex, boolean ignoreCase) {
    if (key.equals("")) {
      return this;
    }
    int toIndex = key.indexOf('.', fromIndex);
    if (toIndex == -1) {
      String childName = key.substring(fromIndex);
      return internalGetChildNamed(childName, ignoreCase);
    }
    String childName = key.substring(fromIndex, toIndex);
    Element child = internalGetChildNamed(childName, ignoreCase);
    if (child != null) {
      return child.internalGetDescendantKeyed(key, toIndex + 1, ignoreCase);
    }
    return null;
  }
  

  public Element getDescendantKeyed(String key)
  {
    return internalGetDescendantKeyed(key, 0, false);
  }
  
  public Element getDescendantKeyedIgnoreCase(String key) { return internalGetDescendantKeyed(key, 0, true); }
  
  public Element[] getChildren() {
    if (m_childArray == null) {
      m_childArray = new Element[m_children.size()];
      m_children.copyInto(m_childArray);
    }
    return m_childArray;
  }
  
  public Element[] getChildren(Class cls) { Vector v = new Vector();
    for (int i = 0; i < m_children.size(); i++) {
      Object child = m_children.elementAt(i);
      if (cls.isAssignableFrom(child.getClass())) {
        v.addElement(child);
      }
    }
    Element[] array = new Element[v.size()];
    v.copyInto(array);
    return array;
  }
  
  protected int internalGetElementCount(Class cls, HowMuch howMuch, int count) {
    if (cls.isAssignableFrom(getClass())) {
      count++;
    }
    for (int i = 0; i < getChildCount(); i++) {
      count = getChildAt(i).internalGetElementCount(cls, howMuch, count);
    }
    return count;
  }
  
  public int getElementCount(Class cls, HowMuch howMuch) { return internalGetElementCount(cls, howMuch, 0); }
  
  public int getElementCount(Class cls) {
    return getElementCount(cls, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
  }
  
  public int getElementCount() { return getElementCount(Element.class); }
  
  public void internalSearch(Criterion criterion, HowMuch howMuch, Vector v)
  {
    if (criterion.accept(this)) {
      v.addElement(this);
    }
    for (int i = 0; i < getChildCount(); i++)
      getChildAt(i).internalSearch(criterion, howMuch, v);
  }
  
  public Element[] search(Criterion criterion, HowMuch howMuch) {
    Vector v = new Vector();
    internalSearch(criterion, howMuch, v);
    Element[] array = new Element[v.size()];
    v.copyInto(array);
    return array;
  }
  
  public Element[] search(Criterion criterion) { return search(criterion, HowMuch.INSTANCE_AND_ALL_DESCENDANTS); }
  
  public Element[] getDescendants(Class cls, HowMuch howMuch)
  {
    Element[] elements = search(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(cls), howMuch);
    if (cls == Element.class) {
      return elements;
    }
    Object array = java.lang.reflect.Array.newInstance(cls, elements.length);
    System.arraycopy(elements, 0, array, 0, elements.length);
    return (Element[])array;
  }
  
  public Element[] getDescendants(Class cls) {
    return getDescendants(cls, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
  }
  
  public Element[] getDescendants() { return getDescendants(Element.class); }
  
  public void setPropertyNamed(String name, Object value, HowMuch howMuch)
  {
    Property property = getPropertyNamed(name);
    if (property != null) {
      property.set(value, howMuch);
    } else
      for (int i = 0; i < m_children.size(); i++) {
        Element child = getChildAt(i);
        child.setPropertyNamed(name, value, howMuch);
      }
  }
  
  public void setPropertyNamed(String name, Object value) {
    setPropertyNamed(name, value, HowMuch.INSTANCE_AND_PARTS);
  }
  
  private void onChildrenChanging(ChildrenEvent childrenEvent) {
    Enumeration enum0 = m_childrenListeners.elements();
    while (enum0.hasMoreElements()) {
      ChildrenListener childrenListener = (ChildrenListener)enum0.nextElement();
      childrenListener.childrenChanging(childrenEvent);
    }
  }
  
  private void onChildrenChange(ChildrenEvent childrenEvent) { m_childArray = null;
    markKeepKeyDirty();
    Enumeration enum0 = m_childrenListeners.elements();
    while (enum0.hasMoreElements()) {
      ChildrenListener childrenListener = (ChildrenListener)enum0.nextElement();
      childrenListener.childrenChanged(childrenEvent);
    }
  }
  
  private boolean internalRemoveChild(Element child) { int oldIndex = m_children.indexOf(child);
    if (oldIndex != -1) {
      ChildrenEvent childrenEvent = new ChildrenEvent(this, child, 3, oldIndex, -1);
      onChildrenChanging(childrenEvent);
      m_children.removeElementAt(oldIndex);
      onChildrenChange(childrenEvent);
      return true;
    }
    return false;
  }
  
  public void insertChildAt(Element child, int index) {
    if (child.getParent() == this) {
      int oldIndex = m_children.indexOf(child);
      if (index != oldIndex) {
        ChildrenEvent childrenEvent = new ChildrenEvent(this, child, 2, oldIndex, index);
        onChildrenChanging(childrenEvent);
        m_children.removeElementAt(oldIndex);
        if (index == -1) {
          index = m_children.size();
        }
        

        m_children.insertElementAt(child, index);
        onChildrenChange(childrenEvent);
      }
    } else {
      if (index == -1) {
        index = m_children.size();
      }
      if (m_children.contains(child)) {
        throw new RuntimeException(child + " " + Messages.getString("is_already_a_child_of_") + this);
      }
      child.internalSetParent(this);
      ChildrenEvent childrenEvent = new ChildrenEvent(this, child, 1, -1, index);
      onChildrenChanging(childrenEvent);
      m_children.insertElementAt(child, index);
      onChildrenChange(childrenEvent);
    }
  }
  
  public void addChild(Element child) { insertChildAt(child, -1); }
  
  public void removeChild(Element child) {
    if (internalRemoveChild(child)) {
      child.internalSetParent(null);
    } else
      warnln(Messages.getString("WARNING__could_not_remove_child_") + child + Messages.getString("___it_is_not_a_child_of_") + this);
  }
  
  public void addChildrenListener(ChildrenListener childrenListener) {
    m_childrenListeners.addElement(childrenListener);
    m_childrenListenerArray = null;
  }
  
  public void removeChildrenListener(ChildrenListener childrenListener) { m_childrenListeners.removeElement(childrenListener);
    m_childrenListenerArray = null;
  }
  
  public ChildrenListener[] getChildrenListeners() { if (m_childrenListenerArray == null) {
      m_childrenListenerArray = new ChildrenListener[m_childrenListeners.size()];
      m_childrenListeners.copyInto(m_childrenListenerArray);
    }
    return m_childrenListenerArray;
  }
  
  public void visit(edu.cmu.cs.stage3.util.VisitListener visitListener, HowMuch howMuch) {
    visitListener.visited(this);
    if (howMuch.getDescend()) {
      for (int i = 0; i < getChildCount(); i++) {
        Element child = getChildAt(i);
        if ((!howMuch.getRespectDescendant()) || (!isFirstClass.booleanValue()))
        {

          child.visit(visitListener, howMuch);
        }
      }
    }
  }
  
  public boolean isReferenceInternalTo(Element whom) {
    return (this == whom) || (isDescendantOf(whom));
  }
  
  public boolean isReferenceExternalFrom(Element whom) { return !isReferenceInternalTo(whom); }
  
  protected void internalGetExternalPropertyReferences(Element whom, HowMuch howMuch, Vector references)
  {
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      if ((properties[i] instanceof ObjectArrayProperty)) {
        ObjectArrayProperty objectArrayProperty = (ObjectArrayProperty)properties[i];
        int precedingTotal = 0;
        for (int j = 0; j < objectArrayProperty.size(); j++) {
          Object o = objectArrayProperty.get(j);
          if (((o instanceof Element)) && 
            (((Element)o).isReferenceExternalFrom(whom))) {
            references.addElement(new ObjectArrayPropertyReference(objectArrayProperty, null, j, precedingTotal++));
          }
        }
      }
      else {
        Object o = properties[i].get();
        if (((o instanceof Element)) && 
          (((Element)o).isReferenceExternalFrom(whom))) {
          references.addElement(new PropertyReference(properties[i], null));
        }
      }
    }
    
    if (howMuch.getDescend()) {
      for (int i = 0; i < getChildCount(); i++) {
        Element child = getChildAt(i);
        if ((!isFirstClass.booleanValue()) || (!howMuch.getRespectDescendant()))
        {

          child.internalGetExternalPropertyReferences(whom, howMuch, references);
        }
      }
    }
  }
  
  public PropertyReference[] getExternalPropertyReferences(HowMuch howMuch) {
    Vector references = new Vector();
    internalGetExternalPropertyReferences(this, howMuch, references);
    PropertyReference[] referencesArray = new PropertyReference[references.size()];
    references.copyInto(referencesArray);
    return referencesArray;
  }
  
  public PropertyReference[] getExternalPropertyReferences() { return getExternalPropertyReferences(HowMuch.INSTANCE_AND_ALL_DESCENDANTS); }
  
  protected void internalGetPropertyReferencesTo(Element whom, HowMuch howMuch, boolean excludeWhomsParent, boolean excludeWhomAndItsDescendants, Vector references)
  {
    if ((excludeWhomAndItsDescendants) && ((this == whom) || (isDescendantOf(whom))))
      return;
    if ((this != whom.getParent()) || (!excludeWhomsParent))
    {

      Property[] properties = getProperties();
      for (int i = 0; i < properties.length; i++) {
        if ((properties[i] instanceof ObjectArrayProperty)) {
          ObjectArrayProperty objectArrayProperty = (ObjectArrayProperty)properties[i];
          int precedingTotal = 0;
          for (int j = 0; j < objectArrayProperty.size(); j++) {
            Object o = objectArrayProperty.get(j);
            if (((o instanceof Element)) && 
              (((Element)o).isReferenceInternalTo(whom))) {
              references.addElement(new ObjectArrayPropertyReference(objectArrayProperty, null, j, precedingTotal++));
            }
          }
        }
        else {
          Object o = properties[i].get();
          if (((o instanceof Element)) && 
            (((Element)o).isReferenceInternalTo(whom))) {
            references.addElement(new PropertyReference(properties[i], null));
          }
        }
      }
    }
    
    if (howMuch.getDescend()) {
      for (int i = 0; i < getChildCount(); i++) {
        Element child = getChildAt(i);
        if ((!isFirstClass.booleanValue()) || (!howMuch.getRespectDescendant()))
        {

          child.internalGetPropertyReferencesTo(whom, howMuch, excludeWhomsParent, excludeWhomAndItsDescendants, references);
        }
      }
    }
  }
  
  public PropertyReference[] getPropertyReferencesTo(Element whom, HowMuch howMuch, boolean excludeWhomsParent, boolean excludeWhomAndItsDescendants) {
    Vector references = new Vector();
    internalGetPropertyReferencesTo(whom, howMuch, excludeWhomsParent, excludeWhomAndItsDescendants, references);
    PropertyReference[] referencesArray = new PropertyReference[references.size()];
    references.copyInto(referencesArray);
    return referencesArray;
  }
  
  public PropertyReference[] getPropertyReferencesTo(Element whom, HowMuch howMuch, boolean excludeWhomsParent) { return getPropertyReferencesTo(whom, howMuch, excludeWhomsParent, true); }
  
  public PropertyReference[] getPropertyReferencesTo(Element whom, HowMuch howMuch) {
    return getPropertyReferencesTo(whom, howMuch, true);
  }
  
  public PropertyReference[] getPropertyReferencesTo(Element whom) { return getPropertyReferencesTo(whom, HowMuch.INSTANCE_AND_ALL_DESCENDANTS); }
  
  public PropertyReference[] getPropertyReferencesToMe(Element fromWhom, HowMuch howMuch, boolean excludeMyParent, boolean excludeMeAndMyDescendants)
  {
    return fromWhom.getPropertyReferencesTo(this, howMuch, excludeMyParent, excludeMeAndMyDescendants);
  }
  
  public PropertyReference[] getPropertyReferencesToMe(Element fromWhom, HowMuch howMuch, boolean excludeMyParent) { return getPropertyReferencesToMe(fromWhom, howMuch, excludeMyParent, true); }
  
  public PropertyReference[] getPropertyReferencesToMe(Element fromWhom, HowMuch howMuch) {
    return getPropertyReferencesToMe(fromWhom, howMuch, true);
  }
  
  public PropertyReference[] getPropertyReferencesToMe(Element fromWhom) { return getPropertyReferencesToMe(fromWhom, HowMuch.INSTANCE_AND_ALL_DESCENDANTS); }
  
  public PropertyReference[] getPropertyReferencesToMe() {
    return getPropertyReferencesToMe(getRoot());
  }
  











  public void removeFromParentsProperties()
  {
    Element parentValue = getParent();
    if (parentValue != null) {
      PropertyReference[] parentReferences = parentValue.getPropertyReferencesTo(this, HowMuch.INSTANCE, false, true);
      for (int i = 0; i < parentReferences.length; i++) {
        if ((parentReferences[i] instanceof ObjectArrayPropertyReference)) {
          ObjectArrayPropertyReference oapr = (ObjectArrayPropertyReference)parentReferences[i];
          oapr.getObjectArrayProperty().remove(oapr.getIndex() - oapr.getPrecedingTotal());
        } else {
          parentReferences[i].getProperty().set(null);
        }
      }
    }
  }
  
  public void removeFromParent() {
    Element root = getRoot();
    PropertyReference[] externalReferences = root.getPropertyReferencesTo(this, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
    if (externalReferences.length > 0) {
      StringBuffer sb = new StringBuffer();
      sb.append("ExternalReferenceException:\n");
      for (int i = 0; i < externalReferences.length; i++) {
        sb.append(externalReferences[i]);
        sb.append("\n");
      }
      throw new RuntimeException(sb.toString());
    }
    removeFromParentsProperties();
    setParent(null);
  }
  
  public void HACK_removeFromParentWithoutCheckingForExternalReferences() {
    removeFromParentsProperties();
    setParent(null);
  }
  
  public boolean isAssignableToOneOf(Class[] classes) {
    if (classes != null) {
      Class cls = getClass();
      for (int i = 0; i < classes.length; i++) {
        if (classes[i].isAssignableFrom(cls)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private CopyFactory createCopyFactory(Class[] classesToShare, HowMuch howMuch, Element internalReferenceRoot) { return new CopyFactory(this, internalReferenceRoot, classesToShare, howMuch); }
  
  public CopyFactory createCopyFactory(Class[] classesToShare, HowMuch howMuch) {
    return createCopyFactory(classesToShare, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, this);
  }
  
  public CopyFactory createCopyFactory(Class[] classesToShare) { return createCopyFactory(classesToShare, HowMuch.INSTANCE_AND_ALL_DESCENDANTS); }
  
  public CopyFactory createCopyFactory() {
    return createCopyFactory(null);
  }
  
  public Element HACK_createCopy(String name, Element parent, int index, Class[] classesToShare, Element parentToBe) {
    CopyFactory copyFactory = createCopyFactory(classesToShare);
    try {
      Element dst = copyFactory.manufactureCopy(getRoot(), null, null, parentToBe);
      name.set(name);
      if (parent != null) {
        parent.insertChildAt(dst, index);
      }
      

      return dst;
    } catch (UnresolvablePropertyReferencesException upre) {
      upre.printStackTrace();
      throw new ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
    }
  }
  
  public Element createCopyNamed(String name, Class[] classesToShare) {
    CopyFactory copyFactory = createCopyFactory(classesToShare);
    try {
      Element dst = copyFactory.manufactureCopy(getRoot());
      name.set(name);
      return dst;
    } catch (UnresolvablePropertyReferencesException upre) {
      upre.printStackTrace();
      throw new ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
    }
  }
  
  public Element createCopyNamed(String name) { return createCopyNamed(name, null); }
  

  protected void internalCopyOver(Element dst, boolean isTopLevel, Dictionary childCopyFactoryToParentMap)
  {
    Element[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      Element child = children[i];
      String childName = name.getStringValue();
      Element dstChild = null;
      if ((childName == null) || (childName.equals("__ita__"))) {
        Element[] itas = dst.getChildren(edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray.class);
        if (itas.length > 0) {
          dstChild = itas[0];
        }
      } else {
        dstChild = dst.getChildNamedIgnoreCase(childName);
      }
      if (dstChild != null) {
        child.internalCopyOver(dstChild, false, childCopyFactoryToParentMap);
      } else {
        CopyFactory childCopyFactory = child.createCopyFactory(null, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, this);
        childCopyFactoryToParentMap.put(childCopyFactory, dst);
      }
    }
    Property[] properties = getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property property = properties[i];
      if (isTopLevel) {
        String propertyName = property.getName();
        if (!propertyName.equals("name"))
        {

          if (!propertyName.equals("vehicle"))
          {

            if (propertyName.equals("localTransformation")) {}
          }
        }
      }
      else if ((!(property instanceof ObjectArrayProperty)) || ((property instanceof edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty)))
      {

        Object value = property.get();
        if (!(value instanceof Element))
        {

          Property dstProperty = dst.getPropertyNamed(property.getName());
          if (dstProperty != null) {
            dstProperty.set(value);
          }
        }
      }
    }
  }
  
  protected void HACK_copyOverTextureMapReferences(Element dst, Dictionary srcTextureMapToDstTextureMapMap)
  {
    Element[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      Element child = children[i];
      Element dstChild = dst.getChildNamedIgnoreCase(name.getStringValue());
      if (dstChild != null)
        child.HACK_copyOverTextureMapReferences(dstChild, srcTextureMapToDstTextureMapMap);
    }
  }
  
  public void copyOver(Element dst) {
    Dictionary childCopyFactoryToParentMap = new Hashtable();
    internalCopyOver(dst, true, childCopyFactoryToParentMap);
    Enumeration enum0 = childCopyFactoryToParentMap.keys();
    while (enum0.hasMoreElements()) {
      CopyFactory childCopyFactory = (CopyFactory)enum0.nextElement();
      Element parent = (Element)childCopyFactoryToParentMap.get(childCopyFactory);
      try {
        Element child = childCopyFactory.manufactureCopy(getRoot(), parent, null, parent);
        

        child.setParent(parent);
      } catch (UnresolvablePropertyReferencesException upre) {
        throw new ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
      }
    }
    if (((this instanceof Sandbox)) && ((dst instanceof Sandbox))) {
      TextureMap[] srcTMs = (TextureMap[])textureMaps.getArrayValue();
      TextureMap[] dstTMs = (TextureMap[])textureMaps.getArrayValue();
      Dictionary srcTextureMapToDstTextureMapMap = new Hashtable();
      for (int i = 0; i < srcTMs.length; i++) {
        TextureMap srcTM = srcTMs[i];
        for (int j = 0; j < srcTMs.length; j++) {
          TextureMap dstTM = dstTMs[i];
          if (name.getStringValue().equals(name.getStringValue())) {
            srcTextureMapToDstTextureMapMap.put(srcTM, dstTM);
            break;
          }
        }
      }
      HACK_copyOverTextureMapReferences(dst, srcTextureMapToDstTextureMapMap);
    }
  }
  
  protected void loadCompleted()
  {
    for (int i = 0; i < getChildCount(); i++) {
      getChildAt(i).loadCompleted();
    }
  }
  
  /* Error */
  protected static Element load(DocumentBuilder builder, DirectoryTreeLoader loader, Vector referencesToBeResolved, ProgressObserver progressObserver)
    throws IOException, ProgressCancelException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 1178 1 0
    //   6: astore 4
    //   8: aload_1
    //   9: ldc 17
    //   11: invokeinterface 1183 2 0
    //   16: astore 5
    //   18: new 1187	java/io/InputStreamReader
    //   21: dup
    //   22: aload 5
    //   24: ldc_w 1189
    //   27: invokespecial 1191	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
    //   30: astore 6
    //   32: new 1194	org/xml/sax/InputSource
    //   35: dup
    //   36: aload 6
    //   38: invokespecial 1196	org/xml/sax/InputSource:<init>	(Ljava/io/Reader;)V
    //   41: astore 7
    //   43: aload_0
    //   44: aload 7
    //   46: invokevirtual 1199	javax/xml/parsers/DocumentBuilder:parse	(Lorg/xml/sax/InputSource;)Lorg/w3c/dom/Document;
    //   49: astore 8
    //   51: aload 8
    //   53: invokeinterface 1205 1 0
    //   58: astore 9
    //   60: aload 9
    //   62: invokeinterface 1211 1 0
    //   67: aload 9
    //   69: ldc_w 1216
    //   72: invokeinterface 1218 2 0
    //   77: astore 10
    //   79: aload 9
    //   81: ldc_w 1221
    //   84: invokeinterface 1218 2 0
    //   89: invokestatic 1223	java/lang/Double:parseDouble	(Ljava/lang/String;)D
    //   92: dstore 11
    //   94: aload 9
    //   96: ldc -118
    //   98: invokeinterface 1218 2 0
    //   103: astore 13
    //   105: aload 10
    //   107: invokestatic 1229	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   110: astore 14
    //   112: aload 14
    //   114: invokevirtual 614	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   117: checkcast 1	edu/cmu/cs/stage3/alice/core/Element
    //   120: astore 15
    //   122: aload 15
    //   124: aload_1
    //   125: ldc 17
    //   127: invokeinterface 1233 2 0
    //   132: putfield 179	edu/cmu/cs/stage3/alice/core/Element:m_xmlFileKeepKey	Ljava/lang/Object;
    //   135: goto +11 -> 146
    //   138: astore 16
    //   140: aload 15
    //   142: aconst_null
    //   143: putfield 179	edu/cmu/cs/stage3/alice/core/Element:m_xmlFileKeepKey	Ljava/lang/Object;
    //   146: aload 13
    //   148: invokevirtual 295	java/lang/String:length	()I
    //   151: ifle +13 -> 164
    //   154: aload 15
    //   156: getfield 142	edu/cmu/cs/stage3/alice/core/Element:name	Ledu/cmu/cs/stage3/alice/core/property/StringProperty;
    //   159: aload 13
    //   161: invokevirtual 1094	edu/cmu/cs/stage3/alice/core/property/StringProperty:set	(Ljava/lang/Object;)V
    //   164: aload 9
    //   166: ldc_w 1237
    //   169: invokeinterface 1238 2 0
    //   174: astore 16
    //   176: iconst_0
    //   177: istore 17
    //   179: goto +91 -> 270
    //   182: aload 16
    //   184: iload 17
    //   186: invokeinterface 1242 2 0
    //   191: checkcast 1212	org/w3c/dom/Element
    //   194: astore 18
    //   196: aload 18
    //   198: ldc -118
    //   200: invokeinterface 1218 2 0
    //   205: invokevirtual 251	java/lang/String:trim	()Ljava/lang/String;
    //   208: astore 19
    //   210: aload 15
    //   212: aload 19
    //   214: invokevirtual 634	edu/cmu/cs/stage3/alice/core/Element:getPropertyNamed	(Ljava/lang/String;)Ledu/cmu/cs/stage3/alice/core/Property;
    //   217: astore 20
    //   219: aload 20
    //   221: ifnull +17 -> 238
    //   224: aload 20
    //   226: aload 18
    //   228: aload_1
    //   229: aload_2
    //   230: dload 11
    //   232: invokevirtual 1248	edu/cmu/cs/stage3/alice/core/Property:decode	(Lorg/w3c/dom/Element;Ledu/cmu/cs/stage3/io/DirectoryTreeLoader;Ljava/util/Vector;D)V
    //   235: goto +32 -> 267
    //   238: new 314	java/lang/StringBuilder
    //   241: dup
    //   242: aload 10
    //   244: invokestatic 318	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   247: invokespecial 322	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   250: ldc_w 1252
    //   253: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   256: aload 19
    //   258: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   261: invokevirtual 332	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   264: invokestatic 805	edu/cmu/cs/stage3/alice/core/Element:warnln	(Ljava/lang/Object;)V
    //   267: iinc 17 1
    //   270: iload 17
    //   272: aload 16
    //   274: invokeinterface 1254 1 0
    //   279: if_icmplt -97 -> 182
    //   282: getstatic 125	edu/cmu/cs/stage3/alice/core/Element:s_loadProgress	I
    //   285: iconst_1
    //   286: iadd
    //   287: putstatic 125	edu/cmu/cs/stage3/alice/core/Element:s_loadProgress	I
    //   290: aload_3
    //   291: ifnull +20 -> 311
    //   294: aload_3
    //   295: getstatic 125	edu/cmu/cs/stage3/alice/core/Element:s_loadProgress	I
    //   298: aload 15
    //   300: getfield 142	edu/cmu/cs/stage3/alice/core/Element:name	Ledu/cmu/cs/stage3/alice/core/property/StringProperty;
    //   303: invokevirtual 713	edu/cmu/cs/stage3/alice/core/property/StringProperty:getStringValue	()Ljava/lang/String;
    //   306: invokeinterface 1257 3 0
    //   311: aload 9
    //   313: ldc_w 1263
    //   316: invokeinterface 1238 2 0
    //   321: astore 17
    //   323: iconst_0
    //   324: istore 18
    //   326: goto +150 -> 476
    //   329: aload 17
    //   331: iload 18
    //   333: invokeinterface 1242 2 0
    //   338: checkcast 1212	org/w3c/dom/Element
    //   341: astore 19
    //   343: aload 19
    //   345: ldc_w 1264
    //   348: invokeinterface 1218 2 0
    //   353: invokevirtual 251	java/lang/String:trim	()Ljava/lang/String;
    //   356: astore 20
    //   358: aload_1
    //   359: aload 20
    //   361: invokeinterface 1266 2 0
    //   366: aload_0
    //   367: aload_1
    //   368: aload_2
    //   369: aload_3
    //   370: invokestatic 1269	edu/cmu/cs/stage3/alice/core/Element:load	(Ljavax/xml/parsers/DocumentBuilder;Ledu/cmu/cs/stage3/io/DirectoryTreeLoader;Ljava/util/Vector;Ledu/cmu/cs/stage3/progress/ProgressObserver;)Ledu/cmu/cs/stage3/alice/core/Element;
    //   373: astore 21
    //   375: aload 21
    //   377: getfield 142	edu/cmu/cs/stage3/alice/core/Element:name	Ledu/cmu/cs/stage3/alice/core/property/StringProperty;
    //   380: invokevirtual 713	edu/cmu/cs/stage3/alice/core/property/StringProperty:getStringValue	()Ljava/lang/String;
    //   383: astore 22
    //   385: aload 22
    //   387: ifnull +66 -> 453
    //   390: aload 15
    //   392: aload 22
    //   394: invokevirtual 1271	edu/cmu/cs/stage3/alice/core/Element:getChildNamed	(Ljava/lang/String;)Ledu/cmu/cs/stage3/alice/core/Element;
    //   397: ifnull +56 -> 453
    //   400: aconst_null
    //   401: astore 21
    //   403: getstatic 522	java/lang/System:err	Ljava/io/PrintStream;
    //   406: new 314	java/lang/StringBuilder
    //   409: dup
    //   410: invokespecial 716	java/lang/StringBuilder:<init>	()V
    //   413: aload 15
    //   415: invokevirtual 414	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   418: ldc_w 717
    //   421: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   424: ldc_w 1273
    //   427: invokestatic 300	edu/cmu/cs/stage3/lang/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   430: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   433: aload 22
    //   435: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   438: ldc_w 1275
    //   441: invokestatic 300	edu/cmu/cs/stage3/lang/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   444: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   447: invokevirtual 332	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   450: invokevirtual 530	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   453: aload 21
    //   455: ifnull +10 -> 465
    //   458: aload 15
    //   460: aload 21
    //   462: invokevirtual 703	edu/cmu/cs/stage3/alice/core/Element:addChild	(Ledu/cmu/cs/stage3/alice/core/Element;)V
    //   465: aload_1
    //   466: aload 4
    //   468: invokeinterface 1266 2 0
    //   473: iinc 18 1
    //   476: iload 18
    //   478: aload 17
    //   480: invokeinterface 1254 1 0
    //   485: if_icmplt -156 -> 329
    //   488: aload 15
    //   490: areturn
    //   491: astore 14
    //   493: new 617	edu/cmu/cs/stage3/alice/core/ExceptionWrapper
    //   496: dup
    //   497: aload 14
    //   499: new 314	java/lang/StringBuilder
    //   502: dup
    //   503: ldc_w 1277
    //   506: invokespecial 322	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   509: aload 10
    //   511: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   514: invokevirtual 332	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   517: invokespecial 620	edu/cmu/cs/stage3/alice/core/ExceptionWrapper:<init>	(Ljava/lang/Exception;Ljava/lang/String;)V
    //   520: athrow
    //   521: astore 14
    //   523: new 617	edu/cmu/cs/stage3/alice/core/ExceptionWrapper
    //   526: dup
    //   527: aload 14
    //   529: new 314	java/lang/StringBuilder
    //   532: dup
    //   533: ldc_w 1279
    //   536: invokespecial 322	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   539: aload 10
    //   541: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   544: invokevirtual 332	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   547: invokespecial 620	edu/cmu/cs/stage3/alice/core/ExceptionWrapper:<init>	(Ljava/lang/Exception;Ljava/lang/String;)V
    //   550: athrow
    //   551: astore 14
    //   553: new 617	edu/cmu/cs/stage3/alice/core/ExceptionWrapper
    //   556: dup
    //   557: aload 14
    //   559: new 314	java/lang/StringBuilder
    //   562: dup
    //   563: ldc_w 1281
    //   566: invokespecial 322	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   569: aload 10
    //   571: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   574: invokevirtual 332	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   577: invokespecial 620	edu/cmu/cs/stage3/alice/core/ExceptionWrapper:<init>	(Ljava/lang/Exception;Ljava/lang/String;)V
    //   580: athrow
    //   581: astore 5
    //   583: new 617	edu/cmu/cs/stage3/alice/core/ExceptionWrapper
    //   586: dup
    //   587: aload 5
    //   589: ldc_w 1283
    //   592: invokespecial 620	edu/cmu/cs/stage3/alice/core/ExceptionWrapper:<init>	(Ljava/lang/Exception;Ljava/lang/String;)V
    //   595: athrow
    // Line number table:
    //   Java source line #1442	-> byte code offset #0
    //   Java source line #1444	-> byte code offset #8
    //   Java source line #1445	-> byte code offset #18
    //   Java source line #1447	-> byte code offset #32
    //   Java source line #1450	-> byte code offset #43
    //   Java source line #1451	-> byte code offset #51
    //   Java source line #1452	-> byte code offset #60
    //   Java source line #1454	-> byte code offset #67
    //   Java source line #1455	-> byte code offset #79
    //   Java source line #1456	-> byte code offset #94
    //   Java source line #1459	-> byte code offset #105
    //   Java source line #1460	-> byte code offset #112
    //   Java source line #1462	-> byte code offset #122
    //   Java source line #1463	-> byte code offset #135
    //   Java source line #1464	-> byte code offset #140
    //   Java source line #1467	-> byte code offset #146
    //   Java source line #1468	-> byte code offset #154
    //   Java source line #1471	-> byte code offset #164
    //   Java source line #1472	-> byte code offset #176
    //   Java source line #1473	-> byte code offset #182
    //   Java source line #1474	-> byte code offset #196
    //   Java source line #1475	-> byte code offset #210
    //   Java source line #1476	-> byte code offset #219
    //   Java source line #1477	-> byte code offset #224
    //   Java source line #1478	-> byte code offset #235
    //   Java source line #1479	-> byte code offset #238
    //   Java source line #1472	-> byte code offset #267
    //   Java source line #1484	-> byte code offset #282
    //   Java source line #1485	-> byte code offset #290
    //   Java source line #1486	-> byte code offset #294
    //   Java source line #1489	-> byte code offset #311
    //   Java source line #1490	-> byte code offset #323
    //   Java source line #1491	-> byte code offset #329
    //   Java source line #1492	-> byte code offset #343
    //   Java source line #1493	-> byte code offset #358
    //   Java source line #1494	-> byte code offset #366
    //   Java source line #1495	-> byte code offset #375
    //   Java source line #1496	-> byte code offset #385
    //   Java source line #1497	-> byte code offset #390
    //   Java source line #1498	-> byte code offset #400
    //   Java source line #1499	-> byte code offset #403
    //   Java source line #1502	-> byte code offset #453
    //   Java source line #1503	-> byte code offset #458
    //   Java source line #1505	-> byte code offset #465
    //   Java source line #1490	-> byte code offset #473
    //   Java source line #1507	-> byte code offset #488
    //   Java source line #1508	-> byte code offset #491
    //   Java source line #1509	-> byte code offset #493
    //   Java source line #1510	-> byte code offset #521
    //   Java source line #1511	-> byte code offset #523
    //   Java source line #1512	-> byte code offset #551
    //   Java source line #1513	-> byte code offset #553
    //   Java source line #1515	-> byte code offset #581
    //   Java source line #1516	-> byte code offset #583
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	596	0	builder	DocumentBuilder
    //   0	596	1	loader	DirectoryTreeLoader
    //   0	596	2	referencesToBeResolved	Vector
    //   0	596	3	progressObserver	ProgressObserver
    //   6	461	4	currentDirectory	String
    //   16	7	5	inputStream	java.io.InputStream
    //   581	7	5	saxe	org.xml.sax.SAXException
    //   30	7	6	reader	java.io.Reader
    //   41	4	7	is	org.xml.sax.InputSource
    //   49	3	8	document	Document
    //   58	254	9	elementNode	org.w3c.dom.Element
    //   77	493	10	classname	String
    //   92	139	11	version	double
    //   103	57	13	nameValue	String
    //   110	3	14	cls	Class
    //   491	7	14	cnfe	ClassNotFoundException
    //   521	7	14	ie	InstantiationException
    //   551	7	14	iae	IllegalAccessException
    //   120	369	15	element	Element
    //   138	3	16	kfnse	KeepFileNotSupportedException
    //   174	99	16	propertyNodeList	org.w3c.dom.NodeList
    //   177	94	17	i	int
    //   321	158	17	childNodeList	org.w3c.dom.NodeList
    //   194	33	18	propertyNode	org.w3c.dom.Element
    //   324	153	18	i	int
    //   208	49	19	propertyName	String
    //   341	3	19	childNode	org.w3c.dom.Element
    //   217	8	20	property	Property
    //   356	4	20	filename	String
    //   373	88	21	child	Element
    //   383	51	22	childName	String
    // Exception table:
    //   from	to	target	type
    //   122	135	138	edu/cmu/cs/stage3/io/KeepFileNotSupportedException
    //   105	490	491	java/lang/ClassNotFoundException
    //   105	490	521	java/lang/InstantiationException
    //   105	490	551	java/lang/IllegalAccessException
    //   8	490	581	org/xml/sax/SAXException
    //   491	581	581	org/xml/sax/SAXException
  }
  
  public static Element load(DirectoryTreeLoader loader, Element externalRoot, ProgressObserver progressObserver)
    throws IOException, ProgressCancelException, UnresolvablePropertyReferencesException
  {
    Vector referencesToBeResolved = new Vector();
    Vector referencesLeftUnresolved = new Vector();
    try
    {
      s_isLoading = true;
      int elementCount = -1;
      try {
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.BufferedInputStream(loader.readFile("elementCountHint.txt"))));
        elementCount = Integer.parseInt(br.readLine().replaceAll("\\D", ""));
        loader.closeCurrentFile();
      }
      catch (java.io.FileNotFoundException localFileNotFoundException) {}
      

      s_loadProgress = 0;
      if (progressObserver != null) {
        progressObserver.progressBegin(elementCount);
      }
      try
      {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Element element = load(builder, loader, referencesToBeResolved, progressObserver);
        ReferenceResolver referenceResolver = new edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver(element, externalRoot);
        Enumeration enum0 = referencesToBeResolved.elements();
        while (enum0.hasMoreElements()) {
          PropertyReference propertyReference = (PropertyReference)enum0.nextElement();
          try {
            propertyReference.resolve(referenceResolver);
          } catch (UnresolvableReferenceException ure) {
            referencesLeftUnresolved.add(propertyReference);
          } catch (Throwable t) {
            System.err.println(propertyReference);
            t.printStackTrace();
          }
        }
      }
      catch (ParserConfigurationException pce) {
        throw new ExceptionWrapper(pce, "loader: " + loader + "; externalRoot: " + externalRoot);
      }
    } finally {
      if (progressObserver != null) {
        progressObserver.progressEnd();
      }
      s_isLoading = false;
    }
    Element element;
    if (progressObserver != null) {
      progressObserver.progressEnd();
    }
    s_isLoading = false;
    
    if (referencesLeftUnresolved.size() == 0) {
      element.loadCompleted();
      return element;
    }
    PropertyReference[] propertyReferences = new PropertyReference[referencesLeftUnresolved.size()];
    referencesLeftUnresolved.copyInto(propertyReferences);
    throw new UnresolvablePropertyReferencesException(propertyReferences, element, "loader: " + loader + "; externalRoot: " + externalRoot);
  }
  
  public static Element load(File file, Element externalRoot, ProgressObserver progressObserver) throws IOException, ProgressCancelException, UnresolvablePropertyReferencesException {
    DirectoryTreeLoader loader = null;
    if (file.isDirectory()) {
      loader = new edu.cmu.cs.stage3.io.FileSystemTreeLoader();
    } else {
      String pathname = file.getAbsolutePath();
      if ((pathname.endsWith(".a2w")) || (pathname.endsWith(".a2c")) || (pathname.endsWith(".zip"))) {
        loader = new edu.cmu.cs.stage3.io.ZipFileTreeLoader();
      }
      else {
        throw new IllegalArgumentException(file + " " + Messages.getString("must_be_a_directory_or_end_in___a2w_____a2c___or___zip__"));
      }
    }
    loader.open(file);
    Element element = null;
    try {
      element = load(loader, externalRoot, progressObserver);
    } finally {
      loader.close();
    }
    return element;
  }
  
  public static Element load(java.net.URL url, Element externalRoot, ProgressObserver progressObserver) throws IOException, ProgressCancelException, UnresolvablePropertyReferencesException { ZipTreeLoader loader = new ZipTreeLoader();
    loader.open(url);
    Element element = null;
    try {
      element = load(loader, externalRoot, progressObserver);
    } finally {
      loader.close();
    }
    return element;
  }
  
  public static Element load(java.io.InputStream is, Element externalRoot, ProgressObserver progressObserver) throws IOException, ProgressCancelException, UnresolvablePropertyReferencesException { ZipTreeLoader loader = new ZipTreeLoader();
    loader.open(is);
    Element element = null;
    try {
      element = load(loader, externalRoot, progressObserver);
    } finally {
      loader.close();
    }
    return element;
  }
  
  public static Element load(java.io.InputStream is, Element externalRoot) throws IOException, UnresolvablePropertyReferencesException {
    try { return load(is, externalRoot, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
  
  public static Element load(File file, Element externalRoot) throws IOException, UnresolvablePropertyReferencesException {
    try { return load(file, externalRoot, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
  
  public static Element load(java.net.URL url, Element externalRoot) throws IOException, UnresolvablePropertyReferencesException {
    try { return load(url, externalRoot, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
  
  private void writeXMLDocument(Document xmlDocument, DirectoryTreeStorer storer, String filename) throws IOException {
    OutputStream os = storer.createFile(filename, true);
    edu.cmu.cs.stage3.xml.Encoder.write(xmlDocument, os);
    storer.closeCurrentFile();
  }
  
  protected int internalStore(DocumentBuilder builder, DirectoryTreeStorer storer, ProgressObserver progressObserver, HowMuch howMuch, ReferenceGenerator referenceGenerator, int count) throws IOException, ProgressCancelException {
    
    if (progressObserver != null) {
      progressObserver.progressUpdate(count, getKey());
    }
    Object xmlFileKeepKey;
    try {
      xmlFileKeepKey = storer.getKeepKey("elementData.xml");
    } catch (KeepFileNotSupportedException kfnse) { Object xmlFileKeepKey;
      xmlFileKeepKey = null;
    }
    if ((m_xmlFileKeepKey == null) || (!m_xmlFileKeepKey.equals(xmlFileKeepKey))) {
      Document document = builder.newDocument();
      
      org.w3c.dom.Element elementNode = document.createElement("element");
      elementNode.setAttribute("class", getClass().getName());
      elementNode.setAttribute("version", Double.toString(2.001D));
      
      document.appendChild(elementNode);
      
      for (int i = 0; i < getChildCount(); i++) {
        org.w3c.dom.Element childNode = document.createElement("child");
        
        childNode.setAttribute("filename", getChildAt(i).getRepr(i));
        
        elementNode.appendChild(childNode);
      }
      
      Property[] properties = getProperties();
      for (int i = 0; i < properties.length; i++) {
        String propertyName = properties[i].getName();
        if (propertyName.equals("name")) {
          String nameValue = this.name.getStringValue();
          if (nameValue != null) {
            elementNode.setAttribute("name", nameValue);
          }
        } else {
          org.w3c.dom.Element propertyNode = document.createElement("property");
          propertyNode.setAttribute("name", properties[i].getName());
          properties[i].encode(document, propertyNode, storer, referenceGenerator);
          elementNode.appendChild(propertyNode);
        }
      }
      
      document.getDocumentElement().normalize();
      

















      writeXMLDocument(document, storer, "elementData.xml");
      try {
        m_xmlFileKeepKey = storer.getKeepKey("elementData.xml");
      } catch (KeepFileNotSupportedException kfnse) {
        m_xmlFileKeepKey = null;
      }
    } else {
      try {
        storer.keepFile("elementData.xml");
        Property[] properties = getProperties();
        for (int i = 0; i < properties.length; i++) {
          if (properties[i].get() != null) {
            properties[i].keepAnyAssociatedFiles(storer);
          }
        }
      } catch (KeepFileNotSupportedException kfnse) {
        kfnse.printStackTrace();
      } catch (edu.cmu.cs.stage3.io.KeepFileDoesNotExistException kfdne) {
        kfdne.printStackTrace();
      }
    }
    
    String thisDirectory = storer.getCurrentDirectory();
    for (int i = 0; i < getChildCount(); i++) {
      Element child = getChildAt(i);
      String name = child.getRepr(i);
      storer.createDirectory(name);
      storer.setCurrentDirectory(name);
      count = child.internalStore(builder, storer, progressObserver, howMuch, referenceGenerator, count);
      storer.setCurrentDirectory(thisDirectory);
    }
    return count;
  }
  










  public void store(DirectoryTreeStorer storer, ProgressObserver progressObserver, Dictionary filnameToByteArrayMap, HowMuch howMuch, ReferenceGenerator referenceGenerator)
    throws IOException, ProgressCancelException
  {
    int elementCount = getElementCount(Element.class, howMuch);
    if (progressObserver != null) {
      progressObserver.progressBegin(elementCount);
    }
    
    BufferedWriter writer = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.BufferedOutputStream(storer.createFile("elementCountHint.txt", true))));
    writer.write(Integer.toString(elementCount));
    writer.newLine();
    writer.flush();
    storer.closeCurrentFile();
    
    if ((filnameToByteArrayMap != null) && 
      (filnameToByteArrayMap.size() > 0)) {
      Enumeration enum0 = filnameToByteArrayMap.keys();
      while (enum0.hasMoreElements()) {
        String filename = (String)enum0.nextElement();
        byte[] byteArray = (byte[])filnameToByteArrayMap.get(filename);
        os = storer.createFile(filename, false);
        os.write(byteArray);
        os.flush();
        storer.closeCurrentFile();
      }
    }
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      int storeCount = internalStore(builder, storer, progressObserver, howMuch, referenceGenerator, 0);
      if (elementCount != storeCount) {
        warnln(Messages.getString("WARNING__elementCount_") + elementCount + " " + Messages.getString("not_equal_storeCount_") + storeCount);
      }
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } finally {
      if (progressObserver != null)
        progressObserver.progressEnd();
    }
  }
  
  public void store(DirectoryTreeStorer storer, ProgressObserver progressObserver, Dictionary filnameToByteArrayMap, HowMuch howMuch) throws IOException, ProgressCancelException {
    store(storer, progressObserver, filnameToByteArrayMap, howMuch, new edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceGenerator(this));
  }
  
  public void store(DirectoryTreeStorer storer, ProgressObserver progressObserver, Dictionary filnameToByteArrayMap) throws IOException, ProgressCancelException { store(storer, progressObserver, filnameToByteArrayMap, HowMuch.INSTANCE_AND_ALL_DESCENDANTS); }
  

  public void store(DirectoryTreeStorer storer, ProgressObserver progressObserver) throws IOException, ProgressCancelException { store(storer, progressObserver, null); }
  
  public void store(DirectoryTreeStorer storer) throws IOException {
    try {
      store(storer, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
  
  public void store(File file, ProgressObserver progressObserver, Dictionary filnameToByteArrayMap) throws IOException, ProgressCancelException { DirectoryTreeStorer storer;
    DirectoryTreeStorer storer;
    if (file.isDirectory()) {
      storer = new edu.cmu.cs.stage3.io.FileSystemTreeStorer();
    } else {
      pathname = file.getAbsolutePath();
      DirectoryTreeStorer storer; if ((pathname.endsWith(".a2w")) || (pathname.endsWith(".a2c")) || (pathname.endsWith(".zip"))) {
        DirectoryTreeStorer storer;
        if (pathname.endsWith(".a2c")) {
          storer = new edu.cmu.cs.stage3.io.ZipTreeStorer();
        } else {
          storer = new edu.cmu.cs.stage3.io.ZipFileTreeStorer();
        }
      } else {
        storer = new edu.cmu.cs.stage3.io.FileSystemTreeStorer();
      }
    }
    storer.open(file);
    try {
      store(storer, progressObserver, filnameToByteArrayMap);
    } finally {
      storer.close();
    }
  }
  
  public void store(File file, ProgressObserver progressObserver) throws IOException, ProgressCancelException { store(file, progressObserver, null); }
  
  public void store(File file) throws IOException {
    try {
      store(file, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
  
  public void store(String filename, ProgressObserver progressObserver) throws IOException, ProgressCancelException { store(new File(filename), progressObserver); }
  
  public void store(String filename) throws IOException {
    try {
      store(filename, null);
    } catch (ProgressCancelException pce) {
      throw new Error();
    }
  }
  

  private String getUnnamedRepr(int childIndex) { return "__Unnamed" + childIndex + "__"; }
  
  public String getRepr(int childIndex) {
    String nameValue = name.getStringValue();
    if ((nameValue == null) || (nameValue.length() == 0)) {
      nameValue = getUnnamedRepr(childIndex);
    }
    return nameValue;
  }
  
  public String getRepr() { String nameValue = (String)name.get();
    if (nameValue == null) { int childIndex;
      int childIndex;
      if (m_parent != null) {
        childIndex = m_parent.getIndexOfChild(this);
      } else {
        childIndex = 0;
      }
      nameValue = getUnnamedRepr(childIndex);
    }
    return nameValue;
  }
  
  protected void started(World world, double time) {
    Element[] children = getChildren();
    for (int i = 0; i < children.length; i++)
      children[i].started(world, time);
  }
  
  protected void stopped(World world, double time) {
    Element[] children = getChildren();
    for (int i = 0; i < children.length; i++)
      children[i].stopped(world, time);
  }
  
  public static void warn(Object o) {
    edu.cmu.cs.stage3.alice.scenegraph.Element.warn(o);
  }
  
  public static void warnln(Object o) { edu.cmu.cs.stage3.alice.scenegraph.Element.warnln(o); }
  
  public static void warnln() {}
  
  /**
   * @deprecated
   */
  public static void debug(Object o) { System.err.print(o); }
  /**
   * @deprecated
   */
  public static void debugln(Object o) { System.err.println(o); }
  

  private static Hashtable s_classToElementCache = new Hashtable();
  
  public static Class getValueClassForPropertyNamed(Class elementClass, String propertyName) { Element element = (Element)s_classToElementCache.get(elementClass);
    if (element == null) {
      try {
        element = (Element)elementClass.newInstance();
        s_classToElementCache.put(elementClass, element);
      } catch (InstantiationException ie) {
        return null;
      } catch (IllegalAccessException iae) {
        return null;
      }
    }
    Property property = element.getPropertyNamed(propertyName);
    return property.getValueClass();
  }
  
  public boolean isAccessibleFrom(Element e) {
    return true;
  }
  
  protected void internalAddExpressionIfAssignableTo(Expression expression, Class cls, Vector v) {
    if ((expression != null) && 
      (cls.isAssignableFrom(expression.getValueClass()))) {
      v.addElement(expression);
    }
  }
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v) {
    if (m_parent != null)
      m_parent.internalFindAccessibleExpressions(cls, v);
  }
  
  public Expression[] findAccessibleExpressions(Class cls) {
    Vector v = new Vector();
    internalFindAccessibleExpressions(cls, v);
    Vector newVect = new Vector(new java.util.LinkedHashSet(v));
    Expression[] array = new Expression[newVect.size()];
    newVect.copyInto(array);
    return array;
  }
  
  public String toString() {
    return getClass().getName() + "[" + getKey() + "]";
  }
}

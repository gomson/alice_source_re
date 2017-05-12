package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.ResizeAnimation;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.StringObjectPair;
import edu.cmu.cs.stage3.util.StringTypePair;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ImageIcon;

public class AuthoringToolResources
{
  public static final long startTime = ;
  public static final String QUESTION_STRING = Messages.getString("function");
  public static Criterion characterCriterion = new Criterion() {
    public boolean accept(Object o) {
      return o instanceof Sandbox;
    }
  };
  public static java.io.FileFilter resourceFileFilter = new java.io.FileFilter() {
    public String getDescription() {
      return Messages.getString("resource_files");
    }
    
    public boolean accept(File file) { return (file.isFile()) && (file.canRead()) && (file.getName().toLowerCase().endsWith(".py")); }
  };
  


  protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  protected static Resources resources;
  
  public static class Resources implements java.io.Serializable { public Vector propertyStructure;
    public Vector oneShotStructure;
    public Vector questionStructure;
    public Vector worldTreeChildrenPropertiesStructure;
    public Vector behaviorParameterPropertiesStructure;
    public HashMap nameMap = new HashMap();
    public HashMap htmlNameMap = new HashMap();
    public HashMap formatMap = new HashMap();
    public HashMap propertyValueFormatMap = new HashMap();
    public HashMap unitMap = new HashMap();
    public Class[] classesToOmitNoneFor;
    public StringTypePair[] propertiesToOmitNoneFor;
    public StringTypePair[] propertiesToIncludeNoneFor;
    public StringTypePair[] propertyNamesToOmit;
    public StringTypePair[] propertiesToOmitScriptDefinedFor;
    public Vector defaultPropertyValuesStructure;
    public StringTypePair[] defaultVariableTypes;
    public String[] defaultAspectRatios;
    public Class[] behaviorClasses;
    public String[] parameterizedPropertiesToOmit;
    public String[] responsePropertiesToOmit;
    public String[] oneShotGroupsToInclude;
    public String[] questionPropertiesToOmit;
    public HashMap colorMap = new HashMap();
    public DecimalFormat decimalFormatter = new DecimalFormat("#0.##");
    public HashMap stringImageMap = new HashMap();
    public HashMap stringIconMap = new HashMap();
    public HashMap disabledIconMap = new HashMap();
    public Class[] importers;
    public Class[] editors;
    public HashMap flavorMap = new HashMap();
    public HashMap keyCodesToStrings = new HashMap();
    public boolean experimentalFeaturesEnabled;
    public HashMap miscMap = new HashMap();
    public java.net.URL mainWebGalleryURL = null;
    public File mainDiskGalleryDirectory = null;
    public File mainCDGalleryDirectory = null;
    public java.net.URL mainUpdateURL = null;
    
    public Resources() {} }
  
  protected static File resourcesDirectory;
  protected static File resourcesCacheFile;
  protected static File resourcesPyFile;
  protected static java.io.FilenameFilter pyFilenameFilter = new java.io.FilenameFilter() {
    public boolean accept(File dir, String name) {
      return name.toLowerCase().endsWith(".py");
    }
  };
  
  static {
    resourcesDirectory = new File(JAlice.getAliceHomeDirectory(), "resources" + System.getProperty("file.separator") + AikMin.locale).getAbsoluteFile();
    resourcesCacheFile = new File(resourcesDirectory, "resourcesCache.bin").getAbsoluteFile();
    resourcesPyFile = new File(resourcesDirectory, authoringToolConfig.getValue("resourceFile")).getAbsoluteFile();
    if (!resourcesPyFile.canRead()) {
      resourcesPyFile = new File(resourcesDirectory, "Alice Style.py").getAbsoluteFile();
    }
    loadResourcesPy();
  }
  









  public AuthoringToolResources() {}
  








  public static boolean safeIsDataFlavorSupported(java.awt.dnd.DropTargetDragEvent dtde, java.awt.datatransfer.DataFlavor flavor)
  {
    try
    {
      return dtde.isDataFlavorSupported(flavor);
    }
    catch (Throwable t) {}
    return false;
  }
  
  public static java.awt.datatransfer.DataFlavor[] safeGetCurrentDataFlavors(java.awt.dnd.DropTargetDropEvent dtde)
  {
    try {
      return dtde.getCurrentDataFlavors();
    } catch (Throwable t) {}
    return null;
  }
  
  public static java.awt.datatransfer.DataFlavor[] safeGetCurrentDataFlavors(java.awt.dnd.DropTargetDragEvent dtde)
  {
    try {
      return dtde.getCurrentDataFlavors();
    } catch (Throwable t) {}
    return null;
  }
  
  public static boolean safeIsDataFlavorSupported(java.awt.dnd.DropTargetDropEvent dtde, java.awt.datatransfer.DataFlavor flavor)
  {
    try {
      return dtde.isDataFlavorSupported(flavor);
    }
    catch (Throwable t) {}
    return false;
  }
  
  public static boolean safeIsDataFlavorSupported(java.awt.datatransfer.Transferable transferable, java.awt.datatransfer.DataFlavor flavor)
  {
    try {
      return transferable.isDataFlavorSupported(flavor);
    }
    catch (Throwable t) {}
    return false;
  }
  
  public static boolean isResourcesCacheCurrent()
  {
    long cacheTime = resourcesCacheFile.exists() ? resourcesCacheFile.lastModified() : 0L;
    long mostCurrentPy = getMostCurrentPyTime(resourcesDirectory, 0L);
    
    return cacheTime > mostCurrentPy;
  }
  
  private static long getMostCurrentPyTime(File directory, long mostCurrentPy) {
    File[] files = directory.listFiles();
    for (int i = 0; i < files.length; i++) {
      if (pyFilenameFilter.accept(directory, files[i].getName())) {
        mostCurrentPy = Math.max(mostCurrentPy, files[i].lastModified());
      } else if (files[i].isDirectory()) {
        mostCurrentPy = Math.max(mostCurrentPy, getMostCurrentPyTime(files[i], mostCurrentPy));
      }
    }
    
    return mostCurrentPy;
  }
  
  public static void loadResourcesPy() {
    resources = new Resources();
    org.python.core.PySystemState.initialize();
    org.python.core.PySystemState pySystemState = org.python.core.Py.getSystemState();
    org.python.core.__builtin__.execfile(resourcesPyFile.getAbsolutePath(), builtins, builtins);
    initKeyCodesToStrings();
    initWebGalleryURL();
  }
  
  public static void loadResourcesCache() throws Exception {
    java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(resourcesCacheFile)));
    resources = (Resources)ois.readObject();
    ois.close();
  }
  
  public static void saveResourcesCache() {
    try {
      java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(resourcesCacheFile)));
      oos.writeObject(resources);
      oos.flush();
      oos.close();
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("Unable_to_save_resources_cache_to_") + resourcesCacheFile.getAbsolutePath(), t);
    }
  }
  
  public static void deleteResourcesCache() {
    try {
      resourcesCacheFile.delete();
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("Unable_to_delete_resources_cache_") + resourcesCacheFile.getAbsolutePath(), t);
    }
  }
  
  public static void setPropertyStructure(Vector propertyStructure) {
    if (propertyStructure != null) {
      for (Iterator iter = propertyStructure.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if ((o instanceof StringObjectPair)) {
          String className = ((StringObjectPair)o).getString();
          try {
            Class.forName(className);
          } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(Messages.getString("propertyStructure_error__") + className + " " + Messages.getString("is_not_a_Class"));
          }
        } else {
          throw new IllegalArgumentException(Messages.getString("Unexpected_object_found_in_propertyStructure__") + o);
        }
      }
    }
    
    resourcespropertyStructure = propertyStructure;
  }
  
  public static Vector getPropertyStructure(Class elementClass) {
    if (resourcespropertyStructure != null) {
      for (Iterator iter = resourcespropertyStructure.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if ((o instanceof StringObjectPair)) {
          String className = ((StringObjectPair)o).getString();
          try {
            Class c = Class.forName(className);
            if (!c.isAssignableFrom(elementClass)) continue;
            return (Vector)((StringObjectPair)o).getObject();
          }
          catch (ClassNotFoundException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_class_") + className, e);
          }
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Unexpected_object_found_in_propertyStructure__") + o, null);
        }
      }
    }
    return null;
  }
  
  public static Vector getPropertyStructure(Element element, boolean includeLeftovers) {
    Vector structure = getPropertyStructure(element.getClass());
    
    if ((includeLeftovers) && (structure != null)) {
      Vector usedProperties = new Vector();
      for (Iterator iter = structure.iterator(); iter.hasNext();) {
        StringObjectPair sop = (StringObjectPair)iter.next();
        Vector propertyNames = (Vector)sop.getObject();
        if (propertyNames != null) {
          for (Iterator jter = propertyNames.iterator(); jter.hasNext();) {
            String name = (String)jter.next();
            Property property = element.getPropertyNamed(name);
            if (property != null) {
              usedProperties.add(property);
            }
          }
        }
      }
      
      Vector leftovers = new Vector();
      Property[] properties = element.getProperties();
      for (int i = 0; i < properties.length; i++) {
        if (!usedProperties.contains(properties[i])) {
          leftovers.add(properties[i].getName());
        }
      }
      
      if (leftovers.size() > 0) {
        structure.add(new StringObjectPair("leftovers", leftovers));
      }
    }
    
    return structure;
  }
  

  public static void setOneShotStructure(Vector oneShotStructure)
  {
    if (oneShotStructure != null) {
      for (Iterator iter = oneShotStructure.iterator(); iter.hasNext();) {
        Object classChunk = iter.next();
        if ((classChunk instanceof StringObjectPair)) {
          String className = ((StringObjectPair)classChunk).getString();
          Object groups = ((StringObjectPair)classChunk).getObject();
          

          if ((groups instanceof Vector)) {
            for (Iterator jter = ((Vector)groups).iterator(); jter.hasNext();) {
              Object groupChunk = jter.next();
              if ((groupChunk instanceof StringObjectPair)) {
                Object responseClasses = ((StringObjectPair)groupChunk).getObject();
                if ((responseClasses instanceof Vector)) {
                  for (Iterator kter = ((Vector)responseClasses).iterator(); kter.hasNext();) {
                    Object className2 = kter.next();
                    if ((!(className2 instanceof String)) && (!(className2 instanceof StringObjectPair)))
                    {

                      throw new IllegalArgumentException(Messages.getString("oneShotStructure_error__expected_String_or_StringObjectPair__got__") + className);
                    }
                  }
                }
              } else {
                throw new IllegalArgumentException(Messages.getString("Unexpected_object_found_in_oneShotStructure__") + groupChunk);
              }
            }
          } else {
            throw new IllegalArgumentException(Messages.getString("oneShotStructure_error__expected_Vector__got__") + groups);
          }
          
        }
        else
        {
          throw new IllegalArgumentException(Messages.getString("Unexpected_object_found_in_oneShotStructure__") + classChunk);
        }
      }
    }
    
    resourcesoneShotStructure = oneShotStructure;
  }
  
  public static Vector getOneShotStructure(Class elementClass) {
    if (resourcesoneShotStructure != null) {
      for (Iterator iter = resourcesoneShotStructure.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if ((o instanceof StringObjectPair)) {
          String className = ((StringObjectPair)o).getString();
          try {
            Class c = Class.forName(className);
            if (!c.isAssignableFrom(elementClass)) continue;
            return (Vector)((StringObjectPair)o).getObject();
          }
          catch (ClassNotFoundException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_class_") + className, e);
          }
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Unexpected_object_found_in_oneShotStructure__") + o, null);
        }
      }
    }
    
    return null;
  }
  
  public static void setQuestionStructure(Vector questionStructure)
  {
    if (questionStructure != null) {
      for (Iterator iter = questionStructure.iterator(); iter.hasNext();) {
        Object classChunk = iter.next();
        if ((classChunk instanceof StringObjectPair)) {
          String className = ((StringObjectPair)classChunk).getString();
          Object groups = ((StringObjectPair)classChunk).getObject();
          

          if ((groups instanceof Vector)) {
            for (Iterator jter = ((Vector)groups).iterator(); jter.hasNext();) {
              Object groupChunk = jter.next();
              if ((groupChunk instanceof StringObjectPair)) {
                Object questionClasses = ((StringObjectPair)groupChunk).getObject();
                if ((questionClasses instanceof Vector)) {
                  for (Iterator kter = ((Vector)questionClasses).iterator(); kter.hasNext();) {
                    Object className2 = kter.next();
                    if ((className2 instanceof String)) {
                      try {
                        Class.forName((String)className2);
                      } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException(Messages.getString("questionStructure_error__") + className2 + " " + Messages.getString("is_not_a_Class"));
                      }
                    } else {
                      throw new IllegalArgumentException(Messages.getString("questionStructure_error__expected_String__got__") + className);
                    }
                  }
                }
              } else {
                throw new IllegalArgumentException(Messages.getString("Unexpected_object_found_in_questionStructure__") + groupChunk);
              }
            }
          } else {
            throw new IllegalArgumentException(Messages.getString("questionStructure_error__expected_Vector__got__") + groups);
          }
          
        }
        else
        {
          throw new IllegalArgumentException(Messages.getString("Unexpected_object_found_in_questionStructure__") + classChunk);
        }
      }
    }
    
    resourcesquestionStructure = questionStructure;
  }
  
  public static Vector getQuestionStructure(Class elementClass) {
    if (resourcesquestionStructure != null) {
      for (Iterator iter = resourcesquestionStructure.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if ((o instanceof StringObjectPair)) {
          String className = ((StringObjectPair)o).getString();
          try {
            Class c = Class.forName(className);
            if (!c.isAssignableFrom(elementClass)) continue;
            return (Vector)((StringObjectPair)o).getObject();
          }
          catch (ClassNotFoundException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_class_") + className, e);
          }
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Unexpected_object_found_in_questionStructure__") + o, null);
        }
      }
    }
    
    return null;
  }
  
  public static void setDefaultPropertyValuesStructure(Vector defaultPropertyValuesStructure)
  {
    if (defaultPropertyValuesStructure != null) {
      for (Iterator iter = defaultPropertyValuesStructure.iterator(); iter.hasNext();) {
        Object classChunk = iter.next();
        if ((classChunk instanceof StringObjectPair))
        {
          Object properties = ((StringObjectPair)classChunk).getObject();
          

          if ((properties instanceof Vector)) {
            for (Iterator jter = ((Vector)properties).iterator(); jter.hasNext();) {
              Object propertyChunk = jter.next();
              if ((propertyChunk instanceof StringObjectPair)) {
                Object values = ((StringObjectPair)propertyChunk).getObject();
                if (!(values instanceof Vector)) {
                  throw new IllegalArgumentException(Messages.getString("defaultPropertyValuesStructure_error__expected_Vector__got__") + values);
                }
                

              }
              else
              {
                throw new IllegalArgumentException(Messages.getString("defaultPropertyValuesStructure_error__expected_StringObjectPair__got__") + propertyChunk);
              }
            }
          } else {
            throw new IllegalArgumentException(Messages.getString("defaultPropertyValuesStructure_error__expected_Vector__got__") + properties);
          }
          
        }
        else
        {
          throw new IllegalArgumentException(Messages.getString("defaultPropertyValuesStructure_error__expected_StringObjectPair__got__") + classChunk);
        }
      }
    }
    
    resourcesdefaultPropertyValuesStructure = defaultPropertyValuesStructure;
  }
  
  public static Vector getDefaultPropertyValues(Class elementClass, String propertyName) {
    if (resourcesdefaultPropertyValuesStructure != null) {
      for (Iterator iter = resourcesdefaultPropertyValuesStructure.iterator(); iter.hasNext();) {
        StringObjectPair classChunk = (StringObjectPair)iter.next();
        String className = classChunk.getString();
        try {
          Class c = Class.forName(className);
          if (c.isAssignableFrom(elementClass)) {
            Vector properties = (Vector)classChunk.getObject();
            for (Iterator jter = properties.iterator(); jter.hasNext();) {
              StringObjectPair propertyChunk = (StringObjectPair)jter.next();
              
              if (propertyName.equals(propertyChunk.getString())) {
                return (Vector)propertyChunk.getObject();
              }
            }
          }
        } catch (ClassNotFoundException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_class_") + className, e);
        }
      }
    }
    
    return null;
  }
  
  public static void putName(Object key, String prettyName) {
    resourcesnameMap.put(key, prettyName);
  }
  
  public static String getName(Object key) {
    String a = (String)resourcesnameMap.get(key);
    return a;
  }
  
  public static boolean nameMapContainsKey(Object key) {
    return resourcesnameMap.containsKey(key);
  }
  
  public static void putHTMLName(Object key, String prettyName) {
    resourceshtmlNameMap.put(key, prettyName);
  }
  
  public static String getHTMLName(Object key) {
    return (String)resourceshtmlNameMap.get(key);
  }
  
  public static boolean htmlNameMapContainsKey(Object key) {
    return resourceshtmlNameMap.containsKey(key);
  }
  
  public static void putFormat(Object key, String formatString) {
    resourcesformatMap.put(key, formatString);
  }
  
  public static String getFormat(Object key) {
    return (String)resourcesformatMap.get(key);
  }
  
  public static String getPlainFormat(Object key) {
    String format = (String)resourcesformatMap.get(key);
    StringBuffer sb = new StringBuffer();
    FormatTokenizer tokenizer = new FormatTokenizer(format);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (token.startsWith("<<")) { if (!token.startsWith("<<<")) {}
      } else { while (token.indexOf("&lt;") > -1) {
          token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
        }
        sb.append(token);
      }
    }
    return sb.toString();
  }
  
  public static boolean formatMapContainsKey(Object key) {
    return resourcesformatMap.containsKey(key);
  }
  
  public static void putPropertyValueFormatMap(String propertyKey, HashMap valueReprMap) {
    resourcespropertyValueFormatMap.put(propertyKey, valueReprMap);
  }
  
  public static HashMap getPropertyValueFormatMap(String propertyKey) {
    return (HashMap)resourcespropertyValueFormatMap.get(propertyKey);
  }
  
  public static boolean propertyValueFormatMapContainsKey(String propertyKey) {
    return resourcespropertyValueFormatMap.containsKey(propertyKey);
  }
  
  public static void putUnitString(String key, String unitString) {
    resourcesunitMap.put(key, unitString);
  }
  
  public static String getUnitString(String key) {
    return (String)resourcesunitMap.get(key);
  }
  
  public static boolean unitMapContainsKey(String key) {
    return resourcesunitMap.containsKey(key);
  }
  
  public static java.util.Set getUnitMapKeySet() {
    return resourcesunitMap.keySet();
  }
  
  public static java.util.Collection getUnitMapValues() {
    return resourcesunitMap.values();
  }
  
  public static void setClassesToOmitNoneFor(Class[] classesToOmitNoneFor) {
    resourcesclassesToOmitNoneFor = classesToOmitNoneFor;
  }
  








  public static void setPropertiesToOmitNoneFor(StringTypePair[] propertiesToOmitNoneFor)
  {
    resourcespropertiesToOmitNoneFor = propertiesToOmitNoneFor;
  }
  
  public static void setPropertiesToIncludeNoneFor(StringTypePair[] propertiesToIncludeNoneFor) {
    resourcespropertiesToIncludeNoneFor = propertiesToIncludeNoneFor;
  }
  
  public static boolean shouldGUIOmitNone(Property property) {
    return !shouldGUIIncludeNone(property);
  }
  
  public static boolean shouldGUIIncludeNone(Property property) {
    if (resourcespropertiesToIncludeNoneFor != null) {
      Class elementClass = property.getOwner().getClass();
      String propertyName = property.getName();
      for (int i = 0; i < resourcespropertiesToIncludeNoneFor.length; i++) {
        if ((resourcespropertiesToIncludeNoneFor[i].getType().isAssignableFrom(elementClass)) && (resourcespropertiesToIncludeNoneFor[i].getString().equals(propertyName))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static boolean shouldGUIIncludeNone(Class elementClass, String propertyName) {
    if (resourcespropertiesToIncludeNoneFor != null) {
      for (int i = 0; i < resourcespropertiesToIncludeNoneFor.length; i++) {
        if ((resourcespropertiesToIncludeNoneFor[i].getType().isAssignableFrom(elementClass)) && (resourcespropertiesToIncludeNoneFor[i].getString().equals(propertyName))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static void setPropertyNamesToOmit(StringTypePair[] propertyNamesToOmit) {
    resourcespropertyNamesToOmit = propertyNamesToOmit;
  }
  
  public static boolean shouldGUIOmitPropertyName(Property property) {
    if (resourcespropertyNamesToOmit != null) {
      Class elementClass = property.getOwner().getClass();
      String propertyName = property.getName();
      for (int i = 0; i < resourcespropertyNamesToOmit.length; i++) {
        if (resourcespropertyNamesToOmit[i].getType().isAssignableFrom(elementClass))
        {
          if (resourcespropertyNamesToOmit[i].getString().equals(propertyName))
            return true;
        }
      }
    }
    return false;
  }
  
  public static void setPropertiesToOmitScriptDefinedFor(StringTypePair[] propertiesToOmitScriptDefinedFor) {
    resourcespropertiesToOmitScriptDefinedFor = propertiesToOmitScriptDefinedFor;
  }
  
  public static boolean shouldGUIOmitScriptDefined(Property property) {
    if (!authoringToolConfig.getValue("enableScripting").equalsIgnoreCase("true"))
      return true;
    if (resourcespropertiesToOmitScriptDefinedFor != null) {
      Class elementClass = property.getOwner().getClass();
      String propertyName = property.getName();
      for (int i = 0; i < resourcespropertiesToOmitScriptDefinedFor.length; i++) {
        if ((resourcespropertiesToOmitScriptDefinedFor[i].getType().isAssignableFrom(elementClass)) && (resourcespropertiesToOmitScriptDefinedFor[i].getString().equals(propertyName))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static String getReprForValue(Object value, Property property)
  {
    return getReprForValue(value, property, null);
  }
  
  public static String getReprForValue(Object value, Class elementClass, String propertyName) { return getReprForValue(value, elementClass, propertyName, null); }
  
  public static String getReprForValue(Object value, Property property, Object extraContextInfo) {
    Class elementClass = property.getOwner().getClass();
    String propertyName = property.getName();
    
    if (((property.getOwner() instanceof PropertyAnimation)) && (property.getName().equals("value"))) {
      PropertyAnimation propertyAnimation = (PropertyAnimation)property.getOwner();
      Object e = element.get();
      if ((e instanceof edu.cmu.cs.stage3.alice.core.Expression)) {
        elementClass = ((edu.cmu.cs.stage3.alice.core.Expression)e).getValueClass();
      } else {
        Object elementValue = element.getElementValue();
        if (elementValue != null) {
          elementClass = elementValue.getClass();
        } else {
          elementClass = null;
        }
      }
      propertyName = propertyName.getStringValue();
    }
    else if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment)) && (property.getName().equals("value"))) {
      edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment propertyAssignment = (edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment)property.getOwner();
      elementClass = element.getElementValue().getClass();
      propertyName = propertyName.getStringValue();
    }
    return getReprForValue(value, elementClass, propertyName, extraContextInfo);
  }
  
  public static String getReprForValue(Object value, Class elementClass, String propertyName, Object extraContextInfo) { boolean verbose = false;
    Class valueClass = null;
    try {
      valueClass = Element.getValueClassForPropertyNamed(elementClass, propertyName);
    } catch (Exception e) {
      valueClass = Object.class;
    }
    if (valueClass == null) {
      valueClass = Object.class;
    }
    if ((elementClass == null) || (propertyName == null)) {
      return getReprForValue(value);
    }
    
    if (((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class.isAssignableFrom(elementClass)) && (propertyName.equals("userDefinedResponse"))) || (
      (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class.isAssignableFrom(elementClass)) && (propertyName.equals("userDefinedQuestion"))))
    {
      verbose = true;
    }
    if (((value instanceof edu.cmu.cs.stage3.alice.core.Variable)) && ((((edu.cmu.cs.stage3.alice.core.Variable)value).getParent() instanceof Sandbox))) {
      verbose = true;
    }
    try
    {
      while (Element.class.isAssignableFrom(elementClass)) {
        String propertyKey = elementClass.getName() + "." + propertyName;
        
        String userRepr = null;
        if ((extraContextInfo != null) && (extraContextInfo.equals("menuContext"))) {
          if (propertyValueFormatMapContainsKey(propertyKey + ".menuContext")) {
            propertyKey = propertyKey + ".menuContext";
          }
        } else if ((extraContextInfo instanceof edu.cmu.cs.stage3.alice.core.property.DictionaryProperty)) {
          edu.cmu.cs.stage3.alice.core.property.DictionaryProperty data = (edu.cmu.cs.stage3.alice.core.property.DictionaryProperty)extraContextInfo;
          if (data.getName().equals("data")) {
            Object repr = data.get("edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName);
            if ((repr != null) && 
              ((repr instanceof String))) {
              if ((Number.class.isAssignableFrom(valueClass)) && ((value instanceof Double))) {
                Double d = parseDouble((String)repr);
                if ((d != null) && (d.equals(value))) {
                  userRepr = (String)repr;
                } else {
                  data.remove("edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName);
                }
              } else {
                userRepr = (String)repr;
              }
            }
          }
        }
        

        String reprString = null;
        if (propertyValueFormatMapContainsKey(propertyKey)) {
          HashMap map = getPropertyValueFormatMap(propertyKey);
          if (map.containsKey(value)) {
            reprString = (String)map.get(value);
            String temp = reprString.replaceAll("<\\w*>", "");
            temp = temp.replaceAll("[^a-zA-Z ]", "").trim();
            reprString = reprString.replace(temp, Messages.getString(temp.replace(" ", "_")));
          } else if (value == null) {
            reprString = null;
          } else if (map.containsKey("default")) {
            reprString = (String)map.get("default");
          }
        }
        
        if (reprString != null) {
          Iterator iter = resourcesunitMap.keySet().iterator();
          for (;;) { String key = (String)iter.next();
            String unitString = getUnitString(key);
            String unitExpression = "<" + key + ">";
            while (reprString.indexOf(unitExpression) > -1) {
              StringBuffer sb = new StringBuffer(reprString);
              sb.replace(reprString.indexOf(unitExpression), reprString.indexOf(unitExpression) + unitExpression.length(), Messages.getString(unitString));
              reprString = sb.toString();
            }
            if (!iter.hasNext()) {
              break;
            }
          }
          





          for (;;)
          {
            String valueString = userRepr != null ? userRepr : getReprForValue(value);
            StringBuffer sb = new StringBuffer(reprString);
            sb.replace(reprString.indexOf("<value>"), reprString.indexOf("<value>") + "<value>".length(), valueString);
            reprString = sb.toString();
            if (reprString.indexOf("<value>") <= -1) {
              break;
            }
          }
          
          do
          {
            double v = ((Double)value).doubleValue() * 100.0D;
            String valueString = resourcesdecimalFormatter.format(v) + "%";
            StringBuffer sb = new StringBuffer(reprString);
            sb.replace(reprString.indexOf("<percentValue>"), reprString.indexOf("<percentValue>") + "<percentValue>".length(), valueString);
            reprString = sb.toString();
            if (reprString.indexOf("<percentValue>") <= -1) break; } while ((value instanceof Double));
          





          while ((reprString.indexOf("<keyCodeValue>") > -1) && ((value instanceof Integer))) {
            String valueString = "";
            switch (((Integer)value).intValue()) {
            case 192:  valueString = "`"; break;
            case 45:  valueString = "-"; break;
            case 61:  valueString = "="; break;
            case 91:  valueString = "["; break;
            case 93:  valueString = "]"; break;
            case 92:  valueString = "\\"; break;
            case 59:  valueString = ";"; break;
            case 222:  valueString = "'"; break;
            case 44:  valueString = ","; break;
            case 46:  valueString = "."; break;
            case 47:  valueString = "/"; break;
            default: 
              valueString = java.awt.event.KeyEvent.getKeyText(((Integer)value).intValue());
            }
            
            StringBuffer sb = new StringBuffer(reprString);
            sb.replace(reprString.indexOf("<keyCodeValue>"), reprString.indexOf("<keyCodeValue>") + "<keyCodeValue>".length(), valueString);
            reprString = sb.toString();
          }
          
          return reprString;
        }
        
        elementClass = elementClass.getSuperclass();
      }
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_finding_repr_for_") + value, t);
    }
    return getReprForValue(value, verbose);
  }
  
  public static String getReprForValue(Object value)
  {
    return getReprForValue(value, false);
  }
  
  protected static void initWebGalleryURL() {
    java.net.URL galleryURL = null;
    try {
      galleryURL = new java.net.URL("http://www.alice.org/gallery/");
      File urlFile = new File(JAlice.getAliceHomeDirectory(), "etc/AliceWebGalleryURL.txt").getAbsoluteFile();
      if ((urlFile.exists()) && 
        (urlFile.canRead())) {
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(urlFile));
        String urlString = null;
        do {
          urlString = br.readLine();
        } while ((urlString != null) && (
        
          (urlString.length() <= 0) || (urlString.charAt(0) == '#')));
        


        br.close();
        
        if (urlString != null) {
          urlString = urlString.trim();
          if (urlString.length() > 0) {
            try {
              galleryURL = new java.net.URL(urlString);
            } catch (java.net.MalformedURLException badURL) {
              if (!urlString.startsWith("www")) {}
            }
            





            return;
          }
        }
      }
    }
    catch (Throwable localThrowable) {}finally
    {
      if (galleryURL != null) {
        setMainWebGalleryURL(galleryURL);
      }
    }
  }
  
  protected static String stripUnnamedsFromName(Object value) {
    String toStrip = new String(value.toString());
    String toReturn = "";
    String toMatch = "__Unnamed";
    boolean notDone = true;
    while (notDone) {
      int nextIndex = toStrip.indexOf(toMatch);
      if (nextIndex >= 0) {
        String toAdd = toStrip.substring(0, nextIndex);
        if (toAdd != null) {
          toReturn = toReturn + toAdd;
        }
        String newToStrip = toStrip.substring(nextIndex, toStrip.length());
        if (newToStrip != null) {
          toStrip = newToStrip;
        }
        else {
          notDone = false;
          break;
        }
        nextIndex = toStrip.indexOf(".");
        if (nextIndex >= 0) {
          newToStrip = toStrip.substring(nextIndex + 1, toStrip.length());
          if (newToStrip != null) {
            toStrip = newToStrip;
          }
          else {
            notDone = false;
            break;
          }
        } else {
          notDone = false;
          break;
        }
      }
      else {
        toReturn = toReturn + toStrip;
        notDone = false;
        break;
      }
    }
    return toStrip;
  }
  
  public static String getReprForValue(Object value, boolean verbose) {
    if (nameMapContainsKey(value)) {
      value = getName(value);
    }
    if (formatMapContainsKey(value)) {
      value = getPlainFormat(value);
    }
    if ((value instanceof Class)) {
      value = ((Class)value).getName();
      if (nameMapContainsKey(value)) {
        value = getName(value);
      }
    }
    if ((value instanceof edu.cmu.cs.stage3.util.Enumerable)) {
      value = ((edu.cmu.cs.stage3.util.Enumerable)value).getRepr();
    }
    if ((value instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue)) {
      String propertyName = propertyName.getStringValue();
      Element element = (Element)element.get();
      Class valueClass = element.getClass();
      if ((element instanceof edu.cmu.cs.stage3.alice.core.Expression)) {
        valueClass = ((edu.cmu.cs.stage3.alice.core.Expression)element).getValueClass();
      }
      try {
        Class declaringClass = valueClass.getField(propertyName).getDeclaringClass();
        if (declaringClass != null) {
          String key = declaringClass.getName() + "." + propertyName;
          if (nameMapContainsKey(key)) {
            propertyName = getName(key);
          }
        }
      } catch (NoSuchFieldException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_representing_PropertyValue__can_t_find_") + propertyName + " " + Messages.getString("on_") + valueClass, e);
      }
      
      value = getReprForValue(element, false) + "." + propertyName;
    }
    if (((value instanceof edu.cmu.cs.stage3.alice.core.Question)) && (formatMapContainsKey(value.getClass()))) {
      String questionRepr = "";
      edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question)value;
      String format = getFormat(value.getClass());
      FormatTokenizer formatTokenizer = new FormatTokenizer(format);
      while (formatTokenizer.hasMoreTokens()) {
        String token = formatTokenizer.nextToken();
        if ((token.startsWith("<")) && (token.endsWith(">"))) {
          property = question.getPropertyNamed(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
          if (property != null) {
            questionRepr = questionRepr + getReprForValue(property.get(), property);
          }
        } else {
          while (token.indexOf("&lt;") > -1) { Property property;
            token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
          }
          questionRepr = questionRepr + token;
        }
      }
      
      if (questionRepr.length() > 0) {
        value = questionRepr;
      }
    }
    if ((value instanceof Element)) {
      if (verbose) {
        Element ancestor = ((Element)value).getSandbox();
        if (ancestor != null) {
          ancestor = ancestor.getParent();
        }
        value = ((Element)value).getKey(ancestor);
        value = stripUnnamedsFromName(value);
      } else {
        value = name.getStringValue();
      }
    }
    if ((value instanceof Number)) {
      double d = ((Number)value).doubleValue();
      












































      value = resourcesdecimalFormatter.format(d);
    }
    











    if ((value instanceof javax.vecmath.Vector3d)) {
      javax.vecmath.Vector3d vec = (javax.vecmath.Vector3d)value;
      value = "Vector3( " + resourcesdecimalFormatter.format(x) + ", " + resourcesdecimalFormatter.format(y) + ", " + resourcesdecimalFormatter.format(z) + " )";
    }
    if ((value instanceof javax.vecmath.Matrix4d)) {
      edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44((javax.vecmath.Matrix4d)value);
      edu.cmu.cs.stage3.math.Vector3 position = m.getPosition();
      Quaternion quaternion = m.getAxes().getQuaternion();
      value = Messages.getString("position__") + resourcesdecimalFormatter.format(x) + ", " + resourcesdecimalFormatter.format(y) + ", " + resourcesdecimalFormatter.format(z) + ";  " + 
        Messages.getString("orientation___") + resourcesdecimalFormatter.format(x) + ", " + resourcesdecimalFormatter.format(y) + ", " + resourcesdecimalFormatter.format(z) + ") " + resourcesdecimalFormatter.format(w);
    }
    if ((value instanceof Quaternion)) {
      Quaternion quaternion = (Quaternion)value;
      value = "(" + resourcesdecimalFormatter.format(x) + ", " + resourcesdecimalFormatter.format(y) + ", " + resourcesdecimalFormatter.format(z) + ") " + resourcesdecimalFormatter.format(w);
    }
    if ((value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)) {
      edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
      if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK)) {
        value = Messages.getString("black");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE)) {
        value = Messages.getString("blue");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN)) {
        value = Messages.getString("brown");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN)) {
        value = Messages.getString("cyan");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY)) {
        value = Messages.getString("dark_gray");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY)) {
        value = Messages.getString("gray");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN)) {
        value = Messages.getString("green");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY)) {
        value = Messages.getString("light_gray");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA)) {
        value = Messages.getString("magenta");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE)) {
        value = Messages.getString("orange");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.PINK)) {
        value = Messages.getString("pink");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE)) {
        value = Messages.getString("purple");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.RED)) {
        value = Messages.getString("red");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE)) {
        value = Messages.getString("white");
      } else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW)) {
        value = Messages.getString("yellow");
      } else {
        value = Messages.getString("Color_r_") + resourcesdecimalFormatter.format(color.getRed()) + ", g:" + resourcesdecimalFormatter.format(color.getGreen()) + ", b:" + resourcesdecimalFormatter.format(color.getBlue()) + ", a:" + resourcesdecimalFormatter.format(color.getAlpha()) + ")";
      }
    }
    if ((value instanceof Property)) {
      String simpleName = ((Property)value).getName();
      if (((Property)value).getDeclaredClass() != null) {
        String key = ((Property)value).getDeclaredClass().getName() + "." + ((Property)value).getName();
        if (nameMapContainsKey(key)) {
          simpleName = getName(key);
        } else {
          simpleName = ((Property)value).getName();
        }
      }
      
      if ((((Property)value).getOwner() instanceof edu.cmu.cs.stage3.alice.core.Variable)) {
        value = getReprForValue(((Property)value).getOwner(), verbose);
      } else if ((verbose) && (((Property)value).getOwner() != null)) {
        value = getReprForValue(((Property)value).getOwner()) + "." + simpleName;
      } else {
        value = simpleName;
      }
    }
    if (value == null) {
      value = Messages.getString("_None_");
    }
    
    return value.toString();
  }
  
  public static String getFormattedReprForValue(Object value, StringObjectPair[] knownPropertyValues) {
    String format = (String)resourcesformatMap.get(value);
    StringBuffer sb = new StringBuffer();
    FormatTokenizer tokenizer = new FormatTokenizer(format);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if ((token.startsWith("<<<")) && (token.endsWith(">>>"))) {
        String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
        for (int i = 0; i < knownPropertyValues.length; i++) {
          if (knownPropertyValues[i].getString().equals(propertyName)) {
            sb.append(getReprForValue(knownPropertyValues[i].getObject(), true));
            break;
          }
        }
      } else if ((!token.startsWith("<<")) || (!token.endsWith(">>")))
      {
        if ((token.startsWith("<")) && (token.endsWith(">"))) {
          String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
          boolean appendedValue = false;
          for (int i = 0; i < knownPropertyValues.length; i++) {
            if (knownPropertyValues[i].getString().equals(propertyName)) {
              sb.append(getReprForValue(knownPropertyValues[i].getObject(), true));
              appendedValue = true;
              break;
            }
          }
          if (!appendedValue) {
            sb.append(token);
          }
        } else {
          sb.append(token);
        }
      } }
    return sb.toString();
  }
  

  public static String getNameInContext(Element element, Element context)
  {
    if ((element instanceof edu.cmu.cs.stage3.alice.core.Variable)) {
      if (element.getParent() != null) {
        Element variableRoot = element.getParent();
        
        if (((variableRoot instanceof Response)) && ((context.isDescendantOf(variableRoot)) || (context == variableRoot))) {
          return name.getStringValue();
        }
      }
    } else if (((element instanceof edu.cmu.cs.stage3.alice.core.Sound)) && ((context instanceof edu.cmu.cs.stage3.alice.core.response.SoundResponse))) {
      edu.cmu.cs.stage3.alice.core.Sound sound = (edu.cmu.cs.stage3.alice.core.Sound)element;
      String s = getReprForValue(element, true);
      double t = NaN.0D;
      edu.cmu.cs.stage3.media.DataSource dataSourceValue = dataSource.getDataSourceValue();
      if (dataSourceValue != null) {
        t = dataSourceValue.getDuration(true);
      }
      return s + " (" + formatTime(t) + ")";
    }
    
    return getReprForValue(element, true);
  }
  
  public static void setDefaultVariableTypes(StringTypePair[] defaultVariableTypes) {
    resourcesdefaultVariableTypes = defaultVariableTypes;
  }
  
  public static StringTypePair[] getDefaultVariableTypes() {
    return resourcesdefaultVariableTypes;
  }
  
  public static void setDefaultAspectRatios(String[] defaultAspectRatios) {
    resourcesdefaultAspectRatios = defaultAspectRatios;
  }
  
  public static String[] getDefaultAspectRatios() {
    return resourcesdefaultAspectRatios;
  }
  
  public static String[] getInitialVisibleProperties(Class elementClass) {
    java.util.LinkedList visible = new java.util.LinkedList();
    String format = getFormat(elementClass);
    FormatTokenizer tokenizer = new FormatTokenizer(format);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if ((token.startsWith("<<<")) && (token.endsWith(">>>"))) {
        visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
      } else if ((token.startsWith("<<")) && (token.endsWith(">>"))) {
        visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
      } else if ((token.startsWith("<")) && (token.endsWith(">"))) {
        visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
      }
    }
    
    return (String[])visible.toArray(new String[0]);
  }
  
  public static String[] getDesiredProperties(Class elementClass) {
    java.util.LinkedList desired = new java.util.LinkedList();
    FormatTokenizer tokenizer = new FormatTokenizer(getFormat(elementClass));
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if ((!token.startsWith("<<<")) || (!token.endsWith(">>>")))
      {

        if ((token.startsWith("<<")) && (token.endsWith(">>")))
        {
          desired.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
        } else if ((token.startsWith("<")) && (token.endsWith(">")))
        {
          desired.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
        }
      }
    }
    return (String[])desired.toArray(new String[0]);
  }
  
  public static void setBehaviorClasses(Class[] behaviorClasses) {
    resourcesbehaviorClasses = behaviorClasses;
  }
  
  public static Class[] getBehaviorClasses() {
    return resourcesbehaviorClasses;
  }
  
  public static void setParameterizedPropertiesToOmit(String[] parameterizedPropertiesToOmit) {
    String[] temp = new String[parameterizedPropertiesToOmit.length];
    for (int i = 0; i < parameterizedPropertiesToOmit.length; i++)
    {
      temp[i] = parameterizedPropertiesToOmit[i];
    }
    resourcesparameterizedPropertiesToOmit = temp;
  }
  
  public static String[] getParameterizedPropertiesToOmit() {
    return resourcesparameterizedPropertiesToOmit;
  }
  
  public static void setOneShotGroupsToInclude(String[] oneShotGroupsToInclude) {
    resourcesoneShotGroupsToInclude = oneShotGroupsToInclude;
  }
  
  public static String[] getOneShotGroupsToInclude() {
    return resourcesoneShotGroupsToInclude;
  }
  
  public static void setBehaviorParameterPropertiesStructure(Vector behaviorParameterPropertiesStructure)
  {
    resourcesbehaviorParameterPropertiesStructure = behaviorParameterPropertiesStructure;
  }
  
  public static String[] getBehaviorParameterProperties(Class behaviorClass) {
    if (resourcesbehaviorParameterPropertiesStructure != null) {
      for (Iterator iter = resourcesbehaviorParameterPropertiesStructure.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if ((o instanceof StringObjectPair)) {
          String className = ((StringObjectPair)o).getString();
          try {
            Class c = Class.forName(className);
            if (!c.isAssignableFrom(behaviorClass)) continue;
            return (String[])((StringObjectPair)o).getObject();
          }
          catch (ClassNotFoundException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_class_") + className, e);
          }
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Unexpected_object_found_in_behaviorParameterPropertiesStructure__") + o, null);
        }
      }
    }
    
    return null;
  }
  
  public static void putColor(String key, java.awt.Color color) {
    resourcescolorMap.put(key, color);
  }
  
  private static float[] rgbToHSL(java.awt.Color rgb) {
    float[] rgbF = rgb.getRGBColorComponents(null);
    float[] hsl = new float[3];
    float min = Math.min(rgbF[0], Math.min(rgbF[1], rgbF[2]));
    float max = Math.max(rgbF[0], Math.max(rgbF[1], rgbF[2]));
    float delta = max - min;
    
    hsl[2] = ((max + min) / 2.0F);
    
    if (delta == 0.0F) {
      hsl[0] = 0.0F;
      hsl[1] = 0.0F;
    } else {
      if (hsl[2] < 0.5D) {
        hsl[1] = (delta / (max + min));
      }
      else {
        hsl[1] = (delta / (2.0F - max - min));
      }
      
      float delR = ((max - rgbF[0]) / 6.0F + delta / 2.0F) / delta;
      float delG = ((max - rgbF[1]) / 6.0F + delta / 2.0F) / delta;
      float delB = ((max - rgbF[2]) / 6.0F + delta / 2.0F) / delta;
      if (rgbF[0] == max) {
        hsl[0] = (delB - delG);
      } else if (rgbF[1] == max) {
        hsl[0] = (0.33333334F + delR - delB);
      } else if (rgbF[2] == max) {
        hsl[0] = (0.6666667F + delG - delR);
      }
      
      if (hsl[0] < 0.0F) {
        hsl[0] += 1.0F;
      }
      if (hsl[0] > 1.0F) {
        hsl[0] -= 1.0F;
      }
    }
    
    return hsl;
  }
  
  private static float hueToRGB(float v1, float v2, float vH) {
    if (vH < 0.0F) vH += 1.0F;
    if (vH > 1.0F) vH -= 1.0F;
    if (6.0F * vH < 1.0F) return v1 + (v2 - v1) * 6.0F * vH;
    if (2.0F * vH < 1.0F) return v2;
    if (3.0F * vH < 2.0F) return v1 + (v2 - v1) * (0.6666667F - vH) * 6.0F;
    return v1;
  }
  
  private static java.awt.Color hslToRGB(float[] hsl)
  {
    if (hsl[1] == 0.0F)
    {
      return new java.awt.Color(hsl[2], hsl[2], hsl[2]);
    }
    float var_2 = 0.0F;
    if (hsl[2] < 0.5D) {
      var_2 = hsl[2] * (1.0F + hsl[1]);
    } else {
      var_2 = hsl[2] + hsl[1] - hsl[1] * hsl[2];
    }
    float var_1 = 2.0F * hsl[2] - var_2;
    float R = Math.min(1.0F, hueToRGB(var_1, var_2, hsl[0] + 0.33333334F));
    float G = Math.min(1.0F, hueToRGB(var_1, var_2, hsl[0]));
    float B = Math.min(1.0F, hueToRGB(var_1, var_2, hsl[0] - 0.33333334F));
    
    return new java.awt.Color(R, G, B);
  }
  














  public static java.awt.Color getColor(String key)
  {
    java.awt.Color toReturn = (java.awt.Color)resourcescolorMap.get(key);
    if ((authoringToolConfig.getValue("enableHighContrastMode").equalsIgnoreCase("true")) && 
      (!key.equalsIgnoreCase("mainFontColor")) && 
      (!key.equalsIgnoreCase("objectTreeDisabledText")) && 
      (!key.equalsIgnoreCase("objectTreeSelectedText")) && 
      (!key.equalsIgnoreCase("disabledHTMLText")) && 
      (!key.equalsIgnoreCase("disabledHTML")) && 
      (!key.equalsIgnoreCase("stdErrTextColor")) && 
      (!key.equalsIgnoreCase("commentForeground")) && 
      (!key.equalsIgnoreCase("objectTreeSelected")) && 
      (!key.equalsIgnoreCase("dndHighlight")) && 
      (!key.equalsIgnoreCase("dndHighlight2")) && 
      (!key.equalsIgnoreCase("dndHighlight3")) && 
      (!key.equalsIgnoreCase("guiEffectsShadow")) && 
      (!key.equalsIgnoreCase("guiEffectsEdge")) && 
      (!key.equalsIgnoreCase("guiEffectsTroughShadow")) && 
      (!key.equalsIgnoreCase("guiEffectsDisabledLine")) && 
      (!key.equalsIgnoreCase("makeSceneEditorBigBackground")) && 
      (!key.equalsIgnoreCase("makeSceneEditorSmallBackground")) && 
      (!key.equalsIgnoreCase("objectTreeText"))) {
      float[] hsl = rgbToHSL(toReturn);
      hsl[2] = Math.max(hsl[2], 0.95F);
      java.awt.Color convertedColor = hslToRGB(hsl);
      return new java.awt.Color(convertedColor.getRed(), convertedColor.getGreen(), convertedColor.getBlue(), toReturn.getAlpha());
    }
    return toReturn;
  }
  
  public static void setMainWebGalleryURL(java.net.URL url)
  {
    resourcesmainWebGalleryURL = url;
  }
  
  public static java.net.URL getMainWebGalleryURL() {
    return resourcesmainWebGalleryURL;
  }
  
  public static void setMainDiskGalleryDirectory(File file) {
    resourcesmainDiskGalleryDirectory = file;
  }
  
  public static File getMainDiskGalleryDirectory() {
    return resourcesmainDiskGalleryDirectory;
  }
  
  public static void setMainCDGalleryDirectory(File file) {
    resourcesmainCDGalleryDirectory = file;
  }
  
  public static File getMainCDGalleryDirectory() {
    return resourcesmainCDGalleryDirectory;
  }
  
  public static void setMainUpdateURL(java.net.URL url) {
    resourcesmainUpdateURL = url;
  }
  
  public static java.net.URL getMainUpdateURL() {
    return resourcesmainUpdateURL;
  }
  
  public static void autodetectMainCDGalleryDirectory(String galleryName) {
    File[] cdRoots = edu.cmu.cs.stage3.alice.authoringtool.util.CDUtil.getCDRoots();
    
    for (int i = 0; i < cdRoots.length; i++) {
      if ((cdRoots[i].exists()) && (cdRoots[i].canRead())) {
        File potentialDir = new File(cdRoots[i], galleryName);
        if ((potentialDir.exists()) && (potentialDir.canRead())) {
          setMainCDGalleryDirectory(potentialDir);
          break;
        }
      }
    }
  }
  
  public static java.awt.Image getAliceSystemIconImage() {
    return getImageForString("aliceHead");
  }
  
  public static ImageIcon getAliceSystemIcon() { return getIconForString("aliceHead"); }
  

  public static java.awt.Image getImageForString(String s)
  {
    if (!resourcesstringImageMap.containsKey(s)) {
      java.net.URL resource = AuthoringToolResources.class.getResource("images/" + s + ".gif");
      if (resource == null) {
        resource = AuthoringToolResources.class.getResource("images/" + s + ".png");
      }
      if (resource == null) {
        resource = AuthoringToolResources.class.getResource("images/" + s + ".jpg");
      }
      if (resource != null) {
        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(resource);
        resourcesstringImageMap.put(s, image);
      } else {
        return null;
      }
    }
    
    return (java.awt.Image)resourcesstringImageMap.get(s);
  }
  
  public static ImageIcon getIconForString(String s) {
    if (!resourcesstringIconMap.containsKey(s)) {
      java.net.URL resource = AuthoringToolResources.class.getResource("images/" + s + ".gif");
      if (resource == null) {
        resource = AuthoringToolResources.class.getResource("images/" + s + ".png");
      }
      if (resource == null) {
        resource = AuthoringToolResources.class.getResource("images/" + s + ".jpg");
      }
      if (resource != null) {
        resourcesstringIconMap.put(s, new ImageIcon(resource));
      } else {
        return null;
      }
    }
    
    return (ImageIcon)resourcesstringIconMap.get(s);
  }
  
  static final ImageIcon cameraIcon = getIconForString("camera");
  static final ImageIcon ambientLightIcon = getIconForString("ambientLight");
  static final ImageIcon directionalLightIcon = getIconForString("directionalLight");
  static final ImageIcon pointLightIcon = getIconForString("pointLight");
  static final ImageIcon defaultLightIcon = getIconForString("pointLight");
  static final ImageIcon modelIcon = getIconForString("model");
  static final ImageIcon subpartIcon = getIconForString("subpart");
  static final ImageIcon sceneIcon = getIconForString("scene");
  static final ImageIcon folderIcon = getIconForString("folder");
  static final ImageIcon defaultIcon = getIconForString("default");
  
  public static ImageIcon getIconForValue(Object value) {
    if ((value instanceof edu.cmu.cs.stage3.alice.core.Camera))
      return cameraIcon;
    if ((value instanceof edu.cmu.cs.stage3.alice.core.light.AmbientLight))
      return ambientLightIcon;
    if ((value instanceof edu.cmu.cs.stage3.alice.core.light.DirectionalLight))
      return directionalLightIcon;
    if ((value instanceof edu.cmu.cs.stage3.alice.core.light.PointLight))
      return pointLightIcon;
    if ((value instanceof edu.cmu.cs.stage3.alice.core.Light))
      return defaultLightIcon;
    if ((value instanceof Transformable)) {
      if ((((Transformable)value).getParent() instanceof Transformable)) {
        return subpartIcon;
      }
      return modelIcon;
    }
    if ((value instanceof World))
      return sceneIcon;
    if ((value instanceof edu.cmu.cs.stage3.alice.core.Group))
      return folderIcon;
    if ((value instanceof java.awt.Image))
      return new ImageIcon((java.awt.Image)value);
    if ((value instanceof String))
      return getIconForString((String)value);
    if ((value instanceof Integer)) {
      String s = (String)resourceskeyCodesToStrings.get(value);
      if (s != null) {
        return getIconForString("keyboardKeys/" + s);
      }
      return null;
    }
    
    return defaultIcon;
  }
  

  public static ImageIcon getDisabledIcon(ImageIcon inputIcon)
  {
    return getDisabledIcon(inputIcon, 70);
  }
  
  public static ImageIcon getDisabledIcon(ImageIcon inputIcon, int percentGray) {
    ImageIcon disabledIcon = (ImageIcon)resourcesdisabledIconMap.get(inputIcon);
    
    if (disabledIcon == null) {
      javax.swing.GrayFilter filter = new javax.swing.GrayFilter(true, percentGray);
      java.awt.image.ImageProducer producer = new java.awt.image.FilteredImageSource(inputIcon.getImage().getSource(), filter);
      java.awt.Image grayImage = java.awt.Toolkit.getDefaultToolkit().createImage(producer);
      disabledIcon = new ImageIcon(grayImage);
      resourcesdisabledIconMap.put(inputIcon, disabledIcon);
    }
    
    return disabledIcon;
  }
  
  public static void openURL(String urlString) throws java.io.IOException {
    if (AikMin.isWindows()) {
      String[] cmdarray = new String[3];
      cmdarray[0] = "rundll32";
      cmdarray[1] = "url.dll,FileProtocolHandler";
      cmdarray[2] = urlString;
      
      if (urlString.indexOf("&stacktrace") > -1) {
        try {
          File tempURL = File.createTempFile("tempURLHolder", ".url");
          tempURL = tempURL.getAbsoluteFile();
          tempURL.deleteOnExit();
          java.io.PrintWriter urlWriter = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(tempURL)));
          urlWriter.println("[InternetShortcut]");
          urlWriter.println("URL=" + urlString);
          urlWriter.flush();
          urlWriter.close();
          cmdarray[2] = tempURL.getAbsolutePath();
        } catch (Throwable t) {
          cmdarray[2] = urlString.substring(0, urlString.indexOf("&stacktrace"));
        }
      }
      
      Runtime.getRuntime().exec(cmdarray);










    }
    else
    {










      try
      {









        String[] cmd = { "netscape", urlString };
        Runtime.getRuntime().exec(cmd);
      } catch (Throwable t) {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        if (lcOSName.startsWith("Mac os x")) {
          Runtime.getRuntime().exec("open " + urlString);
        }
      }
    }
  }
  





























  public static boolean equals(Object o1, Object o2)
  {
    if (o1 == null) {
      return o2 == null;
    }
    return o1.equals(o2);
  }
  
  public static Double parseDouble(String doubleString)
  {
    Double number = null;
    if (doubleString.trim().equalsIgnoreCase(Messages.getString("infinity"))) {
      number = new Double(Double.POSITIVE_INFINITY);
    } else if (doubleString.trim().equalsIgnoreCase(Messages.getString("infinity"))) {
      number = new Double(Double.NEGATIVE_INFINITY);
    } else if (doubleString.indexOf('/') > -1) {
      if (doubleString.lastIndexOf('/') == doubleString.indexOf('/')) {
        String numeratorString = doubleString.substring(0, doubleString.indexOf('/'));
        String denominatorString = doubleString.substring(doubleString.indexOf('/') + 1);
        try {
          number = new Double(Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString));
        } catch (NumberFormatException localNumberFormatException) {}
      }
    } else {
      try {
        number = Double.valueOf(doubleString);
      }
      catch (NumberFormatException localNumberFormatException1) {}
    }
    return number;
  }
  


  public static edu.cmu.cs.stage3.alice.core.Group getDummyObjectGroup(World world)
  {
    Element[] groups = world.getChildren(edu.cmu.cs.stage3.alice.core.Group.class);
    for (int i = 0; i < groups.length; i++) {
      if ((data.get("dummyObjectGroup") != null) && (data.get("dummyObjectGroup").equals("true")) && (world.groups.contains(groups[i]))) {
        return (edu.cmu.cs.stage3.alice.core.Group)groups[i];
      }
    }
    
    edu.cmu.cs.stage3.alice.core.Group dummyObjectGroup = new edu.cmu.cs.stage3.alice.core.Group();
    name.set(Messages.getString("Dummy_Objects"));
    data.put("dummyObjectGroup", "true");
    valueClass.set(edu.cmu.cs.stage3.alice.core.Dummy.class);
    world.addChild(dummyObjectGroup);
    world.groups.add(dummyObjectGroup);
    return dummyObjectGroup;
  }
  
  public static boolean hasDummyObjectGroup(World world) {
    if (world != null) {
      Element[] groups = world.getChildren(edu.cmu.cs.stage3.alice.core.Group.class);
      for (int i = 0; i < groups.length; i++) {
        if ((data.get("dummyObjectGroup") != null) && (data.get("dummyObjectGroup").equals("true")) && (world.groups.contains(groups[i]))) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  public static boolean isMethodHookedUp(Response response, World world) {
    return isMethodHookedUp(response, world, new Vector());
  }
  
  private static boolean isMethodHookedUp(Response response, World world, Vector checkedMethods) {
    edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = response.getRoot().getPropertyReferencesTo(response, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, true);
    for (int i = 0; i < references.length; i++) {
      Element referrer = references[i].getProperty().getOwner();
      if (behaviors.contains(referrer))
        return true;
      if (((referrer instanceof Response)) && (!checkedMethods.contains(referrer))) {
        checkedMethods.add(referrer);
        if (isMethodHookedUp((Response)referrer, world, checkedMethods)) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  public static Response createUndoResponse(Response response) {
    Response undoResponse = null;
    
    Class responseClass = response.getClass();
    if ((response instanceof ResizeAnimation)) {
      ResizeAnimation resizeResponse = (ResizeAnimation)response;
      ResizeAnimation undoResizeResponse = new ResizeAnimation();
      
      amount.set(new Double(1.0D / amount.doubleValue()));
      asSeenBy.set(asSeenBy.get());
      dimension.set(dimension.get());
      likeRubber.set(likeRubber.get());
      subject.set(subject.get());
      
      undoResponse = undoResizeResponse;
    } else if ((response instanceof DirectionAmountTransformAnimation)) {
      try {
        undoResponse = (DirectionAmountTransformAnimation)responseClass.newInstance();
        edu.cmu.cs.stage3.alice.core.Direction direction = (edu.cmu.cs.stage3.alice.core.Direction)direction.getValue();
        edu.cmu.cs.stage3.alice.core.Direction opposite = new edu.cmu.cs.stage3.alice.core.Direction(
          direction.getMoveAxis() == null ? null : edu.cmu.cs.stage3.math.Vector3.negate(direction.getMoveAxis()), 
          direction.getTurnAxis() == null ? null : edu.cmu.cs.stage3.math.Vector3.negate(direction.getTurnAxis()), 
          direction.getRollAxis() == null ? null : edu.cmu.cs.stage3.math.Vector3.negate(direction.getRollAxis()));
        
        subject.set(subject.get());
        amount.set(amount.get());
        direction.set(opposite);
        asSeenBy.set(asSeenBy.get());
        style.set(style.get());
      } catch (IllegalAccessException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_creating_new_response__") + responseClass, e);
      } catch (InstantiationException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_creating_new_response__") + responseClass, e);
      }
    } else if ((response instanceof edu.cmu.cs.stage3.alice.core.response.TransformAnimation)) {
      undoResponse = new PropertyAnimation();
      Transformable transformable = (Transformable)subject.getValue();
      edu.cmu.cs.stage3.math.Matrix44 localTransformation = transformable.getLocalTransformation();
      element.set(transformable);
      propertyName.set(localTransformation.getName());
      value.set(localTransformation);
      howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
    } else if ((response instanceof PropertyAnimation)) {
      undoResponse = new PropertyAnimation();
      Element element = element.getElementValue();
      element.set(element);
      propertyName.set(propertyName.get());
      value.set(element.getPropertyNamed(propertyName.getStringValue()).getValue());
      howMuch.set(howMuch.get());
    } else if (((response instanceof edu.cmu.cs.stage3.alice.core.response.SayAnimation)) || 
      ((response instanceof edu.cmu.cs.stage3.alice.core.response.ThinkAnimation)) || 
      ((response instanceof edu.cmu.cs.stage3.alice.core.response.Wait)) || 
      ((response instanceof edu.cmu.cs.stage3.alice.core.response.SoundResponse))) {
      undoResponse = new edu.cmu.cs.stage3.alice.core.response.Wait();
      duration.set(new Double(0.0D));
    } else if ((response instanceof edu.cmu.cs.stage3.alice.core.response.PoseAnimation)) {
      edu.cmu.cs.stage3.alice.core.response.PoseAnimation poseAnim = (edu.cmu.cs.stage3.alice.core.response.PoseAnimation)response;
      undoResponse = new edu.cmu.cs.stage3.alice.core.response.PoseAnimation();
      Transformable subject = (Transformable)subject.get();
      edu.cmu.cs.stage3.alice.core.Pose currentPose = edu.cmu.cs.stage3.alice.core.Pose.manufacturePose(subject, subject);
      subject.set(subject);
      pose.set(currentPose);
    }
    

    if (undoResponse != null) {
      duration.set(duration.get());
    } else {
      undoResponse = new edu.cmu.cs.stage3.alice.core.response.Wait();
      duration.set(new Double(0.0D));
      AuthoringTool.showErrorDialog(Messages.getString("Could_not_create_undoResponse_for_") + response, null);
    }
    
    return undoResponse;
  }
  
  public static void addAffectedProperties(java.util.List affectedProperties, Element element, String propertyName, edu.cmu.cs.stage3.util.HowMuch howMuch) {
    Property property = element.getPropertyNamed(propertyName);
    if (property != null) {
      affectedProperties.add(property);
    }
    if (howMuch.getDescend()) {
      for (int i = 0; i < element.getChildCount(); i++) {
        Element child = element.getChildAt(i);
        if ((!isFirstClass.booleanValue()) || (!howMuch.getRespectDescendant()))
        {

          addAffectedProperties(affectedProperties, child, propertyName, howMuch);
        }
      }
    }
  }
  


  public static Property[] getAffectedProperties(Response response)
  {
    Property[] properties = null;
    
    if ((response instanceof ResizeAnimation)) {
      Transformable transformable = (Transformable)subject.getElementValue();
      Vector pVector = new Vector();
      pVector.add(localTransformation);
      if ((transformable instanceof edu.cmu.cs.stage3.alice.core.Model)) {
        pVector.add(visualScale);
      }
      Transformable[] descendants = (Transformable[])transformable.getDescendants(Transformable.class);
      for (int i = 0; i < descendants.length; i++) {
        pVector.add(localTransformation);
        if ((descendants[i] instanceof edu.cmu.cs.stage3.alice.core.Model)) {
          pVector.add(visualScale);
        }
      }
      properties = (Property[])pVector.toArray(new Property[0]);
    } else if ((response instanceof edu.cmu.cs.stage3.alice.core.response.TransformAnimation)) {
      Transformable transformable = (Transformable)subject.getElementValue();
      properties = new Property[] { localTransformation };
    } else if ((response instanceof edu.cmu.cs.stage3.alice.core.response.TransformResponse)) {
      Transformable transformable = (Transformable)subject.getElementValue();
      properties = new Property[] { localTransformation };
    } else if ((response instanceof PropertyAnimation)) {
      Element element = element.getElementValue();
      String propertyName = propertyName.getStringValue();
      edu.cmu.cs.stage3.util.HowMuch howMuch = (edu.cmu.cs.stage3.util.HowMuch)howMuch.getValue();
      
      java.util.LinkedList propertyList = new java.util.LinkedList();
      addAffectedProperties(propertyList, element, propertyName, howMuch);
      properties = (Property[])propertyList.toArray(new Property[0]);
    }
    
    if (properties == null) {
      properties = new Property[0];
    }
    
    return properties;
  }
  
  public static edu.cmu.cs.stage3.alice.core.Billboard makeBillboard(edu.cmu.cs.stage3.alice.core.TextureMap textureMap, boolean makeTextureChild) {
    java.awt.image.ImageObserver sizeObserver = new java.awt.image.ImageObserver() {
      public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
        return (infoflags & 0x1 & 0x2) > 0;
      }
    };
    
    if (textureMap != null) {
      int imageWidth = image.getImageValue().getWidth(sizeObserver);
      int imageHeight = image.getImageValue().getHeight(sizeObserver);
      double aspectRatio = imageWidth / imageHeight;
      double height;
      double width; double height; if (aspectRatio < 1.0D) {
        double width = 1.0D;
        height = 1.0D / aspectRatio;
      } else {
        width = aspectRatio;
        height = 1.0D;
      }
      
      edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = {
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F), 
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0D, height, 0.0D, 0.0D, 0.0D, 1.0D, 0.0F, 1.0F), 
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0D, height, 0.0D, 0.0D, 0.0D, 1.0D, 1.0F, 1.0F), 
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0F, 0.0F), 
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0D, 0.0D, 0.0D, 0.0D, 0.0D, -1.0D, 1.0F, 0.0F), 
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0D, height, 0.0D, 0.0D, 0.0D, -1.0D, 1.0F, 1.0F), 
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0D, height, 0.0D, 0.0D, 0.0D, -1.0D, 0.0F, 1.0F), 
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0D, 0.0D, 0.0D, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F) };
      
      int[] indices = {
        0, 1, 2, 
        0, 2, 3, 
        4, 5, 6, 
        4, 6, 7 };
      

      edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray geom = new edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray();
      vertices.set(vertices);
      indices.set(indices);
      
      edu.cmu.cs.stage3.alice.core.Billboard billboard = new edu.cmu.cs.stage3.alice.core.Billboard();
      isFirstClass.set(true);
      geometries.add(geom);
      geometry.set(geom);
      billboard.addChild(geom);
      
      if (makeTextureChild) {
        if (textureMap.getParent() != null) {
          textureMap.removeFromParent();
        }
        billboard.addChild(textureMap);
        textureMaps.add(textureMap);
        diffuseColorMap.set(textureMap);
        name.set(name.getStringValue());
        name.set(name.getStringValue() + "_Texture");
      } else {
        name.set(name.getStringValue() + "_Billboard");
        diffuseColorMap.set(textureMap);
      }
      
      return billboard;
    }
    
    return null;
  }
  
  public static void centerComponentOnScreen(Component c) {
    Dimension size = c.getSize();
    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    
    int x = width / 2 - width / 2;
    int y = height / 2 - height / 2;
    
    c.setLocation(x, y);
  }
  
  public static void ensureComponentIsOnScreen(Component c) {
    java.awt.Point location = c.getLocation();
    Dimension size = c.getSize();
    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    height -= 28;
    
    if (!(c instanceof java.awt.Window)) {
      javax.swing.SwingUtilities.convertPointToScreen(location, c.getParent());
    }
    
    if (x < 0) {
      x = 0;
    } else if (x + width > width) {
      x -= x + width - width;
    }
    if (y < 0) {
      y = 0;
    } else if (y + height > height) {
      y -= y + height - height;
    }
    
    if (!(c instanceof java.awt.Window)) {
      javax.swing.SwingUtilities.convertPointFromScreen(location, c.getParent());
    }
    
    c.setLocation(location);
  }
  
  public static String getNameForNewChild(String baseName, Element parent) {
    String name = baseName;
    
    if (name != null) {
      name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
    }
    if ((name == null) || (parent == null)) {
      return name;
    }
    
    if ((parent.getChildNamedIgnoreCase(name) == null) && (parent.getChildNamedIgnoreCase(name + 1) == null)) {
      return name;
    }
    
    if (baseName.length() < 1) {
      baseName = Messages.getString("copy");
    }
    

    int begin = baseName.length() - 1;
    int end = baseName.length();
    int endDigit = 2;
    while (begin >= 0) {
      try {
        endDigit = Integer.parseInt(baseName.substring(begin, end));
        name = baseName.substring(0, begin);
        begin--;
      } catch (NumberFormatException e) {
        break;
      }
    }
    baseName = name;
    for (int i = endDigit; i < Integer.MAX_VALUE; i++) {
      name = baseName + i;
      if (parent.getChildNamedIgnoreCase(name) == null) {
        return name;
      }
    }
    
    throw new RuntimeException(Messages.getString("Unable_to_find_a_suitable_new_name__baseName___") + baseName + Messages.getString("__parent___") + parent);
  }
  









  public static void setWorldTreeChildrenPropertiesStructure(Vector worldTreeChildrenPropertiesStructure)
  {
    resourcesworldTreeChildrenPropertiesStructure = worldTreeChildrenPropertiesStructure;
  }
  
  public static String[] getWorldTreeChildrenPropertiesStructure(Class elementClass) {
    if (resourcesworldTreeChildrenPropertiesStructure != null) {
      for (Iterator iter = resourcesworldTreeChildrenPropertiesStructure.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if ((o instanceof StringObjectPair)) {
          String className = ((StringObjectPair)o).getString();
          try {
            Class c = Class.forName(className);
            if (!c.isAssignableFrom(elementClass)) continue;
            return (String[])((StringObjectPair)o).getObject();
          }
          catch (ClassNotFoundException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_class_") + className, e);
          }
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Unexpected_object_found_in_worldTreeChildrenPropertiesStructure__") + o, null);
        }
      }
    }
    
    return null;
  }
  
  public static void addElementToAppropriateProperty(Element element, Element parent) {
    edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = null;
    
    if ((element instanceof Transformable)) {
      if ((parent instanceof World)) {
        oap = sandboxes;
      } else if ((parent instanceof Transformable)) {
        oap = parts;
      } else if ((parent instanceof edu.cmu.cs.stage3.alice.core.Group)) {
        oap = values;
      }
    } else if ((element instanceof Response)) {
      if ((parent instanceof Sandbox)) {
        oap = responses;
      }
    } else if ((element instanceof edu.cmu.cs.stage3.alice.core.Behavior)) {
      if ((parent instanceof Sandbox)) {
        oap = behaviors;
      }
    } else if ((element instanceof edu.cmu.cs.stage3.alice.core.Variable)) {
      if ((parent instanceof Sandbox)) {
        oap = variables;
      }
    } else if ((element instanceof edu.cmu.cs.stage3.alice.core.Question)) {
      if ((parent instanceof Sandbox)) {
        oap = questions;
      }
    } else if ((element instanceof edu.cmu.cs.stage3.alice.core.Sound)) {
      if ((parent instanceof Sandbox)) {
        oap = sounds;
      }
    } else if ((element instanceof edu.cmu.cs.stage3.alice.core.TextureMap)) {
      if ((parent instanceof Sandbox)) {
        oap = textureMaps;
      }
    } else if ((element instanceof edu.cmu.cs.stage3.alice.core.Pose)) {
      if ((parent instanceof Transformable)) {
        oap = poses;
      }
    }
    else if ((parent instanceof Sandbox)) {
      oap = misc;
    }
    

    if ((oap != null) && 
      (!oap.contains(element))) {
      oap.add(element);
    }
  }
  
  public static double getAspectRatio(World world)
  {
    if (world != null) {
      edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera[] spCameras = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera[])world.getDescendants(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera.class);
      if (spCameras.length > 0) {
        return 0horizontalViewingAngle.doubleValue() / 0verticalViewingAngle.doubleValue();
      }
    }
    return 0.0D;
  }
  
  public static double getCurrentTime() {
    long timeMillis = System.currentTimeMillis() - startTime;
    return timeMillis / 1000.0D;
  }
  
  public static void setImporterClasses(Class[] importers) {
    resourcesimporters = importers;
  }
  
  public static Class[] getImporterClasses() {
    return resourcesimporters;
  }
  
  public static void setEditorClasses(Class[] editors) {
    resourceseditors = editors;
  }
  
  public static Class[] getEditorClasses() {
    return resourceseditors;
  }
  
  public static void findAssignables(Class baseClass, java.util.Set result, boolean includeInterfaces) {
    if ((baseClass != null) && 
      (!result.contains(baseClass))) {
      result.add(baseClass);
      
      if (includeInterfaces) {
        Class[] interfaces = baseClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
          findAssignables(interfaces[i], result, includeInterfaces);
        }
      }
      
      findAssignables(baseClass.getSuperclass(), result, includeInterfaces);
    }
  }
  
  public static java.awt.datatransfer.DataFlavor getReferenceFlavorForClass(Class c)
  {
    if (!resourcesflavorMap.containsKey(c)) {
      try {
        resourcesflavorMap.put(c, new java.awt.datatransfer.DataFlavor("application/x-java-jvm-local-objectref; class=" + c.getName()));
      } catch (ClassNotFoundException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_class_") + c.getName(), e);
      }
    }
    return (java.awt.datatransfer.DataFlavor)resourcesflavorMap.get(c);
  }
  
  public static Object getDefaultValueForClass(Class cls) {
    if (cls == Boolean.class)
      return Boolean.TRUE;
    if (cls == Number.class)
      return new Double(1.0D);
    if (cls == String.class)
      return new String(Messages.getString("default_string"));
    if (cls == javax.vecmath.Vector3d.class)
      return edu.cmu.cs.stage3.math.MathUtilities.createXAxis();
    if (cls == edu.cmu.cs.stage3.math.Vector3.class)
      return new edu.cmu.cs.stage3.math.Vector3();
    if (cls == Quaternion.class)
      return new Quaternion();
    if (javax.vecmath.Matrix4d.class.isAssignableFrom(cls))
      return new edu.cmu.cs.stage3.math.Matrix44();
    if (cls == java.awt.Color.class)
      return java.awt.Color.white;
    if (cls == edu.cmu.cs.stage3.alice.scenegraph.Color.class)
      return edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE;
    if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(cls)) {
      edu.cmu.cs.stage3.util.Enumerable[] items = edu.cmu.cs.stage3.util.Enumerable.getItems(cls);
      if (items.length > 0) {
        return items[0];
      }
      return null;
    }
    if (cls == edu.cmu.cs.stage3.alice.core.ReferenceFrame.class) {
      return AuthoringTool.getHack().getWorld();
    }
    return null;
  }
  



































  public static double distanceToBackAfterGetAGoodLookAt(Transformable transformable, edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera)
  {
    if ((transformable != null) && (camera != null)) {
      edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
      double radius = bs.getRadius();
      double theta = Math.min(horizontalViewingAngle.doubleValue(), verticalViewingAngle.doubleValue());
      return radius / Math.sin(theta / 2.0D) + radius;
    }
    
    return 0.0D;
  }
  
  public static boolean areExperimentalFeaturesEnabled() {
    return resourcesexperimentalFeaturesEnabled;
  }
  
  public static void setExperimentalFeaturesEnabled(boolean enabled) {
    resourcesexperimentalFeaturesEnabled = enabled;
  }
  
  public static void putMiscItem(Object key, Object item) {
    resourcesmiscMap.put(key, item);
  }
  
  public static Object getMiscItem(Object key) {
    return resourcesmiscMap.get(key);
  }
  
  public static void garbageCollectIfPossible(edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references) {
    for (int i = 0; i < references.length; i++) {
      Element element = references[i].getProperty().getOwner();
      
      edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] metaReferences = element.getRoot().getPropertyReferencesTo(element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, true);
      if (metaReferences.length == 0) {
        element.getParent().removeChild(element);
      }
    }
  }
  
  public static String formatMemorySize(long bytes)
  {
    String sizeString = null;
    if (bytes < 1024L) {
      sizeString = resourcesdecimalFormatter.format(bytes) + " " + Messages.getString("bytes");
    } else if (bytes < 1048576L) {
      sizeString = resourcesdecimalFormatter.format(bytes / 1024.0D) + " KB";
    } else if (bytes < 1073741824L) {
      sizeString = resourcesdecimalFormatter.format(bytes / 1048576.0D) + " MB";
    } else if (bytes < 1099511627776L) {
      sizeString = resourcesdecimalFormatter.format(bytes / 1.073741824E9D) + " GB";
    } else {
      sizeString = resourcesdecimalFormatter.format(bytes / 1.099511627776E12D) + " TB";
    }
    return sizeString;
  }
  
  public static String formatTime(double seconds) {
    if (Double.isNaN(seconds)) {
      return "?:??";
    }
    DecimalFormat decFormatter = new DecimalFormat(".000");
    DecimalFormat secMinFormatter1 = new DecimalFormat("00");
    DecimalFormat secMinFormatter2 = new DecimalFormat("#0");
    
    double secondsFloored = (int)Math.floor(seconds);
    double decimal = seconds - secondsFloored;
    double secs = secondsFloored % 60.0D;
    double minutes = (secondsFloored - secs) / 60.0D % 60.0D;
    double hours = (secondsFloored - 60.0D * minutes - secs) / 3600.0D;
    
    String timeString = secMinFormatter1.format(secs) + decFormatter.format(decimal);
    if (hours > 0.0D) {
      timeString = secMinFormatter1.format(minutes) + ":" + timeString;
      timeString = secMinFormatter2.format(hours) + ":" + timeString;
    } else {
      timeString = secMinFormatter2.format(minutes) + ":" + timeString;
    }
    
    return timeString;
  }
  


  public static void printHierarchy(Component c) { printHierarchy(c, 0); }
  
  private static void printHierarchy(Component c, int level) {
    String tabs = "";
    for (int i = 0; i < level; i++) {
      tabs = tabs + "--";
    }
    System.out.println(tabs + c.getClass().getName() + "_" + c.hashCode());
    
    if ((c instanceof Container)) {
      Component[] children = ((Container)c).getComponents();
      for (int i = 0; i < children.length; i++) {
        printHierarchy(children[i], level + 1);
      }
    }
  }
  
  private static void initKeyCodesToStrings() {
    resourceskeyCodesToStrings.put(new Integer(48), "0");
    resourceskeyCodesToStrings.put(new Integer(49), "1");
    resourceskeyCodesToStrings.put(new Integer(50), "2");
    resourceskeyCodesToStrings.put(new Integer(51), "3");
    resourceskeyCodesToStrings.put(new Integer(52), "4");
    resourceskeyCodesToStrings.put(new Integer(53), "5");
    resourceskeyCodesToStrings.put(new Integer(54), "6");
    resourceskeyCodesToStrings.put(new Integer(55), "7");
    resourceskeyCodesToStrings.put(new Integer(56), "8");
    resourceskeyCodesToStrings.put(new Integer(57), "9");
    resourceskeyCodesToStrings.put(new Integer(65), "A");
    resourceskeyCodesToStrings.put(new Integer(66), "B");
    resourceskeyCodesToStrings.put(new Integer(67), "C");
    resourceskeyCodesToStrings.put(new Integer(68), "D");
    resourceskeyCodesToStrings.put(new Integer(69), "E");
    resourceskeyCodesToStrings.put(new Integer(70), "F");
    resourceskeyCodesToStrings.put(new Integer(71), "G");
    resourceskeyCodesToStrings.put(new Integer(72), "H");
    resourceskeyCodesToStrings.put(new Integer(73), "I");
    resourceskeyCodesToStrings.put(new Integer(74), "J");
    resourceskeyCodesToStrings.put(new Integer(75), "K");
    resourceskeyCodesToStrings.put(new Integer(76), "L");
    resourceskeyCodesToStrings.put(new Integer(77), "M");
    resourceskeyCodesToStrings.put(new Integer(78), "N");
    resourceskeyCodesToStrings.put(new Integer(79), "O");
    resourceskeyCodesToStrings.put(new Integer(80), "P");
    resourceskeyCodesToStrings.put(new Integer(81), "Q");
    resourceskeyCodesToStrings.put(new Integer(82), "R");
    resourceskeyCodesToStrings.put(new Integer(83), "S");
    resourceskeyCodesToStrings.put(new Integer(84), "T");
    resourceskeyCodesToStrings.put(new Integer(85), "U");
    resourceskeyCodesToStrings.put(new Integer(86), "V");
    resourceskeyCodesToStrings.put(new Integer(87), "W");
    resourceskeyCodesToStrings.put(new Integer(88), "X");
    resourceskeyCodesToStrings.put(new Integer(89), "Y");
    resourceskeyCodesToStrings.put(new Integer(90), "Z");
    resourceskeyCodesToStrings.put(new Integer(10), "enter");
    resourceskeyCodesToStrings.put(new Integer(32), "space");
    resourceskeyCodesToStrings.put(new Integer(38), "upArrow");
    resourceskeyCodesToStrings.put(new Integer(40), "downArrow");
    resourceskeyCodesToStrings.put(new Integer(37), "leftArrow");
    resourceskeyCodesToStrings.put(new Integer(39), "rightArrow");
    




    resourceskeyCodesToStrings.put(new Integer(8), "backspace");
    resourceskeyCodesToStrings.put(new Integer(155), "insert");
    resourceskeyCodesToStrings.put(new Integer(127), "delete");
    resourceskeyCodesToStrings.put(new Integer(36), "home");
    resourceskeyCodesToStrings.put(new Integer(35), "end");
    resourceskeyCodesToStrings.put(new Integer(33), "pageup");
    resourceskeyCodesToStrings.put(new Integer(34), "pagedown");
    

    resourceskeyCodesToStrings.put(new Integer(18), "alt");
    resourceskeyCodesToStrings.put(new Integer(17), "ctrl");
    resourceskeyCodesToStrings.put(new Integer(16), "shift");
    resourceskeyCodesToStrings.put(new Integer(96), "num0");
    resourceskeyCodesToStrings.put(new Integer(97), "num1");
    resourceskeyCodesToStrings.put(new Integer(98), "num2");
    resourceskeyCodesToStrings.put(new Integer(99), "num3");
    resourceskeyCodesToStrings.put(new Integer(100), "num4");
    resourceskeyCodesToStrings.put(new Integer(101), "num5");
    resourceskeyCodesToStrings.put(new Integer(102), "num6");
    resourceskeyCodesToStrings.put(new Integer(103), "num7");
    resourceskeyCodesToStrings.put(new Integer(104), "num8");
    resourceskeyCodesToStrings.put(new Integer(105), "num9");
    resourceskeyCodesToStrings.put(new Integer(111), "divide");
    resourceskeyCodesToStrings.put(new Integer(106), "multiply");
    resourceskeyCodesToStrings.put(new Integer(109), "subtract");
    resourceskeyCodesToStrings.put(new Integer(107), "add");
    resourceskeyCodesToStrings.put(new Integer(192), "`");
    resourceskeyCodesToStrings.put(new Integer(45), "-");
    resourceskeyCodesToStrings.put(new Integer(61), "=");
    resourceskeyCodesToStrings.put(new Integer(91), "[");
    resourceskeyCodesToStrings.put(new Integer(93), "]");
    resourceskeyCodesToStrings.put(new Integer(92), "backslash");
    resourceskeyCodesToStrings.put(new Integer(59), ";");
    resourceskeyCodesToStrings.put(new Integer(222), "'");
    resourceskeyCodesToStrings.put(new Integer(44), ",");
    resourceskeyCodesToStrings.put(new Integer(46), "period");
    resourceskeyCodesToStrings.put(new Integer(47), "slash");
  }
  
  public static void copyFile(File from, File to) throws java.io.IOException {
    if (!to.exists()) {
      to.createNewFile();
    }
    java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.io.FileInputStream(from));
    java.io.BufferedOutputStream out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(to));
    
    int b = in.read();
    while (b != -1) {
      out.write(b);
      b = in.read();
    }
    
    in.close();
    out.flush();
    out.close();
  }
  



  public static String getPrefix(String token)
  {
    if ((token.indexOf("<") > -1) && (token.indexOf(">") > token.indexOf("<"))) {
      return token.substring(0, token.indexOf("<"));
    }
    return token;
  }
  
  public static String getSpecifier(String token)
  {
    if ((token.indexOf("<") > -1) && (token.indexOf(">") > token.indexOf("<"))) {
      if (!AikMin.isWindows()) {
        token = token.replaceAll("\\\\", File.separator);
      }
      return token.substring(token.indexOf("<") + 1, token.indexOf(">"));
    }
    return null;
  }
  
  public static Component findElementDnDPanel(Container root, Element element)
  {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if ((o instanceof DnDGroupingPanel)) {
          try {
            java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel)o).getTransferable();
            if ((transferable != null) && (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor))) {
              Element e = (Element)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
              if (equals(e)) {
                return true;
              }
            }
          } catch (Exception e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_finding_ElementDnDPanel_"), e);
          }
        }
        return false;
      }
    };
    Component toReturn = findComponent(root, criterion);
    if ((toReturn instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)) {
      return ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)toReturn).getWorkSpace();
    }
    
    return toReturn;
  }
  
  public static Component findPropertyDnDPanel(Container root, Element element, final String propertyName)
  {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if ((o instanceof DnDGroupingPanel)) {
          try {
            java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel)o).getTransferable();
            if ((transferable != null) && (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor))) {
              Property p = (Property)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
              Element e = p.getOwner();
              if ((equals(e)) && (p.getName().equals(propertyName))) {
                return true;
              }
            }
          } catch (Exception e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_finding_PropertyDnDPanel_"), e);
          }
        }
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findUserDefinedResponseDnDPanel(Container root, Response actualResponse) {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if ((o instanceof DnDGroupingPanel)) {
          try {
            java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel)o).getTransferable();
            if ((transferable != null) && (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor))) {
              edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
              if (p.getActualResponse().equals(AuthoringToolResources.this)) {
                return true;
              }
            }
          } catch (Exception e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_finding_UserDefinedResponseDnDPanel_"), e);
          }
        }
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findUserDefinedQuestionDnDPanel(Container root, edu.cmu.cs.stage3.alice.core.Question actualQuestion) {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if ((o instanceof DnDGroupingPanel)) {
          try {
            java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel)o).getTransferable();
            if ((transferable != null) && (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor))) {
              edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
              if (p.getActualQuestion().equals(AuthoringToolResources.this)) {
                return true;
              }
            }
          } catch (Exception e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_finding_UserDefinedQuestionDnDPanel_"), e);
          }
        }
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findPrototypeDnDPanel(Container root, Class elementClass) {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if ((o instanceof DnDGroupingPanel)) {
          try {
            java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel)o).getTransferable();
            if ((transferable != null) && (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor))) {
              edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
              if (p.getElementClass().equals(AuthoringToolResources.this)) {
                return true;
              }
            }
          } catch (Exception e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_finding_PrototypeDnDPanel_"), e);
          }
        }
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findPropertyViewController(Container root, Element element, final String propertyName) {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if ((o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)) {
          Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)o).getProperty();
          if ((p.getOwner().equals(AuthoringToolResources.this)) && (p.getName().equals(propertyName))) {
            return true;
          }
        }
        else if ((o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController)) {
          Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController)o).getProperty();
          if ((p.getOwner().equals(AuthoringToolResources.this)) && (p.getName().equals(propertyName))) {
            return true;
          }
        }
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findButton(Container root, String buttonText) {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if ((o instanceof javax.swing.JButton)) {
          String text = ((javax.swing.JButton)o).getText();
          if (text.equals(AuthoringToolResources.this)) {
            return true;
          }
        }
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findEditObjectButton(Container root, Element element) {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if (((o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton)) && 
          (((edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton)o).getObject().equals(AuthoringToolResources.this))) {
          return true;
        }
        
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findGalleryObject(Container root, String uniqueIdentifier) {
    Criterion criterion = new Criterion() {
      public boolean accept(Object o) {
        if (((o instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)) && 
          (((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)o).getUniqueIdentifier().equals(AuthoringToolResources.this))) {
          return true;
        }
        
        return false;
      }
      
    };
    return findComponent(root, criterion);
  }
  
  public static Component findComponent(Container root, Criterion criterion) {
    if (criterion.accept(root)) {
      return root;
    }
    
    Component[] children = root.getComponents();
    for (int i = 0; i < children.length; i++) {
      if ((children[i] instanceof Container)) {
        Component result = findComponent((Container)children[i], criterion);
        if (result != null) {
          return result;
        }
      }
    }
    
    return null;
  }
}

package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.Editor;
import edu.cmu.cs.stage3.lang.Messages;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Vector;



























public final class EditorUtilities
{
  private static Class[] allEditors = null;
  
  static {
    allEditors = AuthoringToolResources.getEditorClasses();
    if (allEditors == null) {
      AuthoringTool.showErrorDialog(Messages.getString("no_editors_found__"), null);
      allEditors = new Class[0];
    }
  }
  


  public EditorUtilities() {}
  

  public static Class[] getAllEditors()
  {
    return allEditors;
  }
  





  public static Class[] getEditorsForClass(Class objectClass)
  {
    Vector editors = new Vector();
    if (!Object.class.isAssignableFrom(objectClass)) {
      return null;
    }
    if (allEditors == null) {
      return null;
    }
    for (int i = 0; i < allEditors.length; i++) {
      Class acceptedClass = getObjectParameter(allEditors[i]);
      if (acceptedClass.isAssignableFrom(objectClass)) {
        editors.addElement(allEditors[i]);
      }
    }
    
    sort(editors, objectClass);
    
    Class[] cvs = new Class[editors.size()];
    for (int i = 0; i < cvs.length; i++) {
      cvs[i] = ((Class)editors.elementAt(i));
    }
    return cvs;
  }
  



  public static boolean isInAllEditors(Class potentialEditor)
  {
    if (allEditors == null) {
      return false;
    }
    for (int i = 0; i < allEditors.length; i++) {
      if (potentialEditor == allEditors[i]) {
        return true;
      }
    }
    return false;
  }
  

  public static Editor getEditorFromClass(Class editorClass)
  {
    try
    {
      return (Editor)editorClass.newInstance();
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_creating_new_editor_of_type_") + editorClass, t);
    }
    return null;
  }
  
  public static Method getSetMethodFromClass(Class editorClass) {
    Method[] methods = editorClass.getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method potentialMethod = methods[i];
      if (potentialMethod.getName().equals("setObject")) {
        Class[] parameterTypes = potentialMethod.getParameterTypes();
        if ((parameterTypes.length == 1) && 
          (Object.class.isAssignableFrom(parameterTypes[0]))) {
          return potentialMethod;
        }
      }
    }
    

    return null;
  }
  
  public static Class getObjectParameter(Class editorClass) {
    Method setObject = getSetMethodFromClass(editorClass);
    if (setObject != null) {
      return setObject.getParameterTypes()[0];
    }
    
    return null;
  }
  






  public static Class getBestEditor(Class objectClass)
  {
    Class bestEditor = null;
    int bestDepth = Integer.MAX_VALUE;
    for (int i = 0; i < allEditors.length; i++) {
      Class editorClass = allEditors[i];
      
      Method setObject = getSetMethodFromClass(editorClass);
      if (setObject != null) {
        Class[] parameterTypes = setObject.getParameterTypes();
        if (parameterTypes.length == 1) {
          int depth = getObjectClassDepth(parameterTypes[0], objectClass);
          
          if ((depth < bestDepth) && (depth >= 0)) {
            bestDepth = depth;
            bestEditor = editorClass;
          }
        }
      }
    }
    


    return bestEditor;
  }
  
  public static void editObject(Editor editor, Object object) {
    Method setObject = getSetMethodFromClass(editor.getClass());
    try {
      setObject.invoke(editor, new Object[] { object });
    } catch (Exception e) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_editing_object__") + object, e);
    }
  }
  







  private static boolean isValidEditor(Class editorClass)
  {
    if (!Editor.class.isAssignableFrom(editorClass)) {
      return false;
    }
    
    boolean constructorFound = false;
    Constructor[] editorConstructors = editorClass.getConstructors();
    for (int i = 0; i < editorConstructors.length; i++) {
      Class[] parameterTypes = editorConstructors[i].getParameterTypes();
      if (parameterTypes.length == 0) {
        constructorFound = true;
        break;
      }
    }
    
    if ((getSetMethodFromClass(editorClass) != null) && (constructorFound)) {
      return true;
    }
    
    return false;
  }
  




  private static int getObjectClassDepth(Class superclass, Class subclass)
  {
    if (!superclass.isAssignableFrom(subclass)) {
      return -1;
    }
    
    Class temp = subclass;
    int i = 0;
    while ((temp != superclass) && (superclass.isAssignableFrom(temp))) {
      i++;
      temp = temp.getSuperclass();
    }
    
    return i;
  }
  


  private static void swap(Vector v, int a, int b)
  {
    Object t = v.elementAt(a);
    v.setElementAt(v.elementAt(b), a);
    v.setElementAt(t, b);
  }
  







  private static int compare(Class a, Class b, Class objectClass)
  {
    int aDist = getObjectClassDepth(getObjectParameter(a), objectClass);
    int bDist = getObjectClassDepth(getObjectParameter(b), objectClass);
    if (aDist < bDist)
      return -1;
    if (bDist < aDist) {
      return 1;
    }
    return 0;
  }
  






  private static void sort(Vector v, Class objectClass)
  {
    for (int i = 0; i < v.size(); i++) {
      for (int j = i; (j > 0) && (compare((Class)v.elementAt(j - 1), (Class)v.elementAt(j), objectClass) > 0); j--) {
        swap(v, j, j - 1);
      }
    }
  }
}

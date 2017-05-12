package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.io.TokenBlock;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


















public abstract class Spline
{
  private TreeSet keys;
  private Comparator keyComparator = new Comparator() {
    public int compare(Object o1, Object o2) {
      if (((o1 instanceof Key)) && ((o2 instanceof Key))) {
        Key key1 = (Key)o1;
        Key key2 = (Key)o2;
        if (key1.getTime() < key2.getTime())
          return -1;
        if (key1.getTime() > key2.getTime()) {
          return 1;
        }
        return 0;
      }
      
      throw new ClassCastException(Key.class.getName() + " " + Messages.getString("required_"));
    }
  };
  private Key recentKey;
  
  protected Spline()
  {
    keys = new TreeSet(keyComparator);
  }
  
  protected boolean addKey(Key key) {
    return keys.add(key);
  }
  
  protected boolean removeKey(Key key) {
    return keys.remove(key);
  }
  
  public void clearKeys() {
    keys.clear();
  }
  
  public Key[] getKeyArray(Key[] keyArray) {
    return (Key[])keys.toArray(keyArray);
  }
  
  private Key[] boundingKeys = new Key[2];
  
  public Key[] getBoundingKeys(double time) { Key prevKey = null;
    Key nextKey = null;
    
    if (keys.size() == 1) {
      prevKey = nextKey = (Key)keys.first();
      boundingKeys[0] = prevKey;
      boundingKeys[1] = nextKey;
      return boundingKeys;
    }
    

    if (recentKey != null) {
      Iterator iter = keys.tailSet(recentKey).iterator();
      if (iter.hasNext()) {
        nextKey = (Key)iter.next();
        while (iter.hasNext()) {
          prevKey = nextKey;
          nextKey = (Key)iter.next();
          if ((time >= prevKey.getTime()) && (time < nextKey.getTime())) {
            recentKey = prevKey;
            boundingKeys[0] = prevKey;
            boundingKeys[1] = nextKey;
            return boundingKeys;
          }
        }
      }
    }
    

    Iterator iter = keys.iterator();
    if (iter.hasNext()) {
      nextKey = (Key)iter.next();
      while (iter.hasNext()) {
        prevKey = nextKey;
        nextKey = (Key)iter.next();
        if ((time >= prevKey.getTime()) && (time < nextKey.getTime())) {
          recentKey = prevKey;
          boundingKeys[0] = prevKey;
          boundingKeys[1] = nextKey;
          return boundingKeys;
        }
      }
    }
    
    return null;
  }
  
  public Key getFirstKey() {
    return (Key)keys.first();
  }
  
  public Key getLastKey() {
    return (Key)keys.last();
  }
  
  public double getDuration() {
    return getLastTime();
  }
  
  public double getFirstTime() {
    if (!keys.isEmpty()) {
      return getFirstKey().getTime();
    }
    return 0.0D;
  }
  
  public double getLastTime()
  {
    if (!keys.isEmpty()) {
      return getLastKey().getTime();
    }
    return 0.0D;
  }
  
  public void scaleKeyValueComponents(double scaleFactor) { double[] valueComponents;
    int i;
    for (Iterator iter = keys.iterator(); iter.hasNext(); 
        

        i < valueComponents.length)
    {
      Key key = (Key)iter.next();
      valueComponents = key.getValueComponents();
      i = 0; continue;
      valueComponents[i] *= scaleFactor;i++;
    }
  }
  



  public abstract Object getSample(double paramDouble);
  


  public String toString()
  {
    StringBuffer repr = new StringBuffer();
    
    repr.append("{spline}");
    repr.append("{splineType}");
    repr.append(getClass().getName());
    repr.append("{/splineType}");
    
    repr.append("{keys}");
    for (Iterator iter = keys.iterator(); iter.hasNext();) {
      Key key = (Key)iter.next();
      repr.append("{key}");
      repr.append("{type}");
      repr.append(key.getClass().getName());
      repr.append("{/type}");
      repr.append("{data}");
      repr.append(key.toString());
      repr.append("{/data}");
      repr.append("{/key}");
    }
    repr.append("{/keys}");
    
    repr.append("{/spline}");
    
    return repr.toString();
  }
  
  public static Spline valueOf(String s) {
    s = s.replace('{', '<');
    s = s.replace('}', '>');
    
    TokenBlock splineBlock = TokenBlock.getTokenBlock(0, s);
    TokenBlock splineTypeBlock = TokenBlock.getTokenBlock(0, tokenContents);
    TokenBlock keysBlock = TokenBlock.getTokenBlock(tokenEndIndex, tokenContents);
    
    Spline spline = null;
    Method addKeyMethod = null;
    try {
      Class splineClass = Class.forName(tokenContents);
      spline = (Spline)splineClass.newInstance();
      addKeyMethod = null;
      Method[] methods = splineClass.getMethods();
      for (int i = 0; i < methods.length; i++) {
        if (methods[i].getName().equals("addKey")) {
          addKeyMethod = methods[i];
        }
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return null;
    } catch (InstantiationException e) {
      e.printStackTrace();
      return null;
    }
    
    if (addKeyMethod == null) {
      System.err.println(Messages.getString("Unable_to_find_addKey_method_for_") + spline);
      return null;
    }
    
    int beginIndex = 0;
    TokenBlock keyBlock = TokenBlock.getTokenBlock(beginIndex, tokenContents);
    while (tokenContents != null) {
      TokenBlock typeBlock = TokenBlock.getTokenBlock(0, tokenContents);
      TokenBlock dataBlock = TokenBlock.getTokenBlock(tokenEndIndex, tokenContents);
      try
      {
        Class keyClass = Class.forName(tokenContents);
        Method valueOfMethod = keyClass.getMethod("valueOf", new Class[] { String.class });
        Object key = valueOfMethod.invoke(null, new Object[] { tokenContents });
        addKeyMethod.invoke(spline, new Object[] { key });
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      
      beginIndex = tokenEndIndex;
      keyBlock = TokenBlock.getTokenBlock(beginIndex, tokenContents);
    }
    
    return spline;
  }
}

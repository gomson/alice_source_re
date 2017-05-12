package edu.cmu.cs.stage3.image.codec;

import edu.cmu.cs.stage3.lang.Messages;
import java.util.MissingResourceException;























class JaiI18N
{
  private JaiI18N() {}
  
  public static String getString(String key)
  {
    try
    {
      return Messages.getString(key);
    } catch (MissingResourceException e) {}
    return key;
  }
}

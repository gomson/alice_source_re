package edu.cmu.cs.stage3.lang;

import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
  static
  {
    if (edu.cmu.cs.stage3.alice.authoringtool.AikMin.locale.compareToIgnoreCase("") == 0) {
      Configuration authoringtoolConfig = Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
      if (authoringtoolConfig.getValue("language") == null) {
        authoringtoolConfig.setValue("language", "English");
        edu.cmu.cs.stage3.alice.authoringtool.AikMin.locale = "English";
      } else {
        edu.cmu.cs.stage3.alice.authoringtool.AikMin.locale = authoringtoolConfig.getValue("language");
      }
    }
  }
  
  public Messages() {}
  
  public static String getString(String key) {
    try {
      return (String)ResourceBundle.getBundle("edu.cmu.cs.stage3.lang." + edu.cmu.cs.stage3.alice.authoringtool.AikMin.locale).getObject(key);
    } catch (MissingResourceException e) {}
    return key.replace("_", " ");
  }
}

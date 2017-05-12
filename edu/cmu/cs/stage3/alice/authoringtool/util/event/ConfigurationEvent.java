package edu.cmu.cs.stage3.alice.authoringtool.util.event;





public class ConfigurationEvent
{
  protected String keyName;
  



  protected boolean isList;
  



  protected String oldValue;
  


  protected String newValue;
  


  protected String[] oldValueList;
  


  protected String[] newValueList;
  



  public ConfigurationEvent(String keyName, boolean isList, String oldValue, String newValue, String[] oldValueList, String[] newValueList)
  {
    this.keyName = keyName;
    this.isList = isList;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.oldValueList = oldValueList;
    this.newValueList = newValueList;
  }
  
  public boolean isList() {
    return isList;
  }
  
  public String getKeyName() {
    return keyName;
  }
  
  public String getOldValue() {
    return oldValue;
  }
  
  public String getNewValue() {
    return newValue;
  }
  
  public String[] getOldValueList() {
    return oldValueList;
  }
  



  public String[] getNewValueList()
  {
    return newValueList;
  }
}

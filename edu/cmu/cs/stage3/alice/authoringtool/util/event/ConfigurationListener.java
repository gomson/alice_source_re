package edu.cmu.cs.stage3.alice.authoringtool.util.event;

public abstract interface ConfigurationListener
{
  public abstract void changing(ConfigurationEvent paramConfigurationEvent);
  
  public abstract void changed(ConfigurationEvent paramConfigurationEvent);
}

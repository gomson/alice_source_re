package edu.cmu.cs.stage3.media.event;

import edu.cmu.cs.stage3.media.DataSource;

public class DataSourceEvent extends java.util.EventObject { public DataSourceEvent(DataSource source) { super(source); }
  
  public DataSource getDataSource() {
    return (DataSource)getSource();
  }
}

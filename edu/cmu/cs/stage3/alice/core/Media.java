package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.media.SoundMarker;
import edu.cmu.cs.stage3.alice.core.property.DataSourceProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.media.DataSource;




















public class Media
  extends Element
{
  public final DataSourceProperty dataSource = new DataSourceProperty(this, "dataSource", null);
  public final NumberProperty mediaLockCacheCountHint = new NumberProperty(this, "mediaLockCacheCountHint", new Integer(1));
  public final ElementArrayProperty markers = new ElementArrayProperty(this, "markers", null, [Ledu.cmu.cs.stage3.alice.core.media.SoundMarker.class);
  
  public Media() {}
  
  protected void started(World world, double time) { super.started(world, time);
    DataSource dataSourceValue = dataSource.getDataSourceValue();
    
    int realizedPlayerCount = dataSourceValue.waitForRealizedPlayerCount(mediaLockCacheCountHint.intValue(), 0L);
  }
}

package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ItemOfCollectionProperty;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;


















public class ListItemResponse
  extends ListResponse
{
  public final ItemOfCollectionProperty item = new ItemOfCollectionProperty(this, "item");
  
  private Variable m_variable = null;
  private List m_list = null;
  
  private PropertyListener m_variableValuePropertyListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent propertyEvent) {}
    
    public void propertyChanged(PropertyEvent propertyEvent) {
      if (m_list != null) {
        m_list.valueClass.removePropertyListener(m_listValueClassPropertyListener);
      }
      m_list = ((List)propertyEvent.getValue());
      if (m_list != null) {
        m_list.valueClass.addPropertyListener(m_listValueClassPropertyListener);
      }
      ListItemResponse.this.updateItemCollection();
    }
  };
  private PropertyListener m_listValueClassPropertyListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent propertyEvent) {}
    

    public void propertyChanged(PropertyEvent propertyEvent) { ListItemResponse.this.updateItemCollection(); } };
  
  public ListItemResponse() {}
  
  private void updateItemCollection() { item.setCollection(list.getListValue()); }
  
  protected void propertyChanged(Property property, Object value)
  {
    if (property == list) {
      if (m_variable != null) {
        m_variable.value.removePropertyListener(m_variableValuePropertyListener);
      }
      if (m_list != null) {
        m_list.valueClass.removePropertyListener(m_listValueClassPropertyListener);
      }
      if ((value instanceof Variable)) {
        m_variable = ((Variable)value);
        m_list = ((List)m_variable.value.getValue());
      } else {
        m_variable = null;
        m_list = ((List)value);
      }
      if (m_variable != null) {
        m_variable.value.addPropertyListener(m_variableValuePropertyListener);
      }
      if (m_list != null) {
        m_list.valueClass.addPropertyListener(m_listValueClassPropertyListener);
      }
      
      updateItemCollection();
    }
    super.propertyChanged(property, value); }
  
  public class RuntimeListItemResponse extends ListResponse.RuntimeListResponse { public RuntimeListItemResponse() { super(); }
    
    protected Object getItem() { return item.getValue(); }
  }
}

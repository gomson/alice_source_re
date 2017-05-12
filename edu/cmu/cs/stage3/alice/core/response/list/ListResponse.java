package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;



















public class ListResponse
  extends Response
{
  public final ListProperty list = new ListProperty(this, "list", null);
  public class RuntimeListResponse extends Response.RuntimeResponse { public RuntimeListResponse() { super(); }
    
    protected List getList() { return list.getListValue(); }
  }
  
  public ListResponse() {}
}

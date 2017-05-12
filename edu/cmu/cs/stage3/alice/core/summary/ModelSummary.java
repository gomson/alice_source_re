package edu.cmu.cs.stage3.alice.core.summary;

import edu.cmu.cs.stage3.alice.core.Model;

public class ModelSummary
  extends ElementSummary {
  private String m_name;
  private String m_modeledBy;
  private String m_paintedBy;
  private int m_partCount;
  
  public ModelSummary() {}
  
  private Model getModel() { return (Model)getElement(); }
  
  public void setModel(Model model) {
    super.setElement(model);
    m_name = null;
    m_modeledBy = null;
    m_paintedBy = null;
    m_partCount = -1;
    m_physicalSizeDescription = null;
    m_methodNames = null;
    m_questionNames = null;
    m_soundNames = null;
  }
  
  public String getName() {
    if (getModel() != null) {
      return getModelname.getStringValue();
    }
    return m_name;
  }
  
  public String getModeledBy() {
    if (getModel() != null) {
      return (String)getModeldata.get("modeled by");
    }
    return m_modeledBy;
  }
  
  public String getPaintedBy() {
    if (getModel() != null) {
      return (String)getModeldata.get("painted by");
    }
    return m_paintedBy;
  }
  

  public int getPartCount() { return m_partCount; }
  
  private String m_physicalSizeDescription;
  
  public String getPhysicalSizeDescription() { return m_physicalSizeDescription; }
  
  private String[] m_methodNames;
  public String[] getMethodNames() { if (getModel() != null) {
      String[] methodNames = new String[getModelresponses.size()];
      for (int i = 0; i < methodNames.length; i++) {
        edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse methodI = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)getModelresponses.get(i);
        methodNames[i] = name.getStringValue();
      }
      return methodNames;
    }
    return m_methodNames; }
  
  private String[] m_questionNames;
  private String[] m_soundNames;
  public String[] getQuestionNames() { return m_questionNames; }
  
  public String[] getSoundNames() {
    return m_soundNames;
  }
}

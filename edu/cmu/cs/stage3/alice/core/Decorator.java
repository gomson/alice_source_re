package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;















public abstract class Decorator
{
  public Decorator() {}
  
  protected abstract ReferenceFrame getReferenceFrame();
  
  protected Visual m_sgVisual = null;
  protected Appearance m_sgAppearance = null;
  
  private boolean m_isDirty = true;
  

  public void markDirty() { setIsDirty(true); }
  
  public void setIsDirty(boolean isDirty) {
    m_isDirty = isDirty;
    if (isDirty) {
      updateIfShowing();
    }
  }
  
  public boolean isDirty() { return m_isDirty; }
  
  protected void update() {
    ReferenceFrame referenceFrame = getReferenceFrame();
    if (referenceFrame != null) {
      if (m_sgAppearance == null) {
        m_sgAppearance = new Appearance();
        m_sgAppearance.setShadingStyle(ShadingStyle.NONE);
        m_sgAppearance.setBonus(referenceFrame);
      }
      if (m_sgVisual == null) {
        m_sgVisual = new Visual();
        m_sgVisual.setFrontFacingAppearance(m_sgAppearance);
        m_sgVisual.setIsShowing(false);
        m_sgVisual.setBonus(referenceFrame);
      }
      m_sgVisual.setParent(referenceFrame.getSceneGraphContainer());
    }
  }
  
  protected void updateIfShowing() {
    if (isShowing())
      update();
  }
  
  public void internalRelease(int pass) {
    switch (pass) {
    case 1: 
      if (m_sgVisual != null) {
        m_sgVisual.setFrontFacingAppearance(null);
        m_sgVisual.setGeometry(null);
        m_sgVisual.setParent(null);
      }
      break;
    case 2: 
      if (m_sgVisual != null) {
        m_sgVisual.release();
        m_sgVisual = null;
      }
      if (m_sgAppearance != null) {
        m_sgAppearance.release();
        m_sgAppearance = null;
      }
      break;
    }
  }
  
  public boolean isShowing() {
    if (m_sgVisual == null) {
      return false;
    }
    return m_sgVisual.getIsShowing();
  }
  
  public void setIsShowing(boolean value) {
    if (value) {
      update();
      showRightNow();
    } else {
      hideRightNow();
    }
  }
  

  public void setIsShowing(Boolean value) { setIsShowing((value != null) && (value.booleanValue())); }
  
  protected void showRightNow() {
    if (m_sgVisual != null)
      m_sgVisual.setIsShowing(true);
  }
  
  protected void hideRightNow() {
    if (m_sgVisual != null) {
      m_sgVisual.setIsShowing(false);
    }
  }
}

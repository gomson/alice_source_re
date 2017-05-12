package edu.cmu.cs.stage3.alice.core.visualization;

import edu.cmu.cs.stage3.alice.core.Model;

public class ModelVisualization extends edu.cmu.cs.stage3.alice.core.Visualization
{
  public ModelVisualization() {}
  
  public void unhook(Model model) {
    if (getItem() == model)
      setItem(null);
  }
  
  private edu.cmu.cs.stage3.alice.core.TextureMap getEmptyTextureMap() {
    return (edu.cmu.cs.stage3.alice.core.TextureMap)getChildNamed("EmptyTexture");
  }
  
  private edu.cmu.cs.stage3.alice.core.TextureMap getFilledTextureMap() { return (edu.cmu.cs.stage3.alice.core.TextureMap)getChildNamed("FilledTexture"); }
  
  private edu.cmu.cs.stage3.alice.core.Variable m_itemVariable = null;
  
  private edu.cmu.cs.stage3.alice.core.Variable getItemVariable() { if (m_itemVariable == null) {
      m_itemVariable = ((edu.cmu.cs.stage3.alice.core.Variable)getChildNamed("Item"));
    }
    return m_itemVariable;
  }
  
  public Model getItem() { return (Model)getItemVariablevalue.getValue(); }
  

  public void setItem(Model value) { getItemVariablevalue.set(value); }
  
  private void synchronize(Model curr) {
    Model prev = getItem();
    if ((prev != null) && (prev != curr)) {
      visualization.set(null);
    }
    if (curr != null) {
      curr.setTransformationRightNow(getTransformationFor(curr), this);
      vehicle.set(this, true);
      visualization.set(this);
      diffuseColorMap.set(getFilledTextureMap());
    } else {
      diffuseColorMap.set(getEmptyTextureMap());
    }
  }
  
  protected void loadCompleted() {
    super.loadCompleted();
    edu.cmu.cs.stage3.alice.core.Variable item = getItemVariable();
    if (item != null) {
      value.addPropertyListener(new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
        public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {}
        
        public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
          ModelVisualization.this.synchronize((Model)propertyEvent.getValue());
        }
      });
      synchronize(getItem());
    }
  }
  
  public javax.vecmath.Matrix4d getTransformationFor(Model model) { javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
    m.setIdentity();
    if (model != null) {
      edu.cmu.cs.stage3.math.Box box = model.getBoundingBox();
      javax.vecmath.Vector3d v = box.getCenterOfBottomFace();
      if (v != null) {
        v.negate();
        m30 = x;
        m31 = y;
        m32 = z;
      }
    }
    return m;
  }
}

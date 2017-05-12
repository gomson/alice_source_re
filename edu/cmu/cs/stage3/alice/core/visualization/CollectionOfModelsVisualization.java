package edu.cmu.cs.stage3.alice.core.visualization;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Model;
import java.util.Vector;

public abstract class CollectionOfModelsVisualization extends edu.cmu.cs.stage3.alice.core.Visualization
{
  private Vector m_bins = new Vector();
  
  public CollectionOfModelsVisualization() {}
  
  public void unhook(Model model) { int i = indexOf(model, 0);
    if (i != -1) {
      set(i, null);
    }
  }
  

  protected String getItemsName() { return "items"; }
  
  private edu.cmu.cs.stage3.alice.core.Variable m_itemsVariable = null;
  
  private edu.cmu.cs.stage3.alice.core.Variable getItemsVariable() { if (m_itemsVariable == null) {
      m_itemsVariable = ((edu.cmu.cs.stage3.alice.core.Variable)getChildNamed(getItemsName()));
    }
    return m_itemsVariable;
  }
  
  public Collection getItemsCollection() { return (Collection)getItemsVariablevalue.getValue(); }
  
  public Model[] getItems() {
    return (Model[])getItemsCollectionvalues.getArrayValue();
  }
  
  public void setItems(Model[] items) { getItemsCollectionvalues.set(items); }
  
  private Model getPrototype()
  {
    return (Model)getChildNamed("BinPrototype");
  }
  
  private int getBinCount() { return m_bins.size(); }
  

  private Model getBinAt(int i) { return (Model)m_bins.get(i); }
  
  private void setBinAt(int i, Model bin) {
    if (m_bins.size() == i) {
      m_bins.addElement(bin);
    } else {
      if (m_bins.size() < i) {
        m_bins.ensureCapacity(i + 1);
      }
      m_bins.set(i, bin);
    }
  }
  
  private static final java.awt.Font s_font = new java.awt.Font("Serif", 0, 32);
  
  private static edu.cmu.cs.stage3.alice.core.TextureMap getEmptyTextureMap(Model bin) { return (edu.cmu.cs.stage3.alice.core.TextureMap)bin.getChildNamed("EmptyTexture"); }
  
  private static edu.cmu.cs.stage3.alice.core.TextureMap getFilledTextureMap(Model bin) {
    return (edu.cmu.cs.stage3.alice.core.TextureMap)bin.getChildNamed("FilledTexture");
  }
  
  private static void decorateTextureMap(edu.cmu.cs.stage3.alice.core.TextureMap skin, int i) {
    if (skin != null) {
      java.awt.Image originalImage = skin.image.getImageValue();
      if ((originalImage instanceof java.awt.image.BufferedImage)) {
        java.awt.image.BufferedImage originalBufferedImage = (java.awt.image.BufferedImage)originalImage;
        java.awt.Image image = new java.awt.image.BufferedImage(originalBufferedImage.getWidth(), originalBufferedImage.getHeight(), 2);
        java.awt.Graphics g = image.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.setFont(s_font);
        String s = Integer.toString(i);
        java.awt.FontMetrics fm = g.getFontMetrics();
        java.awt.geom.Rectangle2D r = fm.getStringBounds(s, g);
        g.setColor(java.awt.Color.black);
        g.drawString(s, 80, (int)(20.0D - r.getX() + r.getHeight()));
        g.dispose();
        skin.image.set(image);
        skin.touchImage();
      }
    }
  }
  
  private void synchronize(Model[] curr) {
    int binCount = getBinCount();
    for (int i = binCount - 1; i >= curr.length; i--) {
      Model binI = getBinAt(i);
      vehicle.set(null);
      
      m_bins.remove(binI);
    }
    Model prototype = getPrototype();
    if (prototype != null) {
      for (int i = binCount; i < curr.length; i++) {
        Class[] share = { edu.cmu.cs.stage3.alice.core.Geometry.class };
        String name = "Sub" + i;
        Model binI = (Model)getChildNamed(name);
        if (binI == null) {
          binI = (Model)prototype.HACK_createCopy(name, this, -1, share, null);
          decorateTextureMap(getEmptyTextureMap(binI), i);
          decorateTextureMap(getFilledTextureMap(binI), i);
        }
        setBinAt(i, binI);
      }
      binCount = getBinCount();
      for (int i = 0; i < binCount; i++) {
        Model binI = getBinAt(i);
        vehicle.set(this);
        binI.setPositionRightNow(-(prototype.getWidth() * i), 0.0D, 0.0D);
        if (curr[i] != null) {
          vehicle.set(binI);
          visualization.set(this);
          curr[i].setTransformationRightNow(getTransformationFor(curr[i], i), this);
          diffuseColorMap.set(getFilledTextureMap(binI));
        } else {
          diffuseColorMap.set(getEmptyTextureMap(binI));
        }
      }
      Model rightBracket = (Model)getChildNamed("RightBracket");
      if (rightBracket != null) {
        rightBracket.setPositionRightNow(-(prototype.getWidth() * (binCount - 0.5D)), 0.0D, 0.0D);
      }
    }
  }
  
  public Model get(int i) {
    return (Model)getItemsCollectionvalues.get(i);
  }
  
  public void set(int i, Model model) { getItemsCollectionvalues.set(i, model); }
  
  public int indexOf(Model model, int from) {
    return getItemsCollectionvalues.indexOf(model, from);
  }
  
  public int lastIndexOf(Model model, int from) { return getItemsCollectionvalues.lastIndexOf(model, from); }
  
  public boolean contains(Model model) {
    return getItemsCollectionvalues.contains(model);
  }
  
  public int size() { return getItemsCollectionvalues.size(); }
  
  public boolean isEmpty() {
    return getItemsCollectionvalues.isEmpty();
  }
  
  protected void loadCompleted()
  {
    super.loadCompleted();
    Collection collection = getItemsCollection();
    if (collection != null) {
      values.addPropertyListener(new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
        public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {}
        
        public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
          CollectionOfModelsVisualization.this.synchronize((Model[])propertyEvent.getValue());
        }
      });
      synchronize(getItems());
    } else {
      System.err.println("WARNING: collection is null " + this);
    }
  }
  
  public javax.vecmath.Matrix4d getTransformationFor(Model model, int i) { Model prototype = getPrototype();
    
    javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
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
    m30 -= prototype.getWidth() * i;
    return m;
  }
}

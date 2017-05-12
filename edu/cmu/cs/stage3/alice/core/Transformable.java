package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.EulerAngles;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Sphere;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;
import java.util.Vector;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class Transformable extends ReferenceFrame
{
  public final ElementArrayProperty parts = new ElementArrayProperty(this, "parts", null, [Ledu.cmu.cs.stage3.alice.core.Transformable.class);
  public final ElementArrayProperty poses = new ElementArrayProperty(this, "poses", null, [Ledu.cmu.cs.stage3.alice.core.Pose.class);
  public final Matrix44Property localTransformation = new Matrix44Property(this, "localTransformation", new Matrix44());
  public final VehicleProperty vehicle = new VehicleProperty(this, "vehicle", null);
  public final BooleanProperty isPivotShowing = new BooleanProperty(this, "isPivotShowing", Boolean.FALSE);
  private edu.cmu.cs.stage3.alice.scenegraph.Transformable m_sgTransformable;
  private PivotDecorator m_pivotDecorator = new PivotDecorator();
  
  public Transformable()
  {
    m_sgTransformable = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
    m_sgTransformable.setBonus(this);
    m_pivotDecorator.setTransformable(this);
  }
  








  public Visual[] getAllSceneGraphVisuals(HowMuch howMuch)
  {
    final Vector v = new Vector();
    visit(new edu.cmu.cs.stage3.util.VisitListener() {
      public void visited(Object o) {
        if ((o instanceof Model)) {
          v.addElement(((Model)o).getSceneGraphVisual());
        }
        
      }
    }, howMuch);
    
    Visual[] sgVisuals = new Visual[v.size()];
    v.copyInto(sgVisuals);
    return sgVisuals;
  }
  
  public Visual[] getAllSceneGraphVisuals() { return getAllSceneGraphVisuals(HowMuch.INSTANCE_AND_PARTS); }
  

  protected void internalRelease(int pass)
  {
    switch (pass) {
    case 1: 
      m_sgTransformable.setParent(null);
      m_pivotDecorator.internalRelease(1);
      break;
    case 2: 
      m_sgTransformable.release();
      m_sgTransformable = null;
      m_pivotDecorator.internalRelease(2);
      m_pivotDecorator = null;
    }
    
    super.internalRelease(pass);
  }
  
  private Transformable internalGetPartNamed(String nameValue, boolean ignoreCase) {
    for (int i = 0; i < parts.size(); i++) {
      Transformable part = (Transformable)parts.get(i);
      if (nameValue != null) { boolean found;
        boolean found;
        if (ignoreCase) {
          found = nameValue.equalsIgnoreCase(name.getStringValue());
        } else {
          found = nameValue.equals(name.getStringValue());
        }
        if (found) {
          return part;
        }
      }
      else if (name.getStringValue() == null) {
        return part;
      }
    }
    
    return null;
  }
  
  private Transformable internalGetPartKeyed(String key, int fromIndex, boolean ignoreCase) {
    if (key.equals("")) {
      return this;
    }
    int toIndex = key.indexOf('.', fromIndex);
    if (toIndex == -1) {
      String childName = key.substring(fromIndex);
      return internalGetPartNamed(childName, ignoreCase);
    }
    String childName = key.substring(fromIndex, toIndex);
    Transformable child = internalGetPartNamed(childName, ignoreCase);
    if (child != null) {
      return child.internalGetPartKeyed(key, toIndex + 1, ignoreCase);
    }
    return null;
  }
  


  public Element getPartKeyed(String key)
  {
    return internalGetPartKeyed(key, 0, false);
  }
  
  public Element getPartKeyedIgnoreCase(String key) { return internalGetPartKeyed(key, 0, true); }
  

  public void addAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener)
  {
    m_sgTransformable.addAbsoluteTransformationListener(absoluteTransformationListener);
  }
  
  public void removeAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener) {
    m_sgTransformable.removeAbsoluteTransformationListener(absoluteTransformationListener);
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.Transformable getSceneGraphTransformable() {
    return m_sgTransformable;
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame getSceneGraphReferenceFrame() {
    return m_sgTransformable;
  }
  
  public Container getSceneGraphContainer() {
    return m_sgTransformable;
  }
  
  public void syncLocalTransformationPropertyToSceneGraph()
  {
    Matrix4d m = m_sgTransformable.getLocalTransformation();
    localTransformation.set(m);
  }
  
  protected void nameValueChanged(String value)
  {
    super.nameValueChanged(value);
    String s = null;
    if (value != null) {
      s = value + ".m_sgTransformable";
    }
    m_sgTransformable.setName(s);
  }
  
  protected void propertyChanging(Property property, Object value)
  {
    if (property == vehicle) {
      ReferenceFrame vehicleToBe = (ReferenceFrame)value;
      if (vehicleToBe != null) {
        if (vehicleToBe == this) {
          throw new RuntimeException(this + " " + Messages.getString("cannot_be_its_own_vehicle_"));
        }
        
        if (vehicleToBe.getSceneGraphContainer().isDescendantOf(getSceneGraphContainer())) {
          throw new RuntimeException(this + " " + Messages.getString("cannot_have_a_scenegraph_descendant__") + vehicleToBe + Messages.getString("__as_its_vehicle_"));
        }
      }
    } else if (property == localTransformation) {
      if (value == null) {
        throw new NullPointerException();
      }
    } else if (property != isPivotShowing)
    {

      super.propertyChanging(property, value);
    }
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == vehicle) {
      if (value != null) {
        m_sgTransformable.setParent(((ReferenceFrame)value).getSceneGraphContainer());
      } else {
        m_sgTransformable.setParent(null);
      }
    } else if (property == localTransformation) {
      m_sgTransformable.setLocalTransformation((Matrix4d)value);
    } else if (property == isPivotShowing) {
      m_pivotDecorator.setIsShowing((Boolean)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public void setVehiclePreservingAbsoluteTransformation(ReferenceFrame vehicleValue) {
    edu.cmu.cs.stage3.alice.scenegraph.Transformable sgTransformable = getSceneGraphTransformable();
    Container sgRoot = sgTransformable.getRoot();
    Matrix4d absoluteTransformation = sgTransformable.getAbsoluteTransformation();
    vehicle.set(vehicleValue);
    if ((sgRoot instanceof edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame)) {
      sgTransformable.setTransformation(absoluteTransformation, (edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame)sgRoot);
      syncLocalTransformationPropertyToSceneGraph();
    }
  }
  
  public double getSpatialRelationDistance(SpatialRelation spatialRelation, Transformable object, ReferenceFrame asSeenBy) {
    try {
      if (asSeenBy == null) {
        asSeenBy = object;
      }
      Box subjectBB = getBoundingBox(asSeenBy);
      Box objectBB = object.getBoundingBox(asSeenBy);
      if (spatialRelation == SpatialRelation.RIGHT_OF)
        return getMinimumx - getMaximumx;
      if (spatialRelation == SpatialRelation.LEFT_OF)
        return getMinimumx - getMaximumx;
      if (spatialRelation == SpatialRelation.ABOVE)
        return getMinimumy - getMaximumy;
      if (spatialRelation == SpatialRelation.BELOW)
        return getMinimumy - getMaximumy;
      if (spatialRelation == SpatialRelation.IN_FRONT_OF)
        return getMinimumz - getMaximumz;
      if (spatialRelation == SpatialRelation.BEHIND) {
        return getMinimumz - getMaximumz;
      }
      throw new RuntimeException();
    }
    catch (NullPointerException npe) {
      warnln(npe); }
    return 0.0D;
  }
  
  public boolean isInSpatialRelationTo(SpatialRelation spatialRelation, Transformable object, ReferenceFrame asSeenBy)
  {
    return getSpatialRelationDistance(spatialRelation, object, asSeenBy) > 0.0D;
  }
  


























  public boolean isRightOf(Transformable object, ReferenceFrame asSeenBy)
  {
    return isInSpatialRelationTo(SpatialRelation.RIGHT_OF, object, asSeenBy);
  }
  
  public boolean isRightOf(Transformable object) { return isRightOf(object, null); }
  
  public boolean isLeftOf(Transformable object, ReferenceFrame asSeenBy) {
    return isInSpatialRelationTo(SpatialRelation.LEFT_OF, object, asSeenBy);
  }
  
  public boolean isLeftOf(Transformable object) { return isLeftOf(object, null); }
  
  public boolean isAbove(Transformable object, ReferenceFrame asSeenBy) {
    return isInSpatialRelationTo(SpatialRelation.ABOVE, object, asSeenBy);
  }
  
  public boolean isAbove(Transformable object) { return isAbove(object, null); }
  
  public boolean isBelow(Transformable object, ReferenceFrame asSeenBy) {
    return isInSpatialRelationTo(SpatialRelation.BELOW, object, asSeenBy);
  }
  
  public boolean isBelow(Transformable object) { return isBelow(object, null); }
  
  public boolean isInFrontOf(Transformable object, ReferenceFrame asSeenBy) {
    return isInSpatialRelationTo(SpatialRelation.IN_FRONT_OF, object, asSeenBy);
  }
  
  public boolean isInFrontOf(Transformable object) { return isInFrontOf(object, null); }
  
  public boolean isBehind(Transformable object, ReferenceFrame asSeenBy) {
    return isInSpatialRelationTo(SpatialRelation.BEHIND, object, asSeenBy);
  }
  
  public boolean isBehind(Transformable object) { return isBehind(object, null); }
  
  public double getVolume()
  {
    Sphere sphere = getBoundingSphere();
    if (sphere != null) {
      double r = sphere.getRadius();
      return r * r;
    }
    return 0.0D;
  }
  
  public Matrix44 getLocalTransformation()
  {
    Matrix4d localTransformation = m_sgTransformable.getLocalTransformation();
    if (localTransformation != null) {
      return new Matrix44(localTransformation);
    }
    return null;
  }
  
  public Matrix44 getTransformation(Vector3d offset, ReferenceFrame asSeenBy)
  {
    ReferenceFrame vehicleValue = (ReferenceFrame)vehicle.getValue();
    if (asSeenBy == null) {
      asSeenBy = vehicleValue;
    }
    if ((asSeenBy == vehicleValue) && (offset == null)) {
      return getLocalTransformation();
    }
    return super.getTransformation(offset, asSeenBy);
  }
  
  private static Transformable s_getAGoodLookDummy = null;
  
  protected double getViewingAngleForGetAGoodLookAt() { return 0.7853981633974483D; }
  




































  public Matrix4d calculateGoodLookAt(ReferenceFrame target, ReferenceFrame asSeenBy, HowMuch howMuch)
  {
    if (s_getAGoodLookDummy == null) {
      s_getAGoodLookDummy = new Transformable();
      s_getAGoodLookDummyname.set("s_getAGoodLookDummy");
    }
    


    if ((target instanceof Transformable)) {
      s_getAGoodLookDummyvehicle.set(vehicle.get());
    } else {
      s_getAGoodLookDummyvehicle.set(target);
    }
    Sphere bs = target.getBoundingSphere();
    Vector3d center = bs.getCenter();
    double radius = bs.getRadius();
    if ((center == null) || (Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isNaN(z))) {
      center = target.getPosition();
    }
    if ((radius == 0.0D) || (Double.isNaN(radius))) {
      radius = 1.0D;
    }
    
    double theta = getViewingAngleForGetAGoodLookAt();
    double dist = radius / Math.sin(theta / 2.0D);
    double offset = dist / Math.sqrt(3.0D);
    
    s_getAGoodLookDummy.setPositionRightNow(x - offset, y + offset, z + offset, target);
    s_getAGoodLookDummy.pointAtRightNow(target, center, null, asSeenBy);
    
    Matrix4d m = s_getAGoodLookDummy.getTransformation(asSeenBy);
    s_getAGoodLookDummyvehicle.set(null);
    return m;
  }
  
  public Matrix4d calculateGoodLookAt(ReferenceFrame target, ReferenceFrame asSeenBy) { return calculateGoodLookAt(target, asSeenBy, HowMuch.INSTANCE_AND_PARTS); }
  
  public Matrix4d calculateGoodLookAt(ReferenceFrame target) {
    return calculateGoodLookAt(target, null);
  }
  
  public void getAGoodLookAtRightNow(ReferenceFrame target, ReferenceFrame asSeenBy, HowMuch howMuch) { setTransformationRightNow(calculateGoodLookAt(target, asSeenBy, howMuch), asSeenBy); }
  
  public void getAGoodLookAtRightNow(ReferenceFrame target, ReferenceFrame asSeenBy) {
    getAGoodLookAtRightNow(target, asSeenBy, HowMuch.INSTANCE_AND_PARTS);
  }
  
  public void getAGoodLookAtRightNow(ReferenceFrame target) { getAGoodLookAtRightNow(target, null); }
  
  public Matrix44 calculateTransformation(Matrix4d m, ReferenceFrame asSeenBy)
  {
    ReferenceFrame vehicleValue = (ReferenceFrame)vehicle.getValue();
    if (asSeenBy == null) {
      asSeenBy = vehicleValue;
    }
    if (asSeenBy == vehicleValue) {
      return new Matrix44(m);
    }
    Matrix4d asSeenByAbsolute = asSeenBy.getSceneGraphReferenceFrame().getAbsoluteTransformation();
    Matrix4d vehicleInverseAbsolute = vehicleValue.getSceneGraphReferenceFrame().getInverseAbsoluteTransformation();
    return Matrix44.multiply(m, Matrix44.multiply(asSeenByAbsolute, vehicleInverseAbsolute));
  }
  

  public static Matrix33 calculateOrientation(Vector3d forward, Vector3d upGuide) { return edu.cmu.cs.stage3.alice.scenegraph.Transformable.calculateOrientation(forward, upGuide); }
  
  public Matrix33 calculateStandUp(ReferenceFrame asSeenBy) { edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    if (asSeenBy != null) {
      sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
    } else {
      sgAsSeenBy = null;
    }
    return m_sgTransformable.calculateStandUp(sgAsSeenBy);
  }
  
  public Matrix33 calculatePointAt(ReferenceFrame target, Vector3d offset, Vector3d upGuide, ReferenceFrame asSeenBy, boolean onlyAffectYaw) { edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    if (asSeenBy != null) {
      sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
    } else {
      sgAsSeenBy = null;
    }
    return m_sgTransformable.calculatePointAt(target.getSceneGraphReferenceFrame(), offset, upGuide, sgAsSeenBy, onlyAffectYaw); }
  /**
   * @deprecated
   */
  public Matrix33 calculatePointAt(ReferenceFrame target, Vector3d offset, Vector3d upGuide, ReferenceFrame asSeenBy) { return calculatePointAt(target, offset, upGuide, asSeenBy, false); }
  
  public static Vector3 calculateResizeScale(Dimension dimension, double amount, boolean likeRubber) { double squash;
    double squash;
    if (likeRubber) {
      squash = 1.0D / Math.sqrt(amount);
    } else {
      squash = 1.0D;
    }
    Vector3 scale = Vector3.multiply(dimension.getScaleAxis(), amount);
    if (x == 0.0D) {
      x = squash;
    }
    if (y == 0.0D) {
      y = squash;
    }
    if (z == 0.0D) {
      z = squash;
    }
    return scale;
  }
  

  public void setAbsoluteTransformationRightNow(Matrix4d m)
  {
    m_sgTransformable.setAbsoluteTransformation(m);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void setLocalTransformationRightNow(Matrix4d m) {
    m_sgTransformable.setLocalTransformation(m);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void setTransformationRightNow(Matrix4d m, ReferenceFrame asSeenBy) { m_sgTransformable.setLocalTransformation(calculateTransformation(m, asSeenBy));
    syncLocalTransformationPropertyToSceneGraph(); }
  
  public void setPositionRightNow(Vector3d position, ReferenceFrame asSeenBy) { edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    if (asSeenBy != null) {
      sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
    } else {
      sgAsSeenBy = null;
    }
    m_sgTransformable.setPosition(position, sgAsSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void placeOnRightNow(ReferenceFrame landmark, Vector3d offset, ReferenceFrame asSeenBy) {
    if (asSeenBy == null) {
      asSeenBy = ReferenceFrame.ABSOLUTE;
    }
    Vector3d a = getBoundingBox(asSeenBy).getCenterOfBottomFace();
    Vector3d b = landmark.getBoundingBox(asSeenBy).getCenterOfTopFace();
    Vector3d v = MathUtilities.subtract(b, a);
    if (offset != null) {
      v.add(offset);
    }
    moveRightNow(v, asSeenBy);
  }
  
  public void placeOnRightNow(ReferenceFrame landmark, double[] offset, ReferenceFrame asSeenBy) { placeOnRightNow(landmark, new Vector3d(offset), asSeenBy); }
  
  public void placeOnRightNow(ReferenceFrame landmark, double x, double y, double z, ReferenceFrame asSeenBy) {
    placeOnRightNow(landmark, new Vector3d(x, y, z), asSeenBy);
  }
  
  public void placeOnRightNow(ReferenceFrame landmark, Vector3d offset) { placeOnRightNow(landmark, offset, null); }
  
  public void placeOnRightNow(ReferenceFrame landmark, double[] offset) {
    placeOnRightNow(landmark, new Vector3d(offset));
  }
  
  public void placeOnRightNow(ReferenceFrame landmark, double x, double y, double z) { placeOnRightNow(landmark, new Vector3d(x, y, z)); }
  

  public void placeOnRightNow(ReferenceFrame landmark) { placeOnRightNow(landmark, null); }
  
  public void setOrientationRightNow(Matrix3d axes, ReferenceFrame asSeenBy) {
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    if (asSeenBy != null) {
      sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
    } else {
      sgAsSeenBy = null;
    }
    m_sgTransformable.setAxes(axes, sgAsSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void setOrientationRightNow(Quaternion quaternion, ReferenceFrame asSeenBy) { setOrientationRightNow(quaternion.getMatrix33(), asSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void setOrientationRightNow(EulerAngles eulerAngles, ReferenceFrame asSeenBy) { setOrientationRightNow(EulerAngles.revolutionsToRadians(eulerAngles).getMatrix33(), asSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void setOrientationRightNow(Vector3d[] orientation, ReferenceFrame asSeenBy) { setOrientationRightNow(orientation[0], orientation[1], asSeenBy); }
  
  public void setOrientationRightNow(Vector3d forward, Vector3d upGuide, ReferenceFrame asSeenBy) {
    setOrientationRightNow(calculateOrientation(forward, upGuide), asSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void pointAtRightNow(ReferenceFrame target, Vector3d offset, Vector3d upGuide, ReferenceFrame asSeenBy, boolean onlyAffectYaw) { setOrientationRightNow(calculatePointAt(target, offset, upGuide, asSeenBy, onlyAffectYaw), asSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void scaleSpaceRightNow(Vector3d axis, ReferenceFrame asSeenBy) { if (asSeenBy == null) {
      asSeenBy = this;
    }
    Matrix44 m = new Matrix44(getTransformation(asSeenBy));
    m.scale(axis);
    setTransformationRightNow(m, asSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  protected void scalePositionsOfPosesRightNow(Transformable t, Vector3d scale, ReferenceFrame asSeenBy) {
    for (int i = 0; i < poses.size(); i++) {
      Pose poseI = (Pose)poses.get(i);
      poseI.scalePositionRightNow(t, this, scale, asSeenBy);
    }
    Element parent = getParent();
    if ((parent instanceof Transformable))
      ((Transformable)parent).scalePositionsOfPosesRightNow(t, scale, asSeenBy);
  }
  
  protected void scalePositionRightNow(Vector3d scale, ReferenceFrame asSeenBy) {
    Matrix4d m = getLocalTransformation();
    m30 *= x;
    m31 *= y;
    m32 *= z;
    setLocalTransformationRightNow(m);
    scalePositionsOfPosesRightNow(this, scale, asSeenBy);
  }
  
  public void resizeRightNow(Vector3d scale, ReferenceFrame asSeenBy, HowMuch howMuch) {
    for (int i = 0; i < m_sgTransformable.getChildCount(); i++) {
      edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = m_sgTransformable.getChildAt(i);
      Object bonus = sgComponent.getBonus();
      if ((sgComponent instanceof edu.cmu.cs.stage3.alice.scenegraph.Transformable)) {
        if ((bonus instanceof Transformable)) {
          Transformable transformable = (Transformable)bonus;
          if (transformable != null) {
            transformable.scalePositionRightNow(scale, asSeenBy);
            if ((howMuch.getDescend()) && (
              (!isFirstClass.booleanValue()) || (!howMuch.getRespectDescendant())))
            {

              transformable.resizeRightNow(scale, asSeenBy);
            }
          }
        }
      }
      else if (((sgComponent instanceof Visual)) && 
        ((bonus instanceof Model))) {
        Model model = (Model)bonus;
        if ((model != null) && 
          (sgComponent == model.getSceneGraphVisual())) {
          model.scaleVisualRightNow(scale, asSeenBy);
        }
      }
    }
  }
  


  public void setSizeRightNow(Vector3d size, ReferenceFrame asSeenBy)
  {
    Vector3d prevSize = getSize();
    Vector3d deltaSize = new Vector3d(1.0D, 1.0D, 1.0D);
    if ((x != x) && (x != 0.0D)) {
      x /= x;
    }
    if ((y != y) && (y != 0.0D)) {
      y /= y;
    }
    if ((z != z) && (z != 0.0D)) {
      z /= z;
    }
    if ((MathUtilities.contains(deltaSize, NaN.0D)) || 
      (MathUtilities.contains(deltaSize, Double.POSITIVE_INFINITY)) || 
      (MathUtilities.contains(deltaSize, Double.NEGATIVE_INFINITY))) {
      throw new IllegalArgumentException(Messages.getString("size__") + size + Messages.getString("__previous_size__") + prevSize);
    }
    resizeRightNow(deltaSize, asSeenBy);
  }
  
  public void setScaledSpaceRightNow(Vector3d scale, ReferenceFrame asSeenBy) {
    Vector3d prevScale = getScaledSpace(asSeenBy);
    Vector3d deltaScale = Vector3.divide(scale, prevScale);
    scaleSpaceRightNow(deltaScale, asSeenBy);
  }
  
  public void moveRightNow(Vector3d vector, ReferenceFrame asSeenBy, boolean isScaledBySize) { edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    if (asSeenBy != null) {
      sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
    } else {
      sgAsSeenBy = null;
    }
    if (isScaledBySize) {
      vector = Vector3.multiply(vector, getSize());
    }
    m_sgTransformable.translate(vector, sgAsSeenBy);
    syncLocalTransformationPropertyToSceneGraph(); }
  
  public void rotateRightNow(Vector3d axis, double amount, ReferenceFrame asSeenBy) { edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    if (asSeenBy != null) {
      sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
    } else {
      sgAsSeenBy = null;
    }
    m_sgTransformable.rotate(axis, amount / 0.15915494309189535D, sgAsSeenBy);
    syncLocalTransformationPropertyToSceneGraph();
  }
  
  public void turnRightNow(Direction direction, double amount, ReferenceFrame asSeenBy) { rotateRightNow(direction.getTurnAxis(), amount, asSeenBy); }
  
  public void rollRightNow(Direction direction, double amount, ReferenceFrame asSeenBy) {
    rotateRightNow(direction.getRollAxis(), amount, asSeenBy);
  }
  
  public void standUpRightNow(ReferenceFrame asSeenBy) { setOrientationRightNow(calculateStandUp(asSeenBy), asSeenBy); }
  

  public void setPointOfViewRightNow(Matrix4d m, ReferenceFrame asSeenBy)
  {
    setTransformationRightNow(m, asSeenBy);
  }
  
  public void setPointOfViewRightNow(Matrix4d m) { setPointOfViewRightNow(m, null); }
  
  public void setPointOfViewRightNow(ReferenceFrame asSeenBy) {
    setPointOfViewRightNow(MathUtilities.createIdentityMatrix4d(), asSeenBy);
  }
  
  public void setPositionRightNow(Vector3d position) { setPositionRightNow(position, null); }
  
  public void setPositionRightNow(double[] xyz) {
    setPositionRightNow(new Vector3d(xyz), null);
  }
  
  public void setPositionRightNow(double x, double y, double z, ReferenceFrame asSeenBy) { setPositionRightNow(new Vector3d(x, y, z), asSeenBy); }
  
  public void setPositionRightNow(double x, double y, double z) {
    setPositionRightNow(x, y, z, null);
  }
  
  public void setPositionRightNow(ReferenceFrame asSeenBy) { setPositionRightNow(Vector3.ZERO, asSeenBy); }
  
  public void setOrientationRightNow(Matrix3d axes) {
    setOrientationRightNow(axes, null);
  }
  
  public void setOrientationRightNow(ReferenceFrame asSeenBy) { setOrientationRightNow(MathUtilities.getIdentityMatrix3d(), asSeenBy); }
  
  public void setOrientationRightNow(Quaternion quaternion) {
    setOrientationRightNow(quaternion, null);
  }
  
  public void setOrientationRightNow(Vector3d[] orientation) { setOrientationRightNow(orientation, null); }
  
  public void setOrientationRightNow(Vector3d forward, Vector3d upGuide) {
    setOrientationRightNow(forward, upGuide, null);
  }
  
  public void setOrientationRightNow(Vector3d forward) { setOrientationRightNow(forward, null); }
  
  public void setOrientationRightNow(double[][] orientation, ReferenceFrame asSeenBy) {
    setOrientationRightNow(orientation[0], orientation[1], asSeenBy);
  }
  
  public void setOrientationRightNow(double[][] orientation) { setOrientationRightNow(orientation, null); }
  
  public void setOrientationRightNow(double[] forwardXYZ, double[] upGuideXYZ, ReferenceFrame asSeenBy) {
    setOrientationRightNow(new Vector3d(forwardXYZ), new Vector3d(upGuideXYZ), asSeenBy);
  }
  
  public void setOrientationRightNow(double[] forwardXYZ, double[] upGuideXYZ) { setOrientationRightNow(forwardXYZ, upGuideXYZ, null); }
  
  public void setOrientationRightNow(double[] forwardXYZ) {
    setOrientationRightNow(forwardXYZ, null);
  }
  
  public void setOrientationRightNow(double forwardX, double forwardY, double forwardZ, double upGuideX, double upGuideY, double upGuideZ) { setOrientationRightNow(new Vector3d(forwardX, forwardY, forwardZ), new Vector3d(upGuideX, upGuideY, upGuideZ)); }
  
  public void setOrientationRightNow(double forwardX, double forwardY, double forwardZ) {
    setOrientationRightNow(new Vector3d(forwardX, forwardY, forwardZ), null);
  }
  
  public void setOrientationRightNow(EulerAngles eulerAngles) { setOrientationRightNow(eulerAngles, null); }
  
  public void pointAtRightNow(ReferenceFrame target, Vector3d offset, Vector3d upGuide, ReferenceFrame asSeenBy)
  {
    pointAtRightNow(target, offset, upGuide, asSeenBy, false);
  }
  
  public void pointAtRightNow(ReferenceFrame target, Vector3d offset, Vector3d upGuide) { pointAtRightNow(target, offset, upGuide, null); }
  

  public void pointAtRightNow(ReferenceFrame target, Vector3d offset) { pointAtRightNow(target, offset, null); }
  
  public void pointAtRightNow(ReferenceFrame target) {
    Vector3d offset = null;
    pointAtRightNow(target, offset);
  }
  
  public void pointAtRightNow(ReferenceFrame target, double[] offsetXYZ, double[] upGuideXYZ, ReferenceFrame asSeenBy) { pointAtRightNow(target, new Vector3d(offsetXYZ), new Vector3d(upGuideXYZ), asSeenBy); }
  
  public void pointAtRightNow(ReferenceFrame target, double[] offsetXYZ, double[] upGuideXYZ) {
    pointAtRightNow(target, offsetXYZ, upGuideXYZ, null);
  }
  
  public void pointAtRightNow(ReferenceFrame target, double[] offsetXYZ) { pointAtRightNow(target, offsetXYZ, null); }
  
  public void resizeRightNow(Vector3d scale, ReferenceFrame asSeenBy)
  {
    resizeRightNow(scale, asSeenBy, HowMuch.INSTANCE_AND_PARTS);
  }
  
  public void resizeRightNow(Vector3d scale) { resizeRightNow(scale, null); }
  
  public void resizeRightNow(Dimension dimension, double amount, boolean likeRubber, ReferenceFrame asSeenBy, HowMuch howMuch) {
    resizeRightNow(calculateResizeScale(dimension, amount, likeRubber), asSeenBy, howMuch);
  }
  
  public void resizeRightNow(Dimension dimension, double amount, boolean likeRubber, ReferenceFrame asSeenBy) { resizeRightNow(dimension, amount, likeRubber, asSeenBy, HowMuch.INSTANCE_AND_PARTS); }
  
  public void resizeRightNow(Dimension dimension, double amount, boolean likeRubber) {
    resizeRightNow(dimension, amount, likeRubber, null);
  }
  
  public void resizeRightNow(Dimension dimension, double amount) { resizeRightNow(dimension, amount, false); }
  
  public void resizeRightNow(double x, double y, double z, ReferenceFrame asSeenBy) {
    resizeRightNow(new Vector3d(x, y, z), asSeenBy);
  }
  
  public void resizeRightNow(double x, double y, double z) { resizeRightNow(x, y, z, null); }
  
  public void resizeRightNow(double s) {
    resizeRightNow(s, s, s);
  }
  
  public void setSizeRightNow(Vector3d xyz) {
    setSizeRightNow(xyz, null);
  }
  
  public void setSizeRightNow(double x, double y, double z, ReferenceFrame asSeenBy) { setSizeRightNow(new Vector3d(x, y, z), asSeenBy); }
  
  public void setSizeRightNow(double x, double y, double z) {
    setSizeRightNow(x, y, z, null);
  }
  
  public void setSizeRightNow(double[] xyz, ReferenceFrame asSeenBy) { setSizeRightNow(xyz[0], xyz[1], xyz[2], asSeenBy); }
  
  public void setScaledSpaceRightNow(Vector3d xyz) {
    setScaledSpaceRightNow(xyz, null);
  }
  
  public void setScaledSpaceRightNow(double x, double y, double z, ReferenceFrame asSeenBy) { setScaledSpaceRightNow(new Vector3d(x, y, z), asSeenBy); }
  
  public void setScaledSpaceRightNow(double x, double y, double z) {
    setScaledSpaceRightNow(x, y, z, null);
  }
  
  public void setSizeRightNow(double[] xyz) { setSizeRightNow(xyz[0], xyz[1], xyz[2]); }
  

  public void moveRightNow(Direction direction, double amount, ReferenceFrame asSeenBy, boolean isScaledBySize)
  {
    moveRightNow(Vector3.multiply(direction.getMoveAxis(), amount), asSeenBy, isScaledBySize);
  }
  
  public void moveRightNow(Direction direction, double amount, ReferenceFrame asSeenBy) { moveRightNow(direction, amount, asSeenBy, false); }
  
  public void moveRightNow(Direction direction, double amount) {
    moveRightNow(direction, amount, null);
  }
  
  public void moveRightNow(Vector3d vector, ReferenceFrame asSeenBy) { moveRightNow(vector, asSeenBy, false); }
  
  public void moveRightNow(double[] xyz, ReferenceFrame asSeenBy) {
    moveRightNow(new Vector3d(xyz), asSeenBy);
  }
  
  public void moveRightNow(Vector3d vector) { moveRightNow(vector, null); }
  
  public void moveRightNow(double[] xyz) {
    moveRightNow(xyz, null);
  }
  
  public void moveRightNow(double x, double y, double z, ReferenceFrame asSeenBy) { moveRightNow(new Vector3d(x, y, z), asSeenBy); }
  
  public void moveRightNow(double x, double y, double z) {
    moveRightNow(x, y, z, null);
  }
  
  public void rotateRightNow(Vector3d axis, double amount) { rotateRightNow(axis, amount, null); }
  
  public void turnRightNow(Direction direction, double amount) {
    turnRightNow(direction, amount, null);
  }
  
  public void rollRightNow(Direction direction, double amount) { rollRightNow(direction, amount, null); }
  
  public void standUpRightNow() {
    standUpRightNow(null);
  }
  
  protected PickInfo pick(Vector3d vector, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired)
  {
    RenderTarget renderTarget = (RenderTarget)getWorld().getDescendants(RenderTarget.class)[0];
    
    return renderTarget.getRenderer().pick(((Camera)this).getSceneGraphCamera(), vector, planeMinX, planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance, farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired);
  }
  
  public Vector3d preventPassingThroughOtherObjects(Vector3d vector, double cushion) { if ((z > 0.0D) && 
      ((this instanceof Camera)))
    {
      Camera camera = (Camera)this;
      java.awt.Dimension size = renderTarget.getRenderTargetValue().getAWTComponent().getSize();
      PickInfo pickInfo = camera.pick(width / 2, height / 2);
      cushion += nearClippingPlaneDistance.doubleValue();
      
      if (pickInfo.getCount() > 0)
      {
        Vector3d localPos = pickInfo.getLocalPositionAt(0);
        if (localPos != null) {
          Model model = (Model)pickInfo.getVisualAt(0).getBonus();
          Vector3d pos = model.transformTo(localPos, this);
          if (z < z + cushion) {
            int subElement = pickInfo.getSubElementAt(0);
            IndexedTriangleArray sgITA = (IndexedTriangleArray)geometry.getGeometryValue().getSceneGraphGeometry();
            int index = sgITA.getIndices()[(subElement * 3)];
            Vertex3d vertex = sgITA.getVertices()[index];
            Vector4d transformedNormal = model.transformTo(new edu.cmu.cs.stage3.math.Vector4(normal, 0.0D), this);
            Vector3d normal = new Vector3d(x, y, z);
            Vector3d up = new Vector3d();
            Vector3d slide = new Vector3d();
            y = 1.0D;
            normal.normalize();
            slide.cross(normal, up);
            slide.scale(vector.length() * 0.25D);
            if (x < 0.0D) {
              x = (-x);
              z = (-z);
            }
            vector = slide;
          }
        }
      }
    }
    
    return vector;
  }
  
  public void setPivot(ReferenceFrame pivot) {
    m_sgTransformable.setPivot(pivot.getSceneGraphReferenceFrame());
    syncLocalTransformationPropertyToSceneGraph();
    for (int i = 0; i < m_sgTransformable.getChildCount(); i++) {
      edu.cmu.cs.stage3.alice.scenegraph.Component sgChild = m_sgTransformable.getChildAt(i);
      if ((sgChild instanceof edu.cmu.cs.stage3.alice.scenegraph.Transformable)) {
        if ((sgChild.getBonus() instanceof Transformable)) {
          ((Transformable)sgChild.getBonus()).syncLocalTransformationPropertyToSceneGraph();
        }
      } else if ((sgChild instanceof Visual)) {
        edu.cmu.cs.stage3.alice.scenegraph.Geometry sgGeometry = ((Visual)sgChild).getGeometry();
        if ((sgGeometry instanceof IndexedTriangleArray)) {
          HACK_syncPropertyToSceneGraph("vertices", (IndexedTriangleArray)sgGeometry);
        }
      }
    }
    m_pivotDecorator.markDirty();
  }
}

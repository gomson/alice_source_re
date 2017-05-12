package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.decorator.BoundingBoxDecorator;
import edu.cmu.cs.stage3.alice.core.decorator.BoundingSphereDecorator;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.IntArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.RenderTargetProperty;
import edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.EulerAngles;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Sphere;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;








public abstract class ReferenceFrame
  extends Sandbox
{
  public abstract edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame getSceneGraphReferenceFrame();
  
  public abstract Container getSceneGraphContainer();
  
  public static final ReferenceFrame ABSOLUTE = new World();
  
  public final BooleanProperty eventsStopAscending = new BooleanProperty(this, "eventsStopAscending", null);
  public final BooleanProperty isBoundingBoxShowing = new BooleanProperty(this, "isBoundingBoxShowing", Boolean.FALSE);
  public final BooleanProperty isBoundingSphereShowing = new BooleanProperty(this, "isBoundingSphereShowing", Boolean.FALSE);
  
  private BoundingBoxDecorator m_boundingBoxDecorator = new BoundingBoxDecorator();
  private BoundingSphereDecorator m_boundingSphereDecorator = new BoundingSphereDecorator();
  

  public abstract void addAbsoluteTransformationListener(AbsoluteTransformationListener paramAbsoluteTransformationListener);
  
  public abstract void removeAbsoluteTransformationListener(AbsoluteTransformationListener paramAbsoluteTransformationListener);
  
  public ReferenceFrame()
  {
    m_boundingBoxDecorator.setReferenceFrame(this);
    m_boundingSphereDecorator.setReferenceFrame(this);
  }
  
  protected void internalRelease(int pass) {
    switch (pass) {
    case 1: 
      m_boundingBoxDecorator.internalRelease(1);
      m_boundingSphereDecorator.internalRelease(1);
      break;
    case 2: 
      m_boundingBoxDecorator.internalRelease(2);
      m_boundingBoxDecorator = null;
      m_boundingSphereDecorator.internalRelease(2);
      m_boundingSphereDecorator = null;
    }
    
    super.internalRelease(pass);
  }
  
  public boolean doEventsStopAscending() { return eventsStopAscending.booleanValue(isFirstClass.booleanValue()); }
  
  private void isBoundingBoxShowingValueChanged(Boolean value)
  {
    m_boundingBoxDecorator.setIsShowing(value);
  }
  
  private void isBoundingSphereShowingValueChanged(Boolean value) { m_boundingSphereDecorator.setIsShowing(value); }
  
  protected void propertyChanged(Property property, Object value)
  {
    if (property == isBoundingBoxShowing) {
      isBoundingBoxShowingValueChanged((Boolean)value);
    } else if (property == isBoundingSphereShowing) {
      isBoundingSphereShowingValueChanged((Boolean)value);
    } else
      super.propertyChanged(property, value); }
  
  /**
   * @deprecated
   */
  public Matrix44 getAbsoluteTransformation() { return new Matrix44(getSceneGraphReferenceFrame().getAbsoluteTransformation()); }
  
  public Matrix44 getTransformation(Vector3d offset, ReferenceFrame asSeenBy) {
    if ((asSeenBy == this) && (offset == null))
      return new Matrix44();
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
    if (asSeenBy != null) {
      sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
    } else {
      sgAsSeenBy = null;
    }
    return getSceneGraphReferenceFrame().getTransformation(offset, sgAsSeenBy);
  }
  
  public Matrix44 getTransformation(ReferenceFrame asSeenBy) {
    return getTransformation(null, asSeenBy);
  }
  
  public Vector3 getPosition(Vector3d offset, ReferenceFrame asSeenBy) { return getTransformation(offset, asSeenBy).getPosition(); }
  
  public Vector3 getPosition(ReferenceFrame asSeenBy) {
    return getPosition(null, asSeenBy);
  }
  

  public Matrix33 getOrientationAsAxes(ReferenceFrame asSeenBy) { return getTransformation(asSeenBy).getAxes(); }
  
  public Vector3[] getOrientationAsForwardAndUpGuide(ReferenceFrame asSeenBy) {
    Matrix33 axes = getOrientationAsAxes(asSeenBy);
    Vector3[] orientation = { axes.getRow(2), axes.getRow(1) };
    return orientation;
  }
  
  public Quaternion getOrientationAsQuaternion(ReferenceFrame asSeenBy) { return getOrientationAsAxes(asSeenBy).getQuaternion(); }
  
  public EulerAngles getOrientationAsEulerAngles(ReferenceFrame asSeenBy) {
    return EulerAngles.radiansToRevolutions(getOrientationAsAxes(asSeenBy).getEulerAngles());
  }
  
  public Vector3 getScaledSpace(ReferenceFrame asSeenBy) { return getOrientationAsAxes(asSeenBy).getScaledSpace(); }
  

  public double getDistanceTo(ReferenceFrame other) { return Math.sqrt(getDistanceSquaredTo(other)); }
  
  public double getDistanceSquaredTo(ReferenceFrame other) {
    Vector3 pos = getPosition(other);
    return pos.getLengthSquared();
  }
  
  public Vector4d transformTo(Vector4d xyzw, ReferenceFrame to) {
    return getSceneGraphReferenceFrame().transformTo(xyzw, to.getSceneGraphReferenceFrame());
  }
  
  public Vector3d transformTo(Vector3d xyz, ReferenceFrame to) { return getSceneGraphReferenceFrame().transformTo(xyz, to.getSceneGraphReferenceFrame()); }
  
  public Vector3d transformToViewport(Vector3 xyz, Camera camera) {
    Vector3d xyzInCamera = transformTo(xyz, camera);
    RenderTarget renderTarget = renderTarget.getRenderTargetValue();
    return renderTarget.getInternal().transformFromCameraToViewport(xyzInCamera, camera.getSceneGraphCamera());
  }
  
  public Matrix44 getTransformation()
  {
    return getTransformation(null);
  }
  
  public Matrix44 getPointOfView(ReferenceFrame asSeenBy) { return getTransformation(asSeenBy); }
  
  public Matrix44 getPointOfView() {
    return getPointOfView(null);
  }
  
  public Vector3 getPosition() { return getPosition(null); }
  
  public Matrix33 getOrientationAsAxes() {
    return getOrientationAsAxes(null);
  }
  
  public Vector3[] getOrientationAsForwardAndUpGuide() { return getOrientationAsForwardAndUpGuide(null); }
  
  public Quaternion getOrientationAsQuaternion() {
    return getOrientationAsQuaternion(null);
  }
  
  public EulerAngles getOrientationAsEulerAngles() { return getOrientationAsEulerAngles(null); }
  
  public Vector3 getScaledSpace() {
    return getScaledSpace(null);
  }
  
  public Vector3d transformTo(double[] xyz, ReferenceFrame to) {
    return transformTo(new Vector3(xyz), to);
  }
  
  public Vector3d transformTo(double x, double y, double z, ReferenceFrame to) { return transformTo(new Vector3(x, y, z), to); }
  
  public Vector3d transformToViewport(double[] xyz, Camera camera) {
    return transformToViewport(new Vector3(xyz), camera);
  }
  
  public Vector3d transformToViewport(double x, double y, double z, Camera camera) { return transformToViewport(new Vector3(x, y, z), camera); }
  


































  protected void updateBoundingSphere(Sphere sphere, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden)
  {
    if (howMuch.getDescend()) {
      for (int i = 0; i < getChildCount(); i++) {
        Element child = getChildAt(i);
        if (((child instanceof ReferenceFrame)) && (
          (!howMuch.getRespectDescendant()) || (!isFirstClass.booleanValue())))
        {

          ((ReferenceFrame)child).updateBoundingSphere(sphere, asSeenBy, howMuch, ignoreHidden);
        }
      }
    }
  }
  
  protected void updateBoundingBox(Box box, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
    if (howMuch.getDescend()) {
      for (int i = 0; i < getChildCount(); i++) {
        Element child = getChildAt(i);
        if (((child instanceof ReferenceFrame)) && (
          (!howMuch.getRespectDescendant()) || (!isFirstClass.booleanValue())))
        {

          ((ReferenceFrame)child).updateBoundingBox(box, asSeenBy, howMuch, ignoreHidden);
        }
      }
    }
  }
  
  public Sphere getBoundingSphere(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden)
  {
    if (asSeenBy == null) {
      asSeenBy = this;
    }
    Sphere sphere = new Sphere(null, NaN.0D);
    
    updateBoundingSphere(sphere, asSeenBy, howMuch, ignoreHidden);
    return sphere;
  }
  
  public Box getBoundingBox(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) { if (asSeenBy == null) {
      asSeenBy = this;
    }
    Box box = new Box(null, null);
    updateBoundingBox(box, asSeenBy, howMuch, ignoreHidden);
    return box;
  }
  
  public Vector3 getSize(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) { Box box = getBoundingBox(asSeenBy, howMuch, ignoreHidden);
    if (box != null) {
      return new Vector3(box.getWidth(), box.getHeight(), box.getDepth());
    }
    return new Vector3(0.0D, 0.0D, 0.0D);
  }
  
  public double getSizeAlongDimension(Dimension dimension, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
    Box box = getBoundingBox(asSeenBy, howMuch, ignoreHidden);
    if (box != null) {
      if (dimension == Dimension.LEFT_TO_RIGHT)
        return box.getWidth();
      if (dimension == Dimension.TOP_TO_BOTTOM)
        return box.getHeight();
      if (dimension == Dimension.FRONT_TO_BACK) {
        return box.getDepth();
      }
      throw new IllegalArgumentException(dimension + " " + Messages.getString("is_expected_to_be_in__LEFT_TO_RIGHT__TOP_TO_BOTTOM__FRONT_TO_BACK__"));
    }
    

    return 0.0D;
  }
  
  public double getWidth(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden)
  {
    return getSizeAlongDimension(Dimension.LEFT_TO_RIGHT, asSeenBy, howMuch, ignoreHidden);
  }
  
  public double getHeight(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) { return getSizeAlongDimension(Dimension.TOP_TO_BOTTOM, asSeenBy, howMuch, ignoreHidden); }
  
  public double getDepth(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
    return getSizeAlongDimension(Dimension.FRONT_TO_BACK, asSeenBy, howMuch, ignoreHidden);
  }
  
  public Sphere getBoundingSphere(ReferenceFrame asSeenBy, HowMuch howMuch) {
    return getBoundingSphere(asSeenBy, howMuch, false);
  }
  
  public Sphere getBoundingSphere(ReferenceFrame asSeenBy) { return getBoundingSphere(asSeenBy, HowMuch.INSTANCE_AND_PARTS); }
  
  public Sphere getBoundingSphere() {
    return getBoundingSphere(null);
  }
  
  public Box getBoundingBox(ReferenceFrame asSeenBy, HowMuch howMuch) { return getBoundingBox(asSeenBy, howMuch, false); }
  
  public Box getBoundingBox(ReferenceFrame asSeenBy) {
    return getBoundingBox(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
  }
  
  public Box getBoundingBox() { return getBoundingBox(null); }
  
  public Vector3 getSize(ReferenceFrame asSeenBy, HowMuch howMuch) {
    return getSize(asSeenBy, howMuch, false);
  }
  
  public Vector3 getSize(ReferenceFrame asSeenBy) { return getSize(asSeenBy, HowMuch.INSTANCE_AND_PARTS); }
  
  public Vector3 getSize() {
    return getSize(null);
  }
  
  public double getSizeAlongDimension(Dimension dimension, ReferenceFrame asSeenBy, HowMuch howMuch) { return getSizeAlongDimension(dimension, asSeenBy, howMuch, false); }
  
  public double getSizeAlongDimension(Dimension dimension, ReferenceFrame asSeenBy) {
    return getSizeAlongDimension(dimension, asSeenBy, HowMuch.INSTANCE_AND_PARTS);
  }
  
  public double getSizeAlongDimension(Dimension dimension) { return getSizeAlongDimension(dimension, null); }
  
  public double getWidth(ReferenceFrame asSeenBy, HowMuch howMuch) {
    return getWidth(asSeenBy, howMuch, false);
  }
  
  public double getWidth(ReferenceFrame asSeenBy) { return getWidth(asSeenBy, HowMuch.INSTANCE_AND_PARTS); }
  
  public double getWidth() {
    return getWidth(null);
  }
  
  public double getHeight(ReferenceFrame asSeenBy, HowMuch howMuch) { return getHeight(asSeenBy, howMuch, false); }
  
  public double getHeight(ReferenceFrame asSeenBy) {
    return getHeight(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
  }
  
  public double getHeight() { return getHeight(null); }
  
  public double getDepth(ReferenceFrame asSeenBy, HowMuch howMuch) {
    return getDepth(asSeenBy, howMuch, false);
  }
  
  public double getDepth(ReferenceFrame asSeenBy) { return getDepth(asSeenBy, HowMuch.INSTANCE_AND_PARTS); }
  
  public double getDepth() {
    return getDepth(null);
  }
  
  protected static void HACK_syncPropertyToSceneGraph(String propertyName, edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray sgITA)
  {
    edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray)sgITA.getBonus();
    if (propertyName.equals("vertices")) {
      vertices.set(sgITA.getVertices());
    } else if (propertyName.equals("indices")) {
      indices.set(sgITA.getIndices());
    }
  }
}

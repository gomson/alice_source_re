package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.GeometryProperty;
import edu.cmu.cs.stage3.alice.core.property.IntArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.pratt.maxkeyframing.CatmullRomSpline;
import edu.cmu.cs.stage3.pratt.maxkeyframing.Key;
import edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse;
import edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey;
import edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse;
import edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline;
import edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse;
import edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3SimpleKey;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Vector;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;


















public class ASFBone
{
  public static final Integer DOF_TX = new Integer(0);
  public static final Integer DOF_TY = new Integer(1);
  public static final Integer DOF_TZ = new Integer(2);
  public static final Integer DOF_RX = new Integer(3);
  public static final Integer DOF_RY = new Integer(4);
  public static final Integer DOF_RZ = new Integer(5);
  public static final Integer DOF_L = new Integer(6);
  
  private static final World scene = new World();
  
  public String name;
  
  public Vector3d direction;
  
  public double length;
  
  public Matrix33 model_transform;
  
  public Matrix33 base_axis;
  
  public Matrix33 axis;
  
  public Vector3d base_position;
  
  public Vector3d position;
  
  public Vector dof;
  public Vector children;
  public Vector3d endPoint;
  public Model model;
  public Model realMod;
  public ASFBone parent;
  public double width;
  public PositionKeyframeResponse positionKeyframeAnim;
  public QuaternionKeyframeResponse quaternionKeyframeAnim;
  public ScaleKeyframeResponse lengthKeyframeAnim;
  public CatmullRomSpline positionSpline;
  public QuaternionSlerpSpline quaternionSpline;
  public CatmullRomSpline lengthSpline;
  public double lastTime;
  public boolean hasFrame;
  public boolean accumulated;
  
  public ASFBone()
  {
    name = "";
    dof = new Vector();
    length = 0.04D;
    width = 0.04D;
    children = new Vector();
    base_position = new Vector3d(0.0D, 0.0D, 0.0D);
    position = new Vector3d(0.0D, 0.0D, 0.0D);
    direction = new Vector3d(0.0D, 0.0D, 1.0D);
    base_axis = new Matrix33();
    axis = new Matrix33();
    
    realMod = null;
    lastTime = Double.NEGATIVE_INFINITY;
    hasFrame = false;
    accumulated = false;
    
    parent = null;
    
    positionKeyframeAnim = new PositionKeyframeResponse();
    positionKeyframeAnim.name.set("bonePositionKeyframeAnim");
    positionSpline = new CatmullRomSpline();
    positionKeyframeAnim.spline.set(positionSpline);
    positionKeyframeAnim.duration.set(null);
    
    quaternionKeyframeAnim = new QuaternionKeyframeResponse();
    quaternionKeyframeAnim.name.set("jointAngleKeyframeAnim");
    quaternionSpline = new QuaternionSlerpSpline();
    quaternionKeyframeAnim.spline.set(quaternionSpline);
    quaternionKeyframeAnim.duration.set(null);
    
    lengthKeyframeAnim = new ScaleKeyframeResponse();
    lengthKeyframeAnim.name.set("boneLengthKeyframeAnim");
    lengthSpline = new CatmullRomSpline();
    lengthKeyframeAnim.spline.set(lengthSpline);
    lengthKeyframeAnim.duration.set(null);
  }
  
  public ASFBone(String n) {
    this();
    name = n;
  }
  
  private IndexedTriangleArray buildUnitCube()
  {
    Vertex3d[] vertices = new Vertex3d[24];
    int[] indices = new int[36];
    indices[0] = 0;indices[1] = 1;indices[2] = 2;
    indices[3] = 0;indices[4] = 2;indices[5] = 3;
    indices[6] = 4;indices[7] = 5;indices[8] = 6;
    indices[9] = 4;indices[10] = 6;indices[11] = 7;
    indices[12] = 8;indices[13] = 9;indices[14] = 10;
    indices[15] = 8;indices[16] = 10;indices[17] = 11;
    indices[18] = 12;indices[19] = 13;indices[20] = 14;
    indices[21] = 12;indices[22] = 14;indices[23] = 15;
    indices[24] = 16;indices[25] = 17;indices[26] = 18;
    indices[27] = 16;indices[28] = 18;indices[29] = 19;
    indices[30] = 20;indices[31] = 21;indices[32] = 22;
    indices[33] = 20;indices[34] = 22;indices[35] = 23;
    
    vertices[0] = Vertex3d.createXYZIJKUV(-0.5D, 0.5D, 0.0D, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F);
    vertices[1] = Vertex3d.createXYZIJKUV(0.5D, 0.5D, 0.0D, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F);
    vertices[2] = Vertex3d.createXYZIJKUV(0.5D, -0.5D, 0.0D, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F);
    vertices[3] = Vertex3d.createXYZIJKUV(-0.5D, -0.5D, 0.0D, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F);
    
    vertices[4] = Vertex3d.createXYZIJKUV(0.5D, 0.5D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    vertices[5] = Vertex3d.createXYZIJKUV(0.5D, 0.5D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    vertices[6] = Vertex3d.createXYZIJKUV(0.5D, -0.5D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    vertices[7] = Vertex3d.createXYZIJKUV(0.5D, -0.5D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    
    vertices[8] = Vertex3d.createXYZIJKUV(0.5D, 0.5D, 1.0D, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F);
    vertices[9] = Vertex3d.createXYZIJKUV(-0.5D, 0.5D, 1.0D, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F);
    vertices[10] = Vertex3d.createXYZIJKUV(-0.5D, -0.5D, 1.0D, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F);
    vertices[11] = Vertex3d.createXYZIJKUV(0.5D, -0.5D, 1.0D, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F);
    
    vertices[12] = Vertex3d.createXYZIJKUV(-0.5D, 0.5D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    vertices[13] = Vertex3d.createXYZIJKUV(-0.5D, 0.5D, 0.0D, -1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    vertices[14] = Vertex3d.createXYZIJKUV(-0.5D, -0.5D, 0.0D, -1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    vertices[15] = Vertex3d.createXYZIJKUV(-0.5D, -0.5D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    
    vertices[16] = Vertex3d.createXYZIJKUV(-0.5D, 0.5D, 1.0D, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
    vertices[17] = Vertex3d.createXYZIJKUV(0.5D, 0.5D, 1.0D, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
    vertices[18] = Vertex3d.createXYZIJKUV(0.5D, 0.5D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
    vertices[19] = Vertex3d.createXYZIJKUV(-0.5D, 0.5D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0F, 0.0F);
    
    vertices[20] = Vertex3d.createXYZIJKUV(-0.5D, -0.5D, 0.0D, 0.0D, -1.0D, 0.0D, 0.0F, 0.0F);
    vertices[21] = Vertex3d.createXYZIJKUV(0.5D, -0.5D, 0.0D, 0.0D, -1.0D, 0.0D, 0.0F, 0.0F);
    vertices[22] = Vertex3d.createXYZIJKUV(0.5D, -0.5D, 1.0D, 0.0D, -1.0D, 0.0D, 0.0F, 0.0F);
    vertices[23] = Vertex3d.createXYZIJKUV(-0.5D, -0.5D, 1.0D, 0.0D, -1.0D, 0.0D, 0.0F, 0.0F);
    
    IndexedTriangleArray ita = new IndexedTriangleArray();
    indices.set(indices);
    vertices.set(vertices);
    return ita;
  }
  







  private IndexedTriangleArray buildBoneGeometry(double width, double length)
  {
    Matrix44 coordSys = Matrix44.IDENTITY;
    




    Vertex3d[] vertices = new Vertex3d[24];
    int[] indices = new int[36];
    indices[0] = 0;indices[1] = 1;indices[2] = 2;
    indices[3] = 0;indices[4] = 2;indices[5] = 3;
    indices[6] = 4;indices[7] = 5;indices[8] = 6;
    indices[9] = 4;indices[10] = 6;indices[11] = 7;
    indices[12] = 8;indices[13] = 9;indices[14] = 10;
    indices[15] = 8;indices[16] = 10;indices[17] = 11;
    indices[18] = 12;indices[19] = 13;indices[20] = 14;
    indices[21] = 12;indices[22] = 14;indices[23] = 15;
    indices[24] = 16;indices[25] = 17;indices[26] = 18;
    indices[27] = 16;indices[28] = 18;indices[29] = 19;
    indices[30] = 20;indices[31] = 21;indices[32] = 22;
    indices[33] = 20;indices[34] = 22;indices[35] = 23;
    
    Vector4d normal = MathUtilities.multiply(coordSys, new Vector4d(0.0D, 0.0D, -1.0D, 0.0D));
    Vector4d point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, width / 2.0D, 0.0D, 1.0D));
    vertices[0] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, width / 2.0D, 0.0D, 1.0D));
    vertices[1] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, -width / 2.0D, 0.0D, 1.0D));
    vertices[2] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, -width / 2.0D, 0.0D, 1.0D));
    vertices[3] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    
    normal = MathUtilities.multiply(coordSys, new Vector4d(1.0D, 0.0D, 0.0D, 0.0D));
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, width / 2.0D, 0.0D, 1.0D));
    vertices[4] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, width / 2.0D, length, 1.0D));
    vertices[5] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, -width / 2.0D, length, 1.0D));
    vertices[6] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, -width / 2.0D, 0.0D, 1.0D));
    vertices[7] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    



    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, width / 2.0D, length, 1.0D));
    normal = MathUtilities.multiply(coordSys, new Vector4d(0.0D, 0.0D, 1.0D, 0.0D));
    vertices[8] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, width / 2.0D, length, 1.0D));
    vertices[9] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, -width / 2.0D, length, 1.0D));
    vertices[10] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, -width / 2.0D, length, 1.0D));
    vertices[11] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, width / 2.0D, length, 1.0D));
    normal = MathUtilities.multiply(coordSys, new Vector4d(-1.0D, 0.0D, 0.0D, 0.0D));
    vertices[12] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, width / 2.0D, 0.0D, 1.0D));
    vertices[13] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, -width / 2.0D, 0.0D, 1.0D));
    vertices[14] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, -width / 2.0D, length, 1.0D));
    vertices[15] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, width / 2.0D, length, 1.0D));
    normal = MathUtilities.multiply(coordSys, new Vector4d(0.0D, 1.0D, 0.0D, 0.0D));
    vertices[16] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, width / 2.0D, length, 1.0D));
    vertices[17] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, width / 2.0D, 0.0D, 1.0D));
    vertices[18] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, width / 2.0D, 0.0D, 1.0D));
    vertices[19] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    
    normal = MathUtilities.multiply(coordSys, new Vector4d(0.0D, -1.0D, 0.0D, 0.0D));
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, -width / 2.0D, 0.0D, 1.0D));
    vertices[20] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, -width / 2.0D, 0.0D, 1.0D));
    vertices[21] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(width / 2.0D, -width / 2.0D, length, 1.0D));
    vertices[22] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    point = MathUtilities.multiply(coordSys, new Vector4d(-width / 2.0D, -width / 2.0D, length, 1.0D));
    vertices[23] = Vertex3d.createXYZIJKUV(x, y, z, x, y, z, 0.0F, 0.0F);
    
    IndexedTriangleArray ita = new IndexedTriangleArray();
    indices.set(indices);
    vertices.set(vertices);
    
    return ita;
  }
  
  public Model buildBone(ASFBone parent) {
    return buildBone(parent, false);
  }
  
  public Model buildBone(ASFBone parent, boolean length_geometry) {
    if (parent != null) {
      base_position = endPoint;
      model = new Model();
      model.isFirstClass.set(false);
      model.addChild(model);
      model.parts.add(model);
      
      model.vehicle.set(model);
    } else {
      model = new Model();
      model.isFirstClass.set(true);
      model.vehicle.set(scene);
    }
    this.parent = parent;
    
    model.setOrientationRightNow(base_axis, scene);
    model.setPositionRightNow(base_position, scene);
    
    base_axis = model.getOrientationAsAxes();
    base_position = model.getPosition();
    
    IndexedTriangleArray geom = buildBoneGeometry(0.04D, 0.04D);
    model.addChild(geom);
    model.geometry.set(geom);
    model.name.set(name);
    
    if (parent != null) {
      endPoint = MathUtilities.multiply(direction, length / direction.length());
      endPoint.add(endPoint);
    } else {
      endPoint = new Vector3d(0.0D, 0.0D, 0.0D);
    }
    if ((length_geometry) && (length != 0.0D) && (parent != null))
    {

      Model unit_cube = new Model();
      geom = buildBoneGeometry(0.04D, length);
      unit_cube.addChild(geom);
      geometry.set(geom);
      isFirstClass.set(false);
      name.set(model.name.getStringValue().concat("_bone"));
      model.addChild(unit_cube);
      model.parts.add(unit_cube);
      
      vehicle.set(model);
      if ((direction.x == 0.0D) && (direction.z == 0.0D)) {
        unit_cube.setOrientationRightNow(direction, new Vector3d(0.0D, 0.0D, -1.0D), scene);
      } else {
        unit_cube.setOrientationRightNow(direction, new Vector3d(0.0D, 1.0D, 0.0D), scene);
      }
    }
    

    ListIterator li = children.listIterator();
    
    while (li.hasNext()) {
      ASFBone child = (ASFBone)li.next();
      
      Model localModel1 = child.buildBone(this);
    }
    return model;
  }
  
  public void setBasePose(Model mod) {
    setBasePose(mod, true);
  }
  
  public void setBasePose(Model mod, boolean use) {
    if (mod == null) { mod = model;
    }
    if (use) {
      realMod = mod;
      if (name.compareTo("root") == 0) {
        mod.setPointOfViewRightNow(model.getPointOfView());
        if (model != mod) {
          mod.turnRightNow(Direction.LEFT, 0.5D);
        }
      }
      base_axis = model.getOrientationAsAxes(vehicle.getReferenceFrameValue());
      
      Matrix33 toCharacter = mod.getOrientationAsAxes();
      Matrix33 base_axis_inv = new Matrix33(base_axis);
      base_axis_inv.invert();
      
      model_transform = Matrix33.multiply(toCharacter, base_axis_inv);
      
      base_position = MathUtilities.subtract(mod.getPosition(), base_position);
    }
    


    ListIterator li = children.listIterator();
    
    while (li.hasNext()) {
      ASFBone child = (ASFBone)li.next();
      
      Model part = (Model)mod.getChildNamed(name);
      




      if (part == null)
      {

        BoneSelectDialog bsd = new BoneSelectDialog(name, mod.getChildren(Model.class));
        


        bsd.pack();
        bsd.setVisible(true);
        if (bsd.getSelectedPart() != null) {
          part = (Model)bsd.getSelectedPart();
        } else if (bsd.doDescend()) {
          child.setBasePose(mod, false);
        }
      }
      
      if (part != null) {
        child.setBasePose(part);
      }
    }
  }
  
  public void addFrames()
  {
    addFrames(false, Matrix44.IDENTITY);
  }
  
  public void addFrames(Matrix44 accum) {
    addFrames(true, accum);
  }
  
  public void addFrames(boolean hasAccum, Matrix44 accum) {
    Matrix44 newAccum = Matrix44.IDENTITY;
    if (hasAccum)
      accumulated = true;
    if ((hasFrame) || (hasAccum)) {
      newAccum = Matrix44.multiply(new Matrix44(axis, position), accum);
      if (realMod != null) {
        if ((dof.contains(DOF_TX)) || (dof.contains(DOF_TY)) || (dof.contains(DOF_TZ)))
        {
          positionSpline.addKey(new Vector3SimpleKey(lastTime, Matrix44.multiply(new Matrix44(model_transform, new Vector3d()), Matrix44.multiply(newAccum, new Matrix44(base_axis, new Vector3d()))).getPosition()));
        }
        if ((dof.contains(DOF_RX)) || (dof.contains(DOF_RY)) || (dof.contains(DOF_RZ)) || (hasAccum)) {
          quaternionSpline.addKey(new QuaternionKey(lastTime, Matrix33.multiply(model_transform, Matrix33.multiply(newAccum.getAxes(), base_axis)).getQuaternion()));
        }
      }
    }
    

    ListIterator li = children.listIterator();
    
    while (li.hasNext()) {
      ASFBone child = (ASFBone)li.next();
      
      if ((realMod == null) && ((hasFrame) || (hasAccum))) {
        Matrix44 newInvBase = model.getPointOfView(model);
        newInvBase.invert();
        child.addFrames(Matrix44.multiply(Matrix44.multiply(model.getPointOfView(model), newAccum), newInvBase));
      } else {
        child.addFrames();
      }
    }
    
    hasFrame = false;
  }
  
  public Response buildAnim(CompositeResponse anim) {
    if (realMod != null) {
      if ((dof.contains(DOF_TX)) || (dof.contains(DOF_TY)) || (dof.contains(DOF_TZ))) {
        positionKeyframeAnim.subject.set(realMod);
        positionKeyframeAnim.name.set(name.concat(positionKeyframeAnim.name.getStringValue()));
        anim.addChild(positionKeyframeAnim);
        componentResponses.add(positionKeyframeAnim);
      }
      if ((dof.contains(DOF_RX)) || (dof.contains(DOF_RY)) || (dof.contains(DOF_RZ)) || (accumulated)) {
        quaternionKeyframeAnim.subject.set(realMod);
        quaternionKeyframeAnim.name.set(name.concat(quaternionKeyframeAnim.name.getStringValue()));
        anim.addChild(quaternionKeyframeAnim);
        componentResponses.add(quaternionKeyframeAnim);
      }
    }
    










    ListIterator li = children.listIterator();
    
    while (li.hasNext()) {
      ASFBone child = (ASFBone)li.next();
      
      child.buildAnim(anim);
    }
    return anim;
  }
  





  public void buildPoses(Model rootMod, Pose startPose, Pose endPose)
  {
    if (realMod != null) {
      Vector3 position = realMod.getPosition();
      if ((dof.contains(DOF_TX)) || (dof.contains(DOF_TY)) || (dof.contains(DOF_TZ))) {
        Key positionKey = positionSpline.getFirstKey();
        position = (Vector3)positionKey.createSample(positionKey.getValueComponents());
      }
      Quaternion orientation = realMod.getOrientationAsQuaternion();
      if ((dof.contains(DOF_RX)) || (dof.contains(DOF_RY)) || (dof.contains(DOF_RZ)) || (accumulated)) {
        Key quaternionKey = quaternionSpline.getFirstKey();
        orientation = (Quaternion)quaternionKey.createSample(quaternionKey.getValueComponents());
      }
      ((Hashtable)poseMap.getDictionaryValue()).put(realMod.getKey(rootMod), new Matrix44(orientation, position));
      

      if ((dof.contains(DOF_TX)) || (dof.contains(DOF_TY)) || (dof.contains(DOF_TZ))) {
        Key positionKey = positionSpline.getLastKey();
        position = (Vector3)positionKey.createSample(positionKey.getValueComponents());
      }
      if ((dof.contains(DOF_RX)) || (dof.contains(DOF_RY)) || (dof.contains(DOF_RZ)) || (accumulated)) {
        Key quaternionKey = quaternionSpline.getLastKey();
        orientation = (Quaternion)quaternionKey.createSample(quaternionKey.getValueComponents());
      }
      ((Hashtable)poseMap.getDictionaryValue()).put(realMod.getKey(rootMod), new Matrix44(orientation, position));
    }
    


    ListIterator li = children.listIterator();
    
    while (li.hasNext()) {
      ASFBone child = (ASFBone)li.next();
      
      child.buildPoses(rootMod, startPose, endPose);
    }
  }
}

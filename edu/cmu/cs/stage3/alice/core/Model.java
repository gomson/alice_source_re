package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.FillingStyleProperty;
import edu.cmu.cs.stage3.alice.core.property.GeometryProperty;
import edu.cmu.cs.stage3.alice.core.property.Matrix33Property;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ShadingStyleProperty;
import edu.cmu.cs.stage3.alice.core.property.TextureMapProperty;
import edu.cmu.cs.stage3.alice.core.property.VisualizationProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.FillingStyle;
import edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Sphere;
import edu.cmu.cs.stage3.util.HowMuch;
import java.util.Dictionary;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;







public class Model
  extends Transformable
{
  public final ColorProperty color = new ColorProperty(this, "color", Color.WHITE);
  public final ColorProperty ambientColor = new ColorProperty(this, "ambientColor", null);
  public final NumberProperty opacity = new NumberProperty(this, "opacity", new Double(1.0D));
  public final FillingStyleProperty fillingStyle = new FillingStyleProperty(this, "fillingStyle", FillingStyle.SOLID);
  public final ShadingStyleProperty shadingStyle = new ShadingStyleProperty(this, "shadingStyle", ShadingStyle.SMOOTH);
  public final ColorProperty specularHighlightColor = new ColorProperty(this, "specularHighlightColor", Color.BLACK);
  public final NumberProperty specularHighlightExponent = new NumberProperty(this, "specularHighlightExponent", new Double(0.0D));
  public final ColorProperty emissiveColor = new ColorProperty(this, "emissiveColor", Color.BLACK);
  public final TextureMapProperty diffuseColorMap = new TextureMapProperty(this, "diffuseColorMap", null);
  public final TextureMapProperty opacityMap = new TextureMapProperty(this, "opacityMap", null);
  public final TextureMapProperty emissiveColorMap = new TextureMapProperty(this, "emissiveColorMap", null);
  public final TextureMapProperty specularHighlightColorMap = new TextureMapProperty(this, "specularHighlightColorMap", null);
  public final TextureMapProperty bumpMap = new TextureMapProperty(this, "bumpMap", null);
  public final TextureMapProperty detailMap = new TextureMapProperty(this, "detailMap", null);
  public final TextureMapProperty interactionMap = new TextureMapProperty(this, "interactionMap", null);
  public final Matrix33Property visualScale = new Matrix33Property(this, "visualScale", Matrix33.IDENTITY);
  public final BooleanProperty isShowing = new BooleanProperty(this, "isShowing", Boolean.TRUE);
  public final ElementArrayProperty disabledAffectors = new ElementArrayProperty(this, "disabledAffectors", null, [Ledu.cmu.cs.stage3.alice.core.Affector.class);
  public final GeometryProperty geometry = new GeometryProperty(this, "geometry", null);
  public final VisualizationProperty visualization = new VisualizationProperty(this, "visualization", null);
  
  private Visual m_sgVisual;
  private Appearance m_sgAppearance;
  
  protected void internalRelease(int pass)
  {
    switch (pass) {
    case 1: 
      m_sgAppearance.setDiffuseColorMap(null);
      m_sgAppearance.setOpacityMap(null);
      m_sgAppearance.setEmissiveColorMap(null);
      m_sgAppearance.setSpecularHighlightColorMap(null);
      m_sgAppearance.setBumpMap(null);
      m_sgAppearance.setDetailMap(null);
      m_sgVisual.setFrontFacingAppearance(null);
      m_sgVisual.setGeometry(null);
      m_sgVisual.setParent(null);
      break;
    case 2: 
      m_sgVisual.release();
      m_sgVisual = null;
      m_sgAppearance.release();
      m_sgAppearance = null;
    }
    
    super.internalRelease(pass);
  }
  
  public Visual getSceneGraphVisual() {
    return m_sgVisual;
  }
  
  public Appearance getSceneGraphAppearance() { return m_sgAppearance; }
  
  public Model()
  {
    m_sgAppearance = new Appearance();
    m_sgAppearance.setBonus(this);
    m_sgVisual = new Visual();
    m_sgVisual.setParent(getSceneGraphTransformable());
    m_sgVisual.setFrontFacingAppearance(m_sgAppearance);
    m_sgVisual.setBonus(this);
    color.set(m_sgAppearance.getDiffuseColor());
    ambientColor.set(m_sgAppearance.getAmbientColor());
    opacity.set(new Double(m_sgAppearance.getOpacity()));
    fillingStyle.set(m_sgAppearance.getFillingStyle());
    shadingStyle.set(m_sgAppearance.getShadingStyle());
    specularHighlightColor.set(m_sgAppearance.getSpecularHighlightColor());
    specularHighlightExponent.set(new Double(m_sgAppearance.getSpecularHighlightExponent()));
    emissiveColor.set(m_sgAppearance.getEmissiveColor());
    visualScale.set(m_sgVisual.getScale());
    isShowing.set(new Boolean(m_sgVisual.getIsShowing()));
  }
  
  protected void nameValueChanged(String value) {
    super.nameValueChanged(value);
    if (value != null) {
      m_sgVisual.setName(value + ".m_sgVisual");
      m_sgAppearance.setName(value + ".m_sgAppearance");
    } else {
      m_sgVisual.setName(null);
      m_sgAppearance.setName(null);
    }
  }
  
  private static edu.cmu.cs.stage3.alice.scenegraph.TextureMap getSceneGraphTextureMap(TextureMap textureMap) {
    if (textureMap != null) {
      return textureMap.getSceneGraphTextureMap();
    }
    return null;
  }
  
  private static edu.cmu.cs.stage3.alice.scenegraph.Geometry getSceneGraphGeometry(Geometry geometry) {
    if (geometry != null) {
      return geometry.getSceneGraphGeometry();
    }
    return null;
  }
  

  public void propertyCreated(Property property)
  {
    if (property.getName().equals("color")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("ambientColor")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("opacity")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("fillingStyle")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("shadingStyle")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("specularHighlightColor")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("specularHighlightExponent")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("emissiveColor")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("diffuseColorMap")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("opacityMap")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("emissiveColorMap")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("specularHighlightColorMap")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("bumpMap")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("detailMap")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("interactionMap")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("visualScale")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("isShowing")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("disabledAffectors")) {
      property.setIsAcceptingOfHowMuch(true);
    } else if (property.getName().equals("geometry")) {
      property.setIsAcceptingOfHowMuch(true);
    } else {
      super.propertyCreated(property);
    }
  }
  
  protected void propertyChanging(Property property, Object value)
  {
    if (property == visualization) {
      Visualization prev = visualization.getVisualizationValue();
      if (prev != null) {
        prev.unhook(this);
      }
    } else {
      super.propertyChanging(property, value);
    }
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == color) {
      m_sgAppearance.setDiffuseColor((Color)value);
    } else if (property == ambientColor) {
      m_sgAppearance.setAmbientColor((Color)value);
    } else if (property == opacity) {
      m_sgAppearance.setOpacity(((Number)value).doubleValue());
    } else if (property == fillingStyle) {
      m_sgAppearance.setFillingStyle((FillingStyle)value);
    } else if (property == shadingStyle) {
      m_sgAppearance.setShadingStyle((ShadingStyle)value);
    } else if (property == specularHighlightColor) {
      m_sgAppearance.setSpecularHighlightColor((Color)value);
    } else if (property == specularHighlightExponent) {
      m_sgAppearance.setSpecularHighlightExponent(((Number)value).doubleValue());
    } else if (property == emissiveColor) {
      m_sgAppearance.setEmissiveColor((Color)value);
    } else if (property == diffuseColorMap) {
      m_sgAppearance.setDiffuseColorMap(getSceneGraphTextureMap((TextureMap)value));
    } else if (property == opacityMap) {
      m_sgAppearance.setOpacityMap(getSceneGraphTextureMap((TextureMap)value));
    } else if (property == emissiveColorMap) {
      m_sgAppearance.setEmissiveColorMap(getSceneGraphTextureMap((TextureMap)value));
    } else if (property == specularHighlightColorMap) {
      m_sgAppearance.setSpecularHighlightColorMap(getSceneGraphTextureMap((TextureMap)value));
    } else if (property == bumpMap) {
      m_sgAppearance.setBumpMap(getSceneGraphTextureMap((TextureMap)value));
    } else if (property == detailMap) {
      m_sgAppearance.setDetailMap(getSceneGraphTextureMap((TextureMap)value));
    } else if (property != interactionMap)
    {
      if (property == visualScale) {
        m_sgVisual.setScale((Matrix3d)value);
      } else if (property == isShowing) {
        m_sgVisual.setIsShowing(((Boolean)value).booleanValue());
      } else if (property == disabledAffectors) {
        if (value != null) {
          Affector[] affectors = (Affector[])value;
          edu.cmu.cs.stage3.alice.scenegraph.Affector[] sgAffectors = new edu.cmu.cs.stage3.alice.scenegraph.Affector[affectors.length];
          for (int i = 0; i < sgAffectors.length; i++) {
            sgAffectors[i] = affectors[i].getSceneGraphAffector();
          }
          m_sgVisual.setDisabledAffectors(sgAffectors);
        } else {
          m_sgVisual.setDisabledAffectors(null);
        }
      } else if (property == geometry) {
        m_sgVisual.setGeometry(getSceneGraphGeometry((Geometry)value));
      } else
        super.propertyChanged(property, value);
    }
  }
  
  protected void scaleVisualRightNow(Vector3d scale, ReferenceFrame asSeenBy) {
    Matrix4d scaleMatrix = new Matrix4d(x, 0.0D, 0.0D, 0.0D, 0.0D, y, 0.0D, 0.0D, 0.0D, 0.0D, z, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
    Matrix44 m = calculateTransformation(scaleMatrix, asSeenBy);
    Matrix3d visScale = visualScale.getMatrix3dValue();
    
    visualScale.set(MathUtilities.multiply(visScale, m.getAxes()));
  }
  
  protected void updateBoundingSphere(Sphere sphere, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden)
  {
    super.updateBoundingSphere(sphere, asSeenBy, howMuch, ignoreHidden);
    if ((!ignoreHidden) || (isShowing.booleanValue()))
    {

      Sphere localSphere = m_sgVisual.getBoundingSphere();
      if (localSphere != null) {
        localSphere.transform(getTransformation(asSeenBy));
        sphere.union(localSphere);
      }
    }
  }
  
  protected void updateBoundingBox(Box box, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
    super.updateBoundingBox(box, asSeenBy, howMuch, ignoreHidden);
    if ((!ignoreHidden) || (isShowing.booleanValue()))
    {

      Box localBox = m_sgVisual.getBoundingBox();
      if (localBox != null) {
        localBox.transform(getTransformation(asSeenBy));
        box.union(localBox);
      }
    }
  }
  
  protected void HACK_copyOverTextureMapReferences(Element dst, Dictionary srcTextureMapToDstTextureMapMap)
  {
    super.HACK_copyOverTextureMapReferences(dst, srcTextureMapToDstTextureMapMap);
    if ((dst instanceof Model)) {
      TextureMap tm = diffuseColorMap.getTextureMapValue();
      if (tm != null) {
        tm = (TextureMap)srcTextureMapToDstTextureMapMap.get(tm);
      }
      diffuseColorMap.set(tm);
    }
  }
  
  public static double getDistanceBetween(Model a, Model b) {
    Visual[] aSGVisuals = a.getAllSceneGraphVisuals();
    Visual[] bSGVisuals = b.getAllSceneGraphVisuals();
    Sphere[] aSpheres = new Sphere[aSGVisuals.length];
    Sphere[] bSpheres = new Sphere[bSGVisuals.length];
    for (int i = 0; i < aSGVisuals.length; i++) {
      aSpheres[i] = aSGVisuals[i].getBoundingSphere();
      if (aSpheres[i] != null) {
        aSpheres[i].transform(aSGVisuals[i].getAbsoluteTransformation());
      }
    }
    for (int i = 0; i < bSGVisuals.length; i++) {
      bSpheres[i] = bSGVisuals[i].getBoundingSphere();
      if (bSpheres[i] != null) {
        bSpheres[i].transform(bSGVisuals[i].getAbsoluteTransformation());
      }
    }
    double dMin = Double.MAX_VALUE;
    for (int i = 0; i < aSpheres.length; i++) {
      if ((aSpheres[i] != null) && (aSpheres[i].getCenter() != null) && (aSpheres[i].getRadius() > 0.0D)) {
        for (int j = 0; j < bSpheres.length; j++) {
          if ((bSpheres[j] != null) && (bSpheres[j].getCenter() != null) && (bSpheres[j].getRadius() > 0.0D)) {
            double d = MathUtilities.subtract(aSpheres[i].getCenter(), bSpheres[j].getCenter()).lengthSquared();
            d = Math.sqrt(d);
            d -= aSpheres[i].getRadius();
            d -= bSpheres[j].getRadius();
            dMin = Math.min(dMin, d);
          }
        }
      }
    }
    return dMin;
  }
}

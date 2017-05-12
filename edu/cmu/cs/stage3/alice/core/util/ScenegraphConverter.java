package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.GeometryProperty;
import edu.cmu.cs.stage3.alice.core.property.ImageProperty;
import edu.cmu.cs.stage3.alice.core.property.IntArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TextureMapProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.Scene;
import edu.cmu.cs.stage3.alice.scenegraph.Transformable;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;













public class ScenegraphConverter
{
  public ScenegraphConverter() {}
  
  private static Component getFirstChildOfClass(Container sgContainer, Class cls)
  {
    for (int i = 0; i < sgContainer.getChildCount(); i++) {
      Component sgChild = sgContainer.getChildAt(i);
      if (cls.isAssignableFrom(sgChild.getClass())) {
        return sgChild;
      }
    }
    return null;
  }
  
  private static edu.cmu.cs.stage3.alice.scenegraph.Light getFirstLightChild(Container sgContainer) { return (edu.cmu.cs.stage3.alice.scenegraph.Light)getFirstChildOfClass(sgContainer, edu.cmu.cs.stage3.alice.scenegraph.Light.class); }
  
  private static edu.cmu.cs.stage3.alice.scenegraph.AmbientLight getFirstAmbientLightChild(Container sgContainer) {
    return (edu.cmu.cs.stage3.alice.scenegraph.AmbientLight)getFirstChildOfClass(sgContainer, edu.cmu.cs.stage3.alice.scenegraph.AmbientLight.class);
  }
  
  private static edu.cmu.cs.stage3.alice.scenegraph.Camera getFirstCameraChild(Container sgContainer) { return (edu.cmu.cs.stage3.alice.scenegraph.Camera)getFirstChildOfClass(sgContainer, edu.cmu.cs.stage3.alice.scenegraph.Camera.class); }
  
  private static Visual getFirstVisualChild(Container sgContainer) {
    return (Visual)getFirstChildOfClass(sgContainer, Visual.class);
  }
  
  private static Element internalConvert(Container sgContainer, int id) {
    Element element = null;
    if ((sgContainer instanceof Scene)) {
      Scene sgScene = (Scene)sgContainer;
      edu.cmu.cs.stage3.alice.scenegraph.AmbientLight sgAmbientLight = getFirstAmbientLightChild(sgScene);
      Background sgBackground = sgScene.getBackground();
      World world = new World();
      if (sgBackground != null) {
        atmosphereColor.set(sgBackground.getColor());
      }
      if (sgAmbientLight != null) {
        ambientLightColor.set(sgAmbientLight.getColor());
      }
      element = world;
    } else if ((sgContainer instanceof Transformable)) {
      Transformable sgTransformable = (Transformable)sgContainer;
      edu.cmu.cs.stage3.alice.scenegraph.Light sgLight = getFirstLightChild(sgTransformable);
      edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = getFirstCameraChild(sgTransformable);
      Visual sgVisual = getFirstVisualChild(sgTransformable);
      Model model = null;
      if (sgLight != null) {
        edu.cmu.cs.stage3.alice.core.Light light = null;
        if ((sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.AmbientLight)) {
          edu.cmu.cs.stage3.alice.core.light.AmbientLight ambientLight = new edu.cmu.cs.stage3.alice.core.light.AmbientLight();
          light = ambientLight;
        } else if ((sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.DirectionalLight)) {
          edu.cmu.cs.stage3.alice.core.light.DirectionalLight directionalLight = new edu.cmu.cs.stage3.alice.core.light.DirectionalLight();
          light = directionalLight;
        } else if ((sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.PointLight)) {
          edu.cmu.cs.stage3.alice.core.light.PointLight pointLight = null;
          if ((sgLight instanceof edu.cmu.cs.stage3.alice.scenegraph.SpotLight)) {
            edu.cmu.cs.stage3.alice.core.light.SpotLight spotLight = new edu.cmu.cs.stage3.alice.core.light.SpotLight();
            pointLight = spotLight;
          } else {
            pointLight = new edu.cmu.cs.stage3.alice.core.light.PointLight();
          }
          light = pointLight;
        }
        model = light;
        sgLight.setBonus(model);
      } else if (sgCamera != null) {
        edu.cmu.cs.stage3.alice.core.Camera camera = null;
        if ((sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera)) {
          edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera symmetricPerspectiveCamera = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
          camera = symmetricPerspectiveCamera;
        } else if ((sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera)) {
          edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera perspectiveCamera = new edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera();
          camera = perspectiveCamera;
        } else if ((sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera)) {
          edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthographicCamera = new edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera();
          camera = orthographicCamera;
        } else if ((sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera)) {
          edu.cmu.cs.stage3.alice.core.camera.ProjectionCamera projectionCamera = new edu.cmu.cs.stage3.alice.core.camera.ProjectionCamera();
          camera = projectionCamera;
        }
        model = camera;
        sgCamera.setBonus(model);
      } else {
        model = new Model();
      }
      if (sgVisual != null) {
        sgVisual.setBonus(model);
        Appearance sgAppearance = sgVisual.getFrontFacingAppearance();
        Geometry sgGeometry = sgVisual.getGeometry();
        if (sgAppearance != null) {
          edu.cmu.cs.stage3.alice.scenegraph.TextureMap sgTextureMap = sgAppearance.getDiffuseColorMap();
          if (sgTextureMap != null) {
            edu.cmu.cs.stage3.alice.core.TextureMap diffuseColorMap = new edu.cmu.cs.stage3.alice.core.TextureMap();
            diffuseColorMap.setParent(model);
            image.set(sgTextureMap.getImage());
            format.set(new Integer(sgTextureMap.getFormat()));
            diffuseColorMap.set(diffuseColorMap);
            textureMaps.add(diffuseColorMap);
          }
          color.set(sgAppearance.getDiffuseColor());
          opacity.set(new Double(sgAppearance.getOpacity()));
        }
        if ((sgGeometry instanceof edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray)) {
          edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray sgITA = (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray)sgGeometry;
          edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita = new edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray();
          ita.setParent(model);
          vertices.set(sgITA.getVertices());
          indices.set(sgITA.getIndices());
          geometry.set(ita);
          geometries.add(ita);
        }
      }
      model.setLocalTransformationRightNow(sgTransformable.getLocalTransformation());
      element = model;
    }
    for (int i = 0; i < sgContainer.getChildCount(); i++) {
      Component sgChild = sgContainer.getChildAt(i);
      if ((sgChild instanceof Transformable)) {
        Model child = (Model)internalConvert((Transformable)sgChild, i);
        child.setParent(element);
        vehicle.set(element);
        if (name.getStringValue() == null) {
          name.set("yet to be named part " + i);
        }
        if ((element instanceof World)) {
          sandboxes.add(child);
        } else {
          parts.add(child);
        }
      }
    }
    String name = sgContainer.getName();
    if (name != null) {
      int i = name.indexOf('.');
      if (i != -1) {
        name = name.substring(0, i);
      }
      name.set(name);
    }
    return element;
  }
  
  public static Element convert(Container sgContainer) { Element e = internalConvert(sgContainer, 0);
    if ((e instanceof Model)) {
      isFirstClass.set(Boolean.TRUE);
    }
    if (name.getStringValue() == null) {
      name.set("yet to be named");
    }
    return e;
  }
}

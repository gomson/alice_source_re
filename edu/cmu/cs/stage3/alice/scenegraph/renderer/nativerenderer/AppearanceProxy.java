package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.FillingStyle;
import edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;

public abstract class AppearanceProxy extends ElementProxy
{
  public AppearanceProxy() {}
  
  protected abstract void onAmbientColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected abstract void onDiffuseColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected abstract void onFillingStyleChange(int paramInt);
  
  protected abstract void onShadingStyleChange(int paramInt);
  
  protected abstract void onOpacityChange(double paramDouble);
  
  protected abstract void onSpecularHighlightColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected abstract void onSpecularHighlightExponentChange(double paramDouble);
  
  protected abstract void onEmissiveColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected abstract void onDiffuseColorMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected abstract void onOpacityMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected abstract void onEmissiveColorMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected abstract void onSpecularHighlightColorMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected abstract void onBumpMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected abstract void onDetailMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value)
  {
    if (property == Appearance.AMBIENT_COLOR_PROPERTY) {
      Color color = (Color)value;
      if (color == null) {
        color = ((Appearance)getSceneGraphElement()).getDiffuseColor();
      }
      if (color != null) {
        onAmbientColorChange(red, green, blue, alpha);
      } else {
        onAmbientColorChange(0.0D, 0.0D, 0.0D, 0.0D);
      }
    } else if (property == Appearance.DIFFUSE_COLOR_PROPERTY) {
      Color color = (Color)value;
      if (color != null) {
        onDiffuseColorChange(red, green, blue, alpha);
      } else {
        onDiffuseColorChange(0.0D, 0.0D, 0.0D, 0.0D);
      }
      if (((Appearance)getSceneGraphElement()).getAmbientColor() == null) {
        changed(Appearance.AMBIENT_COLOR_PROPERTY, color);
      }
    } else if (property == Appearance.FILLING_STYLE_PROPERTY) {
      int i;
      if (value.equals(FillingStyle.SOLID)) {
        i = 4; } else { int i;
        if (value.equals(FillingStyle.WIREFRAME)) {
          i = 2; } else { int i;
          if (value.equals(FillingStyle.POINTS)) {
            i = 1;
          } else
            throw new RuntimeException(); } }
      int i;
      onFillingStyleChange(i);
    } else if (property == Appearance.SHADING_STYLE_PROPERTY) {
      int i;
      if ((value == null) || (value.equals(ShadingStyle.NONE))) {
        i = 0; } else { int i;
        if (value.equals(ShadingStyle.FLAT)) {
          i = 1; } else { int i;
          if (value.equals(ShadingStyle.SMOOTH)) {
            i = 2;
          } else
            throw new RuntimeException(); } }
      int i;
      onShadingStyleChange(i);
    } else if (property == Appearance.OPACITY_PROPERTY) {
      onOpacityChange(((Double)value).doubleValue());
    } else if (property == Appearance.SPECULAR_HIGHLIGHT_COLOR_PROPERTY) {
      Color color = (Color)value;
      if (color != null) {
        onSpecularHighlightColorChange(red, green, blue, alpha);
      } else {
        onSpecularHighlightColorChange(0.0D, 0.0D, 0.0D, 0.0D);
      }
    } else if (property == Appearance.SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY) {
      onSpecularHighlightExponentChange(((Double)value).doubleValue());
    } else if (property == Appearance.EMISSIVE_COLOR_PROPERTY) {
      Color color = (Color)value;
      if (color != null) {
        onEmissiveColorChange(red, green, blue, alpha);
      } else {
        onEmissiveColorChange(0.0D, 0.0D, 0.0D, 0.0D);
      }
    } else if (property == Appearance.DIFFUSE_COLOR_MAP_PROPERTY) {
      onDiffuseColorMapChange((TextureMapProxy)getProxyFor((TextureMap)value));
    } else if (property == Appearance.OPACITY_MAP_PROPERTY) {
      onOpacityMapChange((TextureMapProxy)getProxyFor((TextureMap)value));
    } else if (property == Appearance.EMISSIVE_COLOR_MAP_PROPERTY) {
      onEmissiveColorMapChange((TextureMapProxy)getProxyFor((TextureMap)value));
    } else if (property == Appearance.SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY) {
      onSpecularHighlightColorMapChange((TextureMapProxy)getProxyFor((TextureMap)value));
    } else if (property == Appearance.BUMP_MAP_PROPERTY) {
      onBumpMapChange((TextureMapProxy)getProxyFor((TextureMap)value));
    } else if (property == Appearance.DETAIL_MAP_PROPERTY) {
      onDetailMapChange((TextureMapProxy)getProxyFor((TextureMap)value));
    } else {
      super.changed(property, value);
    }
  }
}

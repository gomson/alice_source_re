package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.lang.Messages;

























public class Appearance
  extends Element
{
  public static final Property AMBIENT_COLOR_PROPERTY = new Property(Appearance.class, "AMBIENT_COLOR");
  public static final Property DIFFUSE_COLOR_PROPERTY = new Property(Appearance.class, "DIFFUSE_COLOR");
  public static final Property FILLING_STYLE_PROPERTY = new Property(Appearance.class, "FILLING_STYLE");
  public static final Property SHADING_STYLE_PROPERTY = new Property(Appearance.class, "SHADING_STYLE");
  public static final Property OPACITY_PROPERTY = new Property(Appearance.class, "OPACITY");
  public static final Property SPECULAR_HIGHLIGHT_COLOR_PROPERTY = new Property(Appearance.class, "SPECULAR_HIGHLIGHT_COLOR");
  public static final Property SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY = new Property(Appearance.class, "SPECULAR_HIGHLIGHT_EXPONENT");
  public static final Property EMISSIVE_COLOR_PROPERTY = new Property(Appearance.class, "EMISSIVE_COLOR");
  public static final Property DIFFUSE_COLOR_MAP_PROPERTY = new Property(Appearance.class, "DIFFUSE_COLOR_MAP");
  public static final Property OPACITY_MAP_PROPERTY = new Property(Appearance.class, "OPACITY_MAP");
  public static final Property EMISSIVE_COLOR_MAP_PROPERTY = new Property(Appearance.class, "EMISSIVE_COLOR_MAP");
  public static final Property SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY = new Property(Appearance.class, "SPECULAR_HIGHLIGHT_COLOR_MAP");
  public static final Property BUMP_MAP_PROPERTY = new Property(Appearance.class, "BUMP_MAP");
  public static final Property DETAIL_MAP_PROPERTY = new Property(Appearance.class, "DETAIL_MAP");
  
  private Color m_ambientColor = null;
  private Color m_diffuseColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
  private FillingStyle m_fillingStyle = FillingStyle.SOLID;
  private ShadingStyle m_shadingStyle = ShadingStyle.SMOOTH;
  private double m_opacity = 1.0D;
  private Color m_specularHighlightColor = new Color(0.0F, 0.0F, 0.0F, 1.0F);
  private double m_specularHighlightExponent = 0.0D;
  private Color m_emissiveColor = new Color(0.0F, 0.0F, 0.0F, 1.0F);
  private TextureMap m_diffuseColorMap = null;
  private TextureMap m_opacityMap = null;
  private TextureMap m_emissiveColorMap = null;
  private TextureMap m_specularHighlightColorMap = null;
  private TextureMap m_bumpMap = null;
  private TextureMap m_detailMap = null;
  
  public Appearance() {}
  
  protected void releasePass1() { if (m_diffuseColorMap != null) {
      warnln(Messages.getString("WARNING__released_appearence_") + this + " " + Messages.getString("still_has_diffuse_color_map_") + m_diffuseColorMap + ".");
      setDiffuseColorMap(null);
    }
    if (m_opacityMap != null) {
      warnln(Messages.getString("WARNING__released_appearence_") + this + " " + Messages.getString("still_has_opacity_map_") + m_opacityMap + ".");
      setOpacityMap(null);
    }
    if (m_emissiveColorMap != null) {
      warnln(Messages.getString("WARNING__released_appearence_") + this + " " + Messages.getString("still_has_emissive_color_map_") + m_emissiveColorMap + ".");
      setEmissiveColorMap(null);
    }
    if (m_specularHighlightColorMap != null) {
      warnln(Messages.getString("WARNING__released_appearence_") + this + " " + Messages.getString("still_has_specular_highlight_color_map_") + m_specularHighlightColorMap + ".");
      setSpecularHighlightColorMap(null);
    }
    if (m_bumpMap != null) {
      warnln(Messages.getString("WARNING__released_appearence_") + this + " " + Messages.getString("still_has_bump_map_") + m_bumpMap + ".");
      setBumpMap(null);
    }
    if (m_detailMap != null) {
      warnln(Messages.getString("WARNING__released_appearence_") + this + " " + Messages.getString("still_has_detail_map_") + m_detailMap + ".");
      setDetailMap(null);
    }
    super.releasePass1();
  }
  

  public Color getAmbientColor() { return m_ambientColor; }
  
  public void setAmbientColor(Color ambientColor) {
    if (notequal(m_ambientColor, ambientColor)) {
      m_ambientColor = ambientColor;
      onPropertyChange(AMBIENT_COLOR_PROPERTY);
    }
  }
  
  public Color getDiffuseColor() { return m_diffuseColor; }
  
  public void setDiffuseColor(Color diffuseColor) {
    if (notequal(m_diffuseColor, diffuseColor)) {
      m_diffuseColor = diffuseColor;
      onPropertyChange(DIFFUSE_COLOR_PROPERTY);
    }
  }
  
  public double getOpacity() { return m_opacity; }
  
  public void setOpacity(double opacity) {
    if (m_opacity != opacity) {
      m_opacity = opacity;
      onPropertyChange(OPACITY_PROPERTY);
    }
  }
  

  public double getSpecularHighlightExponent() { return m_specularHighlightExponent; }
  
  public void setSpecularHighlightExponent(double specularHighlightExponent) {
    if (m_specularHighlightExponent != specularHighlightExponent) {
      m_specularHighlightExponent = specularHighlightExponent;
      onPropertyChange(SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY);
    }
  }
  

  public Color getSpecularHighlightColor() { return m_specularHighlightColor; }
  
  public void setSpecularHighlightColor(Color specularHighlightColor) {
    if (notequal(m_specularHighlightColor, specularHighlightColor)) {
      m_specularHighlightColor = specularHighlightColor;
      onPropertyChange(SPECULAR_HIGHLIGHT_COLOR_PROPERTY);
    }
  }
  

  public Color getEmissiveColor() { return m_emissiveColor; }
  
  public void setEmissiveColor(Color emissiveColor) {
    if (notequal(m_emissiveColor, emissiveColor)) {
      m_emissiveColor = emissiveColor;
      onPropertyChange(EMISSIVE_COLOR_PROPERTY);
    }
  }
  

  public FillingStyle getFillingStyle() { return m_fillingStyle; }
  
  public void setFillingStyle(FillingStyle fillingStyle) {
    if (m_fillingStyle != fillingStyle) {
      m_fillingStyle = fillingStyle;
      onPropertyChange(FILLING_STYLE_PROPERTY);
    }
  }
  
  public ShadingStyle getShadingStyle() { return m_shadingStyle; }
  
  public void setShadingStyle(ShadingStyle shadingStyle) {
    if (m_shadingStyle != shadingStyle) {
      m_shadingStyle = shadingStyle;
      onPropertyChange(SHADING_STYLE_PROPERTY);
    }
  }
  

  public TextureMap getDiffuseColorMap() { return m_diffuseColorMap; }
  
  public void setDiffuseColorMap(TextureMap diffuseColorMap) {
    if (notequal(m_diffuseColorMap, diffuseColorMap)) {
      m_diffuseColorMap = diffuseColorMap;
      onPropertyChange(DIFFUSE_COLOR_MAP_PROPERTY);
    }
  }
  
  public TextureMap getOpacityMap() { return m_opacityMap; }
  
  public void setOpacityMap(TextureMap opacityMap) {
    if (notequal(m_opacityMap, opacityMap)) {
      m_opacityMap = opacityMap;
      onPropertyChange(OPACITY_MAP_PROPERTY);
    }
  }
  
  public TextureMap getEmissiveColorMap() { return m_emissiveColorMap; }
  
  public void setEmissiveColorMap(TextureMap emissiveColorMap) {
    if (notequal(m_emissiveColorMap, emissiveColorMap)) {
      m_emissiveColorMap = emissiveColorMap;
      onPropertyChange(EMISSIVE_COLOR_MAP_PROPERTY);
    }
  }
  
  public TextureMap getSpecularHighlightColorMap() { return m_emissiveColorMap; }
  
  public void setSpecularHighlightColorMap(TextureMap specularHighlightColorMap) {
    if (notequal(m_specularHighlightColorMap, specularHighlightColorMap)) {
      m_specularHighlightColorMap = specularHighlightColorMap;
      onPropertyChange(SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY);
    }
  }
  
  public TextureMap getBumpMap() { return m_bumpMap; }
  
  public void setBumpMap(TextureMap bumpMap) {
    if (notequal(m_bumpMap, bumpMap)) {
      m_bumpMap = bumpMap;
      onPropertyChange(BUMP_MAP_PROPERTY);
    }
  }
  
  public TextureMap getDetailMap() { return m_detailMap; }
  
  public void setDetailMap(TextureMap detailMap) {
    if (notequal(m_detailMap, detailMap)) {
      m_detailMap = detailMap;
      onPropertyChange(DETAIL_MAP_PROPERTY);
    }
  }
}

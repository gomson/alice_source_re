package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.criterion.ElementWithPropertyNameValueCriterion;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.math.Interpolator;
import java.util.Vector;


public class AbstractFadeAnimation
  extends Animation
{
  public final ReferenceFrameProperty subject = new ReferenceFrameProperty(this, "subject", null);
  
  protected static java.awt.Color atmosphereColor = new java.awt.Color(72, 72, 72);
  protected static java.awt.Color ambientLightColor = new java.awt.Color(74, 125, 204);
  protected static boolean currentlyBlack = false;
  

  protected static Vector properties = new Vector();
  protected static Vector origPropertyValues = new Vector();
  

  protected static Vector specularProperties = new Vector();
  protected static Vector origSpecularValues = new Vector();
  

  protected static Vector lightProperties = new Vector();
  protected static Vector origLightValues = new Vector();
  
  public abstract class RuntimeAbstractFadeAnimation extends Animation.RuntimeAnimation { public RuntimeAbstractFadeAnimation() { super(); }
    
    protected ColorProperty m_atmosphereColorProp = null;
    protected ColorProperty m_ambientLightColorProp = null;
    

    protected Vector m_beginPropColors = new Vector();
    protected Vector m_endPropColors = new Vector();
    

    protected Vector m_beginSpecularColors = new Vector();
    protected Vector m_endSpecularColors = new Vector();
    

    protected Vector m_beginPropBrightness = new Vector();
    protected Vector m_endPropBrightness = new Vector();
    
    protected java.awt.Color m_beginAtmosphereColor = null;
    protected java.awt.Color m_endAtmosphereColor = null;
    
    protected java.awt.Color m_beginAmbientLightColor = null;
    protected java.awt.Color m_endAmbientLightColor = null;
    
    protected double m_beginBrightness = -1.0D;
    protected double m_endBrightness = -1.0D;
    

    protected abstract boolean endsBlack();
    

    public void prologue(double t)
    {
      super.prologue(t);
      

      World world = getWorld();
      m_atmosphereColorProp = ((ColorProperty)world.getPropertyNamed("atmosphereColor"));
      m_ambientLightColorProp = ((ColorProperty)world.getPropertyNamed("ambientLightColor"));
      

      if (!AbstractFadeAnimation.currentlyBlack) {
        AbstractFadeAnimation.atmosphereColor = m_atmosphereColorProp.getColorValue().createAWTColor();
        AbstractFadeAnimation.ambientLightColor = m_ambientLightColorProp.getColorValue().createAWTColor();
        

        AbstractFadeAnimation.properties.clear();
        AbstractFadeAnimation.origPropertyValues.clear();
        Element[] els = world.search(new ElementWithPropertyNameValueCriterion("emissiveColor", new edu.cmu.cs.stage3.alice.scenegraph.Color(0.0F, 0.0F, 0.0F), false));
        
        for (int i = 0; i < els.length; i++) {
          String key = els[i].getKey();
          ColorProperty emissColorProp = (ColorProperty)els[i].getPropertyNamed("emissiveColor");
          
          AbstractFadeAnimation.properties.addElement(emissColorProp);
          AbstractFadeAnimation.origPropertyValues.addElement(emissColorProp.getColorValue().createAWTColor());
        }
        


        AbstractFadeAnimation.specularProperties.clear();
        AbstractFadeAnimation.origSpecularValues.clear();
        els = world.search(new ElementWithPropertyNameValueCriterion("specularHighlightColor", new edu.cmu.cs.stage3.alice.scenegraph.Color(0.0F, 0.0F, 0.0F), false));
        
        for (int i = 0; i < els.length; i++) {
          String key = els[i].getKey();
          ColorProperty emissColorProp = (ColorProperty)els[i].getPropertyNamed("specularHighlightColor");
          
          AbstractFadeAnimation.properties.addElement(emissColorProp);
          AbstractFadeAnimation.origPropertyValues.addElement(emissColorProp.getColorValue().createAWTColor());
        }
        


        AbstractFadeAnimation.lightProperties.clear();
        AbstractFadeAnimation.origLightValues.clear();
        els = world.search(new ElementWithPropertyNameValueCriterion("brightness", new Double(0.0D), false));
        
        for (int i = 0; i < els.length; i++) {
          NumberProperty brightnessProp = (NumberProperty)els[i].getPropertyNamed("brightness");
          AbstractFadeAnimation.lightProperties.addElement(brightnessProp);
          AbstractFadeAnimation.origLightValues.addElement(brightnessProp.getNumberValue());
        }
      }
      
      m_beginPropColors.clear();
      m_endPropColors.clear();
      
      m_beginSpecularColors.clear();
      m_endSpecularColors.clear();
      
      m_beginPropBrightness.clear();
      m_endPropBrightness.clear();
      

      if (endsBlack()) {
        m_beginAtmosphereColor = m_atmosphereColorProp.getColorValue().createAWTColor();
        m_endAtmosphereColor = new java.awt.Color(0, 0, 0);
        
        m_beginAmbientLightColor = m_atmosphereColorProp.getColorValue().createAWTColor();
        m_endAmbientLightColor = new java.awt.Color(0, 0, 0);
        

        for (int i = 0; i < AbstractFadeAnimation.properties.size(); i++) {
          ColorProperty colorProp = (ColorProperty)AbstractFadeAnimation.properties.elementAt(i);
          m_beginPropColors.addElement(colorProp.getColorValue().createAWTColor());
          m_endPropColors.addElement(new java.awt.Color(0, 0, 0));
        }
        

        for (int i = 0; i < AbstractFadeAnimation.specularProperties.size(); i++) {
          ColorProperty colorProp = (ColorProperty)AbstractFadeAnimation.specularProperties.elementAt(i);
          m_beginSpecularColors.addElement(colorProp.getColorValue().createAWTColor());
          m_endSpecularColors.addElement(new java.awt.Color(0, 0, 0));
        }
        

        for (int i = 0; i < AbstractFadeAnimation.lightProperties.size(); i++) {
          NumberProperty numberProp = (NumberProperty)AbstractFadeAnimation.lightProperties.elementAt(i);
          m_beginPropBrightness.addElement(numberProp.getNumberValue());
          m_endPropBrightness.addElement(new Double(0.0D));
        }
      } else {
        m_beginAtmosphereColor = m_atmosphereColorProp.getColorValue().createAWTColor();
        m_endAtmosphereColor = AbstractFadeAnimation.atmosphereColor;
        
        m_beginAmbientLightColor = m_ambientLightColorProp.getColorValue().createAWTColor();
        m_endAmbientLightColor = AbstractFadeAnimation.ambientLightColor;
        

        for (int i = 0; i < AbstractFadeAnimation.properties.size(); i++) {
          ColorProperty colorProp = (ColorProperty)AbstractFadeAnimation.properties.elementAt(i);
          m_beginPropColors.addElement(colorProp.getColorValue().createAWTColor());
          m_endPropColors.addElement(AbstractFadeAnimation.origPropertyValues.elementAt(i));
        }
        
        for (int i = 0; i < AbstractFadeAnimation.specularProperties.size(); i++) {
          ColorProperty colorProp = (ColorProperty)AbstractFadeAnimation.specularProperties.elementAt(i);
          m_beginSpecularColors.addElement(colorProp.getColorValue().createAWTColor());
          m_endSpecularColors.addElement(AbstractFadeAnimation.origSpecularValues.elementAt(i));
        }
        
        for (int i = 0; i < AbstractFadeAnimation.lightProperties.size(); i++) {
          NumberProperty numberProp = (NumberProperty)AbstractFadeAnimation.lightProperties.elementAt(i);
          m_beginPropBrightness.addElement(numberProp.getNumberValue());
          m_endPropBrightness.addElement(AbstractFadeAnimation.origLightValues.elementAt(i));
        }
      }
    }
    

    public void update(double t)
    {
      super.update(t);
      Object value;
      Object value; if ((m_beginAtmosphereColor != null) && (m_endAtmosphereColor != null)) {
        value = Interpolator.interpolate(m_beginAtmosphereColor, m_endAtmosphereColor, getPortion(t));
      } else {
        value = m_endAtmosphereColor;
      }
      m_atmosphereColorProp.set(new edu.cmu.cs.stage3.alice.scenegraph.Color((java.awt.Color)value));
      
      if ((m_beginAmbientLightColor != null) && (m_endAmbientLightColor != null)) {
        value = Interpolator.interpolate(m_beginAmbientLightColor, m_endAmbientLightColor, getPortion(t));
      } else {
        value = m_endAmbientLightColor;
      }
      m_ambientLightColorProp.set(new edu.cmu.cs.stage3.alice.scenegraph.Color((java.awt.Color)value));
      

      for (int i = 0; i < AbstractFadeAnimation.properties.size(); i++) {
        ColorProperty colorProp = (ColorProperty)AbstractFadeAnimation.properties.elementAt(i);
        java.awt.Color beginColor = (java.awt.Color)m_beginPropColors.elementAt(i);
        java.awt.Color endColor = (java.awt.Color)m_endPropColors.elementAt(i);
        
        if ((beginColor != null) && (endColor != null)) {
          value = Interpolator.interpolate(beginColor, endColor, getPortion(t));
        } else {
          value = endColor;
        }
        
        colorProp.set(new edu.cmu.cs.stage3.alice.scenegraph.Color((java.awt.Color)value));
      }
      

      for (int i = 0; i < AbstractFadeAnimation.specularProperties.size(); i++) {
        ColorProperty colorProp = (ColorProperty)AbstractFadeAnimation.specularProperties.elementAt(i);
        java.awt.Color beginColor = (java.awt.Color)m_beginSpecularColors.elementAt(i);
        java.awt.Color endColor = (java.awt.Color)m_endSpecularColors.elementAt(i);
        
        if ((beginColor != null) && (endColor != null)) {
          value = Interpolator.interpolate(beginColor, endColor, getPortion(t));
        } else {
          value = endColor;
        }
        
        colorProp.set(new edu.cmu.cs.stage3.alice.scenegraph.Color((java.awt.Color)value));
      }
      

      for (int i = 0; i < AbstractFadeAnimation.lightProperties.size(); i++) {
        NumberProperty numberProp = (NumberProperty)AbstractFadeAnimation.lightProperties.elementAt(i);
        Double beginBrightness = (Double)m_beginPropBrightness.elementAt(i);
        Double endBrightness = (Double)m_endPropBrightness.elementAt(i);
        
        if ((beginBrightness.doubleValue() != -1.0D) && (endBrightness.doubleValue() != -1.0D)) {
          value = Interpolator.interpolate(beginBrightness, endBrightness, getPortion(t));
        } else {
          value = endBrightness;
        }
        
        numberProp.set(value);
      }
    }
    
    public void epilogue(double t)
    {
      super.epilogue(t);
      AbstractFadeAnimation.currentlyBlack = endsBlack();
    }
  }
  
  public AbstractFadeAnimation() {}
}

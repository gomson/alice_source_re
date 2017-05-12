package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.util.HowMuch;


















public class TextureMapMovie
  extends Response
{
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public final ElementArrayProperty textureMaps = new ElementArrayProperty(this, "textureMaps", null, [Ledu.cmu.cs.stage3.alice.core.TextureMap.class);
  public final NumberProperty framesPerSecond = new NumberProperty(this, "framesPerSecond", new Integer(24));
  public final BooleanProperty setDiffuseColorMap = new BooleanProperty(this, "setDiffuseColorMap", Boolean.TRUE);
  public final BooleanProperty setOpacityMap = new BooleanProperty(this, "setOpacityMap", Boolean.FALSE);
  public final ObjectProperty howMuch = new ObjectProperty(this, "howMuch", HowMuch.INSTANCE_AND_PARTS, HowMuch.class);
  public class TextureMapMovieResponse extends Response.RuntimeResponse { public TextureMapMovieResponse() { super(); }
    
    private Transformable m_transformable;
    private TextureMap[] m_textureMaps;
    private double m_framesPerSecond;
    private boolean m_setDiffuseColorMap;
    private boolean m_setOpacityMap;
    private HowMuch m_howMuch;
    public void prologue(double t) {
      super.prologue(t);
      m_transformable = subject.getTransformableValue();
      m_textureMaps = ((TextureMap[])textureMaps.getValue());
      m_framesPerSecond = framesPerSecond.doubleValue();
      m_setDiffuseColorMap = setDiffuseColorMap.booleanValue();
      m_setOpacityMap = setOpacityMap.booleanValue();
      m_howMuch = ((HowMuch)howMuch.getValue());
    }
    
    public void update(double t) {
      super.update(t);
      if (m_textureMaps.length > 0) {
        int index = (int)(getTimeElapsed(t) * m_framesPerSecond);
        index %= m_textureMaps.length;
        TextureMap textureMap = m_textureMaps[index];
        if (m_setDiffuseColorMap) {
          m_transformable.setPropertyNamed("diffuseColorMap", textureMap, m_howMuch);
        }
        if (m_setOpacityMap) {
          m_transformable.setPropertyNamed("opacityMap", textureMap, m_howMuch);
        }
      }
    }
  }
  
  public TextureMapMovie() {}
}

package edu.cmu.cs.stage3.alice.core.style;

import edu.cmu.cs.stage3.alice.core.Style;
import edu.cmu.cs.stage3.util.Enumerable;
















public class TraditionalAnimationStyle
  extends Enumerable
  implements Style
{
  /**
   * @deprecated
   */
  public static final TraditionalAnimationStyle LINEAR = new TraditionalAnimationStyle(false, false, false, false); /**
   * @deprecated
   */
  public static final TraditionalAnimationStyle SLOW_IN = new TraditionalAnimationStyle(true, false, false, false); /**
   * @deprecated
   */
  public static final TraditionalAnimationStyle SLOW_OUT = new TraditionalAnimationStyle(false, true, false, false); /**
   * @deprecated
   */
  public static final TraditionalAnimationStyle SLOW_IN_OUT = new TraditionalAnimationStyle(true, true, false, false);
  
  public static final TraditionalAnimationStyle BEGIN_AND_END_GENTLY = new TraditionalAnimationStyle(true, true, false, false);
  public static final TraditionalAnimationStyle BEGIN_GENTLY_AND_END_ABRUPTLY = new TraditionalAnimationStyle(true, false, false, false);
  public static final TraditionalAnimationStyle BEGIN_ABRUPTLY_AND_END_GENTLY = new TraditionalAnimationStyle(false, true, false, false);
  public static final TraditionalAnimationStyle BEGIN_AND_END_ABRUPTLY = new TraditionalAnimationStyle(false, false, false, false);
  private boolean m_beginGently;
  private boolean m_endGently;
  private boolean m_withAnticipation;
  private boolean m_withOvershoot;
  
  public TraditionalAnimationStyle(boolean beginGently, boolean endGently, boolean withAnticipation, boolean withOvershoot)
  {
    m_beginGently = beginGently;
    m_endGently = endGently;
    m_withAnticipation = withAnticipation;
    m_withOvershoot = withOvershoot;
  }
  
  private static double gently(double x, double A, double B) {
    double y;
    double y;
    if (x < A) {
      y = (B - 1.0D) / (A * (B * B - A * B + A - 1.0D)) * x * x; } else { double y;
      if (x > B) {
        double a3 = 1.0D / (B * B - A * B + A - 1.0D);
        double b3 = -2.0D * a3;
        double c3 = 1.0D + a3;
        y = a3 * x * x + b3 * x + c3;
      } else {
        double m = 2.0D * (B - 1.0D) / (B * B - A * B + A - 1.0D);
        double b2 = -m * A / 2.0D;
        y = m * x + b2;
      } }
    return y;
  }
  
  public double getPortion(double current, double total)
  {
    if (total != 0.0D) {
      double portion = current / total;
      if (m_beginGently) {
        if (m_endGently) {
          return gently(portion, 0.3D, 0.8D);
        }
        return gently(portion, 0.99D, 0.999D);
      }
      
      if (m_endGently) {
        return gently(portion, 0.001D, 0.01D);
      }
      return portion;
    }
    


    return 1.0D;
  }
  
  public static TraditionalAnimationStyle valueOf(String s)
  {
    TraditionalAnimationStyle tas = (TraditionalAnimationStyle)Enumerable.valueOf(s, TraditionalAnimationStyle.class);
    if (tas == LINEAR) {
      tas = BEGIN_AND_END_ABRUPTLY;
    } else if (tas == SLOW_IN)
    {
      tas = BEGIN_GENTLY_AND_END_ABRUPTLY;
    } else if (tas == SLOW_OUT)
    {
      tas = BEGIN_ABRUPTLY_AND_END_GENTLY;
    } else if (tas == SLOW_IN_OUT) {
      tas = BEGIN_AND_END_GENTLY;
    }
    return tas;
  }
}

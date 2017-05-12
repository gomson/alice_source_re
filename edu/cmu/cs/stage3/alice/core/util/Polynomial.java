package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.math.BezierCubic;
import edu.cmu.cs.stage3.math.BezierQuadratic;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;














public class Polynomial
{
  public Polynomial() {}
  
  public static void evaluatePolynomial(edu.cmu.cs.stage3.math.Polynomial xPolynomial, edu.cmu.cs.stage3.math.Polynomial yPolynomial, double z, Point3d[] positions, Vector3d[] normals)
  {
    if (positions.length != normals.length) {
      throw new RuntimeException();
    }
    double dt = 1.0D / (positions.length - 1);
    double t = 0.0D;
    for (int i = 0; i < positions.length; i++) {
      double xt = xPolynomial.evaluate(t);
      double yt = yPolynomial.evaluate(t);
      
      if (positions[i] == null) {
        positions[i] = new Point3d();
      }
      x = xt;
      y = yt;
      z = z;
      
      double dxdt = xPolynomial.evaluateDerivative(t);
      double dydt = yPolynomial.evaluateDerivative(t);
      if (normals[i] == null) {
        normals[i] = new Vector3d();
      }
      x = (-dydt);
      y = dxdt;
      z = 0.0D;
      
      t += dt;
    }
  }
  
  public static void evaluateBezierQuadratic(Point2d p0, Point2d p1, Point2d p2, double z, Point3d[] positions, Vector3d[] normals) {
    evaluatePolynomial(new BezierQuadratic(x, x, x), new BezierQuadratic(y, y, y), z, positions, normals);
  }
  
  public static void evaluateBezierCubic(Point2d p0, Point2d p1, Point2d p2, Point2d p3, double z, Point3d[] positions, Vector3d[] normals) { evaluatePolynomial(new BezierCubic(x, x, x, x), new BezierCubic(y, y, y, y), z, positions, normals); }
}

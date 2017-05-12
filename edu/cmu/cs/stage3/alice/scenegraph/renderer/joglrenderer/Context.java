package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import com.sun.opengl.util.GLUT;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
















abstract class Context
  implements GLEventListener
{
  public GL gl;
  public GLU glu;
  public GLUT glut;
  protected int m_width;
  protected int m_height;
  
  Context() {}
  
  public void init(GLAutoDrawable drawable) {}
  
  public void display(GLAutoDrawable drawable)
  {
    gl = drawable.getGL();
    glu = new GLU();
    glut = new GLUT();
  }
  
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    m_width = width;
    m_height = height;
  }
  
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
  
  public int getWidth()
  {
    return m_width;
  }
  
  public int getHeight() { return m_height; }
}

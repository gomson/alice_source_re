package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import javax.media.opengl.GL;




















class LineArrayProxy
  extends VertexGeometryProxy
{
  LineArrayProxy() {}
  
  public void render(RenderContext context)
  {
    Integer id = context.getDisplayListID(this);
    if (id == null) {
      id = context.generateDisplayListID(this);
      setIsGeometryChanged(true);
    }
    if (isGeometryChanged()) {
      gl.glNewList(id.intValue(), 4865);
      gl.glBegin(1);
      for (int i = 0; i < getNumVertices(); i += 2) {
        context.renderVertex(getVertexAt(i));
        context.renderVertex(getVertexAt(i + 1));
      }
      gl.glEnd();
      gl.glEndList();
      setIsGeometryChanged(false);
    } else {
      gl.glCallList(id.intValue());
    }
  }
  
  public void pick(PickContext context, boolean isSubElementRequired) {}
}

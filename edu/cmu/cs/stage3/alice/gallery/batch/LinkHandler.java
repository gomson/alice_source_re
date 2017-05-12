package edu.cmu.cs.stage3.alice.gallery.batch;

import java.io.File;

public abstract interface LinkHandler
{
  public abstract void handleLink(File paramFile, String paramString);
}

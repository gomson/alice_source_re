package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.util.Enumerable;


















public class MultipleRuntimeResponsePolicy
  extends Enumerable
{
  public MultipleRuntimeResponsePolicy() {}
  
  public static final MultipleRuntimeResponsePolicy IGNORE_MULTIPLE = new MultipleRuntimeResponsePolicy();
  public static final MultipleRuntimeResponsePolicy ENQUEUE_MULTIPLE = new MultipleRuntimeResponsePolicy();
  public static final MultipleRuntimeResponsePolicy INTERLEAVE_MULTIPLE = new MultipleRuntimeResponsePolicy();
  
  public static MultipleRuntimeResponsePolicy valueOf(String s) { return (MultipleRuntimeResponsePolicy)Enumerable.valueOf(s, MultipleRuntimeResponsePolicy.class); }
}

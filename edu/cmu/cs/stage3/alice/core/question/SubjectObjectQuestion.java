package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.lang.Messages;



















public abstract class SubjectObjectQuestion
  extends SubjectQuestion
{
  public SubjectObjectQuestion() {}
  
  public final TransformableProperty object = new TransformableProperty(this, "object", null);
  
  protected abstract Object getValue(Transformable paramTransformable1, Transformable paramTransformable2);
  
  protected Object getValue(Transformable subjectValue) { Transformable objectValue = object.getTransformableValue();
    if (objectValue == null) {
      objectValue = subjectValue;
    }
    
    if ((objectValue == null) || (subjectValue == null)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "";
    } else if ((this instanceof DistanceTo)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("distance_to_") + name.getStringValue() + " " + Messages.getString("_is_");
    } else if ((this instanceof DistanceToTheLeftOf)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("distance_to_the_left_of_") + name.getStringValue() + " " + Messages.getString("_is_");
    } else if ((this instanceof DistanceToTheRightOf)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("distance_to_the_right_of_") + name.getStringValue() + " " + Messages.getString("_is_");
    } else if ((this instanceof DistanceAbove)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("distance_above_") + name.getStringValue() + " " + Messages.getString("_is_");
    } else if ((this instanceof DistanceBelow)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("distance_below_") + name.getStringValue() + " " + Messages.getString("_is_");
    } else if ((this instanceof DistanceInFrontOf)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("distance_in_front_of_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof DistanceBehind)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("distance_behind_") + name.getStringValue() + " " + Messages.getString("is_");
    }
    else if ((this instanceof IsSmallerThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_smaller_than_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsLargerThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_larger_than_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsNarrowerThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_narrower_than_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsWiderThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_wider_than_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsShorterThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_shorter_than_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsTallerThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_taller_than_") + name.getStringValue() + " " + Messages.getString("is_");
    }
    else if ((this instanceof IsLeftOf)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_to_the_left_of_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsRightOf)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_to_the_right_of_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsAbove)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_above_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsBelow)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_below_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsInFrontOf)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_in_front_of_") + name.getStringValue() + " " + Messages.getString("is_");
    } else if ((this instanceof IsBehind)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " " + Messages.getString("is_behind_") + name.getStringValue() + " " + Messages.getString("is_");
    } else
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        name.getStringValue() + " __Unknown__ " + name.getStringValue() + " " + Messages.getString("is_");
    return getValue(subjectValue, objectValue);
  }
}

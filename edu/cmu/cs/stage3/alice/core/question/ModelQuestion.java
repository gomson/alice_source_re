package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;

public abstract class ModelQuestion extends Question { public ModelQuestion() {}
  public Class getValueClass() { return edu.cmu.cs.stage3.alice.core.Model.class; }
}

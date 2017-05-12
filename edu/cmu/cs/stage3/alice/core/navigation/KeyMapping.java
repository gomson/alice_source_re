package edu.cmu.cs.stage3.alice.core.navigation;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.IntArrayProperty;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;






























public class KeyMapping
  extends Element
  implements KeyListener
{
  public static final int NAV_DONOTHING = 0;
  public static final int NAV_MOVEFORWARD = 1;
  public static final int NAV_MOVEBACKWARD = 2;
  public static final int NAV_MOVELEFT = 4;
  public static final int NAV_MOVERIGHT = 8;
  public static final int NAV_MOVEUP = 16;
  public static final int NAV_MOVEDOWN = 32;
  public static final int NAV_TURNLEFT = 64;
  public static final int NAV_TURNRIGHT = 128;
  public static final int NAV_TURNUP = 256;
  public static final int NAV_TURNDOWN = 512;
  public static final int NAV_ROLLLEFT = 1024;
  public static final int NAV_ROLLRIGHT = 2048;
  public static final int NAV_HEADSUP = 65536;
  public static final int NAV_STRAFE_MODIFIER = -1;
  public IntArrayProperty keyFunction = new IntArrayProperty(this, "keyFunction", new int['ƒ']);
  
  private boolean[] keyState;
  private boolean strafing;
  
  public KeyMapping()
  {
    keyState = new boolean['ƒ'];
    

    setFunction(38, 1);
    setFunction(40, 2);
    
    setFunction(37, 64);
    setFunction(39, 128);
  }
  





  public void setFunction(int key, int function)
  {
    int[] functions = keyFunction.getIntArrayValue();
    functions[key] = function;
    keyFunction.set(functions);
  }
  
  public int getActions() {
    int actions = 0;
    for (int i = 0; i < keyState.length; i++)
      if (keyState[i] != 0) {
        int val = keyFunction.getIntArrayValue()[i];
        if ((strafing) && (val == 64))
          val = 4;
        if ((strafing) && (val == 128))
          val = 8;
        if ((strafing) && (val == 256))
          val = 32;
        if ((strafing) && (val == 512))
          val = 16;
        actions += val;
      }
    return actions;
  }
  
  public void keyPressed(KeyEvent keyEvent) {
    if (keyFunction.getIntArrayValue()[keyEvent.getKeyCode()] == -1) {
      strafing = true;
    } else
      keyState[keyEvent.getKeyCode()] = true;
  }
  
  public void keyReleased(KeyEvent keyEvent) {
    if (keyFunction.getIntArrayValue()[keyEvent.getKeyCode()] == -1)
      strafing = false;
    keyState[keyEvent.getKeyCode()] = false;
  }
  
  public void keyTyped(KeyEvent keyEvent) {}
  
  public void cleanState() {
    keyState = new boolean['ƒ'];
    strafing = false;
  }
  
  public void printInstructions() {
    System.out.println("Keyboard Navigation Controls:");
    System.out.println("-----------------------------");
    
    for (int i = 0; i < keyFunction.getIntArrayValue().length; i++) {
      if (keyFunction.getIntArrayValue()[i] == 1) {
        System.out.print("Move Forward: ");
      } else if (keyFunction.getIntArrayValue()[i] == 2) {
        System.out.print("Move Backward: ");
      } else if (keyFunction.getIntArrayValue()[i] == 4) {
        System.out.print("Strafe Left: ");
      } else if (keyFunction.getIntArrayValue()[i] == 8) {
        System.out.print("Strafe Right: ");
      } else if (keyFunction.getIntArrayValue()[i] == 64) {
        System.out.print("Turn Left: ");
      } else if (keyFunction.getIntArrayValue()[i] == 128) {
        System.out.print("Turn Right: ");
      } else if (keyFunction.getIntArrayValue()[i] == 256) {
        System.out.print("Tilt Up: ");
      } else if (keyFunction.getIntArrayValue()[i] == 512) {
        System.out.print("Tilt Down: ");







      }
      else if (keyFunction.getIntArrayValue()[i] == -1) {
        System.out.print("Strafe Modifier: ");
      }
      if (keyFunction.getIntArrayValue()[i] != 0) {
        System.out.println(KeyEvent.getKeyText(i));
      }
    }
  }
}

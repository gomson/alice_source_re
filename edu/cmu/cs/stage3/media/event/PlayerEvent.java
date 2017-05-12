package edu.cmu.cs.stage3.media.event;

import edu.cmu.cs.stage3.media.Player;

public class PlayerEvent extends java.util.EventObject { public PlayerEvent(Player source) { super(source); }
  
  public Player getPlayer() {
    return (Player)getSource();
  }
}

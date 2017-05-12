package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
























public class KeepFileDoesNotExistException
  extends Exception
{
  public KeepFileDoesNotExistException(String pathname, String filename)
  {
    super("'" + filename + " " + Messages.getString("in_directory__") + pathname + " " + Messages.getString("cannot_be_retained__because_it_does_not_exist_in_this_store_"));
  }
}

package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
























public class KeepFileNotSupportedException
  extends Exception
{
  public KeepFileNotSupportedException()
  {
    super(Messages.getString("keepFile___is_not_supported_for_this_DirectoryTreeStorer"));
  }
}

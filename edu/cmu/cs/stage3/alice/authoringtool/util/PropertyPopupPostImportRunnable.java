package edu.cmu.cs.stage3.alice.authoringtool.util;













public class PropertyPopupPostImportRunnable
  extends PostImportRunnable
{
  protected PopupItemFactory m_factory;
  











  public PropertyPopupPostImportRunnable(PopupItemFactory factory)
  {
    m_factory = factory;
  }
  
  public void run() { ((Runnable)m_factory.createItem(getImportedElement())).run(); }
}

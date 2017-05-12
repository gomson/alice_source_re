package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;























public class DirectoryBarButton
  extends JButton
{
  GalleryViewer.DirectoryStructure directoryData;
  GalleryViewer mainViewer;
  
  public DirectoryBarButton(GalleryViewer.DirectoryStructure dirData, GalleryViewer viewer)
  {
    setCursor(new Cursor(12));
    if (dirData == null) {
      super.setText(Messages.getString("Home"));
    }
    else {
      super.setText(name);
    }
    setBorder(null);
    setOpaque(false);
    setForeground(GalleryViewer.linkColor);
    directoryData = dirData;
    mainViewer = viewer;
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (mainViewer != null) {
          mainViewer.changeDirectory(directoryData);
        }
      }
    });
  }
}

package edu.cmu.cs.stage3.alice.player;

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.vecmath.Matrix4d;

public class PlayerApplet extends Applet
{
  private AbstractPlayer m_player = new AbstractPlayer()
  {
    protected boolean isPreserveAndRestoreRequired() {
      return true;
    }
    

    protected void handleRenderTarget(RenderTarget renderTarget) { PlayerApplet.this.handleRenderTarget(renderTarget); } };
  
  public PlayerApplet() {}
  
  private JButton m_pauseButton = new JButton("pause");
  private JButton m_resumeButton = new JButton("resume");
  private JButton m_startButton = new JButton("restart");
  private JButton m_stopButton = new JButton("stop");
  
  private Color decodeColorParam(String name, Color valueIfNull) {
    String value = getParameter(name);
    if (value != null) {
      return Color.decode(value);
    }
    return valueIfNull;
  }
  
  private class ProgressPanel extends Panel
  {
    private String m_worldName = getParameter("world");
    private JProgressBar m_downloadProgressBar;
    private JProgressBar m_extractProgressBar;
    
    public ProgressPanel()
    {
      setLayout(null);
      setForeground(PlayerApplet.this.decodeColorParam("boxfgcolor", Color.black));
      setBackground(PlayerApplet.this.decodeColorParam("boxbgcolor", Color.white));
      Color progressColor = PlayerApplet.this.decodeColorParam("progresscolor", Color.blue);
      
      String s = getParameter("WIDTH");
      int width;
      int width; if (s != null) {
        width = Integer.parseInt(s);
      } else {
        width = 320;
      }
      
      m_downloadProgressBar = new JProgressBar();
      m_downloadProgressBar.setLocation(40, 80);
      m_downloadProgressBar.setSize(width - 80, 32);
      m_downloadProgressBar.setString("downloading...");
      m_downloadProgressBar.setStringPainted(true);
      add(m_downloadProgressBar);
      
      m_extractProgressBar = new JProgressBar();
      m_extractProgressBar.setLocation(40, 120);
      m_extractProgressBar.setSize(width - 80, 32);
      m_extractProgressBar.setString("extracting...");
      m_extractProgressBar.setStringPainted(true);
      add(m_extractProgressBar);
    }
    
    public void setDownloadCurrent(int downloadCurrent) {
      m_downloadProgressBar.setValue(downloadCurrent);
    }
    
    public void setDownloadTotal(int downloadTotal) { m_downloadProgressBar.setMaximum(downloadTotal); }
    
    public void setExtractCurrent(int extractCurrent)
    {
      m_extractProgressBar.setValue(extractCurrent);
    }
    
    public void setExtractTotal(int extractTotal) { m_extractProgressBar.setMaximum(extractTotal); }
    

    public void paint(Graphics g)
    {
      super.paint(g);
      g.drawString("Alice world: " + m_worldName, 20, 40);
    }
  }
  
  private void startWorld()
  {
    m_player.stopWorldIfNecessary();
    m_startButton.setText("restart");
    m_pauseButton.setEnabled(true);
    m_stopButton.setEnabled(true);
    m_resumeButton.setEnabled(false);
    m_player.startWorld();
  }
  
  private void stopWorld() { m_startButton.setText("start");
    m_pauseButton.setEnabled(false);
    m_stopButton.setEnabled(false);
    m_resumeButton.setEnabled(false);
    m_player.stopWorld();
  }
  
  private void pauseWorld() { m_pauseButton.setEnabled(false);
    m_resumeButton.setEnabled(true);
    m_player.pauseWorld();
  }
  
  private void resumeWorld() { m_pauseButton.setEnabled(true);
    m_resumeButton.setEnabled(false);
    m_player.resumeWorld();
  }
  
  public void init()
  {
    super.init();
    
    setLayout(new BorderLayout());
    
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(1, 4));
    
    m_pauseButton.setEnabled(false);
    m_resumeButton.setEnabled(false);
    m_startButton.setEnabled(false);
    m_stopButton.setEnabled(false);
    
    panel.add(m_pauseButton);
    panel.add(m_resumeButton);
    panel.add(m_startButton);
    panel.add(m_stopButton);
    
    add(panel, "North");
    
    m_progressPanel = new ProgressPanel();
    add(m_progressPanel, "Center");
    

    m_pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PlayerApplet.this.pauseWorld();
      }
      
    });
    m_resumeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PlayerApplet.this.resumeWorld();
      }
      
    });
    m_stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PlayerApplet.this.stopWorld();
      }
      
    });
    m_startButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PlayerApplet.this.startWorld();
      }
    });
  }
  
  private ProgressPanel m_progressPanel;
  private Runnable m_loadRunnable = new Runnable() {
    public void run() {
      try {
        Matrix4d localMatrix4d = Matrix4d.class;
      } catch (Throwable t) {
        remove(m_progressPanel);
        String initErrorMessage = getParameter("initializationErrorMessage");
        initErrorMessage = null;
        if (initErrorMessage == null) {
          initErrorMessage = "Alice is unable to initialize.\n\nPlease click the link below.";
        }
        JTextArea errorTextArea = new JTextArea();
        errorTextArea.setText(initErrorMessage);
        add(errorTextArea);
        return;
      }
      try {
        URL url = getClass().getResource("My_Alice_World.a2w");
        URLConnection urlConnection = url.openConnection();
        InputStream is = urlConnection.getInputStream();
        int contentLength = urlConnection.getContentLength();
        m_progressPanel.setDownloadTotal(contentLength);
        
        int bufferLength = 2048;
        byte[] content;
        if (contentLength != -1) {
          int offset = 0;
          byte[] content = new byte[contentLength];
          while (offset < contentLength) {
            int actual = is.read(content, offset, Math.min(2048, contentLength - offset));
            offset += actual;
            m_progressPanel.setDownloadCurrent(offset);
          }
        } else {
          byte[] buffer = new byte['ࠀ'];
          ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
          for (;;) {
            int actual = is.read(buffer, 0, 2048);
            if (actual == -1) break;
            baos.write(buffer, 0, actual);
          }
          


          content = baos.toByteArray();
        }
        is.close();
        urlConnection = null;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        
        m_player.loadWorld(bais, new edu.cmu.cs.stage3.progress.ProgressObserver() {
          public void progressBegin(int total) {
            progressUpdateTotal(total);
          }
          
          public void progressUpdateTotal(int total) { m_progressPanel.setExtractTotal(total); }
          
          public void progressUpdate(int current, String description) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
            m_progressPanel.setExtractCurrent(current);
          }
          

          public void progressEnd() {}
        });
        m_startButton.setEnabled(true);
        PlayerApplet.this.startWorld();
      } catch (MalformedURLException murle) {
        murle.printStackTrace();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  };
  
  public void start() {
    super.start();
    new Thread(m_loadRunnable).start();
  }
  
  public void stop() {
    m_player.stopWorldIfNecessary();
    m_player.unloadWorld();
    super.stop();
  }
  
  protected void handleRenderTarget(RenderTarget renderTarget) {
    remove(m_progressPanel);
    add(renderTarget.getInternal().getAWTComponent(), "Center");
    doLayout();
    invalidate();
    repaint();
  }
}

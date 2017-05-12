package edu.cmu.cs.stage3.alice.scripting.jython;

import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.Properties;
import java.util.Vector;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyFile;
import org.python.core.PySystemState;
import org.python.core.__builtin__;








public class ScriptingFactory
  implements edu.cmu.cs.stage3.alice.scripting.ScriptingFactory
{
  private Vector m_interpreters = new Vector();
  private Interpreter[] m_interpreterArray = null;
  
  public ScriptingFactory() {
    Properties preProperties;
    try {
      preProperties = System.getProperties();
    } catch (AccessControlException ace) { Properties preProperties;
      preProperties = new Properties();
      preProperties.setProperty("python.home", System.getProperty("python.home"));
    }
    Properties postProperties = null;
    String[] argv = { "" };
    PySystemState.initialize(preProperties, postProperties, argv, null);
    

    PySystemState systemState = Py.getSystemState();
    String pythonHome = preProperties.getProperty("python.home");
    String pathname = pythonHome + "/Lib/alice/__init__.py";
    try {
      File f = new File(pathname);
      InputStream is = new FileInputStream(f.getAbsoluteFile());
      BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
      StringBuffer sb = new StringBuffer();
      for (;;) {
        String s = br.readLine();
        if (s == null) break;
        sb.append(s);
        sb.append('\n');
      }
      


      if (sb.length() > 0) {
        String script = sb.substring(0, sb.length() - 1);
        PyCode code = __builtin__.compile(script, pathname, "exec");
        Py.exec(code, builtins, builtins);
      }
    } catch (IOException ioe) {
      throw new ExceptionWrapper(ioe, "IOException attempting to load " + pathname);
    }
  }
  
  public synchronized edu.cmu.cs.stage3.alice.scripting.Interpreter manufactureInterpreter() { Interpreter interpreter = new Interpreter(this);
    m_interpreters.addElement(interpreter);
    m_interpreterArray = null;
    return interpreter;
  }
  
  synchronized void releaseInterpreter(edu.cmu.cs.stage3.alice.scripting.Interpreter interpreter) { m_interpreterArray = null;
    m_interpreters.removeElement(interpreter);
  }
  
  public synchronized edu.cmu.cs.stage3.alice.scripting.Interpreter[] getInterpreters() { if (m_interpreterArray == null) {
      m_interpreterArray = new Interpreter[m_interpreters.size()];
      m_interpreters.copyInto(m_interpreterArray);
    }
    return m_interpreterArray;
  }
  
  private OutputStream m_stdout = null;
  private OutputStream m_stderr = null;
  
  public OutputStream getStdOut() { return m_stdout; }
  
  public void setStdOut(OutputStream stdout) {
    m_stdout = stdout;
    getSystemStatestdout = new PyFile(stdout);
  }
  
  public OutputStream getStdErr() { return m_stderr; }
  
  public void setStdErr(OutputStream stderr) {
    m_stderr = stderr;
    getSystemStatestderr = new PyFile(stderr);
  }
}

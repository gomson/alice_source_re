package edu.cmu.cs.stage3.alice.scripting.jython;

import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.scripting.CompileType;
import java.util.Dictionary;
import java.util.Hashtable;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyModule;
import org.python.core.__builtin__;
















public class Interpreter
  implements edu.cmu.cs.stage3.alice.scripting.Interpreter
{
  static final Dictionary s_map = new Hashtable();
  
  static { s_map.put(CompileType.EVAL, "eval");
    s_map.put(CompileType.EXEC_SINGLE, "single");
    s_map.put(CompileType.EXEC_MULTIPLE, "exec");
  }
  

  private ScriptingFactory m_scriptingFactory;
  private PyModule m_module;
  private Namespace m_dict;
  private World m_world;
  public Interpreter(ScriptingFactory scriptingFactory)
  {
    m_scriptingFactory = scriptingFactory;
    
    m_dict = new Namespace();
    m_module = new PyModule("main", m_dict);
  }
  
  private void resetNamespace() {
    m_dict.clear();
    m_dict.setWorld(m_world);
  }
  
  public void setWorld(World world) { m_world = world;
    resetNamespace();
  }
  
  public void release() { m_scriptingFactory.releaseInterpreter(this);
    m_scriptingFactory = null;
  }
  
  public void start() {
    resetNamespace();
  }
  
  public void stop() {}
  
  public edu.cmu.cs.stage3.alice.scripting.Code compile(String script, Object source, CompileType compileType) {
    PyCode pyCode = Py.compile_flags(script, source.toString(), (String)s_map.get(compileType), null);
    return new Code(pyCode, compileType);
  }
  
  public Object eval(edu.cmu.cs.stage3.alice.scripting.Code code) { return __builtin__.eval(((Code)code).getPyCode(), m_dict, m_dict); }
  
  public void exec(edu.cmu.cs.stage3.alice.scripting.Code code) {
    Py.exec(((Code)code).getPyCode(), m_dict, m_dict);
  }
}

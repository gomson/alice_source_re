package edu.cmu.cs.stage3.alice.scripting.jython;

import edu.cmu.cs.stage3.alice.scripting.CompileType;
import org.python.core.PyCode;




















public class Code
  implements edu.cmu.cs.stage3.alice.scripting.Code
{
  private PyCode m_pyCode;
  private CompileType m_compileType;
  
  public Code(PyCode pyCode, CompileType compileType)
  {
    m_pyCode = pyCode;
    m_compileType = compileType;
  }
  
  public PyCode getPyCode() { return m_pyCode; }
  
  public CompileType getCompileType() {
    return m_compileType;
  }
}

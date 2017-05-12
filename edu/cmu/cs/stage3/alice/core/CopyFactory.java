package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Component;
import edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.HowMuch;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Vector;










































































































public class CopyFactory
{
  private ElementCapsule m_capsule;
  private Class m_valueClass;
  private Class HACK_m_hackValueClass;
  
  private class ElementCapsule
  {
    private Class m_cls;
    private PropertyCapsule[] m_propertyCapsules;
    private ElementCapsule[] m_childCapsules;
    
    private class PropertyCapsule
    {
      private String m_name;
      private Object m_value;
      
      private PropertyCapsule(Property property, ReferenceGenerator referenceGenerator)
      {
        m_name = property.getName();
        ObjectArrayProperty oap; if ((property instanceof ObjectArrayProperty)) {
          oap = (ObjectArrayProperty)property;
          if (oap.get() != null) {
            Object[] array = new Object[oap.size()];
            for (int i = 0; i < oap.size(); i++) {
              array[i] = getValueToTuckAway(oap.get(i), referenceGenerator);
            }
            m_value = array;
          } else {
            m_value = null;
          }
        } else {
          m_value = getValueToTuckAway(property.get(), referenceGenerator);
        }
      }
      
      private Object getCopyIfPossible(Object o) { if ((o instanceof Cloneable))
        {


          return o;
        }
        return o;
      }
      
      private Object getValueToTuckAway(Object value, ReferenceGenerator referenceGenerator) {
        if ((value instanceof Element)) {
          return referenceGenerator.generateReference((Element)value);
        }
        return getCopyIfPossible(value);
      }
      
      private Object getValueForCopy(Property property, Object value, Vector referencesToBeResolved) {
        if ((value instanceof Criterion)) {
          referencesToBeResolved.addElement(new PropertyReference(property, (Criterion)value));
          return null;
        }
        return getCopyIfPossible(value);
      }
      
      private Object getValueForCopy(ObjectArrayProperty oap, Object value, int i, Vector referencesToBeResolved) {
        if ((value instanceof Criterion)) {
          referencesToBeResolved.addElement(new ObjectArrayPropertyReference(oap, (Criterion)value, i, 0));
          return null;
        }
        return getCopyIfPossible(value);
      }
      
      public void set(Element element, Vector referencesToBeResolved)
      {
        Property property = element.getPropertyNamed(m_name);
        if ((property instanceof ObjectArrayProperty)) {
          ObjectArrayProperty oap = (ObjectArrayProperty)property;
          if (m_value != null) {
            Object[] src = (Object[])m_value;
            Object[] dst = (Object[])Array.newInstance(oap.getComponentType(), src.length);
            for (int i = 0; i < src.length; i++) {
              dst[i] = getValueForCopy(oap, src[i], i, referencesToBeResolved);
            }
            oap.set(dst);
          } else {
            oap.set(null);
          }
        } else {
          property.set(getValueForCopy(property, m_value, referencesToBeResolved));
        }
      }
    }
    

    private ElementCapsule(Element element, ReferenceGenerator referenceGenerator, Class[] classesToShare, HowMuch howMuch)
    {
      m_cls = element.getClass();
      

      Element[] elementChildren = element.getChildren();
      m_childCapsules = new ElementCapsule[elementChildren.length];
      for (int i = 0; i < m_childCapsules.length; i++) {
        if (elementChildren[i].isAssignableToOneOf(classesToShare)) {
          m_childCapsules[i] = null;
        } else {
          m_childCapsules[i] = new ElementCapsule(CopyFactory.this, elementChildren[i], referenceGenerator, classesToShare, howMuch);
        }
      }
      
      Property[] elementProperties = element.getProperties();
      m_propertyCapsules = new PropertyCapsule[elementProperties.length];
      for (int i = 0; i < m_propertyCapsules.length; i++) {
        m_propertyCapsules[i] = new PropertyCapsule(elementProperties[i], referenceGenerator, null);
      }
    }
    
    private String getName() {
      for (int i = 0; i < m_propertyCapsules.length; i++) {
        PropertyCapsule propertyCapsule = m_propertyCapsules[i];
        if (m_name.equals("name")) {
          return (String)m_value;
        }
      }
      return null;
    }
    
    private Element internalManufacture(Vector referencesToBeResolved) {
      try {
        element = (Element)m_cls.newInstance();
      } catch (Throwable t) { Element element;
        throw new RuntimeException(); }
      Element element;
      for (int i = 0; i < m_childCapsules.length; i++) {
        if (m_childCapsules[i] != null) {
          element.addChild(m_childCapsules[i].internalManufacture(referencesToBeResolved));
        }
      }
      for (int i = 0; i < m_propertyCapsules.length; i++) {
        m_propertyCapsules[i].set(element, referencesToBeResolved);
      }
      return element;
    }
    
    private Element lookup(Element element, VariableCriterion variableCriterion) {
      if (element != null) {
        for (int i = 0; i < element.getChildCount(); i++) {
          Element child = element.getChildAt(i);
          if (variableCriterion.accept(child)) {
            return child;
          }
        }
        return lookup(element.getParent(), variableCriterion);
      }
      return null;
    }
    
    private Element manufacture(ReferenceResolver referenceResolver, ProgressObserver progressObserver, Element parentToBe) throws UnresolvablePropertyReferencesException
    {
      Vector referencesToBeResolved = new Vector();
      Element element = internalManufacture(referencesToBeResolved);
      Vector referencesLeftUnresolved = new Vector();
      element.setParent(parentToBe);
      try {
        if ((referenceResolver instanceof DefaultReferenceResolver)) {
          DefaultReferenceResolver drr = (DefaultReferenceResolver)referenceResolver;
          if (drr.getInternalRoot() == null) {
            drr.setInternalRoot(element);
          }
        }
        Enumeration enum0 = referencesToBeResolved.elements();
        while (enum0.hasMoreElements()) {
          PropertyReference propertyReference = (PropertyReference)enum0.nextElement();
          try {
            Criterion criterion = propertyReference.getCriterion();
            if ((criterion instanceof VariableCriterion)) {
              VariableCriterion variableCriterion = (VariableCriterion)criterion;
              Element variable = lookup(propertyReference.getProperty().getOwner(), variableCriterion);
              if (variable != null) {
                propertyReference.getProperty().set(variable);
              }
              
            }
            else
            {
              propertyReference.resolve(referenceResolver);
            }
          } catch (UnresolvableReferenceException ure) {
            referencesLeftUnresolved.addElement(propertyReference);
          }
        }
      } finally {
        element.setParent(null);
      }
      if (referencesLeftUnresolved.size() > 0) {
        PropertyReference[] propertyReferences = new PropertyReference[referencesLeftUnresolved.size()];
        referencesLeftUnresolved.copyInto(propertyReferences);
        StringBuffer sb = new StringBuffer();
        sb.append("PropertyReferences: \n");
        for (int i = 0; i < propertyReferences.length; i++) {
          sb.append(propertyReferences[i]);
          sb.append("\n");
        }
        throw new UnresolvablePropertyReferencesException(propertyReferences, element, sb.toString());
      }
      return element;
    }
  }
  

  public CopyFactory(Element element, Element internalReferenceRoot, Class[] classesToShare, HowMuch howMuch)
  {
    ReferenceGenerator referenceGenerator;
    ReferenceGenerator referenceGenerator;
    if (((element instanceof Response)) || ((element instanceof Component))) {
      referenceGenerator = new CodeCopyReferenceGenerator(internalReferenceRoot, classesToShare);
    } else {
      referenceGenerator = new CopyReferenceGenerator(internalReferenceRoot, classesToShare);
    }
    m_capsule = new ElementCapsule(element, referenceGenerator, classesToShare, howMuch, null);
    m_valueClass = element.getClass();
    HACK_m_hackValueClass = null;
    try {
      if ((element instanceof Expression)) {
        HACK_m_hackValueClass = ((Expression)element).getValueClass();
      }
    }
    catch (Throwable localThrowable) {}
  }
  
  public Class getValueClass() {
    return m_valueClass;
  }
  
  public Class HACK_getExpressionValueClass() { return HACK_m_hackValueClass; }
  
  public Element manufactureCopy(ReferenceResolver referenceResolver, ProgressObserver progressObserver, Element parentToBe) throws UnresolvablePropertyReferencesException
  {
    return m_capsule.manufacture(referenceResolver, progressObserver, parentToBe);
  }
  




  public Element manufactureCopy(Element externalRoot, Element internalRoot, ProgressObserver progressObserver, Element parentToBe)
    throws UnresolvablePropertyReferencesException
  {
    return manufactureCopy(new DefaultReferenceResolver(internalRoot, externalRoot), progressObserver, parentToBe);
  }
  
  public Element manufactureCopy(Element externalRoot, Element internalRoot, ProgressObserver progressObserver) throws UnresolvablePropertyReferencesException { return manufactureCopy(externalRoot, internalRoot, progressObserver, null); }
  
  public Element manufactureCopy(Element externalRoot, Element internalRoot) throws UnresolvablePropertyReferencesException {
    return manufactureCopy(externalRoot, internalRoot, null);
  }
  
  public Element manufactureCopy(Element externalRoot) throws UnresolvablePropertyReferencesException { return manufactureCopy(externalRoot, null); }
  
  public String toString()
  {
    return "edu.cmu.cs.stage3.alice.core.CopyFactory[" + m_valueClass + "]";
  }
}

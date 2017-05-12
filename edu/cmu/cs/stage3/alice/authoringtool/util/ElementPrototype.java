package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;
import edu.cmu.cs.stage3.alice.core.question.PropertyValue;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.ForEachInOrder;
import edu.cmu.cs.stage3.alice.core.response.ForEachTogether;
import edu.cmu.cs.stage3.alice.core.response.LoopNInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Vector;



public class ElementPrototype
{
  protected Class elementClass;
  protected StringObjectPair[] knownPropertyValues;
  protected String[] desiredProperties;
  
  public ElementPrototype(Class elementClass, StringObjectPair[] knownPropertyValues, String[] desiredProperties)
  {
    if (!Element.class.isAssignableFrom(elementClass)) {
      throw new IllegalArgumentException(Messages.getString("The_elementClass_given_is_not_actually_a_subclass_of_Element_"));
    }
    
    Element testElement = null;
    try {
      testElement = (Element)elementClass.newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(Messages.getString("Unable_to_create_a_new_element_of_type__") + elementClass.getName());
    }
    if ((!CallToUserDefinedResponse.class.isAssignableFrom(elementClass)) && (!CallToUserDefinedQuestion.class.isAssignableFrom(elementClass))) {
      if (knownPropertyValues != null) {
        for (int i = 0; i < knownPropertyValues.length; i++) {
          String propertyName = knownPropertyValues[i].getString();
          Object propertyValue = knownPropertyValues[i].getObject();
          Property property = testElement.getPropertyNamed(propertyName);
          if (property == null) {
            throw new IllegalArgumentException(Messages.getString("property_named_") + propertyName + " " + Messages.getString("does_not_exist_in_") + elementClass.getName());
          }
          if (propertyValue != null)
          {


            if ((propertyValue instanceof Expression)) {
              Class valueClass = property.getValueClass();
              if (!property.getValueClass().isAssignableFrom(propertyValue.getClass()))
              {

                if (!property.getValueClass().isAssignableFrom(((Expression)propertyValue).getValueClass())) {
                  throw new IllegalArgumentException(Messages.getString("property_named_") + propertyName + " " + Messages.getString("in_class_") + elementClass.getName() + " " + Messages.getString("does_not_accept_expressions_of_type_") + ((Expression)propertyValue).getValueClass().getName());
                }
              }
            }
            else if (!property.getValueClass().isAssignableFrom(propertyValue.getClass())) {
              throw new IllegalArgumentException(Messages.getString("property_named_") + propertyName + " " + Messages.getString("in_class_") + elementClass.getName() + " " + Messages.getString("does_not_accept_values_of_type_") + propertyValue.getClass().getName() + Messages.getString("__bad_value__") + propertyValue);
            }
          }
        }
      } else {
        knownPropertyValues = new StringObjectPair[0];
      }
      if (desiredProperties != null) {
        for (int i = 0; i < desiredProperties.length; i++)
        {
          Property property = testElement.getPropertyNamed(desiredProperties[i]);
          if (property == null) {
            throw new IllegalArgumentException(Messages.getString("property_named_") + desiredProperties[i] + " " + Messages.getString("does_not_exist_in_") + elementClass.getName());
          }
        }
      } else {
        desiredProperties = new String[0];
      }
    }
    
    this.elementClass = elementClass;
    this.knownPropertyValues = knownPropertyValues;
    this.desiredProperties = desiredProperties;
  }
  
  public Element createNewElement() {
    try {
      Element element = (Element)elementClass.newInstance();
      
      if (knownPropertyValues != null) {
        for (int i = 0; i < knownPropertyValues.length; i++) {
          String propertyName = knownPropertyValues[i].getString();
          Object propertyValue = knownPropertyValues[i].getObject();
          Property property = element.getPropertyNamed(propertyName);
          property.set(propertyValue);
          
          if ((propertyValue instanceof PropertyValue)) {
            PropertyValue propertyValueQuestion = (PropertyValue)propertyValue;
            propertyValueQuestion.removeFromParent();
            property.getOwner().addChild(propertyValueQuestion);
          }
          else if ((propertyValue instanceof Question)) {
            Question q = (Question)propertyValue;
            q.removeFromParent();
            property.getOwner().addChild(q);
            
            data.put("associatedProperty", property.getName());
          }
          

          if ((propertyValue instanceof Element)) {
            Element e = (Element)propertyValue;
            if ((e.getParent() == null) && (!(e instanceof World))) {
              property.getOwner().addChild(e);
              data.put("associatedProperty", property.getName());
            }
          }
        }
      }
      

      if (edu.cmu.cs.stage3.alice.core.response.ForEach.class.isAssignableFrom(elementClass)) {
        edu.cmu.cs.stage3.alice.core.response.ForEach forResponse = null;
        if (ForEachInOrder.class.isAssignableFrom(elementClass)) {
          forResponse = (ForEachInOrder)element;
        } else if (ForEachTogether.class.isAssignableFrom(elementClass)) {
          forResponse = (ForEachTogether)element;
        }
        Variable eachVar = new Variable();
        name.set(Messages.getString("item"));
        valueClass.set(Object.class);
        forResponse.addChild(eachVar);
        each.set(eachVar);
      } else if (edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach.class.isAssignableFrom(elementClass)) {
        edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach forQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)element;
        Variable eachVar = new Variable();
        name.set(Messages.getString("item"));
        valueClass.set(Object.class);
        forQuestion.addChild(eachVar);
        each.set(eachVar);
      } else if (LoopNInOrder.class.isAssignableFrom(elementClass)) {
        LoopNInOrder loopN = (LoopNInOrder)element;
        Variable indexVar = new Variable();
        name.set(Messages.getString("index"));
        valueClass.set(Number.class);
        loopN.addChild(indexVar);
        index.set(indexVar);
      } else if (LoopN.class.isAssignableFrom(elementClass)) {
        LoopN loopN = (LoopN)element;
        Variable indexVar = new Variable();
        name.set(Messages.getString("index"));
        valueClass.set(Number.class);
        loopN.addChild(indexVar);
        index.set(indexVar);
      }
      return element;
    } catch (Exception e) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_creating_new_element_"), e);
    }
    
    return null;
  }
  
  public ElementPrototype createCopy(StringObjectPair newKnownPropertyValue) {
    return createCopy(new StringObjectPair[] { newKnownPropertyValue });
  }
  
  public ElementPrototype createCopy(StringObjectPair[] newKnownPropertyValues) {
    Vector vKnownPropertyValues = new Vector(Arrays.asList(knownPropertyValues));
    Vector vDesiredProperties = new Vector(Arrays.asList(desiredProperties));
    
    if (newKnownPropertyValues != null) {
      for (int i = 0; i < newKnownPropertyValues.length; i++) {
        if (vDesiredProperties.contains(newKnownPropertyValues[i].getString())) {
          vDesiredProperties.remove(newKnownPropertyValues[i].getString());
        }
        boolean subbed = false;
        for (ListIterator iter = vKnownPropertyValues.listIterator(); iter.hasNext();) {
          StringObjectPair pair = (StringObjectPair)iter.next();
          if (pair.getString().equals(newKnownPropertyValues[i].getString())) {
            iter.set(newKnownPropertyValues[i]);
            subbed = true;
          }
        }
        if (!subbed) {
          vKnownPropertyValues.add(newKnownPropertyValues[i]);
        }
      }
    }
    
    return createInstance(elementClass, (StringObjectPair[])vKnownPropertyValues.toArray(new StringObjectPair[0]), (String[])vDesiredProperties.toArray(new String[0]));
  }
  
  public Class getElementClass() {
    return elementClass;
  }
  
  public StringObjectPair[] getKnownPropertyValues() {
    return knownPropertyValues;
  }
  
  public String[] getDesiredProperties() {
    return desiredProperties;
  }
  

  protected ElementPrototype createInstance(Class elementClass, StringObjectPair[] knownPropertyValues, String[] desiredProperties)
  {
    return new ElementPrototype(elementClass, knownPropertyValues, desiredProperties);
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getClass().getName() + "[ ");
    sb.append("elementClass = " + elementClass.getName() + ", ");
    sb.append("knownPropertyValues = [ ");
    if (knownPropertyValues != null) {
      for (int i = 0; i < knownPropertyValues.length; i++)
      {
        sb.append("StringObjectPair( ");
        sb.append(knownPropertyValues[i].getString() + ", ");
        sb.append(knownPropertyValues[i].getObject().toString() + ", ");
        sb.append("), ");
      }
    } else {
      sb.append("<null>");
    }
    sb.append(" ], ");
    sb.append("desiredProperties = [ ");
    if (desiredProperties != null) {
      for (int i = 0; i < desiredProperties.length; i++) {
        sb.append(desiredProperties[i] + ", ");
      }
    } else {
      sb.append("<null>");
    }
    sb.append(" ], ]");
    
    return sb.toString();
  }
}

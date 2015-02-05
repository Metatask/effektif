/* Copyright 2014 Effektif GmbH.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package com.effektif.workflow.impl.bpmn.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.events.Namespace;

/** xml dom structure that is jsonnable with jackson
 * so that it can be serialized to/from json for rest service and db persistence. */
public class XmlElement {

  public String name;
  public Map<String,String> attributes;
  /** maps prefixes to namespace uris */
  public Map<String,String> namespaces;
  public List<XmlElement> elements;
  public String text;

  public XmlElement() {
  }

  public XmlElement(String name) {
    this.name = name;
  }

  public void addNamespace(String prefix, String namespaceUri) {
    if (namespaces==null) {
      namespaces = new LinkedHashMap<>();
    }
    namespaces.put(prefix, namespaceUri);
  }

  void addNamespace(Namespace namespace) {
    addNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
  }

  public void addAttribute(String name, String value) {
    if (attributes==null) {
      attributes = new LinkedHashMap<>();
    }
    attributes.put(name, value);
  }

  public void addElement(XmlElement xmlElement) {
    if (elements==null) {
      this.elements = new ArrayList<>();
    }
    this.elements.add(xmlElement);
  }

  public void addElementFirst(XmlElement xmlElement) {
    if (elements==null) {
      elements = new ArrayList<>();
    }
    elements.add(0, xmlElement);
  }

  public boolean is(String name) {
    return name.equals(this.name);
  }
  
  public void addText(String text) {
    if (text!=null && !"".equals(text.trim())) {
      this.text = this.text!=null ? this.text+text : text;
    }
  }

  public boolean hasContent() {
    if (elements!=null && !elements.isEmpty()) {
      return false;
    }
    if (text!=null && !"".equals(text)) {
      return false;
    }
    return true;
  }

  public String removeAttribute(String name) {
    return attributes!=null ? attributes.remove(name) : null;
  }
}

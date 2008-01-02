/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.camel.spring;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.helpers.DOMUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SmxEndpointBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {

	@Override
	protected void doParse(Element element, ParserContext ctx,
			BeanDefinitionBuilder bean) {
		parseAttributes(element, ctx, bean);
		parseChildElements(element, ctx, bean);
		bean.setLazyInit(false);
	}

	@Override
	protected Class getBeanClass(Element arg0) {
		return ServiceMixEndpointBean.class;
	}

	@Override
	protected String resolveId(Element elem, AbstractBeanDefinition definition,
			ParserContext ctx) throws BeanDefinitionStoreException {
		String id = super.resolveId(elem, definition, ctx);
		if (id == null || id.length() == 0) {
			throw new BeanDefinitionStoreException("The bean id is needed.");
		}

		return id;
	}

	protected void mapAttribute(BeanDefinitionBuilder bean, Element e,
			String name, String val) {
		if ("serviceName".equals(name)) {
			QName q = parseQName(e, val);
			bean.addPropertyValue(name, q);
		} else {
			mapToProperty(bean, name, val);
		}
	}

	protected void mapElement(ParserContext ctx, BeanDefinitionBuilder bean,
			Element el, String name) {
		if ("properties".equals(name)) {
			Map map = ctx.getDelegate().parseMapElement(el,
					bean.getBeanDefinition());
			bean.addPropertyValue("properties", map);
		} else {
			setFirstChildAsProperty(el, ctx, bean, name);
		}
	}

	protected QName parseQName(Element element, String t) {
		String ns = null;
		String pre = null;
		String local = null;

		if (t.startsWith("{")) {
			int i = t.indexOf('}');
			if (i == -1) {
				throw new RuntimeException(
						"Namespace bracket '{' must having a closing bracket '}'.");
			}

			ns = t.substring(1, i);
			t = t.substring(i + 1);
		}

		int colIdx = t.indexOf(':');
		if (colIdx == -1) {
			local = t;
			pre = "";

			ns = DOMUtils.getNamespace(element, "");
		} else {
			pre = t.substring(0, colIdx);
			local = t.substring(colIdx + 1);

			ns = DOMUtils.getNamespace(element, pre);
		}

		return new QName(ns, local, pre);
	}

	protected void parseChildElements(Element element, ParserContext ctx,
			BeanDefinitionBuilder bean) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				String name = n.getLocalName();

				mapElement(ctx, bean, (Element) n, name);
			}
		}
	}

	protected void parseAttributes(Element element, ParserContext ctx,
			BeanDefinitionBuilder bean) {
		NamedNodeMap atts = element.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Attr node = (Attr) atts.item(i);
			String val = node.getValue();
			String name = node.getLocalName();

			mapAttribute(bean, element, name, val);

		}

	}

	protected void mapToProperty(BeanDefinitionBuilder bean,
			String propertyName, String val) {
		if (ID_ATTRIBUTE.equals(propertyName)) {
			return;
		}

		if (StringUtils.hasText(val)) {
			if (val.startsWith("#")) {
				bean.addPropertyReference(propertyName, val.substring(1));
			} else {
				bean.addPropertyValue(propertyName, val);
			}
		}
	}

	protected void setFirstChildAsProperty(Element element, ParserContext ctx,
			BeanDefinitionBuilder bean, String propertyName) {
		String id = getAndRegisterFirstChild(element, ctx, bean, propertyName);
		bean.addPropertyReference(propertyName, id);

	}

	protected String getAndRegisterFirstChild(Element element,
			ParserContext ctx, BeanDefinitionBuilder bean, String propertyName) {
		Element first = getFirstChild(element);

		if (first == null) {
			throw new IllegalStateException(propertyName
					+ " property must have child elements!");
		}

		// Seems odd that we have to do the registration, I wonder if there is a
		// better way
		String id;
		BeanDefinition child;
		if (first.getNamespaceURI().equals(
				BeanDefinitionParserDelegate.BEANS_NAMESPACE_URI)) {
			String name = first.getLocalName();
			if ("ref".equals(name)) {
				id = first.getAttribute("bean");
				if (id == null) {
					throw new IllegalStateException(
							"<ref> elements must have a \"bean\" attribute!");
				}
				return id;
			} else if ("bean".equals(name)) {
				BeanDefinitionHolder bdh = ctx.getDelegate()
						.parseBeanDefinitionElement(first);
				child = bdh.getBeanDefinition();
				id = bdh.getBeanName();
			} else {
				throw new UnsupportedOperationException(
						"Elements with the name " + name
								+ " are not currently "
								+ "supported as sub elements of "
								+ element.getLocalName());
			}

		} else {
			child = ctx.getDelegate().parseCustomElement(first,
					bean.getBeanDefinition());
			id = child.toString();
		}

		ctx.getRegistry().registerBeanDefinition(id, child);
		return id;
	}
	
	protected Element getFirstChild(Element element) {
        Element first = null;
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                first = (Element) n;
            }
        }
        return first;
    }
}
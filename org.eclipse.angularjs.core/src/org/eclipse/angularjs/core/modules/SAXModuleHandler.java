package org.eclipse.angularjs.core.modules;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.angularjs.core.utils.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import tern.server.protocol.angular.AngularType;

class SAXModuleHandler extends DefaultHandler {

	private Module module;

	private String directiveName;
	private String url;
	private Collection<UseAs> useAs;
	private boolean optionnal;
	private AngularType directiveType;
	private Collection<String> tagsName;
	private StringBuilder description = null;

	public Module load(InputStream in) throws IOException, SAXException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.parse(new InputSource(in));
		return module;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if ("module".equals(name)) {
			String moduleName = attributes.getValue("name");
			module = new Module(moduleName);
		} else if ("directive".equals(name)) {
			this.directiveName = attributes.getValue("name");
			this.url = attributes.getValue("url");
			this.directiveType = AngularType.get(attributes.getValue("type"));
			// tags name
			this.tagsName = new ArrayList<String>();
			String tags = attributes.getValue("tags");
			if (!StringUtils.isEmpty(tags)) {
				String[] names = tags.split(",");
				String tagName = null;

				for (int i = 0; i < names.length; i++) {
					tagName = names[i].trim();
					if (tagName.length() > 0) {
						this.tagsName.add(tagName);
					}
				}
			}
			// use-as
			this.useAs = new ArrayList<UseAs>();
			String useAs = attributes.getValue("use-as");
			if (!StringUtils.isEmpty(useAs)) {
				String[] uses = useAs.split(" ");
				UseAs use = null;

				for (int i = 0; i < uses.length; i++) {
					use = UseAs.get(uses[i].trim());
					if (use != null) {
						this.useAs.add(use);
					}
				}
			}
			if (this.useAs.isEmpty()) {
				this.useAs.add(UseAs.attr);
			}
			// optionnal
			this.optionnal = StringUtils.asBoolean(
					attributes.getValue("optionnal"), false);
		} else if ("description".equals(name)) {
			this.description = new StringBuilder();
		}
		super.startElement(uri, localName, name, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if ("directive".equals(name)) {
			new Directive(directiveName, directiveType, url, tagsName, useAs,
					optionnal, description != null ? description.toString()
							: null, module);
			this.directiveName = null;
			this.directiveType = null;
			this.url = null;
			this.tagsName = null;
			this.useAs = null;
			this.description = null;
		}
		super.endElement(uri, localName, name);
	}

	public Module getModule() {
		return module;
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (description != null) {
			description.append(String.valueOf(ch, start, length));
		}
		super.characters(ch, start, length);
	}
}

package org.eclipse.angularjs.core.modules;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

class SAXModuleHandler extends DefaultHandler {

	private Module module;

	private String directiveName;
	private DirectiveType directiveType;
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
			this.directiveType = DirectiveType.get(attributes.getValue("type"));
			this.tagsName = new ArrayList<String>();

			String tags = attributes.getValue("tags");
			if (tags != null && tags.length() > 0) {
				String[] names = tags.split(",");
				String tagName = null;

				for (int i = 0; i < names.length; i++) {
					tagName = names[i].trim();
					if (tagName.length() > 0) {
						this.tagsName.add(tagName);
					}
				}
			}
		} else if ("description".equals(name)) {
			this.description = new StringBuilder();
		}
		super.startElement(uri, localName, name, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if ("directive".equals(name)) {
			new Directive(directiveName, directiveType, tagsName,
					description != null ? description.toString() : null, module);
			this.directiveName = null;
			this.directiveType = null;
			this.tagsName = null;
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

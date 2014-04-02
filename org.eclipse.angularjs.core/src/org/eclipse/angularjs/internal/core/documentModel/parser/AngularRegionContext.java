/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.core.documentModel.parser;


public interface AngularRegionContext {

	public static final String ANGULAR_EXPRESSION_OPEN = "ANGULAR_EXPRESSION_OPEN";
	public static final String ANGULAR_EXPRESSION_CLOSE = "ANGULAR_EXPRESSION_CLOSE";
	public static final String ANGULAR_EXPRESSION_CONTENT = "ANGULAR_EXPRESSION_CONTENT";

	public static final String ANGULAR_TAG_ATTRIBUTE_VALUE_DQUOTE = "ANGULAR_TAG_ATTRIBUTE_VALUE_DQUOTE";
	public static final String XML_TAG_ATTRIBUTE_VALUE_DQUOTE = "XML_TAG_ATTRIBUTE_VALUE_DQUOTE";

	public static final String ANGULAR_TAG_ATTRIBUTE_VALUE_SQUOTE = "ANGULAR_TAG_ATTRIBUTE_VALUE_SQUOTE";
	public static final String XML_TAG_ATTRIBUTE_VALUE_SQUOTE = "XML_TAG_ATTRIBUTE_VALUE_SQUOTE";

}

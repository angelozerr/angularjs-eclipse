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
package org.eclipse.angularjs.internal.core.documentModel.dom;

import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationReporter;
import org.eclipse.wst.xml.core.internal.validate.ValidationComponent;

/**
 * NullValidator class is intended to be a replacement of null for
 * ValidationComponent type.
 */
public class NullValidator extends ValidationComponent {

	public NullValidator() {
		super();
	}

	public void validate(IndexedRegion node) {
		return;
	}

	@Override
	public void setReporter(ValidationReporter reporter) {
		return;
	}

}

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
package org.eclipse.angularjs.internal.core.documentModel.encoding;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;

/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class FMDocumentCharsetDetector extends FMResourceEncodingDetector
		implements IDocumentCharsetDetector {

	public void set(IDocument document) {

	}

}

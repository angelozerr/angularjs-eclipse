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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;

/*
 * The encoding is set by user preference and not by reading/parsing
 * the content.
 * 
 * @GINO: For now only support UTF-8
 */
public class FMResourceEncodingDetector implements IResourceCharsetDetector {

	private static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
	private static final String UTF_8_JAVA = "UTF8"; //$NON-NLS-1$

	public String getEncoding() throws IOException {
		return UTF_8;
	}

	public EncodingMemento getEncodingMemento() throws IOException {
		EncodingMemento fEncodingMemento = new EncodingMemento();
		fEncodingMemento.setJavaCharsetName(UTF_8_JAVA);
		fEncodingMemento.setDetectedCharsetName(UTF_8);

		fEncodingMemento.setAppropriateDefault(getSpecDefaultEncoding());
		return null;
	}

	public String getSpecDefaultEncoding() {
		return UTF_8;
	}

	public void set(InputStream inputStream) {
		// @GINO: Do nothing for now

	}

	public void set(Reader reader) {
		// @GINO: Do nothing for now

	}

	public void set(IStorage iStorage) throws CoreException {
		// @GINO: Do nothing for now

	}

}

/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.angularjs.internal.core.documentModel.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.eclipse.angularjs.internal.core.documentModel.parser.AngularSourceParser;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularStructuredDocumentReParser;
import org.eclipse.angularjs.internal.core.documentModel.partitioner.AngularStructuredTextPartitioner;
import org.eclipse.angularjs.internal.core.modelquery.ModelQueryAdapterFactoryForAngular;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.html.core.internal.encoding.HTMLDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.encoding.CodedReaderCreator;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.encoding.IContentDescriptionExtended;
import org.eclipse.wst.sse.core.internal.encoding.util.Logger;
import org.eclipse.wst.sse.core.internal.encoding.util.NullInputStream;
import org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;

public class AngularDocumentLoader extends HTMLDocumentLoader {

	private static final boolean DEBUG = false;
	private CodedReaderCreator fCodedReaderCreator;

	protected IEncodedDocument newEncodedDocument() {
		IEncodedDocument doc = super.newEncodedDocument();
		assert doc instanceof BasicStructuredDocument;
		((BasicStructuredDocument) doc)
				.setReParser(new AngularStructuredDocumentReParser());

		// doc.setPreferredLineDelimiter( "\n" );
		return doc;
	}

	public RegionParser getParser() {
		AngularSourceParser parser = new AngularSourceParser();
		// for the "static HTML" case, we need to initialize
		// Blocktags here.
		addHTMLishTag(parser, "script"); //$NON-NLS-1$
		addHTMLishTag(parser, "style"); //$NON-NLS-1$
		return parser;
	}

	public IDocumentLoader newInstance() {
		return new AngularDocumentLoader();
	}

	/*@Override
	public List getAdapterFactories() {
		List factories = super.getAdapterFactories();
		factories.add(new ModelQueryAdapterFactoryForAngular());
		return factories;
	}*/

	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new AngularStructuredTextPartitioner();
	}

	protected CodedReaderCreator getCodedReaderCreator() {
		if (fCodedReaderCreator == null) {
			fCodedReaderCreator = new PHPCodedReaderCreator();
		}
		return fCodedReaderCreator;
	}

	class PHPCodedReaderCreator extends CodedReaderCreator {

		private EncodingRule fEncodingRule;

		private String fFilename;

		private IFile fIFile;

		private InputStream fInputStream;

		private static final String CHARSET_UTF_16 = "UTF-16"; //$NON-NLS-1$

		private static final String CHARSET_UTF_16LE = "UTF-16LE"; //$NON-NLS-1$

		public void set(IFile iFile) throws CoreException, IOException {
			super.set(iFile);
			fIFile = iFile;
		}

		public void set(String filename, InputStream inputStream) {
			super.set(filename, inputStream);
			fFilename = filename;
			fInputStream = inputStream;
		}

		protected EncodingMemento createMemento(
				IContentDescription contentDescription) {
			EncodingMemento result;
			String appropriateDefault = contentDescription.getContentType()
					.getDefaultCharset();
			String detectedCharset = (String) contentDescription
					.getProperty(IContentDescriptionExtended.DETECTED_CHARSET);
			String unSupportedCharset = (String) contentDescription
					.getProperty(IContentDescriptionExtended.UNSUPPORTED_CHARSET);
			String javaCharset = contentDescription.getCharset();

			// Set default workbench encoding:
			if (detectedCharset == null && appropriateDefault == null) {
				detectedCharset = javaCharset = appropriateDefault = ResourcesPlugin
						.getEncoding();
			}

			// integrity checks for debugging
			if (javaCharset == null) {
				Logger.log(Logger.INFO_DEBUG, "charset equaled null!"); //$NON-NLS-1$
			} else if (javaCharset.length() == 0) {
				Logger.log(Logger.INFO_DEBUG, "charset equaled emptyString!"); //$NON-NLS-1$
			}
			byte[] BOM = (byte[]) contentDescription
					.getProperty(IContentDescription.BYTE_ORDER_MARK);
			// result = (EncodingMemento)
			// contentDescription.getProperty(IContentDescriptionExtended.ENCODING_MEMENTO);
			result = createEncodingMemento(BOM, javaCharset, detectedCharset,
					unSupportedCharset, appropriateDefault, null);
			if (!result.isValid()) {
				result.setAppropriateDefault(appropriateDefault);
				// integrity check for debugging "invalid" cases.
				// the apprriate default we have, should equal what's in the
				// detected field. (not sure this is always required)
				if (DEBUG) {
					if (appropriateDefault != null
							&& !appropriateDefault.equals(detectedCharset)) {
						Logger.log(Logger.INFO_DEBUG,
								"appropriate did not equal detected, as expected for invalid charset case"); //$NON-NLS-1$
					}
				}
			}
			return result;
		}

		@Override
		public Reader getCodedReader() throws CoreException, IOException {
			Reader reader = super.getCodedReader();
			try {
				char tBuff[] = new char[CodedIO.MAX_BUF_SIZE];
				reader.read(tBuff, 0, tBuff.length);
				reader.reset();
				return reader;
			} catch (Exception e) {
			}

			InputStream is = getResettableStream();
			EncodingMemento encodingMemento = getEncodingMemento();
			String charsetName = encodingMemento.getJavaCharsetName();
			if (charsetName == null) {
				charsetName = encodingMemento.getDetectedCharsetName();
			}
			if (!encodingMemento.isValid() && !forceDefault()) {
				throw new UnsupportedCharsetExceptionWithDetail(encodingMemento);
			}

			if (fEncodingRule == EncodingRule.FORCE_DEFAULT) {
				charsetName = encodingMemento.getAppropriateDefault();
			}

			// [228366] For files that have a unicode BOM, and a charset name of
			// UTF-16, the charset decoder needs "UTF-16LE"
			if (CHARSET_UTF_16.equals(charsetName)
					&& encodingMemento.getUnicodeBOM() == IContentDescription.BOM_UTF_16LE)
				charsetName = CHARSET_UTF_16LE;
			reader = new BufferedReader(new InputStreamReader(is, charsetName),
					CodedIO.MAX_BUF_SIZE);
			return reader;
		}

		private boolean forceDefault() {

			boolean result = false;
			if (fEncodingRule != null
					&& fEncodingRule == EncodingRule.FORCE_DEFAULT)
				result = true;
			return result;
		}

		private InputStream getResettableStream() throws CoreException,
				IOException {

			InputStream resettableStream = null;

			if (fIFile != null) {
				InputStream inputStream = null;
				try {
					// note we always get contents, even if out of synch
					inputStream = fIFile.getContents(true);
				} catch (CoreException e) {
					// SHOULD actually check for existence of
					// fIStorage, but
					// for now will just assume core exception
					// means it
					// doesn't exist on file system, yet.
					// and we'll log, just in case its a noteable error
					Logger.logException(e);
					inputStream = new NullInputStream();
				}
				resettableStream = new BufferedInputStream(inputStream,
						CodedIO.MAX_BUF_SIZE);
			} else {
				if (fInputStream != null) {
					if (fInputStream.markSupported()) {
						resettableStream = fInputStream;
						// try {
						resettableStream.reset();
						// }
						// catch (IOException e) {
						// // assumed just hasn't been marked yet, so ignore
						// }
					} else {
						resettableStream = new BufferedInputStream(
								fInputStream, CodedIO.MAX_BUF_SIZE);
					}
				}
			}

			if (resettableStream == null) {
				resettableStream = new NullInputStream();
			}

			// mark this once, stream at "zero" position
			resettableStream.mark(MAX_MARK_SIZE);
			return resettableStream;
		}

		public void setEncodingRule(EncodingRule encodingRule) {
			super.setEncodingRule(encodingRule);
			fEncodingRule = encodingRule;
		}
	}
}

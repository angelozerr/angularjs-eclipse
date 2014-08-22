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

import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTokenizer;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class AngularSourceParser extends XMLSourceParser {

	public static ThreadLocal<IResource> editFile = new ThreadLocal<IResource>();
	private IProject project;

	public AngularSourceParser() {
		super();
		IResource resource = (IResource) editFile.get();
		if (resource instanceof IProject) {
			project = (IProject) resource;
		} else if (resource instanceof IFile) {
			project = ((IFile) resource).getProject();
		}
	}

	/*
	 * Change the Tokenizer used by the XMLSourceParser to make it Angular aware
	 */
	public BlockTokenizer getTokenizer() {
		if (fTokenizer == null) {
			AngularTokenizer angularTokenizer = new AngularTokenizer();
			// phpTokenizer.setProject(project);
			fTokenizer = angularTokenizer;
		}
		return fTokenizer;
	}

	public RegionParser newInstance() {
		AngularSourceParser newInstance = new AngularSourceParser();
		AngularTokenizer tokenizer = (AngularTokenizer) getTokenizer()
				.newInstance();
		// tokenizer.setProject(project);
		newInstance.setTokenizer(tokenizer);
		return newInstance;
	}

	private IStructuredDocumentRegion headNode = null;

	private IStructuredDocumentRegion lastNode = null;

	private IStructuredDocumentRegion currentNode = null;

	protected IStructuredDocumentRegion parseNodes() {
		// regions are initially reported as complete offsets within the
		// scanned input
		// they are adjusted here to be indexes from the currentNode's start
		// offset

		// reset the state
		headNode = lastNode = currentNode = null;

		ITextRegion region = null;
		String type = null;

		while ((region = getNextRegion()) != null) {
			type = region.getType();

			// these types (might) demand a IStructuredDocumentRegion for each
			// of them
			if (type == DOMRegionContext.BLOCK_TEXT) {
				if (currentNode != null
						&& currentNode.getLastRegion().getType() == DOMRegionContext.BLOCK_TEXT) {
					// multiple block texts indicated embedded containers; no
					// new IStructuredDocumentRegion
					currentNode.addRegion(region);
					currentNode.setLength(region.getStart()
							+ region.getLength() - currentNode.getStart());
					region.adjustStart(-currentNode.getStart());
					// DW 4/16/2003 regions no longer have parents
					// region.setParent(currentNode);
					if (region instanceof ITextRegionContainer) {
						((ITextRegionContainer) region).setParent(currentNode);
					}
				} else {
					// not continuing a IStructuredDocumentRegion
					if (currentNode != null) {
						// ensure that any existing node is at least
						// terminated
						if (!currentNode.isEnded()) {
							currentNode.setLength(region.getStart()
									- currentNode.getStart());
							// fCurrentNode.setTextLength(region.getStart() -
							// fCurrentNode.getStart());
						}
						lastNode = currentNode;
					}
					fireNodeParsed(currentNode);
					currentNode = createStructuredDocumentRegion(type);
					if (lastNode != null) {
						lastNode.setNext(currentNode);
					}
					currentNode.setPrevious(lastNode);
					currentNode.setStart(region.getStart());
					currentNode.setLength(region.getLength());
					// currentNode.setLength(region.getStart() +
					// region.getLength() - currentNode.getStart());
					currentNode.setEnded(true);
					region.adjustStart(-currentNode.getStart());
					currentNode.addRegion(region);
					// DW 4/16/2003 regions no longer have parents
					// region.setParent(currentNode);
					if (region instanceof ITextRegionContainer) {
						((ITextRegionContainer) region).setParent(currentNode);
					}
				}
			}
			// the following contexts OPEN new StructuredDocumentRegions
			else if ((currentNode != null && currentNode.isEnded())
					|| (type == AngularRegionContext.ANGULAR_EXPRESSION_OPEN)
					|| (type == DOMRegionContext.XML_CONTENT)
					|| (type == DOMRegionContext.XML_CHAR_REFERENCE)
					|| (type == DOMRegionContext.XML_ENTITY_REFERENCE)
					|| (type == DOMRegionContext.XML_TAG_OPEN)
					|| (type == DOMRegionContext.XML_END_TAG_OPEN)
					|| (type == DOMRegionContext.XML_COMMENT_OPEN)
					|| (type == DOMRegionContext.XML_CDATA_OPEN)
					|| (type == DOMRegionContext.XML_DECLARATION_OPEN)) {
				if (currentNode != null) {
					// ensure that any existing node is at least terminated
					if (!currentNode.isEnded()) {
						currentNode.setLength(region.getStart()
								- currentNode.getStart());
						// fCurrentNode.setTextLength(region.getStart() -
						// fCurrentNode.getStart());
					}
					lastNode = currentNode;
				}
				fireNodeParsed(currentNode);
				currentNode = createStructuredDocumentRegion(type);
				if (lastNode != null) {
					lastNode.setNext(currentNode);
				}
				currentNode.setPrevious(lastNode);
				currentNode.setStart(region.getStart());
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength()
						- currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}
			}
			// the following contexts neither open nor close
			// StructuredDocumentRegions; just add to them
			else if ((type == DOMRegionContext.XML_TAG_NAME)
					|| (type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
					|| (type == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)
					|| (type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)
					|| (type == DOMRegionContext.XML_COMMENT_TEXT)
					|| (type == DOMRegionContext.XML_PI_CONTENT)
					|| (type == DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET)
					|| (type == AngularTokenizer.ANGULAR_EXPRESSION_CONTENT)) {
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength()
						- currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}
			}
			// the following contexts close off StructuredDocumentRegions
			// cleanly
			else if ((type == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE)
					|| (type == DOMRegionContext.XML_PI_CLOSE)
					|| (type == DOMRegionContext.XML_TAG_CLOSE)
					|| (type == DOMRegionContext.XML_EMPTY_TAG_CLOSE)
					|| (type == DOMRegionContext.XML_COMMENT_CLOSE)
					|| (type == DOMRegionContext.XML_DECLARATION_CLOSE)
					|| (type == DOMRegionContext.XML_CDATA_CLOSE)) {
				currentNode.setEnded(true);
				currentNode.setLength(region.getStart() + region.getLength()
						- currentNode.getStart());
				currentNode.addRegion(region);
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}
			}
			// this is extremely rare, but valid
			else if (type == DOMRegionContext.WHITE_SPACE) {
				ITextRegion lastRegion = currentNode.getLastRegion();
				// pack the embedded container with this region
				if (lastRegion instanceof ITextRegionContainer) {
					ITextRegionContainer container = (ITextRegionContainer) lastRegion;
					container.getRegions().add(region);
					// containers must have parent set ...
					// setting for EACH subregion is redundent, but not sure
					// where else to do, so will do here for now.
					container.setParent(currentNode);
					// DW 4/16/2003 regions no longer have parents
					// region.setParent(container);
					if (region instanceof ITextRegionContainer) {
						((ITextRegionContainer) region).setParent(currentNode);
					}
					region.adjustStart(container.getLength()
							- region.getStart());
				}
				currentNode.getLastRegion().adjustLength(region.getLength());
				currentNode.adjustLength(region.getLength());
			} else if (type == DOMRegionContext.UNDEFINED
					&& currentNode != null) {
				// skip on a very-first region situation as the default
				// behavior is good enough
				// combine with previous if also undefined
				if (currentNode.getLastRegion() != null
						&& currentNode.getLastRegion().getType() == DOMRegionContext.UNDEFINED) {
					currentNode.getLastRegion()
							.adjustLength(region.getLength());
					currentNode.adjustLength(region.getLength());
				}
				// previous wasn't undefined
				else {
					currentNode.addRegion(region);
					currentNode.setLength(region.getStart()
							+ region.getLength() - currentNode.getStart());
					region.adjustStart(-currentNode.getStart());
				}
			} else {
				// if an unknown type is the first region in the document,
				// ensure that a node exists
				if (currentNode == null) {
					currentNode = createStructuredDocumentRegion(type);
					currentNode.setStart(region.getStart());
				}
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength()
						- currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
				if (region instanceof ITextRegionContainer) {
					((ITextRegionContainer) region).setParent(currentNode);
				}
				if (Debug.debugTokenizer)
					System.out
							.println(getClass().getName()
									+ " found region of not specifically handled type " + region.getType() + " @ " + region.getStart() + "[" + region.getLength() + "]"); //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
			}

			// these regions also get their own node, so close them cleanly
			// NOTE: these regions have new StructuredDocumentRegions created
			// for them above; it may
			// be more readable if that is handled here as well, but the
			// current layout
			// ensures that they open StructuredDocumentRegions the same way
			if ((type == DOMRegionContext.XML_CONTENT)
					|| (type == DOMRegionContext.XML_CHAR_REFERENCE)
					|| (type == DOMRegionContext.XML_ENTITY_REFERENCE)
					|| (type == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE)) {
				currentNode.setEnded(true);
			}
			if (headNode == null && currentNode != null) {
				headNode = currentNode;
			}
		}
		if (currentNode != null) {
			fireNodeParsed(currentNode);
			currentNode.setPrevious(lastNode);
		}
		// fStringInput = null;
		primReset();
		return headNode;
	}

	public void reset(Reader reader, int position) {
		super.reset(reader, position);
	}
}
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
package org.eclipse.angularjs.internal.core.documentModel.partitioner;

import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class AngularStructuredTextPartitioner extends
		StructuredTextPartitionerForHTML {

	public String getContentType(final int offset,
			final boolean preferOpenPartitions) {
		final ITypedRegion partition = getPartition(offset);
		return partition == null ? null : partition.getType();
	}

	public String getPartitionType(final ITextRegion region, final int offset) {
		// if Angular region
		if (isAngularRegion(region.getType())) {
			return AngularPartitionTypes.ANGULAR_DEFAULT;
		}

		// else do super
		return super.getPartitionType(region, offset);
	}

	/**
	 * to be abstract eventually
	 */
	protected void initLegalContentTypes() {
		super.initLegalContentTypes();

		final int length = fSupportedTypes.length;
		final String[] types = new String[fSupportedTypes.length + 1];

		System.arraycopy(fSupportedTypes, 0, types, 0, length);
		types[length] = AngularPartitionTypes.ANGULAR_DEFAULT;

		fSupportedTypes = types;
	}

	/**
	 * @param regionType
	 * @return
	 */
	private static final boolean isAngularRegion(final String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_OPEN
				|| regionType == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE
				|| regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT;
	}

	private final static String[] configuredContentTypes = new String[] { AngularPartitionTypes.ANGULAR_DEFAULT
	/*
	 * , FMPartitionTypes.FM_SINGLE_LINE_COMMENT,
	 * FMPartitionTypes.FM_MULTI_LINE_COMMENT, FMPartitionTypes.FM_DOC,
	 * FMPartitionTypes.FM_QUOTED_STRING
	 */};

	public static String[] getConfiguredContentTypes() {
		return configuredContentTypes;
	}

	public static boolean isFMPartitionType(final String type) {
		for (int i = 0; i < configuredContentTypes.length; i++)
			if (configuredContentTypes[i].equals(type))
				return true;
		return false;
	}

	public IDocumentPartitioner newInstance() {
		return new AngularStructuredTextPartitioner();
	}

	public ITypedRegion getPartition(int offset) {

		// in case we are in the end of document
		// we return the partition of last region
		int docLength = fStructuredDocument.getLength();
		if (offset == docLength && offset > 0) {
			return super.getPartition(offset - 1);
		}
		ITypedRegion result = super.getPartition(offset);
		if (result.getType().equals(AngularPartitionTypes.ANGULAR_DEFAULT)) {
			IStructuredDocumentRegion structuredDocumentRegion = fStructuredDocument
					.getRegionAtCharacterOffset(offset);
			if (structuredDocumentRegion.getStartOffset() == offset
					|| ((offset > 0 && structuredDocumentRegion
							.getStartOffset() == offset - 1))) {
				return super.getPartition(offset - 1);
			}
		}

		return result;
	}

	@Override
	public ITypedRegion[] computePartitioning(int offset, int length) {
		// workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=268930
		ITypedRegion[] result = new ITypedRegion[0];
		try {
			result = super.computePartitioning(offset, length);
		} catch (NullPointerException e) {
		}
		return result;
	}
}

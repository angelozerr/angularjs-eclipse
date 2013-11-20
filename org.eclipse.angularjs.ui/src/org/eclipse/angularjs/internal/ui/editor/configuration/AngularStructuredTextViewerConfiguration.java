package org.eclipse.angularjs.internal.ui.editor.configuration;

import org.eclipse.angularjs.internal.core.documentModel.partitioner.AngularPartitionTypes;
import org.eclipse.angularjs.internal.core.documentModel.partitioner.AngularStructuredTextPartitioner;
import org.eclipse.angularjs.internal.ui.editor.highlighter.LineStyleProviderForAngular;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.core.text.IXMLPartitions;

public class AngularStructuredTextViewerConfiguration extends
		StructuredTextViewerConfigurationHTML {

	private String[] configuredContentTypes;
	private LineStyleProvider fLineStyleProvider;

	/*
	 * Returns an array of all the contentTypes (partition names) supported by
	 * this editor. They include all those supported by HTML editor plus
	 * Freemarker.
	 */
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			String[] phpTypes = AngularStructuredTextPartitioner
					.getConfiguredContentTypes();
			String[] xmlTypes = StructuredTextPartitionerForXML
					.getConfiguredContentTypes();
			String[] htmlTypes = StructuredTextPartitionerForHTML
					.getConfiguredContentTypes();
			configuredContentTypes = new String[2 + phpTypes.length
					+ xmlTypes.length + htmlTypes.length];

			configuredContentTypes[0] = IStructuredPartitions.DEFAULT_PARTITION;
			configuredContentTypes[1] = IStructuredPartitions.UNKNOWN_PARTITION;

			int index = 0;
			System.arraycopy(phpTypes, 0, configuredContentTypes, index += 2,
					phpTypes.length);
			System.arraycopy(xmlTypes, 0, configuredContentTypes,
					index += phpTypes.length, xmlTypes.length);
			System.arraycopy(htmlTypes, 0, configuredContentTypes,
					index += xmlTypes.length, htmlTypes.length);
		}

		return configuredContentTypes;
	}

	public LineStyleProvider getLineStyleProvider(ISourceViewer sourceViewer) {
		if (fLineStyleProvider == null) {
			IDOMModel model = (IDOMModel) StructuredModelManager
					.getModelManager()
					.getExistingModelForRead(
							((StructuredTextViewer) sourceViewer).getDocument());
			fLineStyleProvider = new LineStyleProviderForAngular(model);
		}
		return fLineStyleProvider;
	}

	@Override
	public LineStyleProvider[] getLineStyleProviders(
			ISourceViewer sourceViewer, String partitionType) {
		if (partitionType == IHTMLPartitions.HTML_DEFAULT
				|| partitionType == IHTMLPartitions.HTML_COMMENT
				|| partitionType == IHTMLPartitions.HTML_DECLARATION
				|| partitionType == IXMLPartitions.XML_PI
				|| partitionType == AngularPartitionTypes.ANGULAR_DEFAULT) {
			return new LineStyleProvider[] { getLineStyleProvider(sourceViewer) };
		}
		return super.getLineStyleProviders(sourceViewer, partitionType);
	}
}

/**
 *  Copyright (c) 2013-2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.ui.viewers;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import tern.eclipse.ide.ui.views.AbstractTernOutlineLabelProvider;
import tern.server.protocol.outline.IJSNode;
import tern.utils.StringUtils;

/**
 * Label provider for angular outline.
 *
 */
public class AngularOutlineLabelProvider extends AbstractTernOutlineLabelProvider {

	@Override
	protected String getComputingText() {
		return AngularUIMessages.AngularOutline_computing;
	}

	@Override
	protected Image getComputingImage() {
		return ImageResource.getImage(ImageResource.IMG_ANGULARJS);
	}

	@Override
	protected String getText(IJSNode element) {
		return element.getName();
	}

	@Override
	protected Image getImage(IJSNode element) {
		return AngularElementLabelProvider.INSTANCE.getImage(element);
	}

	@Override
	protected StyledString getStyledText(IJSNode element) {
		StyledString buff = new StyledString(StringUtils.isEmpty(element.getName()) ? "" : element.getName());
		String value = element.getValue();
		if (StringUtils.isEmpty(value)) {
			value = element.getFile();
		}
		if (!StringUtils.isEmpty(value)) {
			buff.append(" : ", StyledString.DECORATIONS_STYLER);
			buff.append(value, StyledString.DECORATIONS_STYLER);
		}
		return buff;
	}
}

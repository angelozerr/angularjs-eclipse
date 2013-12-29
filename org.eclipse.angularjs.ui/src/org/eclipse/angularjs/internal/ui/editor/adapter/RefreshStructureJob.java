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

package org.eclipse.angularjs.internal.ui.editor.adapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This job holds a queue of updates (affected nodes) for multiple structured
 * viewers. When a new request comes in, the current run is cancelled, the new
 * request is added to the queue, then the job is re-scheduled. Support for
 * multiple structured viewers is required because refresh updates are usually
 * triggered by model changes, and the model may be visible in more than one
 * viewer.
 * 
 * NOTICE: This class is exactly the same as
 * org.eclipse.wst.xml.ui.internal.contentoutline.RefreshStructureJob The
 * difference is in the function doRefresh - the explanation is provided there.
 * 
 * @author guy.g
 */
class RefreshStructureJob extends Job {

	/** debug flag */
	static final boolean DEBUG;
	private static final long UPDATE_DELAY = 250;
	static {
		String value = Platform
				.getDebugOption("org.eclipse.wst.sse.ui/debug/refreshStructure"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	/** List of refresh requests (Nodes) */
	private final List fRequests;
	/** the structured viewers */
	List fViewers = new ArrayList(3);

	public RefreshStructureJob() {
		super(XMLUIMessages.refreshoutline_0); //$NON-NLS-1$
		setPriority(Job.LONG);
		setSystem(true);
		fRequests = new ArrayList(2);
	}

	private synchronized void addRequest(Node node) {
		int size = fRequests.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				/*
				 * If we already have a request which contains the new request,
				 * discard the new request
				 */
				Node node2 = (Node) fRequests.get(i);
				if (contains(node2, node))
					return;
				/*
				 * If new request contains any existing requests, replace it
				 * with new request
				 */
				if (contains(node, node2)) {
					fRequests.set(i, node);
					return;
				}
			}
		} else {
			fRequests.add(node);
		}
	}

	private synchronized void addViewer(StructuredViewer viewer) {
		if (!fViewers.contains(viewer)) {
			fViewers.add(viewer);
		}
	}

	/**
	 * @param root
	 * @param possible
	 * @return if the root is parent of possible, return true, otherwise return
	 *         false
	 */
	private boolean contains(Node root, Node possible) {
		if (DEBUG) {
			System.out
					.println("=============================================================================================================="); //$NON-NLS-1$
			System.out
					.println("recursive call w/ root: " + root.getNodeName() + " and possible: " + possible); //$NON-NLS-1$ //$NON-NLS-2$
			System.out
					.println("--------------------------------------------------------------------------------------------------------------"); //$NON-NLS-1$
		}

		// the following checks are important
		// #document node will break the algorithm otherwise

		// can't contain the parent if it's null
		if (root == null) {
			if (DEBUG)
				System.out.println("returning false: root is null"); //$NON-NLS-1$
			return false;
		}
		// nothing can be parent of Document node
		if (possible instanceof Document) {
			if (DEBUG)
				System.out
						.println("returning false: possible is Document node"); //$NON-NLS-1$
			return false;
		}
		// document contains everything
		if (root instanceof Document) {
			if (DEBUG)
				System.out.println("returning true: root is Document node"); //$NON-NLS-1$
			return true;
		}

		// depth first
		Node current = root;
		// loop siblings
		while (current != null) {
			if (DEBUG)
				System.out
						.println("   -> iterating sibling (" + current.getNodeName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			// found it
			if (possible.equals(current)) {
				if (DEBUG)
					System.out
							.println("   !!! found: " + possible.getNodeName() + " in subtree for: " + root.getNodeName()); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			// drop one level deeper if necessary
			if (current.getFirstChild() != null) {
				return contains(current.getFirstChild(), possible);
			}
			current = current.getNextSibling();
		}
		// never found it
		return false;
	}

	/**
	 * Refresh must be on UI thread because it's on a SWT widget.
	 * 
	 * The deferance in the functionallty is this: If we are refreshing the
	 * outline when its in AngularOutlineContentProvider.MODE_Angular mode then
	 * it doesn't metter which node was changed (unless is not php node) - we
	 * need to refresh the whole tree. otherwise - (using the default behaivor)
	 * refreshing only the relevent items in the tree.
	 * 
	 * @param node
	 */
	private void doRefresh(final Node node, final StructuredViewer[] viewers) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (DEBUG)
					System.out
							.println("refresh on: [" + node.getNodeName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

				for (int i = 0; i < viewers.length; i++) {
					if (!viewers[i].getControl().isDisposed()) {
						StructuredViewer viewer = viewers[i];

						viewers[i].refresh(node, true);

					} else {
						if (DEBUG)
							System.out
									.println("   !!! skipped refreshing disposed viewer: " + viewers[i]); //$NON-NLS-1$
					}
				}
			}
		});
	}

	/**
	 * This method also synchronized because it accesses the fRequests queue and
	 * fViewers list
	 * 
	 * @return an array containing and array of the currently requested Nodes to
	 *         refresh and the viewers in which to refresh them
	 */
	private synchronized Object[] getRequests() {
		Node[] toRefresh = (Node[]) fRequests
				.toArray(new Node[fRequests.size()]);
		fRequests.clear();

		StructuredViewer[] viewers = (StructuredViewer[]) fViewers
				.toArray(new StructuredViewer[fViewers.size()]);
		fViewers.clear();

		return new Object[] { toRefresh, viewers };
	}

	/**
	 * Invoke a refresh on the viewer on the given node.
	 * 
	 * @param node
	 */
	public void refresh(StructuredViewer viewer, Node node) {
		if (node == null)
			return;

		addViewer(viewer);
		addRequest(node);
		schedule(UPDATE_DELAY);
	}

	protected IStatus run(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			// Retrieve BOTH viewers and Nodes on one block
			Object[] requests = getRequests();
			Node[] nodes = (Node[]) requests[0];
			StructuredViewer[] viewers = (StructuredViewer[]) requests[1];

			for (int i = 0; i < nodes.length; i++) {
				if (monitor.isCanceled())
					throw new OperationCanceledException();
				doRefresh(nodes[i], viewers);
			}
		} finally {
			monitor.done();
		}
		return status;
	}

}

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

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implementing the XML model notifier, and adjusting it to to the PHP model.
 * IMPORTANT: any fix in the XMLModelNotifierImpl should be added to this class
 * also !!! (valueChanged() method was fixed)
 * 
 * @author Roy, 2006
 */

/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class AngularModelNotifier implements XMLModelNotifier {

	/* end: for debugging only */
	private class NotifyEvent {
		Object changedFeature;
		boolean discarded;
		Object newValue;
		// note: don't initialize instance variables, since
		// that causes double assignments, and lots of these are created.
		INodeNotifier notifier;
		Object oldValue;
		int pos;
		String reason;
		int type;

		NotifyEvent(INodeNotifier notifier, int type, Object changedFeature,
				Object oldValue, Object newValue, int pos) {
			this.notifier = notifier;
			this.type = type;
			this.changedFeature = changedFeature;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.pos = pos;
			this.reason = ""; //$NON-NLS-1$
		}
	}

	private final static String ADDED_THEN_REMOVED = "Discard: Added then removed rule"; //$NON-NLS-1$
	private final static boolean fOptimizeDeferred = true;
	private final static boolean fOptimizeDeferredAccordingToParentAdded = true;
	private final static boolean fOptimizeDeferredAccordingToParentRemoved = true;
	private final static String PARENT_IS_ADDED = "Disarded: Parent has just been added"; //$NON-NLS-1$
	/* start: for debugging only */
	private final static String PARENT_IS_REMOVED_TOO = "Discard: Parent was removed too"; //$NON-NLS-1$
	private final static String PARENT_IS_REPARENTED = "Not Discard: Parent was removed so this implies reparenting"; //$NON-NLS-1$
	private Node changedRoot = null;

	private boolean changing = false;
	private boolean doingNewModel = false;
	private Vector events = null;
	private boolean flushing = false;

	/**
	 */
	public AngularModelNotifier() {
		super();
	}

	/**
	 * attrReplaced method
	 * 
	 * @param element
	 *            org.w3c.dom.Element
	 * @param newAttr
	 *            org.w3c.dom.Attr
	 * @param oldAttr
	 *            org.w3c.dom.Attr
	 */
	public void attrReplaced(Element element, Attr newAttr, Attr oldAttr) {
		if (element == null)
			return;
		Attr attr = null;
		String oldValue = null;
		String newValue = null;
		if (oldAttr != null) {
			attr = oldAttr;
			oldValue = oldAttr.getValue();
		}
		if (newAttr != null) {
			attr = newAttr;
			newValue = newAttr.getValue();
		}
		IDOMNode notifier = (IDOMNode) element;
		int offset = notifier.getStartOffset();
		notify(notifier, INodeNotifier.CHANGE, attr, oldValue, newValue, offset);
		propertyChanged(notifier);
	}

	/**
	 */
	public void beginChanging() {
		this.changing = true;
	}

	/**
	 */
	public void beginChanging(boolean newModel) {
		beginChanging();
		this.doingNewModel = newModel;
	}

	public void cancelPending() {
		// we don't want to change the size of this array, since
		// the array may be being processed, in the defferred notification
		// loop, but we can signal that all
		// should be discarded, so any remaining ones will be ignored.
		if (this.events != null) {
			Iterator iterator = this.events.iterator();
			while (iterator.hasNext()) {
				NotifyEvent event = (NotifyEvent) iterator.next();
				event.discarded = true;
			}
		}
		// this cancel is presumably being called as a function of
		// "reinitiailization" so we can ignore changes to the
		// old root, and changes to the new one will be triggered during
		// reinitialization.
		changedRoot = null;
	}

	/**
	 * childReplaced method
	 * 
	 * @param parentNode
	 *            org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	public void childReplaced(Node parentNode, Node newChild, Node oldChild) {
		if (parentNode == null)
			return;
		IDOMNode notifier = (IDOMNode) parentNode;
		int type = INodeNotifier.CHANGE;
		if (newChild == null)
			type = INodeNotifier.REMOVE;
		else if (oldChild == null)
			type = INodeNotifier.ADD;
		int offset = notifier.getStartOffset();
		notify(notifier, type, oldChild, oldChild, newChild, offset);
		structureChanged(notifier);
	}

	public void editableChanged(Node node) {
		if (node == null)
			return;
		IDOMNode notifier = (IDOMNode) node;
		int offset = notifier.getStartOffset();
		notify(notifier, INodeNotifier.CHANGE, null, null, null, offset);
		propertyChanged(notifier);
	}

	/**
	 */
	public void endChanging() {
		this.doingNewModel = false;
		if (!this.changing)
			return; // avoid nesting calls
		notifyDeferred();
		if (this.changedRoot != null) {
			notifyStructureChanged(this.changedRoot);
			if (Debug.debugNotifyDeferred) {
				String p = this.changedRoot.getNodeName();
				System.out.println("Deferred STRUCUTRE_CHANGED: " + p); //$NON-NLS-1$
			}
			this.changedRoot = null;
		}
		this.changing = false;
	}

	/**
	 */
	public void endTagChanged(Element element) {
		if (element == null)
			return;
		IDOMNode notifier = (IDOMNode) element;
		int offset = notifier.getStartOffset();
		notify(notifier, INodeNotifier.CHANGE, null, null, null, offset);
		propertyChanged(element);
	}

	/**
	 */
	public boolean hasChanged() {
		return (this.events != null);
	}

	/**
	 */
	public boolean isChanging() {
		return this.changing;
	}

	/**
	 */
	private void notify(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (notifier == null)
			return;
		if (this.changing && !this.flushing) {
			// defer notification
			if (this.events == null)
				this.events = new Vector();
			// we do not defer anything if we are doing a new Model,
			// except for the document event, since all others are
			// trivial and not needed at that initial point.
			// But even for that one document event, in the new model case,
			// it is still important to defer it.
			if ((!doingNewModel)
					|| (((Node) notifier).getNodeType() == Node.DOCUMENT_NODE)) {
				this.events.addElement(new NotifyEvent(notifier, eventType,
						changedFeature, oldValue, newValue, pos));
			}
			return;
		}
		try {
			// Its important to "keep going" if exception occurs, since this
			// notification
			// comes in between "about to change" and "changed" events. We do
			// log, however,
			// since would indicate a program error.
			notifier.notify(eventType, changedFeature, oldValue, newValue, pos);
		} catch (Exception e) {
			Logger.logException(
					"A structured model client threw following exception during adapter notification (" + INodeNotifier.EVENT_TYPE_STRINGS[eventType] + " )", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 */
	private void notifyDeferred() {
		if (this.events == null)
			return;
		if (this.flushing)
			return;
		this.flushing = true; // force notification
		int count = this.events.size();
		for (int i = 0; i < count; i++) {
			NotifyEvent event = (NotifyEvent) this.events.elementAt(i);
			if (event == null)
				continue; // error
			if (event.discarded)
				continue;
			if (!doingNewModel && fOptimizeDeferred) {
				// check redundant events (no need to check if doing NewModel,
				// since
				// shouldn't be redunancies)
				if (event.type == INodeNotifier.ADD) {
					for (int n = i + 1; n < count; n++) {
						NotifyEvent next = (NotifyEvent) this.events
								.elementAt(n);
						if (next == null)
							continue; // error
						if (next.type == INodeNotifier.REMOVE
								&& next.oldValue == event.newValue) {
							// Added then removed later, discard both
							event.discarded = true;
							next.discarded = true;
							if (Debug.debugNotifyDeferred) {
								event.reason = event.reason
										+ ADDED_THEN_REMOVED
										+ "(see " + n + ")"; //$NON-NLS-1$ //$NON-NLS-2$
								next.reason = next.reason + ADDED_THEN_REMOVED
										+ "(see " + i + ")"; //$NON-NLS-1$ //$NON-NLS-2$
							}
							break;
						}
					}
					if (event.discarded)
						continue;
					if (fOptimizeDeferredAccordingToParentAdded) {
						for (int p = 0; p < i; p++) {
							NotifyEvent prev = (NotifyEvent) this.events
									.elementAt(p);
							if (prev == null)
								continue; // error
							if (prev.type == INodeNotifier.REMOVE
									&& prev.oldValue == event.notifier) {
								// parent is reparented, do not discard
								if (Debug.debugNotifyDeferred) {
									event.reason = event.reason
											+ PARENT_IS_REPARENTED
											+ "(see " + p + ")"; //$NON-NLS-1$ //$NON-NLS-2$
								}
								break;
							} else if (prev.type == INodeNotifier.ADD
									&& prev.newValue == event.notifier) {
								// parent has been added, discard this
								event.discarded = true;
								if (Debug.debugNotifyDeferred) {
									event.reason = event.reason
											+ PARENT_IS_ADDED
											+ "(see " + p + ")"; //$NON-NLS-1$ //$NON-NLS-2$
								}
								break;
							}
						}
						if (event.discarded)
							continue;
					}
				} else if (event.type == INodeNotifier.REMOVE) {
					if (fOptimizeDeferredAccordingToParentRemoved) {
						for (int n = i + 1; n < count; n++) {
							NotifyEvent next = (NotifyEvent) this.events
									.elementAt(n);
							if (next == null)
								continue; // error
							if (next.type == INodeNotifier.REMOVE) {
								if (next.oldValue == event.notifier) {
									// parent will be removed, discard this
									event.discarded = true;
									if (Debug.debugNotifyDeferred) {
										event.reason = event.reason
												+ PARENT_IS_REMOVED_TOO
												+ "(see " + n + ")"; //$NON-NLS-1$ //$NON-NLS-2$
									}
									break;
								}
							}
						}
						if (event.discarded)
							continue;
					}
				}
			}
			notify(event.notifier, event.type, event.changedFeature,
					event.oldValue, event.newValue, event.pos);
		}
		if (Debug.debugNotifyDeferred) {
			for (int l = 0; l < count; l++) {
				NotifyEvent event = (NotifyEvent) this.events.elementAt(l);
				Object o = null;
				String t = null;
				if (event.type == INodeNotifier.ADD) {
					o = event.newValue;
					t = " + "; //$NON-NLS-1$
				} else if (event.type == INodeNotifier.REMOVE) {
					o = event.oldValue;
					t = " - "; //$NON-NLS-1$
				}
				if (o instanceof Element) {
					String p = ((Node) event.notifier).getNodeName();
					String c = ((Node) o).getNodeName();
					String d = (event.discarded ? "! " : "  "); //$NON-NLS-1$ //$NON-NLS-2$
					System.out.println(d + p + t + c);
				}
			}
		}
		this.flushing = false;
		this.events = null;
	}

	/**
	 */
	private void notifyStructureChanged(Node root) {
		if (root == null)
			return;
		INodeNotifier notifier = (INodeNotifier) root;
		try {
			// Its important to "keep going" if exception occurs, since this
			// notification
			// comes in between "about to change" and "changed" events. We do
			// log, however,
			// since would indicate a program error.
			notifier.notify(INodeNotifier.STRUCTURE_CHANGED, null, null, null,
					-1);
		} catch (Exception e) {
			Logger.logException(
					"A structured model client threw following exception during adapter notification (" + INodeNotifier.EVENT_TYPE_STRINGS[INodeNotifier.STRUCTURE_CHANGED] + " )", e); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 */
	public void propertyChanged(Node node) {
	}

	/**
	 * @param node
	 */
	private void setCommonRootIfNeeded(Node node) {
		// defer notification
		if (this.changedRoot == null) {
			this.changedRoot = node;
		} else {
			// tiny optimization: if previous commonAncestor (changedRoot) is
			// already 'document',
			// or if already equal to this 'node',
			// then no need to re-calculate
			if (changedRoot.getNodeType() != Node.DOCUMENT_NODE
					&& changedRoot != node) {
				Node common = ((NodeImpl) this.changedRoot)
						.getCommonAncestor(node);
				if (common != null)
					this.changedRoot = common;
				else
					this.changedRoot = node;
			}
		}
	}

	/**
	 */
	public void startTagChanged(Element element) {
		if (element == null)
			return;
		IDOMNode notifier = (IDOMNode) element;
		int offset = notifier.getStartOffset();
		notify(notifier, INodeNotifier.CHANGE, null, null, null, offset);
		propertyChanged(element);
	}

	/**
	 */
	public void structureChanged(Node node) {
		if (node == null)
			return;
		if (isChanging()) {
			setCommonRootIfNeeded(node);
			if (Debug.debugNotifyDeferred) {
				String p = this.changedRoot.getNodeName();
				System.out.println("requested STRUCUTRE_CHANGED: " + p); //$NON-NLS-1$
			}
			return;
		}
		if (Debug.debugNotifyDeferred) {
			String p = node.getNodeName();
			System.out.println("STRUCUTRE_CHANGED: " + p); //$NON-NLS-1$
		}
		notifyStructureChanged(node);
	}

	/**
	 * valueChanged method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	public void valueChanged(Node node) {
		if (node == null)
			return;
		IDOMNode notifier = null;
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			Attr attr = (Attr) node;
			notifier = (IDOMNode) attr.getOwnerElement();
			// TODO_dmw: experimental: changed 06/29/2004 to send "strucuture
			// changed" even for attribute value changes
			// there are pros and cons to considering attribute value
			// "structure changed". Will (re)consider
			// setCommonRootIfNeeded(notifier);
			if (notifier == null)
				return;
			String value = attr.getValue();
			int offset = notifier.getStartOffset();
			notify(notifier, INodeNotifier.CHANGE, attr, null, value, offset);
		} else {
			// note: we do not send structured changed event for content
			// changed
			notifier = (IDOMNode) node;
			// PHP ELEMENTS CHANGE: meaning - do not optimize php elements!
			final boolean isPhpContent = node.getNodeType() == Node.TEXT_NODE
					&& ((TextImpl) node).getFirstStructuredDocumentRegion()
							.getType() == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT;
			String value = isPhpContent ? null : node.getNodeValue();
			// end of change
			int offset = notifier.getStartOffset();
			notify(notifier, INodeNotifier.CHANGE, null, null, value, offset);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				IDOMNode parent = (IDOMNode) node.getParentNode();
				if (parent != null) {
					notify(parent, INodeNotifier.CONTENT_CHANGED, node, null,
							value, offset);
				}
			}
		}
		propertyChanged(notifier);
	}
}

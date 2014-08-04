package org.eclipse.angularjs.internal.ui.utils;

public class AngularELRegion {

	private final String expression;
	private final int expressionOffset;

	public AngularELRegion(String expression, int expressionOffset) {
		this.expression = expression;
		this.expressionOffset = expressionOffset;
	}

	public String getExpression() {
		return expression;
	}

	public int getExpressionOffset() {
		return expressionOffset;
	}

}

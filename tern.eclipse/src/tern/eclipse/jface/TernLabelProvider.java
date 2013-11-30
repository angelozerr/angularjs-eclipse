package tern.eclipse.jface;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TernLabelProvider extends LabelProvider {

	private static final ILabelProvider INSTANCE = new TernLabelProvider();

	public static ILabelProvider getInstance() {
		return INSTANCE;
	}

	@Override
	public String getText(Object element) {
		// TODO Auto-generated method stub
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return super.getImage(element);
	}

}

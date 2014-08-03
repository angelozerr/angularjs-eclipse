package org.eclipse.angularjs.internal.ui;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class JavaWordFinder {

  public static IRegion findWord(IDocument document, int offset) {

    int start= -2;
    int end= -1;

    try {
      int pos= offset;
      char c;

      while (pos >= 0) {
        c= document.getChar(pos);
        if (!Character.isJavaIdentifierPart(c))
          break;
        --pos;
      }
      start= pos;

      pos= offset;
      int length= document.getLength();

      while (pos < length) {
        c= document.getChar(pos);
        if (!Character.isJavaIdentifierPart(c))
          break;
        ++pos;
      }
      end= pos;

    } catch (BadLocationException x) {
    }

    if (start >= -1 && end > -1) {
      if (start == offset && end == offset)
        return new Region(offset, 0);
      else if (start == offset)
        return new Region(start, end - start);
      else
        return new Region(start + 1, end - start - 1);
    }

    return null;
  }
}
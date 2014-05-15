/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * The code is copied from com.google.gdt.core plug-in and compiled from the 
 * following classes:
 * - com.google.gdt.eclipse.core.ContentDescriberUtilities
 * - com.google.gdt.eclipse.core.SseUtilities
 * - com.google.gdt.eclipse.core.ResourceUtils
 * 	
 *******************************************************************************/
package org.eclipse.angularjs.internal.core.content.xpl;


import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.content.LazyInputStream;
import org.eclipse.core.internal.content.LazyReader;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * Utility methods useful for
 * {@link org.eclipse.core.runtime.content.IContentDescriber} implementations.
 */
@SuppressWarnings("restriction")
public class ContentDescriberUtilities {

  /**
   * Returns an {@link IFile} for the file backing an input stream. This method
   * is tailored to work with
   * {@link org.eclipse.core.runtime.content.IContentDescriber}, using it
   * elsewhere will likely not work.
   * 
   * @return the filename, or null if it could not be determined
   */
  public static IFile resolveFileFromInputStream(
      InputStream contentInputStream) {
    try {
  
      if (!(contentInputStream instanceof LazyInputStream)) {
        return null;
      }
  
      Class<?> c = contentInputStream.getClass();
  
      Field in = c.getDeclaredField("in");
      in.setAccessible(true);
      Object lazyFileInputStreamObj = in.get(contentInputStream);
  
      if (lazyFileInputStreamObj == null) {
        return null;
      }
  
      if (!Class.forName(
          "org.eclipse.core.internal.resources.ContentDescriptionManager$LazyFileInputStream").isAssignableFrom(
          lazyFileInputStreamObj.getClass())) {
        return null;
      }
  
      Field target = lazyFileInputStreamObj.getClass().getDeclaredField(
          "target");
      target.setAccessible(true);
      Object fileStoreObj = target.get(lazyFileInputStreamObj);
      if (fileStoreObj == null) {
        return null;
      }
  
      if (!(fileStoreObj instanceof IFileStore)) {
        return null;
      }
  
      IFileStore fileStore = (IFileStore) fileStoreObj;
  
      String name = fileStore.getName();
  
      if (name == null || name.length() == 0) {
        return null;
      }
  
      IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(
          fileStore.toURI());
      return files.length > 0 ? files[0] : null;
  
    } catch (Throwable e) {
      // Ignore on purpose
    }
  
    return null;
  }

  /**
   * Gets an {@link IFile} for the file backing a reader. This method is
   * tailored to work with
   * {@link org.eclipse.core.runtime.content.IContentDescriber}, using it
   * elsewhere will likely not work.
   * 
   * @return the filename, or null if it could not be determined
   */
  public static IFile resolveFileFromReader(Reader reader) {
    try {
  
      if (!(reader instanceof LazyReader)) {
        return null;
      }
  
      Class<?> c = reader.getClass();
  
      Field in = c.getDeclaredField("in");
      in.setAccessible(true);
      Object documentReaderObj = in.get(reader);
  
      if (documentReaderObj == null) {
        return null;
      }
  
      if (!Class.forName("org.eclipse.core.internal.filebuffers.DocumentReader").isAssignableFrom(
          documentReaderObj.getClass())) {
        return null;
      }
  
      Field documentField = documentReaderObj.getClass().getDeclaredField(
          "fDocument");
      documentField.setAccessible(true);
      Object documentObj = documentField.get(documentReaderObj);
      if (documentObj == null) {
        return null;
      }
  
      if (!(documentObj instanceof IDocument)) {
        return null;
      }
  
      IDocument document = (IDocument) documentObj;
      return resolveFile(document);
  
    } catch (Throwable e) {
      // Ignore on purpose
    }
  
    return null;
  }
  
  //
  // The following code is copied from com.google.gdt.eclipse.core.SseUtilities
  //
  
  /**
   * Resolves the file that is associated with the given document.
   * 
   * @return an IFile pointing to the file in the workspace, or null if one
   *         could not be resolved.
   */
  private static IFile resolveFile(IDocument document) {
    IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(
        document);
    try {
      return resolveFile(model);
    } finally {
      model.releaseFromRead();
    }
  }

  /**
   * Resolves the file that is associted with the given model.
   * 
   * @return an IFile pointing to the file in the workspace, or null if one
   *         could not be resolved.
   */
  private static IFile resolveFile(IStructuredModel model) {
    return (IFile) getResource(new Path(model.getBaseLocation()));
  }
  
  //
  // The following code is copied from com.google.gdt.eclipse.core.ResourceUtils
  //
  
  /**
   * Returns a resource for the given absolute or workspace-relative path.
   * <p>
   * If the path has a device (e.g. c:\ on Windows), it will be tried as an
   * absolute path. Otherwise, it is first tried as a workspace-relative path,
   * and failing that an absolute path.
   * 
   * @param path the absolute or workspace-relative path to a resource
   * @return the resource, or null
   */
  private static IResource getResource(IPath path) {
    IResource res = null;
    if (path != null) {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      if (path.getDevice() == null) {
        // try searching relative to the workspace first
        res = root.findMember(path);
      }

      if (res == null) {
        // look for files
        IFile[] files = root.findFilesForLocation(path);
        // Check for accessibility since for directories, the above will return
        // a non-accessible IFile
        if (files.length > 0 && files[0].isAccessible()) {
          res = files[0];
        }
      }

      if (res == null) {
        // look for folders
        IContainer[] containers = root.findContainersForLocation(path);
        if (containers.length > 0) {
          res = containers[0];
        }
      }
    }
    return res;
  }

}

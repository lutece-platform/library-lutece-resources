/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * Modifications and adaptations for the Lutece framework by City of Paris, 2024.
 */
package fr.paris.lutece.plugins.resource;


import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import fr.paris.lutece.plugins.resource.loader.FileResourceCreationException;
import fr.paris.lutece.plugins.resource.loader.ResourceNotFoundException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public interface ResourceManager {

    /**
     * Retrieves a resource as an InputStream using the specified resource name.
     *
     * @param name the name of the resource to retrieve
     * @return an InputStream representing the resource's contents
     * @throws ResourceNotFoundException if the resource cannot be found
     */
    InputStream getResourceAsInputStream(String name) throws ResourceNotFoundException;

    /**
     * Retrieves a resource as a File using the specified resource name.
     *
     * @param name the name of the resource to retrieve
     * @return a File object representing the resource's contents
     * @throws ResourceNotFoundException if the resource cannot be found
     * @throws FileResourceCreationException if there is an error creating the File
     */
    File getResourceAsFile(String name) throws ResourceNotFoundException, FileResourceCreationException;

    /**
     * Retrieves a resource as a File and saves it with the specified output filename.
     *
     * @param name the name of the resource to retrieve
     * @param outputFile the name to save the resource as
     * @return a File object representing the resource's contents
     * @throws ResourceNotFoundException if the resource cannot be found
     * @throws FileResourceCreationException if there is an error creating the File
     */
    File getResourceAsFile(String name, String outputFile)
            throws ResourceNotFoundException, FileResourceCreationException;

    /**
     * Sets the output directory where resources will be stored.
     *
     * @param outputDirectory the directory to set as the output location for resources
     */
    void setOutputDirectory(File outputDirectory);

    /**
     * Adds a search path for the resource loader with the specified ID.
     *
     * @param resourceLoaderId the ID of the resource loader
     * @param searchPath the path to add to the resource loader's search paths
     */
    void addSearchPath(String resourceLoaderId, String searchPath);

    /**
     * Searches for a resource with the given name and returns a corresponding LuteceResource object.
     *
     * @param name the name of the resource to search for
     * @return the LuteceResource found with the specified name
     * @throws ResourceNotFoundException if the resource cannot be found
     */
    LuteceResource getResource(String name) throws ResourceNotFoundException;

    /**
     * Retrieves a file with the given resource's contents. If the resource is already available as a file,
     * returns that file. Otherwise, a file in the resource manager's output directory is created,
     * and the resource is downloaded to that file.
     *
     * @param resource the LuteceResource to retrieve as a file
     * @return a File object representing the resource's contents
     * @throws FileResourceCreationException if there is an error creating the File
     */
    File getResourceAsFile(LuteceResource resource) throws FileResourceCreationException;

    /**
     * Downloads the specified resource to the given output file.
     *
     * @param resource the LuteceResource to download
     * @param outputFile the File to which the resource will be downloaded
     * @throws FileResourceCreationException if there is an error creating the File
     */
    void createResourceAsFile(LuteceResource resource, File outputFile) throws FileResourceCreationException;
    /**
     * Retrieves the resource paths as a Set<URL> from a given directory.
     * This method reads the files in the specified directory, converts their paths to URLs,
     * and returns them in a Set. If no resources are found, a {@link ResourceNotFoundException}
     * will be thrown.
     *
     * @param path The base path in the file system, which can be either relative or absolute.
     *                 This path is used to locate the directory containing the resources.
     *                 The path must start with a "/".
     * @return A Set containing the URLs of the resources found in the directory.
     * @throws ResourceNotFoundException If no resources are found or if an error occurs while reading
     *                                    the directory or converting paths to URLs.
     */
    Set<URL> getResourceURL(String path) throws ResourceNotFoundException;
}
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
package fr.paris.lutece.plugins.resource.loader;

import java.net.URL;
import java.util.Set;

import fr.paris.lutece.plugins.resource.LuteceResource;
/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ResourceLoader {
	
	  /**
     * The default configuration ordinal value, {@code 100}.
     */
    int DEFAULT_ORDINAL = 100;
    
    void addSearchPath(String path);

    /**
     * Returns the resource with the given name.
     *
     * @param name The resources name.
     * @return The resource with the given name.
     * @throws ResourceNotFoundException The resource wasn't found, or wasn't available.
     */
    LuteceResource getResource(String name) throws ResourceNotFoundException;
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
    /**
     * Returns the Identifier Named class
     * @return Identifier Named loader
     */
    String getId();
    /**
     * Return the ordinal priority value of this configuration source.
     * <p>
     * If a resource can be loaded by multiple resource loader, the value in the resource loader with the highest ordinal
     * takes precedence. For resource loader with the same ordinal value, the  resource loader name will be
     * used for sorting according to string sorting criteria.
     * <p>
     * Note that this method is only evaluated during the construction of the resource manager, and does not affect the
     * ordering of configuration sources within a configuration after that time.
     * <p>
     * The ordinal values for the default configuration sources can be found
     * <a href="#default_config_sources">above</a>.
     * <p>
     * The default implementation of this method looks for a configuration property named "{@link #CONFIG_ORDINAL
     * config_ordinal}" to determine the ordinal value for this configuration source. If the property is not found, then
     * the {@linkplain #DEFAULT_ORDINAL default ordinal value} is used.
     * <p>
     * This method may be overridden by source loader implementations to provide a different behavior.
     *
     * @return the ordinal value
     */
    default int getOrdinal() {
        return DEFAULT_ORDINAL;
    }
}

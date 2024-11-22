package fr.paris.lutece.plugins.resource.loader;
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

import jakarta.inject.Inject;
import jakarta.inject.Named;

import fr.paris.lutece.plugins.resource.LuteceResource;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
@Named(ThreadContextClasspathResourceLoader.ID)
@ApplicationScoped
public class ThreadContextClasspathResourceLoader extends AbstractResourceLoader {
    public static final String ID = "classloader";
    @Inject
    @ConfigProperty(name="ordinalValue.resourceLoader.classloader", defaultValue="400")
    private Integer ordinalValue;

    // ----------------------------------------------------------------------
    // ResourceLoader Implementation
    // ----------------------------------------------------------------------

    @Override
    public LuteceResource getResource(String name) throws ResourceNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) {
            throw new ResourceNotFoundException(name);
        }

        if (name != null && name.startsWith("/")) {
            name = name.substring(1);
        }

        final URL url = classLoader.getResource(name);
        if (url == null) {
            throw new ResourceNotFoundException(name);
        }

        return new URLLuteceResource(url);
    }
    @Override
	public Set<URL> getResourceURL(String path) throws ResourceNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
            throw new ResourceNotFoundException(path);
        }

        if (path != null && path.startsWith("/")) {
        	path = path.substring(1);
        }
		if (path != null && !path.endsWith("/")) {
			path += "/";
        }
        Enumeration<URL> urls;
		try {
			urls = classLoader.getResources(path);
		} catch (IOException e) {
            throw new ResourceNotFoundException(path, e);
		}
		if( urls == null) {
            throw new ResourceNotFoundException(path);
		}
        Set<URL> resourcePaths = new HashSet<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            resourcePaths.add(url);
        }
        if(resourcePaths.isEmpty()) {
            throw new ResourceNotFoundException(path);
        }
        return resourcePaths;
	}
    @Override
	public String getId() {
		return ID;
	}
    @Override
    public int getOrdinal() {
        return ordinalValue;
    }
	
}

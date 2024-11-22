
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
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.resource.LuteceResource;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
@Named(FileResourceLoader.ID)
@Dependent
public class FileResourceLoader extends AbstractResourceLoader {
    public static final String ID = "file";

    @Inject
    @ConfigProperty(name="ordinalValue.resourceLoader.file", defaultValue="300")
    private Integer ordinalValue;
    // ----------------------------------------------------------------------
    // ResourceLoader Implementation
    // ----------------------------------------------------------------------

    @Override
    public LuteceResource getResource(String name) throws ResourceNotFoundException {
        for (String path : paths) {
            final File file = new File(path, name);

            if (file.canRead()) {
                return new FileLuteceResource(file);
            }
        }
        File file = new File(name);
        if (file.isAbsolute() && file.canRead()) {
            return new FileLuteceResource(file);
        }
        throw new ResourceNotFoundException(name);
    }

	@Override
	public String getId() {
		return ID;
	}
	@Override
	public int getOrdinal() {
	    return ordinalValue;
	}

	@Override
	public Set<URL> getResourceURL(String basePath) throws ResourceNotFoundException {		
		String pathDir=basePath ;
		for (String p : paths) {
            final File file = new File(p, basePath);

            if (file.canRead()) {
            	pathDir = p+basePath; 
            	 break;
            }
        }
		Path path = Paths.get(pathDir);
        Set<URL> listUrl = new HashSet<>();
		try (Stream<Path> stream = Files.walk(path, 1)) {
            Set<Path> resources = stream
                .filter(Files::isRegularFile)
                .collect(Collectors.toSet());

            if (resources.isEmpty()) {
                throw new ResourceNotFoundException("No resources found in the path : " + basePath);
            }

            for(Path rec: resources) {
            	listUrl.add(toURL(rec));
            }            
            return listUrl;

        } catch (IOException e) {
            throw new ResourceNotFoundException("Error while reading files in the path : " + basePath, e);
        }
   }
	private  URL toURL(Path path) throws ResourceNotFoundException {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Error during path conversion to URL : " + path, e);
        }
    }
}

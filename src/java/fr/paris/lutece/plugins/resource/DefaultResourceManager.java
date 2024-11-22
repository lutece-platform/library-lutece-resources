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


import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.paris.lutece.plugins.resource.loader.FileResourceCreationException;
import fr.paris.lutece.plugins.resource.loader.ResourceIOException;
import fr.paris.lutece.plugins.resource.loader.ResourceLoader;
import fr.paris.lutece.plugins.resource.loader.ResourceNotFoundException;
import fr.paris.lutece.plugins.resource.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
@Dependent
public class DefaultResourceManager implements ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger( DefaultResourceManager.class);

    private final Map<String, ResourceLoader> resourceLoaders= new LinkedHashMap<>();

    @Inject @Any
    private Instance<ResourceLoader> resourceLoaderList;
    
    private File outputDirectory;

    @PostConstruct
    public void produceResourceLoaders() {
    	// Sorts the list of ResourceLoaders by their ordinal value
    	List<ResourceLoader> sortedLoaders = new ArrayList<>();
        resourceLoaderList.forEach(sortedLoaders::add); // Conversion de Instance en List
        sortedLoaders.sort(Comparator.comparingInt(ResourceLoader::getOrdinal).reversed());
        for (ResourceLoader loader : sortedLoaders) {
        	resourceLoaders.put(loader.getId( ), loader);
        }
    }
    // ----------------------------------------------------------------------
    // ResourceManager Implementation
    // ----------------------------------------------------------------------

    @Override
    public InputStream getResourceAsInputStream(String name) throws ResourceNotFoundException {
        LuteceResource resource = getResource(name);
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            throw new ResourceIOException("Failed to open resource " + resource.getName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public File getResourceAsFile(String name) throws ResourceNotFoundException, FileResourceCreationException {
        return getResourceAsFile(getResource(name));
    }

    @Override
    public File getResourceAsFile(String name, String outputPath)
            throws ResourceNotFoundException, FileResourceCreationException {
        if (outputPath == null) {
            return getResourceAsFile(name);
        }
        LuteceResource resource = getResource(name);
        File outputFile;
        if (outputDirectory != null) {
            outputFile = new File(outputDirectory, outputPath);
        } else {
            outputFile = new File(outputPath);
        }
        createResourceAsFile(resource, outputFile);
        return outputFile;
    }
    @Override
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void addSearchPath(String id, String path) {
        ResourceLoader loader = resourceLoaders.get(id);

        if (loader == null) {
            throw new IllegalArgumentException("unknown resource loader: " + id);
        }

        loader.addSearchPath(path);
    }

    @Override
    public LuteceResource getResource(String name) throws ResourceNotFoundException {
        for (ResourceLoader resourceLoader : resourceLoaders.values()) {
            try {
                LuteceResource resource = resourceLoader.getResource(name);

                LOGGER.debug("The resource '{}' was found as '{}'", name, resource.getName());

                return resource;
            } catch (ResourceNotFoundException e) {
                LOGGER.debug(
                        "The resource '{}' was not found with resourceLoader '{}'",
                        name,
                        resourceLoader.getClass().getName());
            }
        }

        throw new ResourceNotFoundException(name);
    }

    @Override
    public File getResourceAsFile(LuteceResource resource) throws FileResourceCreationException {
        try {
            File f = resource.getFile();
            if (f != null) {
                return f;
            }
        } catch (IOException e) {
            // Ignore this, try to make use of resource.getInputStream().
        }

        final File outputFile = FileUtils.createTempFile("lutece-resources", "tmp", outputDirectory);
        outputFile.deleteOnExit();
        createResourceAsFile(resource, outputFile);
        return outputFile;
    }

    @Override
    public void createResourceAsFile(LuteceResource resource, File outputFile) throws FileResourceCreationException {
      
        try(InputStream is= resource.getInputStream(); OutputStream os = new FileOutputStream(outputFile)) {
            File dir = outputFile.getParentFile();
            if (!dir.isDirectory() && !dir.mkdirs()) {
                throw new FileResourceCreationException("Failed to create directory " + dir.getPath());
            }
            FileUtils.copy(is, os);
        } catch (IOException e) {
            throw new FileResourceCreationException("Cannot create file-based resource:" + e.getMessage(), e);
        } 
    }

	@Override
	public Set<URL> getResourceURL(String path) throws ResourceNotFoundException {
		 for (ResourceLoader resourceLoader : resourceLoaders.values()) {
	            try {
	                Set<URL> resource = resourceLoader.getResourceURL(path);
	                LOGGER.debug("The resource {} was found as '{}'", () -> resource.toString( ));
	                return resource;
	            } catch (ResourceNotFoundException e) {
	                LOGGER.debug(
	                        "The resource '{}' was not found with resourceLoader '{}'",
	                        path,
	                        resourceLoader.getClass().getName());
	            }
	        }

	        throw new ResourceNotFoundException(path);
	}	
}

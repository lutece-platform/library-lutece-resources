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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.resource.LuteceResource;

/**
 * @author Jason van Zyl
 */
@Named(URLResourceLoader.ID)
@Dependent
public class URLResourceLoader extends AbstractResourceLoader {
    private static final Logger LOGGER = LogManager.getLogger(URLResourceLoader.class);

    public static final String ID = "url";
    @Inject
    @ConfigProperty(name="ordinalValue.resourceLoader.url", defaultValue="200")
    private Integer ordinalValue;

    protected Map<String, String> templateRoots = new HashMap<>();

    /**
     * Get an {@link LuteceResource} with given name.
     *
     * @param name name of resource to fetch byte stream of.
     * @return LuteceResource containing the resource.
     * @throws ResourceNotFoundException if resource not found.
     */
    @Override
    public LuteceResource getResource(String name) throws ResourceNotFoundException {
        if (name == null || name.length() == 0) {
            throw new ResourceNotFoundException("URLResourceLoader : No template name provided");
        }

        for (String path : paths) {
            try {
                URL u;
                if(isPathInArchive(name)){
             	    u = new URL("jar:"+path +name);
                 }else {
                 	u = new URL(path + name);
                 }
                final InputStream inputStream = u.openStream();

                if (inputStream != null) {
                    LOGGER.debug("URLResourceLoader: Found '{}' at '{}'", name, path);

                    // save this root for later re-use
                    templateRoots.put(name, path);

                    return new URLLuteceResource(u) {
                        private boolean useSuper;

                        public synchronized InputStream getInputStream() throws IOException {
                            if (!useSuper) {
                                useSuper = true;
                                return inputStream;
                            }
                            return super.getInputStream();
                        }
                    };
                }
            } catch (MalformedURLException mue) {
                LOGGER.debug("URLResourceLoader: No valid URL '{}{}'", path, name);
            } catch (IOException ioe) {
                LOGGER.debug("URLResourceLoader: Exception when looking for '{}' at '{}'", name, path, ioe);
            }
        }
        // here we try to download without any path just the name which can be an url
        try {
            URL u;
           if(isPathInArchive(name)){
        	    u = new URL("jar:"+name);
            }else {
            	u = new URL(name);
            }
            final InputStream inputStream = u.openStream();
            if (inputStream != null) {
                return new URLLuteceResource(u) {
                    private boolean useSuper;
                    public synchronized InputStream getInputStream() throws IOException {
                        if (!useSuper) {
                            useSuper = true;
                            return inputStream;
                        }
                        return super.getInputStream();
                    }
                };
            }
        } catch (MalformedURLException mue) {
            LOGGER.debug("URLResourceLoader: No valid URL '{}'", name);
        } catch (IOException ioe) {
            LOGGER.debug("URLResourceLoader: Exception when looking for '{}'", name, ioe);
        }

        // convert to a general Velocity ResourceNotFoundException
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
    private  boolean isPathInArchive(String path) {
        try {
            //Check for the presence of "!"
            int index = path.indexOf("!");
            if (index == -1) return false;

            // Extract the path before "!"
            String archivePath = path.substring(0, index).replace("file:", "");
            File archiveFile = new File(archivePath);

            // Check if the file is an existing archive
            return archiveFile.exists() && (archiveFile.getName().endsWith(".war") || archiveFile.getName().endsWith(".jar"));
        } catch (Exception e) {
        	LOGGER.debug(e.getMessage(), e);
            return false;
        }
    }
}

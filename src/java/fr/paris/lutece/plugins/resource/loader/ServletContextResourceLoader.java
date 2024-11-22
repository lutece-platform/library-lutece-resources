package fr.paris.lutece.plugins.resource.loader;


import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import fr.paris.lutece.plugins.resource.LuteceResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;


@Named(ServletContextResourceLoader.ID)
@ApplicationScoped
public class ServletContextResourceLoader extends AbstractResourceLoader {
    public static final String ID = "servletContext";

    private ServletContext servletContext;
    @Inject
    @ConfigProperty(name="ordinalValue.resourceLoader.servletContext", defaultValue="500")
    private Integer ordinalValue;

    // ----------------------------------------------------------------------
    // ResourceLoader Implementation
    // ----------------------------------------------------------------------

    @Override
    public LuteceResource getResource(String name) throws ResourceNotFoundException {
        if (servletContext == null) {
            throw new ResourceNotFoundException(name);
        }
        if (name != null && name.startsWith("/")) {
            name = name.substring(1);
        }
        URL url = null;
		try {
			url = servletContext.getResource(name);
		} catch (MalformedURLException e) {
			throw new ResourceNotFoundException(name, e);
		}
        if (url == null) {
            throw new ResourceNotFoundException(name);
        }

        return new URLLuteceResource(url);
    }
    @Override
	public String getId() {
		return ID;
	}
    @Override
    public int getOrdinal() {
        return ordinalValue;
    }
    
   /** Initialize the service of application
    * 
    * @param context
    *            the context servlet initialized event
    */
	public void initializedOtherService(@Observes @Initialized(ApplicationScoped.class)
		ServletContext context){
		servletContext= context;	    
	}
	@Override
	public Set<URL> getResourceURL(String path) throws ResourceNotFoundException {		
		if (servletContext == null) {
            throw new ResourceNotFoundException(path);
        }
        if (path != null && path.startsWith("/")) {
        	path = path.substring(1);
        }
        Set<URL> resourceUrls = null;
        Set<String> resourcePaths = servletContext.getResourcePaths(path);
        if (resourcePaths != null) {
        	resourceUrls = new HashSet<>();
            for (String p : resourcePaths) {
                try {
                    URL resourceUrl = servletContext.getResource(p);
                    if (resourceUrl != null) {
                        resourceUrls.add(resourceUrl);
                    }
                } catch (Exception e) {
                    throw new ResourceNotFoundException(path, e);
                }
            }
        }
        else{
            throw new ResourceNotFoundException(path);
        }
        return resourceUrls;
	}
}

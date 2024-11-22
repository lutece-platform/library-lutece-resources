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
import java.net.URL;

import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.paris.lutece.plugins.resource.LuteceResource;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@EnableAutoWeld
@AddPackages(fr.paris.lutece.plugins.resource.loader.FileResourceLoader.class)
@AddExtensions(io.smallrye.config.inject.ConfigExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class ThreadContextClasspathResourceLoaderTest extends AbstractResourceLoaderTest {
   
	
	@BeforeAll
	@Inject
	void setResourceLoader( @Named(ThreadContextClasspathResourceLoader.ID) ResourceLoader fileResourceLoader ) {
		this.resourceLoader = fileResourceLoader;
	}
	@Test
    void testLookupWithAAbsolutePathName() throws Exception {
        assertResource("/dir/classpath.txt", "classpath.txt");
    }

    @Test
    void testLookupWithARelativePath() throws Exception {
        assertResource("dir/classpath.txt", "classpath.txt");
    }

    @Test
    void testLookupWhenTheResourceIsMissing() throws Exception {
        assertMissingResource("/foo.txt");

        assertMissingResource("foo.txt");
    }

    @Test
    void testLookupWithANullThreadContextClassLoader() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(null);

        assertMissingResource("/dir/classpath.txt");

        assertMissingResource("dir/classpath.txt");

        Thread.currentThread().setContextClassLoader(loader);
    }

    @Test
    void testLuteceResource() throws Exception {
        LuteceResource resource = resourceLoader.getResource("/dir/classpath.txt");
        assertNull(resource.getFile());
        assertNull(resource.getURI());
        URL url = Thread.currentThread().getContextClassLoader().getResource("dir/classpath.txt");
        assertNotNull(url);
        assertEquals(url, resource.getURL());
        assertEquals(url.toExternalForm(), resource.getName());
    }
}

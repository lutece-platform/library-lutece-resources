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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.paris.lutece.plugins.resource.LuteceResource;
import fr.paris.lutece.plugins.resource.util.FileUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;


@EnableAutoWeld
@AddPackages(fr.paris.lutece.plugins.resource.loader.FileResourceLoader.class)
@AddExtensions(io.smallrye.config.inject.ConfigExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class FileResourceLoaderTest extends AbstractResourceLoaderTest {
	
	@BeforeAll
	@Inject
	 void  setResourceLoader( @Named(FileResourceLoader.ID) ResourceLoader fileResourceLoader ) {
		resourceLoader = fileResourceLoader;
		resourceLoader.addSearchPath(FileUtils.getBasedir()+"/src/test/file-resources");
	}
    @Test
    void testLookupWithAAbsolutePathName() throws Exception {
        assertResource("/dir/file.txt", "file.txt");
    }

    @Test
    void testLookupWithARelativePath() throws Exception {
        assertResource("dir/file.txt", "file.txt");
    }

    @Test
    void testLookupWhenTheResourceIsMissing() throws Exception {
        assertMissingResource("/foo.txt");

        assertMissingResource("foo.txt");
    }

    @Test
    void testLuteceResource() throws Exception {
        LuteceResource resource = resourceLoader.getResource("/dir/file.txt");
        final File f = new File("src/test/file-resources", "/dir/file.txt");
        assertEquals(f.getAbsolutePath(), resource.getFile().getPath());
        assertEquals(f.toURI(), resource.getURI());
        assertEquals(f.toURI().toURL(), resource.getURL());
        assertEquals(f.getAbsolutePath(), resource.getName());
        
    }
}

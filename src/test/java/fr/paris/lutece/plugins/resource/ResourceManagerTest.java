package fr.paris.lutece.plugins.resource;

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

import java.io.File;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.paris.lutece.plugins.resource.loader.FileResourceLoader;
import fr.paris.lutece.plugins.resource.util.FileUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

@EnableAutoWeld
@AddBeanClasses(DefaultResourceManager.class)
@AddPackages(fr.paris.lutece.plugins.resource.loader.FileResourceLoader.class)
@AddExtensions(io.smallrye.config.inject.ConfigExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class ResourceManagerTest {

    private ResourceManager resourceManager;
    
    
    @BeforeAll
    @Inject
	 void setResourceLoaderPaths( ResourceManager resourceManager ) {
    	this.resourceManager = resourceManager;
    	resourceManager.addSearchPath(FileResourceLoader.ID, FileUtils.getBasedir()+"/src/test/file-resources");
	}

    @Test
    void testResourceManagerRetrievingInputStreams() throws Exception {
        InputStream in;

        File absoluteFile = new File(FileUtils.getBasedir(), "src/test/file-resources/dir/file.txt").getAbsoluteFile();
        assertTrue(absoluteFile.isFile());
        assertTrue(absoluteFile.isAbsolute());
        in = resourceManager.getResourceAsInputStream(absoluteFile.getAbsolutePath());
        assertEquals("file.txt", FileUtils.toString(in, "UTF-8"));

        in = resourceManager.getResourceAsInputStream("/dir/file.txt");
        assertEquals("file.txt", FileUtils.toString(in, "UTF-8"));

        in = resourceManager.getResourceAsInputStream("dir/file.txt");
        assertEquals("file.txt", FileUtils.toString(in, "UTF-8"));

        in = resourceManager.getResourceAsInputStream("/dir/classpath.txt");
        assertEquals("classpath.txt", FileUtils.toString(in, "UTF-8"));

        in = resourceManager.getResourceAsInputStream("dir/classpath.txt");
        assertEquals("classpath.txt", FileUtils.toString(in, "UTF-8"));
    }

    @Test
    void testResourceManagerRetrievingFiles() throws Exception {
        File f;

        File absoluteFile = new File(FileUtils.getBasedir(), "src/test/file-resources/dir/file.txt").getAbsoluteFile();
        assertTrue(absoluteFile.isFile());
        assertTrue(absoluteFile.isAbsolute());
        f = resourceManager.getResourceAsFile(absoluteFile.getAbsolutePath());
        assertEquals("file.txt", FileUtils.fileRead(f, "UTF-8"));

        f = resourceManager.getResourceAsFile("/dir/file.txt");
        assertEquals("file.txt", FileUtils.fileRead(f, "UTF-8"));

        f = resourceManager.getResourceAsFile("dir/file.txt");
        assertEquals("file.txt", FileUtils.fileRead(f, "UTF-8"));

        f = resourceManager.getResourceAsFile("/dir/classpath.txt");
        assertEquals("classpath.txt", FileUtils.fileRead(f, "UTF-8"));

        f = resourceManager.getResourceAsFile("dir/classpath.txt");
        assertEquals("classpath.txt", FileUtils.fileRead(f, "UTF-8"));
    }

    @Test
    void testResourceManagerRetrievingFilesToSpecificLocation() throws Exception {
    	File outDir = new File(FileUtils.getBasedir(), "target/");

        resourceManager.setOutputDirectory(outDir);

        File ef = new File(outDir, "f.txt");
        FileUtils.forceDelete(ef);
        assertFalse(ef.exists());
        File f = resourceManager.getResourceAsFile("dir/file.txt", "f.txt");
        assertEquals("file.txt", FileUtils.fileRead(f, "UTF-8"));
        assertEquals(ef, f);

        File ec = new File(outDir, "c.txt");
        FileUtils.forceDelete(ec);
        assertFalse(ec.exists());
        File c = resourceManager.getResourceAsFile("dir/classpath.txt", "c.txt");
        assertEquals("classpath.txt", FileUtils.fileRead(c, "UTF-8"));
        assertEquals(ec, c);
    }
}

package fr.paris.lutece.plugins.resource.util;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;



public class FileUtils {
	
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 16;
    public static final String FAMILY_WINDOWS = "windows";
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
    private static String basedir;

   /**
     * <p>Create a temporary file in a given directory.</p>
     *
     * <p>The file denoted by the returned abstract pathname did not exist before this method was invoked, any subsequent
     * invocation of this method will yield a different file name.</p>
     *
     * <p>The filename is prefixNNNNNsuffix where NNNN is a random number</p>
     *
     * <p>This method is different to {@link File#createTempFile(String, String, File)} of JDK 1.2 as it doesn't create the
     * file itself. It uses the location pointed to by java.io.tmpdir when the parentDir attribute is null.</p>
     *
     * <p>To delete automatically the file created by this method, use the {@link File#deleteOnExit()} method.</p>
     *
     * @param prefix prefix before the random number
     * @param suffix file extension; include the '.'
     * @param parentDir Directory to create the temporary file in <code>-java.io.tmpdir</code> used if not specificed
     * @return a File reference to the new temporary file.
     */
    public static File createTempFile(String prefix, String suffix, File parentDir) {
        File result = null;
        String parent = System.getProperty("java.io.tmpdir");
        if (parentDir != null) {
            parent = parentDir.getPath();
        }
        DecimalFormat fmt = new DecimalFormat("#####");
        SecureRandom secureRandom = new SecureRandom();
        long secureInitializer = secureRandom.nextLong();
        Random rand = new Random(secureInitializer + Runtime.getRuntime().freeMemory());
        synchronized (rand) {
            do {
                result = new File(parent, prefix + fmt.format(Math.abs(rand.nextInt())) + suffix);
            } while (result.exists());
        }

        return result;
    }
    /**
     * Copy and convert bytes from an <code>InputStream</code> to chars on a <code>Writer</code>, using the specified
     * encoding.
     * @param input to convert
     * @param output the result
     * @param encoding The name of a supported character encoding. See the
     *            <a href="http://www.iana.org/assignments/character-sets">IANA Charset Registry</a> for a list of valid
     *            encoding types.
     * @param bufferSize Size of internal buffer to use.
     * @throws IOException io issue
     */
    public static void copy(final InputStream input, final Writer output, final String encoding, final int bufferSize)
            throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output, bufferSize);
    }
    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     * @param input to convert
     * @param output the result
     * @param bufferSize Size of internal buffer to use.
     * @throws IOException io issue
     */
    public static void copy(final Reader input, final Writer output, final int bufferSize) throws IOException {
        final char[] buffer = new char[bufferSize];
        int n = 0;
        while (0 <= (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }
    /**
     * @return Get the contents of an <code>InputStream</code> as a String.
     * @param input to convert
     * @param encoding The name of a supported character encoding. See the
     *            <a href="http://www.iana.org/assignments/character-sets">IANA Charset Registry</a> for a list of valid
     *            encoding types.
     * @throws IOException io issue
     */
    public static String toString(final InputStream input, final String encoding) throws IOException {
        return toString(input, encoding, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * @return Get the contents of an <code>InputStream</code> as a String.
     * @param input to convert
     * @param encoding The name of a supported character encoding. See the
     *            <a href="http://www.iana.org/assignments/character-sets">IANA Charset Registry</a> for a list of valid
     *            encoding types.
     * @param bufferSize Size of internal buffer to use.
     * @throws IOException io issue
     */
    public static String toString(final InputStream input, final String encoding, final int bufferSize)
            throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, encoding, bufferSize);
        return sw.toString();
    }

    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     * @param input to convert
     * @param output the result
     * @param bufferSize Size of internal buffer to use.
     * @throws IOException io issue
     */
    public static void copy(final InputStream input, final OutputStream output, final int bufferSize)
            throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        while (0 <= (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }
    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     * @param input to convert
     * @param output the result
     * @throws IOException io issue
     */
    public static void copy(final InputStream input, final OutputStream output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }
    /**
     * @param file the file path
     * @param encoding the wanted encoding
     * @return the file content using the specified encoding.
     * @throws IOException if any
     */
    public static String fileRead(File file, String encoding) throws IOException {
        return fileRead(file.toPath(), encoding);
    }

    public static String fileRead(Path path, String encoding) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return encoding != null ? new String(bytes, encoding) : new String(bytes);
    }

    public static void fileWrite(Path path, String encoding, String data, OpenOption... openOptions) throws IOException {
        byte[] bytes = encoding != null ? data.getBytes(encoding) : data.getBytes();
        Files.write(path, bytes, openOptions);
    }
    
    /**
     * Delete a file. If file is directory delete it and all sub-directories.
     *
     * @param file a file
     * @throws IOException if any
     */
    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            /*
             * NOTE: Always try to delete the file even if it appears to be non-existent. This will ensure that a
             * symlink whose target does not exist is deleted, too.
             */
            boolean filePresent = file.getCanonicalFile().exists();
            if (!deleteFile(file) && filePresent) {
                final String message = "File " + file + " unable to be deleted.";
                throw new IOException(message);
            }
        }
    }
    /**
     * Recursively delete a directory.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        /*
         * try delete the directory before its contents, which will take care of any directories that are really
         * symbolic links.
         */
        if (directory.delete()) {
            return;
        }

        cleanDirectory(directory);
        if (!directory.delete()) {
            final String message = "Directory " + directory + " unable to be deleted.";
            throw new IOException(message);
        }
    }
    /**
     * Clean a directory without deleting it.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    public static void cleanDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        IOException exception = null;

        final File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    /**
     * Accommodate Windows bug encountered in both Sun and IBM JDKs. Others possible. If the delete does not work, call
     * System.gc(), wait a little and try again.
     *
     * @param file a file
     * @throws IOException if any
     */
    private static boolean deleteFile(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("File " + file + " isn't a file.");
        }

        if (!file.delete()) {
            if (OS_NAME.contains(FAMILY_WINDOWS)) {
                file = file.getCanonicalFile();
                System.gc();
            }

            try {
                Thread.sleep(10);
                return file.delete();
            } catch (InterruptedException ignore) {
                return file.delete();
            }
        }

        return true;
    }
    public static String getBasedir() {
        if (basedir != null) {
            return basedir;
        }

        basedir = System.getProperty("basedir");

        if (basedir == null) {
            basedir = new File("").getAbsolutePath();
        }

        return basedir;
    }
}

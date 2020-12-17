/*
 *
 * MIT License
 *
 * Copyright (c) 2020. Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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
 */

package com.bc.zarr;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;

import static com.bc.zarr.ZarrConstants.FILENAME_DOT_ZGROUP;
import static com.bc.zarr.ZarrUtils.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ZarrGroupTest_open {

    private Path groupPath;
    private Path dotZGroupPath;

    @Before
    public void setUp() throws Exception {
        final FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        groupPath = fs.getPath("/test").resolve("group");
        Files.createDirectories(groupPath);
        dotZGroupPath = groupPath.resolve(FILENAME_DOT_ZGROUP);
        try (final BufferedWriter writer = Files.newBufferedWriter(dotZGroupPath)) {
            toJson(Collections.singletonMap("zarr_format", 2), writer);
        }
    }

    @Test
    public void open_allIsAsExpected() throws IOException {
        //preparation --> setUp
        //execution
        final ZarrGroup zGroup = ZarrGroup.open(groupPath);
        //verification
        assertThat(zGroup, is(notNullValue()));
    }

    @Test
    public void open_pathIsNull_willBeDelegatedToCreateGroupWithInMemoryStore() throws IOException {
        //preparation
        final Path groupPath = null;
        //execution
        final ZarrGroup open = ZarrGroup.open(groupPath);
        //verification
        assertThat(open, is(notNullValue()));
    }

    @Test
    public void open_notExistingDirectory() {
        //preparation
        final Path notExistingDirPath = groupPath.getParent().resolve("notExistingDirPath");
        try {
            //execution
            ZarrGroup.open(notExistingDirPath);
            fail("IOException expected");
        } catch (IOException expected) {
            //verification
            assertThat(expected.getMessage(), is("Path '/test/notExistingDirPath' is not a valid path or not a directory."));
        }
    }

    @Test
    public void open_dotZGroup_fileDoesNotExist() throws IOException {
        //preparation
        Files.delete(dotZGroupPath);
        try {
            //execution
            ZarrGroup.open(groupPath);
            fail("IOException expected");
        } catch (IOException expected) {
            //verification
            assertThat(expected.getMessage(), is("'.zgroup' expected but is not readable or missing in store."));
        }
    }

    @Test
    public void open_dotZGroup_isNotZarrFormat2() throws IOException {
        //preparation
        try (BufferedWriter writer = Files.newBufferedWriter(dotZGroupPath)) {
            toJson(Collections.singletonMap("zarr_format", 1.3), writer);
        }
        try {
            //execution
            ZarrGroup.open(groupPath);
            fail("IOException expected");
        } catch (IOException expected) {
            //verification
            assertThat(expected.getMessage(), is("Zarr format 2 expected but is '1.3'"));
        }
    }
}
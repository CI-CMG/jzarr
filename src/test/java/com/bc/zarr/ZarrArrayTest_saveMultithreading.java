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

import com.bc.zarr.storage.FileSystemStore;
import com.bc.zarr.storage.Store;
import com.bc.zarr.storage.ZipStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ucar.ma2.InvalidRangeException;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ZarrArrayTest_saveMultithreading {

    private String arrayName;
    private Path rootPath;
    private Path testDataPath;

    @Before
    public void setUp() throws IOException {
        testDataPath = Files.createTempDirectory("zarrTest");

        rootPath = testDataPath.resolve("test");

        arrayName = "output";
    }

    @After
    public void tearDown() throws Exception {
        final List<Path> paths = Files.walk(testDataPath).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for (Path path : paths) {
            Files.delete(path);
        }
    }

    @Test
    public void parallelWriting_toTheSameChunk_withCompression_NULL_With_ZipStore() throws IOException, InvalidRangeException {
        try (ZipStore store = new ZipStore(rootPath)) {
            multithreaded_write_read_roundtrip(CompressorFactory.nullCompressor, store);
        }
    }

    @Test
    public void parallelWriting_toTheSameChunk_withCompression_blosc_With_ZipStore() throws IOException, InvalidRangeException {
        try (ZipStore store = new ZipStore(rootPath)) {
            multithreaded_write_read_roundtrip(CompressorFactory.create("blosc"), store);
        }
    }

    @Test
    public void parallelWriting_toTheSameChunk_withCompression_zlib_With_ZipStore() throws IOException, InvalidRangeException {
        try (ZipStore store = new ZipStore(rootPath)) {
            multithreaded_write_read_roundtrip(CompressorFactory.create("zlib"), store);
        }
    }

    @Test
    public void parallelWriting_toTheSameChunk_withCompression_zlib() throws IOException, InvalidRangeException {
        multithreaded_write_read_roundtrip(CompressorFactory.create("zlib"), new FileSystemStore(rootPath));
    }

    @Test
    public void parallelWriting_toTheSameChunk_withCompression_blosc() throws IOException, InvalidRangeException {
        multithreaded_write_read_roundtrip(CompressorFactory.create("blosc"), new FileSystemStore(rootPath));
    }

    private void multithreaded_write_read_roundtrip(Compressor compressor, Store store) throws IOException, InvalidRangeException {
        final ArrayParams parameters = new ArrayParams()
                .shape(30, 30).chunks(10, 10)
                .dataType(DataType.i4).fillValue(0).compressor(compressor);
        final ZarrArray zarrArray = ZarrArray.create(new ZarrPath(arrayName), store, parameters, null);

        final List<Exception> exceptions = Collections.synchronizedList(new LinkedList<>());
        final int[] bufferShape = {1, 5};

        final List<Thread> threads = Collections.synchronizedList(new LinkedList<>());
        threads.add(createThread(new int[]{100, 101, 102, 103, 104}, bufferShape, new int[]{10, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{105, 106, 107, 108, 109}, bufferShape, new int[]{10, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{110, 111, 112, 113, 114}, bufferShape, new int[]{11, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{115, 116, 117, 118, 119}, bufferShape, new int[]{11, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{120, 121, 122, 123, 124}, bufferShape, new int[]{12, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{125, 126, 127, 128, 129}, bufferShape, new int[]{12, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{130, 131, 132, 133, 134}, bufferShape, new int[]{13, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{135, 136, 137, 138, 139}, bufferShape, new int[]{13, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{140, 141, 142, 143, 144}, bufferShape, new int[]{14, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{145, 146, 147, 148, 149}, bufferShape, new int[]{14, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{150, 151, 152, 153, 154}, bufferShape, new int[]{15, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{155, 156, 157, 158, 159}, bufferShape, new int[]{15, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{160, 161, 162, 163, 164}, bufferShape, new int[]{16, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{165, 166, 167, 168, 169}, bufferShape, new int[]{16, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{170, 171, 172, 173, 174}, bufferShape, new int[]{17, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{175, 176, 177, 178, 179}, bufferShape, new int[]{17, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{180, 181, 182, 183, 184}, bufferShape, new int[]{18, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{185, 186, 187, 188, 189}, bufferShape, new int[]{18, 15}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{190, 191, 192, 193, 194}, bufferShape, new int[]{19, 10}, zarrArray, exceptions, threads));
        threads.add(createThread(new int[]{195, 196, 197, 198, 199}, bufferShape, new int[]{19, 15}, zarrArray, exceptions, threads));
        final Thread[] toArray = threads.toArray(new Thread[0]);
        for (Thread thread : toArray) {
            thread.start();
        }
        while (threads.size() > 0) {
            try {
                synchronized (this) {
                    wait(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final int[] targetBuffer = new int[100];

        assertThat(threads.size(), is(0));
        for (Exception e : exceptions) {
            e.printStackTrace();
        }
        assertThat(exceptions.size(), is(0));
        zarrArray.read(targetBuffer, new int[]{10, 10}, new int[]{10, 10});
        assertThat(targetBuffer, is(equalTo(new int[]{
                100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
                110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
                120, 121, 122, 123, 124, 125, 126, 127, 128, 129,
                130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
                140, 141, 142, 143, 144, 145, 146, 147, 148, 149,
                150, 151, 152, 153, 154, 155, 156, 157, 158, 159,
                160, 161, 162, 163, 164, 165, 166, 167, 168, 169,
                170, 171, 172, 173, 174, 175, 176, 177, 178, 179,
                180, 181, 182, 183, 184, 185, 186, 187, 188, 189,
                190, 191, 192, 193, 194, 195, 196, 197, 198, 199

        })));
    }

    private Thread createThread(int[] dataBuffer, int[] bufferShape, int[] to, ZarrArray writer, List<Exception> exceptions, List<Thread> threads) {
        return new Thread(() -> {
            try {
                writer.write(dataBuffer, bufferShape, to);
            } catch (IOException | InvalidRangeException e) {
                exceptions.add(e);
            } finally {
                threads.remove(Thread.currentThread());
            }
        });
    }
}
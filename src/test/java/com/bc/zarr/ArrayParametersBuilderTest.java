/*
 *
 * Copyright (C) 2020 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package com.bc.zarr;

import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class ArrayParametersBuilderTest {

    @Test
    public void buildWithAllMethodCalls() {
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(800, 1000)
                .chunks(200, 100)
                .dataType(DataType.i2)
                .byteOrder(ByteOrder.LITTLE_ENDIAN)
                .fillValue(42)
                .compressor(CompressorFactory.nullCompressor)
                .build();

        assertThat(parameters.getShape(), is(new int[]{800, 1000}));
        assertThat(parameters.getChunks(), is(new int[]{200, 100}));
        assertThat(parameters.getDataType(), is(DataType.i2));
        assertThat(parameters.getByteOrder(), is(ByteOrder.LITTLE_ENDIAN));
        assertThat(parameters.getFillValue(), is(42));
        assertThat(parameters.getCompressor(), is(sameInstance(CompressorFactory.nullCompressor)));
    }

    @Test
    public void ShapeMustBeGiven_noCallToWithShape() {
        try {
            //execution
            new ArrayParams()
                    .chunks(2, 3, 4)
                    .byteOrder(ByteOrder.LITTLE_ENDIAN)
                    .dataType(DataType.i2)
                    .fillValue(23)
                    .compressor(null)
                    // .withShape(3,4,5) // suspended
                    .build();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            //verification
            assertThat(expected.getMessage(), is("Shape must be given."));
        }
    }

    @Test
    public void ShapeMustBeGiven_bulderMethodCallWithZeroArguments() {
        try {
            //execution
            new ArrayParams()
                    .chunks(2, 3, 4)
                    .byteOrder(ByteOrder.LITTLE_ENDIAN)
                    .dataType(DataType.i2)
                    .fillValue(23)
                    .compressor(null)
                    .shape() // no values given
                    .build();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            //verification
            assertThat(expected.getMessage(), is("Shape must be given."));
        }
    }

    @Test
    public void WrongNumberOfChunkDimensions() {
        try {
            //execution
            new ArrayParams()
                    .shape(1000, 1000)
                    .chunks(10, 10, 100)
                    .build();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            //verification
            assertThat(expected.getMessage(), is("Chunks must have the same number of dimensions as shape. Expected: 2 but was 3 !"));
        }
    }

    @Test
    public void Unchuncked() {
        //execution
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(1000, 1000)
                .chunked(false)
                .build();

        //verification
        assertThat(parameters.getChunks(), is(equalTo(new int[]{1000, 1000})));
    }

    @Test
    public void DefaultValues() {
        //execution
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(3800, 5000)
                .build();

        //verification
        assertThat(parameters.getShape(), is(new int[]{3800, 5000}));
        assertThat(parameters.getChunks(), is(new int[]{475, 500}));
        assertThat(parameters.isChunked(), is(true));
        assertThat(parameters.getDataType(), is(DataType.f8));
        assertThat(parameters.getByteOrder(), is(ByteOrder.BIG_ENDIAN));
        assertThat(parameters.getFillValue(), is(0));
        assertThat(parameters.getCompressor().getId(), is("blosc"));
        assertThat(parameters.getCompressor().toString(), is("compressor=blosc/cname=lz4/clevel=5/blocksize=0/shuffle=1"));
    }

    @Test
    public void Rebuild() {
        //preparation
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(3800, 5000)
                .chunks(12, 13)
                .dataType(DataType.i2)
                .byteOrder(ByteOrder.LITTLE_ENDIAN)
                .fillValue(null)
                .compressor(CompressorFactory.nullCompressor)
                .build();

        //execution
        final ArrayParams.Params newParams = parameters.toBuilder()
                .shape(123, 456)
                .build();

        //verification
        assertThat(newParams.getShape(), is(new int[]{123, 456}));
        assertThat(newParams.getChunks(), is(new int[]{12, 13}));
        assertThat(newParams.getDataType(), is(DataType.i2));
        assertThat(newParams.getByteOrder(), is(ByteOrder.LITTLE_ENDIAN));
        assertThat(newParams.getFillValue(), is(nullValue()));
        assertThat(newParams.getCompressor(), is(sameInstance(CompressorFactory.nullCompressor)));
    }

    @Test
    public void autoComputeChunks() {
        //execution
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(3800, 5000)
                .build();

        assertThat(parameters.getChunks(), is(equalTo(new int[]{475, 500})));
    }

    @Test
    public void autoComputeChunks_widthIsNotDividableWithModuloZero() {
        //execution
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(3800, 4999)
                .build();

        assertThat(parameters.getChunks(), is(equalTo(new int[]{475, 500})));
    }

    @Test
    public void autoComputeChunks_widthOneLittleDimension() {
        //execution
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(3800, 33)
                .build();

        assertThat(parameters.getChunks(), is(equalTo(new int[]{475, 33})));
    }

    @Test
    public void replaceZeroOrNegativeChunkDimensionWithShapeDimensionSize() {
        //execution
        final ArrayParams.Params parameters = new ArrayParams()
                .shape(3800, 5000)
                .chunks(444, 0)
                .build();

        assertThat(parameters.getChunks(), is(equalTo(new int[]{444, 5000})));
    }
}
import com.bc.zarr.*;
import com.bc.zarr.storage.InMemoryStore;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.factory.Nd4j;
import ucar.ma2.InvalidRangeException;
import utils.OutputHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static utils.OutputHelper.createOutput;

public class Tutorial_rtd {

    /**
     * Creates a 2-dimensional array of 32-bit integers with 10000 rows and 10000 columns, divided into
     * chunks where each chunk has 1000 rows and 1000 columns (and so there will be 100 chunks in total).
     */
    private static void example_1() throws IOException {
        ZarrArray jZarray = ZarrArray.create(new ArrayParams()
                .shape(10000, 10000)
                .chunks(1000, 1000)
                .dataType(DataType.i4)
        );

        createOutput(out -> out.println(jZarray));
    }

    /**
     * Reading and writing data.
     * Creates an array with size 5 * 7 and fill value <code>-1</code>.<br>
     * Then writes data with shape 3 * 5 in the center of the array.<br>
     * Finally read in the entire array data (int[] with size 5 * 7) and we can see
     * the data written before surrounded by the fill value <code>-1</code>.
     */
    private static void example_2() throws IOException, InvalidRangeException {
        // example 2 code snippet 1 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#writing-and-reading-data
        ZarrArray array = ZarrArray.create(new ArrayParams()
                .shape(5, 7)
                .dataType(DataType.i4) // integer data type
                .fillValue(-9999)
        );
        // example 2 code snippet 1 end

        // example 2 code snippet 2 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#writing-and-reading-data
        // define the data which should be written
        int[] data = {
                11, 12, 13, 14, 15,
                21, 22, 23, 24, 25,
                31, 32, 33, 34, 35
        };
        int[] shape = {3, 5}; // the actual N-D shape of the data
        int[] offset = {2, 0}; // and the offset into the original array
        // example 2 code snippet 2 end

        // write the data
        // example 2 code snippet 3 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#writing-and-reading-data
        array.write(data, shape, offset);
        // example 2 code snippet 3 end

        // read the entire data
        // example 2 code snippet 4 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#writing-and-reading-data
        int[] entireData = (int[]) array.read();
        // example 2 code snippet 4 end

        // Finally we can instantiate for example an org.nd4j.linalg.api.ndarray.INDArray and print out the data
        // example 2 code snippet 5 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#writing-and-reading-data
        OutputHelper.Writer writer = out -> {
            DataBuffer buffer = Nd4j.createBuffer(entireData);
            out.println(Nd4j.create(buffer).reshape('c', array.getShape()));
        };
        // example 2 code snippet 5 end

        createOutput(writer);
    }

    /**
     * Creates an array in a local file store.
     */
    private static void example_3() throws IOException, InvalidRangeException {
        // example 3 code snippet 1 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#persistent-arrays
        ZarrArray created = ZarrArray.create("docs/examples/output/example_3.zarr", new ArrayParams()
                .shape(1000, 1000).chunks(250, 250).dataType(DataType.i4).fillValue(-9999)
        );
        // example 3 code snippet 1 end

        // example 3 code snippet 2 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#persistent-arrays
        created.write(42, new int[]{3, 4}, new int[]{21, 22});
        // example 3 code snippet 2 end

        // example 3 code snippet 3 begin .. see https://jzarr.readthedocs.io/en/latest/tutorial.html#persistent-arrays
        ZarrArray opened = ZarrArray.open("docs/examples/output/example_3.zarr");
        int[] redShape = {5, 6};
        final int[] data = (int[]) opened.read(redShape, new int[]{20, 21});
        // example 3 code snippet 3 end

        createOutput(out -> {
            DataBuffer buffer = Nd4j.createBuffer(data);
            out.println(Nd4j.create(buffer).reshape('c', redShape));
        });
    }

    /**
     * Create an array with an user defined compressor.
     */
    private static void example_4() throws IOException {
        ZarrArray jZarray = ZarrArray.create(new ArrayParams()
                .shape(243, 324, 742)  // three or more dimensions
                .compressor(CompressorFactory.create("zlib", 8)) // 8 : compression level
        );
    }

    /**
     * Create a group and sub groups and an array in a subgroup
     */
    private static void example_5() throws IOException {
        ZarrGroup root = ZarrGroup.create();          // creates an in memory group
        ZarrGroup foo = root.createSubGroup("foo");
        ZarrGroup bar = foo.createSubGroup("bar");
        ZarrArray array = bar.createArray("baz", new ArrayParams()
                .shape(1000, 1000).chunks(100, 100).dataType(DataType.i4)
        );
        createOutput(out -> out.println(array));
    }

    /**
     * Example to demonstrate how to work with user attributes
     */
    private static void example_6() throws IOException {
        Map<String, Object> attrs;
        Map<String, Object> attributes;
        ZarrGroup group;
        ZarrArray array;

        attrs = new HashMap<>();
        attrs.put("baz", 42);
        attrs.put("qux", new int[]{1, 4, 7, 12});

        // store user attributes at group creation time
        group = ZarrGroup.create(new InMemoryStore(), attrs);

        // store user attributes at array creation time
        array = ZarrArray.create(new ArrayParams().shape(10, 10), attrs);

        // also at array creation time within a group
        array = group.createArray("name", new ArrayParams().shape(10, 10), attrs);

        // store or restore user attributes after creation time
        group.writeAttributes(attrs);
        array.writeAttributes(attrs);

        // get attributes from group
        attrs = group.getAttributes();
        // similar from array
        attributes = array.getAttributes();

        createOutput(out -> out.println(ZarrUtils.toJson(attributes, true)));
    }

    /**
     * Example for partly modify data without loading the entire array
     */
    private static void example_7() throws IOException, InvalidRangeException {
        // create an array
        int height = 10;
        int width = 6;
        int[] arrayShape = {height, width};
        ZarrArray arr = ZarrArray.create(new ArrayParams()
                .shape(arrayShape).dataType(DataType.i2));

        // write a data part (value 33) to the offset position with the given shape
        int value = 33;
        int[] shape = {6, 4};   // the shape for the data to be written
        int[] offset = {2, 1};  // y direction offset: 2   x direction offset: 1
        arr.write(value, shape, offset);

        // read entire array data
        short[] data = (short[]) arr.read();

        // wrap the data and create output
        createOutput(out -> {
            DataBuffer buffer = Nd4j.createBuffer(arrayShape, org.nd4j.linalg.api.buffer.DataType.SHORT);
            buffer.setData(data);
            out.println(Nd4j.create(buffer).reshape('c', arrayShape));
        });
        ///
    }

    /**
     * Example prevent chunking at first dimension
     */
    private static void example_8() throws IOException {
        ZarrArray zarray = ZarrArray.create(new ArrayParams()
                .shape(8888, 7777).chunks(100, 0)
        );
        int[] chunks = zarray.getChunks();
        createOutput(out -> {
            out.println(Arrays.toString(chunks));
        });
    }

    /**
     * Example prevent chunking at second dimension
     */
    private static void example_9() throws IOException {
        ZarrArray zarray = ZarrArray.create(new ArrayParams()
                .shape(8888, 7777).chunks(100, 0)
        );
        int[] chunks = zarray.getChunks();
        createOutput(out -> {
            out.println(Arrays.toString(chunks));
        });
    }

    private static void example_10() throws IOException {
        ZarrArray zarray = ZarrArray.create(new ArrayParams()
                .shape(10000, 10000).chunks(1000, 1000)
        );
    }

    private static void example_11() throws IOException {
        ZarrArray zarray;
        zarray = ZarrArray.create(new ArrayParams()
                .shape(6200, 7500).chunked(true)
        );
        int[] chunks1 = zarray.getChunks();
        // array creation without chunked(true) leads to the same result, because true is the
        // default value for the parameter chunked
        zarray = ZarrArray.create(new ArrayParams()
                .shape(6200, 7500)
        );
        int[] chunks2 = zarray.getChunks();
        createOutput(out -> {
            out.println("chunks1 = " + Arrays.toString(chunks1));
            out.println("chunks2 = " + Arrays.toString(chunks2));
        });
    }

    public static void main(String[] args) throws IOException, InvalidRangeException {
        example_1();
        example_2();
        example_3();
        example_4();
        example_5();
        example_6();
        example_7();
        example_8();
        example_9();
        example_10();
        example_11();
    }
}


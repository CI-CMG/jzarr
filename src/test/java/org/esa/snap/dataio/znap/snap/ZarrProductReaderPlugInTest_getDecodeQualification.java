package org.esa.snap.dataio.znap.snap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.bc.zarr.ZarrConstants;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.util.io.TreeDeleter;
import org.junit.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZarrProductReaderPlugInTest_getDecodeQualification {

    private ZarrProductReaderPlugIn plugIn;
    private Path productRoot;
    private Path zarrRootHeader;
    private Path aRasterDataDir;
    private Path zarrHeaderFile;
    private Path testPath;

    @Before
    public void setUp() throws Exception {
        plugIn = new ZarrProductReaderPlugIn();
        final Path fsRoot = FileSystems.getDefault().getRootDirectories().iterator().next();
        testPath = fsRoot.resolve("temporary-snap-development-test-path");
        Files.createDirectories(testPath);
        productRoot = testPath.resolve("snap_zarr_product_root_dir.znap");
        zarrRootHeader = productRoot.resolve(ZarrConstants.FILENAME_DOT_ZGROUP);
        aRasterDataDir = productRoot.resolve("a_raster_data_dir");
        zarrHeaderFile = aRasterDataDir.resolve(ZarrConstants.FILENAME_DOT_ZARRAY);
    }

    @After
    public void tearDown() throws IOException {
        TreeDeleter.deleteDir(testPath);
//        final boolean exists = Files.exists(testPath);
//        System.out.println("exists = " + exists);
    }

    @Test
    public void decodeQualification_INTENDED_perfectMatch_inputIsPathObject() throws IOException {
        Files.createDirectories(aRasterDataDir);
        Files.createFile(zarrRootHeader);
        Files.createFile(zarrHeaderFile);

        final Object input = this.productRoot;
        assertThat(plugIn.getDecodeQualification(input),
                   is(equalTo(DecodeQualification.INTENDED)));
    }

    @Test
    public void decodeQualification_INTENDED_perfectMatch_inputIsFileObject() throws IOException {
        Files.createDirectories(aRasterDataDir);
        Files.createFile(zarrRootHeader);
        Files.createFile(zarrHeaderFile);

        final Object input = this.productRoot.toFile();
        assertThat(plugIn.getDecodeQualification(input),
                   is(equalTo(DecodeQualification.INTENDED)));
    }

    @Test
    public void decodeQualification_INTENDED_perfectMatch_inputStringObject() throws IOException {
        Files.createDirectories(aRasterDataDir);
        Files.createFile(zarrRootHeader);
        Files.createFile(zarrHeaderFile);

        final Object input = this.productRoot.toString();
        assertThat(plugIn.getDecodeQualification(input),
                   is(equalTo(DecodeQualification.INTENDED)));
    }

    @Test
    public void decodeQualification_UNABLE_inputObjectIsNullOrCanNotBeConvertedToPath() throws IOException {
        Files.createDirectories(aRasterDataDir);
        Files.createFile(zarrRootHeader);
        Files.createFile(zarrHeaderFile);


        assertThat(plugIn.getDecodeQualification(null),
                   is(equalTo(DecodeQualification.UNABLE)));

        assertThat(plugIn.getDecodeQualification(productRoot.toUri()),
                   is(equalTo(DecodeQualification.UNABLE)));
    }

    @Test
    public void decodeQualification_UNABLE_productRootDoesNotExist() throws IOException {
        // No file or directory as expected

        final DecodeQualification decodeQualification = plugIn.getDecodeQualification(productRoot);

        assertThat(decodeQualification, is(equalTo(DecodeQualification.UNABLE)));
    }

    @Test
    public void decodeQualification_UNABLE_productRootPathIsFileInsteadOfDirectory() throws IOException {
        Files.createFile(productRoot);

        final DecodeQualification decodeQualification = plugIn.getDecodeQualification(productRoot);

        assertThat(decodeQualification, is(equalTo(DecodeQualification.UNABLE)));
    }

    @Test
    public void decodeQualification_UNABLE_NoSnapHeaderFile() throws IOException {
        Files.createDirectories(productRoot);
//        Files.createFile(headerFile);
        Files.createDirectories(aRasterDataDir);
        Files.createFile(zarrHeaderFile);

        final DecodeQualification decodeQualification = plugIn.getDecodeQualification(productRoot);

        assertThat(decodeQualification, is(equalTo(DecodeQualification.UNABLE)));
    }

    @Test
    public void decodeQualification_UNABLE_SnapHeaderFileExistButIsADirectory() throws IOException {
        Files.createDirectories(productRoot);
//        Files.createFile(headerFile);
        Files.createDirectories(zarrRootHeader);
        Files.createDirectories(aRasterDataDir);
        Files.createFile(zarrHeaderFile);

        final DecodeQualification decodeQualification = plugIn.getDecodeQualification(productRoot);

        assertThat(decodeQualification, is(equalTo(DecodeQualification.UNABLE)));
    }

    @Test
    public void decodeQualification_UNABLE_NoZarrHeaderFile() throws IOException {
        Files.createDirectories(productRoot);
        Files.createFile(zarrRootHeader);
        Files.createDirectories(aRasterDataDir);
//        Files.createFile(zarrHeaderFile);

        final DecodeQualification decodeQualification = plugIn.getDecodeQualification(productRoot);

        assertThat(decodeQualification, is(equalTo(DecodeQualification.UNABLE)));
    }

    @Test
    public void decodeQualification_UNABLE_ZarrHeaderFileExistButIsADirectory() throws IOException {
        Files.createDirectories(productRoot);
        Files.createFile(zarrRootHeader);
        Files.createDirectories(aRasterDataDir);
//        Files.createFile(zarrHeaderFile);
        Files.createDirectories(zarrHeaderFile);

        final DecodeQualification decodeQualification = plugIn.getDecodeQualification(productRoot);

        assertThat(decodeQualification, is(equalTo(DecodeQualification.UNABLE)));
    }
}
.. _amazonS3:

How to use JZarr with AWS S3
============================
In general JZarr can work with :code:`java.nio.file.Path` objects. So if someone extends the abstract :code:`java.nio.file.FileSystem`
(see `FileSystem <https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html>`_) to connect an AWS S3 bucket
this can be used to read from and write directly to such buckets.

In our example we use the `Amazon-S3-FileSystem-NIO2 <https://github.com/lasersonlab/Amazon-S3-FileSystem-NIO2>`_
library which is forked several times by other implementors.

If you want to try the following example, add this maven dependency to your pom::

 <dependency>
     <groupId>org.lasersonlab</groupId>
     <artifactId>s3fs</artifactId>
     <version>2.2.3</version>
 </dependency>

Also in order for the example to work fine, you need a :code:`s3.properties` file filled
with your s3 properties.

.. highlight:: properties
.. literalinclude:: ./examples/resources/s3-template.properties
  :caption: `example properties <https://github.com/bcdev/jzarr/blob/master/docs/examples/resources/s3-template.properties>`_

Below you can see code snippets for **connecting** with, **writing** to and **reading** from an s3 bucket.
You can find the entire example code here: `S3Array_nio.java <https://github.com/bcdev/jzarr/blob/master/docs/examples/java/S3Array_nio.java>`_

connect an s3 bucket
--------------------
.. highlight:: java
.. literalinclude:: ./examples/java/S3Array_nio.java
  :caption: `code example for connecting the s3 bucket <https://github.com/bcdev/jzarr/blob/master/docs/examples/java/S3Array_nio.java>`_
  :start-after: Path getS3BucketNioPath(
  :end-before: throw new
  :dedent: 8

write to an s3 bucket
---------------------
.. highlight:: java
.. literalinclude:: ./examples/java/S3Array_nio.java
  :caption: `code example writing to an s3 bucket <https://github.com/bcdev/jzarr/blob/master/docs/examples/java/S3Array_nio.java>`_
  :start-after: void writeToS3Bucket(
  :end-before: } ///
  :dedent: 8

read from an s3 bucket
----------------------
.. highlight:: java
.. literalinclude:: ./examples/java/S3Array_nio.java
  :caption: `code example reading from an s3 bucket <https://github.com/bcdev/jzarr/blob/master/docs/examples/java/S3Array_nio.java>`_
  :start-after: void readFromS3Bucket(
  :end-before: } ///
  :dedent: 8





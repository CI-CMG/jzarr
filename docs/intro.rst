.. _zarr package: https://zarr.readthedocs.io/en/stable/index.html

JZarr documentation
===================

JZarr is a Java library providing an implementation of chunked,
compressed, N-dimensional arrays close to the Python `zarr package`_.

Highlights
----------

* Create N-dimensional arrays with java primitive data types. At the moment boolean and char type are not supported.
* Chunk arrays along any dimension.
* Compress and/or filter chunks.
* Store arrays in memory, on disk, (Future plans: inside a Zip file, on S3, ...)
* Read an array concurrently from multiple threads or processes.
* Write to an array concurrently from multiple threads or processes.
* Organize arrays into hierarchies via groups.

Status
------

JZarr is in the very beginning phase. Feedback and bug reports are very welcome. Please get in touch via
the `GitHub issue tracker <https://github.com/bcdev/jzarr/issues>`_.

Requirements
------------
JZarr needs Java 8 or higher.

Maven Dependency
----------------

To use JZarr in you project integrate the following lines into your maven pom.xml::

 <dependency>
    <groupId>com.bc.zarr</groupId>
    <artifactId>jzarr</artifactId>
    <version>0.2-SNAPSHOT</version>
 </dependency>

 <repositories>
    <repository>
       <id>bc-nexus-repo</id>
       <name>Brockmann-Consult Public Maven Repository</name>
       <url>http://nexus.senbox.net/nexus/content/groups/public/</url>
    </repository>
 </repositories>

It is planned to deploy the JZarr artifacts to the `maven central <https://mvnrepository.com/repos/central>`
repository too.

API Examples
------------

.. include:: examples.rst
  :start-after: **intro start**
  :end-before: **intro end**

:ref:`Read more ... <examples>`

.. .. toctree::
..    :hidden:
..    :maxdepth: 2
..
..    examples
..    array_creation
..    array_params
..    datatype

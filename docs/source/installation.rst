Installation
=============

System Requirements and Dependencies
-------------------------------------

- **A Java Virtual Machine 1.8.** Bjoern is written in Java 8 and does
  not build with Java 7. It has been tested with OpenJDK 8 but should
  also work with Oracle's JVM.

- **OrientDB 2.1.5 Community Edition.** The bjoern-server is based on
  OrientDB version *2.1.5* and has not been tested with any other
  version. You can download the correct version
  `here<http://orientdb.com/download.php?email=unknown@unknown.com&file=orientdb-community-2.1.5.tar.gz>`_ .

**A dependency tarball** You can download a tarball that bundles all
dependencies
`here<http://user.informatik.uni-goettingen.de/~fyamagu/bjoern-radare/jars.tar.gz>`_ .

Step-by-step Instructions
---------------------------

First, clone the repository and enter the bjoern-radare directory.

.. code-block:: none

	git clone https://github.com/fabsx00/bjoern-radare
	cd bjoern-radare

Next, download Orientdb Community edition:

.. code-block:: none

	wget 'http://orientdb.com/download.php?email=unknown@unknown.com&file=orientdb-community-2.1.5.tar.gz&os=linux'

on Linux, or

.. code-block:: none

	wget 'http://orientdb.com/download.php?email=unknown@unknown.com&file=orientdb-community-2.1.5.tar.gz&os=mac'

on MacOSX.

Finally, download and extract dependencies, and build:

.. code-block:: none

	wget 'http://user.informatik.uni-goettingen.de/~fyamagu/bjoern-radare/jars.tar.gz'
	tar xfz jars.tar.gz
	ant

Installation
=============

System Requirements and Dependencies
-------------------------------------

- **A Java Virtual Machine 1.8.** Bjoern is written in Java 8 and does
  not build with Java 7. It has been tested with OpenJDK 8 but should
  also work with Oracle's JVM.

- **Radare2** The primitives provided by the radare2 reverse
  engineering framework are employed to dissect and analyze binary
  files to obtain graph-based program representations from them.

- **OrientDB 2.1.5 Community Edition.** The bjoern-server is based on
  OrientDB version *2.1.5* and has not been tested with any other
  version. You can download the correct version
  `here <http://orientdb.com/download.php?email=unknown@unknown.com&file=orientdb-community-2.1.5.tar.gz>`_ .

- **Bjoern-shell [Optional. ]** The bjoern-shell is a convenient tool
  to query the database contents and develop new query-primitives
  (so-called steps) that can be re-used in subsequent queries.

**A dependency tarball** You can download a tarball that bundles all
dependencies
`here <http://user.informatik.uni-goettingen.de/~fyamagu/bjoern-radare/jars.tar.gz>`_ .

Building bjoern (step-by-step)
------------------------------

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

on MacOSX. Unpack the tarball directly in the bjoern directory:

.. code-block:: none

	tar xfz orientdb-community-2.1.5.tar.gz


Finally, download and extract jar-dependencies, and build:

.. code-block:: none

	wget 'http://user.informatik.uni-goettingen.de/~fyamagu/bjoern-radare/jars.tar.gz'
	tar xfz jars.tar.gz
	ant

Building bjoern plugins
-----------------------

Bjoern is built to operate on arbitrary property graphs. All
functionality specific to binary code analysis is made available by
plugins contained in the directory `bjoern-plugins`. You can build
plugins by entering a plugin's directory and executing
`ant`. As plugins depend on `bjoern.jar`, this will only succeed if
bjoern has been built already.

Installing radare2
------------------

Please follow the instructions `here
<http://www.radare.org/r/down.html>`_ to install radare2, and make
sure the programs `radare2` and `r2` are in the path.

Installing the bjoern-shell
---------------------------

.. code-block:: none

	git clone https://github.com/a0x77n/bjoern-shell
	cd bjoern-shell
	python3 setup.py install
	bjosh

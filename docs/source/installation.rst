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

Installing radare2
------------------

Please follow the instructions `here
<http://www.radare.org/r/down.html>`_ to install radare2, and make
sure the programs `radare2` and `r2` are in the path.

Building bjoern (step-by-step)
------------------------------

.. code-block:: none

	git clone https://github.com/fabsx00/bjoern
	cd bjoern
	gradle deploy

This will build the bjoern-server and install python utilities into
the user site-packages directory (typically `~/.local/`). To test your
installation, try running

.. code-block:: none

	bjoern-import

If this command is unavailable, please make sure the script directory
(typically `~/.local/bin/`) is in the path, e.g., by adding the
following to your `~/.bashrc`:

.. code-block:: none

	export PATH=$PATH:~/.local/bin

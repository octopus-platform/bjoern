Shell Access (Gremlin)
======================

Octopus provides access to Gremlin shells that can be used to
query the database. Shells run inside the server process and can
therefore make use of the 'plocal' binary protocol for efficient
access.

Usage
-----

The shells currently running inside the server process can be listed
using the `listshells` command as follows:

.. code-block:: none

	curl http://localhost:2480/listshells

A new shell can be created using the `shellcreate` command as follows.

.. code-block:: none

	curl http://localhost:2480/shellcreate/[dbname]

where `dbname` is the name of the database to connect to. By default,
a shell for `bjoernDB` is created.

Configuration
-------------

none

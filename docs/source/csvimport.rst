Plugin: CSV Import
==================

This plugin allows property graphs to be imported into the graph
database. It requires nodes and edges to be specified in a CSV
format.

Usage
-----

The plugin can be invoked via an HTTP GET request or via the utility
'bjoern-csvimport.sh'.

The HTTP GET request can be issued with curl as
follows

.. code-block:: none

	curl http://localhost:2480/importcsv/<nodeFilename>/<edgeFilename><dbname>/

where nodeFilename is a CSV file containing nodes, edgeFilename is a
CSV file containing edges, and dbname is the name of the database to
import into.

Alternatively, the script 'bjoern-csvimport.sh' can be invoked as follows

.. code-block:: none

	./bjoern-csvimport.sh [dbname]

where dbname is the name of the database. The tool will automatically
impor the files 'nodes.csv' and 'edges.csv' if present in the current
working directory.

Exporting of CFGs
==================

Usage
-----

The control flow graphs of all functions in a database instance can be
exported by sending a GET request to the server. This can be easily
done with cURL:

.. code-block:: none

	curl http://localhost:2480/dumpcfg/<dbname>/

or by pointing your browser to the same address. The server's
response contains the destination folder where results have been
stored.

.. note::

	Adapt the server address and port to your local setup.

Configuration
-------------

The CFG export command can be configured in the configuration file
(conf/orientdb-server-config.xml).

.. code-block:: none

	<!-- ... -->
	<commands>
		<!-- Added for bjoern -->
		<!-- ... -->
		<command implementation="server.commands.dumpcfg.OServerCommandGetDumpCFG" pattern="GET|dumpcfg/">
			<parameters>
				<!-- Dump CFGs into this folder -->
				<entry name="dest" value="../../dump" />
				<!-- Number of threads -->
				<entry name="threads" value="4" />
				<!-- Output format (graphml/gml) -->
				<entry name="format" value="graphml" />
			</parameters>
		</command>
		<!-- ... -->
	</commands>
	<!-- ... -->

Configurable parameters:

- **dest** The destination folder. After the execution, this folder
  contains the control flow graphs. The folder hierarchy is
  `cfg/<dbname>/<function_id>.graphml`.
- **nthreads** The number of threads used to compute and export the
  control flow graphs.
- **format** The graph format. Currently graphml and gml are supported.

Layered Control Flow Graphs
---------------------------

The exported control flow graphs are structured into 3 layers. The
function layer, the basic block layer, and the instruction layer. The
function layer contains the unique function node, the basic block layer
all the basic blocks of the function, and the instruction layer the
instruction nodes. The function layer is connected to the basic block
layer by linking each basic block to the function nodes. These edges
are labeled `IS_FUNC_OF`. Similarly, the basic block layer is connected
to the instruction layer. These edges are labeled `IS_BB_OF`. Within
the basic block layer nodes, are connected by control flow edges, i.e.
edges labeled `CFLOW_ALWAYS, CFLOW_TRUE, CFLOW_FALSE`. Additionally,
the instruction nodes of a single basic block are chained together by
edges labeled `IS_NEXT_IN_BB`.

A special node (called the address/root node), representing the address
of the function, is connected to the first element in each layer. More
precisely, this node is connected to the function node, the first basic
block of the function, and its first instruction. The root node is
useful to jump to the required layer and follow the flow of control.

.. .. note::

..        We could call this the `layered control flow graph`. Maybe we
..        should also connect the last instruction of a BB with the first
..        instruction of the next BB.

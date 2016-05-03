Plugins
=======

Plugins are the best way to extend the Octopus server with custom functionality. You can write own plugins or
to use the existing ones.

Execution of Plugins
--------------------

Plugins are invoked via HTTP POST requests. The body of the request message contains the plugin's configuration in JSON
format. It includes information about how to execute the plugin (required by all plugins) and additional settings for
the plugin (dependent on the specific plugin). For example

.. code-block:: none

    {
        "plugin": <name of the plugin>
        "class": <class implementing the IPlugin interface>,
        "settings": <JSON object containing plugin specific settings>
    }

The configuration file is used by the `executeplugin` command of the Octopus server, which loads and executes the plugin.
The POST request can be issued using curl

.. code-block:: none

    cat plugin.json | curl -d @- http://localhost:2480/executeplugin/

where `plugin.json` contains the configuration.

The Function Export Plugin
--------------------------

The function export plugin can be used to export database content at function level. Functions consist of a
function node, basic blocks and instruction nodes along with edges between those nodes. It is possible to export
functions as a whole or only parts of a function, e.g., the control flow graph.

Configuration
~~~~~~~~~~~~~

The plugins configuration file contains the following data:

.. code-block:: none

    {
        "plugin": "functionexport.jar",
        "class": "bjoern.plugins.functionexporter.FunctionExportPlugin",
        "settings": {
            "database": <database name>,
            "format": "dot"|"graphml"|"gml",
            "destination": "<output directory>,
            "threads": <number of threads to use>,
            "nodes": <JSON array of node types>,
            "edges": <JSON array of edge types>
        }
    }

.. note::

    Edges are only exported if the head and the tail is exported as well.

Example
~~~~~~~

To extract the control flow graphs of all functions of a database named `ls` you can start with the settings below:

.. code-block:: none

    {
        "plugin": "functionexport.jar",
        "class": "bjoern.plugins.functionexporter.FunctionExportPlugin",
        "settings": {
            "database": "ls",
            "format": "dot",
            "destination": "some/path/you/like",
            "threads": "4",
            "nodes": ["BB"],
            "edges": ["CFLOW_ALWAYS", "CFLOW_TRUE", "CFLOW_FALSE"]
        }
    }

The Instruction Linker Plugin
-----------------------------

The instruction linker plugin connects the instructions of a function accordingly to the execution order and the flow of
control. This is useful to obtain control flow information at the level of instructions as opposed to basic blocks.

Configuration
~~~~~~~~~~~~~

The plugins configuration file contains the following data:

.. code-block:: none

    {
        "plugin": "instructionlinker.jar",
        "class": "bjoern.plugins.instructionlinker.InstructionLinkerPlugin",
        "settings": {
            "database": <database name>,
        }
    }



Writing Plugins
---------------

All plugins must implement the `IPlugin` interface (`octopus.server.components.pluginInterface.IPlugin`). The interface
specifies the following four methods:

================== =====================================================================================================
Method             Description
================== =====================================================================================================
`configure`        This method is used to configure the plugin. The only argument passed to this method is the JSON
                   object specified by the settings attribute of the configuration file.
`execute`          This method contains the main code of the plugin.
`beforeExecution`  This method is called before the execution of the plugin.
`afterExecution`   This method is called after the execution of the plugin.
================== =====================================================================================================

The methods are invoked in the following order: `configure`, `beforeExecution`, `execute`, `afterExecution`.

Most plugins will require access to some database. The class `OrientGraphConnectionPlugin`
(`bjoern.pluginlib.OrientGraphConnectionPlugin`) implements the `IPlugin` interface and opens a connection to a graph
database in `beforeExecution`. The connection is closed in
`afterExecution`. The name of the database is read in `configure`, the corresponding attribute must be named `database`.
The class `OrientGraphConnectionPlugin
provides two additional methods to acquire a graph instance: `getGraphInstance` and `getNoTxGraphInstance`
for non-transactional graphs and transactional graphs, respectively.

.. note::

    If you override any other method of the `IPlugin` interface, make sure you don't forget to call `super`.

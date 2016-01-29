Overview
========

The Bjoern platform consists of a server component, and a collection
of client utilities which can be used to query and update the database
contents. All heavy computation is performed in threads on the server
side, which are spawned in response to client requests issued via a
HTTP REST API. These threads can directly access the database using a
lightweight binary protocol ("plocal"), a protocol that introduces
only low overhead when compared to standard HTTP-based access
protocols.

Access to the database is synchronized to allow multiple
users and long-running analysis tasks to be executed in parallel. For
multiple user access, bjoern implements a groovy shell server. This
makes it possible for users to execute commands on the server side,
disconnect, and come back later to view results, similar in style to
the way GNU screen sessions are used on shared servers.

Bjoern-Server
-------------

At heart, the bjoern server is an OrientDB server instance, extended
with plugins that are loaded at startup. These plugins expose the
platforms functionality via a REST API, which makes it possible to
invoke server functionality via HTTP requests. Functionality
implemented by Bjoern-server does not make use of language specific
concepts. Bjoern-server is therefore a generic platform for code
analysis with graph databases.

To date, the following plugins are available.

- **CSV Importer Plugin.** The CSV importer plugin enables the user to
  perform batch imports of graphs given in CSV files. It is a generic
  plugin implemented to allow fast import of any property graph into
  an OrientDB graph database. The importer runs in a thread on the
  server-side to access the database without the overhead introduced
  by OrientDB’s access protocols. Several databases can be created in
  parallel using the importer plugin.

- **Shell Plugin.** The shell plugin is a server-side shell that
  provides database access via the general purpose scripting language
  Groovy and the traversal language Gremlin. Like the importer plugin,
  shells run as threads inside the server, giving them access to the
  database with low overhead. Multiple users can spawn shells on the
  server side to work on the database in parallel.

- **CFG Exporter Plugin.** The shell plugin allows control flow graphs
  of all functions to be exported in the standard format graphml. This
  allows external tools to analyze CFGs. Results can be communicated
  back to the server via the CSV importer plugin.

Bjoern-Radare
-------------

Bjoern-radare generates graph-based program representations from
binaries and outputs them in a CSV format. The resulting files can be
imported into the bjoern-server via the importer plugin. Under the
hood, bjoern-radare uses radare2 to perform an initial automatic
analysis of a binary and extract symbol information, control flow
graphs, and call graphs. Moreover, it translates machine code into
radare's intermediate language ESIL to allow platform independent
analysis of code.

Bjoern-Shell
------------

Bjoern-shell provides user-friendly access to the database via a
command line shell. It features a working environment with convenient
features like code completion, reverse search, and in-shell
documentation. It also features an awesome banner. Multiple
bjoern-shells can be used in parallel. Moreover, it is possible to
detach from a bjoern-shell and re-attach to it later on.

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

At heart, the Bjoern is an OrientDB server instance, extended with
plugins that are loaded at startup. These plugins expose the
platforms functionality via a REST API, which makes it possible to
invoke server functionality via HTTP requests. Bjoern-server extends
the language-agnostic server component Octopus with plugins for binary
analysis and provides a language for binary code analysis. In the
following, we describe the main components Bjoern is composed of.

Octopus
-------

Octopus is a server component that provides shell access to an
OrientDB graph database. It allows arbitrary property graphs to be
imported from CSV files that describe nodes and edges. This is
achieved using the OrientDBImporter, a generic library for batch
importing large property graphs into OrientDB.

In summary, octopus offers the following two primary features.

- **Import of CSV files.** The CSV importer enables the user to
  perform batch imports of graphs given in CSV files. It is a generic
  component implemented to allow fast import of any property graph into
  an OrientDB graph database. The importer runs in a thread on the
  server-side to access the database without the overhead introduced
  by OrientDB’s access protocols. Several databases can be created in
  parallel using the importer plugin.

- **Shell Access.** Octopus offers a server-side shell that
  provides database access via the general purpose scripting language
  Groovy and the traversal language Gremlin. Like importers, shells
  run as threads inside the server, giving them access to the database
  with low overhead. Multiple users can spawn shells on the server
  side to work on the database in parallel.

- **Execution of plugins.** Octopus offers a plugin interface that can
  be used to extend functionality at runtime. In particular, this
  allows language-dependent analysis algorithms to be executed on the
  graph database contents.

Bjoern-Radare
-------------

Bjoern-Radare generates graph-based program representations from
binaries and outputs them in a CSV format. The resulting files can be
imported into the octopus server. Under the hood, bjoern-radare uses
radare2 to perform an initial automatic analysis of a binary and
extract symbol information, control flow graphs, and call
graphs. Moreover, it translates machine code into radare's
intermediate language ESIL to allow platform independent analysis of
code.

Bjoern-plugins
--------------

Bjoern-plugins are a set of plugins that turn Octopus into a platform
for binary code analysis. On the one hand, these plugins allow
structures such as control flow graphs of functions to be exported, on
the other, they perform active computations on the database contents
to generate new nodes and edges.

Bjoern-lang and Octopus-lang
-----------------------------

Bjoern-lang and Octopus-lang provide a domain specific language for
binary code analysis and generic traversal of property graphs
respectively. These languages are realized as so called *steps* for
the graph-traversal language Gremlin.

Bjoern-Shell
------------

Bjoern-shell provides user-friendly access to the database via a
command line shell. It features a working environment with convenient
features like code completion, reverse search, and in-shell
documentation. It also features an awesome banner. Multiple
bjoern-shells can be used in parallel. Moreover, it is possible to
detach from a bjoern-shell and re-attach to it later on.

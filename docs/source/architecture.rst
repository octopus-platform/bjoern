Architecture
------------

Bjoern currently consists of the following components.

- **Radare Exporter.** The radare exporter extracts
  graph representations from binary code using operations of the
  reverse engineering swiss army knife radare2. It creates a CSV file
  containing the extracted nodes (nodes.csv), and two files that store
  edges (edges.csv, and keyedEdges.csv).

- **CSV Importer Plugin.** The CSV importer plugin enables the user to
  perform batch imports of graphs given in CSV files. It is a generic
  plugin implemented to allow fast import of any property graph into
  an OrientDB graph database. The importer runs in a thread on the
  server-side to access the database without the overhead introduced
  by OrientDB's access protocols. Several databases can be created in
  parallel using the importer plugin.

- **Shell Plugin.** The shell plugin is a server-side shell that
  provides database access via the general purpose scripting language
  Groovy and the traversal language Gremlin. Like the importer plugin,
  shells run as threads inside the server, giving them access to the
  database with low overhead. Multiple users can spawn shells on the
  server side to work on the database in parallel.

- **Bjoern Shell.** The bjoern-shell (bjosh) accesses server shells
  created by the shell plugin via TCP. It provides a working
  environment with convenient features like code completion, reverse
  search, and in-shell documentation. It also features an awesome
  banner.

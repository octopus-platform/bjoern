from octopus.server.orientdb.orientdb_shell_manager import OrientDBShellManager

DEFAULT_HOST = 'localhost'
DEFAULT_PORT = '2480'

class PythonShellInterface:

    def __init__(self):
        self._initializeDefaults()
        self.shell_manager = OrientDBShellManager(self.host, self.port)

    def _initializeDefaults(self):
        self.host = DEFAULT_HOST
        self.port = DEFAULT_PORT

    def setDatabaseName(self, databaseName):
        self.databaseName = databaseName

    def connectToDatabase(self):
        pass

    def runGremlinQuery(self, query):
        pass

    """
    Create chunks from a list of ids.
    This method is useful when you want to execute many independent
    traversals on a large set of start nodes. In that case, you
    can retrieve the set of start node ids first, then use 'chunks'
    to obtain disjoint subsets that can be passed to idListToNodes.
    """
    def chunks(self, idList, chunkSize):
        for i in xrange(0, len(idList), chunkSize):
            yield idList[i:i+chunkSize]

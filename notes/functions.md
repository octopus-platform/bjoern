OrientDB Functions on the server side
======================================

It should be possible to access orientdb-functions from Gremlin, but
it does not look pretty:

https://groups.google.com/forum/#!topic/orient-database/0xktIHvIxnc

g.getRawGraph().getMetadata().getFunctionLibrary().getFunction("sum").execute(3, 4);

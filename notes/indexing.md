The orientDBImporter automatically creates Lucene compound index for
the fields:

	[childNum,code,comment,nodeType,repr]

To query this index, a query such as:


	SELECT * FROM V WHERE [childNum,code,comment,nodeType,repr] LUCENE "(childNum:1) (repr:mov*) (nodeType:Ins*)"

can be used. The syntax proposed in the orientdb documentation using
the `AND` operator seems to not work.


Object.metaClass.queryNodeIndex = { luceneQuery ->
	import com.orientechnologies.orient.core.sql.OCommandSQL;
	queryStr = 'SELECT * FROM V WHERE [childNum,code,comment,nodeType,repr] LUCENE "' + luceneQuery + '"'
	query = new OCommandSQL(queryStr)
	g.getRawGraph().command(query).execute().toList()._()
}

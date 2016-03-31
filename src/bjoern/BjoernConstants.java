package bjoern;

import com.orientechnologies.orient.core.sql.OCommandSQL;

public class BjoernConstants
{

	private static final String[] INDEX_KEYS = {
			"addr", "childNum", "code", "comment", "esil", "key", "nodeType",
			"repr"
	};

	private static final String INDEX_NAME = "[addr,childNum,code,comment,"
			+ "esil,key,nodeType,repr]";

	public static final OCommandSQL LUCENE_QUERY = new OCommandSQL(
			"SELECT * FROM V WHERE " + INDEX_NAME + " LUCENE ?");

}

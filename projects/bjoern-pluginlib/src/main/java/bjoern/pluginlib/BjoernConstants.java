package bjoern.pluginlib;

import bjoern.structures.BjoernNodeProperties;
import com.orientechnologies.orient.core.sql.OCommandSQL;

public class BjoernConstants
{

	private static final String[] INDEX_KEYS = {
			BjoernNodeProperties.ADDR,
			BjoernNodeProperties.CODE,
			BjoernNodeProperties.COMMENT,
			BjoernNodeProperties.ESIL,
			BjoernNodeProperties.KEY,
			BjoernNodeProperties.TYPE,
			BjoernNodeProperties.REPR
	};

	public static final String INDEX_NAME = "[" + INDEX_KEYS.toString() + "]";

	public static final OCommandSQL LUCENE_QUERY = new OCommandSQL(
			"SELECT * FROM V WHERE " + INDEX_NAME + " LUCENE ?");

}

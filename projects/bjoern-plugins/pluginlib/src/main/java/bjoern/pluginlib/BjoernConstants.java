package bjoern.pluginlib;

import com.orientechnologies.orient.core.sql.OCommandSQL;

import bjoern.structures.BjoernNodeProperties;

public class BjoernConstants
{

	static {
		INDEX_NAME = generateIndexName();
	}

	private static final String[] INDEX_KEYS = {
			BjoernNodeProperties.ADDR, BjoernNodeProperties.CHILD_NUM,
			BjoernNodeProperties.CODE, BjoernNodeProperties.COMMENT,
			BjoernNodeProperties.ESIL, BjoernNodeProperties.KEY,
			BjoernNodeProperties.TYPE, BjoernNodeProperties.REPR
	};

	private static final String INDEX_NAME;

	public static final OCommandSQL LUCENE_QUERY = new OCommandSQL(
			"SELECT * FROM V WHERE " + INDEX_NAME + " LUCENE ?");

	private static String generateIndexName()
	{
		String retval = "[";
		for(String key : INDEX_KEYS){
			retval += key + ",";
		}
		retval = retval.substring(0, retval.length()-1) + "]";
		return retval;
	}

}

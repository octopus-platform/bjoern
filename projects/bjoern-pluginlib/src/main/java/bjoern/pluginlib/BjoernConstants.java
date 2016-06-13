package bjoern.pluginlib;

import com.orientechnologies.orient.core.sql.OCommandSQL;

import bjoern.structures.BjoernNodeProperties;

public class BjoernConstants
{

	private static final String[] INDEX_KEYS = {
			BjoernNodeProperties.ADDR, BjoernNodeProperties.CHILD_NUM,
			BjoernNodeProperties.CODE, BjoernNodeProperties.COMMENT,
			BjoernNodeProperties.ESIL, BjoernNodeProperties.KEY,
			BjoernNodeProperties.TYPE, BjoernNodeProperties.REPR
	};

	public static final String INDEX_NAME = "[" +
			BjoernNodeProperties.ADDR + "," + BjoernNodeProperties.CHILD_NUM + ","
			+ BjoernNodeProperties.CODE + "," + BjoernNodeProperties.COMMENT + ","
			+ BjoernNodeProperties.ESIL +  "," + BjoernNodeProperties.KEY + ","
			+ BjoernNodeProperties.TYPE + "," + BjoernNodeProperties.REPR +"]";


	public static final OCommandSQL LUCENE_QUERY = new OCommandSQL(
			"SELECT * FROM V WHERE " + INDEX_NAME + " LUCENE ?");

}

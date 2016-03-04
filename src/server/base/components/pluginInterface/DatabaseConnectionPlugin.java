package server.base.components.pluginInterface;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import server.base.Constants;

public abstract class DatabaseConnectionPlugin implements IPlugin
{
	private static final int MAX_POOL_SIZE = 10;
	private OrientGraphFactory graphFactory;

	public void open(String databaseName)
	{
		graphFactory = new OrientGraphFactory(Constants.PLOCAL_REL_PATH_TO_DBS + databaseName)
				.setupPool(1, MAX_POOL_SIZE);
	}

	public void close()
	{
		graphFactory.close();
	}

	public Graph getGraphInstance()
	{
		return graphFactory.getNoTx();
	}

}

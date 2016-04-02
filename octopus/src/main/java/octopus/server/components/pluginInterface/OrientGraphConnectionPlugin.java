package octopus.server.components.pluginInterface;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import octopus.server.Constants;

import org.json.JSONObject;

public abstract class OrientGraphConnectionPlugin implements IPlugin
{
	private static final int MAX_POOL_SIZE = 10;
	private OrientGraphFactory graphFactory;
	private String databaseName;

	@Override
	public void configure(JSONObject settings)
	{
		setDatabaseName(settings.getString("database"));
	}

	@Override
	public void beforeExecution() throws Exception
	{
		open();
	}

	@Override
	public void afterExecution() throws Exception
	{
		close();
	}

	public OrientGraphNoTx getNoTxGraphInstance()
	{
		return graphFactory.getNoTx();
	}

	public OrientGraph getGraphInstance()
	{
		return graphFactory.getTx();
	}

	public String getDatabaseName()
	{
		return databaseName;
	}

	protected void open()
	{
		graphFactory = new OrientGraphFactory(
				Constants.PLOCAL_REL_PATH_TO_DBS + getDatabaseName())
				.setupPool(
				1, MAX_POOL_SIZE);
	}

	protected void close()
	{
		graphFactory.close();
	}

	protected void setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
	}

}


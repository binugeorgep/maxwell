package com.zendesk.maxwell.bootstrap;

import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.replication.Replicator;
import com.zendesk.maxwell.row.RowMap;
import com.zendesk.maxwell.producer.AbstractProducer;

import java.io.IOException;
import java.sql.SQLException;

public abstract class AbstractBootstrapper {

	protected MaxwellContext context;

	public AbstractBootstrapper(MaxwellContext context) { this.context = context; }

	public boolean isStartBootstrapRow(RowMap row) {
		return isBootstrapRow(row) &&
			row.getData("started_at") == null &&
			row.getData("completed_at") == null &&
			( long ) row.getData("is_complete") == 0;
	}

	public boolean isCompleteBootstrapRow(RowMap row) {
		return isBootstrapRow(row) &&
			row.getData("started_at") != null &&
			row.getData("completed_at") != null &&
			( long ) row.getData("is_complete") == 1;
	}

	public boolean isBootstrapRow(RowMap row) {
		return row.getDatabase().equals(this.context.getConfig().databaseName) &&
			row.getTable().equals("bootstrap");
	}

	protected String bootstrapDatabase(RowMap rowmap) {
		return (String) rowmap.getData("database_name");
	}

	protected String bootstrapTable(RowMap rowmap) {
		return (String) rowmap.getData("table_name");
	}

	protected String bootstrapField(RowMap rowmap) {
		return (String) rowmap.getData("field_name");
	}

	protected String bootstrapStart(RowMap rowmap) {
		return (String) rowmap.getData("start_date");
	}

	abstract public boolean shouldSkip(RowMap row) throws SQLException, IOException;

	abstract public void startBootstrap(RowMap startBootstrapRow, AbstractProducer producer, Replicator replicator) throws Exception;

	abstract public void completeBootstrap(RowMap completeBootstrapRow, AbstractProducer producer, Replicator replicator) throws Exception;

	public abstract void resume(AbstractProducer producer, Replicator replicator) throws Exception;

	public abstract boolean isRunning();

	public abstract void work(RowMap row, AbstractProducer producer, Replicator replicator) throws Exception;
}

package org.molgenis.compute.db.util;

import app.DatabaseFactory;
import org.molgenis.compute.commandline.WorksheetHelper;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.db.generator.ComputeGenerator;
import org.molgenis.compute.db.generator.ComputeGeneratorDBWorksheet;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.util.Tuple;
import org.molgenis.util.cmdline.CmdLineException;

import java.io.File;
import java.util.List;

public class WorksheetImporter
{

	public static void main(String[] args) throws CmdLineException, DatabaseException
	{
		System.out.println(">> Start..");

		// read the parameters
		ImportWorksheetOptions options = new ImportWorksheetOptions(args);

		// get workflow from database
		Database db = DatabaseFactory.create();
		Query<Workflow> wf_query = db.query(Workflow.class).eq(Workflow.NAME, options.workflow_name);
		if (wf_query.count() < 1) throw new RuntimeException("Workflow name=" + options.workflow_name
				+ " not known in database. Please import");
		Workflow workflow = wf_query.find().get(0);

        List<ComputeParameter> parameters = db.query(ComputeParameter.class).find();

		// load targets from a worksheet
		List<Tuple> worksheet = null;
		try
		{
			worksheet = (new WorksheetHelper()).readTuplesFromFile(new File(options.worksheet_file));
			// "/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/workflows/in-house_worksheet.csv"));
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		for (Tuple t : worksheet)
		{
			t.set("McId", options.McId);
			t.set("McDir", "database");
			t.set("McWorksheet", options.worksheet_file);
			t.set("McParameters", "database");
			t.set("McWorkflow", "database");
			t.set("McProtocols", "database");
		}

		// generate ComputeTasks
		ComputeGenerator generator = new ComputeGeneratorDBWorksheet();

		generator.generateTasks(workflow, parameters, worksheet, options.backend_name);

		System.out.println("... generated");
	}
}

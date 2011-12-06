package plugins.LLcatalogueTree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tuple;

public class LLcatalogueTreePlugin extends PluginModel<Entity> {
	
	private String Status;
	
	private static final long serialVersionUID = -6143910771849972946L;

	private JQueryTreeViewMeasurement<JQueryTreeViewElementMeasurement> treeView = null;

	private HashMap<String, Protocol> nameToProtocol;

	private HashMap<String, JQueryTreeViewElementMeasurement> labelToTree;

	private List<Measurement> shoppingCart = new ArrayList<Measurement>();

	public String MeasurementName = "------------------------------------->";

	public LLcatalogueTreePlugin(String name, ScreenController<?> parent) {
		super(name, parent);

	}
	
	/** My new action */
	public void handleRequest(Database db, Tuple request) {

		System.out.println("CAUGHT IT: " + request);
		System.out.println(request.getInt("measurementId"));
		try {

			Measurement selected = Measurement.findById(db,
					request.getInt("measurementId"));
			if (selected == null) {
				this.setError("measurementId unknown: "
						+ request.getInt("measurementId"));
			} else {

				System.out.println("---------------------->" + selected);

				this.shoppingCart.add(selected);
				
				// clean the ordered measurement list form dublicates
				this.shoppingCart = cleanShoppingCart();
				
				//TODO : SHow X to remove items. 
			}
		} catch (Exception e) {
			this.setError(e.getMessage());
		}
		
		if ("OrderMeasurements".equals(request.getAction())) {
			
			this.shoppingCart = cleanShoppingCart();
			
			
			this.setStatus("<h4>You order is being processed.</h4>" ) ;
		}		

	}

	private List<Measurement> cleanShoppingCart() {
		List<Measurement> cleanShoppingCart = new ArrayList<Measurement>(); 
		
		for (int i=0; i<this.getShoppingCart().size(); i++) {
			Measurement m = this.getShoppingCart().get(i);
			if (!cleanShoppingCart.contains(m)) cleanShoppingCart.add(m);
		}
		
		return cleanShoppingCart;
	}

	public void recursiveAddingTree(List<String> parentNode,
			JQueryTreeViewElementMeasurement parentTree, Database db) {

		for (String protocolName : parentNode) {

			Protocol protocol = nameToProtocol.get(protocolName);

			if (protocol != null) {

				JQueryTreeViewElementMeasurement childTree;

				if (labelToTree.containsKey(protocolName)) {

					childTree = labelToTree.get(protocolName);

				} else {

					childTree = new JQueryTreeViewElementMeasurement(
							protocolName, parentTree);
					childTree.setCollapsed(true);
					labelToTree.put(protocolName, childTree);
				}

				if (protocol.getSubprotocols_Name() != null
						&& protocol.getSubprotocols_Name().size() > 0) {

					recursiveAddingTree(protocol.getSubprotocols_Name(),
							childTree, db);

				}
				if (protocol.getFeatures_Name() != null
						&& protocol.getFeatures_Name().size() > 0) {

					addingMeasurementTotree(protocol.getFeatures_Name(),
							childTree, db);
				}
			}
		}
	}

	// @Override
	// public String getCustomHtmlBodyOnLoad()
	// {
	//
	//
	// // JQuerySplitterContents c = new JQuerySplitterContents();
	// // JQuerySplitter2<JQuerySplitterContents> jqs2 = new
	// JQuerySplitter2<JQuerySplitterContents>("aaa", c);
	//
	//
	// return treeView.toHtml();;
	//
	// }

	public void addingMeasurementTotree(List<String> childNode,
			JQueryTreeViewElementMeasurement parentTree, Database db) {

		String url = "molgenis.do?__target=catalogueOverview&select=measurement";

		// System.out.println(childNode);

		List<Measurement> measurementList;
		try {
			measurementList = db.find(Measurement.class, new QueryRule(
					Measurement.NAME, Operator.IN, childNode));

			for (Measurement measurement : measurementList) {

				JQueryTreeViewElementMeasurement childTree;

				if (labelToTree.containsKey(measurement.getName())) {

					childTree = labelToTree.get(measurement.getName());

				} else {

					childTree = new JQueryTreeViewElementMeasurement(
							measurement, parentTree, url);

					labelToTree.put(measurement.getName(), childTree);
				}
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getViewName() {
		return "plugins_LLcatalogueTree_LLcatalogueTreePlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/LLcatalogueTree/LLcatalogueTreePlugin.ftl";
	}

	

	@Override
	public void reload(Database db) {

		List<String> topProtocols = new ArrayList<String>();

		List<String> bottomProtocols = new ArrayList<String>();

		List<String> middleProtocols = new ArrayList<String>();

		labelToTree = new HashMap<String, JQueryTreeViewElementMeasurement>();

		nameToProtocol = new HashMap<String, Protocol>();

		try {
			
			for (Protocol p : db.find(Protocol.class, 
					new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, "DataShaper"))) {

				// System.out.println(p.getName());

				List<String> subNames = p.getSubprotocols_Name();

				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

				if (!subNames.isEmpty()) {

					if (!topProtocols.contains(p.getName()))
						topProtocols.add(p.getName());

					for (String subProtocol : subNames) {

						if (!middleProtocols.contains(subProtocol))
							middleProtocols.add(subProtocol);
					}

				} else {

					if (!bottomProtocols.contains(p.getName())) {
						bottomProtocols.add(p.getName());

					}
				}
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		middleProtocols.removeAll(bottomProtocols);

		topProtocols.removeAll(middleProtocols);

		JQueryTreeViewElementMeasurement protocolsTree = new JQueryTreeViewElementMeasurement(
				"Protocols", null);

		if(topProtocols.size() == 0){
			recursiveAddingTree(bottomProtocols, protocolsTree, db);

		}else{
			recursiveAddingTree(topProtocols, protocolsTree, db);
		}

		treeView = new JQueryTreeViewMeasurement<JQueryTreeViewElementMeasurement>(
				"Protocols", protocolsTree);

		// a = db.query();
		treeView.setMeasurementDetails("Measurement details........... ");

	}

	@Override
	public boolean isVisible() {
		if (!this.getLogin().isAuthenticated()) {
			return false;
		}
		return true;
	}

	public String getTreeView() {
		return treeView.toHtml();
	}

	public String getMeasurementDetails() {
		return "getMeasurementDetails";
	}

	public List<Measurement> getShoppingCart() {
		return shoppingCart;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}

}

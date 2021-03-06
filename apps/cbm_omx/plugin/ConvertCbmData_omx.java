package plugin;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CollectionProtocol;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Diagnosis;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.ParticipantCollectionSummary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.molgenis.cbm_omx.CbmXmlParser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.io.csv.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.tuple.KeyValueTuple;

public class ConvertCbmData_omx extends PluginModel<Entity>
{

	private File currentFile;

	private static final long serialVersionUID = -6143910771849972946L;

	public ConvertCbmData_omx(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_ConvertCbmData_omx";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugin/ConvertCbmData_omx.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request) throws Exception
	{
		if (request.getString("__action").equals("upload"))
		{
			// get uploaded file and do checks
			File file = request.getFile("uploadData");

			if (file == null)
			{
				throw new Exception("No file selected.");
			}
			else if (!file.getName().endsWith(".xml"))
			{
				throw new Exception("File does not end with '.xml', other formats are not supported.");
			}
			// TODO: Change this to generic arguments
			String path = "/Users/Roan/Work/CBM/output/";
			// Create the files needed
			CsvWriter dataParticipant = new CsvWriter(new File(path + "dataset_participant_cs.csv"));
			CsvWriter observableFeature = new CsvWriter(new File(path + "observablefeature.csv"));
			// TODO: These are closed at the end, suppress warnings??
			CsvWriter dataSetCollectionProtocol = new CsvWriter(new File(path + "dataset_collectionprotocols.csv"));
			CsvWriter dataSet = new CsvWriter(new File(path + "dataset.csv"));
			CsvWriter protocol = new CsvWriter(new File(path + "protocol.csv"));
			try
			{

				// Create tuples
				KeyValueTuple kvtFeature = new KeyValueTuple();
				KeyValueTuple kvtdataSet = new KeyValueTuple();
				KeyValueTuple kvtdataSetCollectionProtocol = new KeyValueTuple();
				KeyValueTuple kvtdataParticipant = null;
				KeyValueTuple kvtprotocol_Collection_Protocol = new KeyValueTuple();
				// get uploaded file and do checks
				File currentXsdfile = new File("Webcontent/res/schema/CBM.xsd");
				// if no error, set file, and continue
				this.setCurrentFile(file);
				// Here the actual data is going to be imported.
				CbmXmlParser cbmXmlParser = new CbmXmlParser();

				CbmNode result = cbmXmlParser.load(currentFile, currentXsdfile);

				List<String> listOfCollectionProtocolFeatures = new ArrayList<String>();

				// These are the headers of the PROTOCOL entity
				protocol.writeColNames(Arrays.asList("identifier", "name", "features_identifier",
						"subprotocols_identifier"));
				// These are the headers of the FEATURE entity
				observableFeature.writeColNames(Arrays.asList("identifier", "name"));

				// These are the headers for DATASET entity
				dataSet.writeColNames(Arrays.asList("identifier", "name", "protocolused_identifier"));

				List<String> listOfAllFeatures = Arrays.asList("row_identifier", "isCollaborationRequired",
						"isAvailableToOutsideInstitution", "isAvailableToForeignInvestigators",
						"isAvailableToCommercialOrganizations", "hasTreatmentInformation",
						"hasParticipantsAvailableForFollowup", "hasOutcomeInformation", "hasMatchedSpecimens",
						"hasLongitudinalSpecimens", "hasLabData", "hasHistopathologicInformation", "hasFamilyHistory",
						"hasExposureHistory", "hasAdditionalPatientDemographics", "emailAddress", "lastName",
						"firstName", "fullName", "streetOrThoroughfareNameAndType", "state", "zipCode", "country",
						"city", "enrolls_row_identifier", "enrollsRef", "pcs", "race", "specimenId", "specimenCount",
						"diagnosis");

				List<String> listOfEnrollsFeatures = Arrays.asList("pcs", "enrolls_row_identifier", "race",
						"specimenId", "specimenCount", "diagnosis");
				StringBuilder enrollsFeatures = new StringBuilder();
				// make enroll features StringBuilder
				for (String feature : listOfEnrollsFeatures)
				{
					enrollsFeatures.append(feature + ",");
				}
				String enrollsFeat = enrollsFeatures.substring(0, enrollsFeatures.length() - 1);

				protocol.writeColNames(Arrays.asList("row_identifier", "isCollaborationRequired",
						"isAvailableToOutsideInstitution", "isAvailableToForeignInvestigators",
						"isAvailableToCommercialOrganizations", "hasTreatmentInformation",
						"hasParticipantsAvailableForFollowup", "hasOutcomeInformation", "hasMatchedSpecimens",
						"hasLongitudinalSpecimens", "hasLabData", "hasHistopathologicInformation", "hasFamilyHistory",
						"hasExposureHistory", "hasAdditionalPatientDemographics", "emailAddress", "lastName",
						"firstName", "fullName", "streetOrThoroughfareNameAndType", "state", "zipCode", "country",
						"city", "enrolls_row_identifier"));

				StringBuilder allFeatures = new StringBuilder();
				for (String feature : listOfAllFeatures)
				{

					kvtFeature.set("identifier", feature);
					kvtFeature.set("name", feature);
					listOfCollectionProtocolFeatures.add(feature);
					observableFeature.write(kvtFeature);
					allFeatures.append(feature + ",");

				}
				dataSetCollectionProtocol.writeColNames(Arrays.asList("row_identifier", "isCollaborationRequired",
						"isAvailableToOutsideInstitution", "isAvailableToForeignInvestigators",
						"isAvailableToCommercialOrganizations", "hasTreatmentInformation",
						"hasParticipantsAvailableForFollowup", "hasOutcomeInformation", "hasMatchedSpecimens",
						"hasLongitudinalSpecimens", "hasLabData", "hasHistopathologicInformation", "hasFamilyHistory",
						"hasExposureHistory", "hasAdditionalPatientDemographics", "emailAddress", "lastName",
						"firstName", "fullName", "streetOrThoroughfareNameAndType", "state", "zipCode", "country",
						"city", "enrolls_row_identifier"));
				// Write the headers for the dataset CollectionProtocol

				String allFeat = allFeatures.substring(0, allFeatures.length() - 1);

				// create protocol collectionProtocols , containing a list of
				// features (allFeat) and the subprotocol is pcs
				kvtprotocol_Collection_Protocol = new KeyValueTuple();
				kvtprotocol_Collection_Protocol.set("identifier", "collectionprot_protocol");
				kvtprotocol_Collection_Protocol.set("name", "collectionprot_protocol");
				kvtprotocol_Collection_Protocol.set("features_identifier", allFeat);
				kvtprotocol_Collection_Protocol.set("subprotocols_identifier", "protocol_participant");
				protocol.write(kvtprotocol_Collection_Protocol);

				// Add allcollectionprotocols to the dataSet entity
				kvtdataSet.set("identifier", "collectionprotocols");
				kvtdataSet.set("name", "allcollectionprotocols");
				kvtdataSet.set("protocolused_identifier", "collectionprot_protocol");
				dataSet.write(kvtdataSet);

				kvtdataSet = new KeyValueTuple();
				kvtdataSet.set("identifier", "participant_cs");
				kvtdataSet.set("name", "participant_cs");
				kvtdataSet.set("protocolused_identifier", "protocol_participant");
				dataSet.write(kvtdataSet);

				// Make headers for every new dataParticipant Entity
				// dataParticipant.writeColNames(Arrays.asList("pcs",
				// "enrolls_row_identifier", "race", "specimenId",
				// "specimenCount", "diagnosis"));
				dataParticipant.writeColNames(listOfEnrollsFeatures);

				StringBuilder buildSubprotocols = new StringBuilder();
				for (CollectionProtocol collectionProtocolFromJaxb : result.getProtocols().getCollectionProtocol())
				{

					// Create for every collectionprotocol a new dataset
					String collectionIdentifier = collectionProtocolFromJaxb.getIdentifier() != null ? collectionProtocolFromJaxb
							.getIdentifier() : collectionProtocolFromJaxb.getId().toString();

					buildSubprotocols.append("protocol_" + collectionIdentifier + ",");

					kvtdataSetCollectionProtocol = new KeyValueTuple();
					kvtdataSetCollectionProtocol.set("row_identifier", collectionIdentifier);

					kvtdataSetCollectionProtocol.set("isCollaborationRequired", collectionProtocolFromJaxb
							.getIsConstrainedBy().isIsCollaborationRequired().toString());

					kvtdataSetCollectionProtocol.set("isAvailableToOutsideInstitution", collectionProtocolFromJaxb
							.getIsConstrainedBy().isIsAvailableToOutsideInstitution().toString());

					kvtdataSetCollectionProtocol.set("isAvailableToForeignInvestigators", collectionProtocolFromJaxb
							.getIsConstrainedBy().isIsAvailableToForeignInvestigators().toString());

					kvtdataSetCollectionProtocol.set("isAvailableToCommercialOrganizations", collectionProtocolFromJaxb
							.getIsConstrainedBy().isIsAvailableToCommercialOrganizations().toString());

					kvtdataSetCollectionProtocol.set("hasOutcomeInformation", collectionProtocolFromJaxb
							.getMakesAvailable().isHasOutcomeInformation().toString());

					kvtdataSetCollectionProtocol.set("hasMatchedSpecimens", collectionProtocolFromJaxb
							.getMakesAvailable().isHasMatchedSpecimens().toString());

					kvtdataSetCollectionProtocol.set("hasTreatmentInformation", collectionProtocolFromJaxb
							.getMakesAvailable().isHasTreatmentInformation().toString());

					kvtdataSetCollectionProtocol.set("hasHistopathologicInformation", collectionProtocolFromJaxb
							.getMakesAvailable().isHasHistopathologicInformation().toString());

					kvtdataSetCollectionProtocol.set("hasParticipantsAvailableForFollowup", collectionProtocolFromJaxb
							.getMakesAvailable().isHasParticipantsAvailableForFollowup().toString());

					kvtdataSetCollectionProtocol.set("hasLongitudinalSpecimens", collectionProtocolFromJaxb
							.getMakesAvailable().isHasLongitudinalSpecimens().toString());

					kvtdataSetCollectionProtocol.set("hasLabData", collectionProtocolFromJaxb.getMakesAvailable()
							.isHasLabData().toString());

					kvtdataSetCollectionProtocol.set("hasFamilyHistory", collectionProtocolFromJaxb.getMakesAvailable()
							.isHasFamilyHistory().toString());

					kvtdataSetCollectionProtocol.set("hasExposureHistory", collectionProtocolFromJaxb
							.getMakesAvailable().isHasExposureHistory().toString());

					kvtdataSetCollectionProtocol.set("hasAdditionalPatientDemographics", collectionProtocolFromJaxb
							.getMakesAvailable().isHasAdditionalPatientDemographics().toString());

					kvtdataSetCollectionProtocol.set("emailAddress", collectionProtocolFromJaxb.getIsAssignedTo()
							.getEmailAddress().toString());

					kvtdataSetCollectionProtocol.set("lastName", collectionProtocolFromJaxb.getIsAssignedTo()
							.getLastName().toString());

					kvtdataSetCollectionProtocol.set("firstName", collectionProtocolFromJaxb.getIsAssignedTo()
							.getFirstName().toString());

					kvtdataSetCollectionProtocol.set("fullName", collectionProtocolFromJaxb.getIsAssignedTo()
							.getFullName().toString());

					kvtdataSetCollectionProtocol.set("streetOrThoroughfareNameAndType", collectionProtocolFromJaxb
							.getIsAssignedTo().getIsLocatedAt().getStreetOrThoroughfareNameAndType().toString());

					kvtdataSetCollectionProtocol.set("state", collectionProtocolFromJaxb.getIsAssignedTo()
							.getIsLocatedAt().getState().toString());

					kvtdataSetCollectionProtocol.set("zipCode", collectionProtocolFromJaxb.getIsAssignedTo()
							.getIsLocatedAt().getZipCode().toString());

					kvtdataSetCollectionProtocol.set("country", collectionProtocolFromJaxb.getIsAssignedTo()
							.getIsLocatedAt().getCountry().toString());

					kvtdataSetCollectionProtocol.set("city", collectionProtocolFromJaxb.getIsAssignedTo()
							.getIsLocatedAt().getCity().toString());
					kvtdataSetCollectionProtocol.set("enrolls_row_identifier", "enrolls_row_identifier"
							+ collectionIdentifier);
					// ****
					// TODO FIXME IDONTKNOW if this should be in or not
					// FIXME: this is what you WANT to do: value is the link to
					// the
					// dataset
					// enrollsRef.setValue(pcs_dataset);
					// not possible, STRING ONLY, so:
					/**
					 * TODO: create a value which links to the enrolls dataset
					 * (think: nested dataset!)
					 */

					// *****
					dataSetCollectionProtocol.write(kvtdataSetCollectionProtocol);

					kvtprotocol_Collection_Protocol = new KeyValueTuple();
					kvtprotocol_Collection_Protocol.set("identifier", "protocol_" + collectionIdentifier);
					kvtprotocol_Collection_Protocol.set("name", "protocol_" + collectionIdentifier);
					kvtprotocol_Collection_Protocol.set("features_identifier", enrollsFeat);
					protocol.write(kvtprotocol_Collection_Protocol);

					for (ParticipantCollectionSummary pcsummary : collectionProtocolFromJaxb.getEnrolls()
							.getParticipantCollectionSummary())
					{

						kvtdataParticipant = new KeyValueTuple();

						kvtdataParticipant.set("pcs", "pcs_id_" + pcsummary.getId());

						if (pcsummary.getIsClassifiedBy().getRace().size() > 1)
						{

							throw new IOException("More than one race found for PCS " + pcsummary.getId());
						}

						kvtdataParticipant.set("enrolls_row_identifier", "enrolls_row_identifier"
								+ collectionIdentifier);

						kvtdataParticipant.set("race", pcsummary.getIsClassifiedBy().getRace().get(0).getRace());

						if (pcsummary.getProvides().getSpecimenCollectionSummary().size() > 1)
						{
							throw new IOException("More than one SpecimenCollectionSummary found for PCS "
									+ pcsummary.getId());

						}

						kvtdataParticipant.set("specimenId", pcsummary.getProvides().getSpecimenCollectionSummary()
								.get(0).getId().toString());
						kvtdataParticipant.set("specimenCount", pcsummary.getProvides().getSpecimenCollectionSummary()
								.get(0).getSpecimenCount().toString());

						StringBuilder diagnosisStringBuilder = new StringBuilder();

						for (Diagnosis dia : pcsummary.getReceives().getDiagnosis())
						{
							diagnosisStringBuilder.append(dia.getDiagnosisType() + ",");

						}
						if (pcsummary.getReceives().getDiagnosis().size() >= 1)
						{

							String diagnosisString = diagnosisStringBuilder.substring(0,
									diagnosisStringBuilder.length() - 1);
							kvtdataParticipant.set("diagnosis", diagnosisString);
						}
						// write participant file
						dataParticipant.write(kvtdataParticipant);
					}

				}
				String allSubprotocolsParticipants = buildSubprotocols.substring(0, buildSubprotocols.length() - 1);
				// create the pcs protocol, containing a list of features
				// (enrollsFeat)
				kvtprotocol_Collection_Protocol = new KeyValueTuple();
				kvtprotocol_Collection_Protocol.set("identifier", "protocol_participant");
				kvtprotocol_Collection_Protocol.set("name", "protocol_participant");
				kvtprotocol_Collection_Protocol.set("subprotocols_identifier", allSubprotocolsParticipants);
				protocol.write(kvtprotocol_Collection_Protocol);
				// Write and close collectionprotocol file
				dataSetCollectionProtocol.write(kvtdataSetCollectionProtocol);
			}
			finally
			{
				IOUtils.closeQuietly(dataParticipant);
				IOUtils.closeQuietly(protocol);
				IOUtils.closeQuietly(dataSetCollectionProtocol);
				IOUtils.closeQuietly(dataSet);
				IOUtils.closeQuietly(observableFeature);

			}

		}
	}

	private void setCurrentFile(File file)
	{
		this.currentFile = file;

	}

	private File getCurrentFile()
	{
		return this.currentFile;

	}

	@Override
	public void reload(Database db)
	{

	}

	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		// e.g.
		// if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}

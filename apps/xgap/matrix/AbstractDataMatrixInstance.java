package matrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import matrix.general.MatrixReadException;

import org.apache.log4j.Logger;
import org.molgenis.core.Nameable;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.legacy.AbstractSliceableMatrix;
import org.molgenis.matrix.component.legacy.RenderDescriptor;
import org.molgenis.matrix.component.legacy.SourceMatrix;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.CsvWriter;

import com.pmstation.spss.SPSSWriter;

/**
 * Abstract implementation for MatrixInterface. Some functions require XGAP
 * components, therefore this class should be seperated into Matrix.java, a
 * generic abstract class with functions that work on generic types, and
 * XgapMatrix.java, with functions that require a few XGAP specific data types.
 * 
 * @author Morris Swertz, Joeri van der Velde
 * @param <E>
 *            the generic type of the matrix. E.g. String, Double etc.
 */
public abstract class AbstractDataMatrixInstance<E> extends
		AbstractSliceableMatrix<ObservationElement, ObservationElement, Object>
		implements DataMatrixInstance,
		SourceMatrix<ObservationElement, ObservationElement, Object>,
		BasicMatrix<ObservationElement, ObservationElement, Object>,
		RenderDescriptor<ObservationElement, ObservationElement, Object>
{

	/**
	 * Protected method for subclasses to add the Data description. This method
	 * MUST be called in the constructor to instantiate the matrix.
	 */
	protected void setData(Data data)
	{
		this.data = data;
	}

	/**
	 * Protected method for subclasses to label the columns. This method MUST be
	 * called in the constructor to instantiate the matrix.
	 */
	protected void setColNames(List<String> list)
	{
		this.colNames = list;
	}

	/**
	 * Protected method for subclasses to label the rows. This method MUST be
	 * called in the constructor to instantiate the matrix.
	 */
	protected void setRowNames(List<String> list)
	{
		this.rowNames = list;
	}

	/**
	 * Protected method to set number of columns. This method MUST be called in
	 * the constructor to instantiate the matrix.
	 */
	protected void setNumberOfCols(int numberOfCols)
	{
		this.numberOfCols = numberOfCols;
	}

	/**
	 * Protected method to set number of rows. This method MUST be called in the
	 * constructor to instantiate the matrix.
	 */
	protected void setNumberOfRows(int numberOfRows)
	{
		this.numberOfRows = numberOfRows;
	}

	// Local variables
	Logger logger = Logger.getLogger(getClass().getSimpleName());
	private Data data;
	private List<String> rowNames;
	private List<String> colNames;
	private int numberOfRows;
	private int numberOfCols;

	// Implementations of MatrixInterface
	public Data getData()
	{
		return data;
	}

	public Object[] getCol(String colName) throws Exception
	{
		return getCol(colNames.indexOf(colName));
	}

	public Object[] getRow(String rowName) throws Exception
	{
		return getRow(rowNames.indexOf(rowName));
	}

	/**
	 * Helper to convert ObservationElement to String
	 */
	public AbstractDataMatrixInstance<Object> getSubMatrixByObservationElement(
			List<ObservationElement> rows, List<ObservationElement> cols)
			throws MatrixException
	{
		List<String> rowNames = new ArrayList<String>();
		for (ObservationElement row : rows)
		{
			rowNames.add(row.getName());
		}

		List<String> colNames = new ArrayList<String>();
		for (ObservationElement col : cols)
		{
			colNames.add(col.getName());
		}

		return getSubMatrix(rowNames, colNames);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrix(
			List<String> rowNames, List<String> colNames) throws MatrixException
	{
		int[] rowIndices = new int[rowNames.size()];
		int[] colIndices = new int[colNames.size()];

		for (int i = 0; i < rowNames.size(); i++)
		{
			rowIndices[i] = this.rowNames.indexOf(rowNames.get(i));
		}

		for (int i = 0; i < colNames.size(); i++)
		{
			colIndices[i] = this.colNames.indexOf(colNames.get(i));
		}

		return getSubMatrix(rowIndices, colIndices);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(
			String rowName, int nRows, String colName, int nCols)
			throws Exception
	{
		return getSubMatrixByOffset(this.rowNames.indexOf(rowName), nRows,
				this.colNames.indexOf(colName), nCols);
	}

	public int getRowIndexForName(String rowName) throws Exception
	{
		if (!rowNames.contains(rowName)) throw new MatrixReadException(
				"rowname " + rowName + " not known in matrix");
		return this.rowNames.indexOf(rowName);
	}

	public int getColIndexForName(String colName) throws Exception
	{
		if (!colNames.contains(colName)) throw new MatrixReadException(
				"colname " + colName + " not known in matrix");
		return this.colNames.indexOf(colName);
	}

	public Object getElement(String rowName, String colName) throws Exception
	{
		return getElement(this.getRowIndexForName(rowName),
				this.getColIndexForName(colName));
	}
	
	public void toPrintStream(PrintStream p)
	{
		try
		{
			for (String col : getColNames())
			{
				p.append("\t" + col);
			}
			p.append("\n");
			Object[][] elements = getElements();
			for(int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++)
			{
				p.append(getRowNames().get(rowIndex));
				for(int colIndex = 0; colIndex < this.getNumberOfCols(); colIndex++)
				{
					if (elements[rowIndex][colIndex] == null)
					{
						p.append("\t");
					}
					else
					{
						p.append("\t" + elements[rowIndex][colIndex]);
					}
				}
				p.append("\n");
				p.flush();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();
		try
		{
			for (String col : getColNames())
			{
				result.append("\t" + col);
			}
			result.append("\n");
			Object[][] elements = getElements();
			for(int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++)
			{
				result.append(getRowNames().get(rowIndex));
				for(int colIndex = 0; colIndex < this.getNumberOfCols(); colIndex++)
				{
					if (elements[rowIndex][colIndex] == null)
					{
						result.append("\t");
					}
					else
					{
						result.append("\t" + elements[rowIndex][colIndex]);
					}
				}
				result.append("\n");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result.toString();
	}
	
	public File getAsSpssFile() throws Exception
	{
		File spssFile = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + this.getData().getName() + ".sav");
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(spssFile));
		SPSSWriter spssWriter = new SPSSWriter(out, "windows-1252");
		spssWriter.setCalculateNumberOfCases(false);
		spssWriter.addDictionarySection(-1);
		
		Object[][] elements = this.getElements();
		
		if(this.getData().getValueType().equals("Decimal"))
		{
			for(String colName : this.colNames)
			{
				spssWriter.addNumericVar(colName, 10, 10, colName);
			}
			
			spssWriter.addDataSection();
			
			for(int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++)
			{
				for(int colIndex = 0; colIndex < this.getNumberOfCols(); colIndex++)
				{
					Object val = elements[rowIndex][colIndex];
					if(val == null)
					{
						spssWriter.addData(0.0); //FIXME: has to be added to prevent shifts, I suppose?
					}
					else
					{
						spssWriter.addData(Double.valueOf(val.toString()));
					}
				}
			}
		}
		else if (this.getData().getValueType().equals("Text"))
		{
			for(String colName : this.colNames)
			{
				spssWriter.addStringVar(colName, 10, colName);
			}
			
			spssWriter.addDataSection();
			
			for(int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++)
			{
				for(int colIndex = 0; colIndex < this.getNumberOfCols(); colIndex++)
				{
					Object val = elements[rowIndex][colIndex];
					if(val == null)
					{
						spssWriter.addData(""); //FIXME: correct?
					}
					else
					{
						spssWriter.addData(val.toString());	
					}
					
				}
			}
		}
		else
		{
			throw new Exception("Value type '" + this.getData().getValueType() + "' unknown");
		}
		
		spssWriter.addFinishSection();
		out.close();
		
		return spssFile;
	}
	
	public String getAsRobject() throws Exception
	{
		//e.g.
//		mdat <- matrix(
//				  nrow = 2,
//				  ncol=3,
//				  byrow=TRUE,
//				dimnames = list(c(
//				  "row1","row2"),c(
//				  "C.1","C.2","C.3")),
//				data = c(
//				  1,2,3,
//				  11,12,13))

		
		StringBuffer result = new StringBuffer();
		result.append(this.getData().getName() + " <- matrix(\n");
		result.append("\tnrow = " + this.getNumberOfRows() + ",\n");
		result.append("\tncol = " + this.getNumberOfCols() + ",\n");
		result.append("\tbyrow=TRUE,\n");
		result.append("dimnames = list(c(\n");
		
		for(String rowName : this.getRowNames()){
			result.append("\""+rowName+"\",");
		}
		result.insert(result.length()-1, ")");
		result.append("c(\n");
		for(String colName : this.getColNames()){
			result.append("\""+colName+"\",");
		}
		result.insert(result.length()-1, "))");
		result.append("\n");
		result.append("data = c(");
		
		try
		{
			Object[][] elements = getElements();
			for(int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++)
			{
				for(int colIndex = 0; colIndex < this.getNumberOfCols(); colIndex++)
				{
					Object val = elements[rowIndex][colIndex];
					if (val == null)
					{
						result.append("NA,");
					}
					else
					{
						if(val instanceof Number)
						{
							result.append(val+",");
						}
						else
						{
							result.append("\""+val+"\",");
						}
					}
				}
				result.append("\n");
			}
			result.setCharAt(result.length()-2, ')');
			result.append(")\n");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result.toString();
	}

	public File getAsExcelFile() throws Exception
	{
		/* Create tmp file */
		File excelFile = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + this.getData().getName() + ".xls");

		/* Create new Excel workbook and sheet */
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(excelFile, ws);
		WritableSheet s = workbook.createSheet("Sheet1", 0);

		/* Format the fonts */
		WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.BOLD);
		WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
		headerFormat.setWrap(false);
		WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.NO_BOLD);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		cellFormat.setWrap(false);

		/* Write column headers */
		List<String> colNames = this.getColNames();
		for (int i = 0; i < colNames.size(); i++)
		{
			Label l = new Label(i + 1, 0, colNames.get(i), headerFormat);
			s.addCell(l);
		}

		/* Write row headers */
		List<String> rowNames = this.getRowNames();
		for (int i = 0; i < rowNames.size(); i++)
		{
			Label l = new Label(0, i + 1, rowNames.get(i), headerFormat);
			s.addCell(l);
		}

		/* Write elements */
		Object[][] elements = getElements();
		for (int i = 0; i < this.getNumberOfCols(); i++)
		{
			for (int j = 0; j < this.getNumberOfRows(); j++)
			{
				if (elements[j][i] != null)
				{
					Label l = new Label(i + 1, j + 1,
							elements[j][i].toString(), cellFormat);
					s.addCell(l);
				}
				else
				{
					s.addCell(new Label(i + 1, j + 1, "", cellFormat));
				}
			}
		}

		/* Close workbook */
		workbook.write();
		workbook.close();

		return excelFile;
	}

	public int getNumberOfCols()
	{
		return numberOfCols;
	}

	public int getNumberOfRows()
	{
		return numberOfRows;
	}

	public List<String> getColNames()
	{
		return colNames;
	}

	public List<String> getRowNames()
	{
		return rowNames;
	}
	
	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByIndex(QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByIndex(
				(AbstractDataMatrixInstance<Object>) this, rules);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowEntityValues(
			Database db, QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByRowEntityValues(
				(AbstractDataMatrixInstance<Object>) this, db, rules);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByColEntityValues(
			Database db, QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByColEntityValues(
				(AbstractDataMatrixInstance<Object>) this, db, rules);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowMatrixValues(
			QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByRowMatrixValues(
				(AbstractDataMatrixInstance<Object>) this, rules);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByColMatrixValues(
			QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByColMatrixValues(
				(AbstractDataMatrixInstance<Object>) this, rules);
	}
	
	public AbstractDataMatrixInstance<Object> getSubMatrix2DFilterByRow(QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrix2DFilterByRow(
				(AbstractDataMatrixInstance<Object>) this, rules);
	}
	
	public AbstractDataMatrixInstance<Object> getSubMatrix2DFilterByCol(QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrix2DFilterByCol(
				(AbstractDataMatrixInstance<Object>) this, rules);
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByRowEntityValues(
			boolean asc) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByColEntityValues(
			Database db, boolean asc) throws Exception
	{
		QueryRule sorting = null;
		if (asc)
		{
			sorting = new QueryRule(Operator.SORTASC);
		}
		else
		{
			sorting = new QueryRule(Operator.SORTDESC);
		}
		List<String> rowNames = this.getRowNames();
		List<Nameable> subCol = (List<Nameable>) db.find(
				db.getClassForName(this.getData().getFeatureType()), sorting);
		List<String> colNames = new ArrayList<String>();
		for (Nameable i : subCol)
		{
			colNames.add(i.getName());
		}
		AbstractDataMatrixInstance res = this.getSubMatrix(rowNames, colNames);
		return res;
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByRowMatrixValues(
			boolean asc) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByColMatrixValues(
			Database db, boolean asc) throws Exception
	{
		List<Data> result = db.find(Data.class, new QueryRule("name",
				Operator.EQUALS, this.getData().getName()));
		Data thisData = null;
		if (result.size() < 1)
		{
			// no Data object for this one..
			throw new Exception("Matrix has no 'Data' description");
		}
		else if (result.size() > 1)
		{
			// multiple Data objects!
			throw new Exception("Multiple 'Data' descriptions for name '"
					+ this.getData().getName() + "'.");
		}
		else
		{
			thisData = result.get(0);
		}
		Query q = null;
		q.addRules(new QueryRule("data", Operator.EQUALS, thisData.getId()));

		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performUnion(
			AbstractDataMatrixInstance<Object> N) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performIntersection(
			AbstractDataMatrixInstance<Object> N) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performDifference(
			AbstractDataMatrixInstance<Object> I) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performExclusion(
			AbstractDataMatrixInstance<Object> I) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performTransposition(
			AbstractDataMatrixInstance<Object> I) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public void writeToCsvWriter(PrintWriter out) throws Exception
	{
		CsvWriter cfr = new CsvWriter(out);
		cfr.writeMatrix(getRowNames(), getColNames(), getElements());
		cfr.close();
	}

	public void writeToPrintWriter(PrintWriter out) throws Exception
	{
		Object[][] elements = getElements();
		for (String col : getColNames())
		{
			out.write("\t" + col);
		}
		out.write("\n");
		for(int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++)
		{
			out.write(getRowNames().get(rowIndex));
			for(int colIndex = 0; colIndex < this.getNumberOfCols(); colIndex++)
			{
				if (elements[rowIndex][colIndex] == null)
				{
					out.write("\t");
				}
				else
				{
					out.write("\t" + elements[rowIndex][colIndex]);
				}
			}
			out.write("\n");
		}
	}

	/**
	 * Get the matrix in the shape of a one-dimensional list. Instead of a
	 * 'grid' or values, just get a default importable CSV list of, for example
	 * feature - target - value.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> getAsObservedValueList() throws Exception
	{
		List<String> res = new ArrayList<String>();

		// get a shorthand to all the matrix values
		Object[][] elements = getElements();

		// add header
		// TODO: finish this part
//		res.add(this.getData().getTarget_Name() + "\t"
//				+ this.getData().getFeature_Name() + "\t" + "etc");

		// iterate over all the values and add in the form of a list
		for(int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++)
		{
			for(int colIndex = 0; colIndex < this.getNumberOfCols(); colIndex++)
			{
				res.add(getRowNames().get(rowIndex) + "\t"
						+ getColNames().get(colIndex) + "\t"
						+ elements[rowIndex][colIndex]);
			}
		}

		return res;
	}

	/*************************************************************/
	/**************** SourceMatrix implementation ****************/
	/*************************************************************/

	@Override
	public String getRowType() throws Exception
	{
		// return this.getVisibleRows().get(0).get__Type();
		return this.getData().getTargetType();
	}

	@Override
	public String getColType() throws Exception
	{
		// return this.getVisibleCols().get(0).get__Type();
		return this.getData().getFeatureType();
	}

	@Override
	public String renderValue(Object value)
	{
		if (value != null)
		{
			return value.toString();
		}
		else
		{
			return "";
		}
	}

	public static String render(ObservationElement o)
	{
		String head = o.getName();
		String content = "";
		for (String f : o.getFields())
		{
			content += f + " = " + o.get(f) + "<br>";
		}
		return "<div style=\"display: inline; text-align: center;\" onmouseover=\"return overlib('"
				+ content
				+ "', CAPTION, '"
				+ head
				+ "')\" onmouseout=\"return nd();\"><nobr>"
				+ "<a href=?__target="
				+ o.get__Type()
				+ "s&__action=filter_set&__filter_attribute="
				+ o.get__Type()
				+ "_name&__filter_operator=EQUALS&__filter_value="
				+ o.getName() + ">" + o.getName() + "</a></nobr></div>";

	}

	@Override
	public String renderRow(ObservationElement row) throws Exception
	{
		return render(row);
	}

	@Override
	public String renderCol(ObservationElement col) throws Exception
	{
		return render(col);
	}

	@Override
	public String renderRowSimple(ObservationElement row)
	{
		return row.getName();
	}

	@Override
	public String renderColSimple(ObservationElement col)
	{
		return col.getName();
	}

	// FIXME: when GenericFunctions' originalRows is set, this can be removed!
	@Override
	public int getTotalNumberOfRows()
	{
		return this.getNumberOfRows();
	}

	// FIXME: when GenericFunctions' originalCols is set, this can be removed!
	@Override
	public int getTotalNumberOfCols()
	{
		return this.getNumberOfCols();
	}

	/************************************************************/
	/**************** BasicMatrix implementation ****************/
	/************************************************************/

	public void setup(Database db) throws DatabaseException
	{
		this.originalCols = db.find(ObservationElement.class, new QueryRule(
				ObservationElement.NAME, Operator.IN, this.getColNames()));
		this.originalRows = db.find(ObservationElement.class, new QueryRule(
				ObservationElement.NAME, Operator.IN, this.getRowNames()));
	}

	@Override
	public Object[][] getValues() throws MatrixException
	{
		return this.getSubMatrixByObservationElement(rowCopy, colCopy)
				.getElements();
	}

	/****************************************************************/
	/**************** SliceableMatrix implementation ****************/
	/****************************************************************/

	@Override
	public BasicMatrix<ObservationElement, ObservationElement, Object> getResult()
			throws Exception
	{
		return this;
	}

	@Override
	public RenderDescriptor<ObservationElement, ObservationElement, Object> getRenderDescriptor()
			throws Exception
	{
		return this;
	}

	@Override
	public List<String> getRowPropertyNames()
	{
		ArrayList<String> attr = new ArrayList<String>();
		attr.add("name");
		attr.add("etc");
		return attr;
	}

	@Override
	public List<String> getColPropertyNames()
	{
		ArrayList<String> attr = new ArrayList<String>();
		attr.add("name");
		attr.add("etc");
		return attr;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByRowValues(
			int index, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByRowValues(
			ObservationElement row, Operator operator, Object value)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByColValues(
			int index, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByColValues(
			ObservationElement col, Operator operator, Object value)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByRowProperty(
			String property, Operator operator, Object value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByColProperty(
			String property, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Integer getColCount() throws MatrixException
	{
		return this.getColHeaders().size();
	}

	@Override
	public Integer getRowCount() throws MatrixException
	{
		return this.getRowHeaders().size();
	}
	
	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> slice(
			MatrixQueryRule rule) throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByColIndex(
			Operator operator, Integer index) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByRowIndex(
			Operator operator, Integer index) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByColValueProperty(
			ObservationElement col, String property, Operator operator,
			Object value) throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByColValueProperty(
			int colIndex, String property, Operator operator, Object value)
			throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getValuePropertyNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRowLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRowLimit(int rowLimit)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRowOffset()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRowOffset(int rowOffset)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getColLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColLimit(int colLimit)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getColOffset()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColOffset(int colOffset)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByRowValueProperty(
			ObservationElement row, String property, Operator operator,
			Object value) throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationElement, ObservationElement, Object> sliceByRowValueProperty(
			int rowIndex, String property, Operator operator, Object value)
			throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}
}

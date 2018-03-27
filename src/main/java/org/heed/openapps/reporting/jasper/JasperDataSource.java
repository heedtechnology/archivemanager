package org.heed.openapps.reporting.jasper;

import org.heed.openapps.reporting.ReportingDataSource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class JasperDataSource implements JRDataSource {
	private ReportingDataSource ds;
	
	
	public JasperDataSource(ReportingDataSource ds) {
		this.ds = ds;
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		return ds.getFieldValue(field.getName());
	}
	@Override
	public boolean next() throws JRException {
		return ds.next();
	}
	
}

package org.heed.openapps.reporting.jasper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.heed.openapps.property.PropertyService;
import org.heed.openapps.reporting.ReportingDataSource;
import org.heed.openapps.reporting.ReportingService;
import org.heed.openapps.scheduling.SchedulingService;


public class JasperReportingService implements ReportingService {
	private static final Log log = LogFactory.getLog(JasperReportingService.class);
	private SchedulingService scheduleService;
	private PropertyService propertyService;
	
	
	public JasperReportingService() {
		
	}
	@Override
	public byte[] generate(String id, int format, String template, ReportingDataSource data) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {			
			FileInputStream in = new FileInputStream(propertyService.getPropertyValue("home.dir") + "/resources/reports/"+template);
			JasperDesign design = JRXmlLoader.load(in);
			
			JasperReport report = JasperCompileManager.compileReport(design);
			
			JasperPrint print = JasperFillManager.fillReport(report, data.getParameters(), new JasperDataSource(data));
				
			JasperExportManager.exportReportToPdfStream(print, buffer);
			log.info("report:"+id+" successfully generated");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}
	
	public void generate(String jobid, boolean scheduled, int format, String title, String template, ReportingDataSource data) {
		try {		
			FileInputStream in = new FileInputStream(propertyService.getPropertyValue("home.dir") + "/resources/reports/"+template);
			
			JasperReport report = (JasperReport)JRLoader.loadObject(in);
			
			JasperPrint print = JasperFillManager.fillReport(report, data.getParameters(), new JasperDataSource(data));
			int totalPages = print.getPages().size();
			if(scheduled) scheduleService.updateJob(jobid, "generating "+print.getPages().size()+" pages...");
			if(format == 1) {
				FileOutputStream out = new FileOutputStream(propertyService.getPropertyValue("home.dir") + "/tomcat-7.0.42/webapps/reports/"+title+".html");
				JRHtmlExporter exporter = new JRHtmlExporter();
				JasperExportProgressMonitor monitor = new JasperExportProgressMonitor(jobid, totalPages);
				exporter.setParameter(JRExporterParameter.PROGRESS_MONITOR, monitor);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				exporter.exportReport();
			} else if(format == 2) {
				FileOutputStream out = new FileOutputStream(new File(propertyService.getPropertyValue("home.dir") + "/tomcat-7.0.42/webapps/reports/"+title+".pdf"));
				JasperExportManager.exportReportToPdfStream(print, out);
			} else if(format == 3) {
				FileOutputStream out = new FileOutputStream(propertyService.getPropertyValue("home.dir") + "/tomcat-7.0.42/webapps/reports/"+title+".rtf");
				JRRtfExporter exporter = new JRRtfExporter();
				JasperExportProgressMonitor monitor = new JasperExportProgressMonitor(jobid, totalPages);
				exporter.setParameter(JRExporterParameter.PROGRESS_MONITOR, monitor);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				exporter.exportReport();
			}
			if(scheduled) scheduleService.updateJob(jobid, "successfully generated "+totalPages+" pages...");
			log.info("report:"+jobid+" successfully generated");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void setSchedulingService(SchedulingService scheduleService) {
		this.scheduleService = scheduleService;
	}
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}
	
	public class JasperExportProgressMonitor implements JRExportProgressMonitor {
		private String jobid;
		private int totalPages;
		private int currentPage;
		
		public JasperExportProgressMonitor(String jobid, int totalPages) {
			this.jobid = jobid;
			this.totalPages = totalPages;
		}
		
		public void afterPageExport() {
			currentPage++;
			if(currentPage % 10 == 0) scheduleService.updateJob(jobid, "generated "+currentPage+" of "+totalPages+" pages...");
		}		
	}
}

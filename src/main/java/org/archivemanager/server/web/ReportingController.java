package org.archivemanager.server.web;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.archivemanager.data.out.CollectionExportProcessor;
import org.archivemanager.data.out.SubjectExportProcessor;
import org.archivemanager.server.config.PropertyConfiguration;
import org.heed.openapps.RepositoryModel;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.IOUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/reporting")
public class ReportingController {
	@Autowired private EntityService entityService;
	@Autowired private SubjectExportProcessor processor;
	@Autowired private CollectionExportProcessor collectionProcessor;
	@Autowired private PropertyConfiguration properties;
	
	@RequestMapping(value = "/subject/list.csv", method = RequestMethod.GET)
    public void subjects(Model model, String error, String logout) throws IOException, InvalidEntityException {
		EntityResultSet subjects = entityService.getEntities(ClassificationModel.SUBJECT, 0, 10000);
		FormatInstructions formatInstr = new FormatInstructions(false,true,true);
		formatInstr.setFormat(FormatInstructions.FORMAT_CSV);
		String csv = (String)processor.export(formatInstr, subjects.getData());
		ByteArrayInputStream in = new ByteArrayInputStream(csv.getBytes());
		FileOutputStream out = new FileOutputStream(properties.getHomeDirectory() + "/data/reports/subjects.csv");
		IOUtility.pipe(in, out);
    }
	@RequestMapping(value = "/collection/list.csv", method = RequestMethod.GET)
    public void collections(Model model, String error, String logout) throws IOException, InvalidEntityException {
		EntityResultSet collections = entityService.getEntities(RepositoryModel.COLLECTION, 0, 10000);
		FormatInstructions formatInstr = new FormatInstructions(false,true,true);
		formatInstr.setFormat(FormatInstructions.FORMAT_CSV);
		String csv = (String)collectionProcessor.export(formatInstr, collections.getData());
		ByteArrayInputStream in = new ByteArrayInputStream(csv.getBytes());
		FileOutputStream out = new FileOutputStream(properties.getHomeDirectory() + "/data/reports/collections.csv");
		IOUtility.pipe(in, out);
		return;
    }
}

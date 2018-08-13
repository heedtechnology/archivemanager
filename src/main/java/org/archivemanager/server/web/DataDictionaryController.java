package org.archivemanager.server.web;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.RestResponse;
import org.archivemanager.search.indexing.EntityIndexingJob;
import org.archivemanager.server.web.model.DataDictionaryRecord;
import org.archivemanager.server.web.model.ModelRecord;
import org.heed.openapps.dictionary.DataDictionary;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/dictionaries")
public class DataDictionaryController {
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	@Autowired private SearchService searchService;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getDictionaries(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		model.addAttribute("newDictionary", new DataDictionaryRecord());
		return "dictionaries/dictionaries";
	}
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editDataDictionary(@RequestParam long id, final Model model) throws InvalidEntityException {
		org.heed.openapps.dictionary.DataDictionary dictionary = dictionaryService.getDataDictionary(id);
		model.addAttribute("dictionary", dictionary);
		return "dictionaries/dictionary";
	}
	
	/** Services **/
	@ResponseBody
	@RequestMapping(value="/search.json")
	public RestResponse<DataDictionaryRecord> search(@RequestParam(required=false) String query, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="20") int size) throws Exception {
		RestResponse<DataDictionaryRecord> data = new RestResponse<DataDictionaryRecord>();		
		int start = (page*size) -  size;
		int end = page*size;
		data.setStartRow(start);
		data.setEndRow(end);
		List<DataDictionary> dictionaries = dictionaryService.getBaseDictionaries();
		dictionaries.add(dictionaryService.getSystemDictionary());
		for(DataDictionary result : dictionaries) {
			DataDictionaryRecord dictionary = new DataDictionaryRecord(result);
			data.addRow(dictionary);
		}
		data.setTotal(dictionaries.size());
		return data;
	}	
	@ResponseBody
	@RequestMapping(value="/add.json", method=RequestMethod.POST)
	public RestResponse<DataDictionaryRecord> addUser(@ModelAttribute("dictionary") DataDictionaryRecord dictionary) throws Exception {
		RestResponse<DataDictionaryRecord> data = new RestResponse<DataDictionaryRecord>();
		//entityService.updateEntity(dictionary);
		data.setStatus(200);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/model/search.json")
	public RestResponse<ModelRecord> searchTransactions(@RequestParam long id, @RequestParam(required=false) String query, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="20") int rows) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();
		DataDictionary dictionary = dictionaryService.getSystemDictionary();
		if(dictionary != null) {
			int start = (page*rows) -  rows;
			int end = page*rows;
			data.setStartRow(start);
			data.setEndRow(end);
			for(org.heed.openapps.dictionary.Model model : dictionary.getAllModels()) {
				ModelRecord record = new ModelRecord(model);
				data.addRow(record);
			}
			data.setTotal(data.getRows().size());
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/model/index.json", method=RequestMethod.POST)
	public RestResponse<ModelRecord> addTransaction(@RequestParam long dictionaryId, @RequestParam long modelId) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();	
		DataDictionary dictionary = dictionaryService.getSystemDictionary();
		if(dictionary != null) {
			org.heed.openapps.dictionary.Model model = dictionary.getModel(modelId);
			EntityIndexingJob job = new EntityIndexingJob(dictionaryService, entityService, searchService, model.getQName());
			job.execute();
			data.addRow(new ModelRecord(model));
		}		
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/model/add.json", method=RequestMethod.POST)
	public RestResponse<ModelRecord> addTransaction(@RequestParam long accountId, @ModelAttribute("model") ModelRecord transaction) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();	
		data.addRow(transaction);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/transaction/remove.json", method=RequestMethod.POST)
	public RestResponse<ModelRecord> removeTransaction(@RequestParam long transactionId) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();
		return data;
	}
}

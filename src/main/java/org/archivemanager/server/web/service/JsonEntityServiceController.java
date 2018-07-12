package org.archivemanager.server.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.model.ContentType;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.User;
import org.heed.openapps.data.RestRequest;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityImpl;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.scheduling.Job;
import org.heed.openapps.util.NumberUtility;
import org.heed.openapps.util.WebUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/entity")
public class JsonEntityServiceController extends WebserviceSupport {

  private final static Logger log = Logger.getLogger(JsonEntityServiceController.class.getName());

  private Map<Long, List<Long>> cache = new HashMap<Long, List<Long>>();
  public static final QName OPENAPPS_ENTITIES = new QName(SystemModel.OPENAPPS_SYSTEM_NAMESPACE,
      "entities");

  @ResponseBody
  @RequestMapping(value = "/named_entities/get.json", method = RequestMethod.GET)
  public RestResponse<Object> fetchNamedEntities(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam String qname) throws Exception {

    QName qName = QName.createQualifiedName(qname);
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();

    List<Entity> entities = getEntityService().getEntities(qName);

    JsonArrayBuilder builder = Json.createArrayBuilder();
    int i = 0;
    for (Entity c : entities) {
      JsonObjectBuilder object = Json.createObjectBuilder();
      builder.add(object.add("name", c.getName())).build();
    }

    String jsonArray = builder.build().toString();
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/get.json", method = RequestMethod.GET)
  public RestResponse<Object> fetchEntity(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(required = false) long id) throws Exception {
    String sourcesStr = request.getParameter("sources");
    String targetsStr = request.getParameter("targets");
    String view = request.getParameter("view");
    String uid = request.getParameter("uid");

    boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
    boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;

    prepareResponse(response);

    return getEntity(id, uid, view, sources, targets);
  }

  @ResponseBody
  @RequestMapping(value = "/get/{id}.json", method = RequestMethod.GET)
  public RestResponse<Object> getEntity(HttpServletRequest request, HttpServletResponse response,
      @PathVariable("id") Long id) throws Exception {
    String sourcesStr = request.getParameter("sources");
    String targetsStr = request.getParameter("targets");
    String view = request.getParameter("view");

    boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
    boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;

    prepareResponse(response);

    return getEntity(id, null, view, sources, targets);
  }

  @ResponseBody
  @RequestMapping(value = "/add.json", method = RequestMethod.POST)
  public RestResponse<Object> addEntity(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("qname") String entityQname) throws Exception {
    QName qname = QName.createQualifiedName(entityQname);

    prepareResponse(response);

    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = getEntity(request, qname);
      //entity.setUser(user.getId());
      String fmt = request.getParameter("format");
      getEntityService().addEntity(entity);
      if (fmt != null && fmt.equals("tree")) {
        FormatInstructions instructions = new FormatInstructions(true);
        instructions.setFormat(FormatInstructions.FORMAT_JSON);
        data.getResponse().addData(getEntityService().export(instructions, entity));
      } else {
        FormatInstructions instructions = new FormatInstructions(false, true, false);
        instructions.setFormat(FormatInstructions.FORMAT_JSON);
        data.getResponse().addData(getEntityService().export(instructions, entity));
      }
      data.getResponse().setStatus(0);
    } catch (Exception e) {
      e.printStackTrace();
      data.getResponse().setStatus(-1);
      data.getResponse().addMessage(e.getMessage());
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/addEntity.json", method = RequestMethod.POST)
  public RestResponse<Object> addEntityRestRequest(@RequestBody RestRequest<Entity> restRequest,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = restRequest.getData().get(0);
      //entity.setUser(user.getId());
      getEntityService().addEntity(entity);
      if (restRequest.getFormat() != null && restRequest.getFormat().equals("tree")) {
        FormatInstructions instructions = new FormatInstructions(true);
        instructions.setFormat(FormatInstructions.FORMAT_JSON);
        data.getResponse().addData(getEntityService().export(instructions, entity));
      } else {
        FormatInstructions instructions = new FormatInstructions(false, true, false);
        instructions.setFormat(FormatInstructions.FORMAT_JSON);
        data.getResponse().addData(getEntityService().export(instructions, entity));
      }
      data.getResponse().setStatus(0);
    } catch (Exception e) {
      e.printStackTrace();
      data.getResponse().setStatus(-1);
      data.getResponse().addMessage(e.getMessage());
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/update.json", method = RequestMethod.POST)
  public RestResponse<Object> updateEntity(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("id") Long id) throws Exception {

    prepareResponse(response);

    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = getEntityService().getEntity(id);
      //entity.setUser(user.getId());
      String printTargets = request.getParameter("targets");
      String printSources = request.getParameter("sources");
      boolean sources = printSources != null ? Boolean.valueOf(printSources) : true;
      boolean targets = printTargets != null ? Boolean.valueOf(printTargets) : false;
      Entity newEntity = getEntity(request, entity.getQName());
      for (Property prop : newEntity.getProperties()) {
        QName q = prop.getQName();
        entity.addProperty(q, prop.getValue());
      }
      entity.setQName(newEntity.getQName());
      entity.setName(newEntity.getName());
      if (newEntity.getUser() > 0 && newEntity.getUser() != entity.getUser()) {
        User user = getSecurityService().getCurrentUser(WebUtility.getHttpRequest(request));
        if (user != null && user.hasRole("administrator") || user.getId() == entity.getUser()) {
          entity.setUser(newEntity.getUser());
        }
      }
      getEntityService().updateEntity(entity);
      FormatInstructions instr = new FormatInstructions(false, sources, targets);
      instr.setFormat(FormatInstructions.FORMAT_JSON);
      data.getResponse().addData(getEntityService().export(instr, entity));
      data.getResponse().setStatus(0);
    } catch (Exception e) {
      data.getResponse().setStatus(-1);
      data.getResponse().addMessage(e.getMessage());
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/remove.json", method = RequestMethod.POST)
  public RestResponse<Object> removeEntity(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("id") Long id) throws Exception {

    prepareResponse(response);

    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = getEntityService().getEntity(id);
      getEntityService().removeEntity(null, id);
      getSearchService().remove(id);
      Map<String, Object> record = new HashMap<String, Object>();
      record.put("id", entity.getId());
      record.put("uid", entity.getUid());
      data.getResponse().addData(record);
      data.getResponse().setStatus(0);
    } catch (Exception e) {
      e.printStackTrace();
      data.getResponse().setStatus(-1);
      data.getResponse().addMessage(e.getMessage());
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/index.json", method = RequestMethod.GET)
  public RestResponse<Object> indexQName(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("qname") String qname) throws Exception {
    QName q = QName.createQualifiedName(qname);
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    Job job = getSearchService().update(q);
    if (job != null) {
      Map<String, Object> statusData = new HashMap<String, Object>();
      String uid = job.getUid();
      String message = job.getLastMessage();
      statusData.put("uid", uid);
      statusData.put("lastMessage", message);
      statusData.put("isRunning", !job.isComplete());
      data.getResponse().addData(statusData);
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/get/children.json", method = RequestMethod.GET)
  public RestResponse<Object> getEntities(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("id") Long id) throws Exception {
    String printTargets = request.getParameter("targets");
    String printSources = request.getParameter("sources");
    boolean sources = printSources != null ? Boolean.valueOf(printSources) : true;
    boolean targets = printTargets != null ? Boolean.valueOf(printTargets) : false;
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = getEntityService().getEntity(id);
      List<Association> assocs = entity.getSourceAssociations();

      for (Association assoc : assocs) {
        Entity target = getEntityService().getEntity(assoc.getTarget());
        FormatInstructions instructions = new FormatInstructions(false, sources, targets);
        instructions.setFormat(FormatInstructions.FORMAT_JSON);
        data.getResponse().addData(getEntityService().export(instructions, target));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/add.json", method = RequestMethod.POST)
  public RestResponse<Object> associationAdd(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("assoc_qname") String assoc_qname,
      @RequestParam(required = false) String entity_qname,
      @RequestParam("source") String source,
      @RequestParam(required = false) String target,
      @RequestParam(required = false) boolean targets) throws Exception {
    String id = request.getParameter("id");
    QName assocQname = QName.createQualifiedName(assoc_qname);
    QName entityQname = QName.createQualifiedName(entity_qname);
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      FormatInstructions instructions = new FormatInstructions(false, true, true);
      instructions.setFormat(FormatInstructions.FORMAT_JSON);
      if (target == null) {
        Entity entity = getEntity(request, entityQname);
        if (entity.getId() == null || entity.getId() == 0) {
          if (NumberUtility.isLong(source)) {
            getEntityService().addEntity(Long.valueOf(source), null, assocQname, null, entity);
            Entity sourceEntity = getEntityService().getEntity(Long.valueOf(source));
            data.getResponse().addData(getEntityService().export(instructions, sourceEntity));
          }
        }
      } else {
        Association assoc = getAssociation(request, id, source, target, assocQname);
        getEntityService().addAssociation(assoc);
        Entity sourceEntity =
            NumberUtility.isLong(source) ? getEntityService().getEntity(Long.valueOf(source))
                : getEntityService().getEntity(source);
        Entity targetEntity =
            NumberUtility.isLong(target) ? getEntityService().getEntity(Long.valueOf(target))
                : getEntityService().getEntity(target);

        if (targets) {
          data.getResponse().addData(getEntityService().export(instructions, targetEntity));
        } else {
          data.getResponse().addData(getEntityService().export(instructions, sourceEntity));
        }
      }
      return data;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/addall.json", method = RequestMethod.POST)
  public RestResponse<Object> associationAddAll(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("assoc_qname") String assoc_qname,
      @RequestParam("source") String source,
      @RequestParam("targetIds") String targetIds,
      @RequestParam(value = "target_qname", required = false) String target_qname,
      @RequestParam(required = false) boolean targets) throws Exception {

    QName assocQname = QName.createQualifiedName(assoc_qname);
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      FormatInstructions instructions = new FormatInstructions(false, true, true);
      instructions.setFormat(FormatInstructions.FORMAT_JSON);
      String[] targetForAssociation = targetIds.split("~~");
      Entity entity = getEntityService().getEntity(Long.valueOf(source));
      Entity targetEntity;
      List<Association> assocs = entity.getAssociations(assocQname);

      for (int i = 0; i < targetForAssociation.length; i++) {
        List<String> targetsMeta = Arrays.asList(targetForAssociation[i].split(":"));
        String id = targetsMeta.get(0);
        String name = targetsMeta.get(1);

        boolean match = false;
        if (id.equals("New")) {
          QName targetQname = QName.createQualifiedName(target_qname);
          targetEntity = getEntity(name, targetQname);
          targetEntity.setId(getEntityService().addEntity(targetEntity));
          getSearchService().update(entity);
        } else {
          targetEntity =
              NumberUtility.isLong(id) ? getEntityService()
                  .getEntity(Long.valueOf(id))
                  : getEntityService().getEntity(id);
          for (Association assoc : assocs) {
            if (assoc.getTarget().equals(targetEntity.getId())) {
              match = true;
              break;
            }
          }
        }

        if (!match) {
          Association assoc = getAssociation(request, null, source,
              Long.toString(targetEntity.getId()),
              assocQname);
          Entity sourceEntity =
              NumberUtility.isLong(source) ? getEntityService().getEntity(Long.valueOf(source))
                  : getEntityService().getEntity(source);

          assoc.setSourceName(sourceEntity.getQName());
          assoc.setTargetName(targetEntity.getQName());
          assoc.setSource(sourceEntity.getId());
          assoc.setTarget(targetEntity.getId());
          getEntityService().addAssociation(assoc);

          if (targets) {
            data.getResponse().addData(getEntityService().export(instructions, targetEntity));
          } else {
            data.getResponse().addData(getEntityService().export(instructions, sourceEntity));
          }
        }
      }
      data.getResponse().setStatus(200);
      return data;

    } catch (Exception e) {
      e.printStackTrace();
      data.getResponse().setStatus(500);
      data.getResponse().setMessages(Arrays.asList(e.getMessage()));
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/switch.json", method = RequestMethod.POST)
  public RestResponse<Object> switchAssociation(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("id") Long id, @RequestParam("source") Long sourceEntityId,
      @RequestParam("target") Long targetEntityId) throws Exception {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Association assoc = getEntityService().getAssociation(id);
      Entity target = getEntityService().getEntity(targetEntityId);
      Entity source = getEntityService().getEntity(sourceEntityId);
      if (assoc != null && source != null && target != null) {
        if (assoc.getSource() == source.getId()) {
          assoc.setTarget(target.getId());
        } else {
          assoc.setSource(source.getId());
        }
        getEntityService().updateAssociation(assoc);
      }
      FormatInstructions instructions = new FormatInstructions(false, true, false);
      instructions.setFormat(FormatInstructions.FORMAT_JSON);
      data.getResponse().addData(getEntityService().export(instructions, source));
      return data;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/remove.json", method = RequestMethod.POST)
  public RestResponse<Object> removeAssociation(HttpServletRequest request,
      HttpServletResponse response, @RequestParam("id") Long id,
      @RequestParam(required = false) String ticket) throws Exception {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      getEntityService().removeAssociation(id);
      Map<String, Object> record = new HashMap<String, Object>();
      record.put("id", id);
      data.getResponse().addData(record);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/remove/target.json", method = RequestMethod.POST)
  public RestResponse<Object> removeAssociationTarget(HttpServletRequest request,
      HttpServletResponse response, @RequestParam("id") Long id,
      @RequestParam("target_id") Long target) throws Exception {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = getEntityService().getEntity(id);
      getEntityService().removeEntity(null, id);

      Map<String, Object> record = new HashMap<String, Object>();
      record.put("id", entity.getId());
      record.put("uid", entity.getUid());
      data.getResponse().addData(record);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/node/browse.json", method = RequestMethod.GET)
  public RestResponse<Object> fetchJson(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String parent = request.getParameter("parent");
    String startStr = request.getParameter("_startRow");
    String endStr = request.getParameter("_endRow");
    String mode = request.getParameter("mode");

    int start = Integer.valueOf(startStr);
    int end = Integer.valueOf(endStr);

    return browse(parent, mode, start, end);
  }

  @ResponseBody
  @RequestMapping(value = "/node/get.json", method = RequestMethod.GET)
  public RestResponse<Object> getEntityJSON(HttpServletRequest request,
      HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
    return getNode(id);
  }

  @ResponseBody
  @RequestMapping(value = "/node/remove.json", method = RequestMethod.POST)
  public RestResponse<Object> removeNode(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("id") Long id) throws Exception {
    return removeNode(id);
  }

  protected void response(RestResponse<Object> data, int status, String message) {
    data.getResponse().setStatus(status);
    if (status == -1) {
      data.getResponse().addError(message);
      log.info(message);
    } else if (message != null && message.length() > 0) {
      data.getResponse().addMessage(message);
      log.info(message);
    }
  }

  protected RestResponse<Object> getEntity(long id, String uid, String view, boolean sources,
      boolean targets) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity =
          id != 0 ? getEntityService().getEntity(id) : getEntityService().getEntity(uid);
      if (entity != null) {
        FormatInstructions instr = new FormatInstructions();
        instr.setFormat(FormatInstructions.FORMAT_JSON);
        instr.setPrintSources(sources);
        instr.setPrintTargets(targets);
        if (view != null) {
          instr.setView(view);
        }
        data.getResponse().addData(getEntityService().export(instr, entity));
      }
      data.getResponse().setStatus(0);
    } catch (Exception e) {
      e.printStackTrace();
      data.getResponse().setStatus(-1);
      data.getResponse().addMessage(e.getMessage());
    }
    return data;
  }

  protected RestResponse<Object> browse(String parent, String mode, int start, int end) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      List<Long> nodes = null;
      if (parent == null || parent.equals("null")) {
        nodes = cache.get(0L);
        if (nodes == null) {
          nodes = new ArrayList<Long>(0);//getEntityService().getAllNodes();
          cache.put(0L, nodes);
        }
      } else {
        Entity parentEntity = getEntityService().getEntity(parent);
        if (mode != null && mode.equals("id")) {
          nodes = new ArrayList<Long>();
          if (parentEntity != null) {
            nodes.add(parentEntity.getId());
          }
        } else if (mode != null && mode.equals("name")) {
          //getEntityService().
        } else {
          if (parentEntity != null) {
            nodes = cache.get(parentEntity.getId());
            if (nodes == null) {
              nodes = new ArrayList<Long>();
              for (Association assoc : parentEntity.getSourceAssociations()) {
                nodes.add(assoc.getTarget());
              }
              cache.put(parentEntity.getId(), nodes);
            }
          }
        }
      }
      if (nodes != null && nodes.size() > 0) {
        data.getResponse().setStartRow(start);
        if (nodes.size() >= end) {
          data.getResponse().setEndRow(end);
        } else {
          data.getResponse().setEndRow(nodes.size() - 1);
        }
        data.getResponse().setTotalRows(nodes.size());
        int index = 0;
        for (Long node : nodes) {
          if (index >= start && index < end) {
            data.getResponse().getData().add(getNodeData(node, false, false));
          }
          index++;
          if (index == end) {
            break;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  protected RestResponse<Object> getNode(long id) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      data.getResponse().getData().add(getNodeData(id, true, true));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  protected RestResponse<Object> removeNode(long id) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      getEntityService().removeEntity(null, id);
      Map<String, Object> nodeData = new HashMap<String, Object>();
      nodeData.put("id", id);
      data.getResponse().getData().add(nodeData);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  protected Map<String, Object> getNodeData(Long node, boolean sources, boolean targets) {
    Map<String, Object> nodeData = new HashMap<String, Object>();
    try {
      Entity entity = getEntityService().getEntity(node);
      String qname = entity.getQName().toString();
      nodeData.put("id", String.valueOf(node));
      if (qname.equals("{openapps.org_system_1.0}entities")) {
        nodeData.put("name", "Entities");
      } else if (qname.equals("{openapps.org_system_1.0}models")) {
        nodeData.put("name", "Models");
      }
      if (entity.hasProperty(SystemModel.NAME)) {
        nodeData.put("name", entity.getProperty(SystemModel.NAME));
      } else if (entity.hasProperty(SystemModel.DESCRIPTION)) {
        nodeData.put("name", entity.getProperty(SystemModel.DESCRIPTION));
      } else {
        nodeData.put("name", entity.getQName().toString());
      }
      if (entity.getSourceAssociations().size() > 0) {
        nodeData.put("children", "true");
      }
      List<Property> nodeProperties = entity.getProperties();
      for (Property property : nodeProperties) {
        nodeData.put(property.getQName().toString(), String.valueOf(property.getValue()));
      }
      if (sources) {
        List<Map<String, Object>> sourceData = new ArrayList<Map<String, Object>>();
        for (Association relationship : entity.getSourceAssociations()) {
          Map<String, Object> source = new HashMap<String, Object>();
          source.put("id", relationship.getId());
          source.put("start", relationship.getSource());
          source.put("end", relationship.getTarget());
          source.put("type", relationship.getQName());
          List<Property> relationshipProperties = relationship.getProperties();
          for (Property property : relationshipProperties) {
            source.put(property.getQName().toString(), String.valueOf(property.getValue()));
          }
          sourceData.add(source);
        }
        nodeData.put("outgoing", sourceData);
      }
      if (targets) {
        List<Map<String, Object>> sourceData = new ArrayList<Map<String, Object>>();
        for (Association relationship : entity.getTargetAssociations()) {
          Map<String, Object> source = new HashMap<String, Object>();
          source.put("id", relationship.getId());
          source.put("start", relationship.getSource());
          source.put("end", relationship.getTarget());
          source.put("type", relationship.getQName());
          List<Property> relationshipProperties = relationship.getProperties();
          for (Property property : relationshipProperties) {
            source.put(property.getQName().toString(), String.valueOf(property.getValue()));
          }
          sourceData.add(source);
        }
        nodeData.put("incoming", sourceData);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return nodeData;
  }
  /*
  protected void printNode(StringWriter buff, Long node) throws NodeException {
		Map<String,Object> nodeProperties = getEntityService().getNodeProperties(node);
		for(String propertyName : nodeProperties.keySet()) {
			buff.append("<"+propertyName+">"+nodeProperties.get(propertyName)+"</"+propertyName+">");
		}
	}
	protected Long getEntityNode(QName qname) {
		try {
			Relationship modelsRelation = getEntityService().getSingleRelationship(0, new QName(OPENAPPS_ENTITIES.toString()), Direction.OUTGOING);
			if(modelsRelation != null) {
				for(Relationship relation : getEntityService().getRelationships(modelsRelation.getEndNode(), Direction.OUTGOING)) {
					String relQname = getEntityService().hasNodeProperty(relation.getEndNode(), "qname") ? (String)getEntityService().getNodeProperty(relation.getEndNode(), "qname") : "";
					if(relQname.equals(qname.toString()))
						return relation.getEndNode();
				}
			}
		} catch(Exception e) {
			//getLoggingService().error(e);
		}
		return null;
	}
	*/
}

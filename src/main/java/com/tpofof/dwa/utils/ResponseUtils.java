package com.tpofof.dwa.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tpofof.core.data.dao.ResultsSet;
import com.tpofof.core.utils.json.JsonUtils;
import com.tpofof.core.utils.json.ObjectMapperProvider;

@Component("responseUtils")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ResponseUtils {

	@Autowired private JsonUtils json;
	@Autowired ObjectMapperProvider objectMapperProvider;
	private ObjectMapper mapper;
	
	// Lazy created because of Spring bullshit
	private ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = objectMapperProvider.get();
		}
		return mapper;
	}
	
	public JsonNode success(JsonNode content) {
		ObjectNode node = getMapper().createObjectNode();
		node.put("success", true);
		node.put("status", 200);
		if (content != null) {
			node.set("data", content);
		}
		return node;
	}
	
	public JsonNode listData(ResultsSet<?> results) {
		return listData(results.getResults(), results.getLimit(), results.getOffset());
	}
	
	public JsonNode listData(List<?> content, int limit, int offset) {
		ObjectNode node = getMapper().createObjectNode();
		node.put("type", "collection");
		ArrayNode contentArray = getMapper().createArrayNode();
		for (Object obj : content) {
			contentArray.add(json.toJsonNode(obj));
		}
		node.set("collection", contentArray);
		node.put("count", content.size());
		ObjectNode pagingNode = getMapper().createObjectNode();
		boolean hasMore = content.size() == limit;
		pagingNode.put("hasMore", hasMore);
		pagingNode.put("limit", limit);
		pagingNode.put("offset", offset + limit);
		node.set("next", pagingNode);
		return node;
	}
	
	public JsonNode modelData(Object content) {
		ObjectNode node = getMapper().createObjectNode();
		node.put("type", "model");
		node.set("model", json.toJsonNode(content));
		return node;
	}
}

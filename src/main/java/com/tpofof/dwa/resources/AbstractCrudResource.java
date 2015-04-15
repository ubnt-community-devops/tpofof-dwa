package com.tpofof.dwa.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.tpofof.core.data.IPersistentModel;
import com.tpofof.core.data.dao.ResultsSet;
import com.tpofof.core.managers.IModelManager;
import com.tpofof.core.utils.json.JsonUtils;
import com.tpofof.dwa.error.HttpCodeException;
import com.tpofof.dwa.error.HttpNotFoundException;
import com.tpofof.dwa.error.HttpInternalServerErrorException;
import com.tpofof.dwa.utils.ResponseUtils;

@Component
public abstract class AbstractCrudResource<ModelT extends IPersistentModel<ModelT, PrimaryKeyT>, PrimaryKeyT, ManagerT extends IModelManager<ModelT, PrimaryKeyT>> {

	@Autowired private JsonUtils json;
	@Autowired private ResponseUtils response;
	private final ManagerT man;
	private final Class<ModelT> modelClass;
	
	public AbstractCrudResource(ManagerT man, Class<ModelT> modelClass) {
		this.man = man;
		this.modelClass = modelClass;
	}
	
	protected final ManagerT getManager() {
		return man;
	}
	
	@GET
	@Timed
	public JsonNode findModels(@QueryParam("limit") Optional<Integer> limit,
			@QueryParam("offset") Optional<Integer> offset,
			@Context HttpServletRequest request) {
		int limitVal = limit.isPresent() && limit.get() > 0 ? limit.get() : -1;
		int offsetVal = offset.isPresent() && offset.get() >= 0 ? offset.get() : 0;
		ResultsSet<ModelT> results = man.find(limitVal, offsetVal);
		return response.success(response.listData(results));
	}
	
	@Path("/count")
	@GET
	@Timed
	public JsonNode count(@Context HttpServletRequest request) throws HttpInternalServerErrorException {
		long count = man.count();
		if (count < 0) {
			throw new HttpInternalServerErrorException("Could not retrieve count for " + modelClass.getSimpleName());
		}
		return response.success(json.getObjectNode().put("count", count));
	}
	
	@Path("/{id}")
	@GET
	@Timed
	public JsonNode findModel(@PathParam("id") PrimaryKeyT id,
			@Context HttpServletRequest request) throws HttpNotFoundException {
		ModelT model = man.find(id);
		if (model == null) {
			throw new HttpNotFoundException("Could not find " + modelClass.getSimpleName() + " with id " + id);
		}
		return response.success(response.modelData(model));
	}
	
	@POST
	@Timed
	public JsonNode post(ModelT model,
			@Context HttpServletRequest request) throws HttpInternalServerErrorException {
		ModelT insertedModel = man.insert(model);
		if (insertedModel == null) {
			throw new HttpInternalServerErrorException("Could not create " + modelClass.getSimpleName());
		}
		return response.success(response.modelData(insertedModel)); 
	}
	
	@Path("/{id}")
	@PUT
	@Timed
	public JsonNode update(@PathParam("id") String id, ModelT model,
			@Context HttpServletRequest request) throws HttpCodeException {
		if (!id.equals(model.getId())) {
			throw new HttpInternalServerErrorException("Invalid Request: ID's do not match");
		}
		ModelT updatedModel = man.update(model);
		if (updatedModel == null) {
			throw new HttpInternalServerErrorException("Could not update the " + modelClass.getSimpleName());
		}
		return response.success(response.modelData(updatedModel));
	}
	
	@Path("/{id}")
	@DELETE
	@Timed
	public JsonNode delete(@PathParam("id") PrimaryKeyT id,
			@Context HttpServletRequest request) throws HttpInternalServerErrorException {
		if (!man.delete(id)) {
			throw new HttpInternalServerErrorException("Failed to delete " + modelClass.getSimpleName() + " with id " + id);
		}
		return response.success(null);
	}
}

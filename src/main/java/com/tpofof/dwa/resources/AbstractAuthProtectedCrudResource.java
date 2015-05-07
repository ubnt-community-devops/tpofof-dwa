package com.tpofof.dwa.resources;

import static com.tpofof.dwa.resources.AuthRequestPermisionType.COUNT;
import static com.tpofof.dwa.resources.AuthRequestPermisionType.CREATE;
import static com.tpofof.dwa.resources.AuthRequestPermisionType.DELETE;
import static com.tpofof.dwa.resources.AuthRequestPermisionType.READ;
import static com.tpofof.dwa.resources.AuthRequestPermisionType.READ_ONE;
import static com.tpofof.dwa.resources.AuthRequestPermisionType.UPDATE;
import io.dropwizard.auth.Auth;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.tpofof.core.data.IPersistentModel;
import com.tpofof.core.data.dao.ResultsSet;
import com.tpofof.core.managers.IModelManager;
import com.tpofof.core.utils.json.JsonUtils;
import com.tpofof.dwa.auth.IAuthValidator;
import com.tpofof.dwa.error.HttpCodeException;
import com.tpofof.dwa.error.HttpInternalServerErrorException;
import com.tpofof.dwa.error.HttpNotFoundException;
import com.tpofof.dwa.error.HttpUnauthorizedException;
import com.tpofof.dwa.utils.RequestUtils;
import com.tpofof.dwa.utils.ResponseUtils;

@Component
public abstract class AbstractAuthProtectedCrudResource<ModelT extends IPersistentModel<ModelT, PrimaryKeyT>, PrimaryKeyT, ManagerT extends IModelManager<ModelT, PrimaryKeyT>, AuthModelT> {

	@Autowired private JsonUtils json;
	@Autowired private ResponseUtils responseUtils;
	@Autowired private RequestUtils requestUtils;
	private final ManagerT man;
	private final Class<ModelT> modelClass;
	
	public AbstractAuthProtectedCrudResource(ManagerT man, Class<ModelT> modelClass) {
		this.man = man;
		this.modelClass = modelClass;
	}
	
	protected final ManagerT getManager() {
		return man;
	}
	
	/**
	 * If you do not want to implement this method then you should override {@link #validate(Object, Object, AuthRequestPermisionType)}.
	 * @return Must not be {@code null}.
	 */
	protected abstract IAuthValidator<AuthModelT, PrimaryKeyT, AuthRequestPermisionType> getValidator();
	
	/**
	 * @param authModel
	 * @throws HttpUnauthorizedException
	 */
	protected void validate(AuthModelT authModel, PrimaryKeyT assetKey, AuthRequestPermisionType permType) throws HttpUnauthorizedException {
		getValidator().validate(authModel, assetKey, permType);
	}

	@GET
	public JsonNode findModels(@Auth AuthModelT authModel,
			@QueryParam("limit") Optional<Integer> limit,
			@QueryParam("offset") Optional<Integer> offset,
			@Context HttpServletRequest request) throws HttpCodeException {
		validate(authModel, null, READ);
		ResultsSet<ModelT> results = man.find(requestUtils.limit(limit), requestUtils.offset(offset));
		return responseUtils.success(responseUtils.listData(results));
	}
	
	@Path("/count")
	@GET
	public JsonNode count(@Auth AuthModelT authModel,
			@Context HttpServletRequest request) throws HttpCodeException {
		validate(authModel, null, COUNT);
		long count = man.count();
		if (count < 0) {
			throw new HttpInternalServerErrorException("Could not retrieve count for " + modelClass.getSimpleName());
		}
		return responseUtils.success(json.getObjectNode().put("count", count));
	}
	
	@Path("/{id}")
	@GET
	public JsonNode findModel(@Auth AuthModelT authModel,
			@PathParam("id") PrimaryKeyT id, @Context HttpServletRequest request) throws HttpCodeException {
		validate(authModel, id, READ_ONE);
		ModelT model = man.find(id);
		if (model == null) {
			throw new HttpNotFoundException("Could not find " + modelClass.getSimpleName() + " with id " + id);
		}
		return responseUtils.success(responseUtils.modelData(model));
	}
	
	@POST
	public JsonNode post(@Auth AuthModelT authModel, ModelT model, 
			@Context HttpServletRequest request) throws HttpCodeException {
		validate(authModel, null, CREATE);
		ModelT insertedModel = man.insert(model);
		if (insertedModel == null) {
			throw new HttpInternalServerErrorException("Could not create " + modelClass.getSimpleName());
		}
		return responseUtils.success(responseUtils.modelData(insertedModel)); 
	}
	
	@Path("/{id}")
	@PUT
	public JsonNode update(@Auth AuthModelT authModel,
			@PathParam("id") PrimaryKeyT id, ModelT model, @Context HttpServletRequest request) throws HttpCodeException {
		if (!id.equals(model.getId())) {
			throw new HttpInternalServerErrorException("Invalid Request: ID's do not match");
		}
		validate(authModel, id, UPDATE);
		ModelT updatedModel = man.update(model);
		if (updatedModel == null) {
			throw new HttpInternalServerErrorException("Could not update the " + modelClass.getSimpleName());
		}
		return responseUtils.success(responseUtils.modelData(updatedModel));
	}
	
	@Path("/{id}")
	@DELETE
	public JsonNode delete(@Auth AuthModelT authModel,
			@PathParam("id") PrimaryKeyT id, @Context HttpServletRequest request) throws HttpCodeException {
		validate(authModel, id, DELETE);
		if (!man.delete(id)) {
			throw new HttpInternalServerErrorException("Failed to delete " + modelClass.getSimpleName() + " with id " + id);
		}
		return responseUtils.success(null);
	}
}

package br.com.e8.soupsi.war.base;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.e8.soupsi.client.base.BaseService;
import br.com.e8.soupsi.client.base.ListWrapper;
import br.com.e8.soupsi.ejb.business.BusinessException;
import br.com.e8.soupsi.ejb.business.IntegrityConstraintViolationException;
import br.com.e8.soupsi.ejb.business.RecordNotFoundException;
import br.com.e8.soupsi.ejb.business.UnauthorizedException;
import br.com.e8.soupsi.jpa.Token;
import br.com.e8.soupsi.jpa.base.BaseEntity;
import br.com.e8.soupsi.war.TokenBean;
import br.com.e8.soupsi.war.util.ResponseUtil;

@ApplicationPath("/")
@Named
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class BaseREST<T extends BaseEntity> extends Application {
	
	@Inject
	protected TokenBean tokenBean;
	
	private @Context ServletContext context;
	
	@SuppressWarnings("rawtypes")
	protected abstract BaseService getService();
	
	protected Response validLogin(String token) {
		try {
			Token t = tokenBean.isValidToken(token, context);
			if (t == null)
				return ResponseUtil.Ok(new UnauthorizedException());
		} catch (Exception e1) {
			e1.printStackTrace();
			return ResponseUtil.InternalServerError(e1);
		}
		return null;
	}
	
	@GET
    @Path("/")
    public Response filter(@HeaderParam("Authorization") String token) {
		Response response = validLogin(token);
		if(response != null)
			return response;
		
		try {
			@SuppressWarnings("unchecked")
			List<T> list = this.getService().listAll();
			return ResponseUtil.Ok(list);
		} catch (Exception e) {
			return ResponseUtil.InternalServerError(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{id}")
	public Response getById(@HeaderParam("Authorization") String token, @PathParam("id") Long id) {
		T tt;
		
		try {
			tt = (T)this.getService().findById(id);
			
			if (tt == null)
				return ResponseUtil.Ok(new RecordNotFoundException());
		
			return ResponseUtil.Ok(tt);
		} catch (Exception e) {
			return ResponseUtil.InternalServerError(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/")
	public Response save(@HeaderParam("Authorization") String token, T entity) {
		
		Response response = validLogin(token);
		if(response != null)
			return response;
		
		entity.setId(0l);
		
		try {
			BaseEntity entitySaved = this.getService().save(entity);
			return ResponseUtil.Ok(entitySaved);
		} catch (BusinessException e) {
			try {
				return ResponseUtil.Ok(e);
			} catch (JsonProcessingException e1) {
				return ResponseUtil.InternalServerError(e);
			}
		} catch (Exception e) {
			return ResponseUtil.InternalServerError(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PUT
	@Path("/{id}")
	public Response update(@HeaderParam("Authorization") String token, T entity, @PathParam("id") Long id) {
		
		Response response = validLogin(token);
		if(response != null)
			return response;
		
		try {
			BaseEntity entitySaved = this.getService().save(entity);
			return ResponseUtil.Ok(entitySaved);
		} catch (BusinessException e) {
			try {
				return ResponseUtil.Ok(e);
			} catch (JsonProcessingException e1) {
				return ResponseUtil.InternalServerError(e);
			}
		} catch (Exception e) {
			return ResponseUtil.InternalServerError(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@DELETE
	@Path("/{id}")
	public Response delete(@HeaderParam("Authorization") String token, @PathParam("id") Long id) {
		
		Response response = validLogin(token);
		if(response != null)
			return response;
		
		try {
			BaseEntity entity = this.getService().findById(id);
			
			if (entity == null)
				return ResponseUtil.Ok(new RecordNotFoundException());
			
			this.getService().delete(entity);
			
			return ResponseUtil.Ok(null);
		} catch (Exception e) {
			if (isSQLIntegrityConstraintViolationException(e.getCause())) {
				try {
					return ResponseUtil.Ok(new IntegrityConstraintViolationException());
				} catch (JsonProcessingException e1) {
					return ResponseUtil.InternalServerError(e);
				}
			} else			
				return ResponseUtil.InternalServerError(e);
		}
	}
	
	private boolean isSQLIntegrityConstraintViolationException(Throwable caught) {
	    if (caught == null) 
	    	return false;
	    
	    if (SQLIntegrityConstraintViolationException.class.isAssignableFrom(caught.getClass())) 
	    	return true;
	    else 
	    	return isSQLIntegrityConstraintViolationException(caught.getCause());
	  }
	
	@SuppressWarnings("rawtypes")
	protected ListWrapper getWrapper(Integer page, Integer pageSize, List<String> sortFields, List<String> sortDirections, 
			Map<String, Object> parameters) throws Exception {
		StringBuilder hql = this.getService().getSimpleHQL();
		return this.getWrapper(page, pageSize, sortFields, sortDirections, hql, parameters);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ListWrapper getWrapper(Integer page, Integer pageSize, List<String> sortFields, List<String> sortDirections, 
			StringBuilder hql, Map<String, Object> parameters) throws Exception {

		ListWrapper<BaseEntity> wrapper = new ListWrapper<>();
		wrapper.setCurrentPage(page);
		wrapper.setPageSize(pageSize);
		wrapper.setSortFields(sortFields);
		wrapper.setSortDirections(sortDirections);
		
		List<BaseEntity> list = this.getService().list(hql, parameters, sortFields, sortDirections, page, pageSize);
		wrapper.setList(list);
		
		Long count = this.getService().count(parameters);
		wrapper.setTotalResults(count.intValue());
		
		return wrapper;
	}
}
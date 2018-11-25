package br.com.e8.soupsi.war.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import br.com.e8.soupsi.client.base.ListWrapper;
import br.com.e8.soupsi.ejb.business.BusinessException;

public class ResponseUtil {
	
	@SuppressWarnings("rawtypes")
	public static Response Ok(Object data) throws JsonProcessingException {
		
		if (data instanceof BusinessException)
			return BusinessExceptionError((BusinessException)data);
		
		boolean islistWrapper = data instanceof ListWrapper;
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		Hibernate5Module hm = new Hibernate5Module();
		hm.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, false);
		
		objectMapper.registerModule(hm);

		Object mapper;
		
		if (islistWrapper)
			mapper = (String)objectMapper.writeValueAsString(((ListWrapper)data).getList());
		else
			mapper = (String)objectMapper.writeValueAsString(data);
		
		ResponseBuilder response = Response.ok(mapper)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with, app_key")
				.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, PUT, HEAD")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Max-Age", "1000");
		
		
		if (islistWrapper) {
			Integer totalResult = ((ListWrapper)data).getTotalResults();
			
			response.header("Access-Control-Expose-Headers", "X-Total-Count");
			response.header("X-Total-Count", totalResult);
		}
		
		return response.build();
	}
	
	private static Response BusinessExceptionError(BusinessException e) {
		JSONObject json = new JSONObject();
		json.put("error", e.getMessage());
		return Response.ok(Response.Status.OK).entity(json).type(MediaType.TEXT_PLAIN_TYPE).build();
	}
	
	public static Response InternalServerError(Exception e) {		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).type(MediaType.TEXT_PLAIN_TYPE).build();
	}
}
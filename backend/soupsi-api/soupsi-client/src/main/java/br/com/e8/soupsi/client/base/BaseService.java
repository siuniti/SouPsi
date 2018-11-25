package br.com.e8.soupsi.client.base;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import br.com.e8.soupsi.jpa.base.BaseEntity;

@Local
public interface BaseService<T extends BaseEntity> {

	public T save(T t) throws Exception;
	public T load(StringBuilder hql, Map<String, Object> parameters) throws Exception;
	public T load(Map<String, Object> parameters) throws Exception;
	public Object find(StringBuilder hql, Map<String, Object> parameters) throws Exception;
	public void delete(T t) throws Exception;
	public T findById(Long id) throws Exception;
	public StringBuilder getSimpleHQL();
	public Long count(Map<String, Object> parameters) throws Exception;
	public Long count(StringBuilder hql, Map<String, Object> parameters) throws Exception;
	public List<T> listAll() throws Exception;
	public List<T> listAtivos() throws Exception;
	public List<T> list(Map<String, Object> parameters) throws Exception;
	public List<T> list(Map<String, Object> parameters, List<String> orders) throws Exception;
	public List<T> list(StringBuilder hql, Map<String, Object> parameters) throws Exception;
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields) throws Exception;
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields, List<String> sortDirections, Integer firstResult, Integer maxResult) 
			throws Exception;
}
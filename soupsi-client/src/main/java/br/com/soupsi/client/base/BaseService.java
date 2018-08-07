package br.com.soupsi.client.base;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

@Local
public interface BaseService<T> {

	public void delete(T t) throws Exception;
	public void save(T t) throws Exception;
	public T load(StringBuffer hql, Map<String, Object> parameters) throws Exception;
	List<T> list(StringBuffer hql, Map<String, Object> parameters, Map<String, String> orders) throws Exception;
}
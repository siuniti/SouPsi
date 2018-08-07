package br.com.soupsi.ejb.base;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.soupsi.client.base.BaseService;

@Stateless
public class BaseServiceImpl<T> implements BaseService<T>{
	
	@PersistenceContext(unitName = "JPAEntityManager")
	protected EntityManager entityManager;
	

	@Override
	public void delete(T t) throws Exception {
		T tt = entityManager.merge(t);
		entityManager.remove(tt);
	}

	@Override
	public void save(T t) throws Exception {
		entityManager.merge(t);
	}
	
	private Query parseHQL(Map<String, Object> parameters, StringBuffer hql, Map<String, String> orders) {
		if (hql == null || hql.length() == 0) {
			return null;
		}
		
		boolean hasWhere = false;
		
		if (parameters != null && !parameters.isEmpty()) {
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				String clause;
				
				if (hasWhere)
					clause = " AND ";
				else {
					clause = " WHERE ";
					hasWhere = true;
				}
				
				clause += parameter.getKey() + " = :" + parameter.getKey().substring(parameter.getKey().lastIndexOf('.') + 1);
				hql.append(clause);
			}
		}
		
		if (orders != null && !orders.isEmpty()) {
			String orderBy = " ORDER BY ";
			
			boolean isFirst = true;
			
			for(Entry<String, String> order : orders.entrySet()) {
				if (isFirst)
					isFirst = false;
				else {
					orderBy += ", ";
				}
				
				orderBy += order.getKey() + " " + order.getValue();
				hql.append(orderBy);
			}
		}
		
		Query query = entityManager.createQuery(hql.toString());
		
		if (parameters != null && !parameters.isEmpty()) {
			for (Entry<String, Object> parameter : parameters.entrySet()) { 
				query.setParameter(parameter.getKey().substring(parameter.getKey().lastIndexOf('.') + 1), parameter.getValue());
			}
		}
		
		return query;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T load(StringBuffer hql, Map<String, Object> parameters) throws Exception {
		
		Query query = parseHQL(parameters, hql, null);
		
		if (query == null) {
			return null;
		}
		
		try {
			return (T)query.getSingleResult();
		} catch (NoResultException e) {
				return null;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(StringBuffer hql, Map<String, Object> parameters, Map<String, String> orders) {
		
		Query query = parseHQL(parameters, hql, orders);
		
		if (query == null) {
			return null;
		}
		
		return query.getResultList();
	}
}
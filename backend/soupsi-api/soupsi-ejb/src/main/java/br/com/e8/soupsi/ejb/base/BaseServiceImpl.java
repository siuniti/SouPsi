package br.com.e8.soupsi.ejb.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;

import br.com.e8.soupsi.client.base.BaseService;
import br.com.e8.soupsi.ejb.util.SQLUtil;
import br.com.e8.soupsi.jpa.base.BaseEntity;

@Stateless
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {
	
	@PersistenceContext(unitName = "JPAEntityManager")
	protected EntityManager entityManager;	

	private static final String INICIAL = "Inicial";
	private static final String FINAL = "Final";
	
	@Override
	public void delete(T t) throws Exception {
		T tt = entityManager.merge(t);
		entityManager.remove(tt);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Long getNextval() throws Exception {
		Class clazz = getGenericClass();
		Annotation annotation = clazz.getAnnotation(Table.class);
		
		if (annotation != null) {
			String tableName = (String)annotation.annotationType().getMethod("name").invoke(annotation);
			tableName = tableName.substring(0, tableName.indexOf("_"));
			
			String schema = (String)annotation.annotationType().getMethod("schema").invoke(annotation);
			
			if (schema != null)
				schema += ".";
			else
				schema = "";
			
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT " + schema + "SQ_" + tableName + ".nextval ");
			hql.append("FROM dual");
			
			Query query = entityManager.createNativeQuery(hql.toString());
			return ((BigDecimal)query.getSingleResult()).longValue();
		}
		
		return null;
	}
	
	@Override
	public T save(T t) throws Exception {
		boolean isNew = t.isNew();
		
		if (isNew) {
			t.setId(getNextval());	
		}
		
		return entityManager.merge(t);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Class getGenericClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	private String getGenericClassName() {
		return getGenericClass().getSimpleName();
	}
	
	public StringBuilder getSimpleHQL() {
		String entity = getGenericClassName();
		String entityAlias = entity.substring(0, 1).toLowerCase() + entity.substring(1);
		
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT " + entityAlias + " ");
		hql.append("FROM " + entity + " " + entityAlias + " ");
		
		return hql;
	}
	
	private StringBuilder getSimpleHQLCount() {
		String entity = getGenericClassName();
		String entityAlias = entity.substring(0, 1).toLowerCase() + entity.substring(1);
		
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(1) ");
		hql.append("FROM " + entity + " " + entityAlias + " ");
		
		return hql;
	}
	
	@Override
	public T findById(Long id) throws Exception {
		Map<String,Object> parameters = new HashMap<>();
		parameters.put("id", id);
		
		return (T)this.load(getSimpleHQL(), parameters);
	}
	
	private String getParameterName(String parameter) {
		return parameter.substring(parameter.lastIndexOf('.') + 1) + Integer.toString(parameter.hashCode()).replace("-", "");
	}
	
	private Query parseHQL(Map<String, Object> parameters, StringBuilder hql, List<String> orders) {
		Integer firstResult = null;
		Integer maxResult = null;
		List<String> sortDirections = null;

		return this.parseHQL(parameters, hql, orders, sortDirections, firstResult, maxResult);
	}
	
	private Query parseHQL(Map<String, Object> parameters, StringBuilder hql, List<String> sortFields, List<String> sortDirections, Integer firstResult, Integer maxResult) {
		if (hql == null || hql.length() == 0) {
			return null;
		}
		
		boolean hasWhere = hql.toString().toUpperCase().contains("WHERE");
		
		if (parameters != null && !parameters.isEmpty()) {
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				String clause;
				
				if (hasWhere)
					clause = " AND ";
				else {
					clause = " WHERE ";
					hasWhere = true;
				}
				
				if (parameter.getValue() instanceof OperationValue) {
					OperationValue operationValue = (OperationValue)parameter.getValue();
					
					switch (operationValue.getOperation()) {
						case EQUAL_CASE_SENSITIVE: {
							clause += "UPPER(TRIM(" + parameter.getKey() + "))" + " = UPPER(TRIM(:" + getParameterName(parameter.getKey()) + "))";
							break;
						}
						case LIKE: {
							clause += "UPPER (" + parameter.getKey() + ") LIKE UPPER(:" + getParameterName(parameter.getKey()) + " )";
							break;
						}
						case BETWEEN: {
							clause += parameter.getKey() + " BETWEEN :" + parameter.getKey() + INICIAL + " AND :" + parameter.getKey() + FINAL;
							break;
						}
						case IN: {
							String value = "";
							
							if (operationValue.getValue().getClass().isArray()) {
								for (Object obj : (Object[])operationValue.getValue()) {
									if (obj instanceof Long) {
										if (value.isEmpty()) {
											value = obj.toString();
										} else {
											value += ',' + obj.toString();
										}
									}
								}
							}
							
							if (!value.isEmpty())
								clause += parameter.getKey() + " IN (" + value + " )";
							break;
						}
						case IS_NOT_NULL: {
							clause += parameter.getKey() + " " + operationValue.getOperation().getOperation() + " ";
							break;
						}
						default: {
							clause += parameter.getKey() + " " + operationValue.getOperation().getOperation() +  " :" + getParameterName(parameter.getKey()); 
						}
					}
				} else {
					clause += parameter.getKey() + " = :" + getParameterName(parameter.getKey());
				}
				
				hql.append(clause);
			}
		}
		
		SQLUtil.addSortFieldsAndDirections(hql, sortFields, sortDirections);
		
		Query query = entityManager.createQuery(hql.toString());
		
		if (firstResult != null && maxResult != null) {
			int start = (firstResult - 1) * maxResult;
			
			query.setFirstResult(start);
			query.setMaxResults(maxResult.intValue());
		}
		
		if (parameters != null && !parameters.isEmpty()) {
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				
				if (parameter.getValue() instanceof OperationValue) {
					OperationValue operationValue = (OperationValue)parameter.getValue();

					switch (operationValue.getOperation()) {
						case LIKE: {
							query.setParameter(getParameterName(parameter.getKey()), "%" + operationValue.getValue() + "%");
							break;
						}
						case BETWEEN: {
							query.setParameter(parameter.getKey() + INICIAL, operationValue.getValue());
							query.setParameter(parameter.getKey() + FINAL, operationValue.getValue2());
							break;
						}
						case IN: {
							// faz nada
							break;
						}
						case IS_NOT_NULL: {
							// faz nada
							break;
						}
						default: {
							query.setParameter(getParameterName(parameter.getKey()), operationValue.getValue());
						}
					}
				} else {
					query.setParameter(getParameterName(parameter.getKey()), parameter.getValue());	
				}
				
			}
		}
		
		return query;
	}
	
	@Override
	public T load(Map<String, Object> parameters) throws Exception {
		return this.load(this.getSimpleHQL(), parameters);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T load(StringBuilder hql, Map<String, Object> parameters) throws Exception {
		
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
	public Object find(StringBuilder hql, Map<String, Object> parameters) throws Exception {
		
		Query query = parseHQL(parameters, hql, null);
		
		if (query == null) {
			return null;
		}
		
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
				return null;
		}
	}
	
	@Override
	public Long count(Map<String, Object> parameters) throws Exception {
		StringBuilder hql = getSimpleHQLCount();
		return this.count(hql, parameters);
	}
	
	@Override
	public Long count(StringBuilder hql, Map<String, Object> parameters) throws Exception {
		List<String> orders = null;
		
		Query query = parseHQL(parameters, hql, orders);
		
		if (query == null) {
			return null;
		}
		
		try {
			return (Long)query.getSingleResult();
		} catch (NoResultException e) {
				return null;
		}
	}
	
	protected List<String> getNaturalOrders() {
		return null;
	}
	
	@Override
	public List<T> listAll() {
		Map<String, Object> parameters  = null;
		return this.list(parameters);
	}
	
	@Override
	public List<T> listAtivos() throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("ativo", true);
		
		return list(parameters, getNaturalOrders());
	}
	
	@Override
	public List<T> list(Map<String, Object> parameters) {
		return this.list(getSimpleHQL(), parameters, getNaturalOrders());
	}
	
	@Override
	public List<T> list(Map<String, Object> parameters, List<String> sortFields) {
		return this.list(getSimpleHQL(), parameters, sortFields);
	}
	
	@Override
	public List<T> list(StringBuilder hql, Map<String, Object> parameters) {
		List<String> sortFields = null;
		List<String> sortDirections = null;
		Integer firstResult = null;
		Integer maxResult = null;
		
		return this.list(hql, parameters, sortFields, sortDirections, firstResult, maxResult);
	}
	
	@Override
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields) {
		List<String> sortDirections = null;
		Integer firstResult = null;
		Integer maxResult = null;
		
		return this.list(hql, parameters, sortFields, sortDirections, firstResult, maxResult);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields, List<String> sortDirections, Integer firstResult,  Integer maxResult) {
		
		Query query = parseHQL(parameters, hql, sortFields, sortDirections, firstResult, maxResult);
		
		if (query == null) {
			return null;
		}
		
		return query.getResultList();
	}
}
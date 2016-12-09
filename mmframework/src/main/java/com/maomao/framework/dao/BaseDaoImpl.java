/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maomao.framework.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.util.Assert;

import com.maomao.framework.support.paglit.PageInfo;

/**
 * Base dao implement.
 * @author maomao
 *
 * @param <T>
 * @param <ID>
 */
public abstract class BaseDaoImpl<T, ID extends Serializable> implements IBaseDao<T, ID> {
	protected Class<T> entityClass;
	protected static volatile long aliasCount = 0;

	@PersistenceContext
	protected EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public BaseDaoImpl() {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
			entityClass = (Class<T>) parameterizedType[0];
		}
	}

	public T get(ID id) {
		if (id != null) {
			return entityManager.find(entityClass, id);
		}
		return null;
	}

	public T get(ID id, LockModeType lockModeType) {
		if (id != null) {
			if (lockModeType != null) {
				return entityManager.find(entityClass, id, lockModeType);
			} else {
				return entityManager.find(entityClass, id);
			}
		}
		return null;
	}

	public void save(T entity) {
		Assert.notNull(entity);
		entityManager.persist(entity);
	}

	public T update(T entity) {
		Assert.notNull(entity);
		return entityManager.merge(entity);
	}

	public void delete(T entity) {
		if (entity != null) {
			entityManager.remove(entity);
		}
	}

	public void delete(ID id) {
		T entity = get(id);
		entityManager.remove(entity);
	}

	public void refresh(T entity) {
		if (entity != null) {
			entityManager.refresh(entity);
		}
	}

	public void refresh(T entity, LockModeType lockModeType) {
		if (entity != null) {
			if (lockModeType != null) {
				entityManager.refresh(entity, lockModeType);
			} else {
				entityManager.refresh(entity);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ID getIdentifier(T entity) {
		Assert.notNull(entity);
		return (ID) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
	}

	public boolean isManaged(T entity) {
		return entityManager.contains(entity);
	}

	public void detach(T entity) {
		entityManager.detach(entity);
	}

	public void lock(T entity, LockModeType lockModeType) {
		if (entity != null && lockModeType != null) {
			entityManager.lock(entity, lockModeType);
		}
	}

	public void clear() {
		entityManager.clear();
	}

	public void flush() {
		entityManager.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String makeSelectQuery() {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
			entityClass = (Class<T>) parameterizedType[0];
			return "select t from " + entityClass.getSimpleName() + " " + " t";
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public List<T> findList(String jpql) {
		Query query = entityManager.createQuery(jpql);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<T> findList(String jpql, Object[] params) {
		if (!jpql.contains("?") && (params == null || params.length == 0)) {
			return findList(jpql);
		} else {
			StringBuffer qlwithParams = new StringBuffer(jpql.length());
			char c;
			for (int i = 0; i < jpql.length(); i++) {
				c = jpql.charAt(i);
				if (jpql.charAt(i) == '?') {
					qlwithParams.append("?" + i);
				} else
					qlwithParams.append(c);
			}

			Query query = entityManager.createQuery(jpql);

			int idx = 1;
			for (Object o : params) {
				query.setParameter(idx++, o);
			}

			List<T> result = query.getResultList();
			return result;
		}

	}

	@SuppressWarnings("unchecked")
	public List<T> findList(final String jpql, final Object[] params, final int firstResult, final int maxResults) {
		StringBuffer qlwithParams = new StringBuffer(jpql.length());
		char c;
		for (int i = 0; i < jpql.length(); i++) {
			c = jpql.charAt(i);
			if (jpql.charAt(i) == '?') {
				qlwithParams.append("?" + i);
			} else
				qlwithParams.append(c);
		}

		Query query = entityManager.createQuery(jpql);
		if (params != null) {
			int idx = 1;
			for (Object o : params) {
				query.setParameter(idx++, o);
			}
		}

		if (firstResult > 0)
			query.setFirstResult(firstResult);
		if (maxResults > 0)
			query.setMaxResults(maxResults);
		List<T> result = query.getResultList();
		return result;
	}

	public List<T> findList(final String jpql, final String countQl, final Object[] params, PageInfo pageInfo) {
		if (pageInfo == null && countQl != null) {
			List<T> result = findList(jpql, params, 0, -1);
			return result;
		} else if (pageInfo == null && countQl == null) {
			List<T> result = this.findList(jpql, params, 0, -1);
			return result;
		} else if (pageInfo != null && countQl == null) {
			List<T> result = findList(jpql, params, pageInfo.getFirstResult(), pageInfo.getMaxResults());
			return result;
		} else {
			List<T> result = findList(jpql, params, pageInfo.getFirstResult(), pageInfo.getMaxResults());

			int count = queryCount(countQl, params);
			pageInfo.setRowCount(count);
			return result;
		}
	}

	public int queryCount(String jpql, Object[] params) {
		List<?> r = this.findCount(jpql, params, 0, -1);
		if (r == null || r.isEmpty())
			return 0;

		try {
			return Integer.parseInt(r.get(0).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public List<?> findCount(final String jpql, final Object[] params, final int firstResult, final int maxResults) {
		StringBuffer qlwithParams = new StringBuffer(jpql.length());
		char c;
		for (int i = 0; i < jpql.length(); i++) {
			c = jpql.charAt(i);
			if (jpql.charAt(i) == '?') {
				qlwithParams.append("?" + i);
			} else
				qlwithParams.append(c);
		}

		Query query = entityManager.createQuery(jpql);

		if (params != null) {
			int idx = 1;
			for (Object o : params) {
				query.setParameter(idx++, o);
			}
		}

		if (firstResult > 0)
			query.setFirstResult(firstResult);
		if (maxResults > 0)
			query.setMaxResults(maxResults);
		return query.getResultList();
	}
}
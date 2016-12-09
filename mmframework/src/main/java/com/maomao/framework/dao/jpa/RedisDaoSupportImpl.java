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
package com.maomao.framework.dao.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

import com.maomao.framework.dao.BaseDaoImpl;
import com.maomao.framework.support.jedis.JedisTemplate;
import com.maomao.framework.support.jedis.JedisTemplate.JedisAction;
import com.maomao.framework.support.jedis.JedisTemplate.JedisActionNoResult;
import com.maomao.framework.support.paglit.PageInfo;
import com.thoughtworks.xstream.XStream;

/**
 * Dao for redis.
 * @author maomao
 *
 * @param <T>
 * @param <ID>
 */
public class RedisDaoSupportImpl<T, ID extends Serializable> extends BaseDaoImpl<T, ID> {
	public static final String POOL = "objpool/";

	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;

	protected XStream x = new XStream();

	/**
	 * Get an object direct from db.
	 * @param id
	 * @return
	 */
	public T getFromDB(final ID id) {
		return super.get(id);
	}

	/**
	 * Get an object by id
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T get(final ID id) {
		if (id != null) {
			T t = jedisTemplate.execute(new JedisAction<T>() {
				
				@Override
				public T action(Jedis jedis) {
					String xml = jedis.hget(POOL + entityClass.getSimpleName(), id.toString());
					T t = null;
					if (StringUtils.isEmpty(xml)) {
						t = entityManager.find(entityClass, id);
						if (t != null) {
							jedis.hset(POOL + entityClass.getSimpleName(), id.toString(), x.toXML(t));
						}
					} else {
						t = (T) x.fromXML(xml);
					}
					return t;
				}
			});
			return t;
		}
		return null;
	}

	/**
	 * Get an object by id
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T get(final ID id, final LockModeType lockModeType) {
		if (id != null) {
			T t = jedisTemplate.execute(new JedisAction<T>() {
				@Override
				public T action(Jedis jedis) {
					String xml = jedis.hget(POOL + entityClass.getSimpleName(), id.toString());
					T t = null;
					if (StringUtils.isEmpty(xml)) {
						if (lockModeType != null) {
							t = entityManager.find(entityClass, id, lockModeType);
						} else {
							t = entityManager.find(entityClass, id);
						}

						if (t != null) {
							jedis.hset(POOL + entityClass.getSimpleName(), id.toString(), x.toXML(t));
						}
					} else {
						t = (T) x.fromXML(xml);
					}
					return t;
				}
			});
			return t;
		}
		return null;
	}

	/**
	 * Query directly from db.
	 * @param jpql
	 * @return
	 */
	public List<?> findListFromDB(String jpql) {
		return super.findList(jpql);
	}

	/**
	 * Query directly from db.
	 * @param jpql
	 * @param params
	 * @return
	 */
	public List<?> findListFromDB(String jpql, Object[] params) {
		return super.findList(jpql, params);
	}

	/**
	 * Query from db and retrive object from redis.
	 */
	@SuppressWarnings("unchecked")
	public List<T> findList(String jpql) {
		Query query = entityManager.createQuery(jpql);
		final List<String> keys = query.getResultList();

		final List<T> result = new ArrayList<T>();
		jedisTemplate.execute(new JedisAction<List<T>>() {
			@Override
			public List<T> action(Jedis jedis) {
				String xml;
				T o = null;
				for (String key : keys) {
					xml = jedis.hget(POOL + entityClass.getSimpleName(), key);
					if (StringUtils.isEmpty(xml) || "<null/>".equals(xml)) {
						o = RedisDaoSupportImpl.super.get((ID) key);
					} else {
						o = (T) x.fromXML(xml);
					}
					result.add(o);
				}
				return result;
			}
		});
		return result;
	}

	/**
	 * Query from db, and retrive objects from redis.
	 */
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

			final List<String> keys = query.getResultList();

			final List<T> result = new ArrayList<T>();
			jedisTemplate.execute(new JedisAction<List<T>>() {
				@Override
				public List<T> action(Jedis jedis) {
					String xml;
					T o = null;
					for (String key : keys) {
						xml = jedis.hget(POOL + entityClass.getSimpleName(), key);
						if (StringUtils.isEmpty(xml) || "<null/>".equals(xml)) {
							o = RedisDaoSupportImpl.super.get((ID) key);

							if (o != null) {
								xml = x.toXML(o);
								jedis.hset(POOL + entityClass.getSimpleName(), key, xml);
							}
						} else {
							o = (T) x.fromXML(xml);
						}
						result.add(o);
					}
					return result;
				}
			});
			return result;
		}

	}

	/**
	 * Query from db , and retrive objects from redis.
	 * 
	 * @param jpql
	 * @param params
	 * @return
	 */
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
		final List<String> keys = query.getResultList();
		final List<T> result = new ArrayList<T>();
		jedisTemplate.execute(new JedisAction<List<T>>() {
			@Override
			public List<T> action(Jedis jedis) {
				String xml;
				T o = null;
				for (String key : keys) {
					xml = jedis.hget(POOL + entityClass.getSimpleName(), key);
					if (StringUtils.isEmpty(xml) || "<null/>".equals(xml)) {
						o = RedisDaoSupportImpl.super.get((ID) key);
						if (o != null) {
							xml = x.toXML(o);
							jedis.hset(POOL + entityClass.getSimpleName(), key, xml);
						}
					} else {
						o = (T) x.fromXML(xml);
					}
					result.add(o);
				}
				return result;
			}
		});

		return result;
	}

	/**
	 * Query from db , and retrive objects from redis.
	 * 
	 * @param ql
	 * @param countQl
	 * @param params
	 * @param pageInfo
	 * @return
	 */
	public List<T> findList(final String jpql, final String countQl, final Object[] params, PageInfo pageInfo) {
		if (pageInfo == null && countQl != null) {
			List<T> result = findList(jpql, params, 0, -1);
			return result;
		} else if (pageInfo == null && countQl == null) {
			List<T> result = this.findList(jpql, params, 0, -1);
			return result;
		} else if (pageInfo != null && countQl == null) {
			List<T> result = findList(jpql, params, pageInfo.getFirstResult(), pageInfo.getMaxResults());
			pageInfo.setShowRows(result.size());
			return result;
		} else {
			List<T> result = findList(jpql, params, pageInfo.getFirstResult(), pageInfo.getMaxResults());

			int count = queryCount(countQl, params);
			pageInfo.setRowCount(count);
			pageInfo.setShowRows(result.size());
			return result;
		}
	}

	/**
	 * Query count from db.
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
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
	
	/**
	 * Query count from db.
	 */
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

	/**
	 * Persist an entity to db.
	 * @param entity
	 */
	public void persist(T entity) {
		Assert.notNull(entity);
		entityManager.persist(entity);

		final ID id = getIdentifier(entity);
		final T e2 = entity;
		jedisTemplate.execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				String xml = x.toXML(e2);
				jedis.hset(POOL + entityClass.getSimpleName(), id.toString(), xml);
			}
		});
	}

	@Override
	public void save(T entity) {
		persist(entity);
	}

	@Override
	public T update(T entity) {
		return merge(entity);
	}

	public T merge(T entity) {
		Assert.notNull(entity);
		T t = entityManager.merge(entity);

		final T t2 = t;
		final ID id = getIdentifier(entity);
		jedisTemplate.execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				String xml = x.toXML(t2);
				jedis.hset(POOL + entityClass.getSimpleName(), id.toString(), xml);
			}
		});

		return t;
	}

	public void remove(T entity) {
		if (entity != null) {
			final ID id = getIdentifier(entity);
			entityManager.remove(entity);
			jedisTemplate.execute(new JedisActionNoResult() {
				@Override
				public void action(Jedis jedis) {
					jedis.hdel(POOL + entityClass.getSimpleName(), id.toString());
				}
			});
		}
	}

	@Override
	public void delete(T entity) {
		if (entity != null) {
			remove(entity);
		}
	}

	@Override
	public void delete(final ID id) {
		T entity = super.get(id);
		entityManager.remove(entity);
		jedisTemplate.execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				jedis.hdel(POOL + entityClass.getSimpleName(), id.toString());
			}
		});
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
			return "select id from " + entityClass.getSimpleName() + " " + " t";
		}
		return "";
	}
}

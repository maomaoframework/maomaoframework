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
import java.util.List;

import javax.persistence.LockModeType;

import com.maomao.framework.support.paglit.PageInfo;

/**
 * Dao interface.
 * 
 * @author maomao
 * 
 * @param <T>
 * @param <ID>
 */
public interface IBaseDao<T, ID extends Serializable> {

	/**
	 * Get entity by id.
	 * 
	 * @param id
	 */
	T get(ID id);

	/**
	 * Get entity by id.
	 */
	T get(ID id, LockModeType lockModeType);

	/**
	 * Query entity list.
	 * 
	 * @param jpql
	 * @return
	 */
	List<T> findList(String jpql);

	List<T> findList(String jpql, Object[] params);

	List<T> findList(final String jpql, final Object[] params, final int firstResult, final int maxResults);

	List<T> findList(final String jpql, final String countQl, final Object[] params, PageInfo pageInfo);

	int queryCount(String jpql, Object[] params);

	List<?> findCount(final String jpql, final Object[] params, final int firstResult, final int maxResults);

	/**
	 * Persist entity.
	 * 
	 */
	void save(T entity);

	/**
	 * Update entity.
	 * 
	 */
	T update(T entity);

	/**
	 * Remove entity.
	 * 
	 */
	void delete(T entity);

	void delete(ID id);

	/**
	 * Refresh entity.
	 */
	void refresh(T entity);

	/**
	 * Refresh entity. 
	 * 
	 */
	void refresh(T entity, LockModeType lockModeType);

	/**
	 * Get entity id.
	 * 
	 */
	ID getIdentifier(T entity);

	/**
	 * Check if the entity is in persistant.
	 */
	boolean isManaged(T entity);

	/**
	 * Detach entity from persistant.
	 * 
	 */
	void detach(T entity);

	/**
	 * Locak an entity.
	 */
	void lock(T entity, LockModeType lockModeType);

	/**
	 * Clear entity cache.
	 */
	void clear();

	/**
	 * Flush entity to db.
	 */
	void flush();

	public String makeSelectQuery();
}
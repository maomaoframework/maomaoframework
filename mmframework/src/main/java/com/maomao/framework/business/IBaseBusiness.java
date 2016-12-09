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
package com.maomao.framework.business;

import java.io.Serializable;
import java.util.List;

import com.maomao.framework.support.paglit.PageInfo;

/**
 * Business interface
 * 
 * @author maomao
 * 
 * @param <T>
 * @param <ID>
 */
public interface IBaseBusiness<T, ID extends Serializable> {

	/**
	 * Query on entity.
	 * 
	 * @param id
	 *            ID
	 */
	T get(ID id);

	/**
	 * Query all entities
	 * 
	 */
	List<T> findAll();

	/**
	 * Query entities specified by the ids.
	 * 
	 * @param ids
	 *            ID
	 */
	@SuppressWarnings("unchecked")
	List<T> findList(ID... ids);

	/**
	 * Save entity.
	 */
	void save(T entity);

	/**
	 * Update entity.
	 */
	T update(T entity);

	/**
	 * Update entity.
	 */
	T update(T entity, String... ignoreProperties);

	/**
	 * Remove entity.
	 */
	void delete(ID id);

	/**
	 * Remoe entity.
	 */
	@SuppressWarnings("unchecked")
	void delete(ID... ids);

	/**
	 * Remove entity.
	 * 
	 */
	void delete(T entity);

	/**
	 * Query for list.
	 * 
	 * @param jpql
	 * @return
	 */
	List<T> findList(String jpql);

	/**
	 * Query for list with params.
	 */
	List<T> findList(String jpql, Object[] params);

	/**
	 * Query for list with params and pagelit.
	 * 
	 * @param jpql
	 * @param params
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	List<T> findList(final String jpql, final Object[] params, final int firstResult, final int maxResults);

	/**
	 * Query for list.
	 * 
	 * @param jpql
	 * @param countQl
	 * @param params
	 * @param pageInfo
	 * @return
	 */
	List<T> findList(final String jpql, final String countQl, final Object[] params, PageInfo pageInfo);

	/**
	 * Query for count.
	 * 
	 * @param jpql
	 * @param params
	 * @return
	 */
	int queryCount(String jpql, Object[] params);

	/**
	 * Make a query.
	 * 
	 * @return
	 */
	String makeSelectQuery();
}
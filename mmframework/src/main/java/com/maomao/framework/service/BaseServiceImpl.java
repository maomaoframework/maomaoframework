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
package com.maomao.framework.service;

import com.maomao.framework.support.paglit.PageInfo;

/**
 * Base service implement.
 * 
 * @author maomao
 */
public abstract class BaseServiceImpl {
	/**
	 * Create pagelit object.
	 * 
	 * @param pageNum
	 * @return
	 */
	public PageInfo initPageInfo(int pageNum) {
		int pageIndex = pageNum <= 0 ? 1 : pageNum;

		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentPageIndex(pageIndex);
		pageInfo.setPageSize(20);
		return pageInfo;
	}
}

/*
 * Copyright 2014 Effektif GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.effektif.adapter.service;

import com.effektif.workflow.api.datasource.ItemQuery;


/**
 * @author Tom Baeyens
 */
public class FindItemsRequest {

  protected String dataSourceKey;
  protected String itemType;
  protected ItemQuery itemQuery;

  public ItemQuery getItemQuery() {
    return this.itemQuery;
  }
  public void setItemQuery(ItemQuery itemQuery) {
    this.itemQuery = itemQuery;
  }
  public FindItemsRequest itemQuery(ItemQuery itemQuery) {
    this.itemQuery = itemQuery;
    return this;
  }

  public String getItemType() {
    return this.itemType;
  }
  public void setItemType(String itemType) {
    this.itemType = itemType;
  }
  public FindItemsRequest itemType(String itemType) {
    this.itemType = itemType;
    return this;
  }

  public String getDataSourceKey() {
    return this.dataSourceKey;
  }
  public void setDataSourceKey(String dataSourceKey) {
    this.dataSourceKey = dataSourceKey;
  }
  public FindItemsRequest dataSourceKey(String dataSourceKey) {
    this.dataSourceKey = dataSourceKey;
    return this;
  }
  
}

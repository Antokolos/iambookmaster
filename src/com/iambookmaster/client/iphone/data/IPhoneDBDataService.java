/*
 * Copyright 2009 Bart Guijt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.iambookmaster.client.iphone.data;

import java.util.Date;

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.service.Connection;
import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.RowIdListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.Select;
import com.google.code.gwt.database.client.service.Update;
import com.google.code.gwt.database.client.service.VoidCallback;

@Connection(name="iambookmaster", version="1.0", description="I am Book Master", maxsize=10000)
public class IPhoneDBDataService implements DataService {

    public Database getDatabase() {return null;}
	  /**
   * Makes sure that the 'saves' table exists in the Database.
   */
  @Update("CREATE TABLE IF NOT EXISTS saves ("
      + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
      + "gameId NVCHAR(32) NOT NULL," +
      	"name VARCHAR(256) NOT NULL, " +
      	"data CLOB NOT NULL," +
      	"stamp INTEGER NOT NULL)")
  public void initTable(VoidCallback callback) {}

  /**
   * Records a Click value, and obtains the ID of the inserted record.
   */
  @Update("INSERT INTO saves (gameId,name,data,stamp) VALUES ({saveGameId},{saveName},{saveData},{when.getTime()})")
  public void insertSave(String saveGameId, String saveName, String saveData, Date when, RowIdListCallback callback) {}

  /**
   * Returns all clicks.
   */
  @Select("SELECT id,gameId,name,data,stamp FROM saves WHERE gameId={saveGameId}")
  public void selectList(String saveGameId,ListCallback<IPhoneSaveGameBean> callback) {}
  
  /**
   * Obtains the number of clicks recorded in the database.
   */
  @Select("SELECT count(*) FROM saves WHERE gameId={saveGameId}")
  public void getClickCount(String saveGameId, ScalarCallback<Integer> callback) {}

  @Update("DELETE FROM saves WHERE id={id}")
  public void deleteSave(int id, VoidCallback callback) {}
  
}

/* Copyright (c) 2014, Effektif GmbH.
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
 * limitations under the License. */
package com.effektif.mongo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;

import com.effektif.workflow.api.deprecated.acl.Authentication;
import com.effektif.workflow.api.deprecated.acl.Authentications;
import com.effektif.workflow.api.deprecated.model.FileId;
import com.effektif.workflow.impl.configuration.Brewable;
import com.effektif.workflow.impl.configuration.Brewery;
import com.effektif.workflow.impl.deprecated.file.File;
import com.effektif.workflow.impl.deprecated.file.FileService;
import com.effektif.workflow.impl.util.Exceptions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;


/**
 * @author Tom Baeyens
 */
public class MongoFileService implements FileService, Brewable {

  public static final Logger log = MongoDb.log;
  
  public MongoCollection filesCollection;
  public GridFS gridFs;
  public MongoJsonMapper mongoJsonMapper;
  
  public interface FieldsFile {
    String _ID = "_id";
    String ORGANIZATION_ID = "organizationId";
    String FILE_NAME = "fileName";
    String taskId = "taskId";
  }

  @Override
  public void brew(Brewery brewery) {
    MongoDb mongoDb = brewery.get(MongoDb.class);
    MongoConfiguration mongoConfiguration = brewery.get(MongoConfiguration.class);
    this.filesCollection = mongoDb.createCollection(mongoConfiguration.getFilesCollectionName());
    this.gridFs = brewery.get(GridFS.class);
    this.mongoJsonMapper = brewery.get(MongoJsonMapper.class);
  }
  
  @Override
  public File createFile(File file, InputStream fileStream) {
    Exceptions.checkNotNullParameter(file, "file");
    Exceptions.checkNotNullParameter(fileStream, "file.inputStream");
    
    GridFSInputFile gridFsFile = gridFs.createFile(fileStream);
    gridFsFile.save();
    
    file.setStreamId(gridFsFile.getId().toString());
    
    insertFile(file);

    return file;
  }

  @Override
  public File createFile(File file) {
    Exceptions.checkNotNullParameter(file, "file");
    Exceptions.checkNotNullParameter(file.getStreamId(), "file.streamId");

    insertFile(file);

    return null;
  }

  protected void insertFile(File file) {
    BasicDBObject dbFile = mongoJsonMapper.writeToDbObject(file);
    filesCollection.insert("insert-file", dbFile);
    ObjectId id = (ObjectId) dbFile.get("_id");
    file.setId(new FileId(id.toString()));
  }
  
  @Override
  public InputStream getFileStream(String fileStreamId) {
    BasicDBObject query = new BasicDBObject("_id", new ObjectId(fileStreamId));
    GridFSDBFile file = gridFs.findOne(query);
    return file!=null ? file.getInputStream() : null;
  }
  
  @Override
  public File getFileById(FileId fileId) {
    Authentication authentication = Authentications.current();
    String organizationId = authentication!=null ? authentication.getOrganizationId() : null;

    BasicDBObject query = new MongoQuery()
      ._id(new ObjectId(fileId.getInternal()))
      .equalOpt(FieldsFile.ORGANIZATION_ID, organizationId)
      .get();

    BasicDBObject dbFile = filesCollection.findOne("get-file", query);

    return mongoJsonMapper.readFromDbObject(dbFile, File.class);
  }
  
  @Override
  public List<File> getFilesByIds(Collection<FileId> fileIds) {
    List<File> files = new ArrayList<>();
    if (fileIds!=null) {
      List<ObjectId> dbFileIds = new ArrayList<>();
      for (FileId fileId: fileIds) {
        dbFileIds.add(new ObjectId(fileId.getInternal()));
      }
      Authentication authentication = Authentications.current();
      String organizationId = authentication!=null ? authentication.getOrganizationId() : null;
      BasicDBObject query = new MongoQuery()
        .in("_id", dbFileIds)
        .equalOpt(FieldsFile.ORGANIZATION_ID, organizationId)
        .get();
  
      DBCursor dbFiles = filesCollection.find("get-files", query);
      while (dbFiles.hasNext()) {
        BasicDBObject dbFile = (BasicDBObject) dbFiles.next();
        File file = mongoJsonMapper.readFromDbObject(dbFile, File.class);
        files.add(file);
      }
    }
    return files;
  }
}
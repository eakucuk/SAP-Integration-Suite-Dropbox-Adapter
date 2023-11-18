package org.example;


/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.net.URISyntaxException;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultPollingEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a www.Sample.com Camel endpoint.
 */
@UriEndpoint(scheme = "sap-sample", syntax = "", title = "")
public class denemeComponentEndpoint extends DefaultPollingEndpoint {
    private denemeComponentComponent component;

    private transient Logger logger = LoggerFactory.getLogger(denemeComponentEndpoint.class);

    @UriParam
    private String greetingsMessage;
    
    @UriParam
    private boolean useFormater;

    @UriParam
    private String FilePath;

    @UriParam
    private String FolderPath;

    @UriParam
    private String FromPath;

    @UriParam
    private String ToPath;

    public int getMaxResults() {
        return MaxResults;
    }

    public void setMaxResults(int maxResults) {
        MaxResults = maxResults;
    }

    @UriParam
    private int MaxResults;

    @UriParam
    private String Path;

    public String getOptionalPath() {
        return OptionalPath;
    }

    public void setOptionalPath(String optionalPath) {
        OptionalPath = optionalPath;
    }

    @UriParam
    private String OptionalPath;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @UriParam
    private String query;

    @UriParam
    private String operation;

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    @UriParam
    private String credential;

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    @UriParam
    private String dataFormat;

    @UriParam
    private boolean autoRename;

    @UriParam
    private boolean recursive;

    public boolean isEmre() {
        return emre;
    }

    public void setEmre(boolean emre) {
        this.emre = emre;
    }

    @UriParam
    private boolean emre;

    public boolean isSharedMemb() {
        return sharedMemb;
    }

    public void setSharedMemb(boolean sharedMemb) {
        this.sharedMemb = sharedMemb;
    }

    @UriParam
    private boolean sharedMemb;


    @UriParam
    private boolean includeMediaInfo;

    @UriParam
    private boolean includeDeleted;

    @UriParam
    private boolean includeMountedFolders;

    @UriParam
    private boolean includeNonDownloadableFiles;

    public String getFilePath(){
        return FilePath;
    }

    public void setFilePath(String FilePath){
        this.FilePath = FilePath;
    }

    public String getPath(){
        return Path;
    }

    public void setPath(String Path){
        this.Path = Path;
    }



    public boolean getAutoRename(){
        return autoRename;
    }

    public void setAutoRename(boolean autoRename){
        this.autoRename = autoRename;
    }

    public boolean getRecursive(){
        return recursive;
    }

    public void setRecursive(boolean recursive){
        this.recursive = recursive;
    }

    public boolean getIncludeMediaInfo(){
        return includeMediaInfo;
    }

    public void setIncludeMediaInfo(boolean includeMediaInfo){
        this.includeMediaInfo = includeMediaInfo;
    }

    public boolean getIncludeDeleted(){
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted){
        this.includeDeleted = includeDeleted;
    }

    public boolean getIncludeMountedFolders(){
        return includeMountedFolders;
    }

    public void setIncludeMountedFolders(boolean includeMountedFolders){
        this.includeMountedFolders = includeMountedFolders;
    }

    public boolean getIncludeNonDownloadableFiles(){
        return includeNonDownloadableFiles;
    }

    public void setIncludeNonDownloadableFiles(boolean includeNonDownloadableFiles){
        this.includeNonDownloadableFiles = includeNonDownloadableFiles;
    }

    public String getFolderPath(){
        return FolderPath;
    }

    public void setFolderPath(String FolderPath){
        this.FolderPath = FolderPath;
    }

    public String getFromPath(){
        return FromPath;
    }

    public void setFromPath(String FromPath){
        this.FromPath = FromPath;
    }

    public String getToPath(){
        return ToPath;
    }

    public void setToPath(String ToPath){
        this.ToPath = ToPath;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

	public boolean getUseFormater() {
		return useFormater;
	}

	public void setUseFormater(boolean useFormater) {
		this.useFormater = useFormater;
	}

	public String getGreetingsMessage() {
		return greetingsMessage;
	}

	public void setGreetingsMessage(String greetingsMessage) {
		this.greetingsMessage = greetingsMessage;
	}

	public denemeComponentEndpoint() {
    }

    public denemeComponentEndpoint(final String endpointUri, final denemeComponentComponent component) throws URISyntaxException {
        super(endpointUri, component);
        this.component = component;
    }

    public denemeComponentEndpoint(final String uri, final String remaining, final denemeComponentComponent component) throws URISyntaxException {
        this(uri, component);
    }

    public Producer createProducer() throws Exception {
        return new denemeComponentProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        final denemeComponentConsumer consumer = new denemeComponentConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    public boolean isSingleton() {
        return true;
    }
}

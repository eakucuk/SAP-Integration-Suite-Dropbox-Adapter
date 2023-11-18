/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example;

import com.sap.it.api.ITApiFactory;
import com.sap.it.api.securestore.AccessTokenAndUser;
import com.sap.it.api.securestore.SecureStoreService;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.DataOutputStream;
import org.json.JSONObject;

/**
 * The www.Sample.com producer.
 */
public class denemeComponentProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(denemeComponentProducer.class);
    private denemeComponentEndpoint endpoint;

    public denemeComponentProducer(denemeComponentEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }


    public void process(final Exchange exchange) throws Exception {


        SecureStoreService secureStoreService = ITApiFactory.getService(SecureStoreService.class, null);
        AccessTokenAndUser accessTokenAndUser = secureStoreService.getAccesTokenForOauth2AuthorizationCodeCredential(endpoint.getCredential());
        String token = accessTokenAndUser.getAccessToken();
        String authorization =  "Bearer " + token;

        String operation = endpoint.getOperation();
        boolean autoRename = endpoint.getAutoRename();
        boolean include_media_info = endpoint.getIncludeMediaInfo();
        boolean include_deleted = endpoint.getIncludeDeleted();
        boolean recursive = endpoint.getRecursive();
        boolean sharedMemb = endpoint.isSharedMemb();
        String dataFormat = endpoint.getDataFormat();


        byte[] fileContent = exchange.getIn().getBody(byte[].class); // Read the file content as byte[]


        if (Objects.equals(operation, "UploadFile")) {

            String path = endpoint.getFilePath();

            JSONObject DropboxHeader =new JSONObject();
            DropboxHeader.put("path",path);
            DropboxHeader.put("mode","add");
            DropboxHeader.put("autorename",autoRename);
            DropboxHeader.put("mute",false);
            DropboxHeader.put("strict_conflict",false);


            String serviceURL = "https://content.dropboxapi.com/2/files/upload";
            URL myURL = new URL(serviceURL);
            HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty("Authorization", authorization);
            myURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
            myURLConnection.setRequestProperty("Dropbox-API-Arg",  DropboxHeader.toString() );


            myURLConnection.setDoOutput(true);

            try (OutputStream outputStream = myURLConnection.getOutputStream()) {
                outputStream.write(fileContent); // Write the byte[] data directly to the output stream
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }

        }

        else if (Objects.equals(operation, "DownloadFile")) {

            String path = endpoint.getFilePath();

            JSONObject DropboxHeader =new JSONObject();
            DropboxHeader.put("path",path);

            String serviceURL = "https://content.dropboxapi.com/2/files/download";
            URL myURL = new URL(serviceURL);
            HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty("Authorization", authorization);
            myURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
            myURLConnection.setRequestProperty("Dropbox-API-Arg",   DropboxHeader.toString()   );

            myURLConnection.setDoOutput(true);

            try (InputStream inputStream = myURLConnection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                StringBuilder base64Content = new StringBuilder();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byte[] chunk = new byte[bytesRead];
                    System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                    base64Content.append(Base64.getEncoder().encodeToString(chunk));
                }

                String base64File = base64Content.toString();
                exchange.getIn().setBody(base64File);
            }
        }

        else if (Objects.equals(operation, "DownloadZipFile")) {

            String path = endpoint.getFolderPath();

            JSONObject DropboxHeader = new JSONObject();
            DropboxHeader.put("path", path);

            String serviceURL = "https://content.dropboxapi.com/2/files/download_zip";
            URL myURL = new URL(serviceURL);
            HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty("Authorization", authorization);
            myURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
            myURLConnection.setRequestProperty("Dropbox-API-Arg", DropboxHeader.toString());

            myURLConnection.setDoOutput(true);


        }

        else if (Objects.equals(operation, "GetMetadata")) {

           String path = endpoint.getPath();


            JSONObject body =new JSONObject();
            body.put("path",path);
            body.put("include_media_info",include_media_info);
            body.put("include_deleted",include_deleted);
            body.put("include_has_explicit_shared_members",sharedMemb);


            String requestBody = body.toString();

            String serviceURL = "https://api.dropboxapi.com/2/files/get_metadata";
            String method = "POST";

            HttpURLConnection myURLConnection = DropboxCall(serviceURL, method ,authorization);

            try (DataOutputStream outputStream = new DataOutputStream(myURLConnection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }


        }

        else if (Objects.equals(operation, "ListFolder")) {

            String path = endpoint.getFolderPath();


            boolean include_mounted_folders = endpoint.getIncludeMountedFolders();
            boolean include_non_downloadable_files = endpoint.getIncludeNonDownloadableFiles();

            JSONObject body =new JSONObject();
            body.put("path",path);
            body.put("recursive",recursive);
            body.put("include_media_info",include_media_info);
            body.put("include_deleted",include_deleted);
            body.put("include_mounted_folders",include_mounted_folders);
            body.put("include_non_downloadable_files",include_non_downloadable_files);
            body.put("include_has_explicit_shared_members",sharedMemb);
            String requestBody = body.toString();

            String serviceURL = "https://api.dropboxapi.com/2/files/list_folder";
            String method = "POST";

            HttpURLConnection myURLConnection = DropboxCall(serviceURL, method ,authorization);

            try (DataOutputStream outputStream = new DataOutputStream(myURLConnection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }

        }

        else if (Objects.equals(operation, "CreateFolder")) {

            String path = endpoint.getFolderPath();

            JSONObject body =new JSONObject();
            body.put("path",path);
            body.put("autorename",autoRename);
            String requestBody = body.toString();

            String serviceURL = "https://api.dropboxapi.com/2/files/create_folder_v2";
            String method = "POST";

            HttpURLConnection myURLConnection = DropboxCall(serviceURL, method ,authorization);

            try (DataOutputStream outputStream = new DataOutputStream(myURLConnection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }

        }

        else if (Objects.equals(operation, "DeleteFileOrFolder")) {

            String path = endpoint.getPath();

            JSONObject body =new JSONObject();
            body.put("path",path);
            String requestBody = body.toString();

            String serviceURL = "https://api.dropboxapi.com/2/files/delete_v2";
            String method = "POST";


            HttpURLConnection myURLConnection = DropboxCall(serviceURL, method ,authorization);

            try (DataOutputStream outputStream = new DataOutputStream(myURLConnection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }

        }

        else if (Objects.equals(operation, "MoveFile")){

            String fromPath = endpoint.getFromPath();
            String toPath = endpoint.getToPath();

            JSONObject body =new JSONObject();
            body.put("from_path",fromPath);
            body.put("to_path",toPath);
            String requestBody = body.toString();

            String serviceURL = "https://api.dropboxapi.com/2/files/move_v2";
            String method = "POST";

            HttpURLConnection myURLConnection = DropboxCall(serviceURL, method ,authorization);

            try (DataOutputStream outputStream = new DataOutputStream(myURLConnection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }


        }

        else if (Objects.equals(operation, "CopyFile")){

            String fromPath = endpoint.getFromPath();
            String toPath = endpoint.getToPath();

            JSONObject body =new JSONObject();
            body.put("from_path",fromPath);
            body.put("to_path",toPath);
            body.put("autorename",autoRename);
            String requestBody = body.toString();

            String serviceURL = "https://api.dropboxapi.com/2/files/copy_v2";
            String method = "POST";

            HttpURLConnection myURLConnection = DropboxCall(serviceURL, method ,authorization);

            try (DataOutputStream outputStream = new DataOutputStream(myURLConnection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }


        }


        else if (Objects.equals(operation, "Search")){

            String query = endpoint.getQuery();
            String path = endpoint.getOptionalPath();
            int maxResults = endpoint.getMaxResults();


            JSONObject body =new JSONObject();

            JSONObject matchFieldOptions = new JSONObject();
            matchFieldOptions.put("include_highlights", false);
            body.put("match_field_options", matchFieldOptions);

            JSONObject options = new JSONObject();
            options.put("file_status", "active");
            options.put("filename_only", false);
            options.put("max_results", maxResults);
            options.put("path", path);
            body.put("options", options);

            body.put("query", query);
            String requestBody = body.toString();

            String serviceURL = "https://api.dropboxapi.com/2/files/search_v2";
            String method = "POST";

            HttpURLConnection myURLConnection = DropboxCall(serviceURL, method ,authorization);

            try (DataOutputStream outputStream = new DataOutputStream(myURLConnection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            String response = getResponse(myURLConnection);

            if(Objects.equals(dataFormat, "JSON")){
                exchange.getIn().setBody(response);
            }
            else{
                exchange.getIn().setBody(convertXML(response));
            }

        }
    }

    public String getResponse(HttpURLConnection myURLConnection) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            return responseBuilder.toString();
        }
        catch (IOException e) {

            InputStream errorStream = myURLConnection.getErrorStream();
            return convertStreamToString(errorStream);
        }

    }

    private String convertXML(String response) throws IOException {



        JSONObject json = new JSONObject(response);
        return "<root>" + XML.toString(json) + "</root>";
    }

    private HttpURLConnection DropboxCall(String serviceURL, String method ,String authorization) throws IOException {

        URL myURL = new URL(serviceURL);
        HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
        myURLConnection.setRequestMethod(method);
        myURLConnection.setRequestProperty("Authorization", authorization);
        myURLConnection.setRequestProperty("Content-Type", "application/json");
        myURLConnection.setDoOutput(true);

        return myURLConnection;

    }

    private static String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        return stringBuilder.toString();
    }

}

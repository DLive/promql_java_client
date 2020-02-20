package com.bdwise.prometheus.client;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class PromethusApiClient {

    private String host;
    private HttpHost targetHost;
    private HttpClient httpClient;
    public PromethusApiClient(String host,Integer port){
        init(host,port);
    }
    private void init(String host,Integer port){

        httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(10)
                .build();
        targetHost = new HttpHost(host, port, "http");
    }

    public String request(URI targetUri) throws IOException {
        HttpGet getRequest = new HttpGet(targetUri);
        HttpResponse httpResponse = null;
        httpResponse = httpClient.execute(targetHost, getRequest);

        HttpEntity entity = httpResponse.getEntity();

//        System.out.println(httpResponse.getStatusLine());
//        Header[] headers = httpResponse.getAllHeaders();
//        for (int i = 0; i < headers.length; i++) {
//            System.out.println(headers[i]);
//        }
//        System.out.println("----------------------------------------");

        if (entity != null) {
            return EntityUtils.toString(entity);
        }else{
            return null;
        }
    }

}

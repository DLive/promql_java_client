package com.bdwise.prometheus.client;

import com.bdwise.prometheus.client.builder.*;
import com.bdwise.prometheus.client.converter.ConvertUtil;
import com.bdwise.prometheus.client.converter.am.DefaultAlertManagerResult;
import com.bdwise.prometheus.client.converter.label.DefaultLabelResult;
import com.bdwise.prometheus.client.converter.query.DefaultQueryResult;
import com.bdwise.prometheus.client.converter.query.MatrixData;
import com.bdwise.prometheus.client.converter.query.QueryResultItemValue;
import com.bdwise.prometheus.client.converter.query.VectorData;
import com.bdwise.prometheus.client.converter.status.DefaultConfigResult;
import com.bdwise.prometheus.client.converter.target.DefaultTargetResult;
import junit.framework.TestCase;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientTest extends TestCase {

    private final static String TARGET_SERVER = "http://192.168.66.60:9090";

    private PromethusApiClient promethusApiClient;

    @Override
    protected void setUp() throws Exception {
        promethusApiClient = new PromethusApiClient("127.0.0.1",80);
    }

    private static String ConvertEpocToFormattedDate(String format, double epocTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(new Date(Math.round(epocTime*1000)));
    }

    public void testSimpleRangeQuery() throws IOException {
        RangeQueryBuilder rangeQueryBuilder =  QueryBuilderType.RangeQuery.newInstance(TARGET_SERVER);
        URI targetUri = rangeQueryBuilder.withQuery("100 - avg(rate(node_cpu{application=\"node_exporter\", mode=\"idle\"}[1m])) by (instance)*100")
                .withStartEpochTime(System.currentTimeMillis() / 1000 - 60*10)
                .withEndEpochTime(System.currentTimeMillis() / 1000)
                .withStepTime("60s")
                .build();

        System.out.println(targetUri.toURL().toString());

        String rtVal = promethusApiClient.request(targetUri);




        DefaultQueryResult<MatrixData> result = ConvertUtil.convertQueryResultString(rtVal);

        for(MatrixData matrixData : result.getResult()) {
            System.out.println(String.format("%s", matrixData.getMetric().get("instance")));
            for(QueryResultItemValue itemValue : matrixData.getDataValues()) {
                System.out.println(String.format("%s %10.2f ",
                        ConvertEpocToFormattedDate("yyyy-MM-dd hh:mm:ss", itemValue.getTimestamp()),
                        itemValue.getValue()
                ));
            }
        }

    }

    public void testSimpleVectorQuery() throws IOException {
        InstantQueryBuilder iqb = QueryBuilderType.InstantQuery.newInstance(TARGET_SERVER);
        URI targetUri = iqb.withQuery("node_cpu{application=\"node_exporter\", mode=\"idle\"}[1m]").build();



        String rtVal = promethusApiClient.request(targetUri);


        DefaultQueryResult<MatrixData> result = ConvertUtil.convertQueryResultString(rtVal);


        for(MatrixData matrixData : result.getResult()) {
            System.out.println(String.format("%s", matrixData.getMetric().get("instance")));
            for(QueryResultItemValue itemValue : matrixData.getDataValues()) {
                System.out.println(String.format("%s %10.2f ",
                        ConvertEpocToFormattedDate("yyyy-MM-dd hh:mm:ss", itemValue.getTimestamp()),
                        itemValue.getValue()
                ));
            }
        }

        System.out.println(targetUri.toURL().toString());
//		System.out.println(result);
    }

    public void testSimpleInstantQuery() throws IOException {
        InstantQueryBuilder iqb = QueryBuilderType.InstantQuery.newInstance(TARGET_SERVER);
        URI targetUri = iqb.withQuery("100 - avg(rate(node_cpu{application=\"node_exporter\", mode=\"idle\"}[1m])) by (instance)*100").build();
        System.out.println(targetUri.toURL().toString());


        String rtVal = promethusApiClient.request(targetUri);


        DefaultQueryResult<VectorData> result = ConvertUtil.convertQueryResultString(rtVal);


        for(VectorData vectorData : result.getResult()) {
            System.out.println(String.format("%s %s %10.2f",
                    vectorData.getMetric().get("instance"),
                    vectorData.getFormattedTimestamps("yyyy-MM-dd hh:mm:ss"),
                    vectorData.getValue() ));
        }

        System.out.println(result);
    }

    public void testSimpleLabel() throws IOException {
        LabelMetaQueryBuilder lmqb = QueryBuilderType.LabelMetadaQuery.newInstance(TARGET_SERVER);
        URI targetUri = lmqb.withLabel("pod").build();
        System.out.println(targetUri.toURL().toString());


        String rtVal = promethusApiClient.request(targetUri);


        DefaultLabelResult result = ConvertUtil.convertLabelResultString(rtVal);


        System.out.println(result);
    }

    public void testSimpleConfig() throws IOException {
        StatusMetaQueryBuilder smqb = QueryBuilderType.StatusMetadaQuery.newInstance(TARGET_SERVER);
        URI targetUri = smqb.build();
        System.out.println(targetUri.toURL().toString());


        String rtVal = promethusApiClient.request(targetUri);


        DefaultConfigResult result = ConvertUtil.convertConfigResultString(rtVal);


        System.out.println(result);
    }

    public void testSimpleTargets() throws IOException {
        TargetMetaQueryBuilder tmqb = QueryBuilderType.TargetMetadaQuery.newInstance(TARGET_SERVER);
        URI targetUri = tmqb.build();
        System.out.println(targetUri.toURL().toString());


        String rtVal = promethusApiClient.request(targetUri);


        DefaultTargetResult result = ConvertUtil.convertTargetResultString(rtVal);


        System.out.println(result);
    }

    public void testSimpleAlertManager() throws IOException {
        AlertManagerMetaQueryBuilder ammqb = QueryBuilderType.AlertManagerMetadaQuery.newInstance(TARGET_SERVER);
        URI targetUri = ammqb.build();
        System.out.println(targetUri.toURL().toString());


        String rtVal = promethusApiClient.request(targetUri);


        DefaultAlertManagerResult result = ConvertUtil.convertAlertManagerResultString(rtVal);


        System.out.println(result);
    }

}

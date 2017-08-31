package org.jon.lv.mapping.query;

import org.jon.lv.ESRestClient;
import org.jon.lv.query.QueryHandle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Package org.jon.lv.mapping.query.QueryHandleTest
 * @Description: QueryHandleTest
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/8/30 9:40
 * version V1.0.0
 */
public class QueryHandleTest {

    private QueryHandle queryHandle;


    @Before
    public void init() {
        queryHandle = new QueryHandle(ESRestClient.getClient(), "library", "book");
    }

    @Test
    public void matchAll() throws IOException {
        Set<String> rtnFields = new HashSet<>();
        rtnFields.addAll(Arrays.asList("title", "summary", "publish_date"));

        System.out.println(queryHandle.matchAll("guide", 0, 2, rtnFields));
    }

    @Test
    public void multiField() throws IOException {
        Set<String> docFields = new HashSet<>();
        docFields.addAll(Arrays.asList("title", "summary^3"));

        Set<String> rtnFields = new HashSet<>();
        rtnFields.addAll(Arrays.asList("title", "summary", "publish_date"));

        System.out.println(queryHandle.multiField("guide", 0, 2, docFields, rtnFields));
    }

    @Test
    public void fuzzyQuery() throws IOException {

        System.out.println(queryHandle.fuzzyQuery("guide", 0, 2, null, null));

        Set<String> docFields = new HashSet<>();
        docFields.addAll(Arrays.asList("title", "summary"));

        Set<String> rtnFields = new HashSet<>();
        rtnFields.addAll(Arrays.asList("title", "summary", "publish_date"));
        System.out.println(queryHandle.fuzzyQuery("guide", 0, 2, docFields, rtnFields));
    }

    @Test
    public void wildcardQuery() throws IOException {
        System.out.println(queryHandle.wildcardQuery("t*", 0, 2, "authors", null));
        Set<String> rtnFields = new HashSet<>();
        rtnFields.addAll(Arrays.asList("title", "summary", "publish_date"));
        System.out.println(queryHandle.wildcardQuery("t*", 0, 2, "authors", rtnFields));
    }

    @After
    public void close() throws IOException {
        queryHandle.restClient.close();
    }
}

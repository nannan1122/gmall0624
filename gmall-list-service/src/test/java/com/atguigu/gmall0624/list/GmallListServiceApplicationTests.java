package com.atguigu.gmall0624.list;


import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {
	@Autowired
	JestClient jestClient;

	@Test
	public void contextLoads() {
	}
	@Test
	public void testES() throws IOException {
		//定义dsl语句
		String query="{\n" +
				"  \"query\": {\n" +
				"    \"match_all\": {}\n" +
				"  }\n" +
				"}";

		//执行动作在哪个index，type 中执行
		Search search = new Search.Builder(query).addIndex("movie_index").addType("movie").build();

		//执行jestClient
		SearchResult searchResult = jestClient.execute(search);

		//结果集获取
		List<SearchResult.Hit<Map, Void>> hits = searchResult.getHits(Map.class);
		for (SearchResult.Hit<Map, Void> hit : hits) {
			Map map = hit.source;
			System.out.println(map);
		}

	}


}

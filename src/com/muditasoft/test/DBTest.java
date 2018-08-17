package com.muditasoft.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class DBTest {

	@Test
	void hasAConnection() throws Exception {
		Properties props = new Properties();
		props.load(new FileReader("src/com/muditasoft/properties/persistence-mysql.properties"));
		
		String url = props.getProperty("jdbc.url");
		String user = props.getProperty("jdbc.user");
		String password = props.getProperty("jdbc.password");
		
		Connection conn = DriverManager.getConnection(url, user, password);
		
		assertTrue(conn != null);
		conn.close();
	}

}

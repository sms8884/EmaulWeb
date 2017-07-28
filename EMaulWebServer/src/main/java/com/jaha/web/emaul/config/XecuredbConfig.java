/**
 * 
 */
package com.jaha.web.emaul.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.softforum.xdbe.XdspNative;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 */
@Configuration
public class XecuredbConfig {
	
	@Value("${xecuredb.conf.path}")
    private String xecuredbConfPath;

	@Bean
	public XdspNative setXdspNative() {
		XdspNative.setPropertiesFile(xecuredbConfPath);
		return XdspNative.propertyInitialize();
	}
	
}

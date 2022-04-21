package com.vmware.pbj.feature

import io.cucumber.core.options.Constants
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/vmware/pbj/feature")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.vmware.pbj.feature")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
class CucumberRunner

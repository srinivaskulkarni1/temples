package com.temples.in.consume_data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.consume_data.Params;
import com.temples.in.consume_data.ParamsBuilder;
import com.temples.in.consume_util.BeanConstants;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.table_info.DBConstants;
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ParamsBuilderTest {
	
	@Mock
	AbstractApplicationContext context;

	@Autowired
	@InjectMocks
	ParamsBuilder paramsBuilder;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

}

package testfiles;

import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

//package io.elasticjob.lite.spring.job;

import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import io.elasticjob.lite.spring.fixture.job.DataflowElasticJob;
import io.elasticjob.lite.spring.fixture.job.FooSimpleElasticJob;
import io.elasticjob.lite.spring.test.AbstractZookeeperJUnit4SpringContextTests;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractJobSpringIntegrateTest extends AbstractZookeeperJUnit4SpringContextTests {

	private final String simpleJobName;

	private final String throughputDataflowJobName;

	@Resource
	private CoordinatorRegistryCenter regCenter;

	@Before
	@After
	public void reset() {
		FooSimpleElasticJob.reset();
		DataflowElasticJob.reset();
	}

	@After
	public void tearDown() {
		JobRegistry.getInstance().shutdown(simpleJobName);
		JobRegistry.getInstance().shutdown(throughputDataflowJobName);
	}

	@Test
	public void assertSpringJobBean() {
		assertSimpleElasticJobBean();
		assertThroughputDataflowElasticJobBean();
	}

	private void assertSimpleElasticJobBean() {
		while (!FooSimpleElasticJob.isCompleted()) {
			sleep(100L);
		}
		assertTrue(FooSimpleElasticJob.isCompleted());
		assertTrue(regCenter.isExisted("/" + simpleJobName + "/sharding"));
	}

	private void assertThroughputDataflowElasticJobBean() {
		while (!DataflowElasticJob.isCompleted()) {
			sleep(100L);
		}
		assertTrue(DataflowElasticJob.isCompleted());
		assertTrue(regCenter.isExisted("/" + throughputDataflowJobName + "/sharding"));
	}

	private static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}

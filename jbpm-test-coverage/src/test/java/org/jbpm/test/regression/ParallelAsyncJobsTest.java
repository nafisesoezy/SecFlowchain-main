/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
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
 */

package org.jbpm.test.regression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.AsynchronousJobEvent;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.jbpm.test.listener.CountDownAsyncJobListener;
import org.jbpm.test.persistence.util.PersistenceUtil;
import org.junit.Test;
import org.kie.api.executor.ExecutorService;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.test.util.db.PoolingDataSourceWrapper;

public class ParallelAsyncJobsTest extends JbpmAsyncJobTestCase {

    private static final String PARENT = "org/jbpm/test/regression/ParallelAsyncJobs-parent.bpmn2";
    private static final String PARENT_ID = "org.jbpm.test.regression.ParallelAsyncJobs-parent";

    private static final String SUBPROCESS = "org/jbpm/test/regression/ParallelAsyncJobs-subprocess.bpmn2";

    /**
     * The tests verifies that async. jobs will be executed in parallel.
     *
     * JobExecutor configuration:
     * - Thread pool size = 4
     * - Pending task scan interval = 1 second
     *
     * The process in this test will create 4 long running tasks (8 seconds) one after another. Then
     * the test will sleep for 4 seconds giving the JobExecutor time to pick up at least 2 tasks.
     *
     * The JobExecutor should be able to pick up all the tasks because one task takes 8 seconds to
     * complete and the scan interval is 1 second. On the other hand a task should not complete in
     * the 4 seconds so pending task count should not be lower than 3 if parallelism does not work.
     */
    @Test(timeout=30000)
    public void testRunBasicAsync() {
        ExecutorService executorService = getExecutorService();
        final Set<String> threadExecutingJobs = new CopyOnWriteArraySet<>();
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(4) {

            @Override
            public void afterJobExecuted(AsynchronousJobEvent event) {
                threadExecutingJobs.add(Thread.currentThread().getName());
                super.afterJobExecuted(event);
            }
            
        };
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        KieSession ks = createKSession(PARENT, SUBPROCESS);
        ks.getWorkItemManager().registerWorkItemHandler("async", new AsyncWorkItemHandler(executorService,
                "org.jbpm.test.command.LongRunningCommand"));

        List<String> exceptions = new ArrayList<String>();
        exceptions.add("ADDRESS_EXCEPTION");
        exceptions.add("ID_EXCEPTION");
        exceptions.add("PHONE_EXCEPTION");

        Map<String, Object> pm = new HashMap<String, Object>();
        pm.put("exceptions", exceptions);

        ProcessInstance pi = ks.startProcess(PARENT_ID, pm);

        // assert that the main process was completed as tasks are executed asynchronously
        assertProcessInstanceCompleted(pi.getId());

        countDownListener.waitTillCompleted();

        // assert that all jobs have where completed.
        Assertions.assertThat(executorService.getCompletedRequests(new QueryContext()))
                .as("All async jobs should have been executed").hasSize(4);
        Assertions.assertThat(threadExecutingJobs)
        .as("There should be 4 distinct threads as jobs where executed in parallel").hasSize(4);
    }
    
    @Override
    protected PoolingDataSourceWrapper setupPoolingDataSource() {
        Properties dsProps = PersistenceUtil.getDatasourceProperties();
        dsProps.setProperty("POOL_CONNECTIONS", "false");

        // Setup the datasource
        PoolingDataSourceWrapper ds1 = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds");
        return ds1;
    }

}
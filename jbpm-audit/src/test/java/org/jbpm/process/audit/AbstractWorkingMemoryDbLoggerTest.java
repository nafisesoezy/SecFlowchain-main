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

package org.jbpm.process.audit;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.test.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.test.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.test.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the following classes:
 * <ul>
 * <li>WorkingMemoryDbLogger</li>
 * </ul>
 */
public abstract class AbstractWorkingMemoryDbLoggerTest extends AbstractBaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractWorkingMemoryDbLoggerTest.class);
    protected HashMap<String, Object> context;
    
    protected AuditLogService logService;

    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, context.get(EnvironmentName.ENTITY_MANAGER_FACTORY));
        logService = new JPAAuditLogService(env);
    }

    @After
    public void tearDown() throws Exception {
        javax.transaction.TransactionManager txm = com.arjuna.ats.jta.TransactionManager.transactionManager();
        assertTrue("There is still a transaction running!", txm.getTransaction() == null );
        
        cleanUp(context);
        logService.dispose();
    }
   
    protected static KieBase createKnowledgeBase() {
        // create a builder
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        // load the process
        Reader source = new InputStreamReader(AbstractWorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(AbstractWorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow2.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(AbstractWorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow3.rf"));
        builder.addProcessFromXml(source);
        // create the knowledge base 
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Arrays.asList(builder.getPackages()));
        return kbase;
    }

    public abstract ProcessInstance startProcess(String processName);
    
    @Test
	public void testLogger1() {
        // start process instance
        long processInstanceId = startProcess("com.sample.ruleflow").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances =
        	logService.findProcessInstances("com.sample.ruleflow");
        assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        logger.debug("{}", processInstance);
        assertNotNull(processInstance.getStart());
        assertNotNull(processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstanceId);
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug("{}", nodeInstance);
            assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        for (int i = 1; i < 4; i = i + 2) {
            assertTrue(nodeInstances.get(i).getConnection().equals(nodeInstances.get(i + 1).getNodeId()));
        }
        for (int i = 2; i < 6; i = i + 2) {
            assertTrue(nodeInstances.get(i).getConnection().equals(nodeInstances.get(i - 1).getNodeId()));
        }
        assertRuleFlowSampleLogsSorting(nodeInstances, processInstanceId);
        logService.clear();
	}

    @Test
	public void testLogger2() {
        // start process instance
        startProcess("com.sample.ruleflow");
        startProcess("com.sample.ruleflow");
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances =
        	logService.findProcessInstances("com.sample.ruleflow");
        assertEquals(2, processInstances.size());
        for (ProcessInstanceLog processInstance: processInstances) {
            logger.debug("{}", processInstance);
            logger.debug(" -> {} - {}", processInstance.getStart(), processInstance.getEnd());
            List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getProcessInstanceId());
            for (NodeInstanceLog nodeInstance: nodeInstances) {
                logger.debug("{}", nodeInstance);
                logger.debug(" -> {}", nodeInstance.getDate());
            }
            assertEquals(6, nodeInstances.size());
            assertRuleFlowSampleLogsSorting(nodeInstances, processInstance.getProcessInstanceId());
        }
        logService.clear();
	}

    @Test
	public void testLogger3() {
        long processInstanceId = startProcess("com.sample.ruleflow2").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow2'");
        List<ProcessInstanceLog> processInstances =
        	logService.findProcessInstances("com.sample.ruleflow2");
        assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        logger.debug("{}", processInstance);
        logger.debug(" -> {} - {} ", processInstance.getStart(), processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull(processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstanceId);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug("{}", nodeInstance);
            logger.debug(" -> {}", nodeInstance.getDate());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        assertEquals(14, nodeInstances.size());
        logService.clear();
	}

	private void assertRuleFlowSampleLogsSorting(List<NodeInstanceLog> nodeInstanceLogs, long processInstanceId){
        assertLogContent(nodeInstanceLogs.get(0),
                         processInstanceId,
                         "com.sample.ruleflow",
                         0,
                         "StartNode",
                         "Start");
        assertLogContent(nodeInstanceLogs.get(1),
                         processInstanceId,
                         "com.sample.ruleflow",
                         1,
                         "StartNode",
                         "Start");
        assertLogContent(nodeInstanceLogs.get(2),
                         processInstanceId,
                         "com.sample.ruleflow",
                         0,
                         "WorkItemNode",
                         "Task");
        assertLogContent(nodeInstanceLogs.get(3),
                         processInstanceId,
                         "com.sample.ruleflow",
                         1,
                         "WorkItemNode",
                         "Task");
        assertLogContent(nodeInstanceLogs.get(4),
                         processInstanceId,
                         "com.sample.ruleflow",
                         0,
                         "EndNode",
                         "End");
        assertLogContent(nodeInstanceLogs.get(5),
                         processInstanceId,
                         "com.sample.ruleflow",
                         1,
                         "EndNode",
                         "End");
    }

    private void assertLogContent(NodeInstanceLog log,
                                  long nodeInstanceId,
                                  String processId,
                                  Integer type,
                                  String nodeType,
                                  String nodeName){

        assertEquals(nodeInstanceId,log.getProcessInstanceId().longValue());
        assertEquals(processId,log.getProcessId());
        assertEquals(type,log.getType());
        assertEquals(nodeType, log.getNodeType());
        assertEquals(nodeName,log.getNodeName());
    }
}

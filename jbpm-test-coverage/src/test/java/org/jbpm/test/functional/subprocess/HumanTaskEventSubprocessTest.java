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

package org.jbpm.test.functional.subprocess;

import java.util.List;

import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class HumanTaskEventSubprocessTest extends JbpmTestCase {
	
	private static final Logger logger = LoggerFactory.getLogger(HumanTaskEventSubprocessTest.class);
	
	public HumanTaskEventSubprocessTest() {
		super(true, true);
	}

	@Test
    public void testSignalProcess() {
	    createRuntimeManager("org/jbpm/test/functional/subprocess/HumanTaskEventSubprocess.bpmn2");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();
	    TaskService taskService = runtimeEngine.getTaskService();

        ProcessInstance processInstance = ksession.startProcess("org.jbpm.test.functional.subprocess.HumanTaskEventSubprocess");
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        
        ksession.signalEvent("again", "", processInstance.getId());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(2, tasks.size());
    }
}

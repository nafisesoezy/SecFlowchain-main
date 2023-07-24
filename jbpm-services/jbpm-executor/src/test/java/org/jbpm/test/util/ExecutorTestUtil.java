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

package org.jbpm.test.util;

import java.io.File;
import java.util.Properties;

import org.jbpm.test.persistence.util.PersistenceUtil;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorTestUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutorTestUtil.class);

    public static PoolingDataSourceWrapper setupPoolingDataSource() {
        return PersistenceUtil.setupPoolingDataSource("jdbc/jbpm-ds");
    }

    /**
     * This reads in the (maven filtered) datasource properties from the test
     * resource directory.
     * 
     * @return Properties containing the datasource properties.
     */
    public static Properties getDatasourceProperties() {
        return PersistenceUtil.getDatasourceProperties();
    }
    
    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            
            String[] jbpmSerFiles = tempDir.list((dir, name) -> name.endsWith("-jbpmSessionId.ser"));
            for (String file : jbpmSerFiles) {
                logger.debug("Temp dir to be removed {} file {}",tempDir, file);
                new File(tempDir, file).delete();
            }
        }
    }

}

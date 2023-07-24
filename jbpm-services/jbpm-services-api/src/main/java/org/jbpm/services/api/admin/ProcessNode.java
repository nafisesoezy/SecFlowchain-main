/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.api.admin;

import java.io.Serializable;

/**
 * Describes process node taken from process definition.
 */
public interface ProcessNode extends Serializable {

    /**
     * Returns name of the node
	 * @return node name
     */
    String getNodeName();

    /**
     * Returns unique id of the node
	 * @return node id
     */
	long getNodeId();	
	
	/**
     * Returns type of the node
	 * @return node type
     */
	String getNodeType();
	
	/**
	 * Returns process id node belongs to
	 * @return process id
	 */
	String getProcessId();	
}

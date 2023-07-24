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

package org.jbpm.services.api.model;

public interface TimerDesc {

    /**
     * Returns the timer description id.
     * @return id
     */
    Long getId();

    /**
     * Returns the timer description node id.
     * @return node id
     */
    Long getNodeId();

    /**
     * Returns the timer description node name.
     * @return node name
     */
    String getNodeName();

    /**
     * Returns the timer description unique id.
     * @return unique id
     */
    String getUniqueId();
}

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
import java.util.Date;

/**
 * Describes timer instance that is active with
 * details about the timer and the owner (such as process instance).
 */
public interface TimerInstance extends Serializable {

    /**
     * Returns name of the timer - taken from timer node in the process
     * @return timer name
     */
    String getTimerName();

    /**
     * Returns id of the timer
     * @return timer id
     */
    long getId();

    /**
     * Returns unique id of the timer
     * @return timer unique id
     */
    long getTimerId();

    /**
     * Returns date when the timer was activated/created
     * @return timer activation/creation date
     */
    Date getActivationTime();

    /**
     * Returns date when the timer was fired last time, can be null
     * @return timer last fire time
     */
    Date getLastFireTime();

    /**
     * Returns next date when the timer is expected to fire
     * @return timer next fire time
     */
    Date getNextFireTime();

    /**
     * Returns delay that comes from definition
     * @return timer delay
     */
    long getDelay();

    /**
     * Returns period that comes from definition - only for repeatable timers
     * @return timer period
     */
    long getPeriod();

    /**
     * Returns repeat limit that comes from definition - only for repeatable timers
     * @return timer repeat limit
     */
    int getRepeatLimit();

    /**
     * Returns process instance id that timer belongs to
     * @return process instance id
     */
    long getProcessInstanceId();

    /**
     * Returns session id that was used to create the timer
     * @return session id
     */
    long getSessionId();
}

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

package org.jbpm.kie.services.impl.query.preprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_ORGANIZATIONAL_ENTITY;

public class BusinessAdminTasksPreprocessor extends UserTasksPreprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessAdminTasksPreprocessor.class);

    public static final String ADMIN_USER = System.getProperty("org.jbpm.ht.admin.user",
                                                               "Administrator");
    public static final String ADMIN_GROUP = System.getProperty("org.jbpm.ht.admin.group",
                                                                "Administrators");

    private IdentityProvider identityProvider;

    private UserGroupCallback userGroupCallback;

    public BusinessAdminTasksPreprocessor(IdentityProvider identityProvider,
                                          UserGroupCallback userGroupCallback,
                                          DataSetMetadata metadata) {
        super(metadata);
        this.identityProvider = identityProvider;
        this.userGroupCallback = userGroupCallback;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void preprocess(DataSetLookup lookup) {
        if (identityProvider == null || userGroupCallback == null) {
            return;
        }

        if (ADMIN_USER.equals(identityProvider.getName())) {
            return;
        }

        final List<String> groups = Optional.ofNullable(userGroupCallback.getGroupsForUser(identityProvider.getName())).orElse(new ArrayList<>());
        if (groups.stream().filter(s -> s.equals(ADMIN_GROUP)).findFirst().isPresent()) {
            return;
        }

        final List<Comparable> orgEntities = new ArrayList<>(groups);
        orgEntities.add(identityProvider.getName());
        final ColumnFilter columnFilter = equalsTo(COLUMN_ORGANIZATIONAL_ENTITY,
                                                   orgEntities);
        LOGGER.debug("Adding column filter: {}",
                     columnFilter);

        if (lookup.getFirstFilterOp() != null) {
            lookup.getFirstFilterOp().addFilterColumn(columnFilter);
        } else {
            DataSetFilter filter = new DataSetFilter();
            filter.addFilterColumn(columnFilter);
            lookup.addOperation(filter);
        }

        super.preprocess(lookup);
    }
}

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
package org.jbpm.document.service.impl;

import java.util.Date;

import java.io.File;
import org.assertj.core.api.Assertions;
import org.jbpm.document.Document;
import org.junit.Assert;
import org.junit.Test;
import java.nio.file.Files;

public class DocumentImplTest {

    private static final String ID = "1234567890";
    private static final String FILENAME = "Document_Name";
    private static final String EXTENSION = ".txt";
    private static final String NAME = FILENAME + EXTENSION;
    private static final Long SIZE = 1024l;
    private static final Date LAST_MODIFIED = new Date();
    private static final String LINK = "downloadLink";
    private static final String CONTENT = "test content";

    @Test
    public void testDefaultConstructor() {
        Document document = new DocumentImpl();

        Assertions.assertThat(document.getIdentifier()).isNotNull();
    }

    @Test
    public void testConstructorWithoutIdentifier() {
        Document document = new DocumentImpl(NAME,
                                             SIZE,
                                             LAST_MODIFIED);

        Assertions.assertThat(document.getIdentifier()).isNotNull();
        Assertions.assertThat(document.getName()).isNotNull().isEqualTo(NAME);
        Assertions.assertThat(document.getSize()).isEqualTo(SIZE);
        Assertions.assertThat(document.getLastModified()).isEqualTo(LAST_MODIFIED);
    }

    @Test
    public void testConstructorWithIdentifier() {
        Document document = new DocumentImpl(ID,
                                             NAME,
                                             SIZE,
                                             LAST_MODIFIED);

        Assertions.assertThat(document.getIdentifier()).isNotNull().isEqualTo(ID);
        Assertions.assertThat(document.getName()).isNotNull().isEqualTo(NAME);
        Assertions.assertThat(document.getSize()).isEqualTo(SIZE);
        Assertions.assertThat(document.getLastModified()).isEqualTo(LAST_MODIFIED);
    }

    @Test
    public void testFullConstructor() {
        Document document = new DocumentImpl(ID,
                                             NAME,
                                             SIZE,
                                             LAST_MODIFIED,
                                             LINK);

        Assertions.assertThat(document.getIdentifier()).isNotNull().isEqualTo(ID);
        Assertions.assertThat(document.getName()).isNotNull().isEqualTo(NAME);
        Assertions.assertThat(document.getSize()).isEqualTo(SIZE);
        Assertions.assertThat(document.getLastModified()).isEqualTo(LAST_MODIFIED);
        Assertions.assertThat(document.getLink()).isNotNull().isNotEmpty().isEqualTo(LINK);
    }

    @Test
    public void testToStringRepresentation() {
        Document document = new DocumentImpl();
        try {
            Assert.assertNotNull(document.toString());
        } catch (Throwable th) {
            Assert.fail("toString method must not fire any exception: " + th.getMessage());
        }
    }

    @Test
    public void testToFile() throws Exception {
        Document document = new DocumentImpl(ID,
                                             NAME,
                                             SIZE,
                                             LAST_MODIFIED,
                                             LINK);
        document.setContent(CONTENT.getBytes());

        File file = document.toFile();
        Assert.assertNotNull(file);
        Assert.assertNotNull(file.getName());
        Assert.assertTrue(file.getName().contains(FILENAME));
        Assert.assertTrue(file.getName().contains(EXTENSION));
        String fileContent = new String(Files.readAllBytes(file.toPath()));
        Assert.assertNotNull(fileContent);
        Assert.assertEquals(fileContent, CONTENT);
    }
}

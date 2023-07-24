/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.cmmn.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.casemgmt.cmmn.core.Definitions;
import org.jbpm.casemgmt.cmmn.core.FileItemDefinition;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessElementHandler extends BaseAbstractHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessElementHandler.class);

    public ProcessElementHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<>();
            this.validParents.add(Definitions.class);
            this.validPeers = new HashSet<>();
            this.validPeers.add(null);
            this.validPeers.add(RuleFlowProcess.class);
            this.validPeers.add(FileItemDefinition.class);
            this.allowNesting = false;
        }
    }

    @SuppressWarnings("unchecked")
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);

        String id = attrs.getValue("id");
        String externalRef = attrs.getValue("externalRef");
        
        if (externalRef.contains(":")) {
            externalRef = externalRef.split(":")[1]; 
        }

        logger.debug("Found process reference with id {} and external ref {}", id, externalRef);

        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, String> processes = (Map<String, String>) buildData.getMetaData("ProcessElements");
        if (processes == null) {
            processes = new HashMap<String, String>();
            buildData.setMetaData("ProcessElements", processes);
        }        
        processes.put(id, externalRef);
        return null;
    }

    @Override
    public Object end(String uri, String localName, ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    @Override
    public Class<?> generateNodeFor() {
        return null;
    }

}

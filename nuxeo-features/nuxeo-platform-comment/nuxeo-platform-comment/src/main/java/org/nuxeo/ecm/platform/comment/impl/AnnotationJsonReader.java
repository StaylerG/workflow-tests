/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     Funsho David
 */

package org.nuxeo.ecm.platform.comment.impl;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;
import static org.nuxeo.ecm.platform.comment.api.AnnotationConstants.ANNOTATION_XPATH;
import static org.nuxeo.ecm.platform.comment.impl.AnnotationJsonWriter.ENTITY_TYPE;
import static org.nuxeo.ecm.platform.comment.impl.CommentJsonReader.fillCommentEntity;
import static org.nuxeo.ecm.platform.comment.impl.CommentJsonReader.setIfExist;

import org.nuxeo.ecm.core.io.marshallers.json.EntityJsonReader;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.platform.comment.api.Annotation;
import org.nuxeo.ecm.platform.comment.api.AnnotationImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @since 10.1
 */
@Setup(mode = SINGLETON, priority = REFERENCE)
public class AnnotationJsonReader extends EntityJsonReader<Annotation> {

    public AnnotationJsonReader() {
        super(ENTITY_TYPE);
    }

    @Override
    protected Annotation readEntity(JsonNode jn) {
        Annotation annotation = new AnnotationImpl();
        fillCommentEntity(jn, annotation);
        setIfExist(jn, ANNOTATION_XPATH, annotation::setXpath);
        return annotation;
    }

}

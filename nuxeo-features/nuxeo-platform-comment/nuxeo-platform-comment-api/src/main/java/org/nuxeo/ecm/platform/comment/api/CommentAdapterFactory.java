/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      Kevin Leturc <kleturc@nuxeo.com>
 */

package org.nuxeo.ecm.platform.comment.api;

import static org.nuxeo.ecm.platform.comment.api.AnnotationConstants.ANNOTATION_SCHEMA;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_SCHEMA;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

/**
 * @since 11.1
 */
public class CommentAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Comment getAdapter(DocumentModel doc, Class<?> itf) {
        if (doc.getDocumentType().hasSchema(ANNOTATION_SCHEMA)) {
            return new AnnotationImpl(doc);
        } else if (doc.getDocumentType().hasSchema(COMMENT_SCHEMA)) {
            return new CommentImpl(doc);
        }
        return null;
    }
}

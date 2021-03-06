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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.nuxeo.ecm.platform.comment.api.ExternalEntityConstants.EXTERNAL_ENTITY_FACET;
import static org.nuxeo.ecm.platform.comment.api.ExternalEntityConstants.EXTERNAL_ENTITY_ID_PROPERTY;
import static org.nuxeo.ecm.platform.comment.api.ExternalEntityConstants.EXTERNAL_ENTITY_ORIGIN_PROPERTY;
import static org.nuxeo.ecm.platform.comment.api.ExternalEntityConstants.EXTERNAL_ENTITY_PROPERTY;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_ANCESTOR_IDS;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_AUTHOR;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_CREATION_DATE;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_DOC_TYPE;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_MODIFICATION_DATE;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_PARENT_ID;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_TEXT;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @since 11.1
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(cleanup = Granularity.CLASS)
@Deploy("org.nuxeo.ecm.platform.comment.api:OSGI-INF/comment-adapter-contrib.xml")
@Deploy("org.nuxeo.ecm.platform.comment:OSGI-INF/comment-schemas-contrib.xml")
public class TestCommentAdapter {

    @Inject
    protected CoreSession session;

    @Test
    public void testGetAdapter() {
        DocumentModel docModel = session.createDocumentModel(COMMENT_DOC_TYPE);
        docModel = session.createDocument(docModel); // we need to create document because we detach during getAdapter
        docModel.setPropertyValue(COMMENT_PARENT_ID, "parentId");
        docModel.setPropertyValue(COMMENT_ANCESTOR_IDS, new ArrayList<>(Arrays.asList("id1", "id2")));
        docModel.setPropertyValue(COMMENT_AUTHOR, "author");
        docModel.setPropertyValue(COMMENT_TEXT, "I am a comment!");
        Instant creationDate = Instant.parse("2020-04-27T07:33:07.00Z");
        docModel.setPropertyValue(COMMENT_CREATION_DATE, toCalendar(creationDate));
        Instant modificationDate = Instant.parse("2020-04-27T07:38:37.00Z");
        docModel.setPropertyValue(COMMENT_MODIFICATION_DATE, toCalendar(modificationDate));
        docModel.addFacet(EXTERNAL_ENTITY_FACET);
        docModel.setPropertyValue(EXTERNAL_ENTITY_ID_PROPERTY, "entityId");
        docModel.setPropertyValue(EXTERNAL_ENTITY_ORIGIN_PROPERTY, "External");
        docModel.setPropertyValue(EXTERNAL_ENTITY_PROPERTY, "<entity/>");

        Comment comment = docModel.getAdapter(Comment.class);
        assertNotNull(comment.getId());
        assertEquals("parentId", comment.getParentId());
        assertEquals(Arrays.asList("id1", "id2"), comment.getAncestorIds());
        assertEquals("author", comment.getAuthor());
        assertEquals("I am a comment!", comment.getText());
        assertEquals(creationDate, comment.getCreationDate());
        assertEquals(modificationDate, comment.getModificationDate());
        ExternalEntity external = (ExternalEntity) comment;
        assertEquals("entityId", external.getEntityId());
        assertEquals("External", external.getOrigin());
        assertEquals("<entity/>", external.getEntity());
    }

    @Test
    public void testGetDocument() {
        CommentImpl comment = new CommentImpl();
        comment.setParentId("parentId");
        comment.setAuthor("author");
        comment.setText("I am a comment!");
        Instant creationDate = Instant.parse("2020-04-27T07:33:07.00Z");
        comment.setCreationDate(creationDate);
        Instant modificationDate = Instant.parse("2020-04-27T07:38:37.00Z");
        comment.setModificationDate(modificationDate);

        // id and ancestor ids are filled by services
        DocumentModel docModel = comment.getDocument();
        try {
            // SimpleDocumentModel throw UnsupportedOperationException, see CommentImpl#getId
            docModel.getId();
            fail("We should not be able to get document id");
        } catch (UnsupportedOperationException e) {
            // ok
        }
        assertEquals("parentId", docModel.getPropertyValue(COMMENT_PARENT_ID));
        assertNotNull(docModel.getPropertyValue(COMMENT_ANCESTOR_IDS));
        assertTrue(((Collection<?>) docModel.getPropertyValue(COMMENT_ANCESTOR_IDS)).isEmpty());
    }

    public static Calendar toCalendar(Instant instant) {
        if (instant == null) {
            return null;
        }
        // an Instant is on UTC by definition
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        return GregorianCalendar.from(zdt);
    }

}

package com.swagata.helloektorp.dao;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.impl.NameConventions;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.StdDesignDocumentFactory;
import org.ektorp.support.View;

import com.google.common.base.Optional;
import com.swagata.helloektorp.pojo.SamplePojo;

/**
 * This is how our DAOs look like
 * 
 * @author swagataacharyya
 * 
 */

@View(name = "all", map = "function(doc) { if (doc.type === 'SamplePojo') emit(null, doc._id)}")
public class SamplePojoDao {

    String designDocName;
    CouchDbConnector connector;

    public SamplePojoDao(CouchDbConnector db) {
        this.connector = db;
    }

    // TODO This method always returns an empty array
    public List<SamplePojo> getAll() {
        return connector.queryView(createQuery("all"), SamplePojo.class);
    }

    protected ViewQuery createQuery(final String viewName) {
        ViewQuery viewQuery = new ViewQuery().designDocId(designDocName).viewName(viewName);
        return viewQuery.includeDocs(true);
    }

    public void initStandardDesignDocument() {
        designDocName = NameConventions.designDocName(SamplePojo.class);
        DesignDocument standard = connector.find(DesignDocument.class, designDocName);
        boolean flag = false;
        if (null == standard) {
            flag = true;
            standard = new DesignDocument(designDocName);
        }
        final DesignDocument generated = new StdDesignDocumentFactory().generateFrom(this);
        final boolean changed = standard.mergeWith(generated);
        if (changed) {
            connector.update(standard);
        } else if (flag) {
            connector.create(standard);
        }
    }

    public <T> void createOrUpdate(final T transaction) {
        ObjectNode serializedTransaction = serializeDocument(transaction);
        try {
            connector.create(serializedTransaction);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    protected final <T> ObjectNode serializeDocument(final T doc) {
        ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode serializedDocument = objectMapper.valueToTree(doc);
        serializedDocument.put("_id", calculateId(doc));
        serializedDocument.put("type", SamplePojo.class.getSimpleName());
        return serializedDocument;
    }

    public <T> String calculateId(final T doc) {
        return buildId("SamplePojo", ((SamplePojo) doc).getName());
    }

    protected String buildId(final String documentType, final String components) {
        return documentType + ":" + components;
    }

    // This method is able to fetch the particular record based on identifier.
    public Optional find(final String identifier) {
        return Optional.fromNullable(connector.find(SamplePojo.class, identifier));
    }
}

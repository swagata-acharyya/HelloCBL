
package com.swagata.helloektorp;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.impl.NameConventions;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.StdDesignDocumentFactory;
import org.ektorp.support.View;

import android.util.Log;

@View(name = "all", map = "function(doc) { if (doc.type === 'SamplePojo') emit(null, doc._id)}")
public class SamplePojoDao {

    String designDocName;
    CouchDbConnector connector;

    public SamplePojoDao(CouchDbConnector db) {
        this.connector = db;
    }

    public List<SamplePojo> getAll() {
        return connector.queryView(createQuery("all"), SamplePojo.class);
    }

    protected ViewQuery createQuery(final String viewName) {
        ViewQuery viewQuery = new ViewQuery().designDocId(designDocName).viewName(viewName);
        return viewQuery.includeDocs(true);
    }

    public void initStandardDesignDocument() {
        designDocName = NameConventions.designDocName(SamplePojo.class);
        Log.d("DESIGNDOCNAME", designDocName);
        DesignDocument standard = connector.find(DesignDocument.class, designDocName);
        Log.d("DESIGNDOCNAME", standard + "");
        boolean flag = false;
        if (null == standard) {
            flag = true;
            standard = new DesignDocument(designDocName);
        }
        Log.d("DESIGNDOCNAME", standard + "");
        final DesignDocument generated = new StdDesignDocumentFactory().generateFrom(this);
        Log.d("DESIGNDOCNAME", generated + "");
        final boolean changed = standard.mergeWith(generated);
        Log.d("DESIGNDOCNAME", changed + "");
        if (changed) {
            connector.update(standard);
        } else if (!flag) {
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
}

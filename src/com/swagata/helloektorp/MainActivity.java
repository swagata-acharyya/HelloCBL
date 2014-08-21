package com.swagata.helloektorp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.couchbase.lite.Manager;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.javascript.JavaScriptViewCompiler;
import com.couchbase.lite.router.URLStreamHandlerFactory;
import com.swagata.helloektorp.dao.SamplePojoDao;
import com.swagata.helloektorp.ektorp.CBLiteHttpClient;
import com.swagata.helloektorp.pojo.SamplePojo;

/**
 * All major actions happens here.
 * 
 * @author swagataacharyya
 * 
 */
public class MainActivity extends Activity {

    Manager manager = null;
    public static CouchDbConnector db;
    final String name = "hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        URLStreamHandlerFactory.registerSelfIgnoreError();
        View.setCompiler(new JavaScriptViewCompiler());
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            startEktorp();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private CouchDbInstance couchDbInstance;

    private boolean dbExists(CouchDbInstance couchDbInstance, String dbPath) {
        if (couchDbInstance == null) {
            return false;
        }
        if (dbPath.isEmpty()) {
            return false;
        }
        List<String> dbNames = couchDbInstance.getAllDatabases();
        if (dbNames == null || dbNames.isEmpty()) {
            return false;
        }
        for (String dbName : dbNames) {
            if (dbPath.equals(dbName)) {
                return true;
            }
        }
        return false;
    }

    private void startEktorp() throws MalformedURLException, InterruptedException {
        final HttpClient couchClient = new CBLiteHttpClient(manager);
        couchDbInstance = new StdCouchDbInstance(couchClient);
        new EktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                final boolean newDatabase = !dbExists(couchDbInstance, name);

                if (!name.startsWith("_replicator")) {

                    if (newDatabase) {
                        couchDbInstance.createDatabase(name);
                    }
                    db = couchDbInstance.createConnector(name, false);
                }
            }

            @Override
            protected void onDbAccessException(final DbAccessException dbAccessException) {
                super.onDbAccessException(dbAccessException);
            }

            protected void onPostExecute(Object result) {
                final SamplePojoDao dao = new SamplePojoDao(db);
                dao.initStandardDesignDocument();
                // Running the app twice will create issues. Revisions are not
                // handled in this project yet.
                SamplePojo pojo = createOnePojo();
                Log.d("POJOIS", pojo.toString());
                dao.createOrUpdate(pojo);

                // Trying to read the data form the db. Put it in a Thread to
                // check whether it takes time to get created in the DB and then
                // is displayed. However, it seems dao.find(_id) always works
                // and dao.getAll() always returns empty array.
                final Thread t = new Thread() {
                    public void run() {
                        while (true) {
//                            Log.d("CHECK", dao.find("SamplePojo:SecondPojo") + "");
                            Log.d("CHECK", dao.getAll()+ "");
                            try {
                                sleep(5000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    };
                };
                t.start();
            }

            private SamplePojo createOnePojo() {
                SamplePojo pojo = new SamplePojo();
                pojo.setId("P1");
                pojo.setName("SecondPojo");
                return pojo;
            };
        }.execute();

    }
}

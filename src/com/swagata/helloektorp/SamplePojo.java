
package com.swagata.helloektorp;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonSetter;

public class SamplePojo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String revision;
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the revision
     */
    String getRevision() {
        return revision;
    }

    /**
     * @param revision
     *            the revision to set
     */
    @JsonSetter("_rev")
    void setRevision(String revision) {
        this.revision = revision;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SamplePojo [id=" + id + ", name=" + name + "]";
    }

}

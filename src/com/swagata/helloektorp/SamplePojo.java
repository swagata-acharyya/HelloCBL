
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
    private String type;
    private String _id;

    /**
     * @return the _id
     */
    String get_id() {
        return _id;
    }

    /**
     * @param _id
     *            the _id to set
     */
    void set_id(String _id) {
        this._id = _id;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the type
     */
    String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    void setType(String type) {
        this.type = type;
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

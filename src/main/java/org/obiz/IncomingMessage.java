package org.obiz;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test_data_200k")
public class IncomingMessage extends PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "descr", nullable = false)
    private String descr;

    @Column(name = "processed")
    private Boolean processed;

    @Override
    public String toString() {
        return "IncomingMessage{" +
                "id=" + id +
                ", descr='" + descr + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
}

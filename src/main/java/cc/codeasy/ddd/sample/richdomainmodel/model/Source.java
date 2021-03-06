package cc.codeasy.ddd.sample.richdomainmodel.model;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Source {
    public static final Integer TYPE_ORDER = 1;
    private String id;
    private Integer type;

    protected Source(){}

    public Source(String id, Integer type) {
        if(id == null || "".equals(id))
            throw new IllegalArgumentException("source id should not be empty.");
        if(type == null)
            throw new IllegalArgumentException("source type should not be null.");
        this.id = id;
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Source{" +
                "id='" + id + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return id.equals(source.id) &&
                type.equals(source.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}

package cc.codeasy.ddd.sample.richdomainmodel.model;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("2")
public class ConsumeRecord extends AccountRecord {
    protected ConsumeRecord(){super();}

    public ConsumeRecord( Account account, Amount amount, Source source) {
        super(account, amount,  source);
    }
}

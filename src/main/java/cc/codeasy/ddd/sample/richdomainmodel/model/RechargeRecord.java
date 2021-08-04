package cc.codeasy.ddd.sample.richdomainmodel.model;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class RechargeRecord extends AccountRecord {
    protected RechargeRecord(){super();}

    public RechargeRecord(Account account, Amount amount,  Source source) {
        super(account, amount, source);
    }
}

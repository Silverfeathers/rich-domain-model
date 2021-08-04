package cc.codeasy.ddd.sample.richdomainmodel.model;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance"))
    })
    private Amount balance;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "owner_id"))
    })
    private Owner owner;


    // https://thoughts-on-java.org/hibernate-tips-how-to-avoid-hibernates-multiplebagfetchexception/
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private Set<RechargeRecord> rechargeRecords = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private Set<ConsumeRecord> consumeRecords = new HashSet<>();


    @CreatedDate
    @Temporal(TIMESTAMP)
    protected Date created;

    @Version
    private Integer version;


    protected Account(){}

    public Account(Owner owner, Amount balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public void recharge(Amount amount, Source source) {

        Optional<RechargeRecord> exists = this.rechargeRecords.stream().filter(r -> r.getSource().equals(source)).findFirst();
        if(exists.isPresent()) {
            throw new RechargeDuplicateException(this, source);
        }

        this.balance = this.balance.add(amount);
        RechargeRecord record = new RechargeRecord(this, amount, source);
        this.rechargeRecords.add(record);
    }

    public void consume(Amount amount,  Source source) {
        this.balance = this.balance.subtract(amount);
        ConsumeRecord record = new  ConsumeRecord(this, amount, source);
        this.consumeRecords.add(record);
    }

    public Long getId() {
        return id;
    }

    public Owner getOwner() {
        return owner;
    }

    public Amount getBalance() {
        return balance;
    }


    public Date getCreated() {
        return created;
    }

    public Integer getVersion() {
        return version;
    }

    public Set<RechargeRecord> getRechargeRecords() {
        return Collections.unmodifiableSet(rechargeRecords);
    }

    public Set<ConsumeRecord> getConsumeRecords() {
        return Collections.unmodifiableSet(consumeRecords);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", balance=" + balance +
                ", created=" + created +
                ", version=" + version +
                '}';
    }

    //仅为了测试
    protected void addRechargeRecord(RechargeRecord record) {
        this.rechargeRecords.add(record);
    }
}

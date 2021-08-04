package cc.codeasy.ddd.sample.richdomainmodel.model;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

// https://wiki.eclipse.org/EclipseLink/Examples/JPA/Inheritance
// https://www.javatpoint.com/jpa-inheritance-overview
// https://www.javatpoint.com/jpa-single-table-strategy

@MappedSuperclass
@Table(name="ACCOUNT_RECORD")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.STRING)
public abstract class AccountRecord {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Amount amount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "source_id")),
            @AttributeOverride(name = "type", column = @Column(name = "source_type"))
    })
    private Source source;

    @CreatedDate
    @Temporal(TIMESTAMP)
    protected Date created;

    protected AccountRecord(){}

    public AccountRecord(Account account, Amount amount,  Source source) {
        this.amount = amount;
        this.account = account;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public Amount getAmount() {
        return amount;
    }

    public Account getAccount() {
        return account;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" +
                "id=" + this. getId()+
                ", account=" + getAccount() +
                ", amount=" + getAmount() +
                ", source=" + getSource() +
                ", created=" + created +
                '}';
    }
}

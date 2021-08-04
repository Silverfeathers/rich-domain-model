package cc.codeasy.ddd.sample.richdomainmodel.model;

public class RechargeDuplicateException extends RuntimeException {
    private Account account;
    private Source source;

    public RechargeDuplicateException(Account account, Source source) {
        super(account.getOwner()+" recharge duplicate, source bill is : " + source);
        this.account = account;
        this.source = source;
    }
}

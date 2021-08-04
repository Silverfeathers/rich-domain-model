package cc.codeasy.ddd.sample.richdomainmodel.service;

import cc.codeasy.ddd.sample.richdomainmodel.model.*;
import cc.codeasy.ddd.sample.richdomainmodel.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.OptimisticLockException;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private TransactionTemplate transactionTemplate;

    public AccountService(AccountRepository accountRepository, TransactionTemplate transactionTemplate) {
        this.accountRepository = accountRepository;
        this.transactionTemplate = transactionTemplate;
    }


    public RechargeResponse recharge(RechargeRequest request) {
        final RechargeResponse.Builder respBuilder = new RechargeResponse.Builder(request);

        //校验入参
        if (request.getAmount() <= 0) {
            return respBuilder.error(RechargeResponse.AMOUNT_LESS_THAN_ZERO).build();
        }
        //TODO 校验更多的参数

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    Account account = accountRepository.findByOwner(new Owner(request.getUserId()));
                    Source source = new Source(request.getSourceId(), request.getSourceType());
                    account.recharge(new Amount(request.getAmount()), source);
                    accountRepository.save(account);
                } catch (RechargeDuplicateException e) {
                    respBuilder.error(RechargeResponse.DUPLICATE_RECHARGE).errorMsg(e.getMessage());
                    status.setRollbackOnly();
                }
                catch (OptimisticLockException e) {
                    respBuilder.error(RechargeResponse.VERSION_CONFLICT).errorMsg(e.getMessage());
                    status.setRollbackOnly();
                } catch (Exception e) {
                    respBuilder.error(RechargeResponse.UNKNOWN).errorMsg(e.getMessage());
                    status.setRollbackOnly();
                }
            }
        });

        return respBuilder.build();
    }


    public static class RechargeRequest {


        private int amount;
        private String sourceId;
        private Integer sourceType;
        private String userId;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }


        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public Integer getSourceType() {
            return sourceType;
        }

        public void setSourceType(Integer sourceType) {
            this.sourceType = sourceType;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class RechargeResponse {

        public static final int AMOUNT_LESS_THAN_ZERO = 2;
        public static final int UNKNOWN = 3;
        public static final int VERSION_CONFLICT = 4;
        public static final int DUPLICATE_RECHARGE = 5;


        private boolean success = true;

        private int errorCode;
        private String message;

        public RechargeResponse(boolean success, int errorCode, String message) {
            this.success = success;
            this.errorCode = errorCode;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }


        @Override
        public String toString() {
            return "RechargeResponse{" +
                    "success=" + success +
                    ", errorCode=" + errorCode +
                    ", message='" + message + '\'' +
                    '}';
        }

        public static class Builder {

            private final RechargeRequest request;
            private boolean success = true;
            private int errorCode;
            private String message;

            public Builder(RechargeRequest request) {
                this.request = request;
            }

            public Builder error(int code) {
                this.success = false;
                this.errorCode = code;
                return this;
            }
            public Builder errorMsg(String message) {
                this.message = message;
                return this;
            }

            public RechargeResponse build() {
                return new RechargeResponse(success, errorCode, message);
            }


        }

    }

}

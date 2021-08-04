package cc.codeasy.ddd.sample.richdomainmodel.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

//https://www.baeldung.com/introduction-to-assertj

class AccountTest {

    @Nested
    @DisplayName("Tom的账户里有100积分")
    class RechargeAndConsumeTest {

        Account account;

        @BeforeEach
        void setUp() {
            account = new Account(new Owner("Tom"), new Amount(100));
        }

        @Test
        @DisplayName("当充值420个积分后，账户中有520积分，并生成一条对应的充值记录")
        void recharge() {
            //when
            account.recharge(
                    new Amount(420),
                    new Source("o-2020-0001", Source.TYPE_ORDER)
            );

            //then
            assertThat(account.getBalance())
                            .isEqualTo(new Amount(520));

            assertThat(account.getRechargeRecords())
                        .hasSize(1)
                        .hasOnlyOneElementSatisfying(
                            record -> assertThat(record.getAmount())
                                .isEqualTo(new Amount(420)));
        }

        @Test
        @DisplayName("当消费1个积分后，账户里还剩99个积分，并生成一条对应的消费记录")
        void consume() {
            //when
            account.consume(
                    new Amount(1),
                    new Source("o-2020-0001", Source.TYPE_ORDER)
            );

            //then
            assertThat(account.getBalance())
                    .isEqualTo(new Amount(99));

            assertThat(account.getConsumeRecords())
                    .hasSize(1)
                    .hasOnlyOneElementSatisfying(record ->
                        assertThat(record.getAmount())
                                .isEqualTo(new Amount(1)));
        }
    }


    @Nested
    @DisplayName("Tom的账户里曾经有一笔来自订单'o-2020-0001'的充值记录")
    class DuplicateRechargeTest {
        Account account;
        Source source;

        @BeforeEach
        void setUp() {
            account = new Account(new Owner("Tom"), new Amount(100));

            source= new Source("o-2020-0001", Source.TYPE_ORDER);
            RechargeRecord record = new RechargeRecord(
                    account,
                    new Amount(20),
                    source
            );

            account.addRechargeRecord(record);
        }

        @Test
        @DisplayName("当再次通过订单'o-2020-0001'来充值时，会抛出RechargeDuplicateException异常")
        void duplicateRecharge() {
            //when
            RechargeRecord record = new RechargeRecord(account, new Amount(20), source);
            account.addRechargeRecord(record);

            //then
            assertThrows(RechargeDuplicateException.class, () -> {
                account.recharge(new Amount(20), source);
            });
        }
    }


    @Nested
    @DisplayName("Tom有一个有效的账户")
    class ErrorAmountTest {

        Account account;
        Source source;
        @BeforeEach
        void setUp() {
            account = new Account(new Owner("Tom"), new Amount(100));
            source = new Source("o-2020-0001", Source.TYPE_ORDER);
        }

        @Test
        @DisplayName("当充值0个积分时，会抛出IllegalArgumentException异常")
        void rechargeAmountZero() {
            assertThrows(IllegalArgumentException.class, () -> {
                account.recharge(new Amount(0), source);
            });
        }

        @Test
        @DisplayName("当充值-5个积分时，会抛出IllegalArgumentException异常")
        void rechargeALessThanZero() {
            assertThrows(IllegalArgumentException.class, () -> {
                account.recharge(new Amount(-5), source);
            });
        }

        @Test
        @DisplayName("当充值0个积分时，会抛出IllegalArgumentException异常")
        void consumeAmountZero() {
            assertThrows(IllegalArgumentException.class, () -> {
                account.consume(new Amount(0), source);
            });
        }

        @Test
        @DisplayName("当充值-5个积分时，会抛出IllegalArgumentException异常")
        void consumeALessThanZero() {
            assertThrows(IllegalArgumentException.class, () -> {
                account.consume(new Amount(-5), source);
            });
        }
    }

}
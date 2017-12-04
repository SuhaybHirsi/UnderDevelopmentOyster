package com.tfl.billing;



import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface GeneralPaymentsSystem {
//    static com.tfl.external.PaymentsSystem getInstance() {
//        return PaymentSystemTemp.instance;
//    }

    void charge();
}

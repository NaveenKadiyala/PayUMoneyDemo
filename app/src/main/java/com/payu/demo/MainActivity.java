package com.payu.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ToastUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;
    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

    String firstName = "NaveenKadiyala";
    String amount = "1.0";
    String txnId;
    String email = "naveen@thecolourmoon.com";
    String mobileNum = "8499052020";
    String productInfo = "Surf Packets";
    String merchantKey = "Place your live Merchant Key here";
    String merchantId = "Place your live Merchant Id here";
    String merchantSalt = "Place your live Merchant Salt here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.pay_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txnId = System.currentTimeMillis() + "";
                builder.setAmount(amount)
                        .setTxnId(txnId)
                        .setPhone(mobileNum)
                        .setProductName(productInfo)
                        .setFirstName(firstName)
                        .setEmail(email)
                        .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                        .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                        .setIsDebug(false)
                        .setKey(merchantKey)
                        .setMerchantId(merchantId);
                try {
                    paymentParam = builder.build();
                    paymentParam = calculateServerSideHashAndInitiatePayment1(paymentParam);
                    PayUmoneyFlowManager.startPayUMoneyFlow(
                            paymentParam,
                            MainActivity.this,
                            R.style.AppThemePayU,
                            false);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {

            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                Log.d("MainActivity", "onActivityResult: " + transactionResponse.toString());
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    ToastUtils.showShort(MainActivity.this, "Payment Success");
                } else {
                    ToastUtils.showShort(MainActivity.this, "Payment Failure");
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

            } else {
                Log.d("SURl", "Both objects are null!");
            }
        } else {
            ToastUtils.showShort(MainActivity.this, "Payment Cancelled");
        }
    }


    /**
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(
            final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(merchantKey).append("|");
        stringBuilder.append(txnId).append("|");
        stringBuilder.append(amount).append("|");
        stringBuilder.append(productInfo).append("|");
        stringBuilder.append(firstName).append("|");
        stringBuilder.append(email).append("|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "||||||");

        stringBuilder.append(merchantSalt);

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }
}

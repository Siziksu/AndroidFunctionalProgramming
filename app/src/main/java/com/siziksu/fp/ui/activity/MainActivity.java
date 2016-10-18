package com.siziksu.fp.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.siziksu.fp.R;
import com.siziksu.fp.common.constants.Constants;
import com.siziksu.fp.common.functions.Fun1Param;
import com.siziksu.fp.common.functions.Fun2Param;
import com.siziksu.fp.common.functions.FunGen;
import com.siziksu.fp.common.functions.FunVoid;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final long THREE_SECONDS = 3000;
    private static final String NL = "\n";

    private Fun2Param<Integer> add = (x1, x2) -> x1 + x2;
    private Fun2Param<Integer> multiply = (s1, s2) -> s1 * s2;
    private Fun2Param<String> concat = (s1, s2) -> s1 + " " + s2;

    @BindView(R.id.result)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        textView.append("Async call started (wait 3 seconds for the result)" + NL);
        testAsync(response -> {
                      textView.append(NL);
                      textView.append(response);
                  },
                  e -> Log.d(Constants.TAG, e.getMessage(), e),
                  () -> textView.append("\nAsync call finished" + NL));
        textView.append(NL);
        textView.append("Add: " + add.apply(12, 7) + NL);
        textView.append("Multiply: " + multiply.apply(add.apply(2, 7), 7) + NL);
        textView.append("Concat: " + concat.apply("text1", "text2") + NL);
        textView.append(NL);
        textView.append("Result: " + testOneParam(n -> n * 10 / 100f, 97f) + NL);
        textView.append("Result: " + testTwoParams(add, 5, 8) + NL);
        textView.append("Result: " + testTwoParams((n1, n2) -> n1 * n2, 40, 8) + NL);
        textView.append("Result: " + testTwoParams((n1, n2) -> n1 / n2, 40, 8) + NL);
        textView.append("Result: " + testTwoParams((s1, s2) -> s1 + " " + s2, "text1", "text2") + NL);
    }

    private <T> T testOneParam(Fun1Param<T> fun, T param) {
        return fun.apply(param);
    }

    private <T> T testTwoParams(Fun2Param<T> fun, T param1, T param2) {
        return fun.apply(param1, param2);
    }

    private void testAsync(FunGen<String> success, FunGen<Exception> fail, FunVoid done) {
        Handler handler = new Handler();
        new Thread(() -> {
            try {
                Thread.sleep(THREE_SECONDS);
                handler.post(() -> success.apply("This is an async call"));
            } catch (InterruptedException e) {
                fail.apply(e);
            }
            handler.post(done::apply);
        }).start();
    }
}

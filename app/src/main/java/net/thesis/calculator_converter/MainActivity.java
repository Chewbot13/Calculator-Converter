package net.thesis.calculator_converter;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.mariuszgromada.math.mxparser.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import Retrofit.RetrofitCreator;
import Retrofit.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText result_display;
    private Spinner dropdownFrom;
    private Spinner dropdownTo;
    private Button btnconvert;
    private Button backspaceButton;
    private Button swapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // converter_start

        dropdownFrom = findViewById(R.id.spFromCurrency);
        dropdownTo = findViewById(R.id.spToCurrency);
        btnconvert = findViewById(R.id.btnConvert);
        swapButton = findViewById(R.id.swapBTN);

        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int spinner1Index = dropdownFrom.getSelectedItemPosition();

                dropdownFrom.setSelection(dropdownTo.getSelectedItemPosition());
                dropdownTo.setSelection(spinner1Index );
            }
        });


        String[] currencyList = {"EUR", "USD", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, currencyList);
        dropdownFrom.setAdapter(adapter);
        dropdownTo.setAdapter(adapter);

        String regex = "(\\d+)?\\.(\\d+)?";
        String regex_int = "[0-9]+";
        String regex_negative = "-(\\d+)?\\.(\\d+)?";
        String regex_int_negative = "-[0-9]+";

        btnconvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitInterface retrofitInterface = RetrofitCreator.getRetrofitInstance().create(RetrofitInterface.class);
                Call<JsonObject> call = retrofitInterface.getCurrency(dropdownFrom.getSelectedItem().toString());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                        if (result_display.getText().toString().matches(regex) || result_display.getText().toString().matches(regex_int) || result_display.getText().toString().matches(regex_negative) || result_display.getText().toString().matches(regex_int_negative)) {
                            JsonObject res = response.body();
                            JsonObject rates = res.getAsJsonObject("conversion_rates");

                            // access data
                            double currency = Double.valueOf(result_display.getText().toString());
                            double multiplier = Double.valueOf(rates.get(dropdownTo.getSelectedItem().toString()).toString());
                            double result = currency * multiplier;


                            // print result
                            String result_formated = String.format("%.2f",result);
                            result_display.setText((String.valueOf(result_formated)));
                            result_display.setSelection(String.valueOf(result_formated).length());


                        } else {

                            // run equals to get result first
                            equalsButton(view);


                            JsonObject res = response.body();
                            JsonObject rates = res.getAsJsonObject("conversion_rates");

                            // access data
                            double currency = Double.valueOf(result_display.getText().toString());
                            double multiplier = Double.valueOf(rates.get(dropdownTo.getSelectedItem().toString()).toString());
                            double result = currency * multiplier;

                            // print result
                            String result_formated = String.format("%.2f",result);
                            result_display.setText((String.valueOf(result_formated)));
                            result_display.setSelection(String.valueOf(result_formated).length());

                        }


                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        });

        // converter_end


        // calc_start

        result_display = findViewById(R.id.result);

        // hide keyboard
        result_display.setShowSoftInputOnFocus(false);


    }


    private void updateText(String userInput) {

        // check for "not a number"
        if (result_display.getText().toString().startsWith("N")) {
            result_display.setText("");
        }

        String prevInput = result_display.getText().toString();

        // get cursor position
        int cursorPosition = result_display.getSelectionStart();

        String leftInput = prevInput.substring(0, cursorPosition);
        String rightInput = prevInput.substring(cursorPosition);

        if (getString(R.string.display).equals(result_display.getText().toString())) {
            result_display.setText(userInput);
            result_display.setSelection(cursorPosition + 1);
        } else {
            result_display.setText(String.format("%s%s%s", leftInput, userInput, rightInput));
            result_display.setSelection(cursorPosition + 1);
        }

    }

    public void equalsButton(View view) {
        String userExpression = result_display.getText().toString();

        Expression expression = new Expression(userExpression);

        String result = String.valueOf(expression.calculate());

        double result2 = expression.calculate();

        if ((result2 % 1) == 0) {
            result = String.format("%.0f", result2);
            result_display.setText(result);
            result_display.setSelection(result.length());
        } else {
            result = String.valueOf(result2);
            result_display.setText(result);
            result_display.setSelection(result.length());
        }
    }

    public void backspaceButton(View view) {
        int cursorPosition = result_display.getSelectionStart();
        int textLength = result_display.getText().length();

        if (cursorPosition != 0 && textLength != 0) {
            SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) result_display.getText();
            spannableStringBuilder.replace(cursorPosition - 1, cursorPosition, "");
            result_display.setText(spannableStringBuilder);
            result_display.setSelection(cursorPosition - 1);
        }
    }


    public void zeroButton(View view) {

        // check for multiple zeros
        if (result_display.getText().toString().equals("0")) {
            return;
        }
        updateText("0");

    }

    public void oneButton(View view) {
        updateText("1");
    }

    public void twoButton(View view) {
        updateText("2");
    }

    public void threeButton(View view) {
        updateText("3");
    }

    public void fourButton(View view) {
        updateText("4");
    }

    public void fiveButton(View view) {
        updateText("5");
    }

    public void sixButton(View view) {
        updateText("6");
    }

    public void sevenButton(View view) {
        updateText("7");
    }

    public void eightButton(View view) {
        updateText("8");
    }

    public void nineButton(View view) {
        updateText("9");
    }

    public void decimal_pointButton(View view) {

        // check for existing
        String check = result_display.getText().toString();

        if (check.substring(check.length() - 1).equals("+")  ||
                check.substring(check.length() - 1).equals("-") ||
                check.substring(check.length() - 1).equals("*") ||
                check.substring(check.length() - 1).equals("/") ||
                check.substring(check.length() - 1).equals(".") ||
                check.substring(check.length() - 1).equals("^")
        )
        {
            return;
        }

        if (result_display.getText().toString().equals("0")) {
            result_display.setText("0.");
            result_display.setSelection(result_display.getText().length());
        }else  updateText(".");
    }


    public void addButton (View view) {

        String check = result_display.getText().toString();

        if (check.substring(check.length() - 1).equals("+")  ||
                check.substring(check.length() - 1).equals("-") ||
                check.substring(check.length() - 1).equals("*") ||
                check.substring(check.length() - 1).equals("/") ||
                check.substring(check.length() - 1).equals(".") ||
                check.substring(check.length() - 1).equals("^")
        )
        {
            return;
        }
        updateText("+");
    }

    public void subtractButton (View view) {
        String check = result_display.getText().toString();

        if (check.substring(check.length() - 1).equals("+")  ||
                check.substring(check.length() - 1).equals("-") ||
                check.substring(check.length() - 1).equals("*") ||
                check.substring(check.length() - 1).equals("/") ||
                check.substring(check.length() - 1).equals(".") ||
                check.substring(check.length() - 1).equals("^")
        )
        {
            return;
        }
        updateText("-");
    }

    public void multiplyButton (View view) {
        String check = result_display.getText().toString();

        if (check.substring(check.length() - 1).equals("+")  ||
                check.substring(check.length() - 1).equals("-") ||
                check.substring(check.length() - 1).equals("*") ||
                check.substring(check.length() - 1).equals("/") ||
                check.substring(check.length() - 1).equals(".") ||
                check.substring(check.length() - 1).equals("^")
        )
        {
            return;
        }
        updateText("*");
    }

    public void divideButton (View view) {
        String check = result_display.getText().toString();

        if (check.substring(check.length() - 1).equals("+")  ||
                check.substring(check.length() - 1).equals("-") ||
                check.substring(check.length() - 1).equals("*") ||
                check.substring(check.length() - 1).equals("/") ||
                check.substring(check.length() - 1).equals(".") ||
                check.substring(check.length() - 1).equals("^")
        )
        {
            return;
        }
        updateText("/");
    }

    public void signButton (View view) {


        if (result_display.getText().toString().startsWith("-")) {
            String sign_swap = result_display.getText().toString().replaceFirst("-","+");
            result_display.setText(sign_swap);
            result_display.setSelection(result_display.getText().length());
            return;
        }

        if (result_display.getText().toString().startsWith("+")) {
            String sign_swap = result_display.getText().toString().replaceFirst("\\+", "-");
            result_display.setText(sign_swap);
            result_display.setSelection(result_display.getText().length());
            return;
        }

        result_display.setSelection(0);
        updateText("-");
        result_display.setSelection(result_display.getText().length());


    }

    public void exponentButton (View view) {
        String check = result_display.getText().toString();

        if (check.substring(check.length() - 1).equals("+")  ||
                check.substring(check.length() - 1).equals("-") ||
                check.substring(check.length() - 1).equals("*") ||
                check.substring(check.length() - 1).equals("/") ||
                check.substring(check.length() - 1).equals(".") ||
                check.substring(check.length() - 1).equals("^")
        )
        {
            return;
        }
        updateText("^");
    }

    public void clearButton (View view) {
        result_display.setText("0");
    }

    // calc_end


}

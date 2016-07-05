package com.xya.UserInterface;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.xya.MainActivity.R;

/**
 * Created by ubuntu on 15-2-12.
 */
public class ResultItem extends FrameLayout {
    private SpannableStringBuilder shortQuotation;
    private SpannableStringBuilder quotation;
    private String shortTranslation;
    private String translation;

    private boolean currentQuotation = true;
    private boolean currentTranslation = true;

    private MaterialEditText et_quotation;
    private MaterialEditText et_translation;
    private ImageView iv_quotation;
    private ImageView iv_translation;


    public ResultItem(Context context) {
        this(context, null);
    }

    public ResultItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResultItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_dictionary_result_corpus_item, this);
        et_quotation = (MaterialEditText) findViewById(R.id.quotation);
        et_translation = (MaterialEditText) findViewById(R.id.translation);
        iv_quotation = (ImageView) findViewById(R.id.updown_quotation);
        iv_quotation.setBackground(getResources().getDrawable(R.drawable.ic_expand_more_grey600_24dp));
        iv_translation = (ImageView) findViewById(R.id.updown_translation);
        iv_translation.setBackground(getResources().getDrawable(R.drawable.ic_expand_more_grey600_24dp));

        et_quotation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuotation) {
                    iv_quotation.setBackground(getResources().getDrawable(R.drawable.ic_expand_less_grey600_24dp));
                    et_quotation.setText(quotation);
                } else {
                    iv_quotation.setBackground(getResources().getDrawable(R.drawable.ic_expand_more_grey600_24dp));
                    et_quotation.setText(shortQuotation);
                }
                currentQuotation = !currentQuotation;
            }
        });

        et_translation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTranslation) {
                    iv_translation.setBackground(getResources().getDrawable(R.drawable.ic_expand_less_grey600_24dp));
                    et_translation.setText(translation);
                } else {
                    iv_translation.setBackground(getResources().getDrawable(R.drawable.ic_expand_more_grey600_24dp));
                    et_translation.setText(shortTranslation);
                }
                currentTranslation = !currentTranslation;
            }
        });
    }

    public void setQuoatation(SpannableStringBuilder quotation, SpannableStringBuilder shortQuotation) {
        this.quotation = quotation;
        this.shortQuotation = shortQuotation;
        et_quotation.setText(shortQuotation);
    }

    public void setTranslation(String translation, String shortTranslation) {
        this.translation = translation;
        this.shortTranslation = shortTranslation;
        et_translation.setText(shortTranslation);
    }


}

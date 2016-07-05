package com.xya.UserInterface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xya.MainActivity.R;

/**
 * Created by ubuntu on 15-2-19.
 */
public class CloudItem extends FrameLayout {

    private ImageView icon;
    private TextView text;

    private TextView total, local, cloud, update, changelog;
    private ProgressBar pb;


    public CloudItem(Context context) {
        this(context, null);
    }

    public CloudItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CloudItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_cloud_fragment_item, this);
        total = (TextView) findViewById(R.id.total);
        local = (TextView) findViewById(R.id.local);
        cloud = (TextView) findViewById(R.id.cloud);
        pb = (ProgressBar) findViewById(R.id.pb);
        update = (TextView) findViewById(R.id.update);
        changelog = (TextView) findViewById(R.id.changelog);

        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.CloudItem);
        icon = (ImageView) findViewById(R.id.iv);
        icon.setBackground(attribute.getDrawable(R.styleable.CloudItem_pb_icon));
        text = (TextView) findViewById(R.id.text);
        text.setText(attribute.getText(R.styleable.CloudItem_pb_text));
        attribute.recycle();
    }

    public void setTotal(final int used, final int unused) {
        local.setText(used + " 条");
        cloud.setText(unused + " 条");
        total.setText((used + unused) + " 条");
        //this.unused = unused;
        pb.setTotal(used, unused);

        if (unused < 1) {
            update.setText("已最新");
            // update.setEnabled(false);
            //update.setVisibility(GONE);
        } else {
            update.setText("立即更新");
            //update.setEnabled(true);
            //update.setVisibility(VISIBLE);
        }
    }

    public void setPrimaryColor(int c) {

        icon.getBackground().setColorFilter(c, PorterDuff.Mode.SRC_ATOP);
        changelog.setTextColor(c);
        pb.setPrimaryColor(c);
    }


    public void setOnUpdateClickListener(OnClickListener l) {
        update.setOnClickListener(l);
    }
}

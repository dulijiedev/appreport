
package com.roobo.appreport.formatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.roobo.appreport.R;
import com.roobo.appreport.data.KnowledgeCharEnum;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class XYMarkerView extends MarkerView {

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;

    private LinearLayout ll2, ll3, ll4, ll5;
    private final IAxisValueFormatter xAxisValueFormatter;

    private final DecimalFormat format;

    private List<String> titles;

    private KnowledgeCharEnum charEnum = KnowledgeCharEnum.All;


    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public void setCharEnum(KnowledgeCharEnum charEnum) {
        this.charEnum = charEnum;
    }

    public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        ll2 = findViewById(R.id.ll_2);
        ll3 = findViewById(R.id.ll_3);
        ll4 = findViewById(R.id.ll_4);
        ll5 = findViewById(R.id.ll_5);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2_2);
        tv3 = findViewById(R.id.tv3_3);
        tv4 = findViewById(R.id.tv4_4);
        tv5 = findViewById(R.id.tv5_5);
        format = new DecimalFormat("###.0");
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof BarEntry) {
            //可能是全部，其他的可能就是已掌握 薄弱点 未检测
            int index = ((int) e.getX());
            if (titles != null && titles.size() > index) {
                tv1.setText((index + 1) + " " + titles.get(index));
            }
            switch (charEnum) {
                case All:
                    float total = 0;
                    for (int i = 0; i < ((BarEntry) e).getYVals().length; i++) {
                        total += ((BarEntry) e).getYVals()[i];
                    }
                    if(((BarEntry) e).getYVals().length == 3) {
                        tv2.setText("" + ((int) (((BarEntry) e).getYVals()[0])) + "");
                        tv3.setText("" + ((int) (((BarEntry) e).getYVals()[1])) + "");
                        tv4.setText("" + ((int) (((BarEntry) e).getYVals()[2])) + "");
                    }
                    tv5.setText("" + ((int) total));
                    ll2.setVisibility(View.VISIBLE);
                    ll3.setVisibility(View.VISIBLE);
                    ll4.setVisibility(View.VISIBLE);
                    ll5.setVisibility(View.VISIBLE);
                    break;
                case Mastered:
                    tv2.setText("" + ((int) (e.getY())) + "");
                    ll2.setVisibility(View.VISIBLE);
                    ll3.setVisibility(View.GONE);
                    ll4.setVisibility(View.GONE);
                    ll5.setVisibility(View.GONE);
                    break;
                case WeakSpot:
                    tv3.setText("" + ((int) (e.getY())) + "");
                    ll3.setVisibility(View.VISIBLE);
                    ll2.setVisibility(View.GONE);
                    ll4.setVisibility(View.GONE);
                    ll5.setVisibility(View.GONE);
                    break;
                case Undetected:
                    tv4.setText("" + ((int) (e.getY())) + "");
                    ll4.setVisibility(View.VISIBLE);
                    ll2.setVisibility(View.GONE);
                    ll3.setVisibility(View.GONE);
                    ll5.setVisibility(View.GONE);
                    break;
            }
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

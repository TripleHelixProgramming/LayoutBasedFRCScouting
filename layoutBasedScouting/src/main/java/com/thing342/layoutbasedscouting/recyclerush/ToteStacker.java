package com.thing342.layoutbasedscouting.recyclerush;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thing342.layoutbasedscouting.Field;
import com.thing342.layoutbasedscouting.R;
import com.thing342.layoutbasedscouting.ScoutingApplication;
import com.thing342.layoutbasedscouting.util.IterableHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wes on 1/12/15.
 */
public class ToteStacker extends Field<ToteStackInfo> implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener
{
    private static final int BUTTON_ADD_STACK = 30;
    private final static int resId = R.layout.tote_stacker;
    private final IterableHashMap<ToteStack, View> stacks = new IterableHashMap<ToteStack, View>();
    private LinearLayout container;
    private LinkedList<ToteStack> stacksToBeAdded = new LinkedList<ToteStack>();

    static {
        ScoutingApplication.addField("toteStacker", ToteStacker.class);
    }

    private String id = "";

    public ToteStacker()
    {
        super(new ToteStackInfo("[]"));
    }

    private void drawStackIcons(View insertView, ToteStack stack)
    {
        LinearLayout container = (LinearLayout) insertView.findViewById(R.id.tote_stacker_box);
        if (container == null) throw new IllegalArgumentException("No suitable view found!");
        container.removeAllViews();

        for (int i = 0; i < stack.getTotes(); i++) {
            ImageView icon = new ImageView(insertView.getContext());
            icon.setImageResource(R.drawable.spite_tote);
            icon.setTag(false);
            container.addView(icon);
        }

        if (stack.hasNoodle()) {
            ImageView noodleIcon = new ImageView(insertView.getContext());
            noodleIcon.setImageResource(R.drawable.spite_can_noodle);
            noodleIcon.setTag(true);
            container.addView(noodleIcon);
        } else if (stack.hasCan()) {
            ImageView canIcon = new ImageView(insertView.getContext());
            canIcon.setImageResource(R.drawable.spite_can);
            canIcon.setTag(true);
            container.addView(canIcon);
        }
    }

    @Override
    public View getView(Context context, ToteStackInfo initValue)
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        container = (LinearLayout) inflater.inflate(resId, null);
        Button addRow = (Button) container.findViewById(R.id.totestackadd);
        addRow.setTag(BUTTON_ADD_STACK);
        addRow.setOnClickListener(this);

        List<ToteStack> list = initValue.getStacks();
        for (ToteStack stack : list) addStack(stack);

        for (ToteStack stack : stacksToBeAdded) addStack(stack);
        stacksToBeAdded.clear();

        return container;
    }

    @Override
    public void setUp(Element e)
    {
        id = e.getAttribute("id");
    }

    @Override
    public void onClick(View v)
    {
        if (v.getTag() instanceof ToteStack && v instanceof Button) {
            ToteStack s = (ToteStack) v.getTag();
            Button b = (Button) v;
            boolean redraw = true;

            if (b.getText().toString().contains("+")) s.addTote();
            else if (b.getText().toString().contains("-")) {
                s.removeTote();
                if (s.getPoints() == 0) {
                    removeStack(s);
                    redraw = false;
                }
            }

            if (redraw) drawStackIcons(stacks.get(s), s);
        } else if (v.getTag() instanceof Integer && v.getTag().equals(BUTTON_ADD_STACK)) {
            addStack();
        }
    }

    @Override
    public ToteStackInfo getValue()
    {
        ToteStackInfo info = new ToteStackInfo(stacks.getKeys());
        stacks.clear();
        return info;
    }

    @Override
    public ToteStackInfo parse(String value)
    {
        return new ToteStackInfo(value);

    }

    @Override
    public ToteStackInfo parse(JSONObject value)
    {
        JSONArray array = value.optJSONArray("stacks");
        if (array == null) return new ToteStackInfo();
        for (int i = 0; i < array.length(); i++) {
            JSONObject stack = array.optJSONObject(i);
            addStack(new ToteStack(stack.optInt("height", 0),
                    stack.optBoolean("can", false), stack.optBoolean("noodle", false)));
        }
        return getValue();
    }

    @Override
    protected JSONObject getJSON(boolean flag) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        Log.d("Tote stack size", stacks.getKeys().size() + "");
        for (ToteStack t : stacks.getKeys()) {
            JSONObject j = t.toJSON();
            Log.d("Stack", j.toString());
            jsonArray.put(j);
        }

        JSONObject json = new JSONObject();
        json.put("stacks", jsonArray);
        json.put("text", getValue().toString());
        return json;
    }

    @Override
    public String getId()
    {
        return id;
    }

    private void removeStack(ToteStack stack)
    {
        container.removeView(stacks.get(stack));
        stacks.remove(stack);
    }

    private void addStack()
    {
        addStack(new ToteStack());
    }

    private void addStack(ToteStack stack)
    {
        if (container != null) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View stackRow = inflater.inflate(R.layout.tote_stacker_row, null);

            Button addTo = (Button) stackRow.findViewById(R.id.totebuttonplus);
            Button removeFrom = (Button) stackRow.findViewById(R.id.totebuttonminus);
            addTo.setTag(stack);
            removeFrom.setTag(stack);
            addTo.setOnClickListener(this);
            removeFrom.setOnClickListener(this);

            CheckBox isNoodle = (CheckBox) stackRow.findViewById(R.id.cbox_noodle);
            CheckBox isCan = (CheckBox) stackRow.findViewById(R.id.cbox_can);
            isNoodle.setTag(stack);
            isCan.setTag(stack);
            isNoodle.setOnCheckedChangeListener(this);
            isCan.setOnCheckedChangeListener(this);

            stacks.put(stack, stackRow);
            container.addView(stackRow);
            drawStackIcons(stackRow, stack);
        } else {
            stacksToBeAdded.add(stack);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        Log.d("Button", buttonView.getId() + "");
        Log.d("Can", R.id.cbox_can + "");
        Log.d("Noodle", R.id.cbox_noodle + "");
        
        if (buttonView.getTag() instanceof ToteStack) {

            ToteStack t = (ToteStack) buttonView.getTag();

            if (buttonView.getId() == R.id.cbox_can) {
                Log.d("Can", "Can is changed!");
                t.setCan(isChecked);

            } else if (buttonView.getId() == R.id.cbox_noodle) {
                t.setNoodle(isChecked);
            }

            drawStackIcons(stacks.get(t), t);
        }
    }
}

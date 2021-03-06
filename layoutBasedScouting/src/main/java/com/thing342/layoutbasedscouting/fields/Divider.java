package com.thing342.layoutbasedscouting.fields;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thing342.layoutbasedscouting.Field;
import com.thing342.layoutbasedscouting.R;
import com.thing342.layoutbasedscouting.ScoutingApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

/**
 * A simple divider field used for separating different groups of fields. Does not return any data.
 */
public class Divider extends Field<String>
{

    private final int resId = R.layout.divider;
    private String name;

    static {
        ScoutingApplication.addField("divider", Divider.class);
    }

    public Divider()
    {
        super("null");
    }

    /**
     * @param name The text of the name displayed on this divider.
     */
    public Divider(String name)
    {
        super("null");
        this.name = name;
    }

    @Override
    public Class getType()
    { //This isn't used to store values, so return null type.
        return null;
    }

    @Override
    public View getView(Context context, String initValue)
    {
        View v = LayoutInflater.from(context).inflate(resId, null);
        TextView fieldname = (TextView) v.findViewById(R.id.field_name);
        fieldname.setText(name);
        return v;
    }

    @Override
    public void setUp(Element e)
    {
        name = "";
        name = e.getAttribute("name");
    }

    @Override
    public String getValue()
    {
        return null;
    }

    @Override
    public String parse(String value)
    {
        return null;
    }

    @Override
    public String parse(JSONObject value)
    {
        return "";
    }

    @Override
    protected JSONObject getJSON(boolean flag) throws JSONException
    {
        return new JSONObject();
    }

    @Override
    public String getId()
    {
        return "";
    }

}

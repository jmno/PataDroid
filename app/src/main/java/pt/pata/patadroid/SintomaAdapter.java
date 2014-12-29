package pt.pata.patadroid;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import pt.pata.patadroid.pt.pata.patadroid.modelo.Sintoma;


public class SintomaAdapter extends ArrayAdapter<Sintoma> {


        private final ArrayList<Sintoma> list;
        private final Activity context;
        boolean checkAll_flag = false;
        boolean checkItem_flag = false;

        public SintomaAdapter(Activity context, ArrayList<Sintoma> list) {
            super(context, R.layout.layout_lista_sintomas, list);
            this.context = context;
            this.list = list;
        }

        static class ViewHolder {
            protected TextView text;
            protected CheckBox checkbox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                convertView = inflator.inflate(R.layout.layout_lista_sintomas, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) convertView.findViewById(R.id.label);
                viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check);
                viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                        list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    }
                });
                convertView.setTag(viewHolder);
                convertView.setTag(R.id.label, viewHolder.text);
                convertView.setTag(R.id.check, viewHolder.checkbox);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.checkbox.setTag(position); // This line is important.

            viewHolder.text.setText(list.get(position).getNome());


            return convertView;
        }
    }

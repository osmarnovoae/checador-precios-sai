package com.vigor.checadorpreciossai;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.NumberFormat;
import java.util.Locale;

public class ConsultaSaldoClientesAdapter extends BaseAdapter {


    org.json.JSONArray data;
    Context context;
    String tipoSaldo;

    public ConsultaSaldoClientesAdapter(Context context,org.json.JSONArray data,String tipoSaldo){
        this.context=context;
        this.data = data;
        this.tipoSaldo = tipoSaldo;
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject jsonObject = null;

        try {
            jsonObject = data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context,R.layout.row_saldo_cliente_item,null);
        TextView txtCliente = (TextView)v.findViewById(R.id.nombreClienteView);
        TextView saldoView = (TextView)v.findViewById(R.id.saldoView);
        double saldo;

        try {

            txtCliente.setText(data.getJSONObject(position).getString("nom_cte"));
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            if (this.tipoSaldo.equals("VENCIDO")) {
                 saldo = Double.valueOf(data.getJSONObject(position).getString("vencido")) * -1;
                 saldoView.setText("VENCIDO: $"+String.valueOf(nf.format(saldo)));
            }else{
                 saldo = Double.valueOf(data.getJSONObject(position).getString("no_vencido")) * -1;
                 saldoView.setText("NO VENCIDO: $"+String.valueOf(nf.format(saldo)));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }
}

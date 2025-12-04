package com.vigor.checadorpreciossai;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductosMasVendidosPorAgenteAdapter extends BaseAdapter {

    private TextView tvDescProd;
    private TextView tvTotalMx;
    private TextView tvCantidad;



    private JSONArray datos;
    private Context context;

    public ProductosMasVendidosPorAgenteAdapter(Context context, JSONArray datos) {
        this.datos = datos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datos.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(position);
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
    public View getView(int position, View view, ViewGroup viewGroup) {

        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        view  = LayoutInflater.from(context).inflate(R.layout.list_item_productos_mas_vendidos_por_agente,null);
        tvDescProd = (TextView) view.findViewById(R.id.tvDescProd);
        tvTotalMx = (TextView) view.findViewById(R.id.tvTotalMx);
        tvCantidad = (TextView) view.findViewById(R.id.tvCantidad);

        //set views
        try {

            tvDescProd.setText(datos.getJSONObject(position).getString("desc_prod").trim());
            DecimalFormat twoPlaces = new DecimalFormat("0.00");
            tvTotalMx.setText("$ "+twoPlaces.format(Double.parseDouble(datos.getJSONObject(position).getString("total")))+" M.N");
            tvCantidad.setText("Cant "+String.valueOf(datos.getJSONObject(position).getString("cant")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
